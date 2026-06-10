package SchedulerUI;

import Tasks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.microsoft.z3.*;

public class ScheduleSolver {

    private static class SolvingTask{
        int taskIndex;
        BoolExpr active;
        IntExpr taskStart;
        IntExpr taskEnd;

        public SolvingTask(int taskIndex, BoolExpr active, IntExpr taskStart, IntExpr taskEnd) {
            this.taskIndex = taskIndex;
            this.active = active;
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
            SolvingTask currentTask = new SolvingTask(i, ctx.mkBoolConst("active" + i), ctx.mkIntConst("taskStart" + i), ctx.mkIntConst("taskEnd" + i));

            BoolExpr taskConstraints = ctx.mkTrue();

            // start time >= scheduleStart
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGe(currentTask.taskStart, ctx.mkInt(scheduleStart)));
            // end time <= scheduleEnd
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkLe(currentTask.taskEnd, ctx.mkInt(scheduleEnd)));
            // start time >= taskStart
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGe(currentTask.taskStart, ctx.mkInt(task.getStartTime())));

            // Check if invalid start time
            if (scheduleEnd <= task.getStartTime()){
                System.out.println("Task start time " + task.getStartTime() + " exceeds schedule end time   " + scheduleEnd + ", skipping task");
                i ++;
                continue;
            }
            // Check if invalid end time
            if (task.getEndTime() < scheduleStart){
                System.out.println("Task end time " + task.getEndTime() + " before schedule start time " + scheduleStart + ", skipping task");
                i ++;
                continue;
            }
            // Check if invalid duration
            if (task.getDuration() <= 0){
                System.out.println("Duration cannot be 0, skipping task");
                i ++;
                continue;
            }

            // end time <= taskEnd
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkLe(currentTask.taskEnd, ctx.mkInt(task.getEndTime())));
            // end time > start time
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGt(currentTask.taskEnd, currentTask.taskStart));
            // end time - start time = duration
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkEq(ctx.mkSub(currentTask.taskEnd, currentTask.taskStart), ctx.mkInt(task.getDuration())));

            if (!task.isOptional()){
                taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkEq(currentTask.active, ctx.mkTrue()));
                s.add(taskConstraints);
            }
            else {
                // Set inactive task defaults
                BoolExpr InactiveTaskConstraints = ctx.mkAnd(ctx.mkEq(currentTask.active, ctx.mkFalse()));
                InactiveTaskConstraints = ctx.mkAnd(InactiveTaskConstraints, ctx.mkEq(currentTask.taskStart, ctx.mkInt(0)));
                InactiveTaskConstraints = ctx.mkAnd(InactiveTaskConstraints, ctx.mkEq(currentTask.taskEnd, ctx.mkInt(0)));

                // If active: fulfill active task constraints, otherwise set inactive defaults
                Expr ifActive = ctx.mkITE(ctx.mkEq(currentTask.active, ctx.mkTrue()), taskConstraints, InactiveTaskConstraints);
                s.add(ifActive);
            }

            // move onto next task
            variableList.add(currentTask);
            i ++;
        }

        // check if all tasks were invalid
        if (variableList.isEmpty()){
            System.out.println("All tasks were invalid. No schedule could be created.");
            return List.of();
        }

        // Prevent tasks from overlapping
        // iterate through each list of task variables
        for (int j = 0; j < variableList.size(); j++) {
            // check them against all the other lists of task variables
            for (int k = 0; k < variableList.size(); k++) {
//                System.out.println(j + " vs " + k);
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
                    // Hence task A must be before task B or task B must be before task A if both are active
                    BoolExpr ABeforeOrBBefore = ctx.mkOr(ABeforeB, BBeforeA);
                    BoolExpr bothActive = ctx.mkAnd(ctx.mkEq(variableList.get(j).active, ctx.mkTrue()), ctx.mkEq(variableList.get(k).active, ctx.mkTrue()));
                    Expr ifActive = ctx.mkITE(bothActive, ABeforeOrBBefore, ctx.mkTrue());
                    s.add(ifActive);
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
                    Expr active = m.evaluate(currentSolving.active, true);
                    boolean isActive = active.isTrue();
//                    System.out.println("Is active: "+ isActive);
                    IntNum tStart = (IntNum) m.evaluate(currentSolving.taskStart, true);
                    int tStartInt = tStart.getInt();
                    IntNum tEnd = (IntNum) m.evaluate(currentSolving.taskEnd, true);
                    int tEndInt = tEnd.getInt();

                    // Only add this task as solved if it's active
                    if (isActive){
                        // init new solved task
                        SolvedTask newSolvedTask = new SolvedTask(tasks.get(j), tStartInt, tEndInt);
                        // System.out.println("active: " + isActive + " start time: " + tStartInt + " end time: " +  tEndInt);

                        // add new solved task
                        solvedTaskList.add(newSolvedTask);
                    }

                    // add this solution to our new constraint
                    BoolExpr currentActive = ctx.mkEq(currentSolving.active, ctx.mkBool(isActive));
                    BoolExpr currentTStart = ctx.mkEq(currentSolving.taskStart, ctx.mkInt(tStartInt));
                    BoolExpr currentTEnd = ctx.mkEq(currentSolving.taskEnd, ctx.mkInt(tEndInt));

                    solution_c = ctx.mkAnd(solution_c, ctx.mkAnd(currentActive, ctx.mkAnd(currentTStart, currentTEnd)));
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
