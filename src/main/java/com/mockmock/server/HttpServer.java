package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.http.*;
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
@Slf4j
public class HttpServer implements com.mockmock.server.Server {

    private IndexHandler indexHandler;
    private MailDetailHandler mailDetailHandler;
    private MailDetailHtmlHandler mailDetailHtmlHandler;
    private MailDeleteHandler mailDeleteHandler;
    private DeleteHandler deleteHandler;
    private AttachmentHandler attachmentHandler;

    @Autowired
    @Setter
    private Settings settings;

    @Autowired
    @Setter
    private ViewRawMessageHandler viewRawMessageHandler;

    @Autowired
    @Setter
    private ViewHeadersHandler viewHeadersHandler;

    @Override
    public void start() {
        Server http = new Server(settings.getHttpPort());

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
        http.setHandler(handlerList);

        try {
            http.start();
            log.info("Starting HTTP server on http://localhost:{}", settings.getHttpPort());
        } catch (Exception x) {
            log.error("Could not start HTTP server. Maybe port {} is already in use? {}", settings.getHttpPort(), x.getMessage());
            log.debug("Stacktrace:", x);
        }
    }

    @Autowired
    public void setIndexHandler(IndexHandler indexHandler) {
        this.indexHandler = indexHandler;
    }

	@Autowired
	public void setMailDetailHandler(MailDetailHandler mailDetailHandler) {
		this.mailDetailHandler = mailDetailHandler;
	}

	@Autowired
	public void setMailDetailHtmlHandler(MailDetailHtmlHandler mailDetailHtmlHandler) {
		this.mailDetailHtmlHandler = mailDetailHtmlHandler;
	}

	@Autowired
	public void setMailDeleteHandler(MailDeleteHandler mailDeleteHandler) {
		this.mailDeleteHandler = mailDeleteHandler;
	}

    @Autowired
    public void setDeleteHandler(DeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    @Autowired
    public void setAttachmentHandler(AttachmentHandler attachmentHandler) {
        this.attachmentHandler = attachmentHandler;
    }

}
