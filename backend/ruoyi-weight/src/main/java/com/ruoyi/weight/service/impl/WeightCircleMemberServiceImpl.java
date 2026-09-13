package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleMemberMapper;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.service.IWeightCircleMemberService;

/**
 * 圈子成员Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleMemberServiceImpl implements IWeightCircleMemberService 
{
    @Autowired
    private WeightCircleMemberMapper weightCircleMemberMapper;

    /**
     * 查询圈子成员
     * 
     * @param id 圈子成员主键
     * @return 圈子成员
     */
    @Override
    public WeightCircleMember selectWeightCircleMemberById(Long id)
    {
        return weightCircleMemberMapper.selectWeightCircleMemberById(id);
    }

    /**
     * 查询圈子成员列表
     * 
     * @param weightCircleMember 圈子成员
     * @return 圈子成员
     */
    @Override
    public List<WeightCircleMember> selectWeightCircleMemberList(WeightCircleMember weightCircleMember)
    {
        return weightCircleMemberMapper.selectWeightCircleMemberList(weightCircleMember);
    }

    /**
     * 新增圈子成员
     * 
     * @param weightCircleMember 圈子成员
     * @return 结果
     */
    @Override
    public int insertWeightCircleMember(WeightCircleMember weightCircleMember)
    {
        weightCircleMember.setCreateTime(DateUtils.getNowDate());
        return weightCircleMemberMapper.insertWeightCircleMember(weightCircleMember);
    }

    /**
     * 修改圈子成员
     * 
     * @param weightCircleMember 圈子成员
     * @return 结果
     */
    @Override
    public int updateWeightCircleMember(WeightCircleMember weightCircleMember)
    {
        weightCircleMember.setUpdateTime(DateUtils.getNowDate());
        return weightCircleMemberMapper.updateWeightCircleMember(weightCircleMember);
    }

    /**
     * 批量删除圈子成员
     * 
     * @param ids 需要删除的圈子成员主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleMemberByIds(Long[] ids)
    {
        return weightCircleMemberMapper.deleteWeightCircleMemberByIds(ids);
    }

    /**
     * 删除圈子成员信息
     * 
     * @param id 圈子成员主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleMemberById(Long id)
    {
        return weightCircleMemberMapper.deleteWeightCircleMemberById(id);
    }
}
