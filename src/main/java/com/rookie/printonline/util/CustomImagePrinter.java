package com.rookie.printonline.util;


import java.awt.*;
import java.awt.print.*;
import java.io.File;
import javax.imageio.ImageIO;

public class CustomImagePrinter implements Printable {
    private String imagePath;

    public CustomImagePrinter(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        try {
            // 加载图片
            Image image = ImageIO.read(new File(imagePath));
            if (image == null) {
                System.err.println("无法加载图片: " + imagePath);
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;

            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            // 计算可打印区域
            double pageWidth = pageFormat.getImageableWidth();
            double pageHeight = pageFormat.getImageableHeight();
            double imageRatio = (double)image.getWidth(null) / image.getHeight(null);

            // 计算最佳缩放比例，确保图片适应可打印区域
            double scale = Math.min(
                    pageWidth / image.getWidth(null),
                    pageHeight / image.getHeight(null)
            );

            // 计算居中位置
            double x = pageFormat.getImageableX() + (pageWidth - (image.getWidth(null) * scale)) / 2;
            double y = pageFormat.getImageableY() + (pageHeight - (image.getHeight(null) * scale)) / 2;

            // 绘制图片
            g2d.drawImage(image, (int)x, (int)y,
                    (int)(image.getWidth(null) * scale),
                    (int)(image.getHeight(null) * scale), null);

            return PAGE_EXISTS;

        } catch (Exception e) {
            e.printStackTrace();
            return NO_SUCH_PAGE;
        }
    }

    public static void main(String[] args) {
        try {
            // 创建自定义页面格式
            PrinterJob job = PrinterJob.getPrinterJob();

            // 获取默认页面格式并调整
            PageFormat format = job.defaultPage();
            Paper paper = format.getPaper();

            // 设置更大的可打印区域（减小边距）
            paper.setImageableArea(
                    10,  // 左边距 (1/4英寸)
                    10,  // 上边距 (1/4英寸)
                    paper.getWidth() - 20,  // 可打印宽度
                    paper.getHeight() - 20  // 可打印高度
            );
            format.setPaper(paper);

            // 设置打印内容
            job.setPrintable(new CustomImagePrinter("D://123.png"), format);

            // 显示打印对话框
            if (job.printDialog()) {
                job.print();
                System.out.println("打印任务已提交");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("打印失败: " + e.getMessage());
        }
    }
}