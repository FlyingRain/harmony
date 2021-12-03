package com.flyingrain.fakelocation.event;

import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyEventHandler extends EventHandler {

    private List<InnerEventHandler> innerEventHandlers = new ArrayList<>(4);


    public MyEventHandler(EventRunner runner) throws IllegalArgumentException {
        super(runner);
    }


    @Override
    protected void processEvent(InnerEvent event) {
        innerEventHandlers.forEach(innerEventHandler -> innerEventHandler.handleEvent(event));
    }


    public void registerListener(InnerEventHandler innerEventHandler) {
        if (innerEventHandler != null) {
            innerEventHandlers.add(innerEventHandler);
        }
    }

}
