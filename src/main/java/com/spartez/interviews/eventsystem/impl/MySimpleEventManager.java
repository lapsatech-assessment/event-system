package com.spartez.interviews.eventsystem.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.spartez.interviews.eventsystem.Event;
import com.spartez.interviews.eventsystem.EventListener;
import com.spartez.interviews.eventsystem.EventManager;

public class MySimpleEventManager implements EventManager {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, EventListener> listeners = new HashMap<>();

    @Override
    public void publishEvent(Event event) {
	final Class<?> eventClazz = event.getClass();
	try {
	    lock.readLock().lock();
	    listeners.values().stream()
		    .filter(l -> l.getHandledEventClasses().length == 0
			    || Arrays.asList(l.getHandledEventClasses()).contains(eventClazz))
		    .forEach(l -> l.handleEvent(event));
	} finally {
	    lock.readLock().unlock();
	}
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
	try {
	    lock.writeLock().lock();
	    listeners.put(listenerKey, listener);
	} finally {
	    lock.writeLock().unlock();
	}
    }

    @Override
    public void unregisterListener(String listenerKey) {
	try {
	    lock.writeLock().lock();
	    listeners.remove(listenerKey);
	} finally {
	    lock.writeLock().unlock();
	}
    }

}
