package com.rookie.printonline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.rookie.printonline.common.HttpUtils;
import com.rookie.printonline.common.JsonUtil;
import com.rookie.printonline.enums.HttpStatus;
import com.rookie.printonline.exe.PrintServe;
import com.rookie.printonline.result.ApiResponse;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.application.Application;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
public class StartApplication extends Application {
    private static final int PORT = 8080; // HTTP 服务端口

    private static final double DPI = 300; // 标准DPI
    private static final double MM_TO_INCH = 25.4;
    private static double mmToPx(double mm) {
        return mm / MM_TO_INCH * DPI;
    }
    @Override
    public void start(Stage stage) throws IOException {
        Button printBtn = new Button("Print XML");
        printBtn.setOnAction(e -> {
            try {
                Document doc = parseXML("D:\\xml\\QR_Print_Template_100_32_2.0.xml");
                TextFlow printableNode = createPrintableNode(doc);
                printableNode.setStyle("-fx-background-color: white; -fx-padding: 10px;");

                Text text1 = new Text("Hello, ");
                text1.setStyle("-fx-font-size: 20px; -fx-fill: red;");

                Text text2 = new Text("JavaFX!");
                text2.setStyle("-fx-font-size: 24px; -fx-fill: blue; -fx-font-weight: bold;");

                printableNode.getChildren().addAll(text1, text2);

                // 生成图片
                WritableImage image = textFlowToImage(printableNode);
                saveImage(image, "textflow_image.png");

                // 添加短暂延迟确保窗口完全初始化
                PauseTransition pause = new PauseTransition(Duration.millis(100));
                pause.setOnFinished(event -> printNode(printableNode, stage));
                pause.play();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(printBtn);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.show();

    }
    private void saveImage(WritableImage image, String filename) {
        try {
            File file = new File(filename);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Image saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save image: " + e.getMessage());
        }
    }
    private WritableImage textFlowToImage(TextFlow textFlow) {
        // 确保TextFlow已完成布局
        textFlow.snapshot(null, null);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        // 计算TextFlow的实际大小
        double width = textFlow.getBoundsInParent().getWidth();
        double height = textFlow.getBoundsInParent().getHeight();

        // 创建合适大小的图片
        return textFlow.snapshot(params, new WritableImage((int)width, (int)height));
    }
    private static void printJavaFXNode(Node node) {
        // 1. 先缩放节点到实际物理尺寸（100mm×30mm）
        double targetWidthPx = mmToPx(0.1); // 100mm → 1181px (300DPI)
        double targetHeightPx = mmToPx(0.3); // 30mm → 354px

        double scaleX = targetWidthPx / node.getBoundsInParent().getWidth();
        double scaleY = targetHeightPx / node.getBoundsInParent().getHeight();
        double scale = Math.min(scaleX, scaleY);
        node.getTransforms().add(new Scale(scale, scale));

        // 2. 必须渲染到Scene（关键步骤！）
        Stage tempStage = new Stage();
        Scene tempScene = new Scene(new Group(node));
        tempStage.setScene(tempScene);
        tempStage.show(); // 必须显示才能渲染
        Platform.runLater(() -> tempStage.hide()); // 异步隐藏

        // 3. 创建打印任务
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            System.err.println("错误：无法创建打印任务");
            return;
        }

        // 4. 设置A4纸张和边距（必须用HARDWARE_MINIMUM）
        PageLayout pageLayout = job.getPrinter().createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );

        // 5. 计算精确位置（10mm,10mm）
        double offsetXPx = mmToPx(10);
        double offsetYPx = mmToPx(10);
        node.setLayoutX(offsetXPx);
        node.setLayoutY(offsetYPx);

        // 6. 打印调试信息
        System.out.println("节点尺寸：" + node.getBoundsInParent());
        System.out.println("可打印区域：" + pageLayout.getPrintableWidth() + "×" + pageLayout.getPrintableHeight());

        // 7. 执行打印
        boolean success = job.printPage(pageLayout, node);
        if (success) {
            job.endJob();
            System.out.println("打印成功！");
        } else {
            System.err.println("打印失败！状态：" + job.getJobStatus());
            // 打印系统级错误信息
            if (job.getJobStatus() == PrinterJob.JobStatus.ERROR) {
               // System.err.println("系统报告：" + job.getPrinter().getPrinterStatus());
            }
        }
    }
    public Document parseXML(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new File(filePath));
    }
    public TextFlow createPrintableNode(Document doc) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(10));

        // 简单示例：提取XML文本内容
        String content = doc.getDocumentElement().getTextContent();

        Text text = new Text(content);
        text.setFont(Font.font("Arial", 12));
        textFlow.getChildren().add(text);

        return textFlow;
    }
    public void printNode(Node node,Stage ownerWindow) {
        Platform.runLater(() -> {
            // 确保布局已完成
            node.applyCss();
         //   ownerWindow.layout();

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                if (job.showPrintDialog(null)) {
                    boolean success = job.printPage(node);
                    if (success) {
                        job.endJob();
                    }
                }
            }
        });
    }
    private static void processLayoutElement(Element layout, Pane parent) throws Exception {
        double width = Double.parseDouble(layout.getAttribute("width"));
        double height = Double.parseDouble(layout.getAttribute("height"));
        double left = Double.parseDouble(layout.getAttribute("left"));
        double top = Double.parseDouble(layout.getAttribute("top"));

        // 处理二维码
        NodeList barcodes = layout.getElementsByTagName("barcode");
        if (barcodes.getLength() > 0) {
            Element barcode = (Element) barcodes.item(0);
            if ("qrcode".equals(barcode.getAttribute("type"))) {
                String content = barcode.getTextContent().replace("<%=_data.qrcode%>", DATA.get("qrcode"));
                ImageView qrCode = generateQrCodeImageView(content, width, height);
                qrCode.setLayoutX(mmToPx(left));
                qrCode.setLayoutY(mmToPx(top));
                parent.getChildren().add(qrCode);
            }
            return;
        }

        // 处理文本
        NodeList texts = layout.getElementsByTagName("text");
        if (texts.getLength() > 0) {
            Element text = (Element) texts.item(0);
            String content = text.getTextContent()
                    .replace("<%=_data.qrcode%>", DATA.get("qrcode"))
                    .replace("<%=_data.sn%>", DATA.get("sn"));

            Text textNode = new Text(content);
            applyTextStyle(textNode, text.getAttribute("style"));

            textNode.setLayoutX(mmToPx(left));
            textNode.setLayoutY(mmToPx(top + getFontSize(text.getAttribute("style")) / 2)); // 垂直居中调整

            parent.getChildren().add(textNode);
        }
    }
    private static double getFontSize(String style) {
        String[] styles = style.split(";");
        for (String s : styles) {
            if (s.startsWith("fontSize")) {
                return Double.parseDouble(s.split(":")[1]);
            }
        }
        return 10; // 默认大小
    }
    private static void processLineElement(Element line, Pane parent) {
        Line lineNode = new Line(
                mmToPx(Double.parseDouble(line.getAttribute("startX"))),
                mmToPx(Double.parseDouble(line.getAttribute("startY"))),
                mmToPx(Double.parseDouble(line.getAttribute("endX"))),
                mmToPx(Double.parseDouble(line.getAttribute("endY")))
        );

        String style = line.getAttribute("style");
        if (style.contains("dashed")) {
            lineNode.getStrokeDashArray().addAll(5d, 5d);
        }
        if (style.contains("lineColor:#")) {
            String color = style.split("lineColor:#")[1].split(";")[0];
            lineNode.setStyle("-fx-stroke: #" + color + ";");
        }

        parent.getChildren().add(lineNode);
    }

    private static ImageView generateQrCodeImageView(String content, double widthMM, double heightMM) throws Exception {
        int sizePx = (int) mmToPx(Math.min(widthMM, heightMM));
        BitMatrix matrix = new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, sizePx, sizePx);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", os);

        return new ImageView(new Image(new ByteArrayInputStream(os.toByteArray())));
    }
    /**
     * 将 JavaFX Node 保存为图片文件
     * @param node     要保存的节点（如 Pane、Group）
     * @param filePath 图片保存路径（如 "output.png"）
     */
    private static void saveNodeAsImage(Node node, String filePath) {
        // 1. 确保节点已正确渲染（临时添加到 Scene）
        Scene scene = new Scene(new Group(node));

        // 2. 创建 WritableImage 并捕获节点内容
        WritableImage image = new WritableImage(
                (int) node.getBoundsInParent().getWidth(),
                (int) node.getBoundsInParent().getHeight()
        );
        node.snapshot(null, image);

        // 3. 保存为 PNG 文件
        File file = new File(filePath);
        try {
            ImageIO.write(
                    SwingFXUtils.fromFXImage(image, null), // 转换 JavaFX Image 为 BufferedImage
                    "png", // 图片格式（可选 "png"、"jpg" 等）
                    file
            );
            System.out.println("图片已保存到: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("保存图片失败: " + e.getMessage());
        }
    }
    private static void applyTextStyle(Text text, String style) {
        String[] styles = style.split(";");
        for (String s : styles) {
            String[] kv = s.split(":");
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "fontFamily":
                    text.setFont(Font.font(kv[1], text.getFont().getSize()));
                    break;
                case "fontSize":
                    text.setFont(Font.font(text.getFont().getFamily(), Double.parseDouble(kv[1])));
                    break;
                case "fontWeight":
                    if ("bold".equals(kv[1])) {
                        text.setStyle("-fx-font-weight: bold;");
                    }
                    break;
            }
        }
    }
    private static final Map<String, String> DATA = Map.of(
            "qrcode", "PROD-12345",
            "sn", "SN-2023-001"
    );


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