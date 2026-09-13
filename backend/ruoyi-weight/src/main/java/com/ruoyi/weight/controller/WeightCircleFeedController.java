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
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.service.IWeightCircleFeedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 圈子动态Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/circle-feed")
public class WeightCircleFeedController extends BaseController
{
    @Autowired
    private IWeightCircleFeedService weightCircleFeedService;

    /**
     * 查询圈子动态列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircleFeed weightCircleFeed)
    {
        startPage();
        List<WeightCircleFeed> list = weightCircleFeedService.selectWeightCircleFeedList(weightCircleFeed);
        return getDataTable(list);
    }

    /**
     * 导出圈子动态列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:export')")
    @Log(title = "圈子动态", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircleFeed weightCircleFeed)
    {
        List<WeightCircleFeed> list = weightCircleFeedService.selectWeightCircleFeedList(weightCircleFeed);
        ExcelUtil<WeightCircleFeed> util = new ExcelUtil<WeightCircleFeed>(WeightCircleFeed.class);
        util.exportExcel(response, list, "圈子动态数据");
    }

    /**
     * 获取圈子动态详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleFeedService.selectWeightCircleFeedById(id));
    }

    /**
     * 新增圈子动态
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:add')")
    @Log(title = "圈子动态", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircleFeed weightCircleFeed)
    {
        return toAjax(weightCircleFeedService.insertWeightCircleFeed(weightCircleFeed));
    }

    /**
     * 修改圈子动态
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:edit')")
    @Log(title = "圈子动态", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircleFeed weightCircleFeed)
    {
        return toAjax(weightCircleFeedService.updateWeightCircleFeed(weightCircleFeed));
    }

    /**
     * 删除圈子动态
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:remove')")
    @Log(title = "圈子动态", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleFeedService.deleteWeightCircleFeedByIds(ids));
    }
}
