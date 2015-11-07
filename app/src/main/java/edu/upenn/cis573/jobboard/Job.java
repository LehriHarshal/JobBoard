package edu.upenn.cis573.jobboard;

/**
 * Jobs always start as "available" (which means it is up for grabs). Once the poster picks
 * a user to complete it, the status becomes "in progress". Upon completion, "complete".
 * Created by joeyraso on 4/2/15.
 */


import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.location.Location;
import android.location.LocationListener;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Job")
public class Job extends ParseObject {
    protected String jobId;
    protected String jobName;
    protected String typeDescription;

    public Job(String name, String description, String start, String end,String latitude,String longitude,String typeDescription ) {
        setJobName(name);
        setJobDescription(description);
        setStartDate(start);
        setEndDate(end);
        setTypeDescription(typeDescription);
        setJobPoster();
        setJobStatus("available");  //default
        setJobLocation(latitude,longitude);
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);


//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                int lat = (int) (location.getLatitude() * 1E6);
//                int lng = (int) (location.getLongitude() * 1E6);
//                GeoPoint point = new GeoPoint(lat, lng);
//                put("jobLocation", point);
//            }
//        }
    }

    public Job() {

    }

    public void  setJobLocation(String latitude, String longitude)
    {
        setLatitude(latitude);
        setLongitude(longitude);
        setGeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    //Setters
    public void setJobName(String name) {
        put("jobName", name);
    }

    public void setJobDescription(String description) {
        put("jobDescription", description);
    }

    public void setStartDate(String start) {
        put("startDate", start);
    }

    public void setEndDate(String end) {
        put("endDate", end);
    }

    public void setJobPoster() {
        put("jobPoster", ParseUser.getCurrentUser().getObjectId());
    }

    public void setJobStatus(String status) {
        put("jobStatus", status);
    }

    public void setLatitude(String latitude)
    {
        put("Latitude",latitude);
    }

    public void setLongitude(String longitude)
    {
        put("Longitude",longitude);
    }

    public void setTypeDescription(String typeDescription) { put("typeDescription",typeDescription);}

    public void setGeoPoint(double latitude,double longitude)
    {
        ParseGeoPoint point = new ParseGeoPoint(latitude,longitude);
        put("Location", point);
    }
    /*public void setJobDoer(String userID) {
        put("jobDoer", userID);
    }*/


    //Getters
    public String getJobName() {
        return getString("jobName");
    }

    public String getJobDescription() {
        return getString("jobDescription");
    }

    public String getStartDate() {
        return getString("startDate");
    }

    public String getEndDate() {
        return getString("endDate");
    }

    /*public List<String> getJobRequestors() {
        return getList("jobRequestors");
    }*/

    /*public String getJobDoer() {
        return getString("jobDoer");
    }*/

    public String getJobPoster() {
        return getString("jobPoster");
    }

    public String getLatitude() {
        return getString("Latitude");
    }
    public String getLongitude() {
        return getString("Longitude");
    }

    public String getTypeDescription() {return getString("typeDescription");}

}