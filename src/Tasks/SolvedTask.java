package Tasks;

// Class used to store the name, start and end time, and score of a solved task.
public class SolvedTask {

    // Name of the task.
    private String Name;
    private Task OriginalTask;
    // The given start time for the task in POSIX time.
    private long StartTime;
    // The given end time for the task in POSIX time.
    private long EndTime;
    // The heuristic score of the task.
    private float score;

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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    // used to calculate the score given priority and desired values
    public void calculateScore(){
        // # TO-DO: DO ACTUAL CALCULATIONS
        int priority = OriginalTask.getPriority();
        long desiredStart = OriginalTask.getStartTime();
        long desiredEnd = OriginalTask.getEndTime();

        this.score = priority;
    }
}
