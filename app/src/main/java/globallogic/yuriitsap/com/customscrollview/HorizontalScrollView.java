package globallogic.yuriitsap.com.customscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by yurii.tsap on 4/2/2015.
 */
public class HorizontalScrollView extends ViewGroup {

    private int OPPOSITE_MASK = 0b11111111_11111111_11111111_11111110;
    private static final String TAG = "HorizontalScrollView";
    private GestureDetector mGestureDetector;
    private Scroller mScroller;

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new OwnGestureListener());
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
        private int firstPoint;

        @Override
        public boolean onDown(MotionEvent e) {
            parentInterceptionAllowed = true;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            disableVerticalScrolling();
            scrollBy((int) distanceX ^ OPPOSITE_MASK, 0);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling((int) e2.getX(), 0, (int) velocityX, 0, getChildAt(0).getLeft(),
                    getChildAt(getChildCount() - 1).getRight(), 0, 0);
            firstPoint = (int) e2.getX();
            post(new Runnable() {
                @Override
                public void run() {
                    if (mScroller.computeScrollOffset()) {
                        scrollBy(mScroller.getCurrX() - firstPoint, 0);
                        firstPoint = mScroller.getCurrX();
                        postDelayed(this, 50);

                    }
                }
            });
            return true;
        }

        private void scrollBy(int offsetX, int offsetY) {
            if (offsetY == 0 && !handleFastScrolling(offsetX)) {
                scrollHorizontaly(offsetX);
            }
        }

        private void scrollHorizontaly(int offset) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                getChildAt(i).offsetLeftAndRight(offset);
            }
            invalidate();
        }

        private boolean handleFastScrollingLeftDirection(int offset) {
            if (getChildAt(0).getLeft() + offset > getPaddingLeft()) {
                scrollHorizontaly(getChildAt(0).getLeft() ^ OPPOSITE_MASK);
                return true;
            }
            return false;
        }

        private boolean handleFastScrollingRightDirection(int offset) {
            if (getChildAt(getChildCount() - 1).getRight() + offset < getRight()) {
                scrollHorizontaly(getRight() - getChildAt(getChildCount() - 1).getRight());
                return true;
            }
            return false;
        }

        private boolean handleFastScrolling(int offset) {
            return handleFastScrollingLeftDirection(offset) || handleFastScrollingRightDirection(
                    offset);
        }

        private void disableVerticalScrolling() {
            if (parentInterceptionAllowed) {
                requestDisallowInterceptTouchEvent(true);
                parentInterceptionAllowed = false;
            }
        }
    }
}