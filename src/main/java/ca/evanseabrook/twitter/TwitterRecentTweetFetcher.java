package ca.evanseabrook.twitter;

import ca.evanseabrook.twitter.io.TwitterReader;
import com.google.api.services.bigquery.model.TableRow;
import ca.evanseabrook.twitter.options.TwitterFetcherOptions;
import ca.evanseabrook.twitter.utils.Utils;
import ca.evanseabrook.twitter.model.TweetObject;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * This class is the entry point of our pipeline application.
 * It's responsible for parsing command line arguments and building the pipeline itself.
 */
public class TwitterRecentTweetFetcher {

    private static final Logger logger = LoggerFactory.getLogger(TwitterRecentTweetFetcher.class);

    /**
     * Builds and runs an Apache Beam pipeline that downloads Tweets for the last 7 days for a given handle and loads
     * those Tweets into BigQuery.
     *
     * @param options The {@code TwitterFetcherOptions} object that provides runtime arguments to the pipeline.
     * @throws URISyntaxException If the schema_tweets.json file can't be read.
     * @throws IOException If the schema_tweets.json file can't be read.
     */
    public static void runTwitterFetcher(TwitterFetcherOptions options) throws URISyntaxException, IOException {

        Pipeline p = Pipeline.create(options);

        p.apply("ReadTweets", TwitterReader.read(options.getApiKey(),
                options.getApiSecret(),
                options.getAccessToken(),
                options.getAccessTokenSecret(),
                options.getTwitterHandle()))
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
                .withJsonSchema(FileUtils.readFileToString(new File(Objects.requireNonNull(TwitterRecentTweetFetcher.class.getClassLoader().getResource("schema_tweets.json")).toURI()), "UTF-8"))
                .withCustomGcsTempLocation(options.getTemporaryBQLocation())
        );
        PipelineResult result = p.run();
        try {
            result.getState();
            result.waitUntilFinish();
        } catch (UnsupportedOperationException e) {
            // Do nothing. This will be raised when generating the template, since we're waiting for the pipeline to finish.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TwitterFetcherOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(TwitterFetcherOptions.class);
        try {
            runTwitterFetcher(options);
        } catch (URISyntaxException | IOException e) {
            logger.error(String.format("There was an error reading the BQ schema: %s", e.getMessage()));
            System.exit(1);
        }
    }
}
