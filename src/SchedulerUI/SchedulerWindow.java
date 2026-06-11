package SchedulerUI;

import Tasks.Schedule;
import Tasks.Task;
import Tasks.SolvedTask;
import Tasks.TaskDependency;
import forms.EditItem;
import forms.SaveButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchedulerWindow extends JFrame {
    private JPanel contentPane;
    private JPanel leftPane;
    private JPanel rightPane;
    private JScrollPane scrollPane;
    private JPanel tasksListPane;
    private List<Task> tasks;

    private int nextTaskID = 0;
    private JPanel scheduleListPane;
    private JScrollPane scheduleScrollPane;
    private List<Schedule> schedules;
    private int currentScheduleIndex = 0;

    private JTextField scheduleStart;
    private JTextField scheduleEnd;
    private int maxSolutions = 100;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public SchedulerWindow(){
        setTitle("Task Scheduler");

        tasks = new ArrayList<>();
        schedules = new ArrayList<>();

        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 2;
        gbc.weighty = 1;

        leftPane = createLeftPane();
        rightPane = createRightPane();
        contentPane.add(leftPane, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        contentPane.add(rightPane, gbc);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();

        setSize(860, 540);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Contains scrollable panel with tasks and new button
    private JPanel createRightPane() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 5;

        tasksListPane = new JPanel();
        tasksListPane.setBackground(Color.white);
        tasksListPane.setLayout(new BoxLayout(tasksListPane, BoxLayout.Y_AXIS));

        // Initialize with any pre-existing tasks
//        for (int i = 0; i < tasks.size(); i++) {
//            tasksListPane.add(createTaskPanel(tasks.get(i)));
//        }

        scrollPane = new JScrollPane(tasksListPane);
        scrollPane.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
        panel.add(scrollPane, gbc);

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 1;
        gbc.weighty = 0;

        JPanel buttonPane = new JPanel(new GridLayout());
        JButton newButton = new JButton("New");
        buttonPane.add(newButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        panel.add(buttonPane, gbc);

        // Open a new EditItem JFrame
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Create a new task associated with this
                Task newTask = new Task();
                newTask.setName("Task " + (tasks.size() + 1));

                EditItem addItem = new EditItem("Adding new task:", newTask, false, tasks);
                addItem.setVisible(true);

                // Callback so that addItem can add the task properly
                addItem.setSaveButtonListener(new SaveButtonListener() {
                    @Override
                    public void onSubmitClicked(Task task) {
                        System.out.println("Registered new task: " + task.getName());
                        task.setTaskID(nextTaskID);
                        nextTaskID++;
                        tasks.add(task);

                        GridBagConstraints gbc2 = new GridBagConstraints();

                        gbc2.insets = new Insets(5, 5, 5, 5);
                        gbc2.gridx = 0;
                        gbc2.gridy = GridBagConstraints.RELATIVE;
                        gbc2.anchor = GridBagConstraints.NORTHWEST;
                        gbc2.fill = GridBagConstraints.HORIZONTAL;

                        tasksListPane.add(createTaskPanel(task), gbc2);

                        // redraw everything because we added a component
                        tasksListPane.revalidate();
                        repaint();
                    }
                });
            }
        });

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)), "Tasks"));

        return panel;
    }

    private JPanel createLeftPane() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 5;

        // Replace scheduleDisplay with actual schedule display
        scheduleListPane = new JPanel();
        scheduleListPane.setBackground(Color.white);
        scheduleListPane.setLayout(new BoxLayout(scheduleListPane, BoxLayout.Y_AXIS));

        scheduleScrollPane = new JScrollPane(scheduleListPane);
        scheduleScrollPane.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
        scheduleScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        panel.add(scheduleScrollPane, gbc);

        LocalDateTime now = LocalDateTime.now();

        scheduleStart = new JTextField(
                now.format(DATE_FORMAT)
        );

        scheduleEnd = new JTextField(
                now.plusDays(7).format(DATE_FORMAT)
        );

        JPanel leftButtonsPane = new JPanel();
        leftButtonsPane.setLayout(new BoxLayout(leftButtonsPane, BoxLayout.LINE_AXIS));


        JButton scheduleTimeUpdateButton = new JButton("Update");

        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> {
            if (schedules.isEmpty()) {
                return;
            }
            currentScheduleIndex--;
            if (currentScheduleIndex < 0) {
                currentScheduleIndex = schedules.size() - 1;
            }
            refreshScheduleDisplay();
        });

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (schedules.isEmpty()) {
                return;
            }
            currentScheduleIndex++;
            if (currentScheduleIndex >= schedules.size()) {
                currentScheduleIndex = 0;
            }
            refreshScheduleDisplay();
        });

        scheduleTimeUpdateButton.addActionListener(e -> {
            try {
                updateSchedule();
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Use format: yyyy-MM-dd HH:mm\nExample: 2026-06-09 08:00",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE
                );
                System.out.println(ex);
                System.out.println("you've met a terrible fate haven't you");
            }
        });

        JLabel scheduleStartLabel = new JLabel("Schedule Start:");
        JLabel scheduleEndLabel = new JLabel("Schedule End:");

        leftButtonsPane.add(scheduleStartLabel);
        leftButtonsPane.add(Box.createRigidArea(new Dimension(5, 0)));
        leftButtonsPane.add(scheduleStart, BorderLayout.WEST);
        leftButtonsPane.add(Box.createRigidArea(new Dimension(10, 0)));
        leftButtonsPane.add(scheduleEndLabel);
        leftButtonsPane.add(Box.createRigidArea(new Dimension(5, 0)));
        leftButtonsPane.add(scheduleEnd, BorderLayout.WEST);

        leftButtonsPane.add(scheduleTimeUpdateButton, BorderLayout.WEST);
        leftButtonsPane.add(Box.createHorizontalGlue());
        leftButtonsPane.add(Box.createRigidArea(new Dimension(5, 0)));
        leftButtonsPane.add(previousButton, BorderLayout.EAST);
        leftButtonsPane.add(Box.createRigidArea(new Dimension(5, 0)));
        leftButtonsPane.add(nextButton, BorderLayout.EAST);
        leftButtonsPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        gbc.gridy = 1;
        gbc.weighty = 0;
        panel.add(leftButtonsPane, gbc);

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)), "Schedule"));

        return panel;
    }

    // Create a new task panel with task name,
    // edit button, lock button, and delete button
    public JPanel createTaskPanel(Task task) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel taskName = new JLabel(task.getName());
        JButton editButton = new JButton("Edit");
        JButton lockButton = new JButton("Lock");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(e -> {
            EditItem editItem = new EditItem("Editing Task: " + task.getName(), task, true, tasks);

            editItem.setVisible(true);

            editItem.setSaveButtonListener(updated -> {
                refreshTaskList();
            });
        });

        lockButton.addActionListener(e-> {
            if (schedules.size() <= 0){
                JOptionPane.showMessageDialog(
                        this,
                        "No available schedule.",
                        "Cannot lock!",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            Schedule currentSchedule = schedules.get(currentScheduleIndex);
            List<SolvedTask> matchingTasks = currentSchedule.getSolvedTask(task);
            if (matchingTasks.size() <= 0){
                JOptionPane.showMessageDialog(
                        this,
                        "No matching tasks found in schedule.",
                        "Cannot lock!",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            else if (matchingTasks.size() > 1){
                JOptionPane.showMessageDialog(
                        this,
                        "More than one matching task found in schedule.",
                        "Cannot lock!",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            SolvedTask solvedTask = matchingTasks.getFirst();
            Task newTask = task.copy();
            newTask.setStartTime(solvedTask.getStartTime());
            newTask.setEndTime(solvedTask.getEndTime());
            Collections.replaceAll(tasks, task, newTask);
            refreshTaskList();
            updateSchedule();
        });

        deleteButton.addActionListener(e -> {
            tasks.remove(task);
            for (Task t : tasks) {
                t.getDependencies().removeIf(dependency -> dependency.getDependencyTaskID() == task.getTaskID());
            }
            refreshTaskList();
        });

        panel.add(taskName);
        panel.add(Box.createHorizontalGlue());
        panel.add(editButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(lockButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(deleteButton);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return panel;
    }

    private void updateSchedule(){
        ScheduleSolver solver = new ScheduleSolver();

        long start = parseDateTime(scheduleStart.getText());
        long end = parseDateTime(scheduleEnd.getText());

        schedules = solver.GenerateSchedules(tasks, start, end, maxSolutions);
        currentScheduleIndex = 0;

        refreshScheduleDisplay();
    }

    private void refreshTaskList() {
        tasksListPane.removeAll();

        for (Task task : tasks) {
            tasksListPane.add(createTaskPanel(task));
        }

        tasksListPane.revalidate();
        tasksListPane.repaint();
    }

    private void refreshScheduleDisplay() {
        scheduleListPane.removeAll();

        if (schedules.isEmpty()) {
            scheduleListPane.add(new JLabel("No schedules generated yet."));
        } else {
            Schedule currentSchedule = schedules.get(currentScheduleIndex);

            JPanel headerPanel = new JPanel(new BorderLayout());

            JLabel header = new JLabel(
                    "Schedule " + (currentScheduleIndex + 1)
                            + " of " + schedules.size()
            );

            JLabel scoreHeader = new JLabel(
                    "Score: " + currentSchedule.getPriorityScore()
            );

            headerPanel.add(header, BorderLayout.WEST);
            headerPanel.add(scoreHeader, BorderLayout.EAST);

            headerPanel.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 30)
            );

            scheduleListPane.add(headerPanel);


            for (SolvedTask solvedTask : currentSchedule.getTaskList()) {
                scheduleListPane.add(createSolvedTaskPanel(solvedTask));
                scheduleListPane.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        scheduleListPane.revalidate();
        scheduleListPane.repaint();

    }

    private JPanel createSolvedTaskPanel(SolvedTask solvedTask) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        //setting to center alignment is kinda ugly
        //panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        panel.add(new JLabel("Task: " + solvedTask.getName()));
        panel.add(new JLabel("Start: " + formatUnixTime(solvedTask.getStartTime())));
        panel.add(new JLabel("End: " + formatUnixTime(solvedTask.getEndTime())));
        panel.add(new JLabel("Score: " + solvedTask.getScore()));

//        System.out.println("displayed start: " + solvedTask.getStartTime());
//        System.out.println("displayed end: " + solvedTask.getEndTime());

        return panel;
    }

    private long parseDateTime(String text) {
        LocalDateTime dateTime =
                LocalDateTime.parse(
                        text,
                        DATE_FORMAT
                );

        return dateTime
                .atZone(ZoneId.systemDefault())
                .toEpochSecond() / 60;
    }

    private String formatUnixTime(long unixTime) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(unixTime * 60),
                ZoneId.systemDefault()
        ).format(DATE_FORMAT);
    }

}
