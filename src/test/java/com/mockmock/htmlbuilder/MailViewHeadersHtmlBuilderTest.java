package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MailViewHeadersHtmlBuilderTest {

    @Test
    public void build_nullMessageProducesEmptyString() {
        MockMail mockMail = null;

        MailViewHeadersHtmlBuilder headersHtmlBuilder = new MailViewHeadersHtmlBuilder();
        headersHtmlBuilder.setMockMail(mockMail);
        Assertions.assertTrue(headersHtmlBuilder.build().isEmpty());
    }

    @Test
    public void build_messageWithNoHeadersProducesEmptyHtmlTag() throws MessagingException {
        MockMail mockMail = Mockito.mock(MockMail.class);
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doReturn(mimeMessage).when(mockMail).getMimeMessage();
        Mockito.doReturn(Collections.emptyEnumeration()).when(mimeMessage).getAllHeaderLines();

        MailViewHeadersHtmlBuilder headersHtmlBuilder = new MailViewHeadersHtmlBuilder();
        headersHtmlBuilder.setMockMail(mockMail);
        Assertions.assertTrue(headersHtmlBuilder.build().contains("<pre>\n</pre>"));
    }

    @Test
    public void build_messagesHeadersArePrintedWithTrailingBrElement() throws MessagingException {
        MockMail mockMail = Mockito.mock(MockMail.class);
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doReturn(mimeMessage).when(mockMail).getMimeMessage();
        List<String> headers = Arrays.asList("FooKey: FooValue", "BarKey: BarValue");
        Mockito.doReturn(Collections.enumeration(headers)).when(mimeMessage).getAllHeaderLines();

        MailViewHeadersHtmlBuilder headersHtmlBuilder = new MailViewHeadersHtmlBuilder();
        headersHtmlBuilder.setMockMail(mockMail);
        Assertions.assertTrue(headersHtmlBuilder.build().contains(
                "<pre>\n" + headers.stream().map(s -> s + "<br/>").collect(Collectors.joining()) + "</pre>")
        );
    }

    @Test
    public void build_exceptionInterruptsOutput() throws MessagingException {
        MockMail mockMail = Mockito.mock(MockMail.class);
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doReturn(mimeMessage).when(mockMail).getMimeMessage();
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getAllHeaderLines();

        MailViewHeadersHtmlBuilder headersHtmlBuilder = new MailViewHeadersHtmlBuilder();
        headersHtmlBuilder.setMockMail(mockMail);
        Assertions.assertTrue(headersHtmlBuilder.build().endsWith("<pre>\n"));
    }

}
