package cn.itcast.mydiyview.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import cn.itcast.mydiyview.R;
import cn.itcast.mydiyview.bean.MainListBran;

public class MainAdapter extends BaseQuickAdapter<MainListBran, BaseViewHolder> {

    public MainAdapter(int layoutResId, @Nullable List<MainListBran> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MainListBran item) {
        if (item.getContent().contains("菜单")) {
            helper.getView(R.id.iv_head).setVisibility(View.GONE);
            ((TextView)helper.getView(R.id.tv_content)).setTextColor(Color.WHITE);
        }
        helper.setBackgroundRes(R.id.iv_head,item.getImageView());
        helper.setText(R.id.tv_content,item.getContent());
    }
}
