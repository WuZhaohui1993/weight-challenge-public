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
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户扩展信息Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/user")
public class WeightUserController extends BaseController
{
    private static final String PROFILE_PATH_SEGMENT = "/profile/";

    @Autowired
    private IWeightUserService weightUserService;

    /**
     * 查询用户扩展信息列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightUser weightUser)
    {
        startPage();
        List<WeightUser> list = weightUserService.selectWeightUserList(weightUser);
        return getDataTable(list);
    }

    /**
     * 导出用户扩展信息列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:export')")
    @Log(title = "用户扩展信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightUser weightUser)
    {
        List<WeightUser> list = weightUserService.selectWeightUserList(weightUser);
        ExcelUtil<WeightUser> util = new ExcelUtil<WeightUser>(WeightUser.class);
        util.exportExcel(response, list, "用户扩展信息数据");
    }

    /**
     * 获取用户扩展信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(weightUserService.selectWeightUserByUserId(userId));
    }

    /**
     * 新增用户扩展信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:add')")
    @Log(title = "用户扩展信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightUser weightUser)
    {
        weightUser.setAvatar(normalizeProfileAsset(weightUser.getAvatar()));
        return toAjax(weightUserService.insertWeightUser(weightUser));
    }

    /**
     * 修改用户扩展信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:edit')")
    @Log(title = "用户扩展信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightUser weightUser)
    {
        weightUser.setAvatar(normalizeProfileAsset(weightUser.getAvatar()));
        return toAjax(weightUserService.updateWeightUser(weightUser));
    }

    /**
     * 删除用户扩展信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:user:remove')")
    @Log(title = "用户扩展信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(weightUserService.deleteWeightUserByUserIds(userIds));
    }

    private String normalizeProfileAsset(String rawValue)
    {
        if (rawValue == null)
        {
            return null;
        }

        String value = rawValue.trim();
        if (value.isEmpty())
        {
            return value;
        }

        int profileIndex = value.indexOf(PROFILE_PATH_SEGMENT);
        if (profileIndex >= 0)
        {
            return value.substring(profileIndex);
        }

        return value;
    }
}
