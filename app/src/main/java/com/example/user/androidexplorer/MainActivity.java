package com.example.user.androidexplorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

import java.io.File;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View menuLayout;
    ArcLayout arcLayout;
    boolean openMenu;
    private boolean hasStoragePermission;
    private ListFragment fragment;
    private File root;
    private LinearLayout pathBarContainer;
    private HorizontalScrollView horizontalScrollView;
    private CustomAdapter currentAdapter;
    private boolean selectionMode;
    FloatingActionButton fab;
    LinearLayout popUpMenu;
    ImageButton addFileBtn;
    ImageButton addFolderBtn;
    ImageButton addFavoriteBtn;
    ImageButton deleteItemBtn;
    ImageButton copyBtn;
    ImageButton cutBtn;
    ImageButton renameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  // removes app name in tool bar
        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparentish
            /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            //toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//       //  Uncomment to show toggle icon in toolbar.
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set default values.
        selectionMode = false;   // use to identify if user is currently selecting files.
        openMenu = false;
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        popUpMenu = (LinearLayout) findViewById(R.id.popUpMenuNoSelect);
        addFileBtn=(ImageButton) findViewById(R.id.newfilebutton);
        addFolderBtn=(ImageButton) findViewById(R.id.newfolderbutton);
        addFavoriteBtn=(ImageButton) findViewById(R.id.addfavbutton);
        deleteItemBtn=(ImageButton) findViewById(R.id.deletebutton);
        copyBtn=(ImageButton) findViewById(R.id.copybutton);
        cutBtn=(ImageButton) findViewById(R.id.cutbutton);
        renameBtn=(ImageButton) findViewById(R.id.renamebutton);

        pathBarContainer = (LinearLayout) findViewById(R.id.pathbarContainer);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        fragment = (ListFragment) getFragmentManager()
                .findFragmentById(R.id.mainFragment);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openMenu) {
                    rotateFabBackward();
                    popUpMenu.startAnimation(outToLeftAnimation());
                    popUpMenu.setVisibility(View.GONE);
                } else {
                    rotateFabForward();
                    popUpMenu.startAnimation(inFromLeftAnimation());
                    popUpMenu.setVisibility(View.VISIBLE);
                }
                openMenu = !openMenu;
            }
        });

        // Check if app has permission to read external storage
        if (canMakeSmores()) {

            // Check if app already has permission.
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            // If app has no storage permission, request for it.
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        225);
            } else {
                hasStoragePermission = true;
            }
        } else {
            hasStoragePermission = true;
        }

        if (hasStoragePermission) {
            updateWholeScreen(root);
        }

    }

    public void rotateFabForward() {
        ViewCompat.animate(fab)
                .rotation(45.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }


    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(200L);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(200L);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private boolean canMakeSmores() {
        // Check if device OS is MM.
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    public void setSelectionMode(Integer position) {

        if (position != null) currentAdapter.selectSpecificItem(position);
        currentAdapter.hideCheckboxes(false);
        updateFab(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.edit_fab_icon, this.getTheme()));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.edit_fab_icon));
        }
        rotateFabForward();
        selectionMode = true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 225: {
                hasStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (hasStoragePermission) {
                    updateWholeScreen(root);
                } else {
                    ActivityCompat.finishAffinity(this);
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    public void updateWholeScreen(File file) {
        fragment.populateScreen(file);
        currentAdapter = fragment.getMyAdapter();
        updateFab(0);
    }


    public void setPathbar(String xpath) {

        // Drawable pathBarArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_right_black_24dp);
        String path = xpath.replace(Environment.getExternalStorageDirectory().toString(), "");
        // dynamically remove views attached to pathbar
        if (pathBarContainer.getChildCount() > 0) {
            pathBarContainer.removeAllViews();
        }
        final TextView homeTextView = new TextView(this);
        homeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.right_arrow_dark, 0, 0, 0);
        homeTextView.setGravity(Gravity.CENTER_VERTICAL);
        homeTextView.setCompoundDrawablePadding(20);
        homeTextView.setText("Internal Storage");
        homeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pathBarTextFocus));
        homeTextView.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {
                fragment.populateScreen(Environment.getExternalStorageDirectory());
            }

            @Override
            public void onDoubleClick(View v) {
            }

        });
        pathBarContainer.addView(homeTextView);

        final String[] splitCurrPath = path.split("/");
        for (int i = 0; i < splitCurrPath.length; i++) {

            final TextView textView = new TextView(this);
            textView.setText(splitCurrPath[i]);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (i < splitCurrPath.length - 1) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.right_arrow_dark, 0);
            }
            textView.setGravity(Gravity.CENTER_VERTICAL);
            final String[] xSplitCurrPath = xpath.split("/");

            textView.setOnClickListener(new DoubleClickListener() {

                @Override
                public void onSingleClick(View v) {
                    File newDir;
                    String newPath = "/";
                    int tag = 0;
                    for (int i = 0; i < xSplitCurrPath.length; i++) {
                        if (tag == 0) {
                            newPath = newPath + xSplitCurrPath[i] + "/";
                            if (textView.getText().equals(xSplitCurrPath[i])) {
                                tag = 1;
                            }
                        }
                    }
                    newPath = newPath.substring(1, newPath.length() - 1);

                    final String[] storagePath = Environment.getExternalStorageDirectory().getAbsolutePath().split("/");
                    String newPath2 = "";
                    int tag2 = 0;
                    for (int i = 0; i < storagePath.length - 1; i++) {
                        newPath2 = newPath2 + storagePath[i] + "/";
                        if (newPath.concat("/").equals(newPath2)) {
                            tag2 = 1;
                        }
                    }

                    if (tag2 == 0) {
                        newDir = new File(newPath);
                        fragment.populateScreen(newDir);
                    } else {
                        Snackbar.make(pathBarContainer, "This action requires root access.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

                @Override
                public void onDoubleClick(View v) {

                }

            });


            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pathBarTextFocus));


            pathBarContainer.addView(textView);

        }

        horizontalScrollView.post(new Runnable() {
            public void run() {
                horizontalScrollView.scrollTo(pathBarContainer.getWidth(), 0);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (selectionMode) {
                currentAdapter.hideCheckboxes(true);
                updateFab(0);
            } else {
                if (fragment.getCurrDir().equals(Environment.getExternalStorageDirectory())) {
                    super.onBackPressed();
                } else {
                    fragment.goUpFolder();
                }
            }
        }
    }

    public void updateFab(int fabmode) {
        // 0 - no selection
        // 1 - selection, one file
        // 2 - selection, two files
        switch (fabmode) {
            case 0:
                addFileBtn.setVisibility(View.VISIBLE);
                addFolderBtn.setVisibility(View.VISIBLE);
                addFavoriteBtn.setVisibility(View.VISIBLE);
                deleteItemBtn.setVisibility(View.GONE);
                copyBtn.setVisibility(View.GONE);
                cutBtn.setVisibility(View.GONE);
                renameBtn.setVisibility(View.GONE);
                break;
            case 1:
                addFileBtn.setVisibility(View.GONE);
                addFolderBtn.setVisibility(View.GONE);
                addFavoriteBtn.setVisibility(View.GONE);
                deleteItemBtn.setVisibility(View.VISIBLE);
                copyBtn.setVisibility(View.VISIBLE);
                cutBtn.setVisibility(View.VISIBLE);
                renameBtn.setVisibility(View.VISIBLE);
                break;
            case 2:
                addFileBtn.setVisibility(View.GONE);
                addFolderBtn.setVisibility(View.GONE);
                addFavoriteBtn.setVisibility(View.GONE);
                deleteItemBtn.setVisibility(View.VISIBLE);
                copyBtn.setVisibility(View.VISIBLE);
                cutBtn.setVisibility(View.VISIBLE);
                renameBtn.setVisibility(View.GONE);

             /*   if (currentAdapter.getCheckedCOunt() > 1) {
                    if (fam_select.getChildCount() == 10) fam_select.removeMenuButton(fab_rename);
                } else {
                    if (fam_select.getChildCount() != 10) fam_select.addMenuButton(fab_rename, 0);
                }*/

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
