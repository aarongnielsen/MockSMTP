package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
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
    public void _handleTestImpl(String targetUrlPath, boolean isMessageInQueue, boolean expectedHandled) throws IOException, ServletException {
        // build HTTP handler
        Request jettyRequest = new Request(null, null);
        Response jettyResponse = Mockito.mock(Response.class);
        StringWriter stringWriter = new StringWriter();
        Mockito.doReturn(new PrintWriter(stringWriter)).when(jettyResponse).getWriter();

        MailQueue mailQueue = new MailQueue();
        if (isMessageInQueue) {
            mailQueue.add(new MockMail());
            mailQueue.add(new MockMail());
        }
        IndexHandler indexHandler = new IndexHandler(mailQueue);
        indexHandler.handle(targetUrlPath, jettyRequest, jettyRequest, jettyResponse);

        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());

            String expectedBodyHtml = "<h1>" + (isMessageInQueue ? "You have 2 emails!" : "No emails in queue");
            Assertions.assertTrue(stringWriter.toString().contains(expectedBodyHtml));
            Mockito.verify(jettyResponse, Mockito.times(1)).setStatus(Mockito.eq(HttpStatus.OK_200));
        }
    }

    private static Stream<Arguments> handleTestArguments() {
        return Stream.of(
                Arguments.of("/incorrect/path", false, false),
                Arguments.of("/", false, true),
                Arguments.of("/", true, true)
        );
    }

}
