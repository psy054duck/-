package presentation;

import java.util.Scanner;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import business.*;
/**
 * Command line interface
 */

 public class UI {

    /**
     * Prompt used to indicate user that the program has started.
     */
    private final String prompt = ">>";

    /**
     * Table manager used to do everything about tax table.
     */
    private TableManager tableManager = TableManager.getInstance();

    /**
     * Calculator used to do all computation.
     */
    private Calculator calculator = new Calculator();

    /**
     * Constructor
     */
    public UI() {
        try {
            /**
             * Set up the command table
             */
            Command[] init = {
                new Command("help", "show command menu", UI.class.getMethod("mHelp", String.class)),
                new Command("cal", "calculate the tax", UI.class.getMethod("mCal", String.class)),
                new Command("chrate", "change some level's rate", UI.class.getMethod("mChrate", String.class)),
                new Command("chexemp", "change some level's rate", UI.class.getMethod("mChexemp", String.class)),
                new Command("new", "create a new table", UI.class.getMethod("mNew", String.class)),
                new Command("load", "load a table from file", UI.class.getMethod("mLoad", String.class)),
                new Command("show", "display current table", UI.class.getMethod("mShow", String.class)),
                new Command("ls", "display all tables that have been created", UI.class.getMethod("mLs", String.class)),
                new Command("quit", "exit this program", UI.class.getMethod("mQuit", String.class)),
            };
            commands = init;
            sayHello();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get command from user.
     * 
     * @return the command getted from user
     */
    public String getCmd() {
        System.out.printf("%s ", prompt);
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        s = s.trim();
        return s;
    }

    /**
     * Execute a command.
     * 
     * @param cmd the command given by user
     */
    public void execCmd(String cmd) {
        String[] args = cmd.split(" ");
        for (Command i : commands) {
            if (i.getName().equals(args[0])) {
                try {
                    i.getFunc().invoke(this, cmd);
                } catch (InvocationTargetException e) {
                    System.out.println(e.getCause().getMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println("There is no such a command, you can type `help` for help");
    }

    /**
     * Function for command `help`.
     * 
     * @param cmd arguments passed to this command
     */
    public void mHelp(String cmd) {
        for (Command i : commands) {
            System.out.printf("%-10s %s %s\n", i.getName(), "--", i.getDesc());
        }
    }

    /**
     * Fucntion for command `ls`.
     * 
     * @param cmd arguments passed to this command
     */
    public void mLs(String cmd) {
        File tables = new File("tables");
        if (tables.exists()) {
            File[] files = tables.listFiles();
            for (File fp : files) {
                System.out.print(fp.getName() + ' ');
            }
            System.out.printf("\n");
        } else {
            System.out.println("No tables");
        }
    }

    /**
     * Function for command `quit`.
     * 
     * @param cmd arguments passed to this command
     */
    public void mQuit(String cmd) {
        System.exit(0);
    }

    /**
     * Function for command `chrate`.
     * 
     * @param cmd arguments passed to this command
     */
    public void mChrate(String cmd) {
        TaxRateTable curTable = tableManager.getTable();
        System.out.print("Please enter which level's rate you want to change: ");
        Scanner in = new Scanner(System.in); 
        int level = 0;
        try {
            level = in.nextInt();
        } catch (Exception e) {
            System.out.println("Sorry, an integer is expected");
            return;
        }
        System.out.print("Please enter what value you want to change to: ");
        double rate = 0;
        try {
            rate = in.nextDouble();
        } catch (Exception e) {
            System.out.println("Sorry, an double is expected");
            return;
        }
        if (rate < 0 || rate >= 1) {
            System.out.println("Sorry, rate should be in [0, 1)");
            return;
        }
        curTable.setRate(level, rate);
        tableManager.completeTable(curTable);
    }

    /**
     * Function for command `chexemp`.
     * 
     * @param cmd arguments passed to this command
     */
    public void mChexemp(String cmd) {
        TaxRateTable curTable = tableManager.getTable();
        Scanner in = new Scanner(System.in); 
        System.out.print("Please enter what value you want to change to: ");
        double exemp = 0;
        try {
            exemp = in.nextDouble();
        } catch (Exception e) {
            System.out.println("Sorry, an double is expected");
            return;
        }
        if (exemp < 0) {
            System.out.println("Sorry, rate should be larger than 0");
            return;
        }
        curTable.setExemption(exemp);
        tableManager.completeTable(curTable);
    }

    /**
     * Function for command `cal`.
     * 
     * @param cmd arguments passed to this command
     * 
     * @throws Exception if the exception occur
     */
    public void mCal(String cmd) throws Exception {
        String[] args = cmd.split("\\s+");
        if (args.length != 2) {
            System.out.println(args.length);
            throw new IllegalArgumentException("cal usage: cal income(double)");
        }
        double income = 0;
        try {
            income = Double.valueOf(args[1]);
        } catch (Exception e) {
            System.out.println("A double is expected");
            return;
        }
        double tax = calculator.calculateTax(income);
        if (tax > 0) {
            System.out.printf("You should pay taxes of %.2f for your income of %.2f\n", tax, income);
        } else {
            System.out.printf("You needn't pay any tax for your income of %.2f\n", income);
        }
    }

    /**
     * Function for command 'load'.
     * 
     * @param cmd arguments passed to this command
     */
    public void mLoad(String cmd) {
        String[] args = cmd.split(" ");
        if (args.length != 2) {
            System.out.println("load usage: load filename");
        }
        tableManager.load(args[1]);
    }

    /**
     * Function for command 'new'.
     * 
     * @param cmd arguments passed to this command
     */
    public void mNew(String cmd) {
        System.out.println("Please enter the number of levels");
        Scanner in = new Scanner(System.in);
        int level = in.nextInt();
        System.out.println("Please enter the exeption");
        double exeption = in.nextDouble();
        System.out.println("Please enter the year when this table published");
        int year = in.nextInt();

        TaxRateTable newTable = tableManager.createTable(level, exeption, year);
        
        System.out.println("\nNext, you need enter the information of every row");
        System.out.printf("Fomat: LeftEndPoint(double) RightEndPoint(double) Rate(double)\n\n");
        System.out.printf("Note: if this line is the last line RightEndPoint should be 'inf'\n");
        System.out.printf("      if this line is the first line LeftEndPoint should be 0\n");
        for (int i = 0; i < level; ++i) {
            System.out.printf("Please enter the information of the %dth row\n", i+1);
            double left = in.nextDouble();
            String rightStr = in.next();
            double right =
                   (rightStr.equals("inf")) ? Double.POSITIVE_INFINITY : Double.valueOf(rightStr);
            double rate = in.nextDouble();

            tableManager.setTable(newTable, i+1, left, right, rate);
        }

        try {
            tableManager.completeTable(newTable);
        } catch (Exception e) {
            System.out.println("This table is not valid!");
        }
    }

    /**
     * Function for command 'show'.
     * 
     * @param cmd arguments passed to this command
     */
    public void mShow(String cmd) {
        TaxRateTable curTable = tableManager.getTable();
        System.out.printf("Exemption: %.2f\n", curTable.getExemption());
        System.out.printf("%5s %20s %15s %20s\n", "Level", "Taxable Income", "Tax Rate", "Quick Deduction");
        System.out.printf("%5s %25s %10s %20s\n", "-----", "----------------------", "--------", "---------------");

        for (RateTableRow i : curTable) {
            System.out.printf("%-8d [%-9.1f, %9.1f] %10.2f %20.2f\n",
                              i.getLevel(),
                              i.getLeft(),
                              i.getRight(),
                              i.getRate(),
                              i.getDeduction());
        }
    }

    /**
     * Part of initial function, should be only called in constructor
     */
    private void sayHello() {
        System.out.printf("%20s\n", "Welcome");
        System.out.printf("-------------------------------------------\n");
        mHelp(null);
    }

    private Command[] commands;
 }

 /**
  * A class used to give the function of a specified command and information about it
  */
 class Command {

    /**
     * The name that will be showed in command menu.
     * And when user type in the name, the corresponding function will be
     * invoked.
     */
     private final String name;

     /**
      * Detailed description of this command.
      */
     private final String description;

     /**
      * The function of this command that will be invoked once user type 
      * in the name of this command.
      */
     private final Method func;

     /**
      * constructor with information
      * @param name the name showed in command menu
      * @param desc description of this command
      * @param func function for this command
      */
     Command(String name, String desc, Method func) {
         this.name = name;
         this.description = desc;
         this.func = func;
     }

     /**
      * Get the command name of this command.
      *
      * @return the name of this command
      */
     public String getName() { return name; }

     /**
      * Get the description of this command.
      *
      * @return the description of this command
      */
     public String getDesc() { return description; }

     /**
      * Get the function of this command.
      *
      * @return this function of this command
      */
     public Method getFunc() { return func; }
 }
