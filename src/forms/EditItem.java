package forms;

import Tasks.Task;
import Tasks.TaskDependency;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.Instant;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
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
    private JRadioButton lockRadioButton;
    private JRadioButton lockRadioButton1;
    private JRadioButton lockRadioButton2;
    private JTextField Priority;
    private JList<String> isDependedOn;
    private JList<String> mustImmediatelyFollow;
    private JList<String> mustImmediatelyPrecede;
    private JList<String> mustPrecede;
    private JList<String> mustFollow;
    private JList<String> dependsOn;
    private JButton validateButton;
    private JCheckBox Optional;
    private JCheckBox skipDependentTaskIfCheckBox;
    private JCheckBox reduceDurationOfThisCheckBox;
    private JTextField textField1;
    private JLabel timeNote1;
    private JLabel timeNote2;
    private JEditorPane description;
    private TimeLockListener timeLockListener;
    private SaveButtonListener saveButtonListener;
    private Task task;
    private LocalDateTime date = LocalDateTime.now();
    private boolean editing;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<Task> tasks;

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").optionalStart().appendLiteral('T').optionalStart().appendPattern("HH:mm").appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().toFormatter();

    public void setupPanel() {

        // Update displayed parameters with the task assigned to EditItem
        if (task != null) {
            Name.setText(task.getName());
            StartTime.setText(
                    Instant.ofEpochSecond(task.getStartTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(DATE_FORMAT)
            );

            EndTime.setText(
                    Instant.ofEpochSecond(task.getEndTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(DATE_FORMAT)
            );
            // StartTime.setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(task.getStartTime()), java.time.ZoneId.systemDefault()).format(FORMATTER));
            // EndTime.setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(task.getEndTime()), java.time.ZoneId.systemDefault()).format(FORMATTER));
            // StartTime.setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(task.getStartTime()), ZoneId.systemDefault()).format(FORMATTER));
            // EndTime.setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(task.getEndTime()), ZoneId.systemDefault()).format(FORMATTER));
            Duration.setText(String.valueOf(task.getDuration()));
            lockRadioButton.setSelected(task.isLockStartTime());
            lockRadioButton2.setSelected(task.isLockEndTime());
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
    }

    public void handleStartTimeChange() {
        // StartTime changed
        System.out.println("StartTime Changed");
        if (StartTime.getText().isEmpty()) {
            System.out.println("Invalid Start Time Format: " + StartTime.getText());
            return;
        }
        LocalDateTime startDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER);
        try {
            startDate = LocalDateTime.parse(StartTime.getText(), FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Start Time Format: " + StartTime.getText());
            return;
        }
        LocalDateTime endDate = startDate.plusHours(1);
        if (!EndTime.getText().isEmpty()) {
            endDate = LocalDateTime.parse(EndTime.getText(), FORMATTER);
        }
        LocalDateTime durationAsDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER);
        if (!Duration.getText().isEmpty()) {
            durationAsDate = LocalDateTime.parse(Duration.getText(), FORMATTER);
        }

        if (lockRadioButton.isSelected()) {
            // StartTime locked, treat duration as driven
            Duration.setText(getDurationString(startDate, endDate));
        } else if (lockRadioButton1.isSelected()) {
            // endtime locked, treat duration as driven
            Duration.setText(getDurationString(startDate, endDate));
        } else if (lockRadioButton2.isSelected()) {
            // duration locked, treat end time as driven
            long durationSeconds = startDate.until(endDate, ChronoUnit.SECONDS);
            LocalDateTime endTime = startDate.minusSeconds(durationSeconds);
            EndTime.setText(endTime.format(FORMATTER));
        }
    }

    public void handleEndTimeChange() {
        // End time changed
        System.out.println("EndTime Changed");

        if (EndTime.getText().isEmpty()) {
            System.out.println("Invalid Ends Time Format: " + EndTime.getText());
            return;
        }
        LocalDateTime endDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER);
        try {
            endDate = LocalDateTime.parse(EndTime.getText(), FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Ends Time Format: " + EndTime.getText());
            return;
        }

        LocalDateTime startDate = endDate.minusHours(1);
        if (!StartTime.getText().isEmpty()) {
            startDate = LocalDateTime.parse(StartTime.getText(), FORMATTER);
        }
        LocalDateTime durationAsDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER);
        if (!Duration.getText().isEmpty()) {
            durationAsDate = LocalDateTime.parse(Duration.getText(), FORMATTER);
        }

        if (lockRadioButton.isSelected()) {
            // StartTime locked, treat duration as driven
            Duration.setText(getDurationString(startDate, endDate));
        } else if (lockRadioButton1.isSelected()) {
            // endtime locked, treat duration as driven
            Duration.setText(getDurationString(startDate, endDate));
        } else if (lockRadioButton2.isSelected()) {
            // duration is locked, treat start time as driven
            long durationSeconds = startDate.until(endDate, ChronoUnit.SECONDS);
            LocalDateTime startTime = endDate.plusSeconds(durationSeconds);
            EndTime.setText(startTime.format(FORMATTER));
        }
    }

    public void handleDurationChange() {
        // Duration changed
        System.out.println("Duration Changed");

        if (Duration.getText().isEmpty()) {
            System.out.println("Invalid Duration Time Format: " + Duration.getText());
            return;
        }
        LocalDateTime durationAsDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER); // FIXME! Don't want it to be 2000 years lol
        try {
            durationAsDate = LocalDateTime.parse(Duration.getText(), FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Duration Time Format: " + Duration.getText());
            return;
        }
        long durationSeconds = Math.abs(durationAsDate.until(LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER), ChronoUnit.SECONDS));

        LocalDateTime startDate = LocalDateTime.parse("2000-01-01T00:00:00", FORMATTER);
        if (!StartTime.getText().isEmpty()) {
            startDate = LocalDateTime.parse(StartTime.getText(), FORMATTER);

        }
        LocalDateTime endDate = startDate.plusSeconds(durationSeconds);
        if (!EndTime.getText().isEmpty()) {
            endDate = LocalDateTime.parse(EndTime.getText(), FORMATTER);
        }

        if (lockRadioButton.isSelected()) {
            // StartTime locked, treat endTime as driven
            LocalDateTime endTime = startDate.plusSeconds(durationSeconds);
            EndTime.setText(endTime.format(FORMATTER));
        } else if (lockRadioButton1.isSelected()) {
            // endtime locked, treat startTime as driven
            LocalDateTime startTime = endDate.plusSeconds(durationSeconds);
            StartTime.setText(startTime.format(FORMATTER));
        } else if (lockRadioButton2.isSelected()) {
            // duration is locked, treat endTime as driven
            LocalDateTime endTime = startDate.plusSeconds(durationSeconds);
            EndTime.setText(endTime.format(FORMATTER));
        }
    }

    private static String getDurationString(LocalDateTime startDate, LocalDateTime endDate) {
        long durationYears = startDate.until(endDate, ChronoUnit.YEARS);
        long durationMonths = startDate.until(endDate, ChronoUnit.MONTHS) - (durationYears * 12);
        long durationDays = startDate.until(endDate, ChronoUnit.DAYS) - (durationMonths * 30);
        long durationHours = startDate.until(endDate, ChronoUnit.HOURS) - (durationDays * 24);
        long durationMinutes = startDate.until(endDate, ChronoUnit.MINUTES) - (durationHours * 60);
        long durationSeconds = startDate.until(endDate, ChronoUnit.SECONDS) - (durationMinutes * 60);

        String durationString = "";
        if (durationYears < 0) {
            durationString += "0000-";
        } else if (durationYears < 10) {
            durationString += "000" + durationYears + "-";
        } else if (durationYears < 100) {
            durationString += "00" + durationYears + "-";
        } else if (durationYears < 1000) {
            durationString += "0" + durationYears + "-";
        }

        if (durationMonths < 0) {
            durationString += "00-";
        } else if (durationMonths < 10) {
            durationString += "0" + durationMonths + "-";
        } else {
            durationString += durationMonths + "-";
        }

        if (durationDays < 0) {
            durationString += "00 ";
        } else if (durationDays < 10) {
            durationString += "0" + durationDays + " ";
        } else {
            durationString += durationDays + " ";
        }

        if (durationHours < 0) {
            durationString += "00:";
        } else if (durationHours < 10) {
            durationString += "0" + durationHours + ":";
        } else {
            durationString += durationHours + ":";
        }

        if (durationMinutes < 0) {
            durationString += "00:";
        } else if (durationMinutes < 10) {
            durationString += "0" + durationMinutes + ":";
        } else {
            durationString += durationMinutes + ":";
        }

        if (durationSeconds < 0) {
            durationString += "00:";
        } else if (durationSeconds < 10) {
            durationString += "0" + durationSeconds + ":";
        } else {
            durationString += durationSeconds;
        }

        return durationString;
    }

    public EditItem(String newTitle, Task newTask, boolean editing, List<Task> tasks) {
        this.task = newTask;

        setTitle(newTitle);
        if (editing) {
            createButton.setText("Update");
        } else {
            createButton.setText("Create");
            long now = Instant.now().getEpochSecond();

            if (task.getStartTime() == 0) {
                task.setStartTime(now);
            }

            if (task.getEndTime() == 0) {
                task.setEndTime(now + 3600);
            }
        }

        this.tasks = tasks;
        String[] taskNames = new String[tasks.size() + 1];
        taskNames[0] = "[None]";

        for (int i = 0; i < tasks.size(); i++) {
            taskNames[i + 1] = tasks.get(i).getName();
        }

        isDependedOn.setListData(taskNames);
        mustImmediatelyFollow.setListData(taskNames);
        mustImmediatelyPrecede.setListData(taskNames);
        mustPrecede.setListData(taskNames);
        mustFollow.setListData(taskNames);
        dependsOn.setListData(taskNames);

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
                        .toEpochSecond()
        );

        task.setEndTime(
                LocalDateTime.parse(
                                EndTime.getText(),
                                DATE_FORMAT
                        )
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond()
        );
        // task.setStartTime(Instant.parse(StartTime.getText()).getEpochSecond()); // FIXME! Form validation does not expect timezone Z but required here
        // task.setEndTime(Instant.parse(EndTime.getText()).getEpochSecond());
        task.setDuration(Long.parseLong(Duration.getText()));
        task.setLockStartTime(lockRadioButton.isSelected());
        task.setLockEndTime(lockRadioButton2.isSelected());
        task.setPriority(Integer.parseInt(Priority.getText()));
        task.setOptional(Optional.isSelected());

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
            // Iterate over depednacy types
            for (int i = 0; i < dependencyLists.size(); i++) {
                // Iterate over dependencies of each type
                for (String name : dependencyLists.get(i)) {
                    if (name.equals("[NONE")) {
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

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }

}
