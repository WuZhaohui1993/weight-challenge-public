package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleCategory;

/**
 * 圈子分类Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightCircleCategoryMapper 
{
    /**
     * 查询圈子分类
     * 
     * @param id 圈子分类主键
     * @return 圈子分类
     */
    public WeightCircleCategory selectWeightCircleCategoryById(Long id);

    /**
     * 查询圈子分类列表
     * 
     * @param weightCircleCategory 圈子分类
     * @return 圈子分类集合
     */
    public List<WeightCircleCategory> selectWeightCircleCategoryList(WeightCircleCategory weightCircleCategory);

    /**
     * 新增圈子分类
     * 
     * @param weightCircleCategory 圈子分类
     * @return 结果
     */
    public int insertWeightCircleCategory(WeightCircleCategory weightCircleCategory);

    /**
     * 修改圈子分类
     * 
     * @param weightCircleCategory 圈子分类
     * @return 结果
     */
    public int updateWeightCircleCategory(WeightCircleCategory weightCircleCategory);

    /**
     * 删除圈子分类
     * 
     * @param id 圈子分类主键
     * @return 结果
     */
    public int deleteWeightCircleCategoryById(Long id);

    /**
     * 批量删除圈子分类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightCircleCategoryByIds(Long[] ids);
}
