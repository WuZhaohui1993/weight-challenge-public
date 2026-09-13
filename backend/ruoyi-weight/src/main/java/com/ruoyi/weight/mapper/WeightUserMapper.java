package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightUser;
import org.apache.ibatis.annotations.Param;

/**
 * 用户扩展信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightUserMapper 
{
    /**
     * 查询用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 用户扩展信息
     */
    public WeightUser selectWeightUserByUserId(Long userId);

    /**
     * 查询用户扩展信息列表
     * 
     * @param weightUser 用户扩展信息
     * @return 用户扩展信息集合
     */
    public List<WeightUser> selectWeightUserList(WeightUser weightUser);

    /**
     * 查询全部用户ID
     *
     * @return 用户ID集合
     */
    public List<Long> selectAllWeightUserIds();

    /**
     * 查询已存在的用户ID
     *
     * @param userIds 用户ID集合
     * @return 已存在的用户ID集合
     */
    public List<Long> selectExistingWeightUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 新增用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    public int insertWeightUser(WeightUser weightUser);

    /**
     * 修改用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    public int updateWeightUser(WeightUser weightUser);

    /**
     * 清空用户主圈子
     *
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 结果
     */
    public int clearMainCircleIdByUserId(@Param("userId") Long userId, @Param("updateBy") String updateBy);

    /**
     * 设置用户主圈子
     *
     * @param userId 用户ID
     * @param mainCircleId 主圈子ID
     * @param updateBy 更新人
     * @return 结果
     */
    public int updateMainCircleIdByUserId(@Param("userId") Long userId,
                                          @Param("mainCircleId") Long mainCircleId,
                                          @Param("updateBy") String updateBy);

    /**
     * 清空用户当前体重与 BMI
     *
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 结果
     */
    public int clearCurrentWeightAndBmiByUserId(@Param("userId") Long userId, @Param("updateBy") String updateBy);

    /**
     * 删除用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 结果
     */
    public int deleteWeightUserByUserId(Long userId);

    /**
     * 批量删除用户扩展信息
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightUserByUserIds(Long[] userIds);
}
