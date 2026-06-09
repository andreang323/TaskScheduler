package Tasks;

public class Task {

    // Name of the task. Displayed in the task list.
    private String Name;
    // The suggested start time for the task.
    private int StartTime;
    // The suggested end time for the task.
    private int EndTime;
    // The duration of the task.
    private int Duration;
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

    public int getStartTime() {
        return StartTime;
    }

    public void setStartTime(int startTime) {
        StartTime = startTime;
    }

    public int getEndTime() {
        return EndTime;
    }

    public void setEndTime(int endTime) {
        EndTime = endTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
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
