package com.mockmock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Getter
@Setter
public class Settings {
    /**
     * Whether to show console output when receiving email.
     */
    private boolean showEmailInConsole = false;

    /**
     * The default port where MockMock will run on
     */
    private int smtpPort = 25;

    /**
     * The default port for the http server
     */
    private int httpPort = 8282;

    /**
     * The maximum size the mail queue may be
     */
    private int maxMailQueueSize = 1000;

	/**
	 * A set of "From" email addresses to filter
	 */
    private Set<String> filterFromEmailAddresses = new HashSet<>();

	/**
	 * A set of "To" email addresses to filter
	 */
    private Set<String> filterToEmailAddresses = new HashSet<>();

    /**
     * Path to the static folder containing the images, css and js
     */
    private String staticFolderPath;

}
