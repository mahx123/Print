package com.rookie.printonline.common;
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

public class Main {

    public static void main(String[] args) throws Exception{

        try {
            // 1. 解析XML模板
            File xmlFile = new File("D://xml/QR_Print_Template_100_32_2.0.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // 关键设置
            Document doc = factory.newDocumentBuilder().parse(xmlFile);
            System.out.println("根元素: " + doc.getDocumentElement().getNodeName());
            // 遍历所有layout节点测试
            // 2. 创建JavaFX容器（按实际尺寸）
            Element page = doc.getDocumentElement();
            double widthMM = Double.parseDouble(page.getAttribute("width"));
            double heightMM = Double.parseDouble(page.getAttribute("height"));

            Pane labelPane = new Pane();
            labelPane.setPrefSize(mmToPx(widthMM), mmToPx(heightMM));

            // 3. 处理所有布局元素
            NodeList layouts = doc.getElementsByTagName("layout");
            for (int i = 0; i < layouts.getLength(); i++) {
                Element layout = (Element) layouts.item(i);
                processLayoutElement(layout, labelPane);
            }

            // 4. 处理线条元素
            NodeList lines = doc.getElementsByTagName("line");
            for (int i = 0; i < lines.getLength(); i++) {
                Element line = (Element) lines.item(i);
                processLineElement(line, labelPane);
            }
         //   saveNodeAsImage(labelPane, "print_preview.png");
//
//            // 5. 打印
            printJavaFXNode(labelPane);

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

    private static final double DPI = 72; // 标准DPI
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

    private static void printJavaFXNode(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            PageLayout pageLayout = job.getPrinter().createPageLayout(
                    Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

            // 缩放节点以适应纸张
            double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
            double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
            double scale = Math.min(scaleX, scaleY);
            node.getTransforms().add(new javafx.scene.transform.Scale(scale, scale));

            boolean success = job.printPage(pageLayout, node);
            if (success) {
                job.endJob();
            }
        }
    }
}
