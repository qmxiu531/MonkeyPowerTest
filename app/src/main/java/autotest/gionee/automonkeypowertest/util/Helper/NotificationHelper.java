package autotest.gionee.automonkeypowertest.util.Helper;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import autotest.gionee.automonkeypowertest.main.MainActivity;
import autotest.gionee.automonkeypowertest.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    private final NotificationManager manager;
    private Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void set(int id) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentText("测试服务正在运行中");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setContentTitle(mContext.getText(R.string.app_name));
        builder.setOngoing(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContent(getRemoteView());
        Notification notification = builder.build();
        manager.notify(id, notification);
    }

    public RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tv_content_title, mContext.getText(R.string.app_name));
        remoteViews.setTextViewText(R.id.tv_content_text, "测试服务正在运行中……");
        Intent stop = new Intent("automonkeypower.stopTest");
        PendingIntent stopIntent = PendingIntent.getBroadcast(mContext, 1, stop, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.stop, stopIntent);
        return remoteViews;
    }

    public void cancelAll() {
        manager.cancelAll();
    }


}
