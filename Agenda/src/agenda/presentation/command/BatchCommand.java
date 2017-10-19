package agenda.presentation.command;

import agenda.presentation.UI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringReader;

/**
 * Subclass of Command used to implement batch.
 */
public class BatchCommand extends Command {

    /**
     * Exectute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: batch [filename]");
            return ;
        }
        UI ui = UI.getInstance();
        File file = new File(args[1]);
        if (! file.exists()) {
            System.out.println("This file doesn't exist");
            return ;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String cmd = null;
        try {
            cmd = in.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        while (cmd != null) {
            if (! cmd.equals("")) {
                System.out.print(ui.getPrompt());
                System.out.println(cmd);
                try {
                    ui.execCmd(cmd);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            try {
                cmd = in.readLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "batch";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Execute commands in the given file";
    }
}