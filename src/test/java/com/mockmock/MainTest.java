package com.mockmock;

import com.mockmock.server.HttpServer;
import com.mockmock.server.SmtpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

public class MainTest {

    @Test
    public void run_defaultOptions() {
        Main mainApplication = new Main();
        int exitCode = mainApplication.run();
        mainApplication.stopServers();
        Assertions.assertEquals(Main.ExitCodes.STARTUP_OK, exitCode);
    }

    @Test
    public void run_invalidOptions() {
        Main mainApplication = new Main();
        int exitCode = mainApplication.run("--invalid-option");
        mainApplication.stopServers();
        Assertions.assertEquals(Main.ExitCodes.CANNOT_PARSE_COMMAND_LINE, exitCode);
    }

    @Test
    public void run_showUsageAndExit() {
        Main mainApplication = new Main();
        int exitCode = mainApplication.run("-?");
        mainApplication.stopServers();
        Assertions.assertEquals(Main.ExitCodes.EXIT_AFTER_SHOW_USAGE, exitCode);
    }

    @Test
    public void run_smtpServerCannotStart() {
        try (MockedConstruction<SmtpServer> mockedConstruction = Mockito.mockConstruction(SmtpServer.class, (mockObject, context) -> {
            throw new RuntimeException("constructor failed");
        })) {
            Main mainApplication = new Main();
            int exitCode = mainApplication.run();
            mainApplication.stopServers();
            Assertions.assertEquals(Main.ExitCodes.CANNOT_START_SMTP_SERVER, exitCode);
        }
    }

    @Test
    public void run_httpServerCannotStart() {
        try (MockedConstruction<HttpServer> mockedConstruction = Mockito.mockConstruction(HttpServer.class, (mockObject, context) -> {
            throw new RuntimeException("constructor failed");
        })) {
            Main mainApplication = new Main();
            int exitCode = mainApplication.run();
            mainApplication.stopServers();
            Assertions.assertEquals(Main.ExitCodes.CANNOT_START_HTTP_SERVER, exitCode);
        }
    }

    @Test
    public void run_loadDemoData() {
        Main mainApplication = new Main();
        int exitCode = mainApplication.run("--demo");
        mainApplication.stopServers();
        Assertions.assertEquals(Main.ExitCodes.STARTUP_OK, exitCode);
        Assertions.assertEquals(3, mainApplication.getMailQueue().size());
    }

}
