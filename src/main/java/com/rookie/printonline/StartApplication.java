package com.rookie.printonline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookie.printonline.common.HttpUtils;
import com.rookie.printonline.common.JsonUtil;
import com.rookie.printonline.enums.HttpStatus;
import com.rookie.printonline.exe.PrintServe;
import com.rookie.printonline.result.ApiResponse;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StartApplication extends Application {
    private static final int PORT = 8080; // HTTP 服务端口

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        // 启动 HTTP Server（在后台线程运行）
        startHttpServer();
    }

    private void startHttpServer() {
        try {
            // 创建 HTTP Server，监听 8080 端口
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // 注册 REST 接口 `/api/data`
            server.createContext("/api/print", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    URI requestUri = exchange.getRequestURI();
                    String path = requestUri.getPath(); // 获取完整路径，如 "/api/data/search"
                    exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
                    // 提取 `/api/data/` 后面的部分（如 "search"）
                    String action = path.substring("/api/print/".length());
                    OutputStream os = null;
                    try {
                        os = exchange.getResponseBody();
                        String response = "";
                        switch (action) {
                            case "search":
                                List<String> allPrint = PrintServe.getAllPrint();
                                ApiResponse<List<String>> success = ApiResponse.success(allPrint);
                                response = JsonUtil.objectToJson(success);
                                break;
                            case "update":
                                response = "";
                                break;
                            case "print":

                                break;
                            default:
                                response = "{\"error\": \"Unknown action: " + action + "\"}";
                                exchange.sendResponseHeaders(404, response.length());

                                return;
                        }


                        // 设置HTTP状态码和响应头

                        exchange.sendResponseHeaders(HttpStatus.OK.getCode(), response.getBytes().length);
                        os.write(response.getBytes());
                    } catch (Exception e) {
                        os = exchange.getResponseBody();
                        ApiResponse<?> error = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
                        String errorJson = JsonUtil.objectToJson(error);
                        exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), errorJson.getBytes().length);
                        os.write(errorJson.getBytes());

                    } finally {
                        if (os != null) {
                            os.flush();
                            os.close();
                        }


                    }
                }
            });

            // 启动 HTTP Server（在后台线程运行）
            server.start();
            System.out.println("HTTP Server started on port " + PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}