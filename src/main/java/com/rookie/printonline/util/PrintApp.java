package com.rookie.printonline.util;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrintApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 设置自定义纸张大小(100mm x 30mm)
        double widthMM = 80;
        double heightMM = 30;
        double marginMM = 2; // 2mm边距

        // 创建页面格式
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);

        // 设置纸张
        Paper paper = new Paper();
        paper.setSize(mmToPoints(widthMM), mmToPoints(heightMM));
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
            // 显示打印对话框
            if (job.printDialog()) {
                job.setPrintable(new PrintTest(), pf);
                System.out.println("开始打印...");
                job.print();
                System.out.println("打印完成");
            }
        } catch (PrinterException e) {
            System.err.println("打印失败: " + e.getMessage());
        } finally {
            Platform.exit(); // 打印完成后退出应用
        }
    }

    private static double mmToPoints(double mm) {
        return mm / 25.4 * 203;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
