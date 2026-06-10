package SchedulerUI;

import Tasks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.microsoft.z3.*;

public class ScheduleSolver {
    public List<Schedule> GenerateSchedules(List<Task> tasks, long scheduleStart, long scheduleEnd) {
        List<Schedule> schedules = new ArrayList<>();

        // Generate all possible schedules
        // While sat:
        // Add dependencies
        // For each task:
            // Super basic requirements
                // start time >= scheduleStart
                // end time <= scheduleEnd
                // end time > start time
                // end time - start time = duration
            // Also basic but slightly less basic:
                // Tasks can't overlap!
                // Tasks can be optional
            // Not basic:
                // Dependencies must be fulfilled
        // Solve using Z3
        // Take Z3 solution and turn it into a schedule
        // Run solvedTask.calculateScore(priority, desiredStart, desiredEnd, desiredDuration)
        // in order to get all solved tasks to have the correct score
        // Run schedule.updatePriorityScore() so the schedule knows its correct priority score

        // Sort in order of highest priority first
        schedules.sort(Collections.reverseOrder());
        // Return our sorted schedule
        return schedules;
    }
}
