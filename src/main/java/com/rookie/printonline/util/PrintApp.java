package com.rookie.printonline.util;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrintApp {
    public static void printDirectly() {
        // 设置自定义纸张大小(100mm x 30mm)
        double widthMM = 100;
        double heightMM = 40;
        double marginMM = 0;

        // 创建页面格式
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);

        // 设置纸张
        Paper paper = new Paper();
        paper.setSize(mmToPoints(widthMM), mmToPoints(heightMM / 3));
        paper.setImageableArea(
                mmToPoints(marginMM),
                mmToPoints(marginMM),
                mmToPoints(widthMM - 2 * marginMM),
                mmToPoints(heightMM - 2 * marginMM)
        );
        pf.setPaper(paper);

        // 创建打印作业
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new PrintTest(), pf);

        try {
            // 直接打印（跳过对话框）
            System.out.println("开始打印...");
            job.print();
            System.out.println("打印完成");
        } catch (Exception e) {
            System.err.println("打印失败: " + e.getMessage());
        }
    }

    private static double mmToPoints(double mm) {
        return mm / 25.4 * 72;
    }
}