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
    private JScrollPane scrollPane;
    private JPanel tasksListPane;
    private List<Task> tasks;

    public SchedulerWindow(){
        setTitle("Task Scheduler");

        tasks = new ArrayList<>();

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
        contentPane.add(leftPane,gbc );
        gbc.weightx = 1;
        gbc.gridx ++;
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
        for (int i = 0; i < tasks.size(); i++) {
            tasksListPane.add(createTaskPanel(tasks.get(i)));
        }

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

                EditItem addItem = new EditItem("Adding new task:", newTask);
                addItem.setVisible(true);

                // Callback so that addItem can add the task properly
                addItem.setSaveButtonListener(new SaveButtonListener() {
                    @Override
                    public void onSubmitClicked(Task task) {
                        System.out.println("Registered new task: " + task.getName());
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
        JPanel scheduleDisplay = new JPanel();
        scheduleDisplay.setBackground(Color.white);
        scheduleDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(scheduleDisplay, gbc);

        JPanel leftButtonsPane = new JPanel();
        leftButtonsPane.setLayout(new BoxLayout(leftButtonsPane, BoxLayout.LINE_AXIS));

        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        leftButtonsPane.add(Box.createHorizontalGlue());
        leftButtonsPane.add(previousButton, BorderLayout.EAST);
        leftButtonsPane.add(Box.createRigidArea(new Dimension(10, 0)));
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
            EditItem editItem = new EditItem("Editing Task: " + task.getName(), task);
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
        panel.add(Box.createHorizontalGlue());
        panel.add(editButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(lockButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(deleteButton);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return panel;
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
