package com.rookie.printonline.util;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LabelPrinter implements Printable {

    private String imagePath;
    private String textContent;

    public LabelPrinter(String imagePath, String textContent) {
        this.imagePath = imagePath;
        this.textContent = textContent;
    }

    public void print() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        if (job.printDialog()) {
            job.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        try {
            // 打印二维码图片
            Image qrImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            g2d.drawImage(qrImage, 50, 50, 100, 100, null);

            // 打印文本内容
            g2d.setFont(new Font("SimHei", Font.BOLD, 12));
            String[] lines = textContent.split("\n");
            for (int i = 0; i < lines.length; i++) {
                g2d.drawString(lines[i], 160, 60 + i * 20);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return PAGE_EXISTS;
    }

    // 直接打印XML模板（替代方案）
    public static void printXmlTemplate(String xmlPath) throws PrintException {
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));

        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream(xmlPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (psStream == null) {
            return;
        }

        DocFlavor docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(psStream, docFlavor, null);

        PrintService[] services = PrintServiceLookup.lookupPrintServices(docFlavor, attributes);
        if (services.length > 0) {
            DocPrintJob job = services[0].createPrintJob();
            try {
                job.print(doc, attributes);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }

        try {
            psStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}