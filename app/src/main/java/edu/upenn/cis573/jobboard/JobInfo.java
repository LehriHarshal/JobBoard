package edu.upenn.cis573.jobboard;

import java.util.ArrayList;

/**
 * Created by Nishant on 10/12/2015.
 */
public class JobInfo {
    final ArrayList<String> jobNames = new ArrayList<String>();
    final ArrayList<String> jobDescriptions = new ArrayList<>();
    public JobInfo(){}


    public void addName(String name){
        jobNames.add(name);
    }
    public void addDescription(String description){
        jobDescriptions.add(description);
    }
    public String getName(int position){
        return jobNames.get(position);
    }
    public String getDescription(int position){
        return jobDescriptions.get(position);
    }

}


