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
    private JScrollBar scrollBar1;
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
    private TimeLockListener timeLockListener;
    private SaveButtonListener saveButtonListener;
    private Task task;

    public void setupPanel() {

        // Update displayed parameters with the task assigned to EditItem
        if (task == null) {
            task = new Task();
        }
        Name.setText(task.getName());
        StartTime.setText(String.valueOf(task.getStartTime()));
        EndTime.setText(String.valueOf(task.getEndTime()));
        Duration.setText(String.valueOf(task.getDuration()));
        lockRadioButton.setSelected(task.isLockStartTime());
        lockRadioButton1.setSelected(task.isLockEndTime());

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
    }

    public EditItem(String newTitle) {
        setTitle(newTitle);
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
        task.setName(this.Name.getText());
        if (this.StartTime.getText().isEmpty()) {
            this.StartTime.setText("0");
        }
        task.setStartTime(Integer.parseInt(this.StartTime.getText()));
        if (this.EndTime.getText().isEmpty()) {
            this.EndTime.setText("0");
        }
        task.setEndTime(Integer.parseInt(this.EndTime.getText()));
        if (this.Duration.getText().isEmpty()) {
            this.Duration.setText("0");
        }
        task.setDuration(Integer.parseInt(this.Duration.getText()));
        task.setLockStartTime(this.lockRadioButton.isSelected());
        task.setLockEndTime(this.lockRadioButton1.isSelected());

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }

}
