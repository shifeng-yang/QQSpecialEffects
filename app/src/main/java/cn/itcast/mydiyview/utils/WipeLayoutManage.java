package cn.itcast.mydiyview.utils;

import cn.itcast.mydiyview.widget.WipeLayout;

public class WipeLayoutManage {
    private static WipeLayoutManage mWipeLayoutManage = null;
    private WipeLayout mCurrentLayout;

    private WipeLayoutManage() {}

    public static WipeLayoutManage getInstance() {
        if (mWipeLayoutManage == null) {
            synchronized (WipeLayoutManage.class) {
                if (mWipeLayoutManage == null) {
                    mWipeLayoutManage = new WipeLayoutManage();
                }
            }
        }

        return mWipeLayoutManage;
    }

    /**
     * 记录打开的wipelayout
     */
    public void setCurrentLayout(WipeLayout currentLayout) {
        this.mCurrentLayout = currentLayout;
    }

    /*
    * 清空当前的wipelayout
    */
    public void clear() {
        mCurrentLayout = null;
    }

    /*
    * 关闭当前打开的wipelayout
    */
    public void close() {
        mCurrentLayout.close();
    }

    /*
    * 判断是否可以滑动
    * 如果没有打开的wipelayout即mCurrentLayout == null,则返回true
    * 否则判断打开的wipelayout和当前按下的是否是同一个
    */
    public boolean isEnableScrol(WipeLayout wipeLayout) {
        if (mCurrentLayout == null) {
            return true;
        }else {
            return mCurrentLayout == wipeLayout;
        }
    }
}
