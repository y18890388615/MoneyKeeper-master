package me.bakumon.moneykeeper.shell.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import me.bakumon.moneykeeper.R;
import me.bakumon.moneykeeper.shell.base.BaseActivity;

public class TextActivity extends BaseActivity {


    ImageView ivBack;
    TextView tvTitle;
    TextView tvContent;

    @Override
    public int getLayoutID() {
        return R.layout.activity_text;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String text = getIntent().getStringExtra("text");
        String title = getIntent().getStringExtra("title");
        tvTitle.setText(title);
        ivBack.setVisibility(View.VISIBLE);
        tvContent.setText(text);

    }

    @Override
    public void initData() {

    }
}