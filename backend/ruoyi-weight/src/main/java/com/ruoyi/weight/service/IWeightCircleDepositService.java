package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleDeposit;

/**
 * 押金记录Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCircleDepositService 
{
    /**
     * 查询押金记录
     * 
     * @param id 押金记录主键
     * @return 押金记录
     */
    public WeightCircleDeposit selectWeightCircleDepositById(Long id);

    /**
     * 查询押金记录列表
     * 
     * @param weightCircleDeposit 押金记录
     * @return 押金记录集合
     */
    public List<WeightCircleDeposit> selectWeightCircleDepositList(WeightCircleDeposit weightCircleDeposit);

    /**
     * 新增押金记录
     * 
     * @param weightCircleDeposit 押金记录
     * @return 结果
     */
    public int insertWeightCircleDeposit(WeightCircleDeposit weightCircleDeposit);

    /**
     * 修改押金记录
     * 
     * @param weightCircleDeposit 押金记录
     * @return 结果
     */
    public int updateWeightCircleDeposit(WeightCircleDeposit weightCircleDeposit);

    /**
     * 批量删除押金记录
     * 
     * @param ids 需要删除的押金记录主键集合
     * @return 结果
     */
    public int deleteWeightCircleDepositByIds(Long[] ids);

    /**
     * 删除押金记录信息
     * 
     * @param id 押金记录主键
     * @return 结果
     */
    public int deleteWeightCircleDepositById(Long id);
}
