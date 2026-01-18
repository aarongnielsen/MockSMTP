package com.mockmock;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** A collection of I/O utilitiy methods. **/
@Slf4j
public class Util {

    /** Returns the contents of an input stream, presumed to be encoded as UTF-8, as a single string. **/
    public static String getStreamContentsAsString(InputStream is) {
        return getStreamContentsAsString(is, StandardCharsets.UTF_8);
    }

    /** Returns the contents of an input stream, encoded in the given character set, as a single string. **/
    public static String getStreamContentsAsString(InputStream is, Charset charset) {
        byte[] streamContentsAsBytes = getStreamContentsAsByteArray(is);
        String streamContentsAsString = new String(streamContentsAsBytes, charset);
        return streamContentsAsString;
    }

    /** Returns the contents of an input stream as a byte array. **/
    public static byte[] getStreamContentsAsByteArray(InputStream is) {
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