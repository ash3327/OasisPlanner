package com.aurora.oasisplanner.data.core.use_cases;

import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.repository.EventRepository;

import java.util.concurrent.Future;

public class EventUseCases {
    private EventRepository repository;

    public EventUseCases(EventRepository repository) {
        this.repository = repository;
    }

    public Future<_Event> get(long eventId) {
        return repository.getEvent(eventId);
    }

    public void delete(_Event event) {
        repository.deleteEvent(event);
    }


    public Future<Event> getWithChild(long eventId) {
        return repository.getEventWithChild(eventId);
    }

    public long putWithChild(_Event event) {
        try {
            return repository.insertEvent(event).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event.id;
    }
//    public void putWithChild(Event event) {
//        repository.insertEventWithChild(event);
//    }

    public void deleteWithChild(Event event) {
        repository.deleteEventWithChild(event);
    }
}
