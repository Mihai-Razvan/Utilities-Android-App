package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HttpContextAccount implements HttpContextBasics {

    private final HttpServer server;

    public HttpContextAccount(HttpServer server) {
        this.server = server;
        createContexts();
    }

    @Override
    public void createContexts() {
        context_account_addresses_new();
        context_account_addresses();
        context_account_addresses_delete();
        context_account_delete();
        context_account_password_change();
    }

    ///////////////////////////////////////////////CONTEXTS/////////////////////////////////////////////


    private void context_account_addresses_new() {
        server.createContext("/account/addresses/new", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                System.out.println("REQUEST RECEIVED ON /account/addresses/new");
                BufferedReader request = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestLine = request.readLine();

                int clientId = MethodsAccount.extractClientIdFromJson(requestLine);
                String city = MethodsAccount.extractCityFromJson(requestLine);
                String street = MethodsAccount.extractStreetFromJson(requestLine);
                String number = MethodsAccount.extractNumberFromJson(requestLine);
                String details = MethodsAccount.extractDetailsFromJson(requestLine);

                DatabasePOST.postAddress(clientId, city, street, number, details);
            }
        });
    }

    private void context_account_addresses() {
        server.createContext("/account/addresses", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                System.out.println("REQUEST RECEIVED ON /account/addresses");
                BufferedReader request = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestLine = request.readLine();
                String responseMessage = "";

                try {
                    int clientId = MethodsAccount.extractClientIdFromJson(requestLine);
                    ArrayList<String> addressesList = DatabaseGET.getAllAddresses(clientId);   //could throw SQLException
                    responseMessage = MethodsIndex.addressListToJson(addressesList);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                exchange.sendResponseHeaders(200, responseMessage.length());
                DataOutputStream response = new DataOutputStream(exchange.getResponseBody());
                response.writeBytes(responseMessage);
                response.flush();
                response.close();
            }
        });
    }

    private void context_account_addresses_delete() {
        server.createContext("/account/addresses/delete", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                System.out.println("REQUEST RECEIVED ON /account/addresses/delete");
                BufferedReader request = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestLine = request.readLine();
                String responseMessage;

                try {
                    int clientId = MethodsAccount.extractClientIdFromJson(requestLine);
                    String city = MethodsAccount.extractCityFromJson(requestLine);
                    String street = MethodsAccount.extractStreetFromJson(requestLine);
                    String number = MethodsAccount.extractNumberFromJson(requestLine);
                    String details = MethodsAccount.extractDetailsFromJson(requestLine);

                    responseMessage = DatabasePOST.deleteAddress(clientId, city, street, number, details);       //could throw SqlException
                    System.out.println("SUCCESSFULLY DELETED ADDRESS FROM DATABASE");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("COULDN'T DELETE ADDRESS FROM DATABASE");
                    responseMessage = "Failed";
                }

                exchange.sendResponseHeaders(200, responseMessage.length());
                DataOutputStream response = new DataOutputStream(exchange.getResponseBody());
                response.writeBytes(responseMessage);
                response.flush();
                response.close();
            }
        });
    }

    private void context_account_delete() {
        server.createContext("/account/delete", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                System.out.println("REQUEST RECEIVED ON /account/delete");
                BufferedReader request = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestLine = request.readLine();
                String responseMessage;

                try {
                    int clientId = MethodsAccount.extractClientIdFromJson(requestLine);

                    responseMessage = DatabasePOST.deleteAccount(clientId);       //could throw SqlException
                    System.out.println("SUCCESSFULLY DELETED ACCOUNT FROM DATABASE");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("COULDN'T DELETE ACCOUNT FROM DATABASE");
                    responseMessage = "Failed";
                }

                exchange.sendResponseHeaders(200, responseMessage.length());
                DataOutputStream response = new DataOutputStream(exchange.getResponseBody());
                response.writeBytes(responseMessage);
                response.flush();
                response.close();
            }
        });
    }

    private void context_account_password_change() {  //response is -2 for failed, -1 for wrong current password and 1 for success
        server.createContext("/account/password/change", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                System.out.println("REQUEST RECEIVED ON /account/password/change");
                BufferedReader request = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestLine = request.readLine();
                String responseMessage = "";

                try {
                    int clientId = MethodsAccount.extractClientIdFromJson(requestLine);
                    String currentPassword = MethodsAccount.extractCurrentPasswordFromJson(requestLine);
                    String newPassword = MethodsAccount.extractNewPasswordFromJson(requestLine);

                    String response = DatabasePOST.changePassword(clientId, currentPassword, newPassword);   //throws exception or returns "Success"/"Wrong password"
                    if(response.equals("Success"))
                        responseMessage = "1";
                    else    //if introduced current password is wrong
                        responseMessage = "-1";

                } catch (SQLException | NoSuchAlgorithmException e) {
                    System.out.println(e.getMessage());
                    responseMessage = "-2";
                }

                exchange.sendResponseHeaders(200, responseMessage.length());
                DataOutputStream response = new DataOutputStream(exchange.getResponseBody());
                response.writeBytes(responseMessage);
                response.flush();
                response.close();
            }
        });
    }
}
