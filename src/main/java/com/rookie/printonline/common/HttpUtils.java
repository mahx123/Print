package com.rookie.printonline.common;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;

public class HttpUtils {
    // 发送JSON响应
    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String json)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    // 发送错误响应
    public static void sendError(HttpExchange exchange, int statusCode, String message)
            throws IOException {
        String errorJson = String.format("{\"error\":\"%s\"}", message);
        sendJsonResponse(exchange, statusCode, errorJson);
    }
}
