package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Request {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private final String method;
    private final String path;
    private final String protocol;
    private final List<String> headers;
    private final String body;
    private List<NameValuePair> params;
    private final String referer = "Referer: ";
    private String refererValue;

    public Request(String method, String path, String protocol, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String protocol() {
        return protocol;
    }

    public List<String> headers() {
        return headers;
    }

    public String body() {
        return body;
    }

    public String getQueryParam(String name) {
        for (final NameValuePair param : params) {
            if (param.getName().equals(name)){
                return param.getValue();
            }
        }
        return "Поле не обнаружено";
    }
    public List<NameValuePair> getQueryParams() {
        return params;
    }

    public void createQueryParams() {
        if (method.equals(GET)){
            params = URLEncodedUtils.parse(path, StandardCharsets.UTF_8, '?', '&', ';');
        } else if (method.equals(POST)){
            for (String header : headers){
                if (header.contains(referer)){
                    refererValue = header.substring(referer.length());
                }
                if (header.contains("application/x-www-form-urlencoded")){
                    params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8, '?', '&', ';');
                }
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
    public String getRefererValue() {
        return refererValue;
    }

}
