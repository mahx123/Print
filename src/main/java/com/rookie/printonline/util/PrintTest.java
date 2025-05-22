package com.rookie.printonline.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import static javafx.application.Application.launch;

public class PrintTest implements Printable {
    /**
     * @param Graphic指明打印的图形环境
     * @param PageFormat指明打印页格式
     *            （页面大小以点为计量单位，1点为1英才的1/72，1英寸为25.4毫米。A4纸大致为595×842点）
     * @param pageIndex指明页号
     **/
    // private final static int POINTS_PER_INCH = 32;
    public int print(Graphics gra, PageFormat pf,
                     int pageIndex)
            throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) gra;
        g2.setColor(Color.black);

        try {





            // 绘制图片
         //   g2.drawImage(img, 0, 0, null);
            Node node = parseXmlToNode();
            saveNodeAsImage(node, "456.png");
            BufferedImage img = ImageIO.read(new File("456.png"));

            // 直接渲染到Graphics2D，不经过中间图片
            double width = node.getBoundsInParent().getWidth();
            double height = node.getBoundsInParent().getHeight();
            // 计算缩放比例
            double scale = Math.min(
                    pf.getImageableWidth() / width,
                    pf.getImageableHeight() / height
            );

            // 应用变换
            g2.translate(pf.getImageableX(), pf.getImageableY());
            g2.scale(scale, scale);
          //  System.out.println("=============");
            double actualWidth = node.getBoundsInParent().getWidth();
            double actualHeight = node.getBoundsInParent().getHeight();
            renderNodeToGraphics2D(node,g2,(int)actualWidth, (int)actualHeight);
            return PAGE_EXISTS;
        } catch (Exception e) {

            e.printStackTrace();
            return NO_SUCH_PAGE;
        }

    }

    public static void saveNodeAsImage(Node node, String filePath) {
        // 1. 确保节点已正确渲染（可能需要临时添加到 Scene）


        // 1. 创建场景并添加节点（触发布局计算）
        Scene scene = new Scene(new Group(node));
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        // 强制布局计算（关键步骤）
        scene.getRoot().applyCss();
        scene.getRoot().layout(); // 触发布局
        Platform.runLater(() -> { // 在JavaFX线程中渲染
         //   System.out.println(node.getBoundsInParent().getWidth());
         //   System.out.println(node.getBoundsInParent().getHeight());
            // 2. 创建 WritableImage 并捕获节点内容
            WritableImage image = new WritableImage(
                    (int) node.getBoundsInParent().getWidth(),
                    (int) node.getBoundsInParent().getHeight()
            );
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT);
            node.snapshot(params, image);

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
        });


    }
    public Node parseXmlToNode(){
        Pane labelPane = new Pane();
        try {
            // 1. 解析XML模板
            Document doc = XmlUtils.parseXmlFromResources("QR_Print_Template_02.xml");

            // 2. 创建JavaFX容器
            Element page = doc.getDocumentElement();
            double widthMM = Double.parseDouble(page.getAttribute("width"));
            double heightMM = Double.parseDouble(page.getAttribute("height"));


            labelPane.setPrefSize(mmToPx(widthMM), mmToPx(heightMM));
            labelPane.setLayoutX(1);
            labelPane.setLayoutY(0);
            labelPane.setStyle("-fx-background-color: white; -fx-padding: 0; -fx-border-width: 0;");


            // 3. 处理布局元素和线条
            NodeList layouts = doc.getElementsByTagName("layout");
            for (int i = 0; i < layouts.getLength(); i++) {
                processLayoutElement((Element) layouts.item(i), labelPane);
            }

            NodeList lines = doc.getElementsByTagName("line");
            for (int i = 0; i < lines.getLength(); i++) {
                processLineElement((Element) lines.item(i), labelPane);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return labelPane;
    }

    private static final double DPI = 300; // 标准DPI
    private static final double MM_TO_INCH = 25.4;
    private  double mmToPx(double mm) {
        return mm / MM_TO_INCH * DPI;
    }

    private  void processLayoutElement(Element layout, Pane parent) throws Exception {
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
     //   if (layout.hasAttribute("style")) {
            applyLayoutStyle(layoutContainer, layout.getAttribute("style"));
    //    }
        // 处理二维码
        NodeList barcodes = layout.getElementsByTagName("barcode");
        if (barcodes.getLength() > 0) {
            Element barcode = (Element) barcodes.item(0);
            if ("qrcode".equals(barcode.getAttribute("type"))) {
                String content = barcode.getTextContent().replace("<%=_data.qrcode%>", DATA.get("qrcode")).trim();
                ImageView qrCode = generateQrCodeImageView(content, width, height);
              //  qrCode.getParent() .setLayoutX(0);
              //  qrCode.getParent().setLayoutY(0);
                qrCode.setLayoutX(0);
                qrCode.setLayoutY(0);
                qrCode.setFitWidth(mmToPx(width));
                qrCode.setFitHeight(mmToPx(height));
                layoutContainer.getChildren().add(qrCode); // 将二维码添加到布局容器
                parent.getChildren().add(layoutContainer); // 将布局容器添加到父容器
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
                    .replace("<%=_data.sn%>", DATA.get("sn")).trim();

            Text textNode = new Text(content);
            double leftPx = mmToPx(Double.parseDouble(layout.getAttribute("left")));
            double topPx = mmToPx(Double.parseDouble(layout.getAttribute("top")));

            // 应用XML中定义的样式
            applyTextStyle(textNode, text.getAttribute("style"));
        //    System.out.println("XML left value: " + layout.getAttribute("left") + "mm");

            // 调整垂直位置
            double textHeight = textNode.boundsInLocalProperty().get().getHeight();
            textNode.setLayoutX(leftPx);
            textNode.setLayoutY(topPx + textHeight / 2);

            // 如果是多行文本（如"O\nC\nO\nC"），设置自动换行
            if (content.contains("\n")) {
                textNode.setWrappingWidth(mmToPx(width)); // 限制宽度以触发换行
            }

            parent.getChildren().add(textNode);
        }
    }
    private ImageView generateQrCodeImageView(String content, double widthMM, double heightMM) throws Exception {
        int sizePx_w = (int) mmToPx(widthMM);
        int sizePx_h = (int) mmToPx(heightMM);

        // 彻底消除二维码边距的设置
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0); // 设置边距为0
      //  hints.put(EncodeHintType.QR_VERSION, 10); // 固定版本号避免自动调整
       // hints.put(EncodeHintType.MARGIN, 0); // 关键：设置边距为0

        BitMatrix matrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePx_w,
                sizePx_h,
                hints);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);

        return new ImageView(new Image(new ByteArrayInputStream(os.toByteArray())));
    }
    private  void applyLayoutStyle(Pane pane, String style) {
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
    // 标准 DPI 值，可根据实际情况调整

    /**
     * 将像素转换为毫米
     * @param px 像素值
     * @return 对应的毫米值
     */
    public static double pxToMm(double px) {
        return px / DPI * 25.4;
    }
    private double mmToPoints(double mm) {
        return mm / 25.4 * 72; // 1英寸=72点
    }
    /**
     * 将毫米转换为像素
     * @param mm 毫米值
     * @return 对应的像素值
     */
    private  void applyTextStyle(Text text, String style) {
        String[] styles = style.split(";");
        for (String s : styles) {
            String[] kv = s.split(":");
            if (kv.length != 2) continue;

            switch (kv[0].trim()) {
                case "fontFamily":
                    String fontFamily = kv[1].trim();
                    if (fontFamily.equals("黑体")) {
                        fontFamily = "SimHei"; // Windows下的黑体
                    }
                    text.setFont(Font.font(fontFamily, text.getFont().getSize()));
                    break;
                case "fontSize":
                    try {
                        double sizeInMm = Double.parseDouble(kv[1].trim());
                        double sizeInPoints = mmToPoints(sizeInMm);
                        text.setFont(Font.font(text.getFont().getFamily(), sizeInPoints));
                    } catch (NumberFormatException e) {
                        System.err.println("字体大小解析错误: " + e.getMessage());
                    }
                    break;
                case "fontWeight":
                    if ("bold".equals(kv[1].trim())) {
                        text.setStyle("-fx-font-weight: bold;");
                        text.setFont(Font.font(
                                text.getFont().getFamily(),
                                FontWeight.BOLD,
                                text.getFont().getSize()
                        ));
                    }
                    break;
                case "align":
                    if ("center".equals(kv[1].trim())) {
                        text.setTextAlignment(TextAlignment.CENTER);
                    }
                    break;
                case "valign":
                    if ("center".equals(kv[1].trim())) {
                        text.setTextAlignment(TextAlignment.CENTER);
                    }
                    break;
            }
        }
    }
    private  void processLineElement(Element line, Pane parent) {
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
    private  final Map<String, String> DATA = Map.of(
            "qrcode", "0000\n2320\n0025\n6448\n9759\n0010",
            "sn", "SN-2023-001"
    );

    public  void renderNodeToGraphics2D(Node node, Graphics2D g2d, int width, int height) {
        // 1. 创建 JavaFX 可写图像
        // 创建透明背景的图像
        WritableImage image = new WritableImage(width, height);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        // 执行快照
        image = node.snapshot(params, image);

        // 转换为精确尺寸的BufferedImage
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = bufferedImage.createGraphics();
        bg.setComposite(AlphaComposite.Clear);
        bg.fillRect(0, 0, width, height);
        bg.setComposite(AlphaComposite.SrcOver);
        SwingFXUtils.fromFXImage(image, bufferedImage);

        // 直接绘制到目标Graphics2D
        g2d.drawImage(bufferedImage, 0, 0, width, height, null);
        bg.dispose();
    }
    // 毫米转点(1英寸=25.4毫米, 1英寸=72点)
  
    public static void main(String[] args) {
        Node node = new PrintTest().parseXmlToNode();
        saveNodeAsImage(node, "456.png");
    }
    // 必须有一个公共无参构造器
    public PrintTest() {
        // 初始化代码
    }
}

// ... 其他代码 ...
// 新建一个单独的JavaFX应用类
