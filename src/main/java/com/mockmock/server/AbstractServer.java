package com.mockmock.server;

import com.mockmock.Settings;
import lombok.Setter;

/** A simple interface for describing MockSMTP servers, such as the SMTP and HTTP servers. **/
public abstract class AbstractServer {

    // instance fields

    /** The configuration of the application to be used when the server starts. **/
    @Setter
    protected Settings settings;

    // methods to be implemented

    /** Starts the server. **/
    public abstract void start();

    /** Stops the server. **/
    public abstract void stop();

}
