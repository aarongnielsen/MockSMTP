package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.Util;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class DemoDataLoader {

    public void load() {
        // build email message to send
        final String resourcePath = "/demodata/";
        Util util = new Util();
        for (int i = 1; i <= 3; i++) {
            // build a message to send:
            StringBuilder stringBuilder = new StringBuilder();

            //  - SMTP handshake
            stringBuilder.append("HELO example.com").append("\r\n")
                    .append("MAIL FROM: alice@example.com").append("\r\n")
                    .append("RCPT TO: bob@example.com").append("\r\n")
                    .append("DATA\r\n");

            //  - read mail message contents
            String resourceFile = resourcePath + "email-message" + i + ".txt";
            String emailMessage = util.getResourceContentsAsString(resourceFile);
            stringBuilder.append(emailMessage);

            //  - close SMTP session
            stringBuilder.append("\r\n")
                    .append(".").append("\r\n")
                    .append("\r\n")
                    .append("QUIT").append("\r\n");

            // send the email
            try (Socket socket = new Socket("localhost", 25)) {
                BufferedOutputStream socketOutputStream = new BufferedOutputStream(socket.getOutputStream());
                socketOutputStream.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
                socketOutputStream.close();
            } catch (IOException iox) {
                log.error("error loading demo email messages", iox);
            }
        }

        log.info("demo data loaded");
    }

}
