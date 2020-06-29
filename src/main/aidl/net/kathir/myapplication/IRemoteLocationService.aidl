// IRemoteLocationService.aidl
package net.kathir.myapplication;

// Declare any non-default types here with import statements

interface IRemoteLocationService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */


   void locationinfo(double latitude, double longitude, long timestamp);
}
