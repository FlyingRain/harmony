package com.flyingrain.fakelocation.slice;

import com.flyingrain.fakelocation.ResourceTable;
import com.flyingrain.fakelocation.event.MyEventHandler;
import com.flyingrain.fakelocation.event.InnerEventHandler;
import com.flyingrain.fakelocation.location.LocationChangeListener;
import com.flyingrain.fakelocation.location.LocatorResult;
import com.flyingrain.fakelocation.location.model.LocationModel;
import com.flyingrain.fakelocation.util.PermissionUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.app.Context;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.location.*;
import ohos.sensor.agent.CategoryOrientationAgent;
import ohos.sensor.bean.CategoryOrientation;
import ohos.sensor.data.CategoryOrientationData;
import ohos.sensor.listener.ICategoryOrientationDataCallback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAbilitySlice extends AbilitySlice implements InnerEventHandler, LocationChangeListener {

    private static final int EVENT_ID= 0x12;

    private static final String PERM_LOCATION = "ohos.permission.LOCATION";

    private static final long SIMPLE_INTERNAL_NANOSECONDS = 500000000;

    private static String FORMAT_DEGREE = "%.2f";

    private static final float DEFLECTION_FLAG = -1;

    private LocatorResult locatorResult = new LocatorResult();

    private MyEventHandler myEventHandler = new MyEventHandler(EventRunner.current());

    private Locator locator;

    private RequestParam requestParam;

    private GeoConvert geoConvert;

    private List<GeoAddress> gaList;

    private LocationModel locationModel;

    private Text geoAddressInfoText;

    private Text locationInfoText;

    private Text compassAngleText;

    private Image compassImg;

    private CategoryOrientationAgent categoryOrientationAgent;

    private float degree;

    private ICategoryOrientationDataCallback iCategoryOrientationDataCallback;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        PermissionUtils.requestPermission(PERM_LOCATION,this);

        initComponent();

        initCompass();
    }




    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }



    private void initComponent() {
        Button startLocatingButton = (Button) findComponentById(ResourceTable.Id_start_locate);
        startLocatingButton.setClickedListener(component -> {
            if (PermissionUtils.hasPermission(PERM_LOCATION,MainAbilitySlice.this)) {
                int timeInterval = 0;
                int distanceInterval = 0;
                locator = new Locator(MainAbilitySlice.this);
                requestParam = new RequestParam(RequestParam.PRIORITY_LOW_POWER, timeInterval, distanceInterval);
                locator.startLocating(requestParam, locatorResult);
            }
        });

        Button stopLocatingButton = (Button) findComponentById(ResourceTable.Id_stop_locate);
        stopLocatingButton.setClickedListener(component -> {
            if (locator != null) {
                locator.stopLocating(locatorResult);
            }
        });

        locationInfoText = (Text) findComponentById(ResourceTable.Id_text_location_info);

        geoAddressInfoText = (Text) findComponentById(ResourceTable.Id_geo_address_info);

    }


    private void initCompass() {
        compassImg = (Image) findComponentById(ResourceTable.Id_compass_img);
        compassAngleText = (Text) findComponentById(ResourceTable.Id_compass_angle_text);

        categoryOrientationAgent = new CategoryOrientationAgent();
        CategoryOrientation categoryOrientation = categoryOrientationAgent.getSingleSensor(CategoryOrientation.SENSOR_TYPE_ORIENTATION);
        iCategoryOrientationDataCallback  = new ICategoryOrientationDataCallback() {
            @Override
            public void onSensorDataModified(CategoryOrientationData categoryOrientationData) {
                degree = categoryOrientationData.getValues()[0];
                myEventHandler.sendEvent(0);
            }

            @Override
            public void onAccuracyDataModified(CategoryOrientation categoryOrientation, int i) {

            }

            @Override
            public void onCommandCompleted(CategoryOrientation categoryOrientation) {

            }
        };
        categoryOrientationAgent.setSensorDataCallback(iCategoryOrientationDataCallback,categoryOrientation, SIMPLE_INTERNAL_NANOSECONDS);
    }

    @Override
    public void handleEvent(InnerEvent event) {
        if (event.eventId == EVENT_ID) {
            notifyLocationChange(locationModel);
        }
        compassAngleText.setText(getRotation(degree));
        compassImg.setRotation(DEFLECTION_FLAG * degree);
    }

    private String getRotation(float degree) {
        if (degree >= 0 && degree <= 22.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " N";
        } else if (degree > 22.5 && degree <= 67.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " NE";
        } else if (degree > 67.5 && degree <= 112.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " E";
        } else if (degree > 112.5 && degree <= 157.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " SE";
        } else if (degree > 157.5 && degree <= 202.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " S";
        } else if (degree > 202.5 && degree <= 247.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " SW";
        } else if (degree > 247.5 && degree <= 282.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " W";
        } else if (degree > 282.5 && degree <= 337.5) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " NW";
        } else if (degree > 337.5 && degree <= 360.0) {
            return String.format(Locale.ENGLISH, FORMAT_DEGREE, degree) + " N";
        } else {
            return "/";
        }
    }

    private void notifyLocationChange(LocationModel locationModel) {
        locationInfoText.setText("");
        locationInfoText.append("纬度 : " + locationModel.getLatitude() + System.lineSeparator());
        locationInfoText.append("经度 : " + locationModel.getLongitude() + System.lineSeparator());
        locationInfoText.append("速度 : " + locationModel.getSpeed() + " m/s" + System.lineSeparator());
        locationInfoText.append("方向 : " + locationModel.getDirection() + System.lineSeparator());
        locationInfoText.append("时间 : " + locationModel.getTime());

        geoAddressInfoText.setText("");
        geoAddressInfoText.append("子行政区域 : " + locationModel.getSubAdministrative() + System.lineSeparator());
        geoAddressInfoText.append("道路名 : " + locationModel.getRoadName() + System.lineSeparator());
        geoAddressInfoText.append("城市 : " + locationModel.getLocality() + System.lineSeparator());
        geoAddressInfoText.append("行政区域 : " + locationModel.getAdministrative() + System.lineSeparator());
        geoAddressInfoText.append("国家 : " + locationModel.getCountryName());
    }

    @Override
    public void onChange(Location location) {
        if (location != null) {
            Date date = new Date(location.getTimeStamp());
            locationModel = new LocationModel();
            locationModel.setTime(date.toString());
            locationModel.setLatitude(location.getLatitude());
            locationModel.setLongitude(location.getLongitude());
            locationModel.setPrecision(location.getAccuracy());
            locationModel.setSpeed(location.getSpeed());
            locationModel.setDirection(location.getDirection());
            fillGeoInfo(locationModel, location.getLatitude(), location.getLongitude());
            myEventHandler.sendEvent(EVENT_ID);
            gaList.clear();
        }
    }

    private boolean fillGeoInfo(LocationModel locationModel, double latitude, double longitude) {
        if (geoConvert == null) {
            geoConvert = new GeoConvert();
        }
        if (geoConvert.isGeoAvailable()) {
            try {
                gaList = geoConvert.getAddressFromLocation(latitude, longitude, 1);
                if (!gaList.isEmpty()) {
                    GeoAddress geoAddress = gaList.get(0);
                    setGeo(locationModel, geoAddress);
                    return true;
                }
            } catch (IllegalArgumentException | IOException e) {
            }
        }
        return false;
    }

    private void setGeo(LocationModel locationModel, GeoAddress geoAddress) {
        locationModel.setRoadName(checkIfNullOrEmpty(geoAddress.getRoadName()));
        locationModel.setLocality(checkIfNullOrEmpty(geoAddress.getLocality()));
        locationModel.setSubAdministrative(checkIfNullOrEmpty(geoAddress.getSubAdministrativeArea()));
        locationModel.setAdministrative(checkIfNullOrEmpty(geoAddress.getAdministrativeArea()));
        locationModel.setCountryName(checkIfNullOrEmpty(geoAddress.getCountryName()));

    }

    private String checkIfNullOrEmpty(String roadName) {
        if (roadName == null || roadName.isEmpty()) {
            return "NA";
        }
        return roadName;
    }
}

