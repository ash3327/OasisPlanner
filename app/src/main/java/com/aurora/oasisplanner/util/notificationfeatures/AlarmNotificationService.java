package com.aurora.oasisplanner.util.notificationfeatures;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.IBinder;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.OasisApp;
import com.aurora.oasisplanner.activities.SplashActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule.NotificationMode;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDateTime;

public class AlarmNotificationService extends Service {

    public static String ALARM_CHANNEL_ID = "alarm_channel";
    public static String ALARM_CHANNEL_NAME = "Alarm";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Alarm alarm = Alarm.unpackContents(intent.getBundleExtra("ALARM"));
            AppModule.provideExecutor().execute(
                    ()->showNotification(alarm)
            );
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    final String SHORTCUT_ID = "OasisShortcutId";
    public void showNotification(Alarm alarm) {
        Resources.context = this;

        Intent activityIntent = new Intent(this, SplashActivity.class);//appClosed ? SplashActivity.class : MainActivity.class);
        activityIntent.putExtra(NotificationModule.NOTIFICATION_MODE, NotificationMode.AGENDA.name());

        activityIntent.putExtra(NotificationModule.NOTIFICATION_CONTENT, alarm.getAgendaId());
        activityIntent.putExtra(NotificationModule.NOTIFICATION_ACTIVITY, alarm.getActivityId());
        activityIntent.putExtra(NotificationModule.NOTIFICATION_EVENT, alarm.getEventId());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0)
        );

        Bitmap img = BitmapFactory.decodeResource(this.getResources(), alarm.getImportance().getNotifIcon());
        img = img.extractAlpha();

        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(alarm.getImportance().getColorPr(), PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(img, 0, 0, paint);

        /*
        RemoteViews collapsedView = new RemoteViews(this.getPackageName(), R.layout.notification_collapsed_view);
        collapsedView.setImageViewBitmap(R.id.logo_collapsed, bitmapResult);
        collapsedView.setTextViewText(R.id.time, DateTimesFormatter.getTime(alarm.datetime.toLocalTime()));
        collapsedView.setTextViewText(R.id.text_view_collapsed_1, alarm.title);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, alarm.getContents(false));
        //*/

        LocalDateTime ldt = alarm.getDateTime();
        if (alarm.getAlarm().isSubalarm()) {
            try {
                ldt = alarm.getAlarm().getParentDatetime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_collapsed_view);
        expandedView.setImageViewBitmap(R.id.logo_collapsed, bitmapResult);
        expandedView.setTextViewText(R.id.time, DateTimesFormatter.getTime(ldt.toLocalTime()));
        expandedView.setTextViewText(R.id.text_view_collapsed_1, alarm.getTitle());
        expandedView.setTextViewText(R.id.text_view_collapsed_2, alarm.getContents(false));
        SpannableStringBuilder locText = alarm.getLoc();
        if (locText == null) {
            expandedView.setViewVisibility(R.id.notif_loc_text, View.INVISIBLE);
            expandedView.setViewVisibility(R.id.notif_loc_icon, View.INVISIBLE);
        } else {
            expandedView.setTextViewText(R.id.notif_loc_text, locText);
        }

        // delay alarm service
        Notification notification = new NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
                .setColor(Color.argb(0, 0, 0, 0))
                .setSmallIcon(R.drawable.ic_agenda_calendar)
                .setContentTitle(alarm.getTitle() + ": " + (alarm.getAlarm().isSubalarm() ? "(SubAlarm)" : "(MainAlarm)"))
                .setContentText(alarm.getContents(false))
                .setCustomContentView(expandedView)//collapsedView)
                .setCustomBigContentView(expandedView)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        /*
        Notification notification = new NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.menuic_sprout)
                .setContentTitle(alarm.title)
                .setContentText(alarm.getContents(false))
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(alarm.getContents(true))
                )
                .setCategory(Notification.CATEGORY_ALARM)
                .setLargeIcon(bitmapResult)
                .setContentIntent(pendingIntent)
                .setShortcutId(SHORTCUT_ID)
                //.addAction(
                //        R.drawable.menuic_sprout,
                //        "Edit",

                //)//
                .setAutoCancel(true)
                .build();*/

        notificationManager.notify(
                // same id -> update notification instead
                (int) alarm.getEncodedId(), notification
        );

        if (OasisApp.getContext() != null)
            Resources.context = OasisApp.getContext();
    }

    public static void notify(Context context, Alarm alarm) {
        Intent serviceIntent = new Intent(context, AlarmNotificationService.class);
        serviceIntent.putExtra("ALARM", alarm.packContents());
        context.startService(serviceIntent);
    }
}
