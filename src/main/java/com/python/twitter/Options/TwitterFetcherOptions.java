package com.python.twitter.Options;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;

public interface TwitterFetcherOptions extends PipelineOptions {

    @Description("The handle of the Twitter user to pull Tweets for.")
    @Validation.Required
    String getTwitterHandle();
    void setTwitterHandle(String value);

    @Description("The API key to use with the Twitter API.")
    @Validation.Required
    String getApiKey();
    void setApiKey(String value);

    @Description("The API Secret to use with the Twitter API.")
    @Validation.Required
    String getApiSecret();
    void setApiSecret(String value);

    @Description("The Access token to use with the Twitter API.")
    @Validation.Required
    String getAccessToken();
    void setAccessToken(String value);

    @Description("The Access Token Secret to use with the Twitter API.")
    @Validation.Required
    String getAccessTokenSecret();
    void setAccessTokenSecret(String value);

    @Description("The path to the staging directory used by BQ prior to loading the data.")
    @Validation.Required
    String getTemporaryBQLocation();
    void setTemporaryBQLocation(String value);

    @Description("The fully qualified name of the table to be inserted into.")
    @Validation.Required
    String getSinkBQTable();
    void setSinkBQTable(String value);
}
