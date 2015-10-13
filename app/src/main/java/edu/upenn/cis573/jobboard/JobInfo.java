public class JobInfo {
    final ArrayList<String> jobNames = new ArrayList<String>();
    final ArrayList<String> jobDescriptions = new ArrayList<>();


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