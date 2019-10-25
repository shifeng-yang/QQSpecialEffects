package cn.itcast.mydiyview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import cn.itcast.mydiyview.R;
import cn.itcast.mydiyview.utils.WipeLayoutManage;

public class WipeLayout extends FrameLayout {

    private View mContentView;
    private View mDeleteView;
    private int mContentWidth;
    private int mContentHeight;
    private int mDeleteWidth;
    private int speed = 300;
    float downX, downY, moveX, moveY;
    private ViewDragHelper mDragHelper;
    public WipeStatus mCurrentStatus = WipeStatus.CLOSE;
    private int position;

    enum WipeStatus {
        CLOSE, OPEN
    }

    public WipeLayout(@NonNull Context context) {
        this(context, null);
    }

    public WipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return child == mContentView || child == mDeleteView;
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return mDeleteWidth;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                if (child == mContentView) {
                    return left < -mDeleteView.getMeasuredWidth() || left > 0 ? left - dx : left;
                }

//                return left < mContentWidth - mDeleteWidth || left >= mContentWidth ? 0 : left;
                return left - dx;
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == mContentView) {
                    mDeleteView.layout(mContentView.getRight(), 0, mContentView.getRight() + mDeleteWidth, mContentHeight);
                }/* else if (changedView == mDeleteView) {
                    mContentView.layout(mDeleteView.getLeft() - mContentWidth, 0, mDeleteView.getLeft(), mContentHeight);
                }*/

                if (mContentView.getLeft() == 0 && mCurrentStatus == WipeStatus.OPEN) {
                    mCurrentStatus = WipeStatus.CLOSE;
                    WipeLayoutManage.getInstance().clear();
                } else if (mContentView.getLeft() == -mDeleteWidth && mCurrentStatus == WipeStatus.CLOSE) {
                    mCurrentStatus = WipeStatus.OPEN;
                    WipeLayoutManage.getInstance().setCurrentLayout(WipeLayout.this);
                }
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
//                Log.d("WipeLayout", "xvel:" + xvel);
                if ((mContentView.getLeft() < -mDeleteWidth / 2 && xvel > -speed && xvel < speed) || (xvel < -speed && mCurrentStatus == WipeStatus.CLOSE)) {
                    open();
                } else if ((mContentView.getLeft() >= -mDeleteWidth / 2 && xvel > -speed && xvel < speed) || (xvel > speed && mCurrentStatus == WipeStatus.OPEN)) {
                    close();
                }

            }
        });
    }

    public void open() {
        mDragHelper.smoothSlideViewTo(mContentView, -mDeleteWidth, mContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void close() {
        mDragHelper.smoothSlideViewTo(mContentView, 0, mContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //若果当前有打开的wipelayout则关闭并且将触摸事件交给touchevent处理
        if (!WipeLayoutManage.getInstance().isEnableScrol(this)) {
            WipeLayoutManage.getInstance().close();
            return true;
        }

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //若果当前有打开的wipelayout, 直接等他关闭,不要响应任何滑动事件
        if (!WipeLayoutManage.getInstance().isEnableScrol(this)) {
            requestDisallowInterceptTouchEvent(true);
            return true;
        }

        //当dx > dy 时 ,让 recycleview不要拦截触摸事件(不做这个处理会导致 onViewReleased() 中的 xvel 始终为 0 )
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if (Math.abs(moveX - downX) > Math.abs(moveY - downY)) {
                    requestDisallowInterceptTouchEvent(true);
                }
                downX = moveX;
                downY = moveY;
                break;
            default:
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取两个子view对象
        mContentView = getChildAt(0);
        mDeleteView = getChildAt(1);

        initClick();
    }

    private void initClick() {
        mDeleteView.findViewById(R.id.tv_1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                position = 0;
                mOnItemClickListen.onClick(position);
            }
        });

        mDeleteView.findViewById(R.id.tv_2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                position = 1;
                mOnItemClickListen.onClick(position);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //记录下两个子 view 的宽高,方便使用
        mContentWidth = mContentView.getMeasuredWidth();
        mContentHeight = mContentView.getMeasuredHeight();
        mDeleteWidth = mDeleteView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //初始化两个子view的位置
        mContentView.layout(0, 0, mContentWidth, mContentHeight);
        mDeleteView.layout(mContentWidth, 0, mContentWidth + mDeleteWidth, mContentHeight);
    }

    private OnItemClickListen mOnItemClickListen;
    public void setOnTtemClickListen(OnItemClickListen onTtemClickListen) {
        this.mOnItemClickListen = onTtemClickListen;
    }
    public interface OnItemClickListen {
        void onClick(int position);
    }
}
