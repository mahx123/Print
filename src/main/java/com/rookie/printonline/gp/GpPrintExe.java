package com.rookie.printonline.gp;

import com.rookie.printonline.dto.TemplateData;
import com.rookie.printonline.sdk.GbLibDll;
import com.rookie.printonline.util.GpXmlParseUtils;

import java.nio.charset.StandardCharsets;

/**
 *@Title: 佳博打印程序初始化
 *@Package: com.rookie.printonline.gp
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/20 16:26
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public class GpPrintExe {
    
    static {
        System.setProperty("jna.encoding", "GBK");
        byte status = GbLibDll.INSTANCE.usbportqueryprinter();
        System.out.println(status);
        GbLibDll.INSTANCE.openport("Gprinter GP-1324D");
        //配置打印机（设定卷标纸宽度为60mm，高度为40mm，此为实际纸卷宽高）
        GbLibDll.INSTANCE.sendcommand("SIZE 100 mm, 30 mm");
        //配置打印机（设定卷标纸间隔为2mm）
        GbLibDll.INSTANCE.sendcommand("GAP 0 mm");
        //配置打印机（设定打印机速度为4）
        GbLibDll.INSTANCE.sendcommand("SPEED 4");
        //配置打印机（设定打印浓度为12，可选0~15）
        GbLibDll.INSTANCE.sendcommand("DENSITY 8");
        //配置打印机（设定打印方向，可选0或1或00、11、10、01）
        GbLibDll.INSTANCE.sendcommand("DIRECTION 0");
        //配置打印机（设定启用/禁用撕纸位置走到撕纸处，可选on（启用）或off（禁用））
        GbLibDll.INSTANCE.sendcommand("SET TEAR ON");
        //配置打印机（设定对应国际代码页为 UTF-8，其他参数可参照TSPL指令）
        GbLibDll.INSTANCE.sendcommand("CODEPAGE UTF-8");

       GbLibDll.INSTANCE.sendcommand("REFERENCE 10,0");
        // 3. 初始化GP指令
//        gpCommands.append("SIZE ").append(width).append(" mm,").append(height).append(" mm\n");
//        gpCommands.append("GAP 2 mm,0\n"); // 假设使用2mm的间隙
//        gpCommands.append("CLS\n");
//        gpCommands.append("DENSITY 8\n"); // 中等打印浓度
//        gpCommands.append("SPEED 3\n"); // 中等打印速度
//        gpCommands.append("DIRECTION 0\n"); // 打印方向
//        gpCommands.append("REFERENCE 0,0\n"); // 参考坐标原点
    }



    public static  void testCommand(){
        //清楚缓存数据（等同于CLS）
        GbLibDll.INSTANCE.clearbuffer();
        byte[] result_unicode = new byte[1024];//声明byte数组
        String word_unicode = "[宋] 欧阳修";//声明文本内容
        result_unicode = word_unicode.getBytes(StandardCharsets.UTF_16LE);//放入数组
        //utf-8 format（UTF-8格式）
        byte[] result_utf8 = new byte[1024];
        String word_utf8 = "TEXT 40,20,\"TSS16.BF2\",0,1,1,\"utf8 test Wörter auf Deutsch\"";
        result_utf8 = word_utf8.getBytes(StandardCharsets.UTF_8);
        //打印指令，打印PCX文件
        GbLibDll.INSTANCE.sendcommand("PUTPCX 40,40,\"UL.PCX\"");
        GbLibDll.INSTANCE.sendcommand("PUTBMP 40,40,\"GPRINTER.BMP\"");

        //使用Windows TTF字型打印文字（x坐标，y坐标，字体高度，旋转角度，字体外形，下划线，字体名称，内容）
        GbLibDll.INSTANCE.windowsfont(160, 20, 48, 0, 0, 0, "Arial", "Hello World");
        //打印文本内容
        GbLibDll.INSTANCE.printerfont("136", "80", "TSS24.BF2", "0", "0", "0", "蝶恋花·庭院深深深几许");
        //使用Windows TTF字型打印固定长度的Unicode文字（x坐标，y坐标，字体高度，旋转角度，字体外形，下划线，字体名称，内容，长度）
        GbLibDll.INSTANCE.windowsfontUnicodeLengh(224, 110, 24, 0, 0, 0, "Arial", result_unicode,word_unicode.length());
//
////        //打印binary（二进制）文件
        GbLibDll.INSTANCE.sendBinaryData(result_utf8, result_utf8.length);
//
//        //打印条码
////        TscLibDll.INSTANCE.barcode("140", "200", "128", "80", "0", "0", "2", "2", "2023040130309");
//        //打印二维码（x坐标，y坐标，纠错等级，二维码宽度，手动/自动编码，旋转角度，条码内容）
        GbLibDll.INSTANCE.sendcommand("QRCODE 160,150,M,5,A,0,\"https://baike.baidu.com/item/%E8%9D%B6%E6%81%8B%E8%8A%B1%C2%B7%E5%BA%AD%E9%99%A2%E6%B7%B1%E6%B7%B1%E6%B7%B1%E5%87%A0%E8%AE%B8/3048988?fr=aladdin\"");
//        //打印指令（1式1份）
        GbLibDll.INSTANCE.printlabel("1", "1");
//        //断开端口
        GbLibDll.INSTANCE.closeport();

    }


    public static void printByXmlTemplate(String cmd){

        TemplateData data = new TemplateData("0000\n2320\n0025\n6448\n9759\n0010", "SN987654321");
       //TemplateData data = new TemplateData("QR123456789", "SN987654321");
//        // 2. 创建转换器
        GpXmlParseUtils converter = new GpXmlParseUtils(data);
        GbLibDll.INSTANCE.clearbuffer();
        // 3. 转换XML为GP指令
        try {


            GbLibDll.INSTANCE.sendcommand(cmd);
            GbLibDll.INSTANCE.closeport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void printByTemplate(){

        //TemplateData data = new TemplateData("0000\n2320\n0025\n6448\n9759\n0010", "SN987654321");
        TemplateData data = new TemplateData("QR123456789", "SN987654321");
//        // 2. 创建转换器
     //   GpXmlParseUtils converter = new GpXmlParseUtils(data);
        GbLibDll.INSTANCE.clearbuffer();
        // 3. 转换XML为GP指令
        try {

            String s = new GpXmlParseUtils(data).convertToGPCommands("D:\\xml/QR_Print_Template_02.xml");
            GbLibDll.INSTANCE.sendcommand(s);
            GbLibDll.INSTANCE.closeport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {


        printByTemplate();
    }
}
