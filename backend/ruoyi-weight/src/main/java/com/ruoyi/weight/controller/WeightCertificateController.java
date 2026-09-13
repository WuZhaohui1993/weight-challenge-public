package com.ruoyi.weight.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.weight.domain.WeightCertificate;
import com.ruoyi.weight.service.IWeightCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTPS证书监控Controller
 *
 * @author ruoyi
 * @date 2026-05-22
 */
@RestController
@RequestMapping("/ruoyi-weight/certificate")
public class WeightCertificateController extends BaseController
{
    @Autowired
    private IWeightCertificateService weightCertificateService;

    /**
     * 查询HTTPS证书监控列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCertificate weightCertificate)
    {
        startPage();
        List<WeightCertificate> list = weightCertificateService.selectWeightCertificateList(weightCertificate);
        return getDataTable(list);
    }

    /**
     * 获取HTTPS证书监控概览
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:list')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(weightCertificateService.selectWeightCertificateSummary());
    }

    /**
     * 导出HTTPS证书监控列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:export')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCertificate weightCertificate)
    {
        List<WeightCertificate> list = weightCertificateService.selectWeightCertificateList(weightCertificate);
        ExcelUtil<WeightCertificate> util = new ExcelUtil<WeightCertificate>(WeightCertificate.class);
        util.exportExcel(response, list, "HTTPS证书监控数据");
    }

    /**
     * 获取HTTPS证书监控详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCertificateService.selectWeightCertificateById(id));
    }

    /**
     * 新增HTTPS证书监控
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:add')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCertificate weightCertificate)
    {
        weightCertificate.setCreateBy(getUsername());
        return toAjax(weightCertificateService.insertWeightCertificate(weightCertificate));
    }

    /**
     * 修改HTTPS证书监控
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:edit')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCertificate weightCertificate)
    {
        weightCertificate.setUpdateBy(getUsername());
        return toAjax(weightCertificateService.updateWeightCertificate(weightCertificate));
    }

    /**
     * 删除HTTPS证书监控
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:remove')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCertificateService.deleteWeightCertificateByIds(ids));
    }

    /**
     * 检测证书状态
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:check')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.OTHER)
    @PostMapping("/{id}/check")
    public AjaxResult check(@PathVariable("id") Long id)
    {
        return success(weightCertificateService.checkCertificate(id));
    }

    /**
     * 申请或续签证书
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:certificate:renew')")
    @Log(title = "HTTPS证书监控", businessType = BusinessType.OTHER)
    @PostMapping("/{id}/renew")
    public AjaxResult renew(@PathVariable("id") Long id,
            @RequestParam(value = "force", required = false, defaultValue = "false") boolean force)
    {
        return success(weightCertificateService.renewCertificate(id, force));
    }
}
