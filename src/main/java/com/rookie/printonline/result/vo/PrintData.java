package com.rookie.printonline.result.vo;

import java.util.List;

public class PrintData {
    private String qrcodeContent;  // 二维码内容
    private List<String> numbers;  // 右侧数字列表

    public PrintData(String qrcodeContent, List<String> numbers) {
        this.qrcodeContent = qrcodeContent;
        this.numbers = numbers;
    }

    public PrintData() {

    }

    // 生成右侧文本格式
    public String getRightSideText() {
        StringBuilder sb = new StringBuilder();
        // 第一组6行数字
        for (String num : numbers) {
            sb.append(num).append("\n");
        }
        // 中间的"1 折线"
        sb.append("1\n折线\n\n");
        // 第二组6行数字
        for (String num : numbers) {
            sb.append(num).append("\n");
        }
        return sb.toString();
    }

    // getters
    public String getQrcodeContent() { return qrcodeContent; }
    public List<String> getNumbers() { return numbers; }
}