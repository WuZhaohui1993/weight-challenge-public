package com.ruoyi.api.dto;

import java.util.List;

/**
 * 移动端动态相关响应 DTO
 */
public class ApiFeedResponse {

    public static class FeedCard {

        private Long id;

        private Long circleId;

        private String circleName;

        private Long originCircleId;

        private String originCircleName;

        private String visibilityScope;

        private Boolean commentEnabled;

        private Long userId;

        private String userNickname;

        private String userAvatar;

        private String feedType;

        private String content;

        private List<String> images;

        private Long likesCount;

        private Long commentsCount;

        private Boolean isFeatured;

        private Boolean likedByMe;

        private String createdAt;

        private List<Long> syncCircleIds;

        private Boolean ownedByMe;

        private String sourceType;

        private Long sourceId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCircleId() {
            return circleId;
        }

        public void setCircleId(Long circleId) {
            this.circleId = circleId;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public Long getOriginCircleId() {
            return originCircleId;
        }

        public void setOriginCircleId(Long originCircleId) {
            this.originCircleId = originCircleId;
        }

        public String getOriginCircleName() {
            return originCircleName;
        }

        public void setOriginCircleName(String originCircleName) {
            this.originCircleName = originCircleName;
        }

        public String getVisibilityScope() {
            return visibilityScope;
        }

        public void setVisibilityScope(String visibilityScope) {
            this.visibilityScope = visibilityScope;
        }

        public Boolean getCommentEnabled() {
            return commentEnabled;
        }

        public void setCommentEnabled(Boolean commentEnabled) {
            this.commentEnabled = commentEnabled;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getFeedType() {
            return feedType;
        }

        public void setFeedType(String feedType) {
            this.feedType = feedType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public Long getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(Long likesCount) {
            this.likesCount = likesCount;
        }

        public Long getCommentsCount() {
            return commentsCount;
        }

        public void setCommentsCount(Long commentsCount) {
            this.commentsCount = commentsCount;
        }

        public Boolean getIsFeatured() {
            return isFeatured;
        }

        public void setIsFeatured(Boolean isFeatured) {
            this.isFeatured = isFeatured;
        }

        public Boolean getLikedByMe() {
            return likedByMe;
        }

        public void setLikedByMe(Boolean likedByMe) {
            this.likedByMe = likedByMe;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public List<Long> getSyncCircleIds() {
            return syncCircleIds;
        }

        public void setSyncCircleIds(List<Long> syncCircleIds) {
            this.syncCircleIds = syncCircleIds;
        }

        public Boolean getOwnedByMe() {
            return ownedByMe;
        }

        public void setOwnedByMe(Boolean ownedByMe) {
            this.ownedByMe = ownedByMe;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Long getSourceId() {
            return sourceId;
        }

        public void setSourceId(Long sourceId) {
            this.sourceId = sourceId;
        }
    }

    public static class FeedCommentItem {

        private Long id;

        private Long feedId;

        private Long userId;

        private String userNickname;

        private String userAvatar;

        private String content;

        private List<String> images;

        private Long replyToCommentId;

        private Long replyToUserId;

        private String replyToUserNickname;

        private Long likesCount;

        private String createdAt;

        private Boolean deleted;

        private Boolean ownedByMe;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getFeedId() {
            return feedId;
        }

        public void setFeedId(Long feedId) {
            this.feedId = feedId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public Long getReplyToCommentId() {
            return replyToCommentId;
        }

        public void setReplyToCommentId(Long replyToCommentId) {
            this.replyToCommentId = replyToCommentId;
        }

        public Long getReplyToUserId() {
            return replyToUserId;
        }

        public void setReplyToUserId(Long replyToUserId) {
            this.replyToUserId = replyToUserId;
        }

        public String getReplyToUserNickname() {
            return replyToUserNickname;
        }

        public void setReplyToUserNickname(String replyToUserNickname) {
            this.replyToUserNickname = replyToUserNickname;
        }

        public Long getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(Long likesCount) {
            this.likesCount = likesCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }

        public Boolean getOwnedByMe() {
            return ownedByMe;
        }

        public void setOwnedByMe(Boolean ownedByMe) {
            this.ownedByMe = ownedByMe;
        }
    }
}
