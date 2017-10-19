/**
 * Main
 */
import presentation.UI;

 public class Main {
     public static void main(String[] args) {
         UI ui = new UI();
         while (true) {
             String cmd = ui.getCmd();
             try {
                ui.execCmd(cmd);
             } catch(Exception e) {

             }
         }
     }
 }
