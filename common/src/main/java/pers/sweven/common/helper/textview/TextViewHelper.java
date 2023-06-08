package pers.sweven.common.helper.textview;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.LinkedList;

/**
 * Created by Sweven on 2021/11/4.
 * Email:sweventears@Foxmail.com
 * <p>多样式 TextView 拼接 [字体颜色,字体大小,背景色,下划线,删除线,文本点击事件]
 */
public class TextViewHelper {
    private final StringBuilder builder = new StringBuilder();
    private final LinkedList<Section> sections = new LinkedList<>();
    private TextView textView;

    public TextViewHelper() {
    }

    public TextViewHelper(TextView textView) {
        this.textView = textView;
    }

    /**
     * @param text  文本
     * @param style 对应文本的样式
     * @return 文本样式拼接
     */
    public TextViewHelper addText(CharSequence text, @NonNull TextViewStyle style) {
        if (text == null) {
            text = "";
        }
        int start = builder.length();
        int end = start + text.length();
        Section section = new Section(start, end, style);
        sections.add(section);
        builder.append(text);
        return this;
    }

    public TextViewHelper addText(CharSequence text) {
        if (text == null) {
            text = "";
        }
        int start = builder.length();
        int end = start + text.length();
        Section section = new Section(start, end, new TextViewStyle());
        sections.add(section);
        builder.append(text);
        return this;
    }


    public TextViewHelper addText(CharSequence text, @ColorInt int textColor) {
        if (text == null) {
            text = "";
        }
        int start = builder.length();
        int end = start + text.length();
        Section section = new Section(start, end, new TextViewStyle(textColor));
        sections.add(section);
        builder.append(text);
        return this;
    }

    public TextView build() {
        SpannableString spannableString = new SpannableString(builder.toString());
        for (Section section : sections) {
            addSpanner(spannableString, section);
        }
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
        if (textView == null) {
            throw new IllegalArgumentException("this textview must to be not null.");
        }
        textView.setText(spannableString);
        return textView;
    }

    public Spanned generateSpanner() {
        SpannableString spannableString = new SpannableString(builder.toString());
        for (Section section : sections) {
            addSpanner(spannableString, section);
        }
        return spannableString;
    }

    private void addSpanner(SpannableString spannableString, Section section) {
        int start = section.start;
        int end = section.end;
        TextViewStyle style = section.style;

        if (style.getTextColor() != 0) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(style.getTextColor());
            spannableString.setSpan(foregroundColorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        TextSize textSize = style.getTextSize();
        if (textSize != null) {
            if (textSize.isAbsoluteSize()) {
                AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(textSize.getSize(), textSize.isDip());
                spannableString.setSpan(absoluteSizeSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(textSize.getProportion());
                spannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        if (style.getBackground() != 0) {
            BackgroundColorSpan colorSpan = new BackgroundColorSpan(style.getBackground());
            spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (style.isUnderline()) {
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableString.setSpan(underlineSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (style.isStrikethrough()) {
            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            spannableString.setSpan(strikethroughSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (style.getClickableSpan() != null) {
            ClickableSpan clickableSpan = style.getClickableSpan();
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (style.getTypefaceSpan() != null) {
            spannableString.setSpan(style, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (style.getSpan() != null) {
            spannableString.setSpan(style.getSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    private static class Section {
        int start;
        int end;
        TextViewStyle style;

        private Section(int start, int end, TextViewStyle style) {
            this.start = start;
            this.end = end;
            this.style = style;
        }
    }
}
