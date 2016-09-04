package com.example.user.androidexplorer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View menuLayout;
    ArcLayout arcLayout;
    boolean openMenu;
    private boolean hasStoragePermission;
    private boolean hasStorageWritePermission;
    private ListFragment fragment;
    private File root;
    private File newFolder;
    private LinearLayout pathBarContainer;
    private HorizontalScrollView horizontalScrollView;
    private HorizontalScrollView MenuHScrollView;
    private CustomAdapter currentAdapter;
    private boolean selectionMode;
    private FloatingActionButton fab;
    private LinearLayout popUpMenu;
    private ImageButton addFileBtn;
    private ImageButton addFolderBtn;
    private ImageButton addFavoriteBtn;
    private ImageButton deleteItemBtn;
    private ImageButton copyBtn;
    private ImageButton cutBtn;
    private ImageButton renameBtn;
    private ImageButton selectAllBtn;
    private ImageButton fileInfoBtn;
    private  boolean allItemsSelected;
    private boolean currPathFav;
    private Intent starterIntent;
    private ArrayList<File> dirList = new ArrayList<>();

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

        starterIntent = getIntent();
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
        allItemsSelected=false;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        popUpMenu = (LinearLayout) findViewById(R.id.popUpMenuNoSelect);
        addFileBtn=(ImageButton) findViewById(R.id.newfilebutton);
        addFolderBtn=(ImageButton) findViewById(R.id.newfolderbutton);
        addFavoriteBtn=(ImageButton) findViewById(R.id.addfavbutton);
        deleteItemBtn=(ImageButton) findViewById(R.id.deletebutton);
        copyBtn=(ImageButton) findViewById(R.id.copybutton);
        cutBtn=(ImageButton) findViewById(R.id.cutbutton);
        renameBtn=(ImageButton) findViewById(R.id.renamebutton);
        selectAllBtn=(ImageButton) findViewById(R.id.selectallbutton);
        fileInfoBtn=(ImageButton) findViewById(R.id.infoButton);

        pathBarContainer = (LinearLayout) findViewById(R.id.pathbarContainer);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        fragment = (ListFragment) getFragmentManager()
                .findFragmentById(R.id.mainFragment);
        MenuHScrollView=(HorizontalScrollView) findViewById(R.id.popUpMenu);

        addFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));


                final EditText folderNameEntry=new EditText((MainActivity.this));
                folderNameEntry.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                LinearLayout diagLayout=new LinearLayout(MainActivity.this);
                diagLayout.setOrientation(LinearLayout.VERTICAL);
                diagLayout.setPadding(50,0,50,0);
                diagLayout.addView(folderNameEntry);


                final AlertDialog d = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle)
                        .setView(diagLayout)
                        .setTitle(R.string.add_new_folder_title)
                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something


                                if (folderNameEntry.getText().toString().length()==0) {
                                    folderNameEntry.setError("Folder name cannot be blank.");
                                } else {
                                    newFolder=new File(fragment.getCurrDir() + "/" + folderNameEntry.getText().toString() );
                                    if (newFolder.exists()) {
                                        folderNameEntry.setError("Folder already exists.");
                                    } else {
                                        newFolder.mkdirs();
                                        updateWholeScreen(newFolder.getParentFile());
                                        d.dismiss();
                                        toggleMenu();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(horizontalScrollView, "New folder added!", Snackbar.LENGTH_SHORT)
                                                        .setAction("Action", null).show();
                                            }
                                        }, 150);
                                    }

                                }
                            }
                        });
                    }
                });

                d.show();



            }
        });

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));



                final EditText folderNameEntry=new EditText((MainActivity.this));
                folderNameEntry.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                LinearLayout diagLayout=new LinearLayout(MainActivity.this);
                diagLayout.setOrientation(LinearLayout.VERTICAL);
                diagLayout.setPadding(50,0,50,0);
                diagLayout.addView(folderNameEntry);


                final AlertDialog d = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle)
                        .setView(diagLayout)
                        .setTitle(R.string.add_new_file_title)
                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something


                                if (folderNameEntry.getText().toString().length()==0) {
                                    folderNameEntry.setError("File name cannot be blank.");
                                } else {
                                    newFolder=new File(fragment.getCurrDir().getAbsolutePath() + "/" + folderNameEntry.getText().toString() );
                                    if (newFolder.exists()) {
                                        folderNameEntry.setError("File already exists.");
                                    } else {
                                        try {
                                            newFolder.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        updateWholeScreen(newFolder.getParentFile());
                                        toggleMenu();
                                        d.dismiss();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Snackbar.make(horizontalScrollView, "New file added!", Snackbar.LENGTH_SHORT)
                                                        .setAction("Action", null).show();
                                            }
                                        }, 150);
                                    }

                                }
                            }
                        });
                    }
                });

                d.show();

            }
        });



        addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                Drawable drw;
                if (!currPathFav) {
                    drw= ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white);
                } else {
                    drw= ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_border_white);
                }

                addFavoriteBtn.setImageDrawable(drw);

                currPathFav=!currPathFav;
            }
        });

        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                // Initialise and design delete dialog elements.
                dirList=fragment.getDirList();
                ScrollView diagScroll=new ScrollView((MainActivity.this));
                LinearLayout diagLayout=new LinearLayout(MainActivity.this);
                diagLayout.setOrientation(LinearLayout.VERTICAL);
                diagLayout.setPadding(0,60,0,0);

                if (currentAdapter.getCheckedCOunt()>0) {

                    final boolean[] checkedItems=currentAdapter.getSelection();
                    for (int i=0;i<checkedItems.length;i++){
                        if (checkedItems[i]) {
                            TextView textView = new TextView(MainActivity.this);
                            textView.setText(" " + dirList.get(i).getName());
                            textView.setSingleLine();
                            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(60, 0, 60, 20); // llp.setMargins(left, top, right, bottom);
                            textView.setLayoutParams(llp);
                            textView.setCompoundDrawablesWithIntrinsicBounds(fragment.getFileIcon(i),0, 0,  0);
                            diagLayout.addView(textView);
                        }
                    }

                    diagScroll.addView(diagLayout);

                    // Create delete dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(R.string.delete_dialog_title );
                    builder.setView(diagScroll);
                    builder.setPositiveButton(R.string.positive_response_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                                                        for (int i=0;i<checkedItems.length;i++){
                                        if (checkedItems[i]) {
                                           dirList.get(i).delete();
                                        }
                                    }
                                    updateWholeScreen(fragment.getCurrDir());
                                  //  onBackPressed();
                                    dialog.dismiss();
                                    toggleMenu();


                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(horizontalScrollView, "Deleted!", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        }
                                    }, 150);
                                }
                            });
                    builder.setNegativeButton(R.string.negative_response_button, null);
                    builder.show();

                }
            }

        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
            }
        });

        cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
            }
        });

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
            }
        });


        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if (allItemsSelected) {
                            updateFab(3);
                            currentAdapter.removeSelection();
                        } else {
                            updateFab(2);
                            currentAdapter.selectAll();
                        }
                        allItemsSelected=!allItemsSelected;
                    }
                }, 100);


            }
        });

        fileInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
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
                updateWholeScreen(root);
            }
        }

    }


    public void updateWholeScreen(File file) {
        currPathFav=false;
        fragment.populateScreen(file);
        currentAdapter = fragment.getMyAdapter();
        updateFab(0);

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
            case 227: {
                hasStorageWritePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (hasStorageWritePermission) {
                    newFolder.mkdir();
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request

        }

        return;
    }

    public void toggleMenu() {

        if (openMenu) {
            if (selectionMode) {
                rotateFabFullBackward();
            } else {
                rotateFabBackward();
            }
            MenuHScrollView.startAnimation(outToLeftAnimation());
            MenuHScrollView.setVisibility(View.GONE);
        } else {
            if (selectionMode) {
                rotateFabFullForward();
            } else {
                rotateFabForward();
            }
            MenuHScrollView.startAnimation(inFromLeftAnimation());
            MenuHScrollView.setVisibility(View.VISIBLE);
        }

        openMenu = !openMenu;
    }

    public void rotateFabForward() {
        ViewCompat.animate(fab)
                .rotation(45.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(1.0F))
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(1.0F))
                .start();
    }

    public void rotateFabFullForward() {
        ViewCompat.animate(fab)
                .rotation(90.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(1.0F))
                .start();
    }

    public void rotateFabFullBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(1.0F))
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
        currentAdapter.hideCheckboxes(selectionMode);
        if (openMenu) rotateFabFullForward();
        selectionMode = !selectionMode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (selectionMode) {
                updateFab(1);


                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_white, this.getTheme()));
            } else {
                updateFab(0);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.add_white, this.getTheme()));
            }

        } else {

            if (selectionMode) {
          updateFab(1);
                if (currentAdapter.getCheckedCOunt()==1) updateFab(2);
            } else {
                updateFab(0);
                fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.add_white));
            }
        }

    }

public void setMenuType() {
    int cCount=currentAdapter.getCheckedCOunt();

    switch (cCount) {

        case 0: updateFab(3);
                break;
        case 1: updateFab(1);
               break;
        default: updateFab(2);
            break;
    }


}
    public void updateFab(int fabmode) {
        // 0 - no selection
        // 1 - selection, one file
        // 2 - selection, two files
        Log.d("CHECKEDCOUNT","Update Fab:" + fabmode);
        switch (fabmode) {
            case 0:setViewVisibility(addFileBtn,View.VISIBLE,0);
                   setViewVisibility(addFolderBtn,View.VISIBLE,50);
                setViewVisibility(addFavoriteBtn,View.VISIBLE,100);
                setViewVisibility(selectAllBtn,View.GONE,150);
                setViewVisibility(deleteItemBtn,View.GONE,200);
                setViewVisibility(copyBtn,View.GONE,250);
                setViewVisibility(cutBtn,View.GONE,300);
                setViewVisibility(renameBtn,View.GONE,350);
                setViewVisibility(fileInfoBtn,View.GONE,400);
                break;
            case 1:setViewVisibility(addFileBtn,View.GONE,0);
                setViewVisibility(addFolderBtn,View.GONE,0);
                setViewVisibility(addFavoriteBtn,View.GONE,0);
                setViewVisibility(selectAllBtn,View.VISIBLE,0);
                setViewVisibility(deleteItemBtn,View.VISIBLE,50);
                setViewVisibility(copyBtn,View.VISIBLE,100);
                setViewVisibility(cutBtn,View.VISIBLE,150);
                setViewVisibility(renameBtn,View.VISIBLE,200);
                setViewVisibility(fileInfoBtn,View.VISIBLE,250);

                break;
            case 2:setViewVisibility(addFileBtn,View.GONE,0);
                setViewVisibility(addFolderBtn,View.GONE,0);
                setViewVisibility(addFavoriteBtn,View.GONE,0);
                setViewVisibility(selectAllBtn,View.VISIBLE,0);
                setViewVisibility(deleteItemBtn,View.VISIBLE,0);
                setViewVisibility(copyBtn,View.VISIBLE,0);
                setViewVisibility(cutBtn,View.VISIBLE,0);
                setViewVisibility(renameBtn,View.GONE,200);
                setViewVisibility(fileInfoBtn,View.GONE,250);
                break;
            case 3: setViewVisibility(addFileBtn,View.GONE,0);
                setViewVisibility(addFolderBtn,View.GONE,0);
                setViewVisibility(addFavoriteBtn,View.GONE,0);
                setViewVisibility(selectAllBtn,View.VISIBLE,0);
                setViewVisibility(deleteItemBtn,View.GONE,250);
                setViewVisibility(copyBtn,View.GONE,200);
                setViewVisibility(cutBtn,View.GONE,150);
                setViewVisibility(renameBtn,View.GONE,100);
                setViewVisibility(fileInfoBtn,View.GONE,50);
                break;
            default:break;

        }

    }

    public void setViewVisibility(View v,int state,int delay) {
        final int vState=state;
        final View vView=v;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vState==View.GONE) {
                    // v.animate().alpha(0.0f);
                    if (vView.getVisibility()!=View.GONE){
                        vView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_disappear));
                        vView.setVisibility(View.GONE);
                    }

                } else {
                    //    v.animate().alpha(1.0f);
                    if (vView.getVisibility()!=View.VISIBLE) {
                        vView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_appear));
                        vView.setVisibility(View.VISIBLE);
                    }

                }
            }
        }, delay);




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
       // homeTextView.setCompoundDrawablePadding(20);
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
               // currentAdapter.hideCheckboxes(true);
                if (openMenu) toggleMenu();
                setSelectionMode(null);
            } else {

                    if (fragment.getCurrDir().equals(Environment.getExternalStorageDirectory())) {
                        super.onBackPressed();
                    } else {
                        fragment.goUpFolder();
                    }
                }
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
