package com.mockmock;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockSmtpMessageHandlerFactory;
import com.mockmock.server.DemoDataLoader;
import com.mockmock.server.HttpServer;
import com.mockmock.server.SmtpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

/**
 * The entry point to the application.
 */
@Getter
@Slf4j
public class Main {

    // constants

    /** The current version number of the application. **/
    public static final String VERSION_NUMBER = "1.6.1";

    // constants - exit codes

    /**
     * The exit codes available when running the application from the command line.
     * If the {@link Main#run(String...)} method returns any value other than {@link ExitCodes#STARTUP_OK zero},
     * the application will exit.
     */
    public static class ExitCodes {
        public static final int STARTUP_OK = 0;
        public static final int EXIT_AFTER_SHOW_USAGE = -1;
        public static final int CANNOT_PARSE_COMMAND_LINE = 1;
        public static final int CANNOT_START_SMTP_SERVER = 2;
        public static final int CANNOT_START_HTTP_SERVER = 3;
    }

    // instance fields

    /** The application's settings, read from the command line. **/
    private Settings settings;

    /** The mail queue instance, shared across the various servers. **/
    private MailQueue mailQueue;

    /** The SMTP server used to receive outgoing emails from other applications. **/
    private SmtpServer smtpServer;

    /** The HTTP server used to create the web user interface. **/
    private HttpServer httpServer;

    // public methods

    /**
     * Runs the application, creating a configuration from the given command-line arguments.
     *
     * @param args command-line arguments.
     * @return a value from {@link ExitCodes}.
     */
    public int run(String... args) {
        log.info("MockSMTP " + VERSION_NUMBER + " is starting...");

        // read command-line parameters into settings bean
        settings = new Settings();
        CommandLine commandLine = new CommandLine(settings);
        try {
            commandLine.parseArgs(args);

            if (settings.isShowUsageAndExit()) {
                commandLine.usage(System.out);
                return ExitCodes.EXIT_AFTER_SHOW_USAGE;
            }
        } catch (Exception x) {
            System.out.println("Error: " + x.getMessage());
            commandLine.usage(System.out);
            return ExitCodes.CANNOT_PARSE_COMMAND_LINE;
        }

        // create a queue that will be common to all servers
        mailQueue = new MailQueue();

        // start the servers here
        try {
            smtpServer = new SmtpServer();
            smtpServer.setSettings(settings);
            smtpServer.setHandlerFactory(new MockSmtpMessageHandlerFactory(mailQueue, settings));
            smtpServer.start();
            if (settings.isLoadDemoData()) {
                new DemoDataLoader(settings).load();
            }
        } catch (Exception x) {
            return ExitCodes.CANNOT_START_SMTP_SERVER;
        }

        try {
            httpServer = new HttpServer();
            httpServer.setSettings(settings);
            httpServer.setMailQueue(mailQueue);
            httpServer.start();
        } catch (Exception x) {
            return ExitCodes.CANNOT_START_HTTP_SERVER;
        }

        log.info("MockSMTP " + VERSION_NUMBER + " is started");
        return ExitCodes.STARTUP_OK;
    }

    /** Waits for the server threads to be terminated. **/
    private void joinThread() {
        // only join this thread, as the HTTP server is started last
        httpServer.joinThread();
    }

    /** Stops the SMTP and HTTP servers. **/
    void stopServers() {
        // this is mainly called by unit tests and shouldn't be necessary for normal operation
        if (httpServer != null) {
            httpServer.stop();
        }
        if (smtpServer != null) {
            smtpServer.stop();
        }
    }

    // main method

    /**
     * The entry point to the application.
     * This method creates a new application object, calls its {@link #run(String...)} method,
     * and joins the server threads.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        // application will exit if an error occurs during command-line initialisation
        Main app = new Main();
        int exitCode = app.run(args);
        if (exitCode != ExitCodes.STARTUP_OK) {
            app.stopServers();
            System.exit(exitCode);
        }
        app.joinThread();
    }

}
