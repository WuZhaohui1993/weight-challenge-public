package com.ruoyi.api.dto;

import java.util.List;

/**
 * 记录提交响应
 */
public class ApiRecordResponse {

    public static class RecordSubmitResult<T> {

        private T record;

        private FeedDraft draft;

        public T getRecord() {
            return record;
        }

        public void setRecord(T record) {
            this.record = record;
        }

        public FeedDraft getDraft() {
            return draft;
        }

        public void setDraft(FeedDraft draft) {
            this.draft = draft;
        }
    }

    public static class FeedDraft {

        private String feedType;

        private String content;

        private String visibilityScope;

        private Long originCircleId;

        private List<Long> syncCircleIds;

        private List<String> images;

        private String sourceType;

        private Long sourceId;

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

        public String getVisibilityScope() {
            return visibilityScope;
        }

        public void setVisibilityScope(String visibilityScope) {
            this.visibilityScope = visibilityScope;
        }

        public Long getOriginCircleId() {
            return originCircleId;
        }

        public void setOriginCircleId(Long originCircleId) {
            this.originCircleId = originCircleId;
        }

        public List<Long> getSyncCircleIds() {
            return syncCircleIds;
        }

        public void setSyncCircleIds(List<Long> syncCircleIds) {
            this.syncCircleIds = syncCircleIds;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
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
}
