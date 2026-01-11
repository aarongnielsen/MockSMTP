package com.mockmock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

public class UtilTest {

    @Test
    public void getStreamContentsAsByteArray_errorReturnsOutputStreamContents() throws IOException {
        int bytesToWriteSuccessfully = 5;
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bytesToWriteSuccessfully)
                .thenThrow(new IOException());
        byte[] byteArray = Util.getStreamContentsAsByteArray(inputStream);
        Assertions.assertEquals(bytesToWriteSuccessfully, byteArray.length);
    }

}
