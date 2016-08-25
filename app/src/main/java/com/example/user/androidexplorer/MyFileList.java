package com.example.user.androidexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyFileList {


    public boolean FILE_NAME_DESC = false;
    public boolean FILE_NAME_ASC = true;
    // fields
    Context mContext;
    HashMap<Integer, File> fileDirList;
    HashMap<Integer, Date> fileModDateList;
    HashMap<Integer, Integer> iconFileDirList;
    HashMap<Integer, Float> fileSizeList;
    HashMap<Integer, String> fileSizeListString;
    Integer fileSize;
    // constructors

    public MyFileList(Context context, ArrayList<File> fileList) {

        mContext = context;
        fileSize = fileList.size();
        fileDirList = new LinkedHashMap<Integer, File>();
        fileModDateList = new LinkedHashMap<Integer, Date>();
        iconFileDirList = new LinkedHashMap<Integer, Integer>();
        fileSizeList = new LinkedHashMap<Integer, Float>();
        fileSizeListString = new LinkedHashMap<Integer, String>();

        Float zeroVal = 0f;

        // Populate other arrays
        for (int i = 0; i < fileList.size(); i++) {

            // add File
            fileDirList.put(i, fileList.get(i));

            // add date
            fileModDateList.put(i, new Date(fileList.get(i).lastModified()));
            //  Log.e("ADAPTER_FETCHED_DATE", fileList.get(i).getName() + "'s last modified date is " + new Date(+fileList.get(i).lastModified()) );


            // add icon
            iconFileDirList.put(i, getFileIcon(fileList.get(i)));

            // add size
            if (fileList.get(i).isDirectory()) {
                fileSizeList.put(i, zeroVal);
            } else {
                fileSizeList.put(i, (float) fileList.get(i).length());
            }

            // add size in string
            fileSizeListString.put(i, getFileSize(fileList.get(i)));
        }

    }

    private static HashMap<Integer, File> sortFileMapByName(Map<Integer, File> unsortMap, final boolean order) {
        // order true= ASC, false=DESC
        List<Map.Entry<Integer, File>> list = new LinkedList<Map.Entry<Integer, File>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Integer, File>>() {
            public int compare(Map.Entry<Integer, File> o1,
                               Map.Entry<Integer, File> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<Integer, File> sortedMap = new LinkedHashMap<Integer, File>();
        for (Map.Entry<Integer, File> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private static HashMap<Integer, Date> sortFileMapByDate(Map<Integer, Date> unsortMap, final boolean order) {
        // order true= ASC, false=DESC
        List<Map.Entry<Integer, Date>> list = new LinkedList<Map.Entry<Integer, Date>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Date>>() {
            public int compare(Map.Entry<Integer, Date> o1,
                               Map.Entry<Integer, Date> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<Integer, Date> sortedMap = new LinkedHashMap<Integer, Date>();
        for (Map.Entry<Integer, Date> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private static HashMap<Integer, Float> sortFileMapBySize(Map<Integer, Float> unsortMap, final boolean order) {
        // order true= ASC, false=DESC
        List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
            public int compare(Map.Entry<Integer, Float> o1,
                               Map.Entry<Integer, Float> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
        for (Map.Entry<Integer, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public Integer size() {
        return fileSize - 1;
    }

    public File getFile(Integer key) {

        return fileDirList.get(key);
    }

    public Date getDate(Integer key) {

        return fileModDateList.get(key);

    }

    public Bitmap getThumb(Integer key) {
        final int THUMBSIZE = 64;
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileDirList.get(key).toString()), THUMBSIZE, THUMBSIZE);


    }

    public String getSize(Integer key) {
        return fileSizeListString.get(key);
    }

    public Integer getIcon(Integer key) {
        return iconFileDirList.get(key);
    }

    public void sortByFileName(boolean order) {

        HashMap<Integer, File> sortedList = new LinkedHashMap<Integer, File>();
        sortedList = sortFileMapByName(fileDirList, order);

        HashMap<Integer, File> tempfileDirList = new LinkedHashMap<Integer, File>();
        HashMap<Integer, Date> tempfileModDateList = new LinkedHashMap<Integer, Date>();
        HashMap<Integer, Integer> tempiconFileDirList = new LinkedHashMap<Integer, Integer>();
        HashMap<Integer, Float> tempfileSizeList = new LinkedHashMap<Integer, Float>();
        HashMap<Integer, String> tempfileSizeListString = new LinkedHashMap<Integer, String>();

        Iterator it = sortedList.entrySet().iterator();
        Integer i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            // add new entries
            tempfileDirList.put(i, fileDirList.get(pair.getKey()));
            tempfileModDateList.put(i, fileModDateList.get(pair.getKey()));
            tempiconFileDirList.put(i, iconFileDirList.get(pair.getKey()));
            tempfileSizeList.put(i, fileSizeList.get(pair.getKey()));
            tempfileSizeListString.put(i, fileSizeListString.get(pair.getKey()));
            i++;
            it.remove(); // avoids a ConcurrentModificationException
        }

        fileDirList = null;
        fileModDateList = null;
        iconFileDirList = null;
        fileSizeList = null;
        fileSizeListString = null;

        fileDirList = tempfileDirList;
        fileModDateList = tempfileModDateList;
        iconFileDirList = tempiconFileDirList;
        fileSizeList = tempfileSizeList;
        fileSizeListString = tempfileSizeListString;

    }

    public void sortByFileDate(boolean order) {

        HashMap<Integer, Date> sortedList = new LinkedHashMap<Integer, Date>();
        sortedList = sortFileMapByDate(fileModDateList, order);

        HashMap<Integer, File> tempfileDirList = new LinkedHashMap<Integer, File>();
        HashMap<Integer, Date> tempfileModDateList = new LinkedHashMap<Integer, Date>();
        HashMap<Integer, Integer> tempiconFileDirList = new LinkedHashMap<Integer, Integer>();
        HashMap<Integer, Float> tempfileSizeList = new LinkedHashMap<Integer, Float>();
        HashMap<Integer, String> tempfileSizeListString = new LinkedHashMap<Integer, String>();

        Iterator it = sortedList.entrySet().iterator();
        Integer i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            // add new entries
            tempfileDirList.put(i, fileDirList.get(pair.getKey()));
            tempfileModDateList.put(i, fileModDateList.get(pair.getKey()));
            tempiconFileDirList.put(i, iconFileDirList.get(pair.getKey()));
            tempfileSizeList.put(i, fileSizeList.get(pair.getKey()));
            tempfileSizeListString.put(i, fileSizeListString.get(pair.getKey()));
            i++;
            it.remove(); // avoids a ConcurrentModificationException
        }

        fileDirList = null;
        fileModDateList = null;
        iconFileDirList = null;
        fileSizeList = null;
        fileSizeListString = null;

        fileDirList = tempfileDirList;
        fileModDateList = tempfileModDateList;
        iconFileDirList = tempiconFileDirList;
        fileSizeList = tempfileSizeList;
        fileSizeListString = tempfileSizeListString;

    }

    public void sortByFileSize(boolean order) {

        HashMap<Integer, Float> sortedList = new LinkedHashMap<Integer, Float>();
        sortedList = sortFileMapBySize(fileSizeList, order);

        HashMap<Integer, File> tempfileDirList = new LinkedHashMap<Integer, File>();
        HashMap<Integer, Date> tempfileModDateList = new LinkedHashMap<Integer, Date>();
        HashMap<Integer, Integer> tempiconFileDirList = new LinkedHashMap<Integer, Integer>();
        HashMap<Integer, Float> tempfileSizeList = new LinkedHashMap<Integer, Float>();
        HashMap<Integer, String> tempfileSizeListString = new LinkedHashMap<Integer, String>();

        Iterator it = sortedList.entrySet().iterator();
        Integer i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            // add new entries
            tempfileDirList.put(i, fileDirList.get(pair.getKey()));
            tempfileModDateList.put(i, fileModDateList.get(pair.getKey()));
            tempiconFileDirList.put(i, iconFileDirList.get(pair.getKey()));
            tempfileSizeList.put(i, fileSizeList.get(pair.getKey()));
            tempfileSizeListString.put(i, fileSizeListString.get(pair.getKey()));
            i++;
            it.remove(); // avoids a ConcurrentModificationException
        }

        fileDirList = null;
        fileModDateList = null;
        iconFileDirList = null;
        fileSizeList = null;
        fileSizeListString = null;

        fileDirList = tempfileDirList;
        fileModDateList = tempfileModDateList;
        iconFileDirList = tempiconFileDirList;
        fileSizeList = tempfileSizeList;
        fileSizeListString = tempfileSizeListString;

    }

    // Utility methods
    private String getFileSize(final File file) {
        float fileSize;
        String value;

        if (file.isDirectory()) {

            return "";

        } else {

            fileSize = file.length();
            if (fileSize <= 1048576) {
                value = String.format("%.2f", fileSize / 1048576) + " kb";
            } else if (fileSize <= 1073741824) {
                value = String.format("%.2f", ((fileSize) / 1024) / 1024) + " mb";

            } else {
                value = String.format("%.2f", (((fileSize) / 1024) / 1024) / 1024) + " gb";
            }

            return value;
        }

    }

    private Integer getFileIcon(File dir) {

        Integer returnVal;

        if (dir.isDirectory()) {
            returnVal = R.drawable.folder;
        } else {
            String extension1 = "xxx";
            String[] fileNameArray;

            if (dir.getName().contains(".")) {
                fileNameArray = dir.getName().split("\\.");
                extension1 = fileNameArray[fileNameArray.length - 1];
            } else {
                extension1 = "1";
            }

            String extension = "blank";
            if (!isInteger(extension1)) {
                switch (extension1) {
                    case "class":
                        extension = "xclass";
                        break;
                    case "7z":
                        extension = "x7z";
                        break;
                    case "3gp":
                        extension = "x3gp";
                        break;
                    case "3g2":
                        extension = "x3g2";
                        break;
                    case "3ds":
                        extension = "x3ds";
                        break;
                    case "3dm":
                        extension = "x3dm";
                        break;
                    case "1":
                        extension = "blank";
                        break;
                    case "nomedia":
                        extension = "blank";
                        break;
                    default:
                        extension = extension1;
                        break;
                }
            }

            int checkExistence = mContext.getResources().getIdentifier(extension, "drawable", mContext.getPackageName());
            final Integer resID;
            // Log.e("RESOURCE_ID", "Extension is : " + extension + ", Resource ID is :" + checkExistence);
            if (checkExistence != 0) {  // the resouce exists...
                resID = mContext.getResources().getIdentifier(extension, "drawable", mContext.getPackageName());

            } else {  // checkExistence == 0  // the resouce does NOT exist!!
                resID = R.drawable.blank;
            }

            returnVal = resID;

        }
        return returnVal;
    }

}
