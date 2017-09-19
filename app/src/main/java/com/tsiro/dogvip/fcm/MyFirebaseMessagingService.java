package com.tsiro.dogvip.fcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tsiro.dogvip.POJO.chat.Message;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.petsitters.ManipulateNewSitterBookingActivity;
import com.tsiro.dogvip.utilities.MyLifecycleHandler;
import com.tsiro.dogvip.utilities.NotificationUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

/**
 * Created by giannis on 16/7/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String debugTag = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get(getResources().getString(R.string.action)).equals(getResources().getString(R.string.send_msg))) {
                String title = remoteMessage.getData().get(getResources().getString(R.string.title));
                String message = remoteMessage.getData().get(getResources().getString(R.string.message));
                String image = remoteMessage.getData().get(getResources().getString(R.string.imageurl));
                String petName = remoteMessage.getData().get(getResources().getString(R.string.pet_name));
                String timestamp = remoteMessage.getData().get(getResources().getString(R.string.timestamp));
                int mesgId = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.id)));
                int userRoleId = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.user_role_id))); //to_id
                int chatRoomId = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.chat_room_id)));
                int fromId = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.from_id)));
//            int role = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.role)));

                Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_chatid));
                bundle.putString(getResources().getString(R.string.title), title);
                bundle.putString(getResources().getString(R.string.pet_name), petName);
                bundle.putInt(getResources().getString(R.string.id), mesgId);
                bundle.putInt(getResources().getString(R.string.chat_room_id), chatRoomId);
                bundle.putInt(getResources().getString(R.string.user_role_id), userRoleId);
                bundle.putInt(getResources().getString(R.string.from_id), fromId);
//            bundle.putInt(getResources().getString(R.string.role), role);
                resultIntent.putExtras(bundle);
                showNotificationMessage(getApplicationContext(), title, message, resultIntent, image);

                if (MyLifecycleHandler.isApplicationInForeground()) {
                    Message mesgobj = new Message();
                    mesgobj.setId(mesgId);
                    mesgobj.setMessage(message);
                    mesgobj.setUser_role_id(userRoleId);
                    mesgobj.setImage_url(image);
                    mesgobj.setChat_room_id(chatRoomId);
                    mesgobj.setCreated_at(timestamp);
                    RxEventBus.createSubject(AppConfig.PUBLISH_MSG_NOTFCTS, AppConfig.PUBLISH_SUBJ).post(mesgobj);
                }
            } else {
//                Log.e(debugTag, remoteMessage.getData().get("id") + " BOOKING ID");
                int bookingId = Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.id)));
                String title = remoteMessage.getData().get(getResources().getString(R.string.title));
                String image = remoteMessage.getData().get(getResources().getString(R.string.imageurl));
                String timestamp = remoteMessage.getData().get(getResources().getString(R.string.timestamp));

                Intent resultIntent = new Intent(getApplicationContext(), ManipulateNewSitterBookingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.id), bookingId);
                resultIntent.putExtras(bundle);
                showNotificationMessage(getApplicationContext(), title, getResources().getString(R.string.new_sitter_booking_msg), resultIntent, image);
            }

        }



//        String title = bundle.getString("title");
//        String message = bundle.getString("message");
//        String image = bundle.getString("image");
//        String timestamp = bundle.getString("created_at");

//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(this,);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils();
//            notificationUtils.playNotificationSound();
//        } else {
//
//            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//            resultIntent.putExtra("message", message);
//
//            if (TextUtils.isEmpty(image)) {
//                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//            } else {
//                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
//            }
//        }
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent, String imageUrl) {
        NotificationUtls notificationUtils = new NotificationUtls(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }
}
