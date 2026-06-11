package Tasks;

// Class used to store the name, start and end time, and score of a solved task.
public class SolvedTask implements Comparable <SolvedTask>{

    // Name of the task.
    private String Name;
    private final Task OriginalTask;
    // The given start time for the task in POSIX time.
    private long StartTime;
    // The given end time for the task in POSIX time.
    private long EndTime;
    // The heuristic score of the task.
    private float score;


    public SolvedTask(Task originalTask, long startTime, long endTime) {
        OriginalTask = originalTask;
        Name = originalTask.getName();
        StartTime = startTime;
        EndTime = endTime;
        calculateScore();
    }


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

    public Task getOriginalTask() {
        return OriginalTask;
    }

    @Override
    public int compareTo(SolvedTask otherTask){
        return Float.compare(getStartTime(), otherTask.getStartTime());
    }

    // used to calculate the score given priority and desired values
    // metric will be priority - penalty, where penalty = sum of difference in hours in terms of start time, end time, and duration
    public void calculateScore(
    ) {
        float calculatedScore = OriginalTask.getPriority();

        long actualDuration = EndTime - StartTime;

        long startDifference = Math.abs(StartTime - OriginalTask.getStartTime());
        long endDifference = Math.abs(EndTime - OriginalTask.getEndTime());
        long durationDifference = Math.abs(actualDuration - OriginalTask.getDuration());

        // difference in hours
        float startPenalty = startDifference / 3600.0f;
        float endPenalty = endDifference / 3600.0f;
        float durationPenalty = durationDifference / 3600.0f;

        float totalPenalty = startPenalty + endPenalty + durationPenalty;

        calculatedScore = calculatedScore / (1.0f + totalPenalty);

        this.score = calculatedScore;
    }
}
