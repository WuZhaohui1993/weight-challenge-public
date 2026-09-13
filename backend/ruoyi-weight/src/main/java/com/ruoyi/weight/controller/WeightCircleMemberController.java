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
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 圈子成员Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/member")
public class WeightCircleMemberController extends BaseController
{
    @Autowired
    private IWeightCircleMemberService weightCircleMemberService;

    /**
     * 查询圈子成员列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircleMember weightCircleMember)
    {
        startPage();
        List<WeightCircleMember> list = weightCircleMemberService.selectWeightCircleMemberList(weightCircleMember);
        return getDataTable(list);
    }

    /**
     * 导出圈子成员列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:export')")
    @Log(title = "圈子成员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircleMember weightCircleMember)
    {
        List<WeightCircleMember> list = weightCircleMemberService.selectWeightCircleMemberList(weightCircleMember);
        ExcelUtil<WeightCircleMember> util = new ExcelUtil<WeightCircleMember>(WeightCircleMember.class);
        util.exportExcel(response, list, "圈子成员数据");
    }

    /**
     * 获取圈子成员详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleMemberService.selectWeightCircleMemberById(id));
    }

    /**
     * 新增圈子成员
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:add')")
    @Log(title = "圈子成员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircleMember weightCircleMember)
    {
        return toAjax(weightCircleMemberService.insertWeightCircleMember(weightCircleMember));
    }

    /**
     * 修改圈子成员
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:edit')")
    @Log(title = "圈子成员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircleMember weightCircleMember)
    {
        return toAjax(weightCircleMemberService.updateWeightCircleMember(weightCircleMember));
    }

    /**
     * 删除圈子成员
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:member:remove')")
    @Log(title = "圈子成员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleMemberService.deleteWeightCircleMemberByIds(ids));
    }
}
