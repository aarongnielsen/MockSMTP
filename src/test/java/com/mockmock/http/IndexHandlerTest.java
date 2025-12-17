package com.mockmock.http;

import com.mockmock.htmlbuilder.FooterHtmlBuilder;
import com.mockmock.htmlbuilder.HeaderHtmlBuilder;
import com.mockmock.htmlbuilder.MailListHtmlBuilder;
import com.mockmock.mail.MailQueue;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

public class IndexHandlerTest {

    @ParameterizedTest
    @MethodSource("handleTestArguments")
    public void _handleTestImpl(String targetUrlPath, boolean expectedHandled) throws IOException, ServletException {
        // mock header/body/footer HTML builders
        HeaderHtmlBuilder headerHtmlBuilder = Mockito.mock(HeaderHtmlBuilder.class);
        MailListHtmlBuilder mailListHtmlBuilder = Mockito.mock(MailListHtmlBuilder.class);
        FooterHtmlBuilder footerHtmlBuilder = Mockito.mock(FooterHtmlBuilder.class);
        Mockito.doReturn(HeaderHtmlBuilder.class.getName() + " ").when(headerHtmlBuilder).build();
        Mockito.doReturn(MailListHtmlBuilder.class.getName() + " ").when(mailListHtmlBuilder).build();
        Mockito.doReturn(FooterHtmlBuilder.class.getName()).when(footerHtmlBuilder).build();
        String expectedBodyHtml = HeaderHtmlBuilder.class.getName() + " " +
                MailListHtmlBuilder.class.getName() + " " +
                FooterHtmlBuilder.class.getName();

        // build HTTP handler
        Request jettyRequest = new Request(null, null);
        Response jettyResponse = Mockito.mock(Response.class);
        StringWriter stringWriter = new StringWriter();
        Mockito.doReturn(new PrintWriter(stringWriter)).when(jettyResponse).getWriter();
        IndexHandler indexHandler = new IndexHandler();
        indexHandler.setHeaderHtmlBuilder(headerHtmlBuilder);
        indexHandler.setMailListHtmlBuilder(mailListHtmlBuilder);
        indexHandler.setFooterHtmlBuilder(footerHtmlBuilder);
        indexHandler.setMailQueue(Mockito.mock(MailQueue.class));

        indexHandler.handle(targetUrlPath, jettyRequest, jettyRequest, jettyResponse);

        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Assertions.assertEquals(expectedBodyHtml, stringWriter.toString());
            Mockito.verify(jettyResponse, Mockito.times(1)).setStatus(Mockito.eq(HttpStatus.OK_200));
        }
    }

    private static Stream<Arguments> handleTestArguments() {
        return Stream.of(
                Arguments.of("/incorrect/path", false),
                Arguments.of("/", true)
        );
    }

}
