package SchedulerUI;

import Tasks.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.microsoft.z3.*;

public class ScheduleSolver {

    private static class SolvingTask{
        static int globalSolvingTaskID = 0;
        int solvingTaskID = 0;
        int taskIndex;
        int taskID;
        BoolExpr active;
        IntExpr taskStart;
        IntExpr taskEnd;
        List<List<ArithExpr>> dependencyListTerms;
        Expr taskConstraints;

        public SolvingTask(int taskIndex, Task task, Context ctx, long scheduleStart, long scheduleEnd) {
            this.taskIndex = taskIndex;
            this.solvingTaskID = globalSolvingTaskID;
            globalSolvingTaskID ++;
            this.taskID = task.getTaskID();
            this.active = ctx.mkBoolConst("active" + taskIndex + "_" + solvingTaskID);
            this.taskStart = ctx.mkIntConst("taskStart" + taskIndex + "_" + solvingTaskID);
            this.taskEnd = ctx.mkIntConst("taskEnd" + taskIndex + "_" + solvingTaskID);
            this.dependencyListTerms = new ArrayList<>();

            taskConstraints = ctx.mkTrue();

            // start time >= scheduleStart
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGe(taskStart, ctx.mkInt(scheduleStart)));
            // end time <= scheduleEnd
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkLe(taskEnd, ctx.mkInt(scheduleEnd)));
            // start time >= taskStart
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGe(taskStart, ctx.mkInt(task.getStartTime())));

            // end time <= taskEnd
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkLe(taskEnd, ctx.mkInt(task.getEndTime())));
            // end time > start time
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkGt(taskEnd, taskStart));
            // end time - start time = duration
            taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkEq(ctx.mkSub(taskEnd, taskStart), ctx.mkInt(task.getDuration())));

            if (!task.isOptional()){
                taskConstraints = ctx.mkAnd(taskConstraints, ctx.mkEq(active, ctx.mkTrue()));
            }
            else {
                // Set inactive task defaults
                BoolExpr InactiveTaskConstraints = ctx.mkAnd(ctx.mkEq(active, ctx.mkFalse()));
                InactiveTaskConstraints = ctx.mkAnd(InactiveTaskConstraints, ctx.mkEq(taskStart, ctx.mkInt(0)));
                InactiveTaskConstraints = ctx.mkAnd(InactiveTaskConstraints, ctx.mkEq(taskEnd, ctx.mkInt(0)));

                // If active: fulfill active task constraints, otherwise set inactive defaults
                Expr ifActive = ctx.mkITE(ctx.mkEq(active, ctx.mkTrue()), taskConstraints, InactiveTaskConstraints);
                taskConstraints =  ifActive;
            }
        }
    }

    public List<Schedule> GenerateSchedules(List<Task> tasks, long scheduleStart, long scheduleEnd, int maxSolutions) {
        Instant startInstant = Instant.ofEpochSecond(scheduleStart * 60);
        Instant endInstant = Instant.ofEpochSecond(scheduleEnd * 60);
        LocalDateTime startTime =
                LocalDateTime.ofInstant(startInstant, ZoneId.of("UTC"));
        LocalDateTime endTime =
                LocalDateTime.ofInstant(endInstant, ZoneId.of("UTC"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStart = startTime.format(formatter);
        String formattedEnd = endTime.format(formatter);

        System.out.println("Starting with schedule from " + formattedStart + " to " + formattedEnd);
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
        List<SolvingTask> solvingTaskList = new ArrayList<>();
        // Track all invalid task IDs, since any tasks dependent on an invalid task becomes invalid
        List<Integer> invalidTaskIDs = new ArrayList<>();

        // Set up base case (singular task) requirements
        int i = 0;
        // Set up lookup table for tasks
        Map<Integer, Task> taskMap = new HashMap<>();
        for (Task task : tasks){
//            System.out.println(
//                    "INPUT TASK: "
//                            + task.getName()
//                            + " start=" + task.getStartTime()
//                            + " end=" + task.getEndTime()
//                            + " duration=" + task.getDuration()
//                            + " window=" + (task.getEndTime() - task.getStartTime())
//                            + " optional=" + task.isOptional()
//            );
            taskMap.put(task.getTaskID(), task);

            // Validate task:
            // Check if invalid start time
            if (scheduleEnd <= task.getStartTime()){
                System.out.println("Task start time " + task.getStartTime() + " exceeds schedule end time   " + scheduleEnd + ", skipping task " + task.getTaskID());
                invalidTaskIDs.add(task.getTaskID());
                i ++;
                continue;
            }
            // Check if invalid end time
            if (task.getEndTime() < scheduleStart){
                System.out.println("Task end time " + task.getEndTime() + " before schedule start time " + scheduleStart + ", skipping task " + task.getTaskID());
                invalidTaskIDs.add(task.getTaskID());
                i ++;
                continue;
            }
            // Check if invalid duration
            if (task.getDuration() <= 0){
                System.out.println("Duration cannot be 0, skipping task " + task.getTaskID());
                invalidTaskIDs.add(task.getTaskID());
                i ++;
                continue;
            }

            // create new intermediate SolvingTask for current task
            SolvingTask currentTask = new SolvingTask(i, task, ctx, scheduleStart, scheduleEnd);
            // move onto next task
            solvingTaskList.add(currentTask);
            i ++;
        }

        // check if all tasks were invalid
        if (solvingTaskList.isEmpty()){
            System.out.println("All tasks were invalid. No schedule could be created.");
            return List.of();
        }

        // Add any repeat tasks that we need to add
        int v = 0;
        while (v < solvingTaskList.size()) {
            SolvingTask solvingTask = solvingTaskList.get(v);
            Task baseTask = tasks.get(solvingTask.taskIndex);
            List<TaskDependency> dependencies = baseTask.getDependencies();
            // No dependencies? go on
            if (dependencies == null || dependencies.isEmpty()) {
                v++;
                continue;
            }
            // For each dependency:
            for (int p = 0; p < dependencies.size(); p++) {
                // current dependency
                TaskDependency dependency = dependencies.get(p);
                int repeats = dependency.getRepeatCount();
                // Invalid dependencies can cause a cascading effect and
                // I honestly have no interest in propagating invalidation
                // up the tree so we will kill it here
                if (invalidTaskIDs.contains(dependency.getDependencyTaskID())) {
                    System.out.println("Dependency on invalid task found: aborting.");
                    return List.of();
                }

                int matchID = dependency.getDependencyTaskID();

                // Abort if self-referential dependency found
                if (solvingTask.taskID == matchID) {
                    System.out.println("Self-referential dependency found: aborting.");
                    return List.of();
                }

                // Create a list of tasks with the correct taskID at least repeats long
                List<SolvingTask> matchingTasks = new ArrayList<>();
                // Check all solving tasks for matching taskID
                for (SolvingTask matchingTask : solvingTaskList) {
                    if (matchingTask.taskID == matchID) {
                        matchingTasks.add(matchingTask);
                        if (matchingTasks.size() == repeats) {
                            break;
                        }
                    }
                }
                // out of solving tasks with matching taskID and we still need repeats:
                if (matchingTasks.size() < repeats) {
                    while (matchingTasks.size() < repeats) {
                        // add another solving task with the correct taskID
                        SolvingTask newTask = new SolvingTask(matchingTasks.getFirst().taskIndex, taskMap.get(matchID), ctx, scheduleStart, scheduleEnd);
                        solvingTaskList.add(newTask);
                        matchingTasks.add(newTask);
                    }
                }
            }
            v ++;
        }

        // Add dependencies for each task
        int n = 0;
        while (n < solvingTaskList.size()){
            SolvingTask solvingTask = solvingTaskList.get(n);
            Task baseTask = tasks.get(solvingTask.taskIndex);
            List<TaskDependency> dependencies = baseTask.getDependencies();
            // No dependencies? go on
            if (dependencies == null || dependencies.isEmpty()){
                n ++;
                continue;
            }
            // For each dependency:
            for (int p = 0; p < dependencies.size(); p++){
                // current dependency
                TaskDependency dependency = dependencies.get(p);
                int repeats = dependency.getRepeatCount();
                int matchID = dependency.getDependencyTaskID();

                // Create a list of tasks with the correct taskID at least repeats long
                List<SolvingTask> matchingTasks = new ArrayList<>();
                // Check all solving tasks for matching taskID
                for (SolvingTask matchingTask : solvingTaskList){
                    if (matchingTask.taskID == matchID){
                        matchingTasks.add(matchingTask);
                    }
                }

                // for each matching task:
                ArithExpr[] terms = new ArithExpr[matchingTasks.size()];
                for (int m = 0; m < matchingTasks.size(); m++){
                    // add a term
                    SolvingTask matchingTask = matchingTasks.get(m);
                    terms[m] = (ctx.mkIntConst("dp_" +  n + "_ "+ p + "_" + m));

                    // both tasks must be active
                    BoolExpr activeAndSatisfies = ctx.mkAnd(ctx.mkEq(matchingTask.active, ctx.mkTrue()), ctx.mkEq(solvingTask.active, ctx.mkTrue()));
                    BoolExpr then = ctx.mkEq(terms[m], ctx.mkInt(1));
                    BoolExpr elseStatement = ctx.mkEq(terms[m], ctx.mkInt(0));

                    // if dependency satisfied: corresponding term == 1
                    switch (dependency.getType()){
                        case LOOSELY_AFTER:
                            // START TIME OF DEPENDENT >= END TIME OF DEPENDENCY
                            activeAndSatisfies = ctx.mkAnd(activeAndSatisfies, ctx.mkAnd(ctx.mkGe(solvingTask.taskStart, matchingTask.taskEnd)));
                            break;

                        case IMMEDIATELY_AFTER:
                            // START TIME OF DEPENDENT == END TIME OF DEPENDENCY
                            activeAndSatisfies = ctx.mkAnd(activeAndSatisfies, ctx.mkAnd(ctx.mkEq(solvingTask.taskStart, matchingTask.taskEnd)));
                            break;

                        case LOOSELY_BEFORE:
                            // END TIME OF DEPENDENT =< START TIME OF DEPENDENCY
                            activeAndSatisfies = ctx.mkAnd(activeAndSatisfies, ctx.mkAnd(ctx.mkLe(solvingTask.taskEnd, matchingTask.taskStart)));
                            break;

                        case IMMEDIATELY_BEFORE:
                            // END TIME OF DEPENDENT == START TIME OF DEPENDENCY
                            activeAndSatisfies = ctx.mkAnd(activeAndSatisfies, ctx.mkAnd(ctx.mkEq(solvingTask.taskEnd, matchingTask.taskStart)));
                            break;
                    }
                    Expr ifActive = ctx.mkITE(activeAndSatisfies, then, elseStatement);
                    solvingTaskList.get(n).taskConstraints = ctx.mkAnd(solvingTask.taskConstraints, ifActive);
                }
                solvingTask.dependencyListTerms.add(List.of(terms));
                // The sum of tasks that satisfy the dependency should be >= to the repeat count
                BoolExpr isActive = ctx.mkEq(solvingTask.active, ctx.mkTrue());
                BoolExpr repeatsMet = ctx.mkGe(ctx.mkAdd(terms), ctx.mkInt(repeats));
                Expr ifActive = ctx.mkITE(isActive, repeatsMet, ctx.mkTrue());
                solvingTaskList.get(n).taskConstraints = ctx.mkAnd(solvingTask.taskConstraints, ifActive);
            }
            n ++;
        }

        // Prevent tasks from overlapping
        // iterate through each list of task variables
        for (int j = 0; j < solvingTaskList.size(); j++) {
            // check them against all the other lists of task variables
            for (int k = 0; k < solvingTaskList.size(); k++) {
//                System.out.println(j + " vs " + k);
                // Only set constraints if not against self
                if (j != k){
                    IntExpr startA = solvingTaskList.get(j).taskStart;
                    IntExpr endA = solvingTaskList.get(j).taskEnd;
                    IntExpr startB = solvingTaskList.get(k).taskStart;
                    IntExpr endB = solvingTaskList.get(k).taskEnd;
                    // startA < startB and endA <= startB
                    BoolExpr ABeforeB = ctx.mkAnd(ctx.mkLt(startA, startB), ctx.mkLe(endA, startB));
                    // startB < startA and endB <= startA
                    BoolExpr BBeforeA = ctx.mkAnd(ctx.mkLt(startB, startA), ctx.mkLe(endB, startA));
                    // Hence task A must be before task B or task B must be before task A if both are active
                    BoolExpr ABeforeOrBBefore = ctx.mkOr(ABeforeB, BBeforeA);
                    BoolExpr bothActive = ctx.mkAnd(ctx.mkEq(solvingTaskList.get(j).active, ctx.mkTrue()), ctx.mkEq(solvingTaskList.get(k).active, ctx.mkTrue()));
                    Expr ifActive = ctx.mkITE(bothActive, ABeforeOrBBefore, ctx.mkTrue());
                    // Add to taskA's constraints
                    solvingTaskList.get(j).taskConstraints = ctx.mkAnd(solvingTaskList.get(j).taskConstraints, ifActive);
                }
            }
        }

        // Add all task constraints
        for (SolvingTask solvingTask : solvingTaskList){
            s.add(solvingTask.taskConstraints);
        }

        // Generate all possible schedules if maxSolutions == 0
        // else generate only maxSolutions amount of solutions
        int k = 0;
        while ((k < maxSolutions)|| (maxSolutions == 0)){
//            Status status = s.check();
//            System.out.println("SOLVER STATUS: " + status);
            if (s.check() == Status.SATISFIABLE){
                Model m = s.getModel();
                BoolExpr solution_c = ctx.mkTrue();
                List<SolvedTask> solvedTaskList = new ArrayList<>();
                for (int j = 0; j < solvingTaskList.size(); j++) {
                    // get solution for this task
                    SolvingTask currentSolving = solvingTaskList.get(j);
                    Expr active = m.evaluate(currentSolving.active, true);
                    boolean isActive = active.isTrue();
//                    System.out.println("Is active: "+ isActive);
                    IntNum tStart = (IntNum) m.evaluate(currentSolving.taskStart, true);
                    long tStartLong = Long.parseLong(tStart.toString());
                    IntNum tEnd = (IntNum) m.evaluate(currentSolving.taskEnd, true);
                    long tEndLong = Long.parseLong(tEnd.toString());

                    // Only add this task as solved if it's active
                    if (isActive){
                        // init new solved task
                        SolvedTask newSolvedTask = new SolvedTask(tasks.get(currentSolving.taskIndex), tStartLong, tEndLong);
                        //System.out.println("active: " + isActive + " start time: " + tStartLong + " end time: " + tEndLong);
                        // add new solved task
                        solvedTaskList.add(newSolvedTask);
                    }

                    // add this solution to our new constraint
                    BoolExpr currentActive = ctx.mkEq(currentSolving.active, ctx.mkBool(isActive));
                    BoolExpr currentTStart = ctx.mkEq(currentSolving.taskStart, ctx.mkInt(tStartLong));
                    BoolExpr currentTEnd = ctx.mkEq(currentSolving.taskEnd, ctx.mkInt(tEndLong));

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
