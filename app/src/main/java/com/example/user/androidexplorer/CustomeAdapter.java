package com.example.user.androidexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


class CustomAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    public Integer DISPLAY_AS_THUMB = 1;
    public Integer DISPLAY_AS_ICON = 2;
    public Integer LIST_AS_DETAIL = 1;
    public Integer LIST_AS_GRID = 2;
    private ArrayList<String> result;
    private Context context;
    private ArrayList<Integer> imageId;
    private ArrayList<String> fSize;
    private ArrayList<Bitmap> thumbs;
    private ArrayList<Date> modDate;
    private String currPath;
    private SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm"); //"yyyy-MM-dd"
    private Boolean hideCB;
    private Boolean[] mCheckedState;
    private Integer imgDispType;
    private Integer reqWidth;
    private Integer reqHeight;
    private LruCache<String, Bitmap> mMemoryCache;
    private Integer viewType;
    private int xCoord;
    private int yCoord;
    private ListFragment callFragment;

    public CustomAdapter(Integer viewTyp, LruCache<String, Bitmap> memCache, Activity mainActivity, ListFragment fragment, ArrayList<String> prgmNameList, ArrayList<Integer> prgmImages, ArrayList<Date> lastDate, ArrayList<String> fileSize, String path, Integer imgDisplayType) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        mCheckedState = new Boolean[prgmNameList.size()];
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = false;
        }
        imgDispType = imgDisplayType;
        context =  mainActivity;
        callFragment = fragment;
        imageId = prgmImages;
        currPath = path;
        modDate = lastDate;
        fSize = fileSize;
        hideCB = true;
        mMemoryCache = memCache;
        viewType = viewTyp;
        if (viewType.equals(LIST_AS_DETAIL)) {
            reqWidth = 128;
            reqHeight = 128;
        } else if (viewType.equals(LIST_AS_GRID)) {
            reqWidth = 512;
            reqHeight = 512;
        }
        //  thumbs=thumbList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(File data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final File bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public ArrayList<String> getNameList() {
        return result;
    }

    public ArrayList<Integer> getIconList() {
        return imageId;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    public boolean[] getSelection() {

        boolean[] checked = new boolean[mCheckedState.length];

        for (int i = 0; i < mCheckedState.length; i++) {
            checked[i] = mCheckedState[i];
        }
        return checked;
    }

    public void setSelection(boolean[] selection) {

        for (int i = 0; i < selection.length; i++) {
            mCheckedState[i] = selection[i];
        }
        notifyDataSetChanged();
    }

    public void selectSpecificItem(int selection) {


        mCheckedState[selection] = true;
        notifyDataSetChanged();
    }

    public int getCheckedCOunt() {
        int counter = 0;
        for (int i = 0; i < mCheckedState.length; i++) {
            if (mCheckedState[i]) counter++;
        }
        return counter;
    }

    public void removeSelection() {
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = true;
        }
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void hideCheckboxes(boolean hidden) {
        hideCB = hidden;
        if (hidden) removeSelection();
    }

    public void rePaintScreen() {
        notifyDataSetChanged();
    }

    public void refreshEvents(Integer viewTyp, LruCache<String, Bitmap> memCache, Activity mainActivity, ListFragment fragment, ArrayList<String> prgmNameList, ArrayList<Integer> prgmImages, ArrayList<Date> lastDate, ArrayList<String> fileSize, String path, Integer imgDisplayType) {
        result = prgmNameList;
        context = mainActivity;
        callFragment = fragment;
        imageId = prgmImages;
        currPath = path;
        modDate = lastDate;
        fSize = fileSize;
        imgDispType = imgDisplayType;
        hideCB = true;
        mMemoryCache = memCache;
        viewType = viewTyp;
        if (viewType.equals(LIST_AS_DETAIL)) {
            reqWidth = 128;
            reqHeight = 128;
        } else if (viewType.equals(LIST_AS_GRID)) {
            reqWidth = 512;
            reqHeight = 512;
        }
        // thumbs=thumbList;
        mCheckedState = new Boolean[prgmNameList.size()];
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = false;
        }
        notifyDataSetChanged();
    }

    private String getExtension(File currFile) {
        String[] fileNameArray;
        String extension;

        if (currFile.getName().contains(".")) {
            fileNameArray = currFile.getName().split("\\.");
            extension = fileNameArray[fileNameArray.length - 1];
        } else {
            extension = "xxx";
        }
        return extension;
    }

    public void loadBitmap(File img, ImageView imageView,ProgressBar progressBar,Context bmContext) {


          /*  Bitmap mPlaceHolderBitmap = BitmapFactory.decodeResource(bmContext.getResources(),
                    R.drawable.blank);*/

        Drawable defDrawable = ContextCompat.getDrawable(bmContext, R.drawable.blank);
        Bitmap mPlaceHolderBitmap= drawableToBitmap(defDrawable);
        Log.d("BITMAPXXX","Holder bitmap xxxyyy:" + mPlaceHolderBitmap);

            String imageKey = img.getAbsolutePath().toString();
            final Bitmap bitmap = getBitmapFromMemCache(imageKey);

            if (bitmap != null) {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } else {

                if (cancelPotentialWork(img, imageView)) {
                    final BitmapWorkerTask task = new BitmapWorkerTask(mMemoryCache, imageView,progressBar, reqWidth,reqHeight,mPlaceHolderBitmap);
                    final AsyncDrawable asyncDrawable =
                            new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
                    imageView.setImageDrawable(asyncDrawable);
                    task.execute(img);
                }
            }

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder;

        //final View rowView;
        if (convertView == null) {
            holder = new Holder();
            if (viewType.equals(1)) {
                convertView = inflater.inflate(R.layout.item_list_detail, null);
                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
                holder.info1 = (TextView) convertView.findViewById(R.id.info1);
                holder.info2 = (TextView) convertView.findViewById(R.id.info2);
                holder.img = (ImageView) convertView.findViewById(R.id.imageView1);
                holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.progressBar=(ProgressBar) convertView.findViewById(R.id.progressBar);
            } else {
                convertView = inflater.inflate(R.layout.item_list_grid, null);
                holder.tv = (TextView) convertView.findViewById(R.id.textView1_grid);
                holder.info1 = (TextView) convertView.findViewById(R.id.info1_grid);
                holder.info2 = (TextView) convertView.findViewById(R.id.info2_grid);
                holder.img = (ImageView) convertView.findViewById(R.id.imageView1_grid);
                holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox_grid);
            }


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.img.setVisibility(View.GONE);

        // keep check after scroll
        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setFocusable(false);

        if (mCheckedState[position].equals(true)) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
                int getPosition = (Integer) buttonView.getTag();
                mCheckedState[getPosition] = buttonView.isChecked();
                //((MainActivity) context).setMenuType();
            }
        });
        holder.cb.setTag(position);
        // check if checkboxes need to be shown or not
        if (hideCB) {
            holder.cb.setVisibility(View.GONE);
        } else {
            holder.cb.setVisibility(View.VISIBLE);

        }

        // add values to viewsdadasd
        // java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        String strDate = fmt.format(modDate.get(position));
        // String strDate = dateFormat.format(modDate.get(position));;
        holder.tv.setText(result.get(position));
        holder.info1.setText(strDate);
        holder.info2.setText(fSize.get(position));

        if (imgDispType.equals(DISPLAY_AS_ICON)) {

            Drawable mDrawable = ContextCompat.getDrawable(context,imageId.get(position));
            Drawable wrappedDrawable = DrawableCompat.wrap(mDrawable);
            wrappedDrawable = wrappedDrawable.mutate();
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.list_icon_color));
            holder.img.setImageDrawable(wrappedDrawable);

        } else {

            File currFile = new File(currPath + "/" + result.get(position));

            if (currFile.isFile()) {

                String ext = getExtension(currFile);

                switch (ext) {
                    case "jpg":
                        loadBitmap(currFile, holder.img,holder.progressBar,context);
                        break;
                    case "jpeg":
                        loadBitmap(currFile, holder.img,holder.progressBar,context);
                        break;
                    case "png":
                        loadBitmap(currFile, holder.img,holder.progressBar,context);
                        break;
                    case "bmp":
                        loadBitmap(currFile, holder.img,holder.progressBar,context);
                        break;
                    case "apk":  ;
                            String filePath = currFile.getPath();
                            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                            if(packageInfo != null) {
                                ApplicationInfo appInfo = packageInfo.applicationInfo;
                                if (Build.VERSION.SDK_INT >= 8) {
                                    appInfo.sourceDir = filePath;
                                    appInfo.publicSourceDir = filePath;
                                }
                                final Drawable icon = appInfo.loadIcon(context.getPackageManager());
                                holder.img.setImageDrawable(icon);
                                holder.progressBar.setVisibility(View.GONE);
                                holder.img.setVisibility(View.VISIBLE);
                            }
                        break;
                    default:
                        holder.progressBar.setVisibility(View.GONE);
                        holder.img.setVisibility(View.VISIBLE);

                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                        if (mimeType != null || mimeType == "xxx") {
                            final Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(currFile), mimeType);
                            PackageManager pm = context.getPackageManager();
                            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

                            if (pm.getApplicationLabel(mInfo.activityInfo.applicationInfo).equals("Android system")) {
                                Drawable mDrawable = ContextCompat.getDrawable(context,imageId.get(position));
                                Drawable wrappedDrawable = DrawableCompat.wrap(mDrawable);
                                wrappedDrawable = wrappedDrawable.mutate();
                                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.list_icon_color));
                                holder.img.setImageDrawable(wrappedDrawable);
                            } else {
                                final Drawable icon = mInfo.loadIcon(context.getPackageManager());
                                holder.img.setImageDrawable(icon);
                            }

                        } else {
                            Drawable mDrawable = ContextCompat.getDrawable(context,imageId.get(position));
                            Drawable wrappedDrawable = DrawableCompat.wrap(mDrawable);
                            wrappedDrawable = wrappedDrawable.mutate();
                            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.list_icon_color));
                            holder.img.setImageDrawable(wrappedDrawable);
                            //holder.img.setImageResource(imageId.get(position));

                        }
                        break;
                }

            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setImageResource(imageId.get(position));

            }

        }


        // set listenrs
        final File dir = new File(currPath + "/" + result.get(position));
        convertView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {

            }

            @Override
            public void onSingleClick(View v) {
                if (!hideCB) {
                    holder.cb.setChecked(!holder.cb.isChecked());
                    ((MainActivity) context).updateFab(2);
                } else {
                    if (dir.isDirectory()) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                callFragment.populateScreen(dir);
                            }
                        }, 150);

                    } else {
                        // Open file.
                        String ext = getExtension(dir);
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                        if (mimeType != null || mimeType == "xxx") {

                            final Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(dir), mimeType);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    context.startActivity(i);
                                }
                            }, 120);


                        } else {
//                           /* Snackbar.make(v, R.string.no_app_in_phone ,  Snackbar.LENGTH_SHORT)
//                                    .setAction("Action", null).show();*/
                        }
                    }
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (hideCB) {
                    ((MainActivity) context).setSelectionMode(position);
                }
                //  ((MainActivity) context).setMenuType();*/
                return true;
            }
        });
        return convertView;
    }

    public class Holder {
        TextView tv;
        TextView info1;
        TextView info2;
        // TextView info3;
        ProgressBar progressBar;
        ImageView img;
        CheckBox cb;

    }


}


class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap>   {
    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<ProgressBar> progressBarWeakReference;
    File data;
    LruCache<String, Bitmap> mMemoryCache;
    int width;
    int height;
    Bitmap defBM;


    public BitmapWorkerTask(LruCache<String, Bitmap> memCache, ImageView imageView, ProgressBar progressBar,int reqWidth, int reqHeight,Bitmap bm) {
        imageViewReference = new WeakReference<>(imageView);
        progressBarWeakReference= new WeakReference<>(progressBar);
        mMemoryCache = memCache;
        width = reqWidth;
        height = reqHeight;
        defBM=bm;
    }

    public static Bitmap decodeSampledBitmapFromResource(File img, int reqWidth, int reqHeight,Bitmap bm) {

        BitmapFactory.Options testOptions = new BitmapFactory.Options();
        testOptions.inJustDecodeBounds = true;
        Bitmap testBM=BitmapFactory.decodeFile(img.toString(),testOptions);
        Log.d("OPTIONS","OPTION:" + testOptions.outHeight);
        Bitmap returnBM;
        if (testOptions.outHeight!=-1) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(img.toString(), options);

            // Calculate inSampleSize

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(img.toString(), options);

        } else {
           return bm;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    @Override
    protected Bitmap doInBackground(File... params) {

        data = params[0];
        Bitmap sqrBitmap;
        final Bitmap bitmap = decodeSampledBitmapFromResource(data, width, height,defBM);


        // get square bitmap
        if (bitmap!=null) {
            if (bitmap.getWidth() >= bitmap.getHeight()) {

                sqrBitmap = Bitmap.createBitmap(
                        bitmap,
                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                        0,
                        bitmap.getHeight(),
                        bitmap.getHeight()
                );
            } else {

                sqrBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                        bitmap.getWidth(),
                        bitmap.getWidth()
                );
            }
            addBitmapToMemoryCache(data.getAbsolutePath(), sqrBitmap);
        } else {
            sqrBitmap=null;
        }

        return sqrBitmap;

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final ProgressBar progressBar=progressBarWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

            }
        }
    }
}

class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         BitmapWorkerTask bitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkerTaskReference =
                new WeakReference<>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}
