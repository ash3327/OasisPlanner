package com.aurora.oasisplanner.data.datasource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DeleteColumn;
import androidx.room.DeleteTable;
import androidx.room.RenameColumn;
import androidx.room.RenameTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aurora.oasisplanner.data.datasource.daos.ActivityDao;
import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.datasource.daos.EventDao;
import com.aurora.oasisplanner.data.datasource.daos.MemoDao;
import com.aurora.oasisplanner.data.datasource.daos.MultimediaDao;
import com.aurora.oasisplanner.data.datasource.daos.TagDao;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.model.entities._others._SelectedDates;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.entities._others._SubPeriod;
import com.aurora.oasisplanner.data.model.entities.util._Tag;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.util.Converters;

@Database(
        entities = {
                _Alarm.class, _Agenda.class, _Activity.class, _Event.class, _Doc.class,
                _SubPeriod.class, _SelectedDates.class, _SubAlarm.class, _Memo.class, _Tag.class
        },
        version = 28,
        exportSchema = true,
        autoMigrations = {
                //INFO: v0.1.0 update
                @AutoMigration(from=5, to=8, spec=AppDatabase.Migration5to6.class),
                @AutoMigration(from=8, to=11, spec=AppDatabase.Migration8to9.class),
                @AutoMigration(from=11, to=17, spec=AppDatabase.Migration11to12.class),
                @AutoMigration(from=17, to=22, spec=AppDatabase.Migration21to22.class),
                @AutoMigration(from=22, to=23),
                //INFO: v0.1.1 update
                @AutoMigration(from=23, to=24, spec=AppDatabase.Migration23to24.class),
                @AutoMigration(from=24, to=25, spec=AppDatabase.Migration24to25.class),
                //INFO: v0.1.2 update
                //INFO: v0.1.3 update
                @AutoMigration(from=25, to=26, spec=AppDatabase.Migration25to26.class),
                @AutoMigration(from=26, to=27, spec=AppDatabase.Migration26to27.class),
                @AutoMigration(from=27, to=28, spec=AppDatabase.Migration27to28.class),
        }
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static String DATABASE_NAME = "oasis_db";

    private static AppDatabase instance;

    public abstract AgendaDao agendaDao();
    public abstract ActivityDao activityDao();
    public abstract EventDao eventDao();
    public abstract AlarmDao alarmDao();
    public abstract MemoDao memoDao();
    public abstract TagDao tagDao();
    public abstract MultimediaDao multimediaDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder
                    (
                        context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME
                    )
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    public static synchronized AppDatabase getInstance() {
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDbAsyncClass(instance).execute();
        }
    };

    @RenameTable(fromTableName = "_Group", toTableName = "_Activity")
    public static class Migration5to6 implements AutoMigrationSpec {}

    @DeleteColumn(tableName = "_Activity", columnName = "importance")
    public static class Migration8to9 implements AutoMigrationSpec {}

    @DeleteTable(tableName = "_Period")
    @DeleteTable(tableName = "_Periods")
    public static class Migration11to12 implements AutoMigrationSpec {}

    @RenameColumn(tableName = "_AlarmList", fromColumnName = "groupId", toColumnName = "activityId")
    public static class Migration21to22 implements AutoMigrationSpec {}

    @DeleteColumn(tableName = "_Alarm", columnName = "title")
    @DeleteColumn(tableName = "_Alarm", columnName = "agendaDescr")
    @DeleteColumn(tableName = "_Alarm", columnName = "alarmDescr")
    @DeleteColumn(tableName = "_SubAlarm", columnName = "title")
    @DeleteColumn(tableName = "_SubAlarm", columnName = "agendaDescr")
    @DeleteColumn(tableName = "_SubAlarm", columnName = "alarmDescr")
    public static class Migration23to24 implements AutoMigrationSpec {}

    @RenameTable(fromTableName = "_AlarmList", toTableName = "_Event")
    public static class Migration24to25 implements AutoMigrationSpec {}

    @RenameColumn(tableName = "_Agenda", fromColumnName = "id", toColumnName = "agendaId")
    @RenameColumn(tableName = "_Agenda", fromColumnName = "title", toColumnName = "agendaTitle")
    @RenameColumn(tableName = "_Agenda", fromColumnName = "type", toColumnName = "agendaType")
    @RenameColumn(tableName = "_Agenda", fromColumnName = "types", toColumnName = "agendaTypes")
    @RenameColumn(tableName = "_Agenda", fromColumnName = "args", toColumnName = "agendaArgs")
    public static class Migration25to26 implements AutoMigrationSpec {}

    @RenameColumn(tableName = "_Alarm", fromColumnName = "id", toColumnName = "alarmId")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "datetime", toColumnName = "alarmDatetime")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "duration", toColumnName = "alarmDuration")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "date", toColumnName = "alarmDate")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "type", toColumnName = "alarmType")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "importance", toColumnName = "alarmImportance")
    @RenameColumn(tableName = "_Alarm", fromColumnName = "args", toColumnName = "alarmArgs")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "id", toColumnName = "alarmId")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "datetime", toColumnName = "alarmDatetime")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "duration", toColumnName = "alarmDuration")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "date", toColumnName = "alarmDate")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "type", toColumnName = "alarmType")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "importance", toColumnName = "alarmImportance")
    @RenameColumn(tableName = "_SubAlarm", fromColumnName = "args", toColumnName = "alarmArgs")
    public static class Migration26to27 implements AutoMigrationSpec {}

    @RenameColumn(tableName = "_Activity", fromColumnName = "id", toColumnName = "activityId")
    @RenameColumn(tableName = "_Activity", fromColumnName = "types", toColumnName = "activityTypes")
    @RenameColumn(tableName = "_Activity", fromColumnName = "type", toColumnName = "activityType")
    @RenameColumn(tableName = "_Activity", fromColumnName = "importance", toColumnName = "activityImportance")
    @RenameColumn(tableName = "_Activity", fromColumnName = "descr", toColumnName = "activityDescr")
    @RenameColumn(tableName = "_Activity", fromColumnName = "args", toColumnName = "activityArgs")
    @RenameColumn(tableName = "_Activity", fromColumnName = "i", toColumnName = "activityI")
    @RenameColumn(tableName = "_Event", fromColumnName = "id", toColumnName = "eventId")
    @RenameColumn(tableName = "_Event", fromColumnName = "title", toColumnName = "eventTitle")
    @RenameColumn(tableName = "_Event", fromColumnName = "dates", toColumnName = "eventDates")
    @RenameColumn(tableName = "_Event", fromColumnName = "time", toColumnName = "eventTime")
    @RenameColumn(tableName = "_Event", fromColumnName = "type", toColumnName = "eventType")
    @RenameColumn(tableName = "_Event", fromColumnName = "importance", toColumnName = "eventImportance")
    @RenameColumn(tableName = "_Event", fromColumnName = "i", toColumnName = "eventI")
    @RenameColumn(tableName = "_Event", fromColumnName = "args", toColumnName = "eventArgs")
    public static class Migration27to28 implements AutoMigrationSpec {}
}
