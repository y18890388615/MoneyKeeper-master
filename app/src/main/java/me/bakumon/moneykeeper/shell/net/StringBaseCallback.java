package me.bakumon.moneykeeper.shell.net;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

public abstract class StringBaseCallback extends StringCallback {
    @Override
    public void onSuccess(Response<String> response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body());
                String data = jsonObject.toString();
                onSuccess(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        ToastUtils.showLong("服务器连接失败:" + response.message());
        LogUtils.e("onError code=" + response.code() + "|message:" + response.message() + "|body: " + response.body() + "|getException:" + response.getException());
    }

    public abstract void onSuccess(String data);

    public abstract void onFail(String msg);

}
