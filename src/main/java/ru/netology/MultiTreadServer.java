package ru.netology;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.nio.file.Files.readAllBytes;

public class MultiTreadServer {
    private final ExecutorService service;
    final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public static final ConcurrentHashMap<String, Handler> getHandlers = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap <String, Handler> postHandlers = new ConcurrentHashMap<>();


    public MultiTreadServer(int threads) {
        service = Executors.newFixedThreadPool(threads);
    }

    public void start(int port){
        createDefaultHandlers();
        while (true){
            try (ServerSocket mainServer = new ServerSocket(port)){
                service.submit(new Server(mainServer.accept()));
            } catch (IOException e) {
                e.printStackTrace();
                service.shutdown();
                break;
            }
        }
    }
    private void createDefaultHandlers(){
        addHandler("GET", "/", (request, responseStream) -> {
            final Path filePath;
            if (validPaths.contains(request.getPath())){
                filePath = Path.of(".", "public", request.path());
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + Files.probeContentType(filePath) + "\r\n" +
                                "Content-Length: " + readAllBytes(filePath).length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(readAllBytes(filePath));
            } else if (getHandlers.containsKey(request.getQueryParams().get(0).getName())){
                getHandlers.get(request.getQueryParams().get(0).getName()).handle(request, responseStream);
            } else {
                responseStream.write((
                        """
                                HTTP/1.1 404 Not Found\r
                                Content-Length: 0\r
                                Connection: close\r
                                \r
                                """
                ).getBytes());
            }
            responseStream.flush();
        });
        addHandler("POST", "/", (request, responseStream) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(request.getRefererValue()).append("\r\n");
            for (NameValuePair param: request.getQueryParams()) {
                builder.append(param.getName()).append(": ").append(param.getValue()).append("\r\n");
            }
            String params = builder.toString();
            if (request.path().startsWith("/")){
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + Arrays.toString(params.getBytes()) + "\r\n" +
                                "Content-Length: " + params.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(params.getBytes(StandardCharsets.UTF_8));
            } else {
                responseStream.write((
                        """
                                HTTP/1.1 404 Not Found\r
                                Content-Length: 0\r
                                Connection: close\r
                                \r
                                """
                ).getBytes());
            }
            responseStream.flush();
        });
    }
    public void addHandler(String method, String path, Handler handler) {
        if (method.equals("GET")){
            getHandlers.put(path, handler);
        } else if (method.equals("POST")){
            postHandlers.put(path, handler);
        }
    }
}
