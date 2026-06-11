package forms;

import Tasks.Task;
import Tasks.TaskDependency;
import jdk.jfr.Description;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class EditItem extends JFrame {
    private JPanel contentPane;
    private JTextField Name;
    private JButton createButton;
    private JButton cancelButton;
    private JTextField StartTime;
    private JTextField EndTime;
    private JTextField Duration;

//    private ButtonGroup lockButtonGroup;
//    private JRadioButton lockRadioButton;
//    private JRadioButton lockRadioButton1;
//    private JRadioButton lockRadioButton2;

    private JTextField Priority;
    private JList<String> isDependedOn;
    private JList<String> mustImmediatelyFollow;
    private JList<String> mustImmediatelyPrecede;
    private JList<String> mustPrecede;
    private JList<String> mustFollow;
    private JList<String> dependsOn;
    private JCheckBox Optional;
    private JLabel timeNote1;
    private JLabel timeNote2;
    private JEditorPane description;
    private TimeLockListener timeLockListener;
    private SaveButtonListener saveButtonListener;
    private Task task;
    private LocalDateTime date = LocalDateTime.now();
    private boolean editing;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<Task> tasks;

    public void setupPanel() {

        // Update displayed parameters with the task assigned to EditItem
        if (task != null) {
            Name.setText(task.getName());
            StartTime.setText(
                    Instant.ofEpochSecond(task.getStartTime() * 60)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(DATE_FORMAT)
            );

            EndTime.setText(
                    Instant.ofEpochSecond(task.getEndTime() * 60)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(DATE_FORMAT)
            );

            Duration.setText(String.valueOf(task.getDuration()));
            description.setText(task.getDescription());
//            lockRadioButton.setSelected(task.isLockStartTime());
//            lockRadioButton2.setSelected(task.isLockEndTime());
            Priority.setText(String.valueOf(task.getPriority()));
            Optional.setSelected(task.isOptional());
        }

        // Save button event listener
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        // Cancel button event listener
        // Closes window after being pressed
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        StartTime.addActionListener(e -> handleStartTimeChange());
        StartTime.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                handleStartTimeChange();
            }
        });

        EndTime.addActionListener(e -> handleEndTimeChange());
        EndTime.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                handleEndTimeChange();
            }
        });

        Duration.addActionListener(e -> handleDurationChange());
        Duration.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                handleDurationChange();
            }
        });

//        lockButtonGroup = new ButtonGroup();
//        lockButtonGroup.add(lockRadioButton);
//        lockButtonGroup.add(lockRadioButton1);
//        lockButtonGroup.add(lockRadioButton2);

        if (!editing) {
            lockButtonGroup.clearSelection();

            Priority.setText(String.valueOf(1));

        }

    }

    public void handleStartTimeChange() {
        // StartTime changed
        System.out.println("StartTime Changed");
        if (StartTime.getText().isEmpty()) {
            System.out.println("Invalid Start Time Format: " + StartTime.getText());
            return;
        }
        LocalDateTime startDate = null;
        try {
            startDate = LocalDateTime.parse(StartTime.getText(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Start Time Format: " + StartTime.getText());
            return;
        }
        LocalDateTime endDate = startDate.plusHours(1);
        if (!EndTime.getText().isEmpty()) {
            endDate = LocalDateTime.parse(EndTime.getText(), DATE_FORMAT);
        }

//        if (lockRadioButton.isSelected()) {
//            // StartTime locked, treat duration as driven
//            Duration.setText(getDurationString(startDate, endDate));
//        } else if (lockRadioButton1.isSelected()) {
//            // endtime locked, treat duration as driven
//            Duration.setText(getDurationString(startDate, endDate));
//        } else if (lockRadioButton2.isSelected()) {
//            // duration locked, treat end time as driven
//            if (!Duration.getText().isEmpty()) {
//                long durationMinutes = Long.parseLong(Duration.getText());
//                LocalDateTime endTime = startDate.plusMinutes(durationMinutes);
//                EndTime.setText(endTime.format(DATE_FORMAT));
//            }
//
//        }
    }

    public void handleEndTimeChange() {
        // End time changed
        System.out.println("EndTime Changed");

        if (EndTime.getText().isEmpty()) {
            System.out.println("Invalid Ends Time Format: " + EndTime.getText());
            return;
        }
        LocalDateTime endDate = null;
        try {
            endDate = LocalDateTime.parse(EndTime.getText(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Ends Time Format: " + EndTime.getText());
            return;
        }

        LocalDateTime startDate = endDate.minusHours(1);
        if (!StartTime.getText().isEmpty()) {
            startDate = LocalDateTime.parse(StartTime.getText(), DATE_FORMAT);
        }

//        if (lockRadioButton.isSelected()) {
//            // StartTime locked, treat duration as driven
//            Duration.setText(getDurationString(startDate, endDate));
//        } else if (lockRadioButton1.isSelected()) {
//            // endtime locked, treat duration as driven
//            Duration.setText(getDurationString(startDate, endDate));
//        } else if (lockRadioButton2.isSelected()) {
//            if (!Duration.getText().isEmpty()) {
//                long durationMinutes = Long.parseLong(Duration.getText());
//                LocalDateTime startTime = endDate.minusMinutes(durationMinutes);
//                StartTime.setText(startTime.format(DATE_FORMAT));
//            }
//        }
    }

    public void handleDurationChange() {
        // Duration changed
        System.out.println("Duration Changed");

        if (Duration.getText().isEmpty()) {
            System.out.println("Invalid Duration Time Format: " + Duration.getText());
            return;
        }
        long durationMinutes;

        try {
            durationMinutes = Long.parseLong(Duration.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Duration Format: " + Duration.getText());
            return;
        }

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        try {
            if (!StartTime.getText().isEmpty()) {
                startDate = LocalDateTime.parse(StartTime.getText(), DATE_FORMAT);
            }

            if (!EndTime.getText().isEmpty()) {
                endDate = LocalDateTime.parse(EndTime.getText(), DATE_FORMAT);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Start/End Time Format");
            return;
        }


//        if (lockRadioButton.isSelected()) {
//            // StartTime locked, treat endTime as driven
//            if (startDate != null) {
//                LocalDateTime endTime = startDate.plusMinutes(durationMinutes);
//                EndTime.setText(endTime.format(DATE_FORMAT));
//            }
//        } else if (lockRadioButton1.isSelected()) {
//            // endtime locked, treat startTime as driven
//            if (endDate != null) {
//                LocalDateTime startTime = endDate.minusMinutes(durationMinutes);
//                StartTime.setText(startTime.format(DATE_FORMAT));
//            }
//        } else if (lockRadioButton2.isSelected()) {
//            // duration is locked, treat endTime as driven
//            if (startDate != null) {
//                LocalDateTime startTime = startDate.plusMinutes(durationMinutes);
//                EndTime.setText(startTime.format(DATE_FORMAT));
//            }
//        }
    }

    private static String getDurationString(LocalDateTime startDate, LocalDateTime endDate) {
        long minutes = startDate.until(endDate, ChronoUnit.MINUTES);
        return String.valueOf(minutes);
    }

    private int getTaskListIndexForDependency(TaskDependency dependency) {
        if (dependency == null || tasks == null) {
            return -1;
        }

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskID() == dependency.getDependencyTaskID()) {
                return i + 1; // +1 because taskNames[0] is [None]
            }
        }

        return -1;
    }

    private void selectDependencyValues(JList<String> list, TaskDependency.DependencyType type) {
        if (task == null || task.getDependencies() == null) {
            return;
        }

        List<Integer> selectedIndices = new ArrayList<>();

        for (TaskDependency dependency : task.getDependencies()) {
            if (dependency.getType() == type) {
                int index = getTaskListIndexForDependency(dependency);
                if (index >= 0) {
                    selectedIndices.add(index);
                }
            }
        }

        if (!selectedIndices.isEmpty()) {
            int[] indices = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
            list.setSelectedIndices(indices);
        }
    }

    private void restoreDependencySelections() {
        mustImmediatelyFollow.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mustImmediatelyPrecede.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mustPrecede.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mustFollow.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        selectDependencyValues(mustImmediatelyFollow, TaskDependency.DependencyType.IMMEDIATELY_AFTER);
        selectDependencyValues(mustImmediatelyPrecede, TaskDependency.DependencyType.IMMEDIATELY_BEFORE);
        selectDependencyValues(mustPrecede, TaskDependency.DependencyType.LOOSELY_BEFORE);
        selectDependencyValues(mustFollow, TaskDependency.DependencyType.LOOSELY_AFTER);
    }

    public EditItem(String newTitle, Task newTask, boolean editing, List<Task> tasks) {
        this.task = newTask;
        this.editing = editing;

        setTitle(newTitle);
        if (editing) {
            createButton.setText("Update");
        } else {
            createButton.setText("Create");

            long now = Instant.now().getEpochSecond() / 60;

            if (task.getStartTime() == 0) {
                task.setStartTime(now);
            }

            if (task.getEndTime() == 0) {
                task.setEndTime(now + 10080);
            }
        }

        this.tasks = tasks;
        String[] taskNames = new String[tasks.size() + 1];
        taskNames[0] = "[None]";

        for (int i = 0; i < tasks.size(); i++) {
            taskNames[i + 1] = tasks.get(i).getName();
        }

        // Populate depeandcy lists with data
//        isDependedOn.setListData(taskNames);
        mustImmediatelyFollow.setListData(taskNames);
        mustImmediatelyPrecede.setListData(taskNames);
        mustPrecede.setListData(taskNames);
        mustFollow.setListData(taskNames);
//        dependsOn.setListData(taskNames);

        restoreDependencySelections();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(contentPane);
        pack();

        setupPanel();

//        setSize(300, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setSaveButtonListener(SaveButtonListener listener) {
        this.saveButtonListener = listener;
    }


    private void saveChanges() {
        System.out.println("Saving changes.");

        // Update the task object that we have
        task.setName(Name.getText());

        task.setStartTime(
                LocalDateTime.parse(
                                StartTime.getText(),
                                DATE_FORMAT
                        )
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond() / 60
        );

        task.setEndTime(
                LocalDateTime.parse(
                                EndTime.getText(),
                                DATE_FORMAT
                        )
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond() / 60
        );

        task.setDuration(Long.parseLong(Duration.getText()));
//        task.setLockStartTime(lockRadioButton.isSelected());
//        task.setLockEndTime(lockRadioButton2.isSelected());
        task.setPriority(Integer.parseInt(Priority.getText()));
        task.setOptional(Optional.isSelected());
        task.setDescription(description.getText());

        // Process dependencies
        List<List<String>> dependencyLists = List.of(
//                isDependedOn.getSelectedValuesList(),
                mustImmediatelyFollow.getSelectedValuesList(),
                mustImmediatelyPrecede.getSelectedValuesList(),
                mustPrecede.getSelectedValuesList(),
                mustFollow.getSelectedValuesList() //,
//                dependsOn.getSelectedValuesList()
        );
        List<TaskDependency.DependencyType> dependencyTypes = List.of(
                TaskDependency.DependencyType.IMMEDIATELY_AFTER,
                TaskDependency.DependencyType.IMMEDIATELY_BEFORE,
                TaskDependency.DependencyType.LOOSELY_BEFORE,
                TaskDependency.DependencyType.LOOSELY_AFTER
        );

        // Iterate over tasks to check
        List<TaskDependency> dependencies = new ArrayList<TaskDependency>();
        for (Task task : tasks) {
            // Iterate over dependency types
            System.out.println("Inspecting task relationships:" + task.getName());
            for (int i = 0; i < dependencyLists.size(); i++) {
                // Iterate over dependencies of each type
                System.out.println("  Inspecting dependency type: " + dependencyLists.get(i));
                for (String name : dependencyLists.get(i)) {
                    // Iterate over dependency names of a specific type
                    System.out.println("    Inspecting selected dependency: " + name);
                    if (name.equals("[NONE]")) {
                        break;
                    } else if (name.equals(task.getName())) {
                        TaskDependency dependency = new TaskDependency();
                        dependency.setDependencyTaskID(task.getTaskID());
                        dependency.setType(dependencyTypes.get(i));
                        dependencies.add(dependency);
                    }
                }
            }
        }
        this.task.setDependencies(dependencies);
        System.out.println("Final dependency list: " + this.task.getDependencies());

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }

}
