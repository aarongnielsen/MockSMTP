package com.mockmock;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class Util {

  	public String getResourceContentsAsString(String fileName) {
        InputStream resourceIS = Objects.requireNonNull(getClass().getResourceAsStream(fileName));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceIS, StandardCharsets.UTF_8));
        String contents = bufferedReader.lines().collect(Collectors.joining("\n"));
        return contents;
      }

    public byte[] getStreamContentsAsByteArray(InputStream is) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 32];
        int bytesRead = 0;
        try {
            while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byteArrayOutputStream.flush();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

}