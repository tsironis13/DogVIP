package com.tsiro.dogvip.utilities.common;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.tsiro.dogvip.POJO.FcmTokenUpload;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.retrofit.RetrofitFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    //image size limit 6MB
    public Image isImageSizeValid(Uri uri, int state, File file) throws IllegalArgumentException {
        File mFile;
        Image img = new Image();
        if (state == 1) {
            String path = "";
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = mContext.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
                cursor.close();
            }
            mFile = new File(path);
        } else {
            mFile = file;
        }
        img.setImage(mFile);
        img.setSize(mFile.length()/1000000 < 6);
        return img;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File image;
        //check external storage availability
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            image = File.createTempFile(timeStamp, ".jpg", mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } else {
            image = File.createTempFile(timeStamp, ".jpg", mContext.getFilesDir());
        }
        return image;
    }

    public void deleteAppStorage(File file) {
        if (file != null) {
            boolean deleted = file.delete();
        }
    }

    public Uri getUriForFile(File output) {
        return FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", output);
    }

    public MultipartBody.Part getRequestFileBody(File file) throws IllegalArgumentException {
//        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public SharedPreferences getSharedPrefs() {
        return mContext.getSharedPreferences(mContext.getResources().getString(R.string.myprefs), Context.MODE_PRIVATE);
    }

    private void saveFcmTokenToPrefs(String token) {
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        editor.putString(mContext.getResources().getString(R.string.fcmtoken), token);
        editor.putBoolean(mContext.getResources().getString(R.string.fcmtoken_uploaded), true);
        editor.apply();
    }

    public void uploadTokenToServer(String mToken, String fcmToken) {
        FcmTokenUpload request = new FcmTokenUpload();
        request.setAction(mContext.getResources().getString(R.string.save_registration_token));
        request.setAuthtoken(mToken);
        request.setDeviceid(android.os.Build.SERIAL);
        request.setFcmtoken(fcmToken);
        RetrofitFactory.getInstance().getServiceAPI().uploadFcmToken(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getSharedPrefs().edit().putBoolean(mContext.getResources().getString(R.string.fcmtoken_uploaded), false).apply();
                    }
                })
                .retry(3)
                .doOnNext(new Consumer<FcmTokenUpload>() {
                    @Override
                    public void accept(@NonNull FcmTokenUpload response) throws Exception {
                        saveFcmTokenToPrefs(response.getFcmtoken());
                    }
                }).subscribe();

    }
}
