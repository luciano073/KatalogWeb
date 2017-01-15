package br.com.katalog.katalogweb.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.fragments.BookListFragment;
import br.com.katalog.katalogweb.fragments.DigiMediaListFragment;
import br.com.katalog.katalogweb.fragments.FilmListFragment;
import br.com.katalog.katalogweb.fragments.MainFragment;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.ArtistDAO;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.BookDAO;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.models.FilmDAO;
import br.com.katalog.katalogweb.utils.Constants;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String MENU_ITEM_SELECTED = "menu_item";
    private int mNavDrawerSelectedOption;
    private NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToobar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_close,
                R.string.navigation_drawer_open
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            mNavDrawerSelectedOption = R.id.nav_home;
        } else {
            mNavDrawerSelectedOption = savedInstanceState.getInt(MENU_ITEM_SELECTED);
        }

        selectNavDrawerMenuOptions(mNavigationView.getMenu().findItem(mNavDrawerSelectedOption));
//        initLists();//to suply search activity
    }

    //Dica obtida em: http://stackoverflow.com/questions/14597229/how-do-i-pass-extra-variables-during-a-search-invoked-by-a-searchview-widget
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }


    @Override
    protected void init() {
        FirebaseUser user = getAuth().getCurrentUser();
        setNavViewValues(
                mNavigationView,
                user.getDisplayName(),
                user.getEmail(),
                null
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MENU_ITEM_SELECTED, mNavDrawerSelectedOption);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            getAuth().signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        selectNavDrawerMenuOptions(item);
        return true;
    }

    protected void selectNavDrawerMenuOptions(MenuItem item) {
        mNavDrawerSelectedOption = item.getItemId();
        switch (mNavDrawerSelectedOption) {
            case R.id.nav_home:
                ActionBar bar = getSupportActionBar();
                bar.setTitle(R.string.app_name);
                replaceFragment(new MainFragment(), MainFragment.TAG);
                break;
            case R.id.nav_readings:
                bar = getSupportActionBar();
                bar.setTitle(R.string.books);
                replaceFragment(new BookListFragment(), BookListFragment.TAG);
                break;
            case R.id.nav_movies:
                bar = getSupportActionBar();
                bar.setTitle(R.string.films);
                replaceFragment(new FilmListFragment(), FilmListFragment.TAG);
                break;
            case R.id.nav_bluray:
                bar = getSupportActionBar();
                bar.setTitle(getString(R.string.media_types));
                replaceFragment(new DigiMediaListFragment(), DigiMediaListFragment.TAG);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction().replace(R.id.content_main, fragment, tag).commit();
        }
    }

}
