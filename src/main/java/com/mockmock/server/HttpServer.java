package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.http.*;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Setter
@Slf4j
public class HttpServer implements com.mockmock.server.Server {

    @Autowired
    private IndexHandler indexHandler;

    @Autowired
    private MailDetailHandler mailDetailHandler;

    @Autowired
    private MailDetailHtmlHandler mailDetailHtmlHandler;

    @Autowired
    private MailDeleteHandler mailDeleteHandler;

    @Autowired
    private DeleteHandler deleteHandler;

    @Autowired
    private AttachmentHandler attachmentHandler;

    @Autowired
    private ViewRawMessageHandler viewRawMessageHandler;

    @Autowired
    private ViewHeadersHandler viewHeadersHandler;

    @Autowired
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
			this.indexHandler,
			this.mailDetailHandler,
			this.mailDetailHtmlHandler,
			this.mailDeleteHandler,
			this.deleteHandler,
            this.attachmentHandler,
            this.viewRawMessageHandler,
            this.viewHeadersHandler,
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
