package com.rookie.printonline.common;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.rookie.printonline.util.PrintTest;
import com.rookie.printonline.util.XmlUtils;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.print.PageFormat;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);  // 使用JavaFX标准启动方式
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            // 1. 解析XML模板
            Document doc = XmlUtils.parseXmlFromResources("QR_Print_Template_02.xml");

            // 2. 创建JavaFX容器
            Element page = doc.getDocumentElement();
            double widthMM = Double.parseDouble(page.getAttribute("width"));
            double heightMM = Double.parseDouble(page.getAttribute("height"));

            Pane labelPane = new Pane();
            labelPane.setPrefSize(mmToPx(widthMM), mmToPx(heightMM));
            labelPane.setStyle("-fx-background-color: white;");

            // 3. 处理布局元素和线条
            NodeList layouts = doc.getElementsByTagName("layout");
            for (int i = 0; i < layouts.getLength(); i++) {
                processLayoutElement((Element) layouts.item(i), labelPane);
            }

            NodeList lines = doc.getElementsByTagName("line");
            for (int i = 0; i < lines.getLength(); i++) {
                processLineElement((Element) lines.item(i), labelPane);
            }

            // 4. 将节点添加到场景并显示
            Scene scene = new Scene(new Group(labelPane));
            primaryStage.setScene(scene);
            List<String> barcodeList=new ArrayList<>();
            //primaryStage.show();
            //saveNodeAsImage(labelPane, "456.png");
            Node node = new PrintTest(barcodeList).parseXmlToNode("12234");
            PrintTest.saveNodeAsImage(node,"456.png");
            // 5. 延迟打印以确保渲染完成
//            PauseTransition delay = new PauseTransition(Duration.millis(400));
//            delay.setOnFinished(event -> printJavaFXNode(labelPane));
//            delay.play();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void saveNodeAsImage(Node node, String filePath) {
        // 1. 确保节点已正确渲染（可能需要临时添加到 Scene）
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
                    SwingFXUtils.fromFXImage(image, null),
                    "png",
                    file
            );
            System.out.println("图片已保存到: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("保存图片失败: " + e.getMessage());
        }
    }

    private static Map<String, String> parseStyle(String style) {
        Map<String, String> styleMap = new HashMap<>();
        if (style == null || style.isEmpty()) return styleMap;

        String[] styles = style.split(";");
        for (String s : styles) {
            String[] kv = s.split(":");
            if (kv.length == 2) {
                styleMap.put(kv[0].trim(), kv[1].trim());
            }
        }
        return styleMap;
    }

    private static void applyLayoutStyle(Pane pane, String style) {
        Map<String, String> styleMap = parseStyle(style);

        // 背景色
        if (styleMap.containsKey("backgroundColor")) {
            pane.setStyle("-fx-background-color: " + styleMap.get("backgroundColor") + ";");
        }

        // 边框
        if (styleMap.containsKey("borderColor")) {
            pane.setStyle(pane.getStyle() +
                    "-fx-border-color: " + styleMap.get("borderColor") + ";" +
                    "-fx-border-width: 1px;");
        }

        // zIndex
        if (styleMap.containsKey("zIndex")) {
            pane.setViewOrder(-Double.parseDouble(styleMap.get("zIndex")));
        }
    }
    private static void processLayoutElement(Element layout, Pane parent) throws Exception {
        double width = Double.parseDouble(layout.getAttribute("width"));
        double height = Double.parseDouble(layout.getAttribute("height"));
        double left = Double.parseDouble(layout.getAttribute("left"));
        double top = Double.parseDouble(layout.getAttribute("top"));

        // 创建布局容器（用于承载所有子元素）
        Pane layoutContainer = new Pane();
        layoutContainer.setPrefSize(mmToPx(width), mmToPx(height));
        layoutContainer.setLayoutX(mmToPx(left));
        layoutContainer.setLayoutY(mmToPx(top));
        // 解析并应用layout的style属性
        if (layout.hasAttribute("style")) {
            applyLayoutStyle(layoutContainer, layout.getAttribute("style"));
        }
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
        // 处理文本
        NodeList texts = layout.getElementsByTagName("text");
        if (texts.getLength() > 0) {
            Element text = (Element) texts.item(0);
            String content = text.getTextContent()
                    .replace("<%=_data.qrcode%>", DATA.get("qrcode"))
                    .replace("<%=_data.sn%>", DATA.get("sn"));

            Text textNode = new Text(content);

            // 应用XML中定义的样式
            applyTextStyle(textNode, text.getAttribute("style"));

            // 设置文本位置（垂直居中调整）
            textNode.setLayoutX(mmToPx(left));
            textNode.setLayoutY(mmToPx(top+2));

            // 如果是多行文本（如"O\nC\nO\nC"），设置自动换行
            if (content.contains("\n")) {
                textNode.setWrappingWidth(mmToPx(width)); // 限制宽度以触发换行
            }

            parent.getChildren().add(textNode);
        }
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
        int sizePx_w = (int) mmToPx(widthMM);
        int sizePx_h = (int) mmToPx(heightMM);
        BitMatrix matrix = new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, sizePx_w, sizePx_h);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", os);

        return new ImageView(new Image(new ByteArrayInputStream(os.toByteArray())));
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
                case "align":
                    if ("center".equals(kv[1].trim())) {
                      //  text.setTextAlignment(TextAlignment.CENTER);
                    }
                case "valign":
                    if ("center".equals(kv[1].trim())) {
                        text.setTextAlignment(TextAlignment.CENTER);
                    }
                    break;
            }
        }
    }
    private static final Map<String, String> DATA = Map.of(
            "qrcode", "0000\n2320\n0025\n6448\n9759\n0010",
            "sn", "SN-2023-001"
    );

    private static final double DPI = 300; // 标准DPI
    private static final double MM_TO_INCH = 25.4;
    private static double getFontSize(String style) {
        String[] styles = style.split(";");
        for (String s : styles) {
            if (s.startsWith("fontSize")) {
                return Double.parseDouble(s.split(":")[1]);
            }
        }
        return 10; // 默认大小
    }

    private static double mmToPx(double mm) {
        return mm / MM_TO_INCH * DPI;
    }

    // 毫米转点(1英寸=25.4毫米, 1英寸=72点)
    private static double mmToPoints(double mm) {
        return mm / 25.4 * 72;
    }
    // 修改打印方法
    private static void printJavaFXNode(Node node) {

        // 确保布局完成
        node.applyCss();
        node.snapshot(null, null);


        // 延迟执行
        Platform.runLater(() -> {
            //   PrinterJob job = PrinterJob.createPrinterJob();
            // 确保节点已完成布局
            node.snapshot(null, null);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                // 使用HARDWARE_MINIMUM边距
// 使用100mm×148mm的明信片规格（高度需手动限制）
                // 设置自定义纸张大小(100mm x 30mm)
                double widthMM = 120;
                double heightMM = 60;
                double marginMM = 2; // 2mm边距

                // 创建页面格式
                PageFormat pf = new PageFormat();
                pf.setOrientation(PageFormat.PORTRAIT);

                // 设置纸张
                java.awt.print.Paper paper = new java.awt.print.Paper();
                paper.setSize(mmToPoints(widthMM), mmToPoints(heightMM));
                paper.setImageableArea(
                        mmToPoints(marginMM),
                        mmToPoints(marginMM),
                        mmToPoints(widthMM - 2 * marginMM),
                        mmToPoints(heightMM - 2 * marginMM)
                );
                pf.setPaper(paper);



                Paper closestPaper = Paper.A4;

                PageLayout pageLayout = job.getPrinter().createPageLayout(
                        closestPaper,
                        PageOrientation.PORTRAIT,
                        Printer.MarginType.HARDWARE_MINIMUM
                );

                // 移除旧变换
                node.getTransforms().clear();

                // 计算缩放比例（确保内容适配标签纸）
              //  double scaleX = mmToPx(100) / node.getBoundsInParent().getWidth();
                //double scaleY = mmToPx(30) / node.getBoundsInParent().getHeight();
                //double scale = Math.min(scaleX, scaleY);

                // 应用缩放（保持原始比例）
                node.getTransforms().add(new Scale(1, 1));
                boolean success = job.printPage(pageLayout, node);
                if (success) {
                    job.endJob();
                    System.out.println("打印成功");
                } else {
                    System.out.println("打印失败");
                }
            }

        });

    }
}
