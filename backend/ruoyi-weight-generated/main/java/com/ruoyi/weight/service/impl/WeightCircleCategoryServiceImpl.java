package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleCategoryMapper;
import com.ruoyi.weight.domain.WeightCircleCategory;
import com.ruoyi.weight.service.IWeightCircleCategoryService;

/**
 * 圈子分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleCategoryServiceImpl implements IWeightCircleCategoryService 
{
    @Autowired
    private WeightCircleCategoryMapper weightCircleCategoryMapper;

    /**
     * 查询圈子分类
     * 
     * @param id 圈子分类主键
     * @return 圈子分类
     */
    @Override
    public WeightCircleCategory selectWeightCircleCategoryById(Long id)
    {
        return weightCircleCategoryMapper.selectWeightCircleCategoryById(id);
    }

    /**
     * 查询圈子分类列表
     * 
     * @param weightCircleCategory 圈子分类
     * @return 圈子分类
     */
    @Override
    public List<WeightCircleCategory> selectWeightCircleCategoryList(WeightCircleCategory weightCircleCategory)
    {
        return weightCircleCategoryMapper.selectWeightCircleCategoryList(weightCircleCategory);
    }

    /**
     * 新增圈子分类
     * 
     * @param weightCircleCategory 圈子分类
     * @return 结果
     */
    @Override
    public int insertWeightCircleCategory(WeightCircleCategory weightCircleCategory)
    {
        weightCircleCategory.setCreateTime(DateUtils.getNowDate());
        return weightCircleCategoryMapper.insertWeightCircleCategory(weightCircleCategory);
    }

    /**
     * 修改圈子分类
     * 
     * @param weightCircleCategory 圈子分类
     * @return 结果
     */
    @Override
    public int updateWeightCircleCategory(WeightCircleCategory weightCircleCategory)
    {
        weightCircleCategory.setUpdateTime(DateUtils.getNowDate());
        return weightCircleCategoryMapper.updateWeightCircleCategory(weightCircleCategory);
    }

    /**
     * 批量删除圈子分类
     * 
     * @param ids 需要删除的圈子分类主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleCategoryByIds(Long[] ids)
    {
        return weightCircleCategoryMapper.deleteWeightCircleCategoryByIds(ids);
    }

    /**
     * 删除圈子分类信息
     * 
     * @param id 圈子分类主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleCategoryById(Long id)
    {
        return weightCircleCategoryMapper.deleteWeightCircleCategoryById(id);
    }
}
