package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleDepositMapper;
import com.ruoyi.weight.domain.WeightCircleDeposit;
import com.ruoyi.weight.service.IWeightCircleDepositService;

/**
 * 押金记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleDepositServiceImpl implements IWeightCircleDepositService 
{
    @Autowired
    private WeightCircleDepositMapper weightCircleDepositMapper;

    /**
     * 查询押金记录
     * 
     * @param id 押金记录主键
     * @return 押金记录
     */
    @Override
    public WeightCircleDeposit selectWeightCircleDepositById(Long id)
    {
        return weightCircleDepositMapper.selectWeightCircleDepositById(id);
    }

    /**
     * 查询押金记录列表
     * 
     * @param weightCircleDeposit 押金记录
     * @return 押金记录
     */
    @Override
    public List<WeightCircleDeposit> selectWeightCircleDepositList(WeightCircleDeposit weightCircleDeposit)
    {
        return weightCircleDepositMapper.selectWeightCircleDepositList(weightCircleDeposit);
    }

    /**
     * 新增押金记录
     * 
     * @param weightCircleDeposit 押金记录
     * @return 结果
     */
    @Override
    public int insertWeightCircleDeposit(WeightCircleDeposit weightCircleDeposit)
    {
        weightCircleDeposit.setCreateTime(DateUtils.getNowDate());
        return weightCircleDepositMapper.insertWeightCircleDeposit(weightCircleDeposit);
    }

    /**
     * 修改押金记录
     * 
     * @param weightCircleDeposit 押金记录
     * @return 结果
     */
    @Override
    public int updateWeightCircleDeposit(WeightCircleDeposit weightCircleDeposit)
    {
        weightCircleDeposit.setUpdateTime(DateUtils.getNowDate());
        return weightCircleDepositMapper.updateWeightCircleDeposit(weightCircleDeposit);
    }

    /**
     * 批量删除押金记录
     * 
     * @param ids 需要删除的押金记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleDepositByIds(Long[] ids)
    {
        return weightCircleDepositMapper.deleteWeightCircleDepositByIds(ids);
    }

    /**
     * 删除押金记录信息
     * 
     * @param id 押金记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleDepositById(Long id)
    {
        return weightCircleDepositMapper.deleteWeightCircleDepositById(id);
    }
}
