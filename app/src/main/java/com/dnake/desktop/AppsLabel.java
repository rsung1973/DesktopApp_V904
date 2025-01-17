package com.dnake.desktop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.utils.Utils;
import com.dnake.v700.sys;
import com.dnake.v700.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class AppsLabel extends Activity {
    private ArrayList<AppData> mApps = new ArrayList<AppData>();
    private GridView mGrid;
    private LinearLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Handler h = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                loadApps();
            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                h.sendMessage(message);
                this.cancel();
            }
        };

        Timer t = new Timer(true);
        t.schedule(task, 100, 100);
    }

    private void loadApps() {
        layoutMain = (LinearLayout) findViewById(R.id.layout_main);
        layoutMain.setBackground(Utils.path2Drawable(this, Utils.getDesktopBgPath()));
        mGrid = (GridView) findViewById(R.id.app_grid);
        mGrid.setAdapter(new AppsAdapter(this, mApps));
        mGrid.setOnItemClickListener(mListener);

        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent it = new Intent(Intent.ACTION_MAIN, null);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(it, 0);

        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mApps != null) {
            mApps.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标

                if (!utils.eHomeMode && (pkgName.contains("com.google.android.inputmethod.pinyin") || pkgName.contains("com.android.email") || pkgName.contains("com.dnake.apps") || pkgName.contains("com.dnake.desktop") || pkgName.contains("com.dnake.talk") || pkgName.contains("com.dnake.smart") || pkgName.contains("com.dnake.security")))
                    continue;

                // 为应用程序的启动Activity 准备Intent
                it = new Intent();
                it.setComponent(new ComponentName(pkgName, activityName));

                // 创建一个AppInfo对象，并赋值
                AppData d = new AppData();
                d.text = appLabel;
                d.icon = icon;
                d.intent = it;
                mApps.add(d); // 添加至列表中
            }
            if (android.os.Build.VERSION.SDK_INT >= 27) {
                try {
                    Intent fmIt = new Intent();
                    //907M
                    fmIt.setComponent(new ComponentName("com.android.documentsui", "com.android.documentsui.files.FilesActivity"));
                    mApps.add(new AppData("File Manager", pm.getApplicationIcon("com.android.documentsui"), fmIt));
//                Intent camera2It = new Intent();
//                camera2It.setComponent(new ComponentName("com.android.camera2", "com.android.camera.CameraLauncher"));
//                mApps.add(new AppData("Camera", pm.getApplicationIcon("com.android.camera2"), camera2It));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class AppData {
        public String text;
        public Drawable icon;
        public Intent intent;

        public AppData() {
        }

        public AppData(String appName, Drawable icon, Intent intent) {
            this.text = appName;
            this.icon = icon;
            this.intent = intent;
        }
    }

    public class AppsAdapter extends ArrayAdapter<AppData> {
        private final LayoutInflater mInflater;

        public AppsAdapter(Context context, ArrayList<AppData> apps) {
            super(context, 0, apps);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) v = mInflater.inflate(R.layout.apps_text, parent, false);

            AppData d = mApps.get(position);
            TextView tv = (TextView) v;
            tv.setCompoundDrawablesWithIntrinsicBounds(null, toAppIcon(d.icon), null, null);
            tv.setText(d.text);
            return v;
        }

        public final int getCount() {
            return mApps.size();
        }

        public final AppData getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        public BitmapDrawable toAppIcon(Drawable drawable) {
            int w = (int) (56 * sys.density);
            int h = (int) (56 * sys.density);

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);

            drawable.draw(canvas);
            return new BitmapDrawable(bitmap);
        }
    }

    private OnItemClickListener mListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startActivity(mApps.get(position).intent);
        }
    };
}
