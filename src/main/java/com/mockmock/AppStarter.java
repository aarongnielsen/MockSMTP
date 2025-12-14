package com.mockmock;

import com.mockmock.server.DemoDataLoader;
import com.mockmock.server.Server;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import picocli.CommandLine;

public class AppStarter
{
    public static final float VERSION_NUMBER = 1.4f;

    public static void main(String[] args)
    {
        BeanFactory factory = new ClassPathXmlApplicationContext("META-INF/beans.xml");

        // read command-line parameters into settings bean
        Settings settings = (Settings) factory.getBean("settings");
        CommandLine commandLine = new CommandLine(settings);
        try {
            commandLine.parseArgs(args);

            if (settings.isShowUsageAndExit()) {
                commandLine.usage(System.out);
                System.exit(0);
            }
        } catch (Exception x) {
            System.err.println("Error: " + x.getMessage());
            commandLine.usage(System.out);
            System.exit(1);
        }

        // start SMTP server
        Server smtpServer = (Server) factory.getBean("smtpServer");
        smtpServer.setSettings(settings);
        smtpServer.start();

        // load demo data if requested
        if (settings.isLoadDemoData()) {
            new DemoDataLoader().load();
        }

        // start HTTP server for front end
        Server httpServer = (Server) factory.getBean("httpServer");
        httpServer.setSettings(settings);
        httpServer.start();
    }
}