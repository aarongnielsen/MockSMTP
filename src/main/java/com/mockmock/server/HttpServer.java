package com.mockmock.server;

import com.mockmock.AppStarter;
import com.mockmock.Settings;
import com.mockmock.http.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class HttpServer implements com.mockmock.server.Server {

    @Setter
    private int port;

    private Settings settings;
    private IndexHandler indexHandler;
    private MailDetailHandler mailDetailHandler;
    private MailDetailHtmlHandler mailDetailHtmlHandler;
    private MailDeleteHandler mailDeleteHandler;
    private DeleteHandler deleteHandler;
    private AttachmentHandler attachmentHandler;

    @Autowired
    @Setter
    private ViewRawMessageHandler viewRawMessageHandler;

    @Autowired
    @Setter
    private ViewHeadersHandler viewHeadersHandler;

    public void start() {
        Server http = new Server(port);

        // get the path to the "static" folder. If it doesn't exists, check if it's in the folder of the file being executed.
        String pathToStaticResources = (settings.getStaticFolderPath() != null ? settings.getStaticFolderPath() : "./static");
        if (!new File(pathToStaticResources).exists()) {
            log.info("Path to static folder does not exist: {}", pathToStaticResources);

            // check inside the directory we're in
            pathToStaticResources = AppStarter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            pathToStaticResources = new File(pathToStaticResources).getParent() + "/static";
        }

        log.info("Path to static resources: {}", pathToStaticResources);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(pathToStaticResources);

        Handler[] handlers = {
			this.indexHandler,
			this.mailDetailHandler,
			this.mailDetailHtmlHandler,
			this.mailDeleteHandler,
			this.deleteHandler,
            this.attachmentHandler,
            this.viewRawMessageHandler,
            this.viewHeadersHandler,
			resourceHandler
        };
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(handlers);
        http.setHandler(handlerList);

        try {
            log.info("Starting http server on port {}", port);
            http.start();
            http.join();
        } catch (Exception x) {
            log.error("Could not start http server. Maybe port {} is already in use?", port);
            log.debug("HTTP server startup error stacktrace:", x);
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

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
