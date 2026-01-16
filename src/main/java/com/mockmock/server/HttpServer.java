package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.http.*;
import com.mockmock.mail.MailQueue;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

@Slf4j
public class HttpServer implements com.mockmock.server.Server {

    @Setter
    private MailQueue mailQueue;

    @Setter
    private Settings settings;

    @Setter(AccessLevel.NONE)
    private Server httpServerImpl;

    @Override
    public void start() {
        httpServerImpl = new Server(settings.getHttpPort());

        // set up the folder of web-facing static files (e.g. images, styles, scripts)
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("/web-static"));
        ContextHandler contextHandler = new ContextHandler("/web-static");
        contextHandler.setHandler(resourceHandler);

        Handler[] handlers = {
            new IndexHandler(mailQueue),
            new MailDetailHandler(mailQueue),
            new MailDeleteHandler(mailQueue),
            new DeleteHandler(mailQueue),
            new AttachmentHandler(mailQueue),
            new ViewMailBodyHandler(mailQueue),
            new ViewHeadersHandler(mailQueue),
            new ViewRawMessageHandler(mailQueue),
            contextHandler
        };
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(handlers);
        httpServerImpl.setHandler(handlerList);

        try {
            httpServerImpl.start();
            log.info("Starting HTTP server on http://localhost:{}", settings.getHttpPort());
        } catch (Exception x) {
            log.error("Could not start HTTP server. Maybe port {} is already in use? {}", settings.getHttpPort(), x.toString());
            log.debug("Stacktrace:", x);
            throw new RuntimeException(x);
        }
    }

    @Override
    public void stop() {
        try {
            httpServerImpl.stop();
        } catch (Exception x) {
            log.error("error stopping HTTP server", x);
        }
    }

}
