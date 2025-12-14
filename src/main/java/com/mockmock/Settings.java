package com.mockmock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.HashSet;
import java.util.Set;

@Service
@Getter
@Setter
@Command(name = "MockSMTP", description = "MockSMTP is a mock server for testing outgoing emails.")
public class Settings {

    /**
     * Whether to show console output when receiving email.
     */
    private boolean showEmailInConsole = false;

    /** The default port on which MockSMTP will listen for SMTP connections (default is 25). **/
    @Option(names = { "-p", "--smtp-port" }, description = "SMTP server port", defaultValue = "25")
    private int smtpPort = 25;

    /** The default port on which MockSMTP will listen for HTTP API/UI connections (default is 8282). **/
    @Option(names = { "-h", "--http-port" }, description = "HTTP server port", defaultValue = "8282")
    private int httpPort = 8282;

    /** The maximum allowable size of the mail queue. **/
    @Option(names = { "-m", "--max-queue-size" }, description = "maximum number of emails to store", defaultValue = "1000")
    private int maxMailQueueSize = 1000;

    /** A set of sender email addresses whose messages will be discarded when they are received. **/
    @Option(names = { "-ff", "--filter-from" }, split = ",", description = "exclude any emails sent from these addresses (comma-separated)")
    private Set<String> filterFromEmailAddresses = new HashSet<>();

    /** A set of recipient email addresses whose messages will be discarded when they are received. **/
    @Option(names = { "-ft", "--filter-to" }, split = ",", description = "exclude any emails sent to these addresses (comma-separated)")
    private Set<String> filterToEmailAddresses = new HashSet<>();

    /** A flag to indicate whether to preload demo data into the server. **/
    @Option(names = { "--demo" }, description = "load demo data at startup", defaultValue = "false")
    private boolean loadDemoData = false;

    /** A flag to indicate whether to show command-line usage and exit. **/
    @Option(names = { "-?", "--help" }, description = "show command-line usage and exit", defaultValue = "false")
    private boolean showUsageAndExit = false;

}
