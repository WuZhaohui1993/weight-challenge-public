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
import com.ruoyi.weight.domain.WeightSocialFeed;
import com.ruoyi.weight.service.IWeightSocialFeedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 关注动态流Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/feed")
public class WeightSocialFeedController extends BaseController
{
    @Autowired
    private IWeightSocialFeedService weightSocialFeedService;

    /**
     * 查询关注动态流列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightSocialFeed weightSocialFeed)
    {
        startPage();
        List<WeightSocialFeed> list = weightSocialFeedService.selectWeightSocialFeedList(weightSocialFeed);
        return getDataTable(list);
    }

    /**
     * 导出关注动态流列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:export')")
    @Log(title = "关注动态流", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightSocialFeed weightSocialFeed)
    {
        List<WeightSocialFeed> list = weightSocialFeedService.selectWeightSocialFeedList(weightSocialFeed);
        ExcelUtil<WeightSocialFeed> util = new ExcelUtil<WeightSocialFeed>(WeightSocialFeed.class);
        util.exportExcel(response, list, "关注动态流数据");
    }

    /**
     * 获取关注动态流详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightSocialFeedService.selectWeightSocialFeedById(id));
    }

    /**
     * 新增关注动态流
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:add')")
    @Log(title = "关注动态流", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightSocialFeed weightSocialFeed)
    {
        return toAjax(weightSocialFeedService.insertWeightSocialFeed(weightSocialFeed));
    }

    /**
     * 修改关注动态流
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:edit')")
    @Log(title = "关注动态流", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightSocialFeed weightSocialFeed)
    {
        return toAjax(weightSocialFeedService.updateWeightSocialFeed(weightSocialFeed));
    }

    /**
     * 删除关注动态流
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feed:remove')")
    @Log(title = "关注动态流", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightSocialFeedService.deleteWeightSocialFeedByIds(ids));
    }
}
