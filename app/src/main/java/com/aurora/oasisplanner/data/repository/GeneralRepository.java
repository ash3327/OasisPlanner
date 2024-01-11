package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.model.entities.util._Tag;

import java.util.List;

public class GeneralRepository {
    private AgendaDao agendaDao;
    private LiveData<List<_Tag>> tags;

    public GeneralRepository(AgendaDao agendaDao) {
        this.agendaDao = agendaDao;
        tags = requestTags();
    }

    public void insert(_Tag Tag) {
        new InsertTagAsyncTask(agendaDao).execute(Tag);
    }

    public _Tag getTagFromName(String name) {
        try {
            return new GeneralRepository.GetTagAsyncTask(agendaDao).execute(name).get();
        } catch (Exception e) {
            return null;
        }
    }

    public void update(_Tag Tag) {
        new UpdateTagAsyncTask(agendaDao).execute(Tag);
    }

    public void delete(_Tag Tag) {
        new DeleteTagAsyncTask(agendaDao).execute(Tag);
    }

    public void deleteAllTags() {
        new DeleteAllTagsAsyncTask(agendaDao).execute();
    }

    public LiveData<List<_Tag>> getTags() {
        return tags;
    }

    public LiveData<List<_Tag>> requestTags(String searchEntry) {
        return tags = agendaDao.getTags(searchEntry);
    }

    public LiveData<List<_Tag>> requestTags() {
        return tags = agendaDao.getTags();
    }

    private static class InsertTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private AgendaDao agendaDao;

        private InsertTagAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            agendaDao.insert(tags[0]);
            return null;
        }
    }
    private static class UpdateTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private AgendaDao agendaDao;

        private UpdateTagAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            agendaDao.insert(tags[0]);
            return null;
        }
    }
    private static class GetTagAsyncTask extends AsyncTask<String, Void, _Tag> {
        private AgendaDao agendaDao;

        private GetTagAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected _Tag doInBackground(String... TagIds) {
            return agendaDao.getTagByName(TagIds[0]);
        }
    }
    private static class DeleteTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteTagAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            agendaDao.delete(tags[0]);
            return null;
        }
    }
    private static class DeleteAllTagsAsyncTask extends AsyncTask<Void, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteAllTagsAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            agendaDao.deleteAllTags();
            return null;
        }
    }
}
