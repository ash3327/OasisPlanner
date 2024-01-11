package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.daos.MemoDao;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.util.Converters;

import java.util.List;

public class MemoRepository {
    private MemoDao memoDao;
    private LiveData<List<_Memo>> memos;

    public MemoRepository(MemoDao memoDao) {
        this.memoDao = memoDao;
        this.memos = memoDao.getMemos();
    }

    private boolean firstTime = true, firstTimeSubMemo = true;
    /**
    public void schedule(Memoscheduler Memoscheduler, LifecycleOwner obs, CountDownLatch latch) {
        firstTime = true;
        memos.observe(obs, (_Memos)->{
            if (!firstTime) return;
            firstTime = false;
            try {
                for (_Memo Memo : _Memos)
                    Memoscheduler.schedule(Memo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
            //Log.d("test3", "SCHEDULED memos: "+_Memos);
        });
        subMemos.observe(obs, (_subMemos)->{
            if (!firstTimeSubMemo) return;
            firstTimeSubMemo = false;
            try {
                for (_SubMemo Memo : _subMemos)
                    Memoscheduler.schedule(Memo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
            //Log.d("test3", "SCHEDULED memos: "+_Memos);
        });
    }**/

    public void insert(_Memo Memo) {
        new InsertMemoAsyncTask(memoDao).execute(Memo);
    }

    public _Memo getMemoFromId(long id) {
        try {
            return new MemoRepository.GetMemoAsyncTask(memoDao).execute(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Memo Not Found Exception: "+id);
        }
    }

    public void update(_Memo Memo) {
        new UpdateMemoAsyncTask(memoDao).execute(Memo);
    }

    public void delete(_Memo Memo) {
        new DeleteMemoAsyncTask(memoDao).execute(Memo);
    }

    public void deleteAllMemos() {
        new DeleteAllMemosAsyncTask(memoDao).execute();
    }

    public LiveData<List<_Memo>> getMemos() {
        return memos;
    }

    public LiveData<List<_Memo>> requestMemos(String searchEntry) {
        return memos = memoDao.getMemos(searchEntry, new Converters().spannableToString(searchEntry));
    }

    public LiveData<List<_Memo>> requestMemos() {
        return memos = memoDao.getMemos();
    }

    private static class InsertMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private MemoDao memoDao;

        private InsertMemoAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            memoDao.insert(memos[0]);
            return null;
        }
    }
    private static class UpdateMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private MemoDao memoDao;

        private UpdateMemoAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            memoDao.insert(memos[0]);
            return null;
        }
    }
    private static class GetMemoAsyncTask extends AsyncTask<Long, Void, _Memo> {
        private MemoDao memoDao;

        private GetMemoAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected _Memo doInBackground(Long... MemoIds) {
            return memoDao.getMemoById(MemoIds[0]);
        }
    }
    private static class DeleteMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private MemoDao memoDao;

        private DeleteMemoAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            memoDao.delete(memos[0]);
            return null;
        }
    }
    private static class DeleteAllMemosAsyncTask extends AsyncTask<Void, Void, Void> {
        private MemoDao memoDao;

        private DeleteAllMemosAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            memoDao.deleteAllMemos();
            return null;
        }
    }
}
