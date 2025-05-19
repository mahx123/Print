package com.rookie.printonline.common;

public class CustomPaper {
    private String name;
    private double width; // 单位：毫米
    private double height; // 单位：毫米

    public CustomPaper(String name, double width, double height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
