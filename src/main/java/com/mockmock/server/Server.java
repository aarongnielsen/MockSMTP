package com.mockmock.server;

import com.mockmock.Settings;

/** A simple interface for describing MockSMTP servers, such as the SMTP and HTTP servers. **/
public interface Server {
    void setSettings(Settings settings);
    void start();
}
