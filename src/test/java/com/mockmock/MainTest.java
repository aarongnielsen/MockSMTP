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
        Main mainApplication = new Main(new Settings());
        mainApplication.run();
        mainApplication.stopServers();
        Assertions.assertEquals(0, mainApplication.getExitCode());
    }

    @Test
    public void run_invalidOptions() {
        Main mainApplication = new Main(new Settings());
        mainApplication.run("--invalid-option");
        mainApplication.stopServers();
        Assertions.assertEquals(1, mainApplication.getExitCode());
    }

    @Test
    public void run_showUsageAndExit() {
        Main mainApplication = new Main(new Settings());
        mainApplication.run("-?");
        mainApplication.stopServers();
        Assertions.assertEquals(-1, mainApplication.getExitCode());
    }

    @Test
    public void run_smtpServerCannotStart() {
        try (MockedConstruction<SmtpServer> mockedConstruction = Mockito.mockConstruction(SmtpServer.class, (mockObject, context) -> {
            throw new RuntimeException("constructor failed");
        })) {
            Main mainApplication = new Main(new Settings());
            mainApplication.run();
            mainApplication.stopServers();
            Assertions.assertEquals(2, mainApplication.getExitCode());
        };
    }

    @Test
    public void run_httpServerCannotStart() {
        try (MockedConstruction<HttpServer> mockedConstruction = Mockito.mockConstruction(HttpServer.class, (mockObject, context) -> {
            throw new RuntimeException("constructor failed");
        })) {
            Main mainApplication = new Main(new Settings());
            mainApplication.run();
            mainApplication.stopServers();
            Assertions.assertEquals(3, mainApplication.getExitCode());
        };
    }

}
