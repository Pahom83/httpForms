package ru.netology;
import java.util.List;

public class RequestBuilder {
    private String method;
    private String path;
    private String protocol;
    private String body;
    private List<String> headers;

    public void createMethod(String method){
        this.method = method;
    }
    public void createPath(String path){
        this.path = path;
    }
    public void createProtocol(String protocol){
        this.protocol = protocol;
    }
    public void createBody(String body){
        this.body = body;
    }
    public void createHeaders(List<String> headers) {
        this.headers = headers;
    }
    public Request build(){
        Request request = new Request(method, path, protocol, headers, body);
        if (method != null && path != null && protocol != null){
            request.createQueryParams();
            return request;
        }
        return null;
    }

}
