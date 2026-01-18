package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A helper class to load demo data into the application at start-up time,
 * if specified in the {@link com.mockmock.Settings#setLoadDemoData(boolean) command-line options}.
 * <p>
 * The data is read from the {@code demodata/} folder in classpath resources.
 * Note that demo data is specified as entire email messages in individual files,
 * and each one is loaded by connecting to the SMTP server directly.
 */
@RequiredArgsConstructor
@Slf4j
public class DemoDataLoader {

    // instance fields

    /** The configuration of the application, used to locate the SMTP server. **/
    @NonNull
    private Settings settings;

    // public methods

    /** Loads the demo data from classpath resources. **/
    public void load() {
        // read email messages from classpath resources (src/main/resources)
        final String resourceBasePath = "/demodata/";

        for (int i = 1; i <= 3; i++) {
            // build a message to send:
            StringBuilder stringBuilder = new StringBuilder();

            //  - SMTP handshake
            stringBuilder.append("HELO example.com").append("\r\n")
                    .append("MAIL FROM: demodata@example.com").append("\r\n")
                    .append("RCPT TO: demorecipient@example.com").append("\r\n")
                    .append("DATA\r\n");

            //  - read mail message contents
            String resourceFile = resourceBasePath + "email-message" + i + ".txt";
            String emailMessage = Util.getStreamContentsAsString(getClass().getResourceAsStream(resourceFile));
            stringBuilder.append(emailMessage);

            //  - close SMTP session
            stringBuilder.append("\r\n")
                    .append(".").append("\r\n")
                    .append("\r\n")
                    .append("QUIT").append("\r\n");

            // send the email
            try (Socket socket = new Socket("localhost", settings.getSmtpPort())) {
                BufferedOutputStream socketOutputStream = new BufferedOutputStream(socket.getOutputStream());
                socketOutputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
                socketOutputStream.close();
            } catch (IOException iox) {
                log.error("error loading demo email messages", iox);
            }

            // space the loads apart so the messages don't get out of order
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) { }
        }

        log.info("demo data loaded");
    }

}
