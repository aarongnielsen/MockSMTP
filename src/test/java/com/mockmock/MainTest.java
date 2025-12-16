package com.mockmock;

import com.mockmock.server.HttpServer;
import com.mockmock.server.SmtpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;

public class MainTest {

    @Test
    public void run_defaultOptions() {
        BeanFactory mockBeanFactory = Mockito.mock(BeanFactory.class);
        Mockito.when(mockBeanFactory.getBean(Mockito.eq(SmtpServer.class))).thenReturn(Mockito.mock(SmtpServer.class));
        Mockito.when(mockBeanFactory.getBean(Mockito.eq(HttpServer.class))).thenReturn(Mockito.mock(HttpServer.class));
        Main mainApplication = new Main(mockBeanFactory, new Settings());
        mainApplication.run();
        Assertions.assertEquals(0, mainApplication.getExitCode());
    }

    @Test
    public void run_invalidOptions() {
        Main mainApplication = new Main(Mockito.mock(BeanFactory.class), new Settings());
        mainApplication.run("--invalid-option");
        Assertions.assertEquals(1, mainApplication.getExitCode());
    }

    @Test
    public void run_showUsageAndExit() {
        Main mainApplication = new Main(Mockito.mock(BeanFactory.class), new Settings());
        mainApplication.run("-?");
        Assertions.assertEquals(-1, mainApplication.getExitCode());
    }

    @Test
    public void run_smtpServerCannotStart() {
        BeanFactory mockBeanFactory = Mockito.mock(BeanFactory.class);
        Main mainApplication = new Main(mockBeanFactory, new Settings());
        mainApplication.run();
        Assertions.assertEquals(2, mainApplication.getExitCode());
    }

    @Test
    public void run_httpServerCannotStart() {
        BeanFactory mockBeanFactory = Mockito.mock(BeanFactory.class);
        Mockito.when(mockBeanFactory.getBean(Mockito.eq(SmtpServer.class))).thenReturn(Mockito.mock(SmtpServer.class));
        Main mainApplication = new Main(mockBeanFactory, new Settings());
        mainApplication.run();
        Assertions.assertEquals(3, mainApplication.getExitCode());
    }

}
