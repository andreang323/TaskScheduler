package forms;

import Tasks.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


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
    private LocalDateTime date = LocalDateTime.now();
    private boolean editing;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


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

        StartTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // StartTime changed
                System.out.println("StartTime Changed");

                LocalDateTime startDate = LocalDateTime.parse(StartTime.getText());
                LocalDateTime endDate = LocalDateTime.parse(StartTime.getText());
//                LocalDateTime durationAsDate = LocalDateTime.parse(Duration.getText());


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
                    EndTime.setText(endTime.toString());
                }
            }
        });
        EndTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // End time changed
                System.out.println("EndTime Changed");

                LocalDateTime startDate = LocalDateTime.parse(StartTime.getText());
                LocalDateTime endDate = LocalDateTime.parse(StartTime.getText());
//                LocalDateTime durationAsDate = LocalDateTime.parse(Duration.getText());

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
                    EndTime.setText(startTime.toString());
                }
            }
        });

        Duration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Duration changed
                System.out.println("Duration Changed");

                LocalDateTime startDate = LocalDateTime.parse(StartTime.getText());
                LocalDateTime endDate = LocalDateTime.parse(StartTime.getText());
//                LocalDateTime durationAsDate = LocalDateTime.parse(Duration.getText());
                long durationSeconds = startDate.until(endDate, ChronoUnit.SECONDS);

                if (lockRadioButton.isSelected()) {
                    // StartTime locked, treat endTime as driven
                    LocalDateTime endTime = startDate.plusSeconds(durationSeconds);
                    EndTime.setText(endTime.toString());
                } else if (lockRadioButton1.isSelected()) {
                    // endtime locked, treat startTime as driven
                    LocalDateTime startTime = endDate.plusSeconds(durationSeconds);
                    StartTime.setText(startTime.toString());
                } else if (lockRadioButton2.isSelected()) {
                    // duration is locked, treat endTime as driven
                    LocalDateTime endTime = startDate.plusSeconds(durationSeconds);
                    EndTime.setText(endTime.toString());
                }
            }
        });
    }

    private static String getDurationString(LocalDateTime startDate, LocalDateTime endDate) {
        long durationYears = startDate.until(endDate, ChronoUnit.YEARS);
        long durationMonths = startDate.until(endDate, ChronoUnit.MONTHS) - (durationYears * 12);
        long durationDays = startDate.until(endDate, ChronoUnit.DAYS) - (durationMonths * 30);
        long durationHours = startDate.until(endDate, ChronoUnit.HOURS) - (durationDays * 24);
        long durationMinutes = startDate.until(endDate, ChronoUnit.MINUTES) - (durationHours * 60);
        long durationSeconds = startDate.until(endDate, ChronoUnit.SECONDS) - (durationMinutes * 60);

        return durationYears + "-" + durationMonths + "-" + durationDays + " " + durationHours + ":" + durationMinutes + ":" + durationSeconds;
    }

    public EditItem(String newTitle, Task newTask, boolean editing){
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
        task.setDuration(Long.parseLong(Duration.getText()));
        task.setLockStartTime(lockRadioButton.isSelected());
        task.setLockEndTime(lockRadioButton2.isSelected());
        task.setPriority(Integer.parseInt(Priority.getText()));
        task.setOptional(Optional.isSelected());

        // Notify the listener with the task object
        if (saveButtonListener != null) {
            saveButtonListener.onSubmitClicked(task);
        }

        // Close the window now that we've made our changes
        dispose();
    }

}
