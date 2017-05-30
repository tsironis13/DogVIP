package com.tsiro.dogvip.utilities.common;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.tsiro.dogvip.R;
import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by giannis on 25/5/2017.
 */

public class CommonUtls {

    private Context mContext;

    public CommonUtls(Context context) {
        mContext = context;
    }

    public void buildNotification(String title, String msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE);
        mBuilder.setContentText(msg);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }

}
