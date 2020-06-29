package net.kathir.myapplication;

import com.google.gson.annotations.SerializedName;

public class LocationDetailModel {


    @SerializedName("latitude")
    String latitude = "";
    @SerializedName("longitude")
    String longitude = "";
    @SerializedName("timeStamp")
    String timeStamp = "";
    @SerializedName("rowId")
    String rowId = "";

    public LocationDetailModel(String rowId,String latitude, String longitude, String timeStamp) {
        this.rowId = rowId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public LocationDetailModel()
    {

    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }



}
