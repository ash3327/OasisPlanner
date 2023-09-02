package com.aurora.oasisplanner.util.notificationfeatures;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import androidx.core.app.NotificationCompat;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule.NotificationMode;

public class AlarmNotificationService {

    public static String ALARM_CHANNEL_ID = "alarm_channel";
    public static String ALARM_CHANNEL_NAME = "Alarm";

    private Context context;
    private NotificationManager notificationManager;

    public AlarmNotificationService(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(_Alarm alarm) {

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra(NotificationModule.NOTIFICATION_MODE, NotificationMode.AGENDA.name());
        activityIntent.putExtra(NotificationModule.NOTIFICATION_CONTENT, alarm.agendaId);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.g_regular_v2);
        img = img.extractAlpha();
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(alarm.importance.getColorPr(), PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(img, 0, 0, paint);

        // delay alarm service

        Notification notification = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
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
                /*.addAction(
                        R.drawable.menuic_sprout,
                        "Edit",

                )//*/
                .setAutoCancel(true)
                .build();

        notificationManager.notify(
                // same id -> update notification instead
                (int) alarm.id, notification
        );
    }

}
