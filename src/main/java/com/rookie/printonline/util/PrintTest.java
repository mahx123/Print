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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

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
        System.out.println("pageIndex=" + pageIndex);
        Component c = null;
        // 转换成Graphics2D
        Graphics2D g2 = (Graphics2D) gra;
        // 设置打印颜色为黑色
        g2.setColor(Color.black);

        // 打印起点坐标
        double x = pf.getImageableX();
        double y = pf.getImageableY();

        switch (pageIndex) {
            case 0:
                System.out.println("x=" + x);
                Image img = null;
                // img = Toolkit.getDefaultToolkit().getImage("/a.jpg");
                FileInputStream is;
                try {
                    is = new FileInputStream("D://image.png");
                    BufferedImage imgs = ImageIO.read(is);
                    img = imgs;
                    is.close();
                    imgs.flush();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                g2.drawImage(img, (int) x, (int) y, c);
                img.flush();
                return PAGE_EXISTS;
            default:
                return NO_SUCH_PAGE;
        }

    }

    public static void main(String[] args) {

        // 通俗理解就是书、文档
        Book book = new Book(); // 设置成竖打
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT); // 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
        Paper p = new Paper();
        p.setSize(120, 83);
        p.setImageableArea(20, 12, 120, 83);
        pf.setPaper(p);
        // 把PageFormat 和 Printable 添加到书中，组成一个页面
        book.append(new PrintTest(), pf);
        // 获取打印服务对象
        PrinterJob job = PrinterJob.getPrinterJob(); // 设置打印类
        job.setPageable(book);

        try { // 可以用printDialog显示打印对话框，在用户确认后打印；也可以直接打印
            boolean a = job.printDialog();
            if (a) {
                job.print();
            }
        } catch (PrinterException e) {
            e.printStackTrace(); } }

}