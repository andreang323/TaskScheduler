import javax.swing.*;
import forms.EditItem;
import SchedulerUI.*;

public class Main {
  static SchedulerWindow mainScheduler;
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        mainScheduler =  new SchedulerWindow();
        EditItem addItem = new EditItem("Adding new task:");
        addItem.setVisible(true);
      }
    });



  }
}
