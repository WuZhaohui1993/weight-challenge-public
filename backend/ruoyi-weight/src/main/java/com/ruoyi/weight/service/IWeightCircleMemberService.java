package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleMember;

/**
 * 圈子成员Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCircleMemberService 
{
    /**
     * 查询圈子成员
     * 
     * @param id 圈子成员主键
     * @return 圈子成员
     */
    public WeightCircleMember selectWeightCircleMemberById(Long id);

    /**
     * 查询圈子成员列表
     * 
     * @param weightCircleMember 圈子成员
     * @return 圈子成员集合
     */
    public List<WeightCircleMember> selectWeightCircleMemberList(WeightCircleMember weightCircleMember);

    /**
     * 新增圈子成员
     * 
     * @param weightCircleMember 圈子成员
     * @return 结果
     */
    public int insertWeightCircleMember(WeightCircleMember weightCircleMember);

    /**
     * 修改圈子成员
     * 
     * @param weightCircleMember 圈子成员
     * @return 结果
     */
    public int updateWeightCircleMember(WeightCircleMember weightCircleMember);

    /**
     * 批量删除圈子成员
     * 
     * @param ids 需要删除的圈子成员主键集合
     * @return 结果
     */
    public int deleteWeightCircleMemberByIds(Long[] ids);

    /**
     * 删除圈子成员信息
     * 
     * @param id 圈子成员主键
     * @return 结果
     */
    public int deleteWeightCircleMemberById(Long id);
}
