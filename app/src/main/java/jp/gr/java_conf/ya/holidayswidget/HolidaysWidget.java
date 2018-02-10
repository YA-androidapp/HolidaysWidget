package jp.gr.java_conf.ya.holidayswidget; // Copyright (c) 2018 YA <ya.androidapp@gmail.com> All rights reserved.

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

public class HolidaysWidget extends AppWidgetProvider {
    private static String URL_HOLIDAYS = "http://www8.cao.go.jp/chosei/shukujitsu/syukujitsu_kyujitsu.csv";

    @Override
    public void onUpdate(Context c, AppWidgetManager awm, int[] awi) {
        Intent in = new Intent(c, WidgetService.class);
        c.startService(in);
    }

    public static class WidgetService extends Service {
        @Override
        public void onStart(Intent in, int si) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(holidaysReceiver, filter);
        }

        @Override
        public IBinder onBind(Intent in) {
            return null;
        }
    }

    private static BroadcastReceiver holidaysReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent in) {
            String ac = in.getAction();
            if (ac.equals(Intent.ACTION_BATTERY_CHANGED)) {
                final Date today = new Date();

                final AppWidgetManager awm = AppWidgetManager.getInstance(c);
                final ComponentName cn = new ComponentName(c, HolidaysWidget.class);
                final RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.main);

                try {
                    final URL url = new URL(URL_HOLIDAYS);
                    AsyncDlTask aAsyncDlTask = new AsyncDlTask(new AsyncDlTask.AsyncCallback() {

                        public void onPreExecute() {
                        }

                        public void onProgressUpdate(int progress) {
                        }

                        public void onCancelled() {
                        }

                        public void onPostExecute(String[] result) {
                            List<ListItem> holidaysList = CsvUtil.parse(result[0]);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(today);
                            for (int i = 0; i < 7 * 10; i++) {
                                final ListItem item = new ListItem();
                                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                                    case Calendar.SUNDAY:
                                        item.setDate(calendar.getTime());
                                        item.setName("");
                                        holidaysList.add(item);
                                        break;
                                    case Calendar.SATURDAY:
                                        item.setDate(calendar.getTime());
                                        item.setName("");
                                        holidaysList.add(item);
                                        break;
                                    default:
                                        break;
                                }

                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                            }

                            Collections.sort(holidaysList, new Comparator<ListItem>() {
                                public int compare(ListItem a, ListItem b) {
                                    long no1 = a.getDate().getTime();
                                    long no2 = b.getDate().getTime();

                                    if (no1 > no2)
                                        return 1;
                                    else if (no1 == no2)
                                        return 0;
                                    else
                                        return -1;
                                }

                            });

                            for (int i = holidaysList.size() - 1; i > 0; i--) {
                                if (ListItem.sdFormat.format(holidaysList.get(i).getDate().getTime()).equals(ListItem.sdFormat.format(holidaysList.get(i - 1).getDate().getTime()))) {
                                    holidaysList.remove(i);
                                }
                            }


                            final StringBuilder sb = new StringBuilder();
                            sb.append(ListItem.sdFormat.format(today)).append("\n\n");

                            for (final ListItem item : holidaysList) {
                                if (((item.getDate().getTime() - today.getTime()) / 1000 * 60 * 60 * 24) > 0)
                                    sb.append(item.toString()).append("\n");
                            }

                            rv.setTextViewText(R.id.TextView, sb.toString());

                            awm.updateAppWidget(cn, rv);

//                            try {
//                                JSONObject json = new JSONObject(result[0]);
//                                JSONArray jArr = json.getJSONArray("data");
//
//                                for (int i = 0; i < jArr.length(); i++) {
//                                    JSONObject jRec = jArr.getJSONObject(i);
//                                    String string = jRec.getString("item");
//                                }
//                            } catch (JSONException e) {
//                            }
                        }
                    });
                    aAsyncDlTask.execute(url);
                } catch (Exception e) {
                }
            }
        }
    };
}
