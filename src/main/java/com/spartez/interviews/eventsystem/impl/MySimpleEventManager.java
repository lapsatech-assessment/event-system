package com.spartez.interviews.eventsystem.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.spartez.interviews.eventsystem.Event;
import com.spartez.interviews.eventsystem.EventListener;
import com.spartez.interviews.eventsystem.EventManager;

public class MySimpleEventManager implements EventManager {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    private final Map<String, EventListener> listeners = new HashMap<>();

    @Override
    public void publishEvent(final Event event) {
	final Class<?> eventClazz = event.getClass();
	final Collection<EventListener> eventListenertsCollection;
	try {
	    r.lock();
	    eventListenertsCollection = listeners.values();
	} finally {
	    r.unlock();
	}
	for (EventListener listener : eventListenertsCollection) {
	    final Class[] handlingClasses = listener.getHandledEventClasses();
	    if (handlingClasses.length == 0 || Arrays.asList(handlingClasses).contains(eventClazz))
		listener.handleEvent(event);
	}
	//
	// listeners.values().stream()
	// .filter(l -> l.getHandledEventClasses().length == 0
	// || Arrays.asList(l.getHandledEventClasses()).contains(eventClazz))
	// .forEach(l -> l.handleEvent(event));
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
	try {
	    w.lock();
	    listeners.put(listenerKey, listener);
	} finally {
	    w.unlock();
	}
    }

    @Override
    public void unregisterListener(String listenerKey) {
	try {
	    w.lock();
	    listeners.remove(listenerKey);
	} finally {
	    w.unlock();
	}
    }

}
