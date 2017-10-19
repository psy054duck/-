package business;

import java.io.File;
import storage.Storage;
/**
 * TaxRateTable manager
 */
public class TableManager {

    /**
     * The sole instance of this class.
     */
    private static TableManager instance = null;

    /**
     * Default folder where the tax rate tables is.
     */
    private final String defaultPath= "tables/";

    /**
     * Default file where the default table should be read from.
     */
    private final String defaultFileName = "2017";

    /**
     * The Table which is currently used to calculate taxes.
     */
    private TaxRateTable curTable;

    /**
     * Get the singleton.
     * 
     * @return the singleton of this class.
     */
    public static TableManager getInstance() {
        if (instance == null) {
            instance = new TableManager();
        }
        return instance;
    }

    /**
     * Get the table currently used.
     * 
     * @return the table currently used.
     */
    public TaxRateTable getTable() {
        return curTable;
    }

    /**
     * Save the table currently used to a file named after the table's year.
     * 
     * @param table the table to be saved.
     */
    public void save(TaxRateTable table) {
        Storage storage = Storage.getInstance();
        storage.save(table, defaultPath + String.valueOf(table.getYear()));
    }

    /**
     * Set the information of a given table
     * 
     * @param table the table to be set
     * @param row row of the table
     * @param left left point of the interval
     * @param right right point of the interval
     * @param rate the rate of this row
     */
    public void setTable(TaxRateTable table, int row, double left, double right, double rate) {
        table.setRow(row, left, right, rate);
    }

    /**
     * Complete a given table.
     * 
     * @param table the table to be completed
     * 
     * @throws IllegalArgumentException if the given table is invalid or null
     */
    public void completeTable(TaxRateTable table)
                   throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("table should NOT be null");
        }
        if (table.isValid()) {
            table.calDeduction();
            save(table);
        } else {
            throw new IllegalArgumentException("this table is invalid");
        }
    }

    /**
     * Load a table from a file.
     * 
     * @param filename the name of the file where this method should load an 
     *                 object
     */
    public void load(String filename) {
        Storage storage = Storage.getInstance();
        curTable = (TaxRateTable) storage.load(defaultPath + filename);
    }

    /**
     * Create a new tax table.
     * 
     * @param num the number of rows
     * @param exep the exemption
     * @param year the year when this table published
     * 
     * @return the new table
     */
    public TaxRateTable createTable(int num, double exep, int year) {
        TaxRateTable newTable = new TaxRateTable(num, exep, year);
        return newTable;
    }

    /**
     * Private constructor to avoid user from creating more than one TableManager instance.
     * What the constructor do is to set curTable to default table.
     */
    private TableManager() {
        File defaultFile = new File(defaultPath + defaultFileName);
        File path = new File(defaultPath);
        Storage storage = Storage.getInstance();

        if (!path.exists()) {
            path.mkdirs();
        }
        if (defaultFile.exists()) {
            curTable = (TaxRateTable) storage.load(defaultPath + defaultFileName);
        } else {
            curTable = TaxRateTable.getDefaultTable();
        }
        completeTable(curTable);
    }
}
