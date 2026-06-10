package SchedulerUI;

import Tasks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.microsoft.z3.*;

public class ScheduleSolver {
    public List<Schedule> GenerateSchedules(List<Task> tasks, long scheduleStart, long scheduleEnd) {
        System.out.println("Starting with schedule from " + scheduleStart + " to " + scheduleEnd);
        // Validate given input
        if (scheduleEnd <= scheduleStart){
            System.out.println("ERROR: ScheduleStart is equal or greater than ScheduleEnd.");
            return List.of();
        } else if (tasks.isEmpty()) {
            System.out.println("ERROR: No tasks provided.");
            return List.of();
        }

        // Set up solver
        List<Schedule> schedules = new ArrayList<>();
        Context ctx = new Context();
        Solver s = ctx.mkSolver();
        List<List<IntExpr>> variableList = new ArrayList<>();

        // Set up base case (singular task) requirements
        int i = 0;
        for (Task task : tasks){
            // create list of variables for current task
            List<IntExpr> taskVariableList = new ArrayList<>();
            // taskVariableList.get(0)
            taskVariableList.add((ctx.mkIntConst("taskID" + i)));
            // taskVariableList.get(1)
            taskVariableList.add((ctx.mkIntConst("taskStart" + i)));
            // taskVariableList.get(2)
            taskVariableList.add((ctx.mkIntConst("taskEnd" + i)));

            // Set task id
            s.add(ctx.mkEq(taskVariableList.getFirst(), ctx.mkInt(task.getTaskID())));
            // start time >= scheduleStart
            s.add(ctx.mkGe(taskVariableList.get(1), ctx.mkInt(scheduleStart)));
            // end time <= scheduleEnd
            s.add(ctx.mkLe(taskVariableList.get(2), ctx.mkInt(scheduleEnd)));
            // end time > start time
            s.add(ctx.mkGt(taskVariableList.get(2), taskVariableList.get(1)));
            // end time - start time = duration
            s.add(ctx.mkEq(ctx.mkSub(taskVariableList.get(2), taskVariableList.get(1)), ctx.mkInt(task.getDuration())));

            // move onto next task
            variableList.add(taskVariableList);
            i ++;
        }

        // Prevent tasks from overlapping
        // iterate through each list of task variables
        for (int j = 0; j < variableList.size(); j++) {
            // check them against all the other lists of task variables
            for (int k = 0; k < variableList.size(); k++) {
                // Only set constraints if not against self
                if (j != k){
                    IntExpr startA = variableList.get(j).get(1);
                    IntExpr endA = variableList.get(j).get(2);
                    IntExpr startB = variableList.get(k).get(1);
                    IntExpr endB = variableList.get(k).get(2);
                    // startA < startB and endA <= startB
                    BoolExpr ABeforeB = ctx.mkAnd(ctx.mkLt(startA, startB), ctx.mkLe(endA, startB));
                    // startB < startA and endB <= startA
                    BoolExpr BBeforeA = ctx.mkAnd(ctx.mkLt(startB, startA), ctx.mkLe(endB, startA));
                    // Hence task A must be before task B or task B must be before task A
                    s.add(ctx.mkOr(ABeforeB, BBeforeA));
                }
            }
        }

        // Set up optional tasks
        // Add dependencies

        // Generate all possible schedules
        while (true){
            // if sat:
                // convert solution to schedule
                    // create new solved task for each task
                    // Run solvedTask.calculateScore()
                // run schedule.UpdatePriorityScore() now that all solved tasks are updated
                // add to schedules
                // add constraints to solver forbidding this schedule as a solution
            // else: // just break, we're done
            break;
        }

        // Sort in order of highest priority first
        schedules.sort(Collections.reverseOrder());
        // Return our sorted schedule
        return schedules;
    }
}
