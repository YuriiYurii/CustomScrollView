package globallogic.yuriitsap.com.customscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by yurii.tsap on 4/2/2015.
 */
public class HorizontalScrollView extends ViewGroup {

    private static final String TAG = "HorizontalScrollView";
    private GestureDetector mGestureDetector;
    private int mMaxChildIndex = 0;
    private Scroller mScroller;

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new OwnGestureListener());
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            if (parentHeight < child.getMeasuredHeight()) {
                parentHeight = child.getMeasuredHeight();
                mMaxChildIndex = i;
            }
        }
        setMeasuredDimension(parentWidth, parentHeight * 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int currentLeft = getPaddingLeft();
        int currentTop = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int currentBottom = currentTop + child.getMeasuredHeight();
            int currentRight = currentLeft + child.getMeasuredWidth();
            child.layout(currentLeft, currentTop, currentRight,
                    currentBottom);
            currentLeft = currentRight;
        }
    }

    private void showEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN x = " + event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE x = " + event.getX());
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP x = " + event.getX());
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_UP x = " + event.getX());
                break;

        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class OwnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean parentInterceptionAllowed;

        @Override
        public boolean onDown(MotionEvent e) {
            parentInterceptionAllowed = true;
            return false;
        }

        private void disableVerticalScrolling() {
            if (parentInterceptionAllowed) {
                requestDisallowInterceptTouchEvent(true);
                parentInterceptionAllowed = false;
            }

        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            disableVerticalScrolling();
            if (canScroll(-distanceX)) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    getChildAt(i).offsetLeftAndRight((int) -distanceX);
                }
            }
            Log.e(TAG,"Last child X = "+getChildAt(getChildCount()-1).getRight());
            invalidate();
            return true;
        }

        private boolean leftBorderIsReached() {
            return getChildAt(0).getLeft() == getPaddingLeft();
        }

        private boolean rightBorderIsReached() {
            return getChildAt(getChildCount() - 1).getRight()
                    == (getRight() - getLeft()) - getPaddingRight();
        }

        private void scrollToEnd() {
            if (!rightBorderIsReached()) {
                Log.e(TAG, "scrollToEnd");
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    getChildAt(i).offsetLeftAndRight(
                            getPaddingRight() - getChildAt(getChildCount() - 1).getRight());
                }
            }


        }

        private void scrollToStart() {
            if (!leftBorderIsReached()) {
                Log.e(TAG, "scrollToStart");
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    getChildAt(i).offsetLeftAndRight(Math.abs(getChildAt(0).getLeft()));
                }
            }
        }


        private boolean canScroll(float offset) {

            return getChildAt(0).getLeft() + offset < getPaddingLeft()
                    && getChildAt(getChildCount() - 1).getRight() + offset
                    > (getRight() - getLeft()) - getPaddingRight();
        }


        @Deprecated
        private boolean touchInChild(float x, float y) {
            return ((x > getChildAt(0).getLeft() && x < getChildAt(getChildCount() - 1).getRight())
                    && y > getChildAt(mMaxChildIndex).getTop() && y < getChildAt(mMaxChildIndex)
                    .getBottom());
        }
    }
}