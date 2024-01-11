package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.daos.TagDao;
import com.aurora.oasisplanner.data.model.entities.util._Tag;

import java.util.List;

public class TagRepository {
    private TagDao tagDao;
    private LiveData<List<_Tag>> tags;

    public TagRepository(TagDao tagDao) {
        this.tagDao = tagDao;
        tags = requestTags();
    }

    public void insert(_Tag Tag) {
        new InsertTagAsyncTask(tagDao).execute(Tag);
    }

    public _Tag getTagFromName(String name) {
        try {
            return new TagRepository.GetTagAsyncTask(tagDao).execute(name).get();
        } catch (Exception e) {
            return null;
        }
    }

    public void update(_Tag Tag) {
        new UpdateTagAsyncTask(tagDao).execute(Tag);
    }

    public void delete(_Tag Tag) {
        new DeleteTagAsyncTask(tagDao).execute(Tag);
    }

    public void deleteAllTags() {
        new DeleteAllTagsAsyncTask(tagDao).execute();
    }

    public LiveData<List<_Tag>> getTags() {
        return tags;
    }

    public LiveData<List<_Tag>> requestTags(String searchEntry) {
        return tags = tagDao.getTags(searchEntry);
    }

    public LiveData<List<_Tag>> requestTags() {
        return tags = tagDao.getTags();
    }

    private static class InsertTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private TagDao tagDao;

        private InsertTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            tagDao.insert(tags[0]);
            return null;
        }
    }
    private static class UpdateTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private TagDao tagDao;

        private UpdateTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            tagDao.insert(tags[0]);
            return null;
        }
    }
    private static class GetTagAsyncTask extends AsyncTask<String, Void, _Tag> {
        private TagDao tagDao;

        private GetTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected _Tag doInBackground(String... TagIds) {
            return tagDao.getTagByName(TagIds[0]);
        }
    }
    private static class DeleteTagAsyncTask extends AsyncTask<_Tag, Void, Void> {
        private TagDao tagDao;

        private DeleteTagAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(_Tag... tags) {
            tagDao.delete(tags[0]);
            return null;
        }
    }
    private static class DeleteAllTagsAsyncTask extends AsyncTask<Void, Void, Void> {
        private TagDao tagDao;

        private DeleteAllTagsAsyncTask(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tagDao.deleteAllTags();
            return null;
        }
    }
}
