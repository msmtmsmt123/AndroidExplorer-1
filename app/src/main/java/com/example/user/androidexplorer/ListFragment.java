package com.example.user.androidexplorer;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class ListFragment extends Fragment {

    private static final boolean HIDEHIDDENFILES = false;
    private static final boolean SHOWHIDDENFILES = true;
    private ArrayList<File> fileList = new ArrayList<>();
    private ArrayList<File> dirList = new ArrayList<>();
    private ArrayList<String> fileDirLlist = new ArrayList<>();
    private ArrayList<String> fileSizeList = new ArrayList<>();
    private ArrayList<Integer> iconFileDirLlist = new ArrayList<>();
    private ArrayList<Date> fileModDate = new ArrayList<>();
    private ArrayList<Bitmap> thumbs = new ArrayList<>();
    private boolean showHiddenObjects;
    private MyFileList myFiles;
    private MyFileList myDirs;
    private Integer sortStyle;
    private Boolean folderFirst;
    private Integer listViewType;
    private CustomAdapter myAdapter;

    private LruCache<String, Bitmap> mMemoryCache;
    private ListView lv;
    private GridView gv;
    private LinearLayout frame;
    private Integer imgDisplayType;
    private File currDir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,
                container, false);

        showHiddenObjects = false; // set default hidden object setting
        sortStyle = 1;             // set default sort setting
        folderFirst = true;        // set folder, file arrangement setting
        listViewType = 1;          // set display type  default setting
        imgDisplayType = 1;
        // Bitmap cache
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        frame = (LinearLayout) view.findViewById(R.id.Frame_Container);
        setViewType();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String path = bundle.getString("currentPath"); // get current path
            showHiddenObjects = bundle.getBoolean("hideObjects"); // get current hidden object setting
            populateScreen(new File(path));
            currDir = new File(path);
        }
    }

    public void showHiddenFiles(boolean ishidden) {

    }

    public void goUpFolder() {
        File upFolder = currDir.getParentFile();
        populateScreen(upFolder);
    }

    public ArrayList<File> getDirList() {
        return dirList;
    }

    public Integer getFileIcon(int i) {

    return iconFileDirLlist.get(i);

    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public CustomAdapter getMyAdapter() {
        return myAdapter;
    }

    public File getCurrDir() {
        return currDir;
    }

    public void populateScreen(File file) {

        ((MainActivity) getActivity()).setPathbar(file.toString());
        currDir = file;
        getfile(file);
        dirList.addAll(fileList);

        fileDirLlist.clear();
        iconFileDirLlist.clear();
        fileSizeList.clear();
        thumbs.clear();

        switch (sortStyle) {

            case 1:
                myFiles.sortByFileName(myFiles.FILE_NAME_ASC);
                myDirs.sortByFileName(myFiles.FILE_NAME_ASC);
                break;
            case 2:
                myFiles.sortByFileName(myFiles.FILE_NAME_DESC);
                myDirs.sortByFileName(myFiles.FILE_NAME_DESC);
                break;
            case 3:
                myFiles.sortByFileSize(myFiles.FILE_NAME_ASC);
                myDirs.sortByFileSize(myFiles.FILE_NAME_ASC);
                break;
            case 4:
                myFiles.sortByFileSize(myFiles.FILE_NAME_DESC);
                myDirs.sortByFileSize(myFiles.FILE_NAME_DESC);
                break;
            case 5:
                myFiles.sortByFileDate(myFiles.FILE_NAME_ASC);
                myDirs.sortByFileDate(myFiles.FILE_NAME_ASC);
                break;
            case 6:
                myFiles.sortByFileDate(myFiles.FILE_NAME_DESC);
                myDirs.sortByFileDate(myFiles.FILE_NAME_DESC);
                break;
        }

        if (folderFirst) {

            for (Integer i = 0; i <= myDirs.size(); i++) {
                fileDirLlist.add(myDirs.getFile(i).getName());
                iconFileDirLlist.add(myDirs.getIcon(i));
                fileSizeList.add(myDirs.getSize(i));
                fileModDate.add(myDirs.getDate(i));

            }

            for (Integer i = 0; i <= myFiles.size(); i++) {
                fileDirLlist.add(myFiles.getFile(i).getName());
                iconFileDirLlist.add(myFiles.getIcon(i));
                fileSizeList.add(myFiles.getSize(i));
                fileModDate.add(myFiles.getDate(i));
            }

        } else {

            for (Integer i = 0; i <= myFiles.size(); i++) {
                fileDirLlist.add(myFiles.getFile(i).getName());
                iconFileDirLlist.add(myFiles.getIcon(i));
                fileSizeList.add(myFiles.getSize(i));
                fileModDate.add(myFiles.getDate(i));
            }
            for (Integer i = 0; i <= myDirs.size(); i++) {
                fileDirLlist.add(myDirs.getFile(i).getName());
                iconFileDirLlist.add(myDirs.getIcon(i));
                fileSizeList.add(myDirs.getSize(i));
                fileModDate.add(myDirs.getDate(i));

            }
        }

        if (myAdapter != null) {

            myAdapter.refreshEvents(listViewType, mMemoryCache, getActivity(),getActivity().getApplicationContext(),this, fileDirLlist, iconFileDirLlist, fileModDate, fileSizeList, file.toString(), imgDisplayType);
            /*updateFab(0);
            selectionMode=false;*/


        } else {

            myAdapter = new CustomAdapter(listViewType, mMemoryCache, getActivity(),getActivity().getApplicationContext(), this, fileDirLlist, iconFileDirLlist, fileModDate, fileSizeList, file.toString(), imgDisplayType);
            if (listViewType.equals(1)) {
                lv.setAdapter(myAdapter);
            } else {
                gv.setAdapter(myAdapter);
            }

        }

    }




    public void getfile(File dir) {


        dirList.clear();
        fileList.clear();

        File listFile[] = dir.listFiles();

        File currFile;

        Arrays.sort(listFile); // Sorts list alphabetically by default

        if (listFile != null) {
            if (listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {

                    currFile = listFile[i];

                    if (!showHiddenObjects) {
                        if (currFile.isFile()) {
                            if (!currFile.isHidden()) fileList.add(currFile);
                        } else {
                            if (!currFile.isHidden()) dirList.add(currFile);
                        }

                    } else {
                        if (currFile.isFile()) {
                            fileList.add(currFile);
                        } else {
                            dirList.add(currFile);
                        }
                    }

                }
            }
        }

        // return fileList;
        myDirs = new MyFileList(getActivity(), dirList);
        myFiles = new MyFileList(getActivity(), fileList);
    }

    public void setViewType() {
        //ViewGroup parent = (ViewGroup) currAttachedViewType.getParent();
        frame.removeAllViews();
        View currAttachedViewType;
        if (listViewType.equals(1)) {
            currAttachedViewType = getActivity().getLayoutInflater().inflate(R.layout.content_main_detail, null);
            frame.addView(currAttachedViewType);
            lv = (ListView) frame.findViewById(R.id.mainListView);

        } else {
            currAttachedViewType = getActivity().getLayoutInflater().inflate(R.layout.content_main_grid, null);
            frame.addView(currAttachedViewType);
            gv = (GridView) frame.findViewById(R.id.mainGridView);
        }

    }

}
