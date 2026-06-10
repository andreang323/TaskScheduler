package SchedulerUI;

import Tasks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.microsoft.z3.*;

public class ScheduleSolver {

    private static class SolvingTask{
        int taskIndex;
        IntExpr taskStart;
        IntExpr taskEnd;

        public SolvingTask(int taskIndex, IntExpr taskStart, IntExpr taskEnd) {
            this.taskIndex = taskIndex;
            this.taskStart = taskStart;
            this.taskEnd = taskEnd;
        }
    }

    public List<Schedule> GenerateSchedules(List<Task> tasks, long scheduleStart, long scheduleEnd, int maxSolutions) {
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
        List<SolvingTask> variableList = new ArrayList<>();

        // Set up base case (singular task) requirements
        int i = 0;
        for (Task task : tasks){
            // create new intermediate SolvingTask for current task
            SolvingTask currentTask = new SolvingTask(i, ctx.mkIntConst("taskStart" + i), ctx.mkIntConst("taskEnd" + i));

            // start time >= scheduleStart
            s.add(ctx.mkGe(currentTask.taskStart, ctx.mkInt(scheduleStart)));
            // end time <= scheduleEnd
            s.add(ctx.mkLe(currentTask.taskEnd, ctx.mkInt(scheduleEnd)));
            // start time >= taskStart
            s.add(ctx.mkGe(currentTask.taskStart, ctx.mkInt(task.getStartTime())));
            System.out.println(task.getStartTime());
            // end time <= taskEnd
            System.out.println(task.getEndTime());
            s.add(ctx.mkLe(currentTask.taskEnd, ctx.mkInt(task.getEndTime())));
            // end time > start time
            s.add(ctx.mkGt(currentTask.taskEnd, currentTask.taskStart));
            // end time - start time = duration
            s.add(ctx.mkEq(ctx.mkSub(currentTask.taskEnd, currentTask.taskStart), ctx.mkInt(task.getDuration())));

            // move onto next task
            variableList.add(currentTask);
            i ++;
        }

        // Prevent tasks from overlapping
        // iterate through each list of task variables
        for (int j = 0; j < variableList.size(); j++) {
            // check them against all the other lists of task variables
            for (int k = 0; k < variableList.size(); k++) {
                System.out.println(j + " vs " + k);
                // Only set constraints if not against self
                if (j != k){
                    IntExpr startA = variableList.get(j).taskStart;
                    IntExpr endA = variableList.get(j).taskEnd;
                    IntExpr startB = variableList.get(k).taskStart;
                    IntExpr endB = variableList.get(k).taskEnd;
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

        // Generate all possible schedules if maxSolutions == 0
        // else generate only maxSolutions amount of solutions
        int k = 0;
        while ((k < maxSolutions)|| (maxSolutions == 0)){
            if (s.check() == Status.SATISFIABLE){
                Model m = s.getModel();
                BoolExpr solution_c = ctx.mkTrue();
                List<SolvedTask> solvedTaskList = new ArrayList<>();
                for (int j = 0; j < variableList.size(); j++) {
                    // get solution for this task
                    SolvingTask currentSolving = variableList.get(j);
                    IntNum tStart = (IntNum) m.evaluate(currentSolving.taskStart, true);
                    int tStartInt = tStart.getInt();
                    IntNum tEnd = (IntNum) m.evaluate(currentSolving.taskEnd, true);
                    int tEndInt = tEnd.getInt();

                    // init new solved task
                    SolvedTask newSolvedTask = new SolvedTask(tasks.get(j), tStartInt, tEndInt);
                    // System.out.println("start time: " + tStartInt + " end time: " +  tEndInt);

                    // add new solved task
                    solvedTaskList.add(newSolvedTask);

                    // add this solution to our new constraint
                    BoolExpr currentTStart = ctx.mkEq(currentSolving.taskStart, ctx.mkInt(tStartInt));
                    BoolExpr currentTEnd = ctx.mkEq(currentSolving.taskEnd, ctx.mkInt(tEndInt));

                    solution_c = ctx.mkAnd(solution_c, ctx.mkAnd(currentTStart, currentTEnd));
                }

                // Finalize our schedule
                schedules.add(new Schedule(solvedTaskList));
                // add our new constraint
                s.add(ctx.mkNot(solution_c));
                // increment k
                k ++;
            }
            else {break;}
        }

        // Sort in order of highest priority first
        schedules.sort(Collections.reverseOrder());
        // Return our sorted schedule
        return schedules;
    }
}
