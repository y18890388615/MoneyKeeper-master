package me.bakumon.moneykeeper.shell.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;


public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        BarUtils.setNavBarVisibility(this, false);//隐藏导航栏
        // ButterKnife
        mContext = this;
        initView(savedInstanceState);
        initData();
    }

    public abstract int getLayoutID();

    /**
     * 初始UI
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * 初始数据
     */
    public abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
