package com.flyingrain.fakelocation.location;

import ohos.location.Location;
import ohos.location.LocatorCallback;

import java.util.ArrayList;
import java.util.List;

public class LocatorResult implements LocatorCallback {

    private List<LocationChangeListener> locationChangeListeners = new ArrayList<>(4);

    public void registerListener(LocationChangeListener locationChangeListener) {
        locationChangeListeners.add(locationChangeListener);
    }

    @Override
    public void onLocationReport(Location location) {
        locationChangeListeners.forEach(locationChangeListener -> locationChangeListener.onChange(location));
    }

    @Override
    public void onStatusChanged(int i) {

    }

    @Override
    public void onErrorReport(int i) {

    }
}
