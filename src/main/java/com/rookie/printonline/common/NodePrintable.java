package com.rookie.printonline.common;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;


public  class NodePrintable implements Printable {
    private final Image image;

    public NodePrintable(Image image) {
        this.image = image;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        // 转换为 Java2D 的 Graphics2D
        Graphics2D g2d = (Graphics2D) graphics;

        // 绘制图像到打印上下文
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        g2d.drawImage(bufferedImage, 0, 0, null);

        return Printable.PAGE_EXISTS;
    }
}
