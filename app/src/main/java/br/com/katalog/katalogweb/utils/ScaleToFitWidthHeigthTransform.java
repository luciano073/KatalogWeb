package br.com.katalog.katalogweb.utils;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by luciano on 11/09/2016.
 */
public class ScaleToFitWidthHeigthTransform implements Transformation {

    private int mSize;
    private boolean isHeightScale;

    public ScaleToFitWidthHeigthTransform(int mSize, boolean isHeightScale) {
        this.mSize = mSize;
        this.isHeightScale = isHeightScale;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        float scale;
        int newSize;
        Bitmap scaleBitmap;

        if (isHeightScale) {
            /*if (mSize >= source.getHeight()){
                return source;
            }*/
            scale = (float) mSize / source.getHeight();
            newSize = Math.round(source.getWidth() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
        } else {
            /*if (mSize >= source.getWidth()){
                return source;
            }*/
            scale = (float) mSize / source.getWidth();
            newSize = Math.round(source.getHeight() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
        }

        if (scaleBitmap != source){
            source.recycle();
        }


        return scaleBitmap;
    }

    @Override
    public String key() {
        return "scaleRespectRatio"+mSize+ isHeightScale;
    }
}
