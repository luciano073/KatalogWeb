package br.com.katalog.katalogweb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.utils.Permissions;

/**
 * Created by luciano on 27/08/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static String TAG = "BaseActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mCallInit;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallInit = true;
        performLogin();
        performPermissions();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void performLogin() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (mCallInit) { // Call init just once
                        mCallInit = false;
                        init();
                    }
                } else {
                    finish();
                    Intent it = new Intent(BaseActivity.this, LoginActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
                }
            }
        };
    }

    protected void performPermissions() {

        if (!Permissions.hasStoragePermission(this)) {
            Permissions.requestStoragePermission(this, 0);
        }

        if (!Permissions.hasGetAccountPermission(this)){
            Permissions.requestGetAccountPermission(this, 0);
        }
    }

    protected void setUpToobar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    //Configura a NavDrawer
    protected void setUpNavDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_close,
                R.string.navigation_drawer_open
        );
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
    }

    protected void setNavViewValues(NavigationView view, String userName, String email, Uri photo) {
        View headerView = view.getHeaderView(0);
        TextView tvName = (TextView) headerView.findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView) headerView.findViewById(R.id.tvUserEmail);
        ImageView imgPhoto = (ImageView) headerView.findViewById(R.id.imageView);
        tvName.setText(userName);
        tvEmail.setText(email);
//        imgPhoto.setImageURI(photo); // don't work
    }

    protected final FirebaseAuth getAuth() {
        return mAuth;
    }

    protected abstract void init();

}
