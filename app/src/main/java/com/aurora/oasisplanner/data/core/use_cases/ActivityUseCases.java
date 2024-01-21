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
    private FragmentManager fragmentManager;

    public ActivityUseCases(ActivityRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Future<_Activity> get(long activityId) {
        return repository.getActivity(activityId);
    }
    public Future<Activity> getActivityWithChild(long activityId) {
        return repository.getActivityWithChild(activityId);
    }

    public void put(_Activity activity) {
        // NOT IMPEMENTED
        repository.insertActivity(activity);
    }

    public void delete(_Activity activity) {
        // NOT IMPEMENTED
        repository.deleteActivity(activity);
    }
}
