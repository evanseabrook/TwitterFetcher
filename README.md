# TwitterRecentTweetFetcher

This is a sample project to show how you could interact with the Twitter API using Apache Beam. This pipeline will download Tweets
present in the last 7 days for a given Twitter handle and store the results into a BigQuery table.

For more information, please visit my blog post on [Pythian's Official Blog](https://blog.pythian.com/consuming-tweets-using-apache-beam-on-dataflow/).


## Getting Started
### Prerequisites
To build and use this project, you must:
1. Create a [Google Cloud Platform](http://cloud.google.com/) project
2. The latest version of the [gcloud CLI tools](https://cloud.google.com/sdk)
3. Setup a Twitter API app in the [Twitter Developer Portal](https://developer.twitter.com/)
4. Install JDK 8 and Maven 3.8.1

### Running Locally
You can run this Apache Beam pipeline locally using the Direct Runner:
```shell script
export api_key=<YOUR_KEY>
export api_secret=<YOUR_SECRET>
export access_token=<YOUR_TOKEN>
export access_token_secret=<YOUR_TOKEN_SECRET>
export twitter_handle=<YOUR_HANDLE>
export temp_bq_location=<YOUR_TEMP_GCS_LOCATION>
export bq_sink_table=<YOUR_BQ_TABLE_NAME>

mvn compile exec:java \
  -Dexec.mainClass=ca.evanseabrook.twitter.TwitterRecentTweetFetcher \
  -Dexec.args="--runner=direct --apiKey=${api_key} \
               --apiSecret=${api_secret} \
               --accessToken=${access_token} \ 
               --accessTokenSecret=${access_token_secret} \
               --twitterHandle=${twitter_handle} \
               --temporaryBQLocation=${temp_bq_location} \
               --sinkBQTable=${bq_sink_table}"
```

### Running on GCP Dataflow
You can also choose to publish a custom Dataflow template, which can then be used to create a Dataflow job on GCP.

```shell script
export template_location=<YOUR_TEMPLATE_GCS_LOCATION>
export project_id=<YOUR_GCP_PROJECT_ID>

mvn compile exec:java \
    -Dexec.mainClass=ca.evanseabrook.twitter.TwitterRecentTweetFetcher \
    -Dexec.args="--runner=DataflowRunner \
                --project=${project_id} \
                --templateLocation=${template_location} \
                --region=us-central1"
```
A TWITTER_FETCHER_metadata file has also been provided, which is used to tell Dataflow with runtime parameters the pipeline accepts. This should be put in the same location as your Dataflow template file.

If you've named your template something other than "TWITTER_FETCHER", you will need to rename this metadata file to match whatever you've named it, i.e. `HELLO_WORLD_metadata`.