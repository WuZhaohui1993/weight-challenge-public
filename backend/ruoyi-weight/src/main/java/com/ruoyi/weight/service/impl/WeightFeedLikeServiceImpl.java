package com.ruoyi.weight.service.impl;

import com.ruoyi.weight.domain.WeightFeedLike;
import com.ruoyi.weight.mapper.WeightFeedLikeMapper;
import com.ruoyi.weight.service.IWeightFeedLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeightFeedLikeServiceImpl implements IWeightFeedLikeService {

    @Autowired
    private WeightFeedLikeMapper weightFeedLikeMapper;

    @Override
    public WeightFeedLike selectWeightFeedLikeById(Long id) {
        return weightFeedLikeMapper.selectWeightFeedLikeById(id);
    }

    @Override
    public List<WeightFeedLike> selectWeightFeedLikeList(WeightFeedLike weightFeedLike) {
        return weightFeedLikeMapper.selectWeightFeedLikeList(weightFeedLike);
    }

    @Override
    public int insertWeightFeedLike(WeightFeedLike weightFeedLike) {
        return weightFeedLikeMapper.insertWeightFeedLike(weightFeedLike);
    }

    @Override
    public int updateWeightFeedLike(WeightFeedLike weightFeedLike) {
        return weightFeedLikeMapper.updateWeightFeedLike(weightFeedLike);
    }

    @Override
    public int deleteWeightFeedLikeByIds(Long[] ids) {
        return weightFeedLikeMapper.deleteWeightFeedLikeByIds(ids);
    }

    @Override
    public int deleteWeightFeedLikeById(Long id) {
        return weightFeedLikeMapper.deleteWeightFeedLikeById(id);
    }

    @Override
    public List<WeightFeedLike> selectWeightFeedLikeListByUserAndFeedIds(Long userId, List<Long> feedIds) {
        return weightFeedLikeMapper.selectWeightFeedLikeListByUserAndFeedIds(userId, feedIds);
    }

    @Override
    public int deleteWeightFeedLikeByFeedIdAndUserId(Long feedId, Long userId) {
        return weightFeedLikeMapper.deleteWeightFeedLikeByFeedIdAndUserId(feedId, userId);
    }
}
