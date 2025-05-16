package com.rookie.printonline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookie.printonline.common.HttpUtils;
import com.rookie.printonline.common.JsonUtil;
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

                    // 提取 `/api/data/` 后面的部分（如 "search"）
                    String action = path.substring("/api/print/".length());
                    try {
                        String response = "";
                        switch (action) {
                            case "search":
                                List<String> allPrint = PrintServe.getAllPrint();
                                ApiResponse<List<String>> success = ApiResponse.success(allPrint);
                                response = new ObjectMapper().writeValueAsString(success);
                                break;
                            case "update":
                                response = "";
                                break;
                            default:
                                response = "{\"error\": \"Unknown action: " + action + "\"}";
                                exchange.sendResponseHeaders(404, response.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                                return;
                        }


                        // 设置HTTP状态码和响应头
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        exchange.getResponseBody().write(response.getBytes());
                    }catch (Exception e){
                        e.printStackTrace();
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