package br.com.katalog.katalogweb.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/**
 * Created by luciano on 10/01/2016.
 */
public class ImageUtils {

    public static Bitmap resizeToMaxHeight(/*Bitmap src*/ String filePath, int newHeight) {

        // Get the ImageView and its bitmap
//        Drawable drawing = view.getDrawable();
//        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //Lê as dimensões
        int originaWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // Get current dimensions
//        int width = src.getWidth();
//        int height = src.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
//        float xScale = ((float) boundBoxInDp) / width;
//        float yScale = ((float) boundBoxInDp) / height;
//        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
//        Bitmap scaledBitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
//        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
//        width = scaledBitmap.getWidth();
//        height = scaledBitmap.getHeight();

        float scaleNewWidth = (float) newHeight / originalHeight;
        int newWith = Math.round(originaWidth * scaleNewWidth);
//        int newWith = (int) (height * 0.654); //manter proporção da imagem
        int scale = Math.min(originaWidth / newWith, originalHeight / newHeight);

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        return bitmap;
    }


    public static Bitmap byte2Bitmap(byte[] blob) {
        Bitmap tmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        return tmp;
    }


    public static byte[] bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
        return bos.toByteArray();
    }

    public static String getPathImageGallery(Uri uri, Context context) {
        String id = null;
        String check = uri.getLastPathSegment();
        if (check.contains(":")) {       // Essa verificação é necessária pq as imagens vindas
            id = check.split(":")[1];   // da pasta Pictures não trazem a Uri com o final :id
        } else {
            id = check;
        }
        final String[] imageColumns = {MediaStore.Images.Media.DATA};

        Uri contentUri = getUri();

        String selectedImagePath = "path";

        Cursor imageCursor = context.getContentResolver().query(contentUri, imageColumns,
                MediaStore.Images.Media._ID + "=" + id, null, null);

        if (imageCursor != null) {
            if (imageCursor.moveToFirst()) {
                selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } else {
                // for older version use existing code here
                return null;
            }
        }

        return selectedImagePath;
    }

    private static  Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

}
