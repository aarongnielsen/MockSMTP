package com.mockmock.http;

import com.mockmock.htmlbuilder.FooterHtmlBuilder;
import com.mockmock.htmlbuilder.HeaderHtmlBuilder;
import com.mockmock.htmlbuilder.MailListHtmlBuilder;
import lombok.Setter;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Setter
public class IndexHandler extends BaseHandler {

    @Autowired
    private HeaderHtmlBuilder headerHtmlBuilder;

    @Autowired
    private FooterHtmlBuilder footerHtmlBuilder;

    @Autowired
    private MailListHtmlBuilder mailListHtmlBuilder;

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

        String header = headerHtmlBuilder.build();

        mailListHtmlBuilder.setMailQueue(mailQueue.getMailQueue());
        String body = mailListHtmlBuilder.build();

        String footer = footerHtmlBuilder.build();

        response.getWriter().print(header + body + footer);

        request.setHandled(true);
    }

}
