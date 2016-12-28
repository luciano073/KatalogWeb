package br.com.katalog.katalogweb;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by luciano on 27/08/2016.
 */
public class KatalogApp extends Application {
    private static final String TAG = "KatalogApp";

    private static KatalogApp instance = null;

    public static KatalogApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        //Dica do canal 
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        Picasso.setSingletonInstance(built);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "KatalogApp.onTerminate()");
    }
}
