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

import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
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
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.util.Converters;

@Database(
        entities = {
                _Alarm.class, _Agenda.class, _Activity.class, _AlarmList.class, _Doc.class,
                _SubPeriod.class, _SelectedDates.class, _SubAlarm.class, _Memo.class, _Tag.class
        },
        version = 23,
        autoMigrations = {
                @AutoMigration(from=5, to=8, spec=AppDatabase.Migration5to6.class),
                @AutoMigration(from=8, to=11, spec=AppDatabase.Migration8to9.class),
                @AutoMigration(from=11, to=17, spec=AppDatabase.Migration11to12.class),
                @AutoMigration(from=17, to=18),
                @AutoMigration(from=18, to=19),
                @AutoMigration(from=19, to=20),
                @AutoMigration(from=20, to=21),
                @AutoMigration(from=21, to=22, spec=AppDatabase.Migration21to22.class),
                @AutoMigration(from=22, to=23),
        }
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static String DATABASE_NAME = "oasis_db";

    private static AppDatabase instance;

    public abstract AgendaDao agendaDao();
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
}
