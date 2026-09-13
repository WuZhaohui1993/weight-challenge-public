package com.ruoyi.weight.mapper;

import com.ruoyi.weight.domain.WeightFeedLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeightFeedLikeMapper {

    WeightFeedLike selectWeightFeedLikeById(Long id);

    List<WeightFeedLike> selectWeightFeedLikeList(WeightFeedLike weightFeedLike);

    int insertWeightFeedLike(WeightFeedLike weightFeedLike);

    int updateWeightFeedLike(WeightFeedLike weightFeedLike);

    int deleteWeightFeedLikeById(Long id);

    int deleteWeightFeedLikeByIds(Long[] ids);

    List<WeightFeedLike> selectWeightFeedLikeListByUserAndFeedIds(@Param("userId") Long userId,
                                                                  @Param("feedIds") List<Long> feedIds);

    int deleteWeightFeedLikeByFeedIdAndUserId(@Param("feedId") Long feedId, @Param("userId") Long userId);
}
