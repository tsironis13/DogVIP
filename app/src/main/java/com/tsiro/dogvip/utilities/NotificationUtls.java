package com.tsiro.dogvip.utilities;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by giannis on 17/7/2017.
 */

public class NotificationUtls {

    private static final String debugTag = NotificationUtls.class.getSimpleName();

    private Context mContext;

    public NotificationUtls() {}

    public NotificationUtls(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        showNotificationMessage(title, message, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent, bitmap);
//                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent, null);
                }
            }
        } else {
            showSmallNotification(mBuilder, title, message, resultPendingIntent, null);
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, String title, String message, PendingIntent resultPendingIntent, Bitmap largeIcon) {
        Notification notification;
        notification = mBuilder
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AppConfig.NOTIFICATION_ID, notification);
    }

//    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
//        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//        bigPictureStyle.setBigContentTitle(title);
//        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
//        bigPictureStyle.bigPicture(bitmap);
//        Notification notification;
//        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
//                .setAutoCancel(true)
//                .setContentTitle(title)
//                .setContentIntent(resultPendingIntent)
//                .setSound(alarmSound)
//                .setStyle(bigPictureStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
//                .setContentText(message)
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(2323, notification);
//    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) isInBackground = false;
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) isInBackground = false;
        }
        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
//        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
