package com.ruoyi.weight.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * HTTPS证书监控对象 weight_certificate
 *
 * @author ruoyi
 * @date 2026-05-22
 */
public class WeightCertificate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 证书名称 */
    @Excel(name = "证书名称")
    private String name;

    /** 主域名 */
    @Excel(name = "主域名")
    private String primaryDomain;

    /** 备用域名，多个逗号分隔 */
    @Excel(name = "备用域名")
    private String domains;

    /** 证书服务商：letsencrypt / zerossl / aliyun / tencent / custom */
    @Excel(name = "证书服务商")
    private String provider;

    /** ACME目录地址 */
    @Excel(name = "ACME目录地址")
    private String caDirectoryUrl;

    /** 申请方式：certbot_webroot / custom_command / manual */
    @Excel(name = "申请方式")
    private String acmeMode;

    /** 域名验证方式：http-01 / dns-01 / manual */
    @Excel(name = "验证方式")
    private String challengeType;

    /** DNS服务商 */
    @Excel(name = "DNS服务商")
    private String dnsProvider;

    /** webroot目录 */
    @Excel(name = "webroot目录")
    private String webrootPath;

    /** 申请邮箱 */
    @Excel(name = "申请邮箱")
    private String email;

    /** 证书文件路径 */
    @Excel(name = "证书文件路径")
    private String certPath;

    /** 私钥文件路径 */
    @Excel(name = "私钥文件路径")
    private String keyPath;

    /** 完整链证书路径 */
    @Excel(name = "完整链证书路径")
    private String fullchainPath;

    /** 证书颁发者 */
    @Excel(name = "证书颁发者")
    private String issuer;

    /** 证书序列号 */
    @Excel(name = "证书序列号")
    private String serialNumber;

    /** 证书生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "证书生效时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date notBefore;

    /** 证书到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "证书到期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date notAfter;

    /** 剩余天数 */
    @Excel(name = "剩余天数")
    private Integer daysRemaining;

    /** 提前续签天数 */
    @Excel(name = "提前续签天数")
    private Integer renewBeforeDays;

    /** 是否自动续签：0否 1是 */
    @Excel(name = "自动续签", readConverterExp = "0=否,1=是")
    private Integer autoRenew;

    /** 续签后执行命令 */
    @Excel(name = "部署命令")
    private String deployCommand;

    /** 自定义签发命令 */
    @Excel(name = "自定义签发命令")
    private String issueCommand;

    /** 自定义续签命令 */
    @Excel(name = "自定义续签命令")
    private String renewCommand;

    /** 当前状态：unknown / valid / expiring / expired / failed */
    @Excel(name = "当前状态")
    private String status;

    /** 最近检测时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近检测时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastCheckedAt;

    /** 最近续签时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近续签时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastRenewedAt;

    /** 最近错误 */
    @Excel(name = "最近错误")
    private String lastError;

    /** 最近执行日志 */
    @Excel(name = "最近执行日志")
    private String lastLog;

    /** 启用状态：0启用 1停用 */
    @Excel(name = "启用状态", readConverterExp = "0=启用,1=停用")
    private String enabled;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPrimaryDomain()
    {
        return primaryDomain;
    }

    public void setPrimaryDomain(String primaryDomain)
    {
        this.primaryDomain = primaryDomain;
    }

    public String getDomains()
    {
        return domains;
    }

    public void setDomains(String domains)
    {
        this.domains = domains;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getCaDirectoryUrl()
    {
        return caDirectoryUrl;
    }

    public void setCaDirectoryUrl(String caDirectoryUrl)
    {
        this.caDirectoryUrl = caDirectoryUrl;
    }

    public String getAcmeMode()
    {
        return acmeMode;
    }

    public void setAcmeMode(String acmeMode)
    {
        this.acmeMode = acmeMode;
    }

    public String getChallengeType()
    {
        return challengeType;
    }

    public void setChallengeType(String challengeType)
    {
        this.challengeType = challengeType;
    }

    public String getDnsProvider()
    {
        return dnsProvider;
    }

    public void setDnsProvider(String dnsProvider)
    {
        this.dnsProvider = dnsProvider;
    }

    public String getWebrootPath()
    {
        return webrootPath;
    }

    public void setWebrootPath(String webrootPath)
    {
        this.webrootPath = webrootPath;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCertPath()
    {
        return certPath;
    }

    public void setCertPath(String certPath)
    {
        this.certPath = certPath;
    }

    public String getKeyPath()
    {
        return keyPath;
    }

    public void setKeyPath(String keyPath)
    {
        this.keyPath = keyPath;
    }

    public String getFullchainPath()
    {
        return fullchainPath;
    }

    public void setFullchainPath(String fullchainPath)
    {
        this.fullchainPath = fullchainPath;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public Date getNotBefore()
    {
        return notBefore;
    }

    public void setNotBefore(Date notBefore)
    {
        this.notBefore = notBefore;
    }

    public Date getNotAfter()
    {
        return notAfter;
    }

    public void setNotAfter(Date notAfter)
    {
        this.notAfter = notAfter;
    }

    public Integer getDaysRemaining()
    {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining)
    {
        this.daysRemaining = daysRemaining;
    }

    public Integer getRenewBeforeDays()
    {
        return renewBeforeDays;
    }

    public void setRenewBeforeDays(Integer renewBeforeDays)
    {
        this.renewBeforeDays = renewBeforeDays;
    }

    public Integer getAutoRenew()
    {
        return autoRenew;
    }

    public void setAutoRenew(Integer autoRenew)
    {
        this.autoRenew = autoRenew;
    }

    public String getDeployCommand()
    {
        return deployCommand;
    }

    public void setDeployCommand(String deployCommand)
    {
        this.deployCommand = deployCommand;
    }

    public String getIssueCommand()
    {
        return issueCommand;
    }

    public void setIssueCommand(String issueCommand)
    {
        this.issueCommand = issueCommand;
    }

    public String getRenewCommand()
    {
        return renewCommand;
    }

    public void setRenewCommand(String renewCommand)
    {
        this.renewCommand = renewCommand;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getLastCheckedAt()
    {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Date lastCheckedAt)
    {
        this.lastCheckedAt = lastCheckedAt;
    }

    public Date getLastRenewedAt()
    {
        return lastRenewedAt;
    }

    public void setLastRenewedAt(Date lastRenewedAt)
    {
        this.lastRenewedAt = lastRenewedAt;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }

    public String getLastLog()
    {
        return lastLog;
    }

    public void setLastLog(String lastLog)
    {
        this.lastLog = lastLog;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("primaryDomain", getPrimaryDomain())
            .append("domains", getDomains())
            .append("provider", getProvider())
            .append("caDirectoryUrl", getCaDirectoryUrl())
            .append("acmeMode", getAcmeMode())
            .append("challengeType", getChallengeType())
            .append("dnsProvider", getDnsProvider())
            .append("webrootPath", getWebrootPath())
            .append("email", getEmail())
            .append("certPath", getCertPath())
            .append("keyPath", getKeyPath())
            .append("fullchainPath", getFullchainPath())
            .append("issuer", getIssuer())
            .append("serialNumber", getSerialNumber())
            .append("notBefore", getNotBefore())
            .append("notAfter", getNotAfter())
            .append("daysRemaining", getDaysRemaining())
            .append("renewBeforeDays", getRenewBeforeDays())
            .append("autoRenew", getAutoRenew())
            .append("deployCommand", getDeployCommand())
            .append("issueCommand", getIssueCommand())
            .append("renewCommand", getRenewCommand())
            .append("status", getStatus())
            .append("lastCheckedAt", getLastCheckedAt())
            .append("lastRenewedAt", getLastRenewedAt())
            .append("lastError", getLastError())
            .append("lastLog", getLastLog())
            .append("enabled", getEnabled())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
