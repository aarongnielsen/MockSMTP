package com.mockmock.http;

import com.mockmock.htmlbuilder.MailListViewHtmlBuilder;
import com.mockmock.mail.MailQueue;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP handler used to display the list of messages received, the default view of the application.
 * <p>
 * The user invokes this handler by requesting the URL {@code /}.
 */
public class IndexHandler extends BaseHandler {

    // instance fields

    private final MailListViewHtmlBuilder mailListViewHtmlBuilder = new MailListViewHtmlBuilder();

    // constructors

    public IndexHandler(MailQueue mailQueue) {
        setMailQueue(mailQueue);
    }

    // methods implemented for BaseHandler

    @Override
    protected String getUrlPathPattern() {
        return "^/$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if(!isUrlPathMatch(target)) {
            return;
        }

        setDefaultResponseOptions(response);
        String mailListHTML = mailListViewHtmlBuilder.buildMailListView(mailQueue);
        response.getWriter().print(mailListHTML);
        request.setHandled(true);
    }

}
