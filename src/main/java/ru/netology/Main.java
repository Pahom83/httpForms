package ru.netology;

import org.apache.http.NameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        MultiTreadServer multiTreadServer = new MultiTreadServer(64);
        multiTreadServer.addHandler("GET", "/messages", (request, responseStream) -> {
            StringBuilder builder = new StringBuilder();
            for (NameValuePair param: request.getQueryParams()) {
                builder.append(param.getName()).append(": ").append(param.getValue()).append("\r\n");
            }
            String params = builder.toString();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + Arrays.toString(params.getBytes()) + "\r\n" +
                            "Content-Length: " + params.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());

            responseStream.write(params.getBytes(StandardCharsets.UTF_8));
            responseStream.flush();
        });
        multiTreadServer.addHandler("POST", "/messages", (request, responseStream) -> {
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + "Hello".strip() + "\r\n" +
                            "Content-Length: " + "Hello".length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.write("Hello\r\n".getBytes());
            responseStream.flush();
        });

        multiTreadServer.start(9999);
    }
}