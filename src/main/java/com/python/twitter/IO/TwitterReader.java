package com.python.twitter.IO;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.*;
import com.github.redouane59.twitter.dto.user.User;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.google.auto.value.AutoValue;
import com.python.twitter.model.TweetObject;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;

import java.nio.charset.StandardCharsets;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TwitterReader {
    public static ListTweets read(String apiKey, String apiSecretKey, String accessToken, String accessTokenSecret, String twitterUser) {
        return new ListTweets(
                ValueProvider.StaticValueProvider.of(twitterUser),
                apiKey,
                apiSecretKey,
                accessToken,
                accessTokenSecret
        );

    }

    public static class ListTweets extends PTransform<PBegin, PCollection<TweetObject>> {

        private final ValueProvider<String> twitterHandle;
        private final String apiKey;
        private final String apiSecretKey;
        private final String accessToken;
        private final String accessTokenSecret;

        ListTweets(ValueProvider<String> twitterHandle,
                   String apiKey,
                   String apiSecretKey,
                   String accessToken,
                   String accessTokenSecret
        ) {
            this.twitterHandle = twitterHandle;
            this.apiKey = apiKey;
            this.apiSecretKey = apiSecretKey;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }

        public PCollection<TweetObject> expand(PBegin input) {
            return input
                    .apply(Create.ofProvider(this.twitterHandle, StringUtf8Coder.of()))
                    .apply(ParDo.of(new ListTweetsFn(this.apiKey, this.apiSecretKey, this.accessToken, this.accessTokenSecret)))
                    .setCoder(SerializableCoder.of(TweetObject.class));
        }
    }

    static class ListTweetsFn extends DoFn<String, TweetObject> {

        private transient TwitterClient client;

        private String apiKey;
        private String apiSecretKey;
        private String accessToken;
        private String accessTokenSecret;

        private final Logger logger = LogManager.getLogger(ListTweetsFn.class);

        ListTweetsFn(
                String apiKey,
                String apiSecretKey,
                String accessToken,
                String accessTokenSecret
        ) {
            this.apiKey = apiKey;
            this.apiSecretKey = apiSecretKey;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }

        @Setup
        public void initClient() {

            logger.info("About to connect to the Twitter API!");
            logger.info(String.format("API Key: %s\nAPI secret key: %s\nAccessToken: %s\nAccess Token secret: %s", this.apiKey, this.apiSecretKey, this.accessToken, this.accessTokenSecret));

            TwitterCredentials creds = TwitterCredentials.builder()
                    .apiKey(this.apiKey)
                    .apiSecretKey(this.apiSecretKey)
                    .accessToken(this.accessToken)
                    .accessTokenSecret(this.accessTokenSecret)
                    .build();

            this.client = new TwitterClient(creds);
        }

        @ProcessElement
        public void listTweets(
                @Element String twitterHandle,
                OutputReceiver<TweetObject> outputReceiver
        ) {
            List<Tweet> tweets = this.client.searchForTweetsWithin7days(String.format("from:%s", twitterHandle));

            for (Tweet t : tweets ) {
                logger.info(String.format("Received tweet: %s", t.getText()));
                outputReceiver.output(new TweetObject(
                        t.getId(),
                        new String(t.getText().getBytes(), StandardCharsets.UTF_8),
                        t.getAuthorId(),
                        t.getRetweetCount(),
                        t.getLikeCount(),
                        t.getReplyCount(),
                        t.getQuoteCount(),
                        t.getCreatedAt(),
                        t.getLang()
                ));
            }
        }
    }
}
