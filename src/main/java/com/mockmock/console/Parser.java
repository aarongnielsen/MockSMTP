package com.mockmock.console;


import com.mockmock.Settings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
@Slf4j
public class Parser
{
    /**
     * Parses the given parameters and returns the possible options
     * @param args String[]
     */
    public Settings parseOptions(String[] args, Settings settings) {
        // define the possible options
        Options options = new Options();
        options.addOption("p", true, "The mail port number to use. Default is 25000.");
        options.addOption("h", true, "The http port number to use. Default is 8282.");
        options.addOption("m", true, "The maximum size of the mail qeueue. Default is 1000.");
        options.addOption("ff", true, "Filters out from email addresses (comma separated).");
        options.addOption("ft", true, "Filters out to email addresses (comma separated).");
        options.addOption("demo", false, "Loads demo data at startup");
        options.addOption("?", false, "Shows this help information.");

        // parse the given arguments
        CommandLineParser parser = new PosixParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("?")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "java -jar MockMock.jar -p 25 -h 8282", options );
                System.exit(0);
            }

            parseSmtpPortOption(cmd, settings);
            parseHttpPortOption(cmd, settings);
            parseMailQueueSizeOption(cmd, settings);
			parseFilterFromEmailAddressesOption(cmd, settings);
			parseFilterToEmailAddressesOption(cmd, settings);
            parseDemoDataOption(cmd, settings);
        }
        catch (ParseException parseX) {
            log.error("error parsing command line arguments", parseX);
        }

        return settings;
    }

    protected void parseSmtpPortOption(CommandLine cmd, Settings settings) {
        if (cmd.hasOption("p")) {
            try {
                settings.setSmtpPort(Integer.parseInt(cmd.getOptionValue("p")));
            }
            catch (NumberFormatException nfx) {
                log.error("Invalid mail port {}, using default {}", cmd.getOptionValue("p"), settings.getSmtpPort());
            }
        }
    }

    protected void parseHttpPortOption(CommandLine cmd, Settings settings) {
        if (cmd.hasOption("h")) {
            try {
                settings.setHttpPort(Integer.parseInt(cmd.getOptionValue("h")));
            }
            catch (NumberFormatException nfx) {
                log.error("Invalid HTTP port {}, using default {}", cmd.getOptionValue("h"), settings.getHttpPort());
            }
        }
    }

    protected void parseMailQueueSizeOption(CommandLine cmd, Settings settings) {
        if (cmd.hasOption("m")) {
            try {
                settings.setMaxMailQueueSize(Integer.parseInt(cmd.getOptionValue("m")));
            }
            catch (NumberFormatException nfx) {
                log.error("Invalid max mail queue size {}, using default {}", cmd.getOptionValue("m"), settings.getMaxMailQueueSize());
            }
        }
    }

	protected void parseFilterFromEmailAddressesOption(CommandLine cmd, Settings settings) {
		if (cmd.hasOption("ff")) {
			String input = cmd.getOptionValue("ff");
			String[] emailAddresses = input.split(",");
			settings.setFilterFromEmailAddresses(new HashSet<>(Arrays.asList(emailAddresses)));
		}
	}

	protected void parseFilterToEmailAddressesOption(CommandLine cmd, Settings settings) {
		if (cmd.hasOption("ft")) {
			String input = cmd.getOptionValue("ft");
			String[] emailAddresses = input.split(",");
			settings.setFilterToEmailAddresses(new HashSet<>(Arrays.asList(emailAddresses)));
		}
	}

    protected void parseDemoDataOption(CommandLine cmd, Settings settings) {
        if (cmd.hasOption("demo")) {
            settings.setLoadDemoData(true);
        }
    }

}
