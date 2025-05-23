package com.rookie.printonline.util;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class PrintCase implements Printable {
    /**
     * @param Graphic指明打印的图形环境
     * @param PageFormat指明打印页格式 （页面大小以点为计量单位，1点为1英才的1/72，1英寸为25.4毫米。A4纸大致为595×842点）
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
            BufferedImage img = ImageIO.read(new File("D:\\work_space\\Print\\456.png"));

            // 计算缩放比例
            double scale = Math.min(
                    pf.getImageableWidth() / img.getWidth(),
                    pf.getImageableHeight() / img.getHeight()
            );

            // 应用变换
            g2.translate(pf.getImageableX(), pf.getImageableY());
            g2.scale(scale, scale);

            // 绘制图片
            g2.drawImage(img, 0, 0, null);

            return PAGE_EXISTS;
        } catch (IOException e) {
            e.printStackTrace();
            return NO_SUCH_PAGE;
        }

    }

    // 毫米转点(1英寸=25.4毫米, 1英寸=72点)
    private static double mmToPoints(double mm) {
        return mm / 25.4 * 72;
    }

    public static void main(String[] args) {
        // 设置自定义纸张大小(100mm x 30mm)
        double widthMM = 100;
        double heightMM = 60;
        double marginMM = 2; // 2mm边距
        List<String> barcodeList = new ArrayList<>();
        // 创建页面格式
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);

        // 设置纸张
        Paper paper = new Paper();
        paper.setSize(mmToPoints(widthMM), mmToPoints(heightMM));
        paper.setImageableArea(
                mmToPoints(marginMM),
                mmToPoints(marginMM)-40,
                mmToPoints(widthMM - 2 * marginMM),
                mmToPoints(heightMM - 2 * marginMM)+40
        );
        pf.setPaper(paper);

        // 创建打印作业
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new PrintTest(barcodeList), pf);

        try {
            // 显示打印对话框
            if (job.printDialog()) {
                // 重要：设置自定义纸张大小
                job.setPrintable(new PrintTest(barcodeList), pf);

                System.out.println("开始打印...");
                job.print();
                System.out.println("打印完成");
            }
        } catch (PrinterException e) {
            System.err.println("打印失败: " + e.getMessage());
        }
    }

}