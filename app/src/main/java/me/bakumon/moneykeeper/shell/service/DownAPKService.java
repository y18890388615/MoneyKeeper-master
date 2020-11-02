package me.bakumon.moneykeeper.shell.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;

import me.bakumon.moneykeeper.App;
import me.bakumon.moneykeeper.shell.ui.VersionDownloadDialog;

/**
 * @author lam
 * @date 2019/02/27
 */
public class DownAPKService extends Service {
    private OkDownload okDownload;
    private DownloadTask task;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        // 接收Intent传来的参数:
        // 文件下载路径
        String APK_URL = intent.getStringExtra("apk_url");

        DownFile(APK_URL, App.getContext().getExternalFilesDir("").getAbsolutePath() + "/apk/");

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * @Description 判断是否插入SD卡
     */
    private boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param url    下载链接
     * @param target 保存路径
     */
    private void DownFile(String url, String target) {
        stopSelf();
        //初始化OkGo
        File file = new File(target);
        okDownload = OkDownload.getInstance();
        okDownload.setFolder(file.getPath());
        //判断有没有上次下载的数据
        Progress progress = DownloadManager.getInstance().get(url);
        if (progress != null && FileUtils.isFileExists(progress.filePath)) {
            task = OkDownload.restore(progress);
        } else {
            GetRequest<File> request = OkGo.<File>get(url); //构建下载请求
            task = OkDownload.request(url, request); //创建下载任务，tag为一个任务的唯一标示
        }
        task.register(new DownloadListener(url) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {
                NotificationManager notificationManager = VersionDownloadDialog.
                        sendProgressViewNotification(App.getContext(), (int) (progress.fraction * 100));
                if (notificationManager != null && (int) (progress.fraction * 100) == 100) {
                        notificationManager.cancel(1);
                    }
            }

            @Override
            public void onError(Progress progress) {

            }

            @Override
            public void onFinish(File file, Progress progress) {
                stopSelf();
                AppUtils.installApp(file);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        }).save();
        task.start(); //开始或继续下载
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
