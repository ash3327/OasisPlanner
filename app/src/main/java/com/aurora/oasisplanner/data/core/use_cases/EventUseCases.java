package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.repository.EventRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EventUseCases {
    private EventRepository repository;
    private FragmentManager fragmentManager;

    public EventUseCases(EventRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Future<_AlarmList> get(long eventId) {
        return repository.getEvent(eventId);
    }

    public long put(_AlarmList event) {
        try {
            return repository.insertEvent(event).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event.id;
    }

    public void delete(_AlarmList event) {
        repository.deleteEvent(event);
    }


    public Future<AlarmList> getWithChild(long eventId) {
        return repository.getEventWithChild(eventId);
    }

    public void putWithChild(AlarmList event) {
        repository.insertEventWithChild(event);
    }

    public void deleteWithChild(AlarmList event) {
        repository.deleteEventWithChild(event);
    }
}
