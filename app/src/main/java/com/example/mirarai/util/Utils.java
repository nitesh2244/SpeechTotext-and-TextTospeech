package com.example.mirarai.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class Utils {

    public static File getFile(Context context, Uri uri) throws IOException {
        try {

            File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
            try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
                createFileFromStream(ins, destinationFilename);
            } catch (Exception ex) {
                Log.e("Save File", ex.getMessage());
                ex.printStackTrace();
            }
            return destinationFilename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String name = null;
            if (uri.getScheme().equals("content")) {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                assert cursor != null;
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                name = cursor.getString(nameIndex);
            }
            if (name == null) {
                name = uri.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }

            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }


    public static String getServerError(int responseCode, ResponseBody responseBody) {
        String message = "";
        Gson gson = new GsonBuilder().create();
        try {
            CommonErrorResponse commonErrorResponse = gson.fromJson(responseBody.string(), CommonErrorResponse.class);

            if (responseCode == 400) {
                message = commonErrorResponse.getMessage();
            } else if (responseCode == 401) {
                message = commonErrorResponse.getMessage();
            } else if (responseCode == 500) {
                message = commonErrorResponse.getMessage();
            } else if (responseCode == 403) {
                message = commonErrorResponse.getMessage();
            } else if (responseCode == 409) {
                message = commonErrorResponse.getMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}
