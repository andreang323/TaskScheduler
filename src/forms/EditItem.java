package forms;

import Tasks.Schedule;
import Tasks.SolvedTask;
import Tasks.Task;
import Tasks.TaskDependency;

import java.awt.*;
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
import java.util.Collections;
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

    private JTextField Priority;
    private JList<String> isDependedOn;
    private JList<String> mustImmediatelyFollow;
    private JList<String> mustImmediatelyPrecede;
    private JList<String> mustPrecede;
    private JList<String> mustFollow;
    private JList<String> dependsOn;
    private JCheckBox Optional;
    private JLabel timeNote1;
    private JEditorPane description;
    private JScrollPane dependencyScrollPane;
    private JLabel dependenciesSectionLabel;
    private JButton addDependencyButton;
    private TimeLockListener timeLockListener;
    private SaveButtonListener saveButtonListener;
    private Task task;
    private LocalDateTime date = LocalDateTime.now();
    private boolean editing;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<Task> tasks;
    private List<TaskDependency> dependencies;

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
            dependencies = task.getDependencies();
            refreshDependencyList();
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

        addDependencyButton.addActionListener(e -> {
            TaskDependency newDependency = new TaskDependency();
            newDependency.setType(TaskDependency.DependencyType.IMMEDIATELY_AFTER);
            newDependency.setRepeatCount(1);

            EditDependency editDependency =
                    new EditDependency(
                            tasks,
                            newDependency,
                            "New Dependency",
                            task.getName()
                    );

            editDependency.setDependencySaveButtonListener(dependency -> {
                if (task.getDependencies() == null) {
                    task.setDependencies(new ArrayList<>());
                }

                for (TaskDependency existing : task.getDependencies()) {
                    if (existing.getDependencyTaskID() == dependency.getDependencyTaskID()
                            && existing.getType() == dependency.getType()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "This dependency already exists.",
                                "Duplicate Dependency",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                }

                dependencies.add(dependency);

                System.out.println("Added dependency: " + dependency);
                refreshDependencyDisplay();
            });
        });

        if (!editing) {
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
    }

    private void refreshDependencyDisplay() {
        dependencyScrollPane.removeAll();

//        for (TaskDependency dependency : task.getDependencies()) {
//            dependencyScrollPane.add(
//                    createDependencyPanel(dependency)
//            );
//        }

        dependencyScrollPane.revalidate();
        dependencyScrollPane.repaint();
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
    }

    private static String getDurationString(LocalDateTime startDate, LocalDateTime endDate) {
        long minutes = startDate.until(endDate, ChronoUnit.MINUTES);
        return String.valueOf(minutes);
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
        task.setPriority(Integer.parseInt(Priority.getText()));
        task.setOptional(Optional.isSelected());
        task.setDescription(description.getText());

        task.setDependencies(dependencies);
        System.out.println("Final dependency list: " + this.task.getDependencies());

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }


    // Create a new dependency panel with dependency description,
    // edit button, and delete button
    public JPanel createDependencyPanel(TaskDependency dependency) {
        String description = dependency.getRepeatCount() + " of " + dependency.getTaskName();

        switch (dependency.getType()){
            case LOOSELY_AFTER:
                description = description + " must occur after";
                break;
            case IMMEDIATELY_AFTER:
                description = description + " must occur immediately after";
                break;
            case LOOSELY_BEFORE:
                description = description + " must occur before";
                break;
            case IMMEDIATELY_BEFORE:
                description = description + " must occur immediately before";
                break;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel dependencyDescription = new JLabel(description);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        // EDIT BUTTON FUNCTIONALITY
        editButton.addActionListener(e -> {
            EditDependency editDependency = new EditDependency(tasks, dependency, "Editing dependency:", task.getName());

            editDependency.setVisible(true);

            editDependency.setSaveButtonListener(updated -> {
                refreshDependencyList();
            });
        });

        // DELETE BUTTON FUNCTIONALITY
        deleteButton.addActionListener(e -> {
            dependencies.remove(dependency);
            refreshDependencyList();
        });

        panel.add(dependencyDescription);
        panel.add(Box.createHorizontalGlue());
        panel.add(editButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(deleteButton);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return panel;
    }

    private void refreshDependencyList() {
        dependencyScrollPane.removeAll();

        if (dependencies != null) {
            for (TaskDependency dependency : dependencies) {
                dependencyScrollPane.add(createDependencyPanel(dependency));
            }

            dependencyScrollPane.revalidate();
            dependencyScrollPane.repaint();
        }
    }
}
