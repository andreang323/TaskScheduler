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
    // metric will be priority - penalty, where penalty = sum of difference in hours in terms of start time, end time, and duration
    public void calculateScore(
            int priority,
            long desiredStart,
            long desiredEnd,
            long desiredDuration
    ) {
        float calculatedScore = priority;

        long actualDuration = EndTime - StartTime;

        long startDifference = Math.abs(StartTime - desiredStart);
        long endDifference = Math.abs(EndTime - desiredEnd);
        long durationDifference = Math.abs(actualDuration - desiredDuration);

        // difference in hours
        float startPenalty = startDifference / 3600.0f;
        float endPenalty = endDifference / 3600.0f;
        float durationPenalty = durationDifference / 3600.0f;

        float totalPenalty = startPenalty + endPenalty + durationPenalty;

        calculatedScore = calculatedScore / (1.0f + totalPenalty);

        this.score = calculatedScore;
    }
}
