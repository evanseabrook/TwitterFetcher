package com.python.twitter.model;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TweetObject implements java.io.Serializable {
    public String tweetId;
    public String tweetBody;
    public String authorId;
    public int retweetCount;
    public int likeCount;
    public int replyCount;
    public int quoteCount;
    public LocalDateTime createdAt;
    public String lang;

    public TweetObject(
                @Nullable String tweetId,
                @Nullable String tweetBody,
                @Nullable String authorId,
                int retweetCount,
                int likeCount,
                int replyCount,
                int quoteCount,
                @Nullable LocalDateTime createdAt,
                @Nullable String lang
    ) {
        this.tweetId = tweetId;
        this.tweetBody = tweetBody;
        this.authorId = authorId;
        this.retweetCount = retweetCount;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.quoteCount = quoteCount;
        this.createdAt = createdAt;
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TweetObject that = (TweetObject) o;
        return retweetCount == that.retweetCount &&
                likeCount == that.likeCount &&
                replyCount == that.replyCount &&
                quoteCount == that.quoteCount &&
                Objects.equals(tweetId, that.tweetId) &&
                Objects.equals(tweetBody, that.tweetBody) &&
                Objects.equals(authorId, that.authorId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lang, that.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweetId, tweetBody, authorId, retweetCount, likeCount, replyCount, quoteCount, createdAt, lang);
    }
}