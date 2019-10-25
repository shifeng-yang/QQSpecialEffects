package cn.itcast.mydiyview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class MyLinearLayout extends LinearLayout {
    DragView mDragView;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragView(DragView dragView) {
        this.mDragView = dragView;
    }

    /**
     * 如果菜单为开启状态则屏蔽mainview的listview的滑动
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragView != null && mDragView.mCurrentStatus == DragView.DragStatus.OPEN) {
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理截获的触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragView != null && mDragView.mCurrentStatus == DragView.DragStatus.OPEN) {
            //触摸mainview后 抬起手指关闭菜单
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mDragView.close();
            }

            return true;
        }

        return super.onTouchEvent(event);
    }
}
