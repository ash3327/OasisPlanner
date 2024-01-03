package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;
import android.text.SpannableStringBuilder;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.AgendaDao;
import com.aurora.oasisplanner.data.model.entities._Memo;
import com.aurora.oasisplanner.data.util.Converters;

import java.util.List;

public class MemoRepository {
    private AgendaDao agendaDao;
    private LiveData<List<_Memo>> memos;

    public MemoRepository(AgendaDao agendaDao) {
        this.agendaDao = agendaDao;
        this.memos = agendaDao.getMemos();
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
        new InsertMemoAsyncTask(agendaDao).execute(Memo);
    }

    public _Memo getMemoFromId(long id) {
        try {
            return new MemoRepository.GetMemoAsyncTask(agendaDao).execute(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Memo Not Found Exception: "+id);
        }
    }

    public void update(_Memo Memo) {
        new UpdateMemoAsyncTask(agendaDao).execute(Memo);
    }

    public void delete(_Memo Memo) {
        new DeleteMemoAsyncTask(agendaDao).execute(Memo);
    }

    public void deleteAllMemos() {
        new DeleteAllMemosAsyncTask(agendaDao).execute();
    }

    public LiveData<List<_Memo>> getMemos() {
        return memos;
    }

    public LiveData<List<_Memo>> requestMemos(String searchEntry) {
        return memos = agendaDao.getMemos(searchEntry, new Converters().spannableToString(searchEntry));
    }

    public LiveData<List<_Memo>> requestMemos() {
        return memos = agendaDao.getMemos();
    }

    private static class InsertMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private AgendaDao agendaDao;

        private InsertMemoAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            agendaDao.insert(memos[0]);
            return null;
        }
    }
    private static class UpdateMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private AgendaDao agendaDao;

        private UpdateMemoAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            agendaDao.insert(memos[0]);
            return null;
        }
    }
    private static class GetMemoAsyncTask extends AsyncTask<Long, Void, _Memo> {
        private AgendaDao agendaDao;

        private GetMemoAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected _Memo doInBackground(Long... MemoIds) {
            return agendaDao.getMemoById(MemoIds[0]);
        }
    }
    private static class DeleteMemoAsyncTask extends AsyncTask<_Memo, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteMemoAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Memo... memos) {
            agendaDao.delete(memos[0]);
            return null;
        }
    }
    private static class DeleteAllMemosAsyncTask extends AsyncTask<Void, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteAllMemosAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            agendaDao.deleteAllMemos();
            return null;
        }
    }
}
