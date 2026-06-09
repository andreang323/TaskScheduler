package Tasks;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;

    private int startTimeSeconds;
    private int endTimeSeconds;
    private int durationSeconds;
    private int priority;

    private boolean optional;
    private boolean restrictSchedulingToAllocatedTime;
    private boolean skipDependentTaskIfUnfulfilled;
    private boolean reduceDurationIfDependencyFulfilled;

    private int durationReductionSeconds;

    private boolean lockStartTime;
    private boolean lockEndTime;
    private boolean lockDuration;

    private String group;

    private List<TaskDependency> dependencies;

    public Task() {
        this.dependencies = new ArrayList<>();
    }

    public Task(
            String name,
            int startTimeSeconds,
            int endTimeSeconds,
            int durationSeconds,
            int priority,
            boolean optional,
            boolean restrictSchedulingToAllocatedTime,
            boolean skipDependentTaskIfUnfulfilled,
            boolean reduceDurationIfDependencyFulfilled,
            int durationReductionSeconds,
            boolean lockStartTime,
            boolean lockEndTime,
            boolean lockDuration,
            String group,
            List<TaskDependency> dependencies
    ) {
        this.name = name;
        this.startTimeSeconds = startTimeSeconds;
        this.endTimeSeconds = endTimeSeconds;
        this.durationSeconds = durationSeconds;
        this.priority = priority;
        this.optional = optional;
        this.restrictSchedulingToAllocatedTime = restrictSchedulingToAllocatedTime;
        this.skipDependentTaskIfUnfulfilled = skipDependentTaskIfUnfulfilled;
        this.reduceDurationIfDependencyFulfilled = reduceDurationIfDependencyFulfilled;
        this.durationReductionSeconds = durationReductionSeconds;
        this.lockStartTime = lockStartTime;
        this.lockEndTime = lockEndTime;
        this.lockDuration = lockDuration;
        this.group = group;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public int getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isRestrictSchedulingToAllocatedTime() {
        return restrictSchedulingToAllocatedTime;
    }

    public boolean isSkipDependentTaskIfUnfulfilled() {
        return skipDependentTaskIfUnfulfilled;
    }

    public boolean isReduceDurationIfDependencyFulfilled() {
        return reduceDurationIfDependencyFulfilled;
    }

    public int getDurationReductionSeconds() {
        return durationReductionSeconds;
    }

    public boolean isLockStartTime() {
        return lockStartTime;
    }

    public boolean isLockEndTime() {
        return lockEndTime;
    }

    public boolean isLockDuration() {
        return lockDuration;
    }

    public String getGroup() {
        return group;
    }

    public List<TaskDependency> getDependencies() {
        return dependencies;
    }

    private List<TaskDependency> getDependenciesOfType(DependencyType type) {
        return dependencies.stream()
                .filter(dep -> dep.getType() == type)
                .toList();
    }

    public List<TaskDependency> getImmediatelyFollows() {
        return getDependenciesOfType(DependencyType.IMMEDIATELY_AFTER);
    }


    public List<TaskDependency> getImmediatelyPrecede() {
        return getDependenciesOfType(DependencyType.IMMEDIATELY_BEFORE);
    }


    public List<TaskDependency> getFollows() {
        return getDependenciesOfType(DependencyType.LOOSELY_AFTER);
    }


    public List<TaskDependency> getPrecede() {
        return getDependenciesOfType(DependencyType.LOOSELY_BEFORE);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setStartTimeSeconds(int startTimeSeconds) {
        this.startTimeSeconds = startTimeSeconds;
    }

    public void setEndTimeSeconds(int endTimeSeconds) {
        this.endTimeSeconds = endTimeSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setRestrictSchedulingToAllocatedTime(boolean restrictSchedulingToAllocatedTime) {
        this.restrictSchedulingToAllocatedTime = restrictSchedulingToAllocatedTime;
    }

    public void setSkipDependentTaskIfUnfulfilled(boolean skipDependentTaskIfUnfulfilled) {
        this.skipDependentTaskIfUnfulfilled = skipDependentTaskIfUnfulfilled;
    }

    public void setReduceDurationIfDependencyFulfilled(boolean reduceDurationIfDependencyFulfilled) {
        this.reduceDurationIfDependencyFulfilled = reduceDurationIfDependencyFulfilled;
    }

    public void setDurationReductionSeconds(int durationReductionSeconds) {
        this.durationReductionSeconds = durationReductionSeconds;
    }

    public void setLockStartTime(boolean lockStartTime) {
        this.lockStartTime = lockStartTime;
    }

    public void setLockEndTime(boolean lockEndTime) {
        this.lockEndTime = lockEndTime;
    }

    public void setLockDuration(boolean lockDuration) {
        this.lockDuration = lockDuration;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDependencies(List<TaskDependency> dependencies) {
        this.dependencies = dependencies;
    }
}