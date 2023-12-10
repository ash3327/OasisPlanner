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
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule.NotificationMode;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

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
            _Alarm alarm = _Alarm.unpackContents(intent.getBundleExtra("ALARM"));
            showNotification(alarm);
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
    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(_Alarm alarm) {
        Resources.context = this;

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.putExtra(NotificationModule.NOTIFICATION_MODE, NotificationMode.AGENDA.name());

        activityIntent.putExtra(NotificationModule.NOTIFICATION_CONTENT, alarm.agendaId);
        activityIntent.putExtra(NotificationModule.NOTIFICATION_ACTIVITY, alarm.activityId);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Bitmap img = BitmapFactory.decodeResource(this.getResources(), alarm.importance.getNotifIcon());
        img = img.extractAlpha();

        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(alarm.importance.getColorPr(), PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(img, 0, 0, paint);

        RemoteViews collapsedView = new RemoteViews(this.getPackageName(), R.layout.notification_collapsed_view);
        collapsedView.setImageViewBitmap(R.id.logo_collapsed, bitmapResult);
        collapsedView.setTextViewText(R.id.time, DateTimesFormatter.getTime(alarm.datetime.toLocalTime()));
        collapsedView.setTextViewText(R.id.text_view_collapsed_1, alarm.title);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, alarm.getContents(false));

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_collapsed_view);
        expandedView.setImageViewBitmap(R.id.logo_collapsed, bitmapResult);
        expandedView.setTextViewText(R.id.time, DateTimesFormatter.getTime(alarm.datetime.toLocalTime()));
        expandedView.setTextViewText(R.id.text_view_collapsed_1, alarm.title);
        expandedView.setTextViewText(R.id.text_view_collapsed_2, alarm.getContents(true));

        // delay alarm service
        Notification notification = new NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
                .setColor(Color.argb(0, 0, 0, 0))
                .setSmallIcon(R.drawable.ic_agenda_calendar)
                .setContentTitle(alarm.title)
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
                (int) alarm.id, notification
        );

        if (MainActivity.main != null)
            Resources.context = MainActivity.main;
    }

}
