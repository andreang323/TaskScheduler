package Tasks;

public class TaskDependency {
    public enum DependencyType{
        IMMEDIATELY_AFTER,
        IMMEDIATELY_BEFORE,
        LOOSELY_BEFORE,
        LOOSELY_AFTER
    }

    private int DependencyTaskID;
    private DependencyType type;

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
}
