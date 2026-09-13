package com.ruoyi.weight.service;

import com.ruoyi.weight.domain.WeightFeedLike;

import java.util.List;

public interface IWeightFeedLikeService {

    WeightFeedLike selectWeightFeedLikeById(Long id);

    List<WeightFeedLike> selectWeightFeedLikeList(WeightFeedLike weightFeedLike);

    int insertWeightFeedLike(WeightFeedLike weightFeedLike);

    int updateWeightFeedLike(WeightFeedLike weightFeedLike);

    int deleteWeightFeedLikeByIds(Long[] ids);

    int deleteWeightFeedLikeById(Long id);

    List<WeightFeedLike> selectWeightFeedLikeListByUserAndFeedIds(Long userId, List<Long> feedIds);

    int deleteWeightFeedLikeByFeedIdAndUserId(Long feedId, Long userId);
}
