package net.kathir.myapplication;

import android.location.Location;

public class MessageEvent {



    public Location mlocation;

    public MessageEvent(Location location)
    {
        mlocation = location;

    }

    public MessageEvent() {
    }


    public Location getLocation() {
        return mlocation;
    }

    public void setLocation(Location location) {
        mlocation = location;
    }



}
