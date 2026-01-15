package com.mockmock.http;

import com.mockmock.htmlbuilder.MailListViewHtmlBuilder;
import com.mockmock.mail.MailQueue;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexHandler extends BaseHandler {

    private final MailListViewHtmlBuilder mailListViewHtmlBuilder = new MailListViewHtmlBuilder();

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
