package Tasks;

public class TaskDependency {
    public enum DependencyType {
        IMMEDIATELY_AFTER,
        IMMEDIATELY_BEFORE,
        LOOSELY_BEFORE,
        LOOSELY_AFTER
    }

    private int DependencyTaskID;
    private DependencyType type;
    private int RepeatCount = 1;

    public int getDependencyTaskID() {
        return DependencyTaskID;
    }

    public void setDependencyTaskID(int dependencyTaskID) {
        DependencyTaskID = dependencyTaskID;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public int getRepeatCount() {
        return RepeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        RepeatCount = repeatCount;
    }
}
