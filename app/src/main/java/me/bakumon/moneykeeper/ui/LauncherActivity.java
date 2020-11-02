/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.bakumon.moneykeeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.GsonUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import me.bakumon.moneykeeper.shell.bean.SwitchBean;
import me.bakumon.moneykeeper.shell.net.HttpUtils;
import me.bakumon.moneykeeper.shell.net.StringBaseCallback;
import me.bakumon.moneykeeper.shell.service.DownAPKService;
import me.bakumon.moneykeeper.shell.ui.VersionDownloadDialog;
import me.bakumon.moneykeeper.shell.ui.WebActivity;
import me.bakumon.moneykeeper.ui.home.HomeActivity;

/**
 * LauncherActivity
 *
 * @author bakumon https://bakumon.me
 * @date 2018/5/2
 */
public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpUtils.getInstance().get(LauncherActivity.this,
                "http://mock-api.com/Zn5Mlenj.mock/switch/entrance", new StringBaseCallback() {
                    @Override
                    public void onSuccess(String data) {
                        SwitchBean switchBean = GsonUtils.fromJson(data, SwitchBean.class);
                        switch (switchBean.getSwitchX()){
                            case 1://网页
                                LauncherActivity.this.startActivity(new Intent(LauncherActivity.this,
                                        WebActivity.class).putExtra("web_url",switchBean.getWeb_url()));
                                finish();
                                break;
                            case 2://更新
                                XPopup.Builder builder = new XPopup.Builder(LauncherActivity.this);
                                BasePopupView show = builder.asCustom(new VersionDownloadDialog(
                                        LauncherActivity.this, switchBean.getUpdate_url())).show();
                                show.dismissWith(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(LauncherActivity.this, DownAPKService.class);
                                        intent.putExtra("apk_url", switchBean.getUpdate_url());
                                        LauncherActivity.this.startService(intent);
                                    }
                                });
                                break;
                            case 0://自己页面
                                startActivity(new Intent(LauncherActivity.this,HomeActivity.class));
                                break;
                        }
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
        finish();
    }
}
