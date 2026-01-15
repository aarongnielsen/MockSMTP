package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.http.*;
import com.mockmock.mail.MailQueue;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class HttpServerTest {

    @Test
    public void start_valid() throws IOException {
        int port = 20000 + new Random().nextInt(10000);
        Settings settings = new Settings();
        settings.setHttpPort(port);

        HttpServer httpServer = createHttpServer(settings);
        httpServer.start();

        try {
            String url = "http://localhost:" + port + "/delete/all";
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.getInputStream().close();
            Assertions.assertEquals(HttpStatus.FOUND_302, urlConnection.getResponseCode());
        } catch (IOException e) {
            httpServer.stop();
            throw e;
        }

    }

    @Test
    public void start_portAlreadyInUse() {
        int port = 20000 + new Random().nextInt(10000);
        Settings settings = new Settings();
        settings.setHttpPort(port);

        HttpServer httpServer1 = createHttpServer(settings);
        httpServer1.start();

        HttpServer httpServer2 = createHttpServer(settings);
        Assertions.assertThrows(RuntimeException.class, httpServer2::start);

        httpServer2.stop();
        httpServer1.stop();
    }

    private HttpServer createHttpServer(Settings settings) {
        HttpServer httpServer = new HttpServer();
        httpServer.setSettings(settings);

        // these tests only actually request /delete/all, so we only need the one handler
        DeleteHandler deleteHandler = new DeleteHandler();
        deleteHandler.setMailQueue(new MailQueue());

        httpServer.setMailDetailHtmlHandler(new ViewMailBodyHandler());
        httpServer.setMailDeleteHandler(new MailDeleteHandler());
        httpServer.setDeleteHandler(deleteHandler);
        httpServer.setAttachmentHandler(new AttachmentHandler());
        httpServer.setViewRawMessageHandler(new ViewRawMessageHandler());
        httpServer.setViewHeadersHandler(new ViewHeadersHandler());
        return httpServer;
    }

}
