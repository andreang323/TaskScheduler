package SchedulerUI;

import Tasks.Task;
import forms.EditItem;
import forms.SaveButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SchedulerWindow extends JFrame{
    private JPanel contentPane;
    private JPanel leftPane;
    private JPanel rightPane;
    private JPanel tasksListPane;
    private List<Task> tasks;
    private JPanel scheduleDisplayPane;
    private JTextField scheduleStartTimeField;
    private JTextField scheduleEndTimeField;

    public SchedulerWindow(){
        setTitle("Task Scheduler");

        tasks = new ArrayList<>();

        contentPane = new JPanel();
        leftPane = createLeftPane();
        rightPane = createRightPane();
        contentPane.add(leftPane);
        contentPane.add(rightPane);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Need to add scrollbar to this if overflow of task panels
    private JPanel createRightPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5));

        tasksListPane = new JPanel();
        tasksListPane.setLayout(new GridLayout(0, 1, 5, 5));

        // Initialize with any pre-existing tasks
        for (int i = 0; i < tasks.size(); i++) {
            tasksListPane.add(createTaskPanel(tasks.get(i)));
        }

        panel.add(tasksListPane);

        JButton newButton = new JButton("New");

        panel.add(newButton);

        // Open a new EditItem JFrame
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Create a new task associated with this
                Task newTask = new Task();
                newTask.setName("Task " + (tasks.size() + 1));

                EditItem addItem = new EditItem("Adding new task:", newTask, false);
                addItem.setVisible(true);

                // Callback so that addItem can add the task properly
                addItem.setSaveButtonListener(new SaveButtonListener() {
                    @Override
                    public void onSubmitClicked(Task task) {
                        System.out.println("Registered new task: " + task.getName());
                        tasks.add(task);
                        tasksListPane.add(createTaskPanel(task));

                        // redraw everything because we added a component
                        tasksListPane.revalidate();
                        repaint();
                    }
                });
            }
        });

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Tasks"));

        return panel;


    }

    private JPanel createLeftPane() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(0, 2, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();

        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        panel.add(previousButton, gbc);

        gbc.gridy++;
        panel.add(nextButton, gbc);

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Schedule"));

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
            EditItem editItem = new EditItem("Editing Task: " + task.getName(), task, true);

            editItem.setVisible(true);

            editItem.setSaveButtonListener(updated -> {
                refreshTaskList();
            });
        });

        lockButton.addActionListener(e-> {
            System.out.println("not implemented yet, to do after display done");
        });

        deleteButton.addActionListener(e -> {
            tasks.remove(task);
            refreshTaskList();
        });

        panel.add(taskName);
        panel.add(editButton);
        panel.add(lockButton);
        panel.add(deleteButton);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return panel;
    }

    // Remove task panel from list of tasks
    public void deleteTask() {
        // panel.remove();
    }

    public void updateTask() {

    }

    private void refreshTaskList() {
        tasksListPane.removeAll();

        for (Task task : tasks) {
            tasksListPane.add(createTaskPanel(task));
        }

        tasksListPane.revalidate();
        tasksListPane.repaint();
    }

}
