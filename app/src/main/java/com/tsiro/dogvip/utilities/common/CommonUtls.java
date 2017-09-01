package com.tsiro.dogvip.utilities.common;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.tsiro.dogvip.POJO.FcmTokenUpload;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.retrofit.RetrofitFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
//    private static final String debugTag = CommonUtls.class.getSimpleName();
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
        File mFile = null;
        Image img = new Image();
        if (state == 1) {
            String path = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(mContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        img.setIsInvalid_filetype(true);
                    }
                    mFile = new File(path);
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                        path = getDataColumn(mContext, contentUri, null, null);
                    } else {
                        img.setIsInvalid_filetype(true);
                    }
                    if (path != null)mFile = new File(path);
                } else if (isMediaDocument(uri)) {
                    if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[] {
                                split[1]
                        };
                        path = getDataColumn(mContext, contentUri, selection, selectionArgs);
                    } else {
                        img.setIsInvalid_filetype(true);
                    }
                    if (path != null)mFile = new File(path);
                } else if (isGoogleDriveStorageDocument(uri)) {
                    try {
                        String uriExt = getUriExtenstion(getMimeType(uri));
                        if (!"unknown".equals(uriExt)) {
                            mFile = createImageFile(uriExt);
                            writeInputStreamUriDataToFile(uri, mFile);
                            img.setDeleteLocalFile(true);
                        } else {
                            img.setIsInvalid_filetype(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                String uriExt = getUriExtenstion(getMimeType(uri));
                if (isGooglePhotosUri(uri)) {
                    if (!"unknown".equals(uriExt)) {
                        path = uri.getLastPathSegment();
                        mFile = new File(path);
                    } else {
                        img.setIsInvalid_filetype(true);
                    }
                } else if (isPicasaStorageDocument(uri) || isGoogleDriveStorageDocument(uri) || isGoogleDriveLegacyStorageDocument(uri) || isOneDriveStorageDocument(uri)) {
                    try {
                        if (!"unknown".equals(uriExt)) {
                            mFile = createImageFile(uriExt);
                            writeInputStreamUriDataToFile(uri, mFile);
                            img.setDeleteLocalFile(true);
                        } else {
                            img.setIsInvalid_filetype(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (!"unknown".equals(uriExt)) {
                            mFile = createImageFile(uriExt);
                            writeInputStreamUriDataToFile(uri, mFile);
                            img.setDeleteLocalFile(true);
                        } else {
                            img.setIsInvalid_filetype(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
//                        path = getDataColumn(mContext, uri, null, null);
//                        if (path != null)mFile = new File(path);
//                    } else {
//                        img.setIsInvalid_filetype(true);
//                    }
                }
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
                    path = uri.getPath();
                    mFile = new File(path);
                } else {
                    img.setIsInvalid_filetype(true);
                }
            }
        } else {
            mFile = file;
        }
        if (mFile != null) {
            img.setImage(mFile);
            img.setSize(mFile.length()/1000000 < 6);
        }
        return img;
    }

    private void writeInputStreamUriDataToFile(Uri uri, File mFile) {
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);

            FileOutputStream fos = new FileOutputStream(mFile);
            byte[] buf = new byte[512];
            while (true) {
                int len = inputStream.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }
            inputStream.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = mContext.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private String getUriExtenstion(String mimeType) {
        if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg")) {
            return ".jpg";
        } else if (mimeType.equals("image/png")) {
            return ".png";
        } else if (mimeType.equals("image/gif")) {
            return ".gif";
        } else {
            return "unknown";
        }
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is OneDriveStorageProvider.
     */
    private static boolean isOneDriveStorageDocument(Uri uri) {
        return "com.microsoft.skydrive.content.external".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDriveStorageProvider.
     */
    private static boolean isGoogleDriveStorageDocument(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDriveLegacyStorageProvider.
     */
    private static boolean isGoogleDriveLegacyStorageDocument(Uri uri) {
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is PicasaStorageProvider.
     */
    private static boolean isPicasaStorageDocument(Uri uri) {
        return "com.sec.android.gallery3d.provider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public File createImageFile(String extension) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File image;
        //check external storage availability
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            image = File.createTempFile(timeStamp, extension, mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } else {
            image = File.createTempFile(timeStamp, extension, mContext.getFilesDir());
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
