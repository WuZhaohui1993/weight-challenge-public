package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleMapper;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.service.IWeightCircleService;

/**
 * 圈子主Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleServiceImpl implements IWeightCircleService 
{
    @Autowired
    private WeightCircleMapper weightCircleMapper;

    /**
     * 查询圈子主
     * 
     * @param id 圈子主主键
     * @return 圈子主
     */
    @Override
    public WeightCircle selectWeightCircleById(Long id)
    {
        return weightCircleMapper.selectWeightCircleById(id);
    }

    /**
     * 查询圈子主列表
     * 
     * @param weightCircle 圈子主
     * @return 圈子主
     */
    @Override
    public List<WeightCircle> selectWeightCircleList(WeightCircle weightCircle)
    {
        return weightCircleMapper.selectWeightCircleList(weightCircle);
    }

    /**
     * 新增圈子主
     * 
     * @param weightCircle 圈子主
     * @return 结果
     */
    @Override
    public int insertWeightCircle(WeightCircle weightCircle)
    {
        weightCircle.setCreateTime(DateUtils.getNowDate());
        return weightCircleMapper.insertWeightCircle(weightCircle);
    }

    /**
     * 修改圈子主
     * 
     * @param weightCircle 圈子主
     * @return 结果
     */
    @Override
    public int updateWeightCircle(WeightCircle weightCircle)
    {
        weightCircle.setUpdateTime(DateUtils.getNowDate());
        return weightCircleMapper.updateWeightCircle(weightCircle);
    }

    /**
     * 批量删除圈子主
     * 
     * @param ids 需要删除的圈子主主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleByIds(Long[] ids)
    {
        return weightCircleMapper.deleteWeightCircleByIds(ids);
    }

    /**
     * 删除圈子主信息
     * 
     * @param id 圈子主主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleById(Long id)
    {
        return weightCircleMapper.deleteWeightCircleById(id);
    }
}
