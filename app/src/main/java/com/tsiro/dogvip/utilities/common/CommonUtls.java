package com.tsiro.dogvip.utilities.common;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by giannis on 25/5/2017.
 */

public class CommonUtls {

    private static final String debugTag = CommonUtls.class.getSimpleName();
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
        Log.e(debugTag, uri.toString());
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
//            Log.e("ss", mFile+"");
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
//            Log.e(debugTag, deleted+"");
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
}
