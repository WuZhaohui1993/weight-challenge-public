package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightCertificate;

/**
 * HTTPS证书监控Mapper接口
 *
 * @author ruoyi
 * @date 2026-05-22
 */
public interface WeightCertificateMapper
{
    public WeightCertificate selectWeightCertificateById(Long id);

    public List<WeightCertificate> selectWeightCertificateList(WeightCertificate weightCertificate);

    public List<WeightCertificate> selectEnabledAutoRenewCertificates();

    public int insertWeightCertificate(WeightCertificate weightCertificate);

    public int updateWeightCertificate(WeightCertificate weightCertificate);

    public int deleteWeightCertificateById(Long id);

    public int deleteWeightCertificateByIds(Long[] ids);
}
