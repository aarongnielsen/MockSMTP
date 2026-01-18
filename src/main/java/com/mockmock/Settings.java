package com.mockmock;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.HashSet;
import java.util.Set;

/**
 * A bean containing the settings for the application, as specified at the command-line.
 * <p>
 * The available command line options are:
 * <table border="1">
 *   <thead>
 *     <tr>
 *       <th>Option</th>
 *       <th>Description</th>
 *       <th>Default Value</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>-p</code> <em>port</em> <br> <code>--smtp-port-</code><em>port</em></td>
 *       <td>Sets the SMTP server port</td>
 *       <td>25</td>
 *     </tr>
 *     <tr>
 *       <td><code>-h</code> <em>port</em> <br> <code>--http-port=</code><em>port</em></td>
 *       <td>Sets the HTTP server port</td>
 *       <td>8282</td>
 *     </tr>
 *     <tr>
 *       <td><code>-m</code> <em>size</em> <br> <code>--max-queue-size=</code><em>size</em></td>
 *       <td>Limits the number of messages to be displayed</td>
 *       <td>0 (unlimited)</td>
 *     </tr>
 *     <tr>
 *       <td><code>-ff</code> <em>addresses</em> <br> <code>--filter-from=</code><em>addresses</em></td>
 *       <td>Discards messages sent from certain addresses (single comma-separated string)</td>
 *       <td>(empty, accept all messages)</td>
 *     </tr>
 *     <tr>
 *       <td><code>-ft</code> <em>addresses</em> <br> <code>--filter-to=</code><em>addresses</em></td>
 *       <td>Discards messages sent to certain addresses (single comma-separated string)</td>
 *       <td>(empty, accept all messages)</td>
 *     </tr>
 *     <tr>
 *       <td><code>--demo</code></td>
 *       <td>Starts the application with demo data loaded</td>
 *       <td>false</td>
 *     </tr>
 *     <tr>
 *       <td><code>-?</code> <br> <code>--help</code></td>
 *       <td>Shows command-line usage and exits</td>
 *       <td>false</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
@Command(name = "MockSMTP", description = "MockSMTP is a mock server for testing outgoing emails.")
@Getter
@Setter
public class Settings {

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
