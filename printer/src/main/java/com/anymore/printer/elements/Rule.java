package com.anymore.printer.elements;

import android.support.annotation.IntRange;
import com.anymore.printer.annotations.PrintAlign;
import com.anymore.printer.constansts.PrintConstants;

/**
 * 打印规则
 * Created by liuyuanmao on 2019/6/21.
 */
public class Rule {

    //常用文本规则
    public static final Rule TEXT_ALIGN_LEFT = new Rule(PrintConstants.PRINT_ALIGN_LEFT, 0, 0, 28,
            0, 0);
    //居中条形码规则
    public static final Rule BARCODE_ALIGN_CENTER = new Rule(PrintConstants.PRINT_ALIGN_CENTER, 0,
            0, 28, 0, 0);
    //居左Bitmap规则
    public static final Rule BITMAP_ALIGN_LEFT = new Rule(PrintConstants.PRINT_ALIGN_LEFT, 0, 0, 28,
            0, 0);

    @PrintAlign
    private int printAlign;

    @IntRange(from = 0, to = 7)
    private int widthScale;

    @IntRange(from = 0, to = 7)
    private int heightScale;

    private int lineHeight;

    private int marginLeft;

    private int marginRight;

    Rule(int printAlign, int widthScale, int heightScale, int lineHeight, int marginLeft,
            int marginRight) {
        this.printAlign = printAlign;
        this.widthScale = widthScale;
        this.heightScale = heightScale;
        this.lineHeight = lineHeight;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    public int getPrintAlign() {
        return printAlign;
    }

    public int getWidthScale() {
        return widthScale;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    @Override
    public String toString() {
        return "Rule{" + "printAlign=" + printAlign + ", widthScale=" + widthScale
                + ", heightScale=" + heightScale + ", lineHeight=" + lineHeight + ", marginLeft="
                + marginLeft + ", marginRight=" + marginRight + '}';
    }

    public static class Builder {

        @PrintAlign
        private int printAlign;

        @IntRange(from = 0, to = 7)
        private int widthScale;

        @IntRange(from = 0, to = 7)
        private int heightScale;

        private int lineHeight;

        private int marginLeft;

        private int marginRight;

        public Builder() {
            printAlign = PrintConstants.PRINT_ALIGN_LEFT;
            widthScale = 0;
            heightScale = 0;
        }

        public Builder setPrintAlign(int printAlign) {
            this.printAlign = printAlign;
            return this;
        }

        public Builder setWidthScale(@IntRange(from = 0,to = 7) int widthScale) {
            this.widthScale = widthScale;
            return this;
        }

        public Builder setHeightScale(@IntRange(from = 0,to = 7) int heightScale) {
            this.heightScale = heightScale;
            return this;
        }

        public Builder setLineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public Builder setMarginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setMarginRight(int marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public Rule build() {
            return new Rule(printAlign, widthScale, heightScale, lineHeight, marginLeft,
                    marginRight);
        }
    }
}
