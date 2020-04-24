package com.anymore.printer.elements;

/**
 * 空行
 * Created by liuyuanmao on 2019/6/22.
 */
public class EmptyLines {

    private int lines;//空行数目

    public EmptyLines(int lines) {
        this.lines = lines;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "EmptyLines{" + "lines=" + lines + '}';
    }
}
