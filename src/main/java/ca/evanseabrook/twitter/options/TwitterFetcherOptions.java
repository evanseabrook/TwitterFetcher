package ca.evanseabrook.twitter.options;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.ValueProvider;

public interface TwitterFetcherOptions extends PipelineOptions {

    @Description("The handle of the Twitter user to pull Tweets for.")
    @Validation.Required
    ValueProvider<String> getTwitterHandle();

    void setTwitterHandle(ValueProvider<String> value);

    @Description("The API key to use with the Twitter API.")
    @Validation.Required
    ValueProvider<String> getApiKey();

    void setApiKey(ValueProvider<String> value);

    @Description("The API Secret to use with the Twitter API.")
    @Validation.Required
    ValueProvider<String> getApiSecret();

    void setApiSecret(ValueProvider<String> value);

    @Description("The Access token to use with the Twitter API.")
    @Validation.Required
    ValueProvider<String> getAccessToken();

    void setAccessToken(ValueProvider<String> value);

    @Description("The Access Token Secret to use with the Twitter API.")
    @Validation.Required
    ValueProvider<String> getAccessTokenSecret();

    void setAccessTokenSecret(ValueProvider<String> value);

    @Description("The path to the staging directory used by BQ prior to loading the data.")
    @Validation.Required
    ValueProvider<String> getTemporaryBQLocation();

    void setTemporaryBQLocation(ValueProvider<String> value);

    @Description("The fully qualified name of the table to be inserted into.")
    @Validation.Required
    ValueProvider<String> getSinkBQTable();

    void setSinkBQTable(ValueProvider<String> value);
}
