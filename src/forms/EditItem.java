package forms;

import Tasks.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
    private JList list1;
    private JList list2;
    private JList list3;
    private JList list4;
    private JList list5;
    private JList Group;
    private JButton validateButton;
    private JFormattedTextField formattedTextField1;
    private JCheckBox Optional;
    private JCheckBox skipDependentTaskIfCheckBox;
    private JCheckBox reduceDurationOfThisCheckBox;
    private JTextField textField1;
    private JLabel timeNote1;
    private JLabel timeNote2;
    private TimeLockListener timeLockListener;
    private SaveButtonListener saveButtonListener;
    private Task task;
    private boolean editing;

    public void setupPanel() {

        // Update displayed parameters with the task assigned to EditItem
        if (task != null) {
            Name.setText(task.getName());
            StartTime.setText(task.getStartTime());
            EndTime.setText(task.getEndTime());
            Duration.setText(task.getDuration());
            lockRadioButton.setSelected(task.isLockStartTime());
            lockRadioButton2.setSelected(task.isLockEndTime());
        }

        // Set up lock listeners here
//        timeLockListener = new TimeLockListener();
//        lockRadioButton.addActionListener(timeLockListener);
//        lockRadioButton1.addActionListener(timeLockListener);
//        lockRadioButton2.addActionListener(timeLockListener);

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

        StartTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // StartTime changed
                if (lockRadioButton.isSelected()) {
                    // StartTime locked, treat duration as driven

                } else if(lockRadioButton1.isSelected()) {
                    // endtime locked, treat duration as driven
                } else if(lockRadioButton2.isSelected()) {
                    // duration locked, treat end time as driven
                }
            }
        });
        EndTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // End time changed
                if (lockRadioButton.isSelected()) {
                    // StartTime locked, treat duration as driven
                } else if(lockRadioButton1.isSelected()) {
                    // endtime locked, treat duration as driven
                } else if (lockRadioButton2.isSelected()) {
                    // duration is locked, treat start time as driven
                }
            }
        });
        Duration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Duration changed
            }
        });
    }

    public EditItem(String newTitle, Task newTask, boolean editing){
        this.task = newTask;

        setTitle(newTitle);
        if (editing) {
            createButton.setText("Update");
        } else {
            createButton.setText("Create");
        }

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
        task.setStartTime(StartTime.getText());
        task.setEndTime(EndTime.getText());
        task.setDuration(Duration.getText());
        task.setLockStartTime(lockRadioButton.isSelected());
        task.setLockEndTime(lockRadioButton2.isSelected());

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }

}
