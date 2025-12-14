package com.mockmock;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Util {

    public String getStreamContentsAsString(InputStream is) {
        return getStreamContentsAsString(is, StandardCharsets.UTF_8);
    }

    public String getStreamContentsAsString(InputStream is, Charset charset) {
        byte[] streamContentsAsBytes = getStreamContentsAsByteArray(is);
        String streamContentsAsString = new String(streamContentsAsBytes, charset);
        return streamContentsAsString;
    }

    public byte[] getStreamContentsAsByteArray(InputStream is) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 32];
        int bytesRead;
        try {
            while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byteArrayOutputStream.flush();
        } catch (IOException iox) {
            log.error("error reading stream contents into array", iox);
        }
        return byteArrayOutputStream.toByteArray();
    }

}