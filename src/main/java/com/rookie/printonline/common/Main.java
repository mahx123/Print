package com.rookie.printonline.common;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.layout.StackPane;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.client.j2se.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception{


        // 使用DOM解析XML模板
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new File("D:\\vx_storage\\xwechat_files\\mahuaixiao_5123\\msg\\file\\2025-05\\QR_Print_Template_100_32_2.0.xml"));

// 创建JavaFX容器
        StackPane rootPane = new StackPane();
        rootPane.setPrefSize(100, 32);  // 单位：毫米

// 解析布局元素
        NodeList layouts = doc.getElementsByTagName("layout");
        for (int i = 0; i < layouts.getLength(); i++) {
            Element layout = (Element) layouts.item(i);
            double width = Double.parseDouble(layout.getAttribute("width"));
            double height = Double.parseDouble(layout.getAttribute("height"));
            double left = Double.parseDouble(layout.getAttribute("left"));
            double top = Double.parseDouble(layout.getAttribute("top"));

            // 构建文本节点
            if (layout.getElementsByTagName("text").getLength() > 0) {
                Text textNode = createTextNode(layout);
              //  rootPane.getChildren().add(textNode);
            }
            // 构建二维码节点
//            else if (layout.getElementsByTagName("barcode").getLength() > 0) {
//                ImageView qrNode = createQRNode(layout);
//                rootPane.getChildren().add(qrNode);
//            }
        }
    }

    // 打印分辨率转换（考虑300dpi标准）
    private double mmToPixel(double mm, double dpi) {
        return mm * dpi / 25.4;
    }

    // 创建文本节点方法示例
    private static Text createTextNode(Element layout) {
        Element text = (Element) layout.getElementsByTagName("text").item(0);
//        Text textNode = new Text(text.getTextContent());
//
//        // 样式映射（黑体/加粗/居中）
//        String fontFamily = text.getAttribute("fontFamily");
//        double fontSize = Double.parseDouble(text.getAttribute("fontSize"));
//        textNode.setFont(Font.font(fontFamily,  FontWeight.BOLD, fontSize));
//
//        // 精确定位
//        textNode.setTranslateX(mmToPixel(left,  300));
//        textNode.setTranslateY(mmToPixel(top,  300));
        return null;
    }
}