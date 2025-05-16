package com.rookie.printonline.common;
import com.rookie.printonline.result.vo.PrintData;
import com.rookie.printonline.result.vo.QrPrintTemplate;
import com.rookie.printonline.util.PrintController;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        try {
            // 准备打印数据
            PrintData data = new PrintData();
            data.setQrcode("https://example.com/product/12345");
            data.setSn("0000\n2320\n0025\n6448\n9759\n0010");

            // 生成并打印标签
            PrintController controller = new PrintController();
            controller.generateAndPrintLabel(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
