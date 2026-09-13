package com.ruoyi.weight.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.weight.domain.WeightCertificate;

/**
 * HTTPS证书监控Service接口
 *
 * @author ruoyi
 * @date 2026-05-22
 */
public interface IWeightCertificateService
{
    public WeightCertificate selectWeightCertificateById(Long id);

    public List<WeightCertificate> selectWeightCertificateList(WeightCertificate weightCertificate);

    public Map<String, Object> selectWeightCertificateSummary();

    public int insertWeightCertificate(WeightCertificate weightCertificate);

    public int updateWeightCertificate(WeightCertificate weightCertificate);

    public int deleteWeightCertificateByIds(Long[] ids);

    public int deleteWeightCertificateById(Long id);

    public WeightCertificate checkCertificate(Long id);

    public WeightCertificate renewCertificate(Long id, boolean force);

    public void autoRenewDueCertificates();
}
