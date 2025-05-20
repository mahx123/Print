package com.rookie.printonline.sdk;
import com.sun.jna.Library;
import com.sun.jna.Native;
/**
 *@Title: 佳博打印机函数库
 *@Package: com.rookie.printonline.sdk
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/20 16:18
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public interface GbLibDll {
    GbLibDll INSTANCE = (GbLibDll) Native.loadLibrary("\\TSCLIB", GbLibDll.class);
    int about ();
    //指定计算机端的输出端口（1.单机打印时，请指定打印机驱动程序名称，例如: TSC CLEVER TTP-243  2.若连接打印机服务器，请指定服务器路径及共享打印机名称，例如: \\SERVER\TTP243  3.USB）
    int openport (String pirnterName);
    //关闭指定电脑端输出端口
    int closeport ();
    //配置打印机指令
    int sendcommand (String printerCommand);
    //发送Binary（二进制）文件（文件数据【data】，文件长度）
    int sendBinaryData (byte[] printerCommand, int CommandLength);
    //设定卷标的参数（宽度、高度、打印速度、打印浓度、感应器类别、gap/black mark 垂直间距、gap/black mark 偏移距离)
    int setup (String width,String height,String speed,String density,String sensor,String vertical,String offset);
    //下载单色PCX格式图文件至打印机（文件名【可含路径】，下载至打印机内的文件名称）
    int downloadpcx (String filename,String image_name);
    //打印条码（x坐标，y坐标，条码高度，条码内容，旋转角度，窄线条宽度，宽线条宽度，条码内容）
    int barcode (String x,String y,String type,String height,String readable,String rotation,String narrow,String wide,String code);
    //文字打印（x坐标，y做表，字体名称，旋转角度，x方向放大，y方向放大，内容）
    int printerfont (String x,String y,String fonttype,String rotation,String xmul,String ymul,String text);
    //清除缓存
    int clearbuffer ();
    //打印卷标内容（打印卷标式数，打印份数）
    int printlabel (String set, String copy);
    //使用Windows TTF字型打印文字（x坐标，y坐标，字体高度，旋转角度，字体外形，下划线，字体名称，内容）
    int windowsfont (int x, int y, int fontheight, int rotation, int fontstyle, int fontunderline, String szFaceName, String content);
    //使用Windows TTF字型打印Unicode文字（x坐标，y坐标，字体高度，旋转角度，字体外形，下划线，字体名称，内容）
    int windowsfontUnicode(int x, int y, int fontheight, int rotation, int fontstyle, int fontunderline, String szFaceName, byte[] content);
    //使用Windows TTF字型打印固定长度的Unicode文字（x坐标，y坐标，字体高度，旋转角度，字体外形，下划线，字体名称，内容，长度）
    int windowsfontUnicodeLengh(int x, int y, int fontheight, int rotation, int fontstyle, int fontunderline, String szFaceName, byte[] content, int length);
    //从USB端口回传打印机状态，即tspl指令 <ESC>!?
    byte usbportqueryprinter();
}
