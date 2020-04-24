package com.anymore.printer.elements;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import com.android.print.sdk.Barcode;
import com.anymore.printer.annotations.PrintAlign;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 打印的文档对象
 * Created by liuyuanmao on 2019/6/21.
 */
public class Document {

    /**
     * 是否是标准模式？标准模式从先找到黑标再开始打印，
     * 非标准模式，直接从当前位置开始打印。
     */
    private boolean isStandardMode;
    /**打印元素*/
    private LinkedList<Element<?>> mElements;

    Document() {
        mElements = new LinkedList<>();
    }

    Document(boolean standardMode,@NonNull List<Element<?>> elements) {
        mElements = new LinkedList<>(elements);
        isStandardMode = standardMode;
    }


    public LinkedList<Element<?>> getElements() {
        return mElements;
    }

    public boolean isStandardMode() {
        return isStandardMode;
    }

    public Builder newBuilder() {
        return new Builder(isStandardMode,mElements);
    }

    @Override
    public String toString() {
        return "Document{" + "mElements=" + mElements + '}';
    }

    /**
     * Document建造者，默认构建一个标准文档对象(打印前会先寻黑标)
     */
    public static class Builder {

        private boolean isStandardMode;

        private LinkedList<Element<?>> mElements;

        Builder(boolean standardMode,Collection<Element<?>> elements) {
            isStandardMode = standardMode;
            mElements = new LinkedList<>(elements);
        }

        public Builder() {
            isStandardMode = true;
            mElements = new LinkedList<>();
        }

        /**
         * 设置文档是否是标准格式的文档，默认是标准格式文档(即打印之前先寻黑标)
         * @param standardMode
         * @return
         */
        public Builder setStandardMode(boolean standardMode) {
            isStandardMode = standardMode;
            return this;
        }

        /**
         * 添加空行
         * @param lines 空行数量
         * @return this
         */
        public Builder addEmptyLines(int lines) {
            mElements.add(new EmptyLinesElement(new EmptyLines(lines)));
            return this;
        }

        /**
         * 添加文字，默认的居左,规则是{@link Rule#TEXT_ALIGN_LEFT}
         * @param content 文本的内容
         * @return this
         */
        public Builder addText(String content) {
            mElements.add(new StringElement(content));
            return this;
        }

        /**
         * 添加自定义的排列方式的文字
         * @param content 文本内容
         * @param printAlign 排列布局
         * @return this
         */
        public Builder addText(String content, @PrintAlign int printAlign) {
            Rule rule = new Rule.Builder()
                    .setPrintAlign(printAlign)
                    .build();
            mElements.add(new StringElement(content, rule));
            return this;
        }

        /**
         * 添加自定义的排列方式的文字
         * @param content 文本内容
         * @param printAlign 排列布局
         * @param widthScale 宽度缩放
         * @param heightScale 高度缩放
         * @return this
         */
        public Builder addText(String content, @PrintAlign int printAlign,
                @IntRange(from = 0, to = 7) int widthScale,
                @IntRange(from = 0, to = 7) int heightScale) {
            Rule rule = new Rule.Builder()
                    .setPrintAlign(printAlign)
                    .setWidthScale(widthScale)
                    .setHeightScale(heightScale)
                    .build();
            mElements.add(new StringElement(content, rule));
            return this;
        }


        /**
         * 添加Bitmap元素，默认排列放在左边，规则是{@link Rule#BITMAP_ALIGN_LEFT}
         * @param bitmap 图片内容
         * @return this
         */
        public Builder addBitmap(Bitmap bitmap) {
            mElements.add(new BitmapElement(bitmap));
            return this;
        }

        /**
         * 添加一个默认的条码对象，规则是{@link Rule#BARCODE_ALIGN_CENTER}
         * @param barcode 条码对象
         * @return this
         */
        public Builder addBarcode(Barcode barcode) {
            mElements.add(new BarcodeElement(barcode));
            return this;
        }

        /**
         * 替换指定节点元素，场景是打印两联罚单的时候，就仅仅是两联的页脚不同
         * @param index 文档的第index个元素
         * @param element 要替换的元素
         * @return this
         */
        public Builder setElement(int index, Element<?> element) {
            mElements.set(index, element);
            return this;
        }

        /**
         * @return 文档的总节点数目
         */
        public int getLength() {
            return mElements.size();
        }

        /**
         * 添加自定义的节点类型
         * @param element 打印元素
         * @return this
         */
        public Builder addElement(Element<?> element) {
            mElements.add(element);
            return this;
        }

        /**
         * 构造对应的Document对象
         * @return document
         */
        public Document build() {
            return new Document(isStandardMode,mElements);
        }
    }
}
