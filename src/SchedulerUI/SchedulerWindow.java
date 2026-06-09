package SchedulerUI;

import Tasks.Task;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.TitledBorder;

public class SchedulerWindow extends JFrame{
    private JPanel contentPane;
    private JPanel leftPane;
    private JPanel rightPane;
    private List<Task> tasks;

    public SchedulerWindow(){
        setTitle("Task Scheduler");

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

//        for (int i = 0; i < tasks.size(); i++) {
        for (int i = 0; i < 10; i++) {
            panel.add(createTaskPanel());
        }

        JButton newButton = new JButton("New");

        panel.add(newButton);

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
    public JPanel createTaskPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel taskName = new JLabel("Task Name");
        JButton editButton = new JButton("Edit");
        JButton lockButton = new JButton("Lock");
        JButton deleteButton = new JButton("Delete");

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

}
