import presentation.UI;
import business.Calculator;
import business.TableManager;
import business.TaxRateTable;
import business.RateTableRow;
import storage.Storage;
import java.util.Iterator;
import java.io.File;

public class Test {
    public static void main(String[] args) {
        checkException();
        System.out.println("--------------");
        checkCalculation();
        System.out.println("--------------");
        checkNewAndLoad();
        System.out.println("--------------");
        checkCalculationAfterLoad();
     }

    public static void checkException() {
        boolean hasThrown = false;
        System.out.println("Now check exception:");
        System.out.printf("\tCalculator.calculateTax:\t");
        Calculator calculator = new Calculator();
        try {
            calculator.calculateTax(-1);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        } catch (Exception e) {

        }
        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(calculator.calculateTax(-1) should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }

        hasThrown = false;
        TableManager tableManager = TableManager.getInstance();
        System.out.printf("\tTableManager.completeTable:\t");
        try {
            tableManager.completeTable(null);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        }

        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(TableManager.completeTable(null) should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }

        hasThrown = false;
        System.out.printf("\tTaxRateTable.TaxRateTable:\t");
        try {
            TaxRateTable table = new TaxRateTable(null, 0, 0);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        }
        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(TaxRateTable.TaxRateTable(null, 0, 0) should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }

        hasThrown = false;
        System.out.printf("\tStorage.save:\t\t\t");
        Storage storage = Storage.getInstance();
        try {
            storage.save(null, "tables/2011");
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        }
        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(Storage.save(null, \"tables/2011\") should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }

        hasThrown = false;
        System.out.printf("\tStorage.save:\t\t\t");
        try {
            storage.save(new TaxRateTable(), null);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        }
        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(Storage.save(new TaxRateTable(), null) should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }

        System.out.printf("\tStorage.load:\t\t\t");
        try {
            storage.load(null);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
        }
        if (!hasThrown) {
            System.out.printf("Failed ");
            System.out.println("(Storage.load(null) should throw IllegalArgumentException)");
        } else {
            System.out.println("Pass");
        }
    }

    public static void checkCalculation() {
        Calculator calculator = new Calculator();
        System.out.println("Now check calculation:");
        System.out.printf("\tCalculator.calculateTax:\t");
        double res = 0;
        double[] input = {1000, 2000, 3000, 4000, 5000};
        double[] output = {0, 20, 115, 235, 385};
        int i = 0;
        for (; i < input.length; ++i) {
            try {
                res = calculator.calculateTax(input[i]);
            } catch (Exception e) {
            
            }
            if (Math.abs(res - output[i]) > 0.0001) {
                System.out.printf("Failed ");
                System.out.printf("(Calculator.calculateTax(%.2f) should be %.2f but the result is %.2f)", input[i], output[i], res);
                break;
            }
        }
        if (i == input.length) {
            System.out.println("Pass");
        }
    }

    public static void checkCalculationAfterLoad() {
        Calculator calculator = new Calculator();
        System.out.println("Now check calculation after load a new table:");
        System.out.printf("\tCalculator.calculateTax:\t");
        double res = 0;
        double[] input = {2000, 3000, 4000, 5000, 6000, 7000};
        double[] output = {0, 0, 15, 45, 145, 245};
        int i = 0;
        for (; i < input.length; ++i) {
            try {
                res = calculator.calculateTax(input[i]);
            } catch (Exception e) {

            }
            if (Math.abs(res - output[i]) > 0.0001) {
                System.out.printf("Failed ");
                System.out.printf("(Calculator.calculateTax(%.2f) should be %.2f but the result is %f)", input[i], output[i], res);
            }
        }

        if (i == input.length) {
            System.out.println("Pass");
        }
    }

    public static void checkNewAndLoad() {
        System.out.println("Now check create and load a table:");
        TableManager tableManager = TableManager.getInstance();
        TaxRateTable newTable = tableManager.createTable(7, 3500, 2011);
        double[][] rawTable = {
            {1, 0, 1500, 0.03},
            {2, 1500, 4500, 0.1},
            {3, 4500, 9000, 0.2},
            {4, 9000, 35000, 0.25},
            {5, 35000, 55000, 0.3},
            {6, 55000, 80000, 0.35},
            {7, 80000, Double.POSITIVE_INFINITY, 0.45},
        };
        for (int i = 0; i < rawTable.length; ++i) {
            tableManager.setTable(newTable, (int) rawTable[i][0], rawTable[i][1], rawTable[i][2], rawTable[i][3]);
        }
        tableManager.completeTable(newTable);
        tableManager.save(newTable);
        tableManager.load("2011");

        TaxRateTable curTable = tableManager.getTable();

        Iterator<RateTableRow> iterator = curTable.iterator();
        int i = 0;
        for (i = 0; i < rawTable.length; ++i) {
            if (!iterator.hasNext()) {
                break;
            }
            RateTableRow row = iterator.next();
            if (rawTable[i][0] != row.getLevel()
                || rawTable[i][1] != row.getLeft()
                || rawTable[i][2] != row.getRight()
                || rawTable[i][3] != row.getRate()) {
                    break;
                }
        }
        System.out.printf("\tCheck whether current table is the expected one:\t");
        if (i != rawTable.length) {
            System.out.printf("Failed\n");
        } else {
            System.out.printf("Pass\n");
        }
    }
}
