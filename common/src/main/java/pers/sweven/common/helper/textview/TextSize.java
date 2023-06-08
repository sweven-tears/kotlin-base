package pers.sweven.common.helper.textview;

import androidx.annotation.FloatRange;

/**
 * Created by Sweven on 2021/11/4.
 * Email:sweventears@Foxmail.com
 */
public class TextSize {
    private boolean absoluteSize;
    private int size;
    private float proportion;
    private boolean dip;

    public TextSize(int size) {
        this(size, false);
    }

    public TextSize(int size, boolean dip) {
        this.absoluteSize = true;
        this.size = size;
        this.dip = dip;
    }

    public TextSize(@FloatRange(from = 0) float proportion) {
        this.absoluteSize = false;
        this.proportion = proportion;
    }

    public boolean isAbsoluteSize() {
        return absoluteSize;
    }

    public void setAbsoluteSize(boolean absoluteSize) {
        this.absoluteSize = absoluteSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getProportion() {
        return proportion;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }

    public boolean isDip() {
        return dip;
    }

    public void setDip(boolean dip) {
        this.dip = dip;
    }
}
