package cn.itcast.mydiyview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.itcast.mydiyview.adapter.MainAdapter;
import cn.itcast.mydiyview.bean.MainListBran;
import cn.itcast.mydiyview.widget.DragView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycle_main)
    RecyclerView mMain;
    @BindView(R.id.recycle_menu)
    RecyclerView mMenu;
    @BindView(R.id.slideMenu)
    DragView mDragView;
    private Unbinder mUnbinder;
    List<MainListBran> mainList = new ArrayList<>();
    List<MainListBran> menuList = new ArrayList<>();
    MainAdapter mMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mMain.setLayoutManager(new LinearLayoutManager(this));
        mMain.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mainList.clear();
        for (int i = 0;i < 10;i++) {
            mainList.add(new MainListBran(R.drawable.head_1,"用户............" + i));
            mainList.add(new MainListBran(R.drawable.head_2,"用户............" + (i + 1)));
            mainList.add(new MainListBran(R.drawable.head_3,"用户............" + (i + 2)));
        }
        mMainAdapter = new MainAdapter(R.layout.item_main,mainList);
        mMain.setAdapter(mMainAdapter);

        mMenu.setLayoutManager(new LinearLayoutManager(this));
        mMenu.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        menuList.clear();
        for (int i = 0;i < 20;i++) {
            menuList.add(new MainListBran(R.drawable.head_1,"菜单......." + i));
        }
        mMainAdapter = new MainAdapter(R.layout.item_content,menuList);
        mMenu.setAdapter(mMainAdapter);

        mDragView.setDragViewListen(new DragView.DragViewListen() {
            @Override
            public void onOpen() {
//                Log.d("MainActivity", "打开了.....");
            }

            @Override
            public void onClose() {
//                Log.d("MainActivity", "关闭了.....");
            }

            @Override
            public void change(float fraction) {
//                Log.d("MainActivity", "fraction:" + fraction);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
