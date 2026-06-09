package Tasks;

public class Task {

    // Name of the task. Displayed in the task list.
    private String Name;
    // The suggested start time for the task.
    private String StartTime;
    // The suggested end time for the task.
    private String EndTime;
    // The duration of the task.
    private String Duration;
    // If true, ensure this task starts at the given time.
    private boolean LockStartTime;
    // If true, ensure this task ends at the given time.
    private boolean LockEndTime;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public boolean isLockStartTime() {
        return LockStartTime;
    }

    public void setLockStartTime(boolean lockStartTime) {
        LockStartTime = lockStartTime;
    }

    public boolean isLockEndTime() {
        return LockEndTime;
    }

    public void setLockEndTime(boolean lockEndTime) {
        LockEndTime = lockEndTime;
    }
}
