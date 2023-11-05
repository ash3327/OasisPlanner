package com.aurora.oasisplanner.data.datasource;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DeleteColumn;
import androidx.room.DeleteTable;
import androidx.room.RenameTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities._SubPeriod;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.pojo.Activity;
import com.aurora.oasisplanner.data.model.entities._Agenda;
import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.tags.AgendaType;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalTime;

@Database(
        entities = {
                _Alarm.class, _Agenda.class, _Activity.class, _AlarmList.class, _Doc.class,
                _SubPeriod.class
        },
        version = 14,
        autoMigrations = {
                @AutoMigration(from=5, to=6, spec=AppDatabase.Migration5to6.class),
                @AutoMigration(from=6, to=7),
                @AutoMigration(from=7, to=8),
                @AutoMigration(from=8, to=9, spec=AppDatabase.Migration8to9.class),
                @AutoMigration(from=9, to=10),
                @AutoMigration(from=10, to=11),
                @AutoMigration(from=11, to=12, spec=AppDatabase.Migration11to12.class),
                @AutoMigration(from=12, to=13),
                @AutoMigration(from=13, to=14)
        }
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static String DATABASE_NAME = "oasis_db";

    private static AppDatabase instance;

    public abstract AgendaDao agendaDao();

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

    private static class PopulateDbAsyncClass extends AsyncTask<Void, Void, Void> {
        private final AgendaRepository agendaRepository;

        private PopulateDbAsyncClass(AppDatabase db) {
            agendaRepository = AppModule.provideAgendaRepository(db, null);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            agendaRepository.insert(
                    new Agenda(
                            AgendaType.agenda,
                            "Agenda 1"
                    ).putItems(
                            new _Doc("Doc 1"),
                            new Activity(null).putItems(
                                    new _Doc(Styles.toStyled("Subdoc 1")),
                                    new AlarmList(
                                            AlarmType.notif, Importance.important
                                    ).putDates(
                                            LocalTime.of(12, 30),
                                            LocalDate.of(2023, 9, 30)
                                    )
                            ),
                            new _Doc("Doc 2"),
                            new Activity(null).putItems(
                                    new _Doc("Subdoc 2"),
                                    new AlarmList(
                                            AlarmType.notif, Importance.regular
                                    ).putDates(
                                            LocalTime.of(12, 58),
                                            LocalDate.of(2023, 11, 1),
                                            LocalDate.of(2023, 11, 2)
                                    )
                            )
                    )
            );
            agendaRepository.insert(
                    new Agenda(
                            AgendaType.agenda,
                            "Agenda 2"
                    ).putItems(
                            new _Doc("Doc 3\ncontains next line"),
                            new Activity(null).putItems(
                                    new _Doc("Subdoc 3"),
                                    new AlarmList(
                                            AlarmType.notif, Importance.regular
                                    ).putDates(
                                            LocalTime.of(12, 30),
                                            LocalDate.of(2023, 9, 22),
                                            LocalDate.of(2023, 11, 1)
                                    ),
                                    new AlarmList(
                                            AlarmType.agenda, Importance.regular
                                    ).putDates(
                                            LocalTime.of(12, 30),
                                            LocalDate.of(2023, 11, 2)
                                    )
                            )
                    )
            );

            return null;
        }
    }

    @RenameTable(fromTableName = "_Group", toTableName = "_Activity")
    public static class Migration5to6 implements AutoMigrationSpec {}

    @DeleteColumn(tableName = "_Activity", columnName = "importance")
    public static class Migration8to9 implements AutoMigrationSpec {}

    @DeleteTable(tableName = "_Period")
    @DeleteTable(tableName = "_Periods")
    public static class Migration11to12 implements AutoMigrationSpec {}
}
