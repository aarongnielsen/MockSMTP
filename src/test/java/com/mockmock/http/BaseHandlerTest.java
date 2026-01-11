package com.mockmock.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BaseHandlerTest {

    @Test
    public void getRegexMatchedGroup_valid() {
        BaseHandler mockBaseHandler = Mockito.mock(BaseHandler.class);
        Mockito.doReturn("/message/(\\d+)/part/(\\d+)/info").when(mockBaseHandler).getUrlPathPattern();
        Mockito.doCallRealMethod().when(mockBaseHandler).getRegexMatchedGroup(Mockito.anyString(), Mockito.anyInt());

        String urlPath = "/message/123/part/456/info";
        Assertions.assertEquals(123, mockBaseHandler.getRegexMatchedGroup(urlPath, 1));
        Assertions.assertEquals(456, mockBaseHandler.getRegexMatchedGroup(urlPath, 2));
    }

    @Test
    public void getRegexMatchedGroup_IfMatchNotFoundReturnZero() {
        BaseHandler mockBaseHandler = Mockito.mock(BaseHandler.class);
        Mockito.doReturn("/message/(\\d+)/part/(\\d+)/info").when(mockBaseHandler).getUrlPathPattern();
        Mockito.doCallRealMethod().when(mockBaseHandler).getRegexMatchedGroup(Mockito.anyString(), Mockito.anyInt());

        String urlPath = "/message/abc/part/def/info";
        Assertions.assertEquals(0, mockBaseHandler.getRegexMatchedGroup(urlPath, 1));
        Assertions.assertEquals(0, mockBaseHandler.getRegexMatchedGroup(urlPath, 2));
    }

    @Test
    public void getRegexMatchedGroup_IfNumberFormatExceptionReturnZero() {
        BaseHandler mockBaseHandler = Mockito.mock(BaseHandler.class);
        Mockito.doReturn("/message/(\\d+)/part/(\\d+)/info").when(mockBaseHandler).getUrlPathPattern();
        Mockito.doCallRealMethod().when(mockBaseHandler).getRegexMatchedGroup(Mockito.anyString(), Mockito.anyInt());

        String urlPath = "/message/123/part/456789012345678901234567890/info";
        Assertions.assertEquals(123, mockBaseHandler.getRegexMatchedGroup(urlPath, 1));
        Assertions.assertEquals(0, mockBaseHandler.getRegexMatchedGroup(urlPath, 2));
    }

}
