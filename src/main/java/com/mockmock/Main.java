package com.mockmock;

import com.mockmock.server.DemoDataLoader;
import com.mockmock.server.HttpServer;
import com.mockmock.server.SmtpServer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner, ExitCodeGenerator {

    public static final String VERSION_NUMBER = "1.5.0-dev";

    // methods overridden for picoCLI:
    //  - inject Spring bean factory and application settings at construction-time
    //  - parse command line, tracking exit code so that Spring can use it again
    //  - if all is well, start servers

    @NonNull
    private BeanFactory beanFactory;

    @NonNull
    private Settings appSettings;

    @Getter
    private int exitCode;

    @Override
    public void run(String... args) {
        // read command-line parameters into settings bean
        CommandLine commandLine = new CommandLine(appSettings);
        try {
            commandLine.parseArgs(args);

            if (appSettings.isShowUsageAndExit()) {
                commandLine.usage(System.out);
                exitCode = -1;
            }
        } catch (Exception x) {
            System.out.println("Error: " + x.getMessage());
            commandLine.usage(System.out);
            exitCode = 1;
        }

        // start the servers here
        if (exitCode == 0) {
            try {
                beanFactory.getBean(SmtpServer.class).start();
                if (appSettings.isLoadDemoData()) {
                    new DemoDataLoader(appSettings).load();
                }
            } catch (Exception x) {
                exitCode = 2;
            }

            try {
                beanFactory.getBean(HttpServer.class).start();
            } catch (Exception x) {
                exitCode = 3;
            }
        }
    }

    // main method

    public static void main(String[] args) {
        // application will exit if an error occurs during command-line initialisation
        int exitCode = SpringApplication.exit(SpringApplication.run(Main.class, args));
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

}
