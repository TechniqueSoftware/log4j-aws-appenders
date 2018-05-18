# JSON Layout

While the standard Log4J [PatternLayout](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html)
works well for humans grepping logs, large-scale log management revolves around tools such as 
[Elasticsearch](https://www.elastic.co/products/elasticsearch), which can filter and visualize
log data. However, Elasticsearch wants JSON as input, and while you can use a Lambda to parse
text logs, it's faster and less error-prone to simply send JSON.

This layout transforms the Log4J [LogEvent](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/pattern/LogEvent.html)
into JSON, adding optional information such as the server's hostname, EC2 instance tags, and
user-defined metadata.


## Configuration

The complete list of properties is as follows (also available in the JavaDoc). Boolean properties are disabled if
not specified, explicitly disabled with the case-insensitive value "false", and explicitly enabled with the
case-insensitive value "true".

 Name               | Type      | Description
--------------------|-----------|----------------------------------------------------------------------------------------------------------------
`enableLocation`    | Boolean   | If "true", the JSON will include a sub-object that holds the location (class, source file, and line number) where the log message was written. This adds to the cost of every logging message so should not be enabled in production.
`enableInstanceId`  | Boolean   | If "true", the JSON will include the EC2 instance ID where the application is running. This is retrieved from EC2 metadata, and will delay application startup if it's not running on EC2.
`enableHostname`    | Boolean   | If "true", the JSON will include the name of the machine where the application is running, retrieved from the Java runtime. This is often a better choice than instance ID.
`instanceTags`      | String    | If present, the JSON will include a sub-object with specified EC2 instance tags. See below for more information.
`tags`              | String    | If present, the JSON will include a sub-object with specified user metadata. See below for more information.


## Data

The generated JSON object will have the following properties, some of which are optional:

 Key            | Value
----------------|------------------------------------------------------------------------------------------------------------------------
 `timestamp`    | The date/time that the message was logged, formatted as an [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601) timestamp with milliseconds (example: `2017-10-15T23:19:02.123Z`)
 `thread`       | The name of the thread where the message was logged.
 `logger`       | The name of the logger (normally the class that's writing the message, but you can use custom loggers).
 `level`        | The level of the log message: DEBUG, INFO, WARNING, ERROR.
 `message`      | The logged message.
 `processId`    | The PID of the invoking process, if available (this is retrieved from `RuntimeMxBean` and may not be available on all platforms).
 `exception`    | The stack trace of an associated exception, if one exists. This is exposed as an array of strings, with the first element being the location where the exception was caught.
 `mdc`          | The mapped diagnostic context, if it exists. This is a child map containing whatever entries are in the MDC.
 `ndc`          | The nested diagnostic context, if it exists. This is a single string that contains each of the pushed entries separated by spaces (yes, that's how Log4J provides it).
 `locationInfo` | The location where the logger was called. This is a child object with the following components: `className`, `methodName`, `fileName`, `lineNumber`.
 `instanceId`   | The EC2 instance ID of the machine where the logger is running. *WARNING*: this will delay appender initialization if not running on EC2.
 `instanceTags` | Optional sub-object containing specific EC2 instance tags; see below.
 `hostname`     | The name of the machine where the logger is running, if available (this is currently retrieved from `RuntimeMxBean` and may not be available on all platforms).
 `tags`         | Optional sub-object containing user-specified metadata; see below.


## Metadata

The `tags` and `instanceTags` properties are intended to provide metadata for search-based log analysis.
Both result in the creation of a like-named sub-object containing the desired metadata. 

* `tags` takes a comma-separated list of `NAME=VALUE` pairs, and inserts those values in the sub-object.
  You can use substitutions (eg: `runDate={date}`), but may not use names (or values) that contain either
  '=' or ','.
* `instanceTags` takes a comma-separated list of names, or the special value `*`. In the first case it
  retrieves all named tags (if they exist) and adds them to the metadata. In the second case it adds all
  tags to the metadata.

In both cases the metadata sub-objects are evaluated with the first message, and are not re-evaluated.
This is primarily of concern when using instance tags, as any changes to the actual tags will not be
reflected in the metadata.

Beware that `instanceTags` attempts to retrieve EC2 metadata, and then uses a `DescribeTags` operation
to retrieve the tag values. The former will time-out (delaying logging) if you're not running on an
EC2 instance, and the latter will fail if you do not have `ec2:DescribeTags` permission.


## Example

Here's a configuration that enables all features, including retrieval of all instance tags:

```
log4j.appender.kinesis.layout=com.kdgregory.log4j.aws.JsonLayout
log4j.appender.kinesis.layout.enableHostname=true
log4j.appender.kinesis.layout.enableLocation=true
log4j.appender.kinesis.layout.tags=applicationName=Example,runDate={date},env=prod
```

The raw output looks like this:

```
```

Running it through a pretty-printer, you get this:

```
```
