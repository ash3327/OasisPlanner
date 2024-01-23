package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.repository.ActivityRepository;

import java.util.concurrent.Future;

public class ActivityUseCases {
    private ActivityRepository repository;

    public ActivityUseCases(ActivityRepository repository) {
        this.repository = repository;
    }

    public Future<_Activity> get(long activityId) {
        return repository.getActivity(activityId);
    }
    public Future<Activity> getActivityWithChild(long activityId) {
        return repository.getActivityWithChild(activityId);
    }
}
