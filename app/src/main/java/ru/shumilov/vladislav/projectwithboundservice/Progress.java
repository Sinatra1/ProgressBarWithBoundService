package ru.shumilov.vladislav.projectwithboundservice;

import java.util.Observable;

public class Progress extends Observable {
    private Integer mPercent;
    private int mValue;

    public Integer getPercent() {
        return mPercent;
    }

    public void setPercent(int percent) {
        mPercent = percent;
        setChanged();
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }
}
