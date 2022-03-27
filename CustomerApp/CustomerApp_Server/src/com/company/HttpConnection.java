package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpConnection {

    private HttpServer server;
    private HttpContextTest httpContextTest;
    private HttpContextBills httpContextBills;

    public void createServer() {
        try  {
            server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            createRoutes(); //sets the http paths
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server.setExecutor(threadPoolExecutor);
            server.start();

            System.out.println("SERVER STARTED");

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }


    private void createRoutes()
    {
        httpContextTest = new HttpContextTest(server);
        httpContextBills = new HttpContextBills(server);
    }

    public HttpServer getServer() {
        return server;
    }
}
