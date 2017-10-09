package com.tsiro.dogvip.utilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.di.qualifiers.ActivityContext;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.image_states.State;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 30/9/2017.
 */

public class ImageUtls {

    private Context mContext;

    @Inject
    public ImageUtls(@ApplicationContext Context context) {
        this.mContext = context;
    }

    public TestImage isCameraImageValid(File file, TestImage img) {
        if (file != null) {
            img.setFile(file);
            img.setHasValidSize(file.length()/1000000 < 6);
        }
        return img;
    }

    public TestImage isGalleryImageValid(Uri uri, TestImage img) {
        File mFile = null;
        String path = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(mContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                if (!"unknown".equals(getUriExtenstion(getMimeType(uri)))) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) path = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    img.setFileTypeInvalid(true);
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
                    img.setFileTypeInvalid(true);
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
                    img.setFileTypeInvalid(true);
                }
                if (path != null)mFile = new File(path);
            } else if (isGoogleDriveStorageDocument(uri)) {
                try {
                    String uriExt = getUriExtenstion(getMimeType(uri));
                    if (!"unknown".equals(uriExt)) {
                        mFile = createTempImageFile(uriExt);
                        writeInputStreamUriDataToFile(uri, mFile);
//                        img.setDeleteLocalFile(true);
                    } else {
                        img.setFileTypeInvalid(true);
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
                    img.setFileTypeInvalid(true);
                }
            } else if (isPicasaStorageDocument(uri) || isGoogleDriveStorageDocument(uri) || isGoogleDriveLegacyStorageDocument(uri) || isOneDriveStorageDocument(uri)) {
                try {
                    if (!"unknown".equals(uriExt)) {
                        mFile = createTempImageFile(uriExt);
                        writeInputStreamUriDataToFile(uri, mFile);
//                        img.setDeleteLocalFile(true);
                    } else {
                        img.setFileTypeInvalid(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (!"unknown".equals(uriExt)) {
                        mFile = createTempImageFile(uriExt);
                        writeInputStreamUriDataToFile(uri, mFile);
//                        img.setDeleteLocalFile(true);
                    } else {
                        img.setFileTypeInvalid(true);
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
                img.setFileTypeInvalid(true);
            }
        }
        if (mFile != null) {
            img.setFile(mFile);
            img.setHasValidSize(mFile.length()/1000000 < 6);
        }
        return img;
    }

    public TestImage getCameraImage() {
        return  null;
    }

    public File createTempImageFile(String extension) throws IOException {
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

    public Uri getUriForFile(File output) {
        return FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", output);
    }

    public MultipartBody.Part getRequestFileBody(File file) throws IllegalArgumentException {
//        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    public void loadImageWithGlide(Object obj, final State state, final File file, ImageView imageView, final ImageUploadViewModel imageUploadViewModel, final String action, final String token, final int id, int default_icon) {
        Glide.with(imageView.getContext())
                .load(obj)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { return false; }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        state.uploadImageToServer(imageUploadViewModel, action, token, id, file);
                        return false;
                    }
                })
                .transition(withCrossFade())
                .apply(new RequestOptions().centerCrop().error(default_icon))
                .into(imageView);
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadRecyclerViewImage(ImageView imageView, String url) {
        //.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
        int default_img = 0;
        if (imageView.getId() == R.id.petImgv) {
            default_img = R.drawable.ic_pets;
        } else if (imageView.getId() == R.id.profileImgv) {
            default_img = R.drawable.default_person;
        } else {
            default_img = R.drawable.default_person;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(default_img).placeholder(default_img))
                .into(imageView);
    }

    public void clearImageWithGlide(ImageView imageView, int default_icon) {
        Glide.with(imageView.getContext()).load(default_icon).into(imageView);
    }

}
