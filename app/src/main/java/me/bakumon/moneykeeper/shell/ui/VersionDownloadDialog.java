package me.bakumon.moneykeeper.shell.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.lxj.xpopup.core.CenterPopupView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;
import me.bakumon.moneykeeper.R;
import me.bakumon.moneykeeper.shell.utils.NumberUtils;

public class VersionDownloadDialog extends CenterPopupView {
    ProgressBar progressBar;
    TextView tvPercentProgress;
    TextView tvBitRate;
    TextView btnCancel;
    TextView tvTop;
    private String url;
    private Context context;
    private OkDownload okDownload;
    private DownloadTask task;

    public VersionDownloadDialog(@NonNull Context context, String url) {
        super(context);
        this.url = url;
        this.context = context;
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        tvTop.setText("版本更新");
        sendProgressViewNotification(context, 0);
        //初始化OkGo
        okDownload = OkDownload.getInstance();
        File file = new File(context.getExternalFilesDir("").getAbsolutePath() + "/apk/");
        if (!file.exists()) {
            file.mkdirs();
        }
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
                    tvBitRate.setText(NumberUtils.getFormartSize(progress.speed) + "/s");
                    tvPercentProgress.setText(NumberUtils.keepNumberTwoPoint(progress.fraction * 100) + "%");
                    progressBar.setProgress((int) (progress.fraction * 100));
                    sendProgressViewNotification(context, (int) (progress.fraction * 100));
                    if (notificationManager != null && (int) (progress.fraction * 100) == 100) {
                        notificationManager.cancel(1);
                    }
            }

            @Override
            public void onError(Progress progress) {

            }

            @Override
            public void onFinish(File file, Progress progress) {
                AppUtils.installApp(file);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        }).save();
        task.start(); //开始或继续下载
    }

    @Override
    protected int getMaxWidth() {
        return ConvertUtils.dp2px(300);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_download_file;
    }

    @Override
    public void init(Runnable afterAnimationStarted, Runnable afterAnimationEnd) {
        super.init(afterAnimationStarted, afterAnimationEnd);
        progressBar = findViewById(R.id.progressBar);
        tvPercentProgress = findViewById(R.id.tv_percent_progress);
        tvBitRate = findViewById(R.id.tv_bit_rate);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                task.pause();
                dismiss();
            }
        });
        tvTop = findViewById(R.id.tv_top);
        startDownload();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        task.pause();
    }

    private static NotificationManager notificationManager;

    public static NotificationManager sendProgressViewNotification(Context context, int progress) {
        String channelId = null;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "1";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel1", NotificationManager.IMPORTANCE_MIN);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.RED); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel);
        }
        //点击进入mainActivity
        Intent resultIntent = new Intent(context, context.getClass());
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pi = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder build = new NotificationCompat.Builder(context, channelId);
        build.setContentTitle("更新下载中")
                .setContentText("加载进度:" + progress + "%")
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知不可删除
                .setOngoing(true)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        build.setProgress(100, progress, false);
        if (progress == 100) {
            build.setContentText("已完成");
        }
        notificationManager.notify(1, build.build());
        return notificationManager;
    }
}
