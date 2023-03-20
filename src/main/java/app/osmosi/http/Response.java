package app.osmosi.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {
    private final ResponseCodes code;
    private final String body;
    private final String contentType;

    public Response(ResponseCodes code, String body, String contentType) {
        this.code = code;
        this.body = body;
        this.contentType = contentType;
    }

    public Response(ResponseCodes code, String body) {
        this(code, body, "text/html");
    }

    public Response(String body) {
        this(ResponseCodes.OK, body);
    }

    public Response(ResponseCodes code, Object bean) {
        this.code = code;
        this.body = parseBean(bean);
        this.contentType = "application/json";
    }

    public Response(Object bean) {
        this(ResponseCodes.OK, bean);
    }

    private String parseBean(Object bean) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            System.out.println("Problem generating the JSON from bean");
        }
        return "";
    }

    public String getBody() {
        return body;
    }

    public ResponseCodes getCode() {
        return code;
    }

    public String getContentType() {
        return contentType;
    }
}
