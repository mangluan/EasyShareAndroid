package com.easyshare.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * uri 转 file
     */
    public static File uri2File(Context context, Uri uri) throws IOException {
        Log.e("TAG", "uri2File: " + uri);
//        String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
//        if (TextUtils.isEmpty(type) || "null".equals(type)) type = "png";
        String displayName = System.currentTimeMillis() + ".png";
        File imageFile = new File(getFilePath(context), displayName);
        // 写入文件
        InputStream input = context.getContentResolver().openInputStream(uri);
        FileOutputStream out = new FileOutputStream(imageFile);
        byte[] bytes = new byte[1024];
        while (input.read(bytes) != -1) {
            out.write(bytes);
        }
        return imageFile;
    }

    /**
     * 获取内置文件路径
     *
     * @return 返回可用文件路径
     */
    public static String getFilePath(Context context) {
        File mDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
        return mDirectory.toString();
    }

    /**
     * 获取Assets真实地址
     */
    public static String getAssetsURL(String fileName) {
        return "file:///android_asset/" + fileName;
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssets(Context context, String parentPath, String fileName) {
        return getImageFromAssets(context, parentPath + File.separator + fileName);
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssets(Context context, String fileName) {
        Bitmap image = null;
        AssetManager assets = context.getAssets();
        try {
            InputStream is = assets.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
