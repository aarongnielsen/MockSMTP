package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class DeleteHandler extends BaseHandler
{
    private MailQueue mailQueue;

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if(!target.equals("/mail/delete/all")) {
            return;
        }

        // empty the mail queue
        this.mailQueue.emptyQueue();

        response.setHeader(HttpHeaders.LOCATION, "/");
        response.setStatus(HttpStatus.FOUND.value());
        response.getWriter().println("Mail queue has been emptied");
        request.setHandled(true);
    }

    @Autowired
    public void setMailQueue(MailQueue mailQueue) {
        this.mailQueue = mailQueue;
    }
}
