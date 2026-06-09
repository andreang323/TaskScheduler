package SchedulerUI;

import Tasks.Task;
import forms.EditItem;

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
    private List<Task> tasks;
    private JPanel taskListPanel;

    public SchedulerWindow(){
        setTitle("Task Scheduler");

        contentPane = new JPanel();
        tasks = new ArrayList<>();

        tasks.add(new Task());
        tasks.add(new Task());
        tasks.add(new Task());

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
        JPanel panel = new JPanel(new BorderLayout());

        taskListPanel = new JPanel();
        taskListPanel.setLayout(new GridLayout(0, 1, 5, 5));

        JScrollPane scrollPane = new JScrollPane(taskListPanel);

        JButton newButton = new JButton("New");

        // Open a new EditItem JFrame
        newButton.addActionListener(e -> {
            EditItem addItem = new EditItem("Adding new task:");
            addItem.setVisible(true);
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(newButton, BorderLayout.SOUTH);

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Tasks"
                )
        );

        refreshTaskList();

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

        JLabel taskName = new JLabel("Task Name");
        JButton editButton = new JButton("Edit");
        JButton lockButton = new JButton("Lock");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(e -> {
            EditItem editItem = new EditItem("Editing Task: " + taskName);
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
        taskListPanel.removeAll();

        for (Task task : tasks) {
            taskListPanel.add(createTaskPanel(task));
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

}
