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
    private Scroller mScroller;
    private Runnable mScrollWorker;
    private int mScrollX;
    private int mMaxX;

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new OwnGestureListener());
        mScrollX = 0;
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
        mMaxX = getChildAt(getChildCount() - 1).getRight() - getMeasuredWidth();
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

//    @Override
//    public void computeScroll() {
//        if (mScroller.computeScrollOffset()) {
//            int currentOffset = mScroller.getCurrX();
//            if (canScroll(currentOffset)) {
//                Log.e(TAG, "current offset = " + currentOffset);
//                Log.e(TAG, "mScrollX = " + mScrollX);
////                mScrollX -= currentOffset;
//            }
//        }
//        super.computeScroll();
//    }

    private boolean canScroll(int offset) {
        return mScrollX + offset >= 0 && mScrollX + offset <= mMaxX;
    }

    private class OwnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean parentInterceptionAllowed;

        @Override
        public boolean onDown(MotionEvent e) {
            parentInterceptionAllowed = true;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
                removeCallbacks(mScrollWorker);
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            disableVerticalScrolling();
            scrollHorizontaly((int) distanceX);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(mScrollX, 0, (int)-velocityX, 0, 0,
                    mMaxX, 0, 0);
            post(mScrollWorker = new Runnable() {
                @Override
                public void run() {
                    if (mScroller.computeScrollOffset()) {
                        scrollHorizontaly(mScroller.getCurrX()-mScrollX);
                        postOnAnimation(this);

                    }
                }
            });
            return true;
        }

        private void scrollHorizontaly(int offset) {
            if (canScroll(offset)) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    getChildAt(i).offsetLeftAndRight(-offset);
                }
                mScrollX += offset;
            }
            invalidate();
        }


        private void disableVerticalScrolling() {
            if (parentInterceptionAllowed) {
                requestDisallowInterceptTouchEvent(true);
                parentInterceptionAllowed = false;
            }
        }
    }
}