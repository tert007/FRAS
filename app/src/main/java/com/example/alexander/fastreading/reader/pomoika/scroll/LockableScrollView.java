package com.example.alexander.fastreading.reader.pomoika.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Alexander on 15.08.2016.
 */
public class LockableScrollView extends ScrollView {

    private ScrollChanged scrollChanged;

    public void setScrollChanged(ScrollChanged scrollChanged) {
        this.scrollChanged = scrollChanged;
    }

    private boolean scrollable = true;

    private boolean fullScroll = false;

    public LockableScrollView(Context context) {
        super(context);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setScrollingEnabled(boolean enabled) {
        scrollable = enabled;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public boolean isFullScroll() {
        View view = this.getChildAt(0);

        int difference = (view.getBottom() - (getHeight() + getScrollY()));

        if (difference <= 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        scrollChanged.onScrollChanged(t - oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollable)
                    return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollable; // scrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!scrollable)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }
}
