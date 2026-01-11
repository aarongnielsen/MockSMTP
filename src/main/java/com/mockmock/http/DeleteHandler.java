package com.mockmock.http;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class DeleteHandler extends BaseHandler  {

    @Override
    protected String getUrlPathPattern() {
        return "^/delete/all$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if (!isUrlPathMatch(target)) {
            return;
        }

        // empty the mail queue
        this.mailQueue.emptyQueue();

        response.setHeader(HttpHeader.LOCATION.asString(), "/");
        response.setStatus(HttpStatus.FOUND_302);
        response.getWriter().println("Mail queue has been emptied");
        request.setHandled(true);
    }

}
