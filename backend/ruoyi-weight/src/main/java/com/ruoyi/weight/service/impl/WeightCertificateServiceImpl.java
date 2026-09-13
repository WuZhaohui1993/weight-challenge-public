package com.ruoyi.weight.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightCertificate;
import com.ruoyi.weight.mapper.WeightCertificateMapper;
import com.ruoyi.weight.service.IWeightCertificateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * HTTPS证书监控Service业务层处理
 *
 * @author ruoyi
 * @date 2026-05-22
 */
@Service
public class WeightCertificateServiceImpl implements IWeightCertificateService
{
    private static final int DEFAULT_RENEW_BEFORE_DAYS = 20;

    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final int CONNECT_TIMEOUT_MILLIS = 8000;

    private static final int COMMAND_TIMEOUT_SECONDS = 180;

    private static final int LOG_LIMIT = 6000;

    private static final Pattern SAFE_COMMAND_PATTERN = Pattern.compile("^[\\p{Alnum}\\s_./~:,@=%+\\-\"'&|;(){}]+$");

    private static final Map<String, String> PROVIDER_DIRECTORY_URLS;

    static
    {
        Map<String, String> providers = new LinkedHashMap<>();
        providers.put("letsencrypt", "https://acme-v02.api.letsencrypt.org/directory");
        providers.put("zerossl", "https://acme.zerossl.com/v2/DV90");
        providers.put("buypass", "https://api.buypass.com/acme/directory");
        providers.put("google", "https://dv.acme-v02.api.pki.goog/directory");
        PROVIDER_DIRECTORY_URLS = Collections.unmodifiableMap(providers);
    }

    @Value("${certificate.default-provider:letsencrypt}")
    private String defaultProvider;

    @Value("${certificate.default-ca-directory-url:}")
    private String defaultCaDirectoryUrl;

    @Value("${certificate.default-webroot:/var/www/weight-challenge-admin}")
    private String defaultWebroot;

    @Value("${certificate.command-timeout-seconds:180}")
    private int commandTimeoutSeconds;

    @Autowired
    private WeightCertificateMapper weightCertificateMapper;

    @Override
    public WeightCertificate selectWeightCertificateById(Long id)
    {
        return weightCertificateMapper.selectWeightCertificateById(id);
    }

    @Override
    public List<WeightCertificate> selectWeightCertificateList(WeightCertificate weightCertificate)
    {
        return weightCertificateMapper.selectWeightCertificateList(weightCertificate);
    }

    @Override
    public Map<String, Object> selectWeightCertificateSummary()
    {
        List<WeightCertificate> list = weightCertificateMapper.selectWeightCertificateList(new WeightCertificate());
        Map<String, Object> summary = new LinkedHashMap<>();
        int total = 0;
        int valid = 0;
        int expiring = 0;
        int expired = 0;
        int failed = 0;
        int unknown = 0;
        int autoRenew = 0;
        Integer minDaysRemaining = null;
        WeightCertificate nearest = null;

        for (WeightCertificate item : list)
        {
            total++;
            String status = StringUtils.defaultIfBlank(item.getStatus(), "unknown");
            if ("valid".equals(status))
            {
                valid++;
            }
            else if ("expiring".equals(status))
            {
                expiring++;
            }
            else if ("expired".equals(status))
            {
                expired++;
            }
            else if ("failed".equals(status))
            {
                failed++;
            }
            else
            {
                unknown++;
            }
            if (Integer.valueOf(1).equals(item.getAutoRenew()))
            {
                autoRenew++;
            }
            if (item.getDaysRemaining() != null
                    && (minDaysRemaining == null || item.getDaysRemaining() < minDaysRemaining))
            {
                minDaysRemaining = item.getDaysRemaining();
                nearest = item;
            }
        }

        summary.put("total", total);
        summary.put("valid", valid);
        summary.put("expiring", expiring);
        summary.put("expired", expired);
        summary.put("failed", failed);
        summary.put("unknown", unknown);
        summary.put("autoRenew", autoRenew);
        summary.put("minDaysRemaining", minDaysRemaining);
        summary.put("nearestName", nearest == null ? null : nearest.getName());
        summary.put("nearestDomain", nearest == null ? null : nearest.getPrimaryDomain());
        return summary;
    }

    @Override
    public int insertWeightCertificate(WeightCertificate weightCertificate)
    {
        normalizeCertificate(weightCertificate, true);
        weightCertificate.setCreateTime(DateUtils.getNowDate());
        return weightCertificateMapper.insertWeightCertificate(weightCertificate);
    }

    @Override
    public int updateWeightCertificate(WeightCertificate weightCertificate)
    {
        normalizeCertificate(weightCertificate, false);
        weightCertificate.setUpdateTime(DateUtils.getNowDate());
        return weightCertificateMapper.updateWeightCertificate(weightCertificate);
    }

    @Override
    public int deleteWeightCertificateByIds(Long[] ids)
    {
        return weightCertificateMapper.deleteWeightCertificateByIds(ids);
    }

    @Override
    public int deleteWeightCertificateById(Long id)
    {
        return weightCertificateMapper.deleteWeightCertificateById(id);
    }

    @Override
    public WeightCertificate checkCertificate(Long id)
    {
        WeightCertificate certificate = requireCertificate(id);
        try
        {
            X509Certificate x509Certificate = loadCertificate(certificate);
            applyCertificateInfo(certificate, x509Certificate);
            certificate.setLastError("");
            certificate.setLastLog("证书检测成功：" + DateUtils.getTime());
        }
        catch (Exception ex)
        {
            certificate.setStatus("failed");
            certificate.setLastCheckedAt(DateUtils.getNowDate());
            certificate.setLastError(StringUtils.abbreviate(ex.getMessage(), 1000));
            certificate.setLastLog("证书检测失败：" + StringUtils.abbreviate(ex.toString(), LOG_LIMIT));
        }
        certificate.setUpdateTime(DateUtils.getNowDate());
        weightCertificateMapper.updateWeightCertificate(certificate);
        return certificate;
    }

    @Override
    public WeightCertificate renewCertificate(Long id, boolean force)
    {
        WeightCertificate certificate = checkCertificate(id);
        if (!force && !needsRenew(certificate))
        {
            certificate.setLastLog("证书未到续签阈值，跳过续签。剩余天数：" + certificate.getDaysRemaining());
            certificate.setUpdateTime(DateUtils.getNowDate());
            weightCertificateMapper.updateWeightCertificate(certificate);
            return certificate;
        }

        String command = buildRenewCommand(certificate, force);
        if (StringUtils.isBlank(command))
        {
            throw new ServiceException("当前证书没有可执行的签发/续签配置，请配置 certbot webroot 或自定义续签命令");
        }

        CommandResult renewResult = runShellCommand(command);
        StringBuilder log = new StringBuilder();
        log.append("$ ").append(command).append('\n').append(renewResult.output);

        if (!renewResult.success)
        {
            certificate.setStatus("failed");
            certificate.setLastError(StringUtils.abbreviate(renewResult.output, 1000));
            certificate.setLastLog(StringUtils.abbreviate(log.toString(), LOG_LIMIT));
            certificate.setUpdateTime(DateUtils.getNowDate());
            weightCertificateMapper.updateWeightCertificate(certificate);
            return certificate;
        }

        if (StringUtils.isNotBlank(certificate.getDeployCommand()))
        {
            CommandResult deployResult = runShellCommand(certificate.getDeployCommand());
            log.append('\n').append("$ ").append(certificate.getDeployCommand()).append('\n').append(deployResult.output);
            if (!deployResult.success)
            {
                certificate.setStatus("failed");
                certificate.setLastError(StringUtils.abbreviate(deployResult.output, 1000));
                certificate.setLastLog(StringUtils.abbreviate(log.toString(), LOG_LIMIT));
                certificate.setUpdateTime(DateUtils.getNowDate());
                weightCertificateMapper.updateWeightCertificate(certificate);
                return certificate;
            }
        }

        certificate.setLastRenewedAt(DateUtils.getNowDate());
        try
        {
            X509Certificate x509Certificate = loadCertificate(certificate);
            applyCertificateInfo(certificate, x509Certificate);
            certificate.setLastError("");
        }
        catch (Exception ex)
        {
            certificate.setLastCheckedAt(DateUtils.getNowDate());
            certificate.setLastError(StringUtils.abbreviate("续签命令已执行，但重新读取证书失败：" + ex.getMessage(), 1000));
        }
        certificate.setLastLog(StringUtils.abbreviate(log.toString(), LOG_LIMIT));
        certificate.setUpdateTime(DateUtils.getNowDate());
        weightCertificateMapper.updateWeightCertificate(certificate);
        return certificate;
    }

    @Override
    public void autoRenewDueCertificates()
    {
        List<WeightCertificate> certificates = weightCertificateMapper.selectEnabledAutoRenewCertificates();
        for (WeightCertificate certificate : certificates)
        {
            try
            {
                WeightCertificate checked = checkCertificate(certificate.getId());
                if (needsRenew(checked))
                {
                    renewCertificate(certificate.getId(), false);
                }
            }
            catch (Exception ex)
            {
                certificate.setStatus("failed");
                certificate.setLastCheckedAt(DateUtils.getNowDate());
                certificate.setLastError(StringUtils.abbreviate(ex.getMessage(), 1000));
                certificate.setLastLog("自动续签检查失败：" + StringUtils.abbreviate(ex.toString(), LOG_LIMIT));
                certificate.setUpdateTime(DateUtils.getNowDate());
                weightCertificateMapper.updateWeightCertificate(certificate);
            }
        }
    }

    private WeightCertificate requireCertificate(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("证书ID不能为空");
        }
        WeightCertificate certificate = weightCertificateMapper.selectWeightCertificateById(id);
        if (certificate == null)
        {
            throw new ServiceException("证书配置不存在");
        }
        return certificate;
    }

    private void normalizeCertificate(WeightCertificate certificate, boolean inserting)
    {
        if (certificate == null)
        {
            throw new ServiceException("证书配置不能为空");
        }
        certificate.setName(StringUtils.trimToNull(certificate.getName()));
        certificate.setPrimaryDomain(StringUtils.trimToNull(certificate.getPrimaryDomain()));
        certificate.setDomains(joinDomains(resolveDomains(certificate)));
        certificate.setProvider(StringUtils.defaultIfBlank(StringUtils.trimToNull(certificate.getProvider()), defaultProvider));
        certificate.setAcmeMode(StringUtils.defaultIfBlank(StringUtils.trimToNull(certificate.getAcmeMode()), "certbot_webroot"));
        certificate.setChallengeType(StringUtils.defaultIfBlank(StringUtils.trimToNull(certificate.getChallengeType()), "http-01"));
        certificate.setDnsProvider(StringUtils.trimToNull(certificate.getDnsProvider()));
        certificate.setEmail(StringUtils.trimToNull(certificate.getEmail()));
        certificate.setWebrootPath(StringUtils.defaultIfBlank(StringUtils.trimToNull(certificate.getWebrootPath()), defaultWebroot));
        certificate.setCaDirectoryUrl(resolveCaDirectoryUrl(certificate));
        certificate.setCertPath(StringUtils.trimToNull(certificate.getCertPath()));
        certificate.setKeyPath(StringUtils.trimToNull(certificate.getKeyPath()));
        certificate.setFullchainPath(StringUtils.trimToNull(certificate.getFullchainPath()));
        certificate.setDeployCommand(normalizeCommand(certificate.getDeployCommand(), "部署命令"));
        certificate.setIssueCommand(normalizeCommand(certificate.getIssueCommand(), "自定义签发命令"));
        certificate.setRenewCommand(normalizeCommand(certificate.getRenewCommand(), "自定义续签命令"));
        certificate.setRenewBeforeDays(certificate.getRenewBeforeDays() == null
                ? DEFAULT_RENEW_BEFORE_DAYS : certificate.getRenewBeforeDays());
        certificate.setAutoRenew(certificate.getAutoRenew() == null ? 1 : certificate.getAutoRenew());
        certificate.setEnabled(StringUtils.defaultIfBlank(certificate.getEnabled(), "0"));
        if (inserting)
        {
            certificate.setStatus(StringUtils.defaultIfBlank(certificate.getStatus(), "unknown"));
        }
        if (StringUtils.isBlank(certificate.getName()))
        {
            certificate.setName(certificate.getPrimaryDomain());
        }
        if (StringUtils.isBlank(certificate.getPrimaryDomain()))
        {
            throw new ServiceException("主域名不能为空");
        }
        if ("certbot_webroot".equals(certificate.getAcmeMode()) && StringUtils.isBlank(certificate.getWebrootPath()))
        {
            throw new ServiceException("certbot webroot 模式必须配置 webroot 目录");
        }
    }

    private X509Certificate loadCertificate(WeightCertificate certificate) throws Exception
    {
        if (StringUtils.isNotBlank(certificate.getFullchainPath()) || StringUtils.isNotBlank(certificate.getCertPath()))
        {
            String path = StringUtils.defaultIfBlank(certificate.getFullchainPath(), certificate.getCertPath());
            File certFile = new File(path);
            if (!certFile.exists())
            {
                throw new IllegalStateException("证书文件不存在：" + path);
            }
            try (InputStream inputStream = java.nio.file.Files.newInputStream(certFile.toPath()))
            {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                return (X509Certificate) certificateFactory.generateCertificate(inputStream);
            }
        }
        return loadRemoteCertificate(certificate.getPrimaryDomain());
    }

    private X509Certificate loadRemoteCertificate(String domain) throws Exception
    {
        RemoteEndpoint endpoint = parseRemoteEndpoint(domain);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new ReadOnlyTrustManager() }, null);
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        try (Socket tcpSocket = new Socket())
        {
            tcpSocket.connect(new java.net.InetSocketAddress(endpoint.host, endpoint.port), CONNECT_TIMEOUT_MILLIS);
            try (SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(tcpSocket, endpoint.host, endpoint.port, true))
            {
                sslSocket.setSoTimeout(CONNECT_TIMEOUT_MILLIS);
                SSLParameters sslParameters = sslSocket.getSSLParameters();
                sslParameters.setServerNames(Collections.singletonList(new SNIHostName(endpoint.host)));
                sslSocket.setSSLParameters(sslParameters);
                sslSocket.startHandshake();
                java.security.cert.Certificate[] peerCertificates = sslSocket.getSession().getPeerCertificates();
                if (peerCertificates.length == 0 || !(peerCertificates[0] instanceof X509Certificate))
                {
                    throw new IllegalStateException("未读取到远端证书");
                }
                byte[] encoded = peerCertificates[0].getEncoded();
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(encoded));
            }
        }
    }

    private RemoteEndpoint parseRemoteEndpoint(String value)
    {
        String endpoint = StringUtils.trimToEmpty(value);
        if (StringUtils.isBlank(endpoint))
        {
            throw new ServiceException("证书域名不能为空");
        }
        int port = DEFAULT_HTTPS_PORT;
        String host = endpoint;
        int colonIndex = endpoint.lastIndexOf(':');
        if (colonIndex > 0 && colonIndex < endpoint.length() - 1)
        {
            String portText = endpoint.substring(colonIndex + 1);
            if (StringUtils.isNumeric(portText))
            {
                host = endpoint.substring(0, colonIndex);
                port = Integer.parseInt(portText);
            }
        }
        if (StringUtils.isBlank(host) || port <= 0 || port > 65535)
        {
            throw new ServiceException("证书域名或端口不合法：" + value);
        }
        return new RemoteEndpoint(host, port);
    }

    private void applyCertificateInfo(WeightCertificate certificate, X509Certificate x509Certificate)
    {
        Date now = DateUtils.getNowDate();
        Date notAfter = x509Certificate.getNotAfter();
        Date notBefore = x509Certificate.getNotBefore();
        int daysRemaining = (int) Math.floor((notAfter.getTime() - now.getTime()) / (1000D * 60 * 60 * 24));

        certificate.setIssuer(toIssuerName(x509Certificate.getIssuerX500Principal()));
        BigInteger serialNumber = x509Certificate.getSerialNumber();
        certificate.setSerialNumber(serialNumber == null ? null : serialNumber.toString(16).toUpperCase());
        certificate.setNotBefore(notBefore);
        certificate.setNotAfter(notAfter);
        certificate.setDaysRemaining(daysRemaining);
        certificate.setLastCheckedAt(now);
        if (daysRemaining < 0)
        {
            certificate.setStatus("expired");
        }
        else if (daysRemaining <= getRenewBeforeDays(certificate))
        {
            certificate.setStatus("expiring");
        }
        else
        {
            certificate.setStatus("valid");
        }
    }

    private String buildRenewCommand(WeightCertificate certificate, boolean force)
    {
        if (StringUtils.isNotBlank(certificate.getRenewCommand()))
        {
            return renderCommandTemplate(certificate.getRenewCommand(), certificate);
        }
        if (StringUtils.isNotBlank(certificate.getIssueCommand()))
        {
            return renderCommandTemplate(certificate.getIssueCommand(), certificate);
        }
        if (!"certbot_webroot".equals(certificate.getAcmeMode()))
        {
            return null;
        }

        List<String> domains = resolveDomains(certificate);
        if (domains.isEmpty())
        {
            throw new ServiceException("至少需要配置一个域名");
        }

        StringBuilder command = new StringBuilder("certbot certonly --webroot --non-interactive --agree-tos");
        if (StringUtils.isNotBlank(certificate.getEmail()))
        {
            command.append(" --email ").append(shellQuote(certificate.getEmail()));
        }
        else
        {
            command.append(" --register-unsafely-without-email");
        }
        command.append(" -w ").append(shellQuote(certificate.getWebrootPath()));
        for (String domain : domains)
        {
            command.append(" -d ").append(shellQuote(domain));
        }
        if (StringUtils.isNotBlank(certificate.getCaDirectoryUrl()))
        {
            command.append(" --server ").append(shellQuote(certificate.getCaDirectoryUrl()));
        }
        command.append(force ? " --force-renewal" : " --keep-until-expiring");
        return command.toString();
    }

    private String renderCommandTemplate(String template, WeightCertificate certificate)
    {
        return template
            .replace("{name}", safeTemplateValue(certificate.getName()))
            .replace("{primaryDomain}", safeTemplateValue(certificate.getPrimaryDomain()))
            .replace("{domains}", safeTemplateValue(joinDomains(resolveDomains(certificate))))
            .replace("{webrootPath}", safeTemplateValue(certificate.getWebrootPath()))
            .replace("{email}", safeTemplateValue(certificate.getEmail()))
            .replace("{provider}", safeTemplateValue(certificate.getProvider()))
            .replace("{caDirectoryUrl}", safeTemplateValue(certificate.getCaDirectoryUrl()))
            .replace("{dnsProvider}", safeTemplateValue(certificate.getDnsProvider()));
    }

    private List<String> resolveDomains(WeightCertificate certificate)
    {
        Set<String> domains = new LinkedHashSet<>();
        addDomain(domains, certificate.getPrimaryDomain());
        if (StringUtils.isNotBlank(certificate.getDomains()))
        {
            Arrays.stream(certificate.getDomains().split("[,，\\s]+"))
                .map(StringUtils::trimToNull)
                .filter(StringUtils::isNotBlank)
                .forEach(domain -> addDomain(domains, domain));
        }
        return new ArrayList<>(domains);
    }

    private void addDomain(Set<String> domains, String domain)
    {
        String cleanDomain = StringUtils.trimToNull(domain);
        if (cleanDomain != null)
        {
            domains.add(stripDomainPort(cleanDomain).toLowerCase());
        }
    }

    private String stripDomainPort(String domain)
    {
        RemoteEndpoint endpoint = parseRemoteEndpoint(domain);
        return endpoint.host;
    }

    private String joinDomains(List<String> domains)
    {
        return String.join(",", domains);
    }

    private String resolveCaDirectoryUrl(WeightCertificate certificate)
    {
        String explicitUrl = StringUtils.trimToNull(certificate.getCaDirectoryUrl());
        if (explicitUrl != null)
        {
            return explicitUrl;
        }
        if (StringUtils.isNotBlank(defaultCaDirectoryUrl))
        {
            return defaultCaDirectoryUrl;
        }
        return PROVIDER_DIRECTORY_URLS.get(StringUtils.defaultIfBlank(certificate.getProvider(), defaultProvider));
    }

    private boolean needsRenew(WeightCertificate certificate)
    {
        if (certificate.getDaysRemaining() == null)
        {
            return true;
        }
        return certificate.getDaysRemaining() <= getRenewBeforeDays(certificate);
    }

    private int getRenewBeforeDays(WeightCertificate certificate)
    {
        return certificate.getRenewBeforeDays() == null ? DEFAULT_RENEW_BEFORE_DAYS : certificate.getRenewBeforeDays();
    }

    private CommandResult runShellCommand(String command)
    {
        String normalizedCommand = normalizeCommand(command, "执行命令");
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", normalizedCommand);
        processBuilder.redirectErrorStream(true);
        try
        {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(resolveCommandTimeoutSeconds(), TimeUnit.SECONDS);
            String output = readProcessOutput(process);
            if (!finished)
            {
                process.destroyForcibly();
                return new CommandResult(false, StringUtils.abbreviate("命令执行超时\n" + output, LOG_LIMIT));
            }
            return new CommandResult(process.exitValue() == 0, StringUtils.abbreviate(output, LOG_LIMIT));
        }
        catch (Exception ex)
        {
            return new CommandResult(false, StringUtils.abbreviate(ex.toString(), LOG_LIMIT));
        }
    }

    private String readProcessOutput(Process process) throws Exception
    {
        try (InputStream inputStream = process.getInputStream())
        {
            return new String(org.apache.commons.io.IOUtils.toByteArray(inputStream), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private int resolveCommandTimeoutSeconds()
    {
        return commandTimeoutSeconds > 0 ? commandTimeoutSeconds : COMMAND_TIMEOUT_SECONDS;
    }

    private String normalizeCommand(String command, String fieldName)
    {
        String normalized = StringUtils.trimToNull(command);
        if (normalized == null)
        {
            return null;
        }
        if (!SAFE_COMMAND_PATTERN.matcher(normalized).matches())
        {
            throw new ServiceException(fieldName + "包含不支持的特殊字符");
        }
        return normalized;
    }

    private String shellQuote(String value)
    {
        return "'" + StringUtils.defaultString(value).replace("'", "'\"'\"'") + "'";
    }

    private String safeTemplateValue(String value)
    {
        return StringUtils.defaultString(value).replace("\n", "").replace("\r", "");
    }

    private String toIssuerName(X500Principal principal)
    {
        if (principal == null)
        {
            return null;
        }
        return principal.getName(X500Principal.RFC2253);
    }

    private static class CommandResult
    {
        private final boolean success;

        private final String output;

        private CommandResult(boolean success, String output)
        {
            this.success = success;
            this.output = output;
        }
    }

    private static class RemoteEndpoint
    {
        private final String host;

        private final int port;

        private RemoteEndpoint(String host, int port)
        {
            this.host = host;
            this.port = port;
        }
    }

    private static class ReadOnlyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    }
}
