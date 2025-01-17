package com.dnake.desktop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dnake.misc.AccuWeather;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;
import com.dnake.widget.Button2;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;

@SuppressLint({"HandlerLeak", "DefaultLocale"})
public class MainActivity extends Activity {
    private TextView year, month, day, week, hour, min;
    private TextView msg, sms, miss;
    private Boolean wOK = false;
    private Handler e_timer = null;
    private long dTs = 0, wTs = 0, sTs = 0;

    private int sms_nread = -1;
    private int msg_nread = -1;
    private int miss_nread = -1;

    private int security = -1;

    private HorizontalScrollView scroll;
    private boolean currentAlarmed = false;
    private static MainActivity __Main;
    private static Thread __ServiceCheck;
    //    private String doorStatusIdentFilter = "com.dnake.doorStatus";
    private boolean addedSmartApp = false;
    private static final String __ADDED_SMART_APP = "com.gemteks.litenetrcu";   //"com.netvox.zigbulter.pad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        __Main = this;

        if (utils.eHomeMode) {
            // 中华电信layout
            setContentView(R.layout.main_tw);
        } else if (sys.limit() >= 970 && sys.limit() < 990) {
            setContentView(R.layout.main_tw);

            RelativeLayout r = (RelativeLayout) this
                    .findViewById(R.id.main_bkg);
            r.setBackgroundResource(R.drawable.main_v970_bkg);
        } else
            setContentView(R.layout.main);

        Resources r = this.getResources();
        sys.density = r.getDisplayMetrics().density;

        year = (TextView) findViewById(R.id.main_time_year);
        month = (TextView) this.findViewById(R.id.main_time_month);
        day = (TextView) this.findViewById(R.id.main_time_day);
        week = (TextView) findViewById(R.id.main_time_week);
        hour = (TextView) this.findViewById(R.id.main_time_hour);
        min = (TextView) this.findViewById(R.id.main_time_min);
        msg = (TextView) findViewById(R.id.main_prompt_msg_text);
        sms = (TextView) findViewById(R.id.main_prompt_sms_text);
        miss = (TextView) findViewById(R.id.main_prompt_talk_text);

        scroll = (HorizontalScrollView) this
                .findViewById(R.id.main_scroll_view);

        Button2 b = (Button2) this.findViewById(R.id.main_quick_apps);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppsLabel.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_monitor);
/*        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = getPackageManager().getLaunchIntentForPackage(
                        "com.android.gallery3d");
                startActivity(it);
            }
        });*/
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk",
                        "com.dnake.talk.MonitorLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);

                ///2020-09-16 demo
//                Intent intent = new Intent(MainActivity.this, MainSecom.class);
//                startActivity(intent);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_secom);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ///2020-09-16 demo
                Intent intent = new Intent(MainActivity.this, MainSecom.class);
                startActivity(intent);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_elev);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.smart",
                        "com.dnake.smart.ElevatorLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
            }
        });
//        //大安傳家特別版
//        b.setBackgroundResource(R.drawable.dian_home);
//        b.setEnabled(false);
//        b.setText("");
//        //
/**
 *      2021-01-08  警報記錄查詢
 */
        b = (Button2) this.findViewById(R.id.main_quick_logs);

        b.setBackgroundResource(R.drawable.main_event_log);
        b.setText("保全歷史記錄");

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
//                        Intent it = new Intent("com.awtek.messageGuard.GasReport");
//                        sendBroadcast(it);
                    Intent it = new Intent();
                    ComponentName comp = new ComponentName("com.awtek.messageGuard",
                            "crc648040af3feb41865b.WebContentActivity");
                    it.setComponent(comp);
                    it.putExtra("hostService",3);
//                    it.setAction("android.intent.action.VIEW");
                    startActivity(it);

                } catch (Exception ex) {
                    Log.e("invoke messageGuard",ex.getMessage(),ex);
                }
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_services);

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
//                        Intent it = new Intent("com.awtek.messageGuard.GasReport");
//                        sendBroadcast(it);
                    Intent it = new Intent();
                    ComponentName comp = new ComponentName("com.awtek.messageGuard",
                            "crc648040af3feb41865b.WebContentActivity");
                    it.setComponent(comp);
                    it.putExtra("hostService",6);
//                    it.setAction("android.intent.action.VIEW");
                    startActivity(it);

                } catch (Exception ex) {
                    Log.e("invoke messageGuard",ex.getMessage(),ex);
                }
            }
        });

/**
 *
*/

        b = (Button2) this.findViewById(R.id.main_taiwan_taix);

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent it = new Intent();
                    ComponentName comp = new ComponentName("com.awtek.messageGuard",
                            "crc648040af3feb41865b.WebContentActivity");
                    it.setComponent(comp);
                    it.putExtra("hostService",4);
//                    it.setAction("android.intent.action.VIEW");
                    startActivity(it);

                } catch (Exception ex) {
                    Log.e("invoke messageGuard",ex.getMessage(),ex);
                }
            }
        });

        b = (Button2) this.findViewById(R.id.main_power_meter);

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent it = new Intent();
                    ComponentName comp = new ComponentName("com.awtek.messageGuard",
                            "crc648040af3feb41865b.WebContentActivity");
                    it.setComponent(comp);
                    it.putExtra("hostService",7);
//                    it.setAction("android.intent.action.VIEW");
                    startActivity(it);

                } catch (Exception ex) {
                    Log.e("invoke messageGuard",ex.getMessage(),ex);
                }
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_smart);
/*        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it;
                if (addedSmartApp) {
                    it = getPackageManager().getLaunchIntentForPackage(__ADDED_SMART_APP);
                    startActivity(it);
                } else {
//					Intent it = new Intent();
                    it = getPackageManager().getLaunchIntentForPackage(
                            "com.dnake.smart");
                    startActivity(it);
                }
            }
        });*/

        //管理中心快速撥號
        b.setBackgroundResource(R.drawable.controll_center_talk);
//        b.setText("管理中心1999");
        b.setText("管理中心");

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

//                Intent it = new Intent("com.dnake.quickCall");
//                it.putExtra("call_id", "1999");
//                sendBroadcast(it);

                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk",
                        "com.dnake.talk.CallLabel");
                it.setComponent(comp);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                it.putExtra("callID", "1999");
                it.putExtra("callID", "center");
                it.setAction("android.intent.action.VIEW");
                startActivity(it);

            }
        });
        //

        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(__ADDED_SMART_APP, 0);
            if (appInfo != null) {
//				b.setText(pm.getApplicationLabel(appInfo));
//				b.setBackground(pm.getApplicationIcon(appInfo));
//            b.setBackgroundResource(R.drawable.community_information);
                addedSmartApp = true;
            }
        } catch (Exception ex) {
            Log.e("AddedSmartApp", ex.toString());
        }

        b = (Button2) this.findViewById(R.id.main_quick_security);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it;
                it = getPackageManager().getLaunchIntentForPackage(
                        "com.dnake.security");
                if (it == null) {
                    it = getPackageManager().getLaunchIntentForPackage(
                            "com.awtek.messageGuard");
                }
                if (it != null)
                    startActivity(it);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_talk);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                it = getPackageManager().getLaunchIntentForPackage(
                        "com.dnake.talk");
                startActivity(it);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_onoff);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.security",
                        "com.dnake.security.DefenceLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                try {
                    startActivity(it);
                } catch (Exception ex) {
                    Log.e("main_quick_onoff", ex.getMessage());
                }
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_cloud);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                it = getPackageManager().getLaunchIntentForPackage(
                        "com.dnake.apps");
                startActivity(it);
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_contacts);
//        b.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                v.setBackgroundResource(R.drawable.main_quick_contacts);
//                SysReceiver.doorStatus = 0;
//                return true;
//            }
//        });
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Intent it = new Intent();
//				it = getPackageManager().getLaunchIntentForPackage(
//						"com.android.contacts");
//				startActivity(it);
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk",
                        "com.dnake.talk.MonitorLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
			}
		});

        b = (Button2) this.findViewById(R.id.main_quick_manual);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent it = new Intent();
                    ComponentName comp = new ComponentName("com.awtek.messageGuard",
                            "crc648040af3feb41865b.WebContentActivity");
                    it.setComponent(comp);
                    it.putExtra("hostService",5);
                    startActivity(it);

                } catch (Exception ex) {
                    Log.e("invoke messageGuard",ex.getMessage(),ex);
                }
            }
        });

        b = (Button2) this.findViewById(R.id.main_quick_player);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent();
                it = getPackageManager().getLaunchIntentForPackage(
                        "com.android.gallery3d");
                startActivity(it);
            }
        });
//		b.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
////				Intent it = new Intent();
////				it = getPackageManager().getLaunchIntentForPackage(
////						"com.android.gallery3d");
////				startActivity(it);
//                Intent it = new Intent("com.dnake.doorAlarm");
////                it.putExtra("event", "com.dnake.boot");
//                sendBroadcast(it);
//			}
//		});

        b = (Button2) this.findViewById(R.id.main_quick_alert);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentAlarmed) {
                    v.setBackgroundResource(R.drawable.main_quick_sos);
                    currentAlarmed = false;
                    Intent it = new Intent("com.awtek.messageGuard.Alarm");
                    it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
                    it.putExtra("io", -1);
                    it.putExtra("sensor", -1);
                    sendBroadcast(it);
                } else {
                    v.setBackgroundResource(R.drawable.main_quick_sos_s);
                    Intent it = new Intent("com.dnake.doorAlarm");
                    it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
//                it.putExtra("event", "com.dnake.boot");
                    sendBroadcast(it);
                    currentAlarmed = true;
                }
                return true;
            }
        });


        //
        //
        // b = (Button2) this.findViewById(R.id.main_quick_out);
        // b.setOnClickListener(new OnClickListener() {
        // public void onClick(View v) {
        // Intent it = new Intent("com.dnake.broadcast");
        // it.putExtra("event", "com.dnake.smart.out");
        // sendBroadcast(it);
        // }
        // });

        findViewById(R.id.main_prompt_sms).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //if (sms_nread > 0) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.apps",
                        "com.dnake.apps.LoggerLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                //}
            }
        });

        findViewById(R.id.main_prompt_msg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //if (msg_nread > 0) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk",
                        "com.dnake.talk.LoggerLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                //}
            }
        });

        findViewById(R.id.main_prompt_talk).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //if (miss_nread > 0) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk",
                        "com.dnake.talk.LoggerLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                //}
            }
        });

        ImageView w = (ImageView) this.findViewById(R.id.main_weather_icon);
        w.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Math.abs(System.currentTimeMillis() - wTs) >= 10 * 1000) {
                    wTs = System.currentTimeMillis();
                    AccuWeather a = new AccuWeather() {
                        @Override
                        public void onFinished() {
                            wOK = true;
                        }
                    };
                    a.start();
                }
            }
        });

        w = (ImageView) this.findViewById(R.id.main_logo);
        w.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final View aboutView = getLayoutInflater().inflate(
                        R.layout.about, null);
                dialogBuilder.setView(aboutView);
                final AlertDialog aboutDialog = dialogBuilder.show();
//                final TextView versionName = (TextView) aboutView
//                        .findViewById(R.id.vesion_name);
//
//                versionName.setText(Utility.getAppVersion(HomeActivity.this));

                aboutDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        // 关闭对话框
                        aboutDialog.cancel();
                        aboutDialog.dismiss();
                        return false;
                    }
                });
            }
        });

        // this.loadScrollData();

        e_timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (timer != null)
                    doProcess();
            }
        };

        Intent it = new Intent("com.dnake.broadcast");
        it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
        it.putExtra("event", "com.dnake.boot");
        sendBroadcast(it);

        Intent intent = new Intent(this, SysService.class);
        this.startService(intent);

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            //android4.4 service会延迟启动，先由桌面提前启动APK Service
            ProcessThread pt = new ProcessThread();
            Thread t = new Thread(pt);
            t.start();
        }

        sTs = System.currentTimeMillis();

//        registerReceiver(broadcastReceiver, new IntentFilter(doorStatusIdentFilter));
/*        if(__ServiceCheck==null) {
            __ServiceCheck = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(60000);
                            checkMessageGuard();
                        } catch (Exception ex) {
                            Log.e("desktop",ex.getMessage());
                        }
                    }
                }
            });
            __ServiceCheck.run();
        }*/

    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();

    }

    private void refresh() {
        Button2 b;
//        if (SysReceiver.doorStatus == 1) {
//            b = (Button2) MainActivity.this.findViewById(R.id.main_quick_contacts);
//            b.setBackgroundResource(R.drawable.main_quick_door_opened);
//        } else {
//            b = (Button2) MainActivity.this.findViewById(R.id.main_quick_contacts);
//            b.setBackgroundResource(R.drawable.main_quick_contacts);
//        }

        b = (Button2) this.findViewById(R.id.main_quick_security);
        if (SysReceiver.defenceStatus != 0) {
            b.setTextColor(Color.argb(255, 0, 0, 255));
        } else {
            b.setTextColor(Color.argb(255, 255, 255, 255));
        }
    }

    public static void UpdateUI() {
        if (__Main != null) {
            __Main.refresh();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        this.tStart();
        this.doProcess();

        utils.eHome.idle = System.currentTimeMillis();
        checkMessageGuard();
    }

    String messageGuardService = "crc648040af3feb41865b.MessageGuardService";

    private void checkMessageGuard() {
        try {
            boolean isRunning = false;
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//				Log.d("desktop:list service", service.service.getClassName());
                if (messageGuardService.equals(service.service.getClassName())) {
                    isRunning = true;
                    Log.d("desktop:check", "MessageGuardService is running...");
                }
            }

            if (!isRunning || Math.abs(System.currentTimeMillis() - sTs) > 60000) {
                sTs = System.currentTimeMillis();
                startMessageGuard();
            }
        } catch (Exception ex) {
            Log.d("desktop", ex.toString());
        }
    }

    private void startMessageGuard() {
        try {
            ComponentName componentName = new ComponentName("com.awtek.messageGuard", messageGuardService);
            ServiceInfo info = getPackageManager().getServiceInfo(componentName,
                    PackageManager.GET_META_DATA);
            if (info != null) {
                Intent intent = new Intent();
                intent.setComponent(componentName);
                this.startService(intent);
                Log.d("desktop:launch", messageGuardService);
            }
        } catch (Exception ex) {
            Log.d("desktop", ex.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.tStop();

        utils.eHome.idle = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.tStop();
//        unregisterReceiver(broadcastReceiver);
    }

    private Timer timer = null;

    private void tStart() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new tRun(), 1000, 1000);
    }

    private void tStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void doProcess() {
        if (Math.abs(System.currentTimeMillis() - dTs) >= 5000) {
            dTs = System.currentTimeMillis();
            Calendar c = Calendar.getInstance();
            String s = String.valueOf(c.get(Calendar.YEAR));
            year.setText(s + ".");
            s = String.valueOf(c.get(Calendar.MONTH) + 1);
            month.setText(s + ".");
            s = String.valueOf(c.get(Calendar.DATE));
            day.setText(s + "/");

            String[] weekarr = {getString(R.string.week1),
                    getString(R.string.week2), getString(R.string.week3),
                    getString(R.string.week4), getString(R.string.week5),
                    getString(R.string.week6), getString(R.string.week7)};
            s = String.format("%01d", c.get(Calendar.DAY_OF_WEEK) - 1);
            week.setText(weekarr[Integer.valueOf(s)]);
            s = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
            hour.setText(s + ":");
            s = String.format("%02d", c.get(Calendar.MINUTE));
            min.setText(s);
        }

        this.loadPrompt();
        this.loadWeather();
    }

    private class tRun extends java.util.TimerTask {

        //        int count;
        @Override
        public void run() {
            if (e_timer != null)
                e_timer.sendMessage(e_timer.obtainMessage());
//            count++;
//            if (count >= 300) {
//                count = 0;
////                checkMessageGuard();
//                startMessageGuard();
//            }
        }
    }

    private Intent[] scroll_it = new Intent[32];

    private void loadScrollData() {
        Context toolbar = null;
        try {
            toolbar = this.createPackageContext("com.dnake.toolbar",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (toolbar == null)
            return;

        Resources r = toolbar.getResources();
        dxml p = getAssetsXml(r, "toolbar.xml");
        if (p != null) {
            PackageManager pm = this.getPackageManager();

            ImageView logo = (ImageView) this.findViewById(R.id.logo);
            if (p.getInt("/sys/logo", 0) != 0) {
                int rid = r.getIdentifier("ex_logo", "drawable",
                        "com.dnake.toolbar");
                if (rid != 0) {
                    Drawable d = r.getDrawable(rid);
                    if (d != null)
                        logo.setImageDrawable(d);
                }
            } else
                logo.setVisibility(ImageView.GONE);

            int max = p.getInt("/sys/max", 0);
            for (int i = 0; i < max; i++) {
                String s = "/sys/app" + i;
                String apk = p.getText(s + "/package");
                String icon = p.getText(s + "/icon");

                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(apk, 0);
                    Drawable d = r.getDrawable(r.getIdentifier(icon,
                            "drawable", "com.dnake.toolbar"));
                    d.setAlpha(200);

                    String label = pm.getApplicationLabel(pi.applicationInfo)
                            .toString();

                    TextView tv = (TextView) this
                            .findViewById(R.id.main_scroll_item0 + i);
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, d, null,
                            null);
                    tv.setCompoundDrawablePadding(-8);
                    tv.setText(label);
                    tv.setTag(i);
                    tv.setVisibility(TextView.VISIBLE);

                    tv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int n = (Integer) v.getTag();
                            startActivity(scroll_it[n]);
                        }
                    });

                    scroll_it[i] = pm.getLaunchIntentForPackage(apk);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadPrompt() {
        if (sms_nread != SysReceiver.sms_nread) {
            sms_nread = SysReceiver.sms_nread;

            String s;
            if (SysReceiver.sms_nread > 0) {
                s = this.getResources().getString(R.string.main_prompt_sms);
                s = s.replaceFirst("xxyy",
                        String.valueOf(SysReceiver.sms_nread));
            } else
                s = this.getResources().getString(R.string.main_prompt_no_sms);
            sms.setText(s);
        }
        if (msg_nread != SysReceiver.msg_nread) {
            msg_nread = SysReceiver.msg_nread;

            String s;
            if (SysReceiver.msg_nread > 0) {
                s = this.getResources().getString(R.string.main_prompt_msg);
                s = s.replaceFirst("xxyy",
                        String.valueOf(SysReceiver.msg_nread));
            } else
                s = this.getResources().getString(R.string.main_prompt_no_msg);
            msg.setText(s);
        }
        if (miss_nread != SysReceiver.miss_nread) {
            miss_nread = SysReceiver.miss_nread;

            String s;
            if (SysReceiver.miss_nread > 0) {
                s = this.getResources().getString(R.string.main_prompt_missed);
                s = s.replaceFirst("xxyy",
                        String.valueOf(SysReceiver.miss_nread));
            } else
                s = this.getResources().getString(
                        R.string.main_prompt_no_missed);
            miss.setText(s);
        }
        // if (security != SysReceiver.security) {
        // security = SysReceiver.security;
        //
        // ImageView iv = (ImageView)
        // this.findViewById(R.id.main_security_icon);
        // TextView tv = (TextView) this.findViewById(R.id.main_security_text);
        //
        // if (security != 0) {
        // Drawable d =
        // this.getResources().getDrawable(R.drawable.main_security_green);
        // iv.setImageDrawable(d);
        // } else {
        // Drawable d =
        // this.getResources().getDrawable(R.drawable.main_security_red);
        // iv.setImageDrawable(d);
        // }
        //
        // switch (security) {
        // case 0:
        // tv.setText(R.string.main_security_off);
        // break;
        // case 1:
        // tv.setText(R.string.main_security_out);
        // break;
        // case 2:
        // tv.setText(R.string.main_security_home);
        // break;
        // case 3:
        // tv.setText(R.string.main_security_sleep);
        // break;
        // }
        // }
    }

    private AccuWeather aWeather = null;

    public void loadWeather() {
        if (wOK) {
            wOK = false;

            TextView tv = (TextView) this.findViewById(R.id.main_weather_text);
            tv.setText(AccuWeather.text);

            tv = (TextView) this.findViewById(R.id.main_weather_text2);
            String s = this.getResources()
                    .getString(R.string.main_weather_temp)
                    + String.valueOf(AccuWeather.temp) + "℃";
            tv.setText(s);
            tv = (TextView) this.findViewById(R.id.main_weather_text3);
            s = this.getResources().getString(R.string.main_weather_humidity)
                    + String.valueOf(AccuWeather.humidity) + "%";
            tv.setText(s);

            TextView pm25 = (TextView) this
                    .findViewById(R.id.main_weather_pm25);
            TextView quality = (TextView) this
                    .findViewById(R.id.main_weather_quality);
            if (AccuWeather.pm25 != 0 && AccuWeather.quality != null
                    && !AccuWeather.quality.equals("null")) {
                s = this.getResources().getString(R.string.main_weather_pm25)
                        + String.valueOf(AccuWeather.pm25);
                pm25.setText(s);
                s = this.getResources()
                        .getString(R.string.main_weather_quality)
                        + AccuWeather.quality;
                // quality.setText(s);
            } else {
                pm25.setText("");
                quality.setText("");
            }

            ImageView v = (ImageView) this.findViewById(R.id.main_weather_icon);
            if (AccuWeather.icon < 2)
                v.setImageResource(R.drawable.main_weather_w0);
            else if (AccuWeather.icon < 5)
                v.setImageResource(R.drawable.main_weather_w1);
            else if (AccuWeather.icon < 8)
                v.setImageResource(R.drawable.main_weather_w2);
            else if (AccuWeather.icon < 11)
                v.setImageResource(R.drawable.main_weather_w3);
            else if (AccuWeather.icon < 12)
                v.setImageResource(R.drawable.main_weather_w2);
            else if (AccuWeather.icon < 13)
                v.setImageResource(R.drawable.main_weather_w4);
            else if (AccuWeather.icon < 18)
                v.setImageResource(R.drawable.main_weather_w5);
            else if (AccuWeather.icon < 19)
                v.setImageResource(R.drawable.main_weather_w4);
            else if (AccuWeather.icon < 20)
                v.setImageResource(R.drawable.main_weather_w6);
            else if (AccuWeather.icon < 22)
                v.setImageResource(R.drawable.main_weather_w7);
            else if (AccuWeather.icon < 23)
                v.setImageResource(R.drawable.main_weather_w6);
            else if (AccuWeather.icon < 24)
                v.setImageResource(R.drawable.main_weather_w7);
            else if (AccuWeather.icon < 30)
                v.setImageResource(R.drawable.main_weather_w6);
            else if (AccuWeather.icon < 33)
                v.setImageResource(R.drawable.main_weather_w0);
            else if (AccuWeather.icon < 35)
                v.setImageResource(R.drawable.main_weather_w8);
            else if (AccuWeather.icon < 39)
                v.setImageResource(R.drawable.main_weather_w9);
            else if (AccuWeather.icon < 43)
                v.setImageResource(R.drawable.main_weather_w10);
            else if (AccuWeather.icon < 45)
                v.setImageResource(R.drawable.main_weather_w11);
            else
                v.setImageResource(R.drawable.main_weather_w0);
        }
        if (Math.abs(System.currentTimeMillis() - wTs) > 60 * 60 * 1000
                || (AccuWeather.haved == false && Math.abs(System
                .currentTimeMillis() - wTs) > 5 * 60 * 1000)) {
            wTs = System.currentTimeMillis();

            if (aWeather == null) {
                aWeather = new AccuWeather() {
                    @Override
                    public void onFinished() {
                        wOK = true;
                    }
                };
            }
            aWeather.start();
        }
    }

    public dxml getAssetsXml(Resources r, String fileName) {
        try {
            InputStream in = r.getAssets().open(fileName);
            dxml xml = new dxml();
            xml.parse(in);
            in.close();
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    private Context mContext = this;

    private class ProcessThread implements Runnable {
        @Override
        public void run() {
            this.start("com.dnake.eSettings", "com.dnake.v700.settings");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            this.start("com.dnake.talk", "com.dnake.v700.talk");
            this.start("com.dnake.smart", "com.dnake.v700.smart");
            this.start("com.dnake.security", "com.dnake.v700.security");
            this.start("com.dnake.apps", "com.dnake.v700.apps");
        }

        private void start(String apk, String name) {
            try {
                Intent it = new Intent();
                it.setComponent(new ComponentName(apk, name));
                mContext.startService(it);
            } catch (RuntimeException e) {
            }
        }
    }

//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Button2 b = (Button2) MainActivity.this.findViewById(R.id.main_quick_contacts);
//            b.setBackgroundResource(R.drawable.main_quick_door_opened);
//        }
//    };

    public static void CallElevator(String event) {
//                Intent intent = new Intent(ctx, ElevatorLabel.class);
        Intent intent = new Intent();
        intent.setClassName("com.dnake.smart","com.dnake.smart.ElevatorLabel");
        intent.putExtra("event",event);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        __Main.startActivity(intent);
    }

}
