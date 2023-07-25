package pers.sweven.common.helper.textview;

import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.ColorInt;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sweven on 2021/11/4.
 * Email:sweventears@Foxmail.com
 */
public class TextViewStyle {


    private int textColor;
    private TextSize textSize;
    private int background;
    private boolean underline;
    private boolean strikethrough;
    private ClickableSpan clickableSpan;
    private TypefaceSpan typefaceSpan;
    private Object span;
    private List<CharacterStyle> styles;

    public TextViewStyle() {
    }

    public TextViewStyle(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    public TextViewStyle(float textSizeProportion) {
        this.textSize = new TextSize(textSizeProportion);
    }

    public TextViewStyle(int textColor, float textSizeProportion) {
        this.textColor = textColor;
        this.textSize = new TextSize(textSizeProportion);
    }

    public TextViewStyle(int textColor, float textSizeProportion, boolean strikethrough) {
        this.textColor = textColor;
        this.textSize = new TextSize(textSizeProportion);
        this.strikethrough = strikethrough;
    }

    public TextViewStyle(int textColor, TextSize textSize, int background, boolean underline, boolean strikethrough, ClickableSpan clickableSpan, TypefaceSpan typefaceSpan, Object span) {
        this.textColor = textColor;
        this.textSize = textSize;
        this.background = background;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.clickableSpan = clickableSpan;
        this.typefaceSpan = typefaceSpan;
        this.span = span;
    }

    public int getTextColor() {
        return textColor;
    }

    public TextViewStyle setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TextSize getTextSize() {
        return textSize;
    }

    public TextViewStyle setTextSize(TextSize textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getBackground() {
        return background;
    }

    public TextViewStyle setBackground(int background) {
        this.background = background;
        return this;
    }

    public boolean isUnderline() {
        return underline;
    }

    public TextViewStyle setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public TextViewStyle setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public ClickableSpan getClickableSpan() {
        return clickableSpan;
    }

    public TextViewStyle setClickableSpan(ClickableSpan clickableSpan) {
        this.clickableSpan = clickableSpan;
        return this;
    }

    public TypefaceSpan getTypefaceSpan() {
        return typefaceSpan;
    }

    public TextViewStyle setTypefaceSpan(TypefaceSpan typefaceSpan) {
        this.typefaceSpan = typefaceSpan;
        return this;
    }

    public Object getSpan() {
        return span;
    }

    public TextViewStyle setSpan(Object span) {
        this.span = span;
        return this;
    }

    public List<CharacterStyle> getStyles() {
        return styles;
    }

    public TextViewStyle setStyles(CharacterStyle... styles) {
        this.styles = Arrays.asList(styles);
        return this;
    }
}
