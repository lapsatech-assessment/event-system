package com.spartez.interviews.eventsystem.impl;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.spartez.interviews.eventsystem.Event;
import com.spartez.interviews.eventsystem.EventListener;
import com.spartez.interviews.eventsystem.EventManager;

public class MySimpleEventManager implements EventManager {

    private final ConcurrentMap<String, EventListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void publishEvent(Event event) {
	final Class<?> eventClazz = event.getClass();
	listeners.values().stream()
		.filter(l -> l.getHandledEventClasses().length == 0
			|| Arrays.asList(l.getHandledEventClasses()).contains(eventClazz))
		.forEach(l -> l.handleEvent(event));
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
	listeners.put(listenerKey, listener);
    }

    @Override
    public void unregisterListener(String listenerKey) {
	listeners.remove(listenerKey);
    }

}
