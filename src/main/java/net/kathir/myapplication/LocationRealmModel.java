package net.kathir.myapplication;

import io.realm.RealmObject;

public class LocationRealmModel extends RealmObject {

    Double latitude;
    Double longitude;
    long timeStamp;


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public String toString() {
        return "LocationRealmModel{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
