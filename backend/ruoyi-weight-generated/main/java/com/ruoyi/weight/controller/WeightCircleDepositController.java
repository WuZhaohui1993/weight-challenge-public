package com.ruoyi.weight.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.weight.domain.WeightCircleDeposit;
import com.ruoyi.weight.service.IWeightCircleDepositService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 押金记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/deposit")
public class WeightCircleDepositController extends BaseController
{
    @Autowired
    private IWeightCircleDepositService weightCircleDepositService;

    /**
     * 查询押金记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircleDeposit weightCircleDeposit)
    {
        startPage();
        List<WeightCircleDeposit> list = weightCircleDepositService.selectWeightCircleDepositList(weightCircleDeposit);
        return getDataTable(list);
    }

    /**
     * 导出押金记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:export')")
    @Log(title = "押金记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircleDeposit weightCircleDeposit)
    {
        List<WeightCircleDeposit> list = weightCircleDepositService.selectWeightCircleDepositList(weightCircleDeposit);
        ExcelUtil<WeightCircleDeposit> util = new ExcelUtil<WeightCircleDeposit>(WeightCircleDeposit.class);
        util.exportExcel(response, list, "押金记录数据");
    }

    /**
     * 获取押金记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleDepositService.selectWeightCircleDepositById(id));
    }

    /**
     * 新增押金记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:add')")
    @Log(title = "押金记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircleDeposit weightCircleDeposit)
    {
        return toAjax(weightCircleDepositService.insertWeightCircleDeposit(weightCircleDeposit));
    }

    /**
     * 修改押金记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:edit')")
    @Log(title = "押金记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircleDeposit weightCircleDeposit)
    {
        return toAjax(weightCircleDepositService.updateWeightCircleDeposit(weightCircleDeposit));
    }

    /**
     * 删除押金记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:deposit:remove')")
    @Log(title = "押金记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleDepositService.deleteWeightCircleDepositByIds(ids));
    }
}
