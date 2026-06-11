package Tasks;

import java.util.List;

public class Task {

    // Name of the task. Displayed in the task list.
    private String Name;
    // The suggested start time for the task in POSIX time.
    private long StartTime;
    // The suggested end time for the task in POSIX time.
    private long EndTime;
    // The duration of the task in POSIX time.
    private long Duration;
    // If true, ensure this task starts at the given time.
    private boolean LockStartTime;
    // If true, ensure this task ends at the given time.
    private boolean LockEndTime;
    // Whether or not this task must be included in a solution.
    private boolean optional;
    // Priority of task
    private int priority;
    // List of task dependencies
    private List<TaskDependency> dependencies;
    // Unique ID of task
    private int taskID;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    public long getEndTime() {
        return EndTime;
    }

    public void setEndTime(long endTime) {
        EndTime = endTime;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public List<TaskDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<TaskDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Task copy(){
        Task newCopy =  new Task();

        newCopy.Name = Name;
        newCopy.StartTime = StartTime;
        newCopy.EndTime = EndTime;
        newCopy.Duration = Duration;
        newCopy.LockStartTime = LockStartTime;
        newCopy.LockEndTime= LockEndTime;
        newCopy.optional = optional;
        newCopy.priority = priority;
        newCopy.dependencies= dependencies;
        newCopy.taskID = taskID;
        return newCopy;
    }
}
