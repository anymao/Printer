package com.anymore.printer.elements;

/**
 * 空行元素
 * Created by liuyuanmao on 2019/6/22.
 */
public class EmptyLinesElement implements Element<EmptyLines> {

    private EmptyLines emptyLines;

    public EmptyLinesElement(EmptyLines emptyLine) {
        this.emptyLines = emptyLine;
    }

    @Override
    public Rule getRule() {
        //空行不需要规则
        return null;
    }

    @Override
    public EmptyLines getContent() {
        return emptyLines;
    }

    @Override
    public String toString() {
        return "EmptyLinesElement{" + "emptyLines=" + emptyLines + '}';
    }
}
