package ca.evanseabrook.twitter.io;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.*;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import ca.evanseabrook.twitter.model.TweetObject;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;

import java.nio.charset.StandardCharsets;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TwitterReader is an I/O connector that fetches the last 7 days worth of Tweets for a given Twitter handle.
 */
public class TwitterReader {
    public static ListTweets read(ValueProvider<String> apiKey,
                                  ValueProvider<String> apiSecretKey,
                                  ValueProvider<String> accessToken,
                                  ValueProvider<String> accessTokenSecret,
                                  ValueProvider<String> twitterUser) {
        return new ListTweets(
                twitterUser,
                apiKey,
                apiSecretKey,
                accessToken,
                accessTokenSecret
        );

    }

    public static class ListTweets extends PTransform<PBegin, PCollection<TweetObject>> {

        private final ValueProvider<String> twitterHandle;
        private final ValueProvider<String> apiKey;
        private final ValueProvider<String> apiSecretKey;
        private final ValueProvider<String> accessToken;
        private final ValueProvider<String> accessTokenSecret;

        ListTweets(ValueProvider<String> twitterHandle,
                   ValueProvider<String> apiKey,
                   ValueProvider<String> apiSecretKey,
                   ValueProvider<String> accessToken,
                   ValueProvider<String> accessTokenSecret
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

        private ValueProvider<String> apiKey;
        private ValueProvider<String> apiSecretKey;
        private ValueProvider<String> accessToken;
        private ValueProvider<String> accessTokenSecret;

        private final Logger logger = LoggerFactory.getLogger(ListTweetsFn.class);

        ListTweetsFn(
                ValueProvider<String> apiKey,
                ValueProvider<String> apiSecretKey,
                ValueProvider<String> accessToken,
                ValueProvider<String> accessTokenSecret
        ) {
            this.apiKey = apiKey;
            this.apiSecretKey = apiSecretKey;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }


        @Setup
        public void initClient() {

            TwitterCredentials creds = TwitterCredentials.builder()
                    .apiKey(this.apiKey.get())
                    .apiSecretKey(this.apiSecretKey.get())
                    .accessToken(this.accessToken.get())
                    .accessTokenSecret(this.accessTokenSecret.get())
                    .build();

            this.client = new TwitterClient(creds);
        }

        /**
         * Fetches Tweets for the last 7 days for the given twitterHandle
         * @param twitterHandle The handle of the user you wish to download Tweets for.
         * @param outputReceiver The output receiver that we emit our results to.
         */
        @ProcessElement
        public void listTweets(
                @Element String twitterHandle,
                OutputReceiver<TweetObject> outputReceiver
        ) {


            List<Tweet> tweets = this.client.searchForTweetsWithin7days(String.format("from:%s", twitterHandle));

            for (Tweet t : tweets) {
                logger.debug(String.format("Received tweet: %s", t.getText()));
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
