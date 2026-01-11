Building
========

MockSMTP is a Java 8 application and can be built using Maven 3.0 or later:

To build the executable JAR file, run `mvn package`.
This generates the `target/MockSMTP-<version>-jar-with-dependencies.jar` file from which the application can be run.
(For convenience, this is also included in releases as `release/MockSMTP.jar`.)
Everything is contained within the JAR file, so you can copy this file to a convenient path and run it directly from there.

To start the application, simply run the MockSMTP JAR file in Java:
* To start with default settings: `java -jar /path/to/MockSMTP.jar`
* To start with default setting and included demo data: `java -jar /path/to/MockSMTP.jar --demo`
* To show other command line options: `java -jar /path/to/MockSMTP.jar --help`
