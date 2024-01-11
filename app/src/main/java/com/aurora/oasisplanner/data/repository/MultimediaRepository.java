package com.aurora.oasisplanner.data.repository;

import com.aurora.oasisplanner.data.datasource.daos.MultimediaDao;
import com.aurora.oasisplanner.data.model.entities.util._Doc;

import java.util.concurrent.Executor;

public class MultimediaRepository {
    private final MultimediaDao multimediaDao;
    private final Executor executor;

    public MultimediaRepository(MultimediaDao multimediaDao, Executor executor) {
        this.multimediaDao = multimediaDao;
        this.executor = executor;
    }

    public void insertDoc(_Doc doc) {
        multimediaDao.insert(doc);
    }

    public void deleteDoc(_Doc doc) {
        multimediaDao.delete(doc);
    }
}
