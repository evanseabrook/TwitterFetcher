package com.python.twitter;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.python.twitter.IO.TwitterReader;
import com.python.twitter.Options.TwitterFetcherOptions;
import com.python.twitter.Utils.Utils;
import com.python.twitter.model.TweetObject;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.values.PCollection;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TwitterRecentTweetFetcher {

    private static final Logger logger = LogManager.getLogger(TwitterRecentTweetFetcher.class);

    public static void RunTwitterFetcher(TwitterFetcherOptions options) {

        Pipeline p = Pipeline.create(options);

        logger.info("Let's get started");

        PCollection<TweetObject> tweets = p.apply("ReadTweets", TwitterReader.read(options.getApiKey(),
                options.getApiSecret(),
                options.getAccessToken(),
                options.getAccessTokenSecret(),
                options.getTwitterHandle()));

        TableSchema schema = new TableSchema().setFields(
                Arrays.asList(
                        new TableFieldSchema()
                        .setName("tweet_id")
                        .setType("STRING")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("tweet")
                        .setType("STRING")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("author_id")
                        .setType("STRING")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("retweet_count")
                        .setType("INT64")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("like_count")
                        .setType("INT64")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("reply_count")
                        .setType("INT64")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("quote_count")
                        .setType("INT64")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("lang")
                        .setType("STRING")
                        .setMode("NULLABLE"),
                        new TableFieldSchema()
                        .setName("created_at")
                        .setType("DATETIME")
                        .setMode("NULLABLE")
                )
        );

        logger.info(String.format("Temporary BQ location: %s", options.getTemporaryBQLocation()));
        logger.info(String.format("BQ sink location: %s", options.getSinkBQTable()));

        tweets
                .apply("LoadToBq", BigQueryIO.<TweetObject>write().to(options.getSinkBQTable())
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withFormatFunction(
                                (TweetObject t) ->
                                        new TableRow().set("tweet_id", t.tweetId)
                                        .set("tweet", t.tweetBody)
                                        .set("author_id", t.authorId)
                                        .set("retweet_count", t.retweetCount)
                                        .set("like_count", t.likeCount)
                                        .set("reply_count", t.replyCount)
                                        .set("quote_count", t.quoteCount)
                                        .set("lang", t.lang)
                                        .set("created_at", Utils.buildBigQueryDateTime(t.createdAt))
                        )
                        .withSchema(schema)
                        .withCustomGcsTempLocation(ValueProvider.StaticValueProvider.of(options.getTemporaryBQLocation()))
                );
        p.run().waitUntilFinish();
    }

    public static void main(String[] args) {
        TwitterFetcherOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(TwitterFetcherOptions.class);
        RunTwitterFetcher(options);
    }
}
