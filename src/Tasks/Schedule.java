package Tasks;

import java.util.Collections;
import java.util.List;

public class Schedule implements Comparable <Schedule>{
    private List<SolvedTask> TaskList;
    private float priorityScore;

    public Schedule(List<SolvedTask> taskList) {
        Collections.sort(taskList);
        TaskList = taskList;
        updatePriorityScore();
    }

    // Sets priority score to a sum of all solved tasks' scores
    private void updatePriorityScore() {
        priorityScore = 0;
        for(SolvedTask solvedTask : TaskList){
            priorityScore += solvedTask.getScore();
        }
    }

    public List<SolvedTask> getTaskList() {
        return TaskList;
    }

    public void setTaskList(List<SolvedTask> taskList) {
        TaskList = taskList;
    }

    public float getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(float priorityScore) {
        this.priorityScore = priorityScore;
    }

    @Override
    public int compareTo(Schedule otherSchedule){
        return Float.compare(priorityScore, otherSchedule.getPriorityScore());
    }
}
