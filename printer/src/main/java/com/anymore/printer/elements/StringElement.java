package com.anymore.printer.elements;

/**
 * 文本元素
 * Created by liuyuanmao on 2019/6/22.
 */
public class StringElement implements Element<String>{

    private String content;

    private Rule rule;

    public StringElement(String content) {
        this(content,Rule.TEXT_ALIGN_LEFT);
    }

    public StringElement(String content, Rule rule) {
        this.content = content;
        this.rule = rule;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "StringElement{" + "content='" + content + '\'' + ", rule=" + rule + '}';
    }
}
