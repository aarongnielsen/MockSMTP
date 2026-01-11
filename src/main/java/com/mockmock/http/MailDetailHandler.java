package com.mockmock.http;

import com.mockmock.htmlbuilder.FooterHtmlBuilder;
import com.mockmock.htmlbuilder.HeaderHtmlBuilder;
import com.mockmock.htmlbuilder.MailViewHtmlBuilder;
import com.mockmock.mail.MockMail;
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
public class MailDetailHandler extends BaseHandler {

    @Autowired
    private HeaderHtmlBuilder headerHtmlBuilder;

    @Autowired
    private FooterHtmlBuilder footerHtmlBuilder;

    @Autowired
    private MailViewHtmlBuilder mailViewHtmlBuilder;

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if (!isUrlPathMatch(target)) {
            return;
        }

        int mailIndex = getMailIndex(target);
        if (mailIndex == 0) {
            return;
        }

        MockMail mockMail = this.mailQueue.getByIndex(mailIndex);
        if (mockMail == null) {
            return;
        }

        setDefaultResponseOptions(response);

        String header = headerHtmlBuilder.build();

        mailViewHtmlBuilder.setMockMail(mockMail);
        mailViewHtmlBuilder.setMailIndex(mailIndex);
        String body = mailViewHtmlBuilder.build();

        String footer = footerHtmlBuilder.build();

        response.getWriter().print(header + body + footer);

        request.setHandled(true);
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
