package com.rookie.printonline;

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
                    // 处理 GET 请求
                    if ("GET".equals(exchange.getRequestMethod())) {
                        String response = "{\"message\": \"Hello from JavaFX HTTP Server!\"}";
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                    // 处理 POST 请求
                    else if ("POST".equals(exchange.getRequestMethod())) {
                        // 读取请求体
                        String requestBody = new String(exchange.getRequestBody().readAllBytes());
                        System.out.println("Received POST data: " + requestBody);

                        // 返回响应
                        String response = "{\"status\": \"success\", \"data\": \"" + requestBody + "\"}";
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
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