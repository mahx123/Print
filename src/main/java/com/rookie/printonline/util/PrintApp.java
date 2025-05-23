package com.rookie.printonline.util;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

public class PrintApp {
    public static void printDirectly(List<String> barCodes,String printerName)throws RuntimeException {
        // 设置自定义纸张大小(100mm x 30mm)
        double widthMM = 100;
        double heightMM = 32;
        double marginMM = 0;

        // 创建页面格式
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);

        // 设置纸张
        Paper paper = new Paper();
        paper.setSize(mmToPoints(widthMM), mmToPoints(heightMM));
        paper.setImageableArea(
                mmToPoints(marginMM)-20,
                mmToPoints(marginMM-20),
                mmToPoints(widthMM - 2 * marginMM),
                mmToPoints(heightMM - 2 * marginMM)
        );
        pf.setPaper(paper);


        // 创建打印作业
        PrinterJob job = PrinterJob.getPrinterJob();
        // 创建打印作业
        // 获取指定打印机


        if(!printerName.contains("默认")){
            PrintService printService = getPrintServiceByName(printerName);
            if (printService == null) {
                System.err.println("未找到指定的打印机: " + printerName);
                throw new RuntimeException("未找到指定的打印机: " + printerName);
                //  return;
            }
            try {
                job.setPrintService(printService);
            } catch (PrinterException e) {
                throw new RuntimeException(e);
            }
        }

        int ct=0;
        for (String barCode : barCodes) {
            ct++;
            job.setPrintable(new PrintTest(barCode,ct), pf);

            try {
                // 直接打印（跳过对话框）
                System.out.println("开始打印...");
                job.print();
                System.out.println("打印完成");
            } catch (Exception e) {
                System.err.println("打印失败: " + e.getMessage());
            }
        }

    }
    private static PrintService getPrintServiceByName(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
    private static double mmToPoints(double mm) {
        return mm / 25.4 * 72;
    }
}