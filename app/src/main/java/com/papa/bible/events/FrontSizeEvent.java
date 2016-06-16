package com.papa.bible.events;

/**
 * Created by Papa on 2016/5/25.
 */
public class FrontSizeEvent {
    private int mSize;

    public FrontSizeEvent(int size) {

        mSize = size;
    }

    public int getSize() {
        return mSize;
    }
}
