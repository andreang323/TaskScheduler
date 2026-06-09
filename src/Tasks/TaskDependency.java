package Tasks;

public class TaskDependency {
    private String dependencyTaskName;
    private DependencyType type;

    public TaskDependency(String dependencyTaskName, DependencyType type, int repeatsRequired, boolean optional) {
        this.dependencyTaskName = dependencyTaskName;
        this.type = type;
    }

    public String getDependencyTaskName() {
        return dependencyTaskName;
    }

    public DependencyType getType() {
        return type;
    }
}