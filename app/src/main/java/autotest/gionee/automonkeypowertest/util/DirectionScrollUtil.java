package autotest.gionee.automonkeypowertest.util;


import android.view.MotionEvent;
import android.view.View;

public class DirectionScrollUtil {
    public static void setDirectionScrollListener(View view, int mTouchSlop, OnDirectionScrollListener listener) {
        new DirectionScroll().setScrollListener(view, mTouchSlop, listener);
    }

    private static class DirectionScroll implements View.OnTouchListener {

        private float mFirstY;
        private int direction;
        private int mTouchSlop;
        private OnDirectionScrollListener listener;
        private boolean mShow;

        void setScrollListener(View view, int mTouchSlop, OnDirectionScrollListener listener) {
            this.mTouchSlop = mTouchSlop;
            this.listener = listener;
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mFirstY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float mCurrentY = event.getY();
                    if (mCurrentY - mFirstY > mTouchSlop) {
                        direction = 0;
                    } else if (mFirstY - mCurrentY > mTouchSlop) {
                        direction = 1;
                    }
                    if (direction == 1) {
                        if (mShow) {
                            if (listener != null) {
                                listener.onScrollUp();
                            }
                            mShow = !mShow;
                        }
                    } else if (direction == 0) {
                        if (!mShow) {
                            if (listener != null) {
                                listener.onScrollDown();
                            }
                            mShow = !mShow;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }


    }

    public interface OnDirectionScrollListener {

        void onScrollUp();

        void onScrollDown();
    }
}
