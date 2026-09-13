package com.ruoyi.api.dto;

/**
 * 移动端通知响应 DTO
 */
public class ApiNotificationResponse {

    public static class NotificationItem {

        private Long id;

        private String type;

        private Long fromUserId;

        private String fromUserNickname;

        private String fromUserAvatar;

        private String targetType;

        private Long targetId;

        private Long feedId;

        private Long commentId;

        private String content;

        private String preview;

        private Boolean read;

        private String createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(Long fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getFromUserNickname() {
            return fromUserNickname;
        }

        public void setFromUserNickname(String fromUserNickname) {
            this.fromUserNickname = fromUserNickname;
        }

        public String getFromUserAvatar() {
            return fromUserAvatar;
        }

        public void setFromUserAvatar(String fromUserAvatar) {
            this.fromUserAvatar = fromUserAvatar;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public Long getTargetId() {
            return targetId;
        }

        public void setTargetId(Long targetId) {
            this.targetId = targetId;
        }

        public Long getFeedId() {
            return feedId;
        }

        public void setFeedId(Long feedId) {
            this.feedId = feedId;
        }

        public Long getCommentId() {
            return commentId;
        }

        public void setCommentId(Long commentId) {
            this.commentId = commentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public Boolean getRead() {
            return read;
        }

        public void setRead(Boolean read) {
            this.read = read;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
