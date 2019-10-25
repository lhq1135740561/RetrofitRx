package com.ly.masterviewmodelretrofitrxjava.event.base;

/**
 * Create by Allen Liu at 2019/2/19 10:35.
 */
public class BaseEvent {
    private int mAction;

    public BaseEvent(int action) {
        mAction = action;
    }

    public int getAction() {
        return mAction;
    }
}
