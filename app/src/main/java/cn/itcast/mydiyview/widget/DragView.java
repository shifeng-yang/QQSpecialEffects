package cn.itcast.mydiyview.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import cn.itcast.mydiyview.R;
import cn.itcast.mydiyview.utils.ColorUtil;

public class DragView extends FrameLayout {

    private View mMenuView;
    private MyLinearLayout mMainView;
    private ViewDragHelper mDragHelper;
    //main可拖拽的范围
    private int mSlideWidth;
    private FloatEvaluator mFloatEvaluator;
    private IntEvaluator mIntEvaluator;
    private float mFraction;
    public DragStatus mCurrentStatus = DragStatus.CLOSE;
    private DragViewListen mDragViewListen;
    enum DragStatus {
        OPEN,CLOSE
    }

    public DragView(Context context) {
        this(context,null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mFloatEvaluator = new FloatEvaluator();
        mIntEvaluator = new IntEvaluator();

        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

            /**用于判断是否要捕获view的触摸事件
             * @param view 当前触摸的view
             * @param i
             * @return true:捕获
             */
            @Override
            public boolean tryCaptureView(@NonNull View view, int i) {
                return view == mMainView || view == mMenuView;
            }

            /**
             * 控制触摸范围(拖拽),不实现这个方法或是返回0(可以返回任意数值,目前为止测试是这样)会导致只有顶部一小块部分可以相应拖拽
             */
            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return child.getMeasuredWidth();
//                return 0;
            }


            /**控制水平方向拖拽之后的位置
             * @param child 当前触摸的子View
             * @param left 计算得出的最终左边的坐标
             * @param dx 水平的拖拽偏移量
             * @return 拖拽之后的位置
             */
            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                if (child == mMainView) {
                    return left < 0 || left > mSlideWidth ? left - dx : left;
                }

                return left;
            }

            /**
             * 当子view拖拽过程中执行,多用于做伴随移动
             */
            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);

                if (changedView == mMenuView) {
                    //固定menu不动
                    mMenuView.layout(0,0, mMenuView.getRight(), mMenuView.getBottom());
                    //main做伴随移动
                    int l = mMainView.getLeft() + dx > mSlideWidth || mMainView.getLeft() + dx < 0 ?
                            mMainView.getLeft() : mMainView.getLeft() + dx;
                    mMainView.layout(l,0,l + mMainView.getMeasuredWidth(), mMainView.getBottom());
                }

                //计算滑动百分比传给动画
                mFraction = mMainView.getLeft() / (float)mSlideWidth;
                executeAnim();
                ViewHelper.setAlpha(mMainView.findViewById(R.id.iv_head),1 - mFraction);
                mDragViewListen.change(mFraction);

                if (mMainView.getLeft() == 0) {
                    mDragViewListen.onClose();
                } else if (mMainView.getLeft() == mSlideWidth) {
                    mDragViewListen.onOpen();
                }
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);

//                Log.d("DragView", "onViewReleased");
                mDragHelper.smoothSlideViewTo(mMainView, mMainView.getLeft() < mSlideWidth / 2 ? 0 : mSlideWidth, mMainView.getTop());
                if ((mMainView.getLeft() < mSlideWidth / 2 && xvel > -400 && xvel < 400) || (xvel < -400 && mCurrentStatus == DragStatus.OPEN)) {
                    close();
                    mCurrentStatus = DragStatus.CLOSE;
//                    Log.d("DragView", "关闭了...");
                } else if ((mMainView.getLeft() >= mSlideWidth / 2 && xvel > -400 && xvel < 400) || (xvel > 400 && mCurrentStatus == DragStatus.CLOSE)) {
                    open();
                    mCurrentStatus = DragStatus.OPEN;
//                    Log.d("DragView", "打开了...");
                }


            }
        });
    }

    public void open() {
        mDragHelper.smoothSlideViewTo(mMainView,mSlideWidth,mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void close() {
        mDragHelper.smoothSlideViewTo(mMainView,0,mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);

        ViewPropertyAnimator.animate(mMainView.findViewById(R.id.iv_head)).translationXBy(20)
                .setInterpolator(new CycleInterpolator(3))
                .setDuration(500)
                .start();
    }

    private void executeAnim() {
        ViewHelper.setScaleX(mMainView, mFloatEvaluator.evaluate(mFraction,1f,0.8f));
        ViewHelper.setScaleY(mMainView, mFloatEvaluator.evaluate(mFraction,1f,0.8f));

        ViewHelper.setTranslationX(mMenuView,mIntEvaluator.evaluate(mFraction,-mMenuView.getMeasuredWidth() / 2,0));
        ViewHelper.setScaleX(mMenuView, mFloatEvaluator.evaluate(mFraction,0.5f,1f));
        ViewHelper.setScaleY(mMenuView, mFloatEvaluator.evaluate(mFraction,0.5f,1f));
        ViewHelper.setAlpha(mMenuView, mFloatEvaluator.evaluate(mFraction,0.3f,1f));
        //给背景添加一层黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(mFraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    /**
     * 不佳这个方法 会导致onViewReleased方法的滑动smoothSlideViewTo失效
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
//        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMenuView = getChildAt(0);
        mMainView = (MyLinearLayout) getChildAt(1);

        //把DragView对象传给MyLinearLayout
        mMainView.setDragView(this);
    }

    /**
     * 这个方法在 onMeasure 执行完之后执行,常用于初始化父,子vview的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mSlideWidth = (int)(mMainView.getMeasuredWidth() * 0.6);
    }

    public void setDragViewListen(DragViewListen dragViewListen) {
        this.mDragViewListen = dragViewListen;
    }

    public interface DragViewListen {
        void onOpen();
        void onClose();
        void change(float fraction);
    }
}
