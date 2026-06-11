package forms;

import Tasks.Task;
import Tasks.TaskDependency;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static Tasks.TaskDependency.DependencyType.*;

public class EditDependency extends JFrame {
    private String[] taskNames;
    private Map<String, Integer> taskIDMap = new HashMap<>();
    private final String[] dependencyTypes = {
            "Immediately before",
            "Immediately after",
            "Loosely before",
            "Loosely after"};
    private TaskDependency dependency;
    private String title = "New dependency: ";
    int selectedTaskIndex = 0;

    private DependencySaveButtonListener dependencySaveButtonListener;

    JPanel panel;
    JLabel taskLabel;
    JList<String> taskList;
    JLabel typeLabel;
    JList<String> typeList;
    JLabel timesLabel;
    JTextField timesField;

    public void setSaveButtonListener(DependencySaveButtonListener listener) {
        this.dependencySaveButtonListener = listener;
    }

    // Constructor that sets up list displays and internal map as well as title
    public EditDependency(List<Task> tasks, TaskDependency dependency, String title, String ownerName){
        // Generate our task names and taskID map
        List<String> taskNamesList = new ArrayList<>();
        int i = 0;
        for (Task task : tasks){
            // Skip self
            if (Objects.equals(ownerName, task.getName())) {
                continue;
            }
            taskNamesList.add(task.getName());
            taskIDMap.put(task.getName(), task.getTaskID());
            // Record the current index in taskNamesList so we can set it in JList later
            if (task.getTaskID() == dependency.getDependencyTaskID()){
                selectedTaskIndex = i;
            }
            i ++;
        }
        taskNames = taskNamesList.toArray(new String[0]);
        this.dependency = dependency;
        this.title = title;

        setupPanel();
    }

    // Displays JFrame.
    private void setupPanel(){
        setTitle(title);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel newPanel = createPanel();
        newPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(newPanel);
        pack();

        //setSize(860, 540);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Creates the panel for this JFrame.
    private JPanel createPanel(){
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 10));
        taskLabel = new JLabel("Task: ");
        taskList = new JList<>(taskNames);
        typeLabel = new JLabel("Type: ");
        typeList = new JList<>(dependencyTypes);
        timesLabel = new JLabel("Times: ");
        timesField = new JTextField(String.valueOf(dependency.getRepeatCount()));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        panel.add(taskLabel);
        panel.add(taskList);

        panel.add(typeLabel);
        panel.add(typeList);

        panel.add(timesLabel);
        panel.add(timesField);

        panel.add(saveButton);
        panel.add(cancelButton);

        // Load all info from dependency
        taskList.setSelectedIndex(selectedTaskIndex);
        switch (dependency.getType()){
            case IMMEDIATELY_BEFORE:
                typeList.setSelectedIndex(0);
                break;
            case IMMEDIATELY_AFTER:
                typeList.setSelectedIndex(1);
                break;
            case LOOSELY_BEFORE:
                typeList.setSelectedIndex(2);
                break;
            case LOOSELY_AFTER:
                typeList.setSelectedIndex(3);
                break;
        }

        // Save button event listener
        saveButton.addActionListener(new ActionListener() {
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

        return panel;
    }

    // Saves any changes to the dependency and closes the window.
    private void saveChanges() {

        // Set repeat value
        // We set this first because it's the only thing that can really fail
        int repeat = 0;
        try{
            repeat = Integer.parseInt(timesField.getText());
            dependency.setRepeatCount(repeat);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a numerical value.",
                    "Invalid task repeat value.",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (repeat <= 0){
            JOptionPane.showMessageDialog(
                    this,
                    "Dependency task must occur one or more times.",
                    "Invalid task repeat value.",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (taskList.getSelectedIndex() == -1){
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a task.",
                    "No task selected.",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        // it's just easier if it knows
        dependency.setTaskName(taskNames[taskList.getSelectedIndex()]);

        // Set TaskID
        dependency.setDependencyTaskID(taskIDMap.get(taskNames[taskList.getSelectedIndex()]));

        // Set correct type
        switch (typeList.getSelectedIndex()){
            case 0:
                dependency.setType(IMMEDIATELY_BEFORE);
                break;
            case 1:
                dependency.setType(IMMEDIATELY_AFTER);
                break;
            case 2:
                dependency.setType(LOOSELY_BEFORE);
                break;
            case 3:
                dependency.setType(LOOSELY_AFTER);
                break;
        }

        // Notify the listener with the task object
        if (dependencySaveButtonListener != null) {
            dependencySaveButtonListener.onSubmitClicked(dependency);
        }
        dispose();
    }

    public void setDependencySaveButtonListener(DependencySaveButtonListener listener) {
        this.dependencySaveButtonListener = listener;
    }
}