package com.ruoyi.weight.task;

import com.ruoyi.weight.service.IWeightCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 证书自动检查和续签任务。
 *
 * @author ruoyi
 */
@Component("weightCertificateTask")
public class WeightCertificateTask
{
    @Autowired
    private IWeightCertificateService weightCertificateService;

    public void autoRenewDueCertificates()
    {
        weightCertificateService.autoRenewDueCertificates();
    }
}
