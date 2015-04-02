package globallogic.yuriitsap.com.customscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yurii.tsap on 4/2/2015.
 */
public class HorizontalScrollView extends ViewGroup {

    private static final String TAG = "HorizontalScrollView";
    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mGestureListener;

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureListener = new OwnGestureListener();
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int maxChildHeight = parentHeight;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }
        setMeasuredDimension(parentWidth, maxChildHeight * 2);
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                return mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    private class OwnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            requestDisallowInterceptTouchEvent(true);
            int offset = (int) (e2.getX() - e2.getX() < 0 ? distanceX : -distanceX);
            if (!(intersectsLeftBorder(offset) || intersectsRightBorder(offset))) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    getChildAt(i).offsetLeftAndRight(offset);
                }
            }
            return true;
        }

        private boolean intersectsLeftBorder(int offset) {
            return getChildAt(0).getLeft() + offset > getPaddingLeft();
        }

        private boolean intersectsRightBorder(int offset) {
            return getChildAt(getChildCount() - 1).getRight() + offset
                    < (getRight() - getLeft()) - getPaddingRight();
        }
    }
}