package business;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Iterable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tax rate table
 */
public class TaxRateTable implements Iterable<RateTableRow>, Serializable {

    /**
     * Implements the iterface of Serializable.
     */
    static private final long serialVersionUID = 1;

    /**
     * The year when this table is published.
     */
    private final int year;

    /**
     * Default constructor without any extra information.
     */
    public TaxRateTable() {
        numLevels = 0;
        exemption = 0;
        year = 0;
    }

    /**
     * Constructor with a given table.
     * 
     * @param table the tax table
     * @param exep the exemption of this specification
     * @param year the year when this table published
     * 
     * @throws IllegalArgumentException if table is null
     */
    public TaxRateTable(RateTableRow[] table, double exep, int year) {
        if (table == null) {
            throw new IllegalArgumentException("table should NOT be null");
        }
        this.table = table;
        numLevels = table.length;
        exemption = exep;
        this.year = year;

        calDeduction();
    }

    /**
     * Class constructor specifying the number of tax levels.
     * 
     * @param nl the number of levels
     * @param exep the exemption of this specification
     * @param year the year when this table published
     */
    public TaxRateTable(int nl, double exep, int year) {
        numLevels = nl;
        exemption = exep;
        this.year = year;
        table = new RateTableRow[nl];

        for (int i = 0; i < table.length; ++i) {
            table[i] = new RateTableRow();
        }
    }

    /**
     * Calculate the deduction for this table.
     */
    public void calDeduction() {
        for (int i = 1; i < table.length; ++i) {
            table[i].setDeduction(table[i-1].getRight() * (table[i].getRate() - table[i-1].getRate()) + table[i-1].getDeduction());
        }
    }

    /**
     * Getter of exemption
     * 
     * @return the exemption of this specification
     */
    public double getExemption() { return exemption; }

    /**
     * Setter of exemption
     * 
     * @param exemp new exemption value
     */
    public void setExemption(double exemp) { exemption = exemp; }

    /**
     * Iterator of this class.
     * 
     * @return iterator of this object
     */
    public Iterator<RateTableRow> iterator() {
        return new TableIterator();
    }

    /**
     * Getter of year.
     * 
     * @return the year when the table published
     */
    public int getYear() { 
        return year;
    }

    /**
     * Iterator class.
     */
    class TableIterator implements Iterator<RateTableRow> {
        private int cur = 0;

        /**
         * Test whether this iterater is exhaulted.
         * 
         * @return <tt>true</tt> if this iterator is exhaulted.
         */
        public boolean hasNext() {
            return cur != numLevels;
        }

        /**
         * Get the next element in this iterator.
         * 
         * @return the next element if this iterator still has elements.
         * 
         * @throws NoSuchElementException if this iterator is exhaulted.
         */
        public RateTableRow next() {
            if (!hasNext()) {
                throw new NoSuchElementException("the iterator is exhaulted");
            }
            return table[cur++];
        }

        /**
         * Doesn't support this method
         */
        public void remove() {
            throw new UnsupportedOperationException("not supported operation");
        }
    }

    /**
     * Set the information of a specified row.
     * 
     * @param row the line number of the specified row
     * @param leftPoint the left number of the interval
     * @param rightPoint the right number of the interval
     * @param rate tax rate
     */
    public void setRow(int row, double leftPoint, double rightPoint, double rate) {
        table[row-1].set(row, leftPoint, rightPoint, rate);
    }

    /**
     * Set rate of a row
     * 
     * @param row the level that will be changed
     * @param rate new rate value
     */
    public void setRate(int row, double rate) {
        table[row-1].setRate(rate);
    }

    /**
     * Check whether this table is valid.
     * 
     * @return true if this table is valid
     */
    public boolean isValid() {
        return checkRange() && checkAdjacent() && checkRate();
    }

    /**
     * Get the default table, mainly for test.
     * 
     * @return get the default table
     */
    public static TaxRateTable getDefaultTable() {
        return new TaxRateTable(defaultTable, 1600, 2017);
    }

    /**
     * Check whether the interval of every row is valid.
     * 
     * @return true if the interval is valid
     */
    private boolean checkRange() {
        for (RateTableRow row : table) {
            if (row.getLeft() >= row.getRight()) {
                return false;
            }
        }
        return table[0].getLeft() == 0 && Double.isInfinite(table[table.length-1].getRight());
    }

    /**
     * Check whether the left point of the ith row's interval equals to
     * counterpart of the (i-1)th row's.
     * 
     * @return true if the adjacent rows are valid
     */
    private boolean checkAdjacent() {
        for (int i = 1; i < table.length; ++i) {
            if (table[i].getLeft() != table[i-1].getRight()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether rate is less than 1.
     * 
     * @return true if every rate is less than 1
     */
    private boolean checkRate() {
        for (RateTableRow row : table) {
           if (row.getRate() >= 1) {
               return false;
           }
        }
        return true;
    }

    /** 
     * The number of levels of this table.
     */
    private final int numLevels;

    /**
     * The exemption of this table.
     */
    private double exemption;

    /**
     * Default talbe mainly used to test
     */
    private static final RateTableRow[] defaultTable = {
            new RateTableRow(1, 0, 500, 0.05),
            new RateTableRow(2, 500, 2000, 0.1),
            new RateTableRow(3, 2000, 5000, 0.15),
            new RateTableRow(4, 5000, 20000, 0.2),
            new RateTableRow(5, 20000, Double.POSITIVE_INFINITY, 0.25),
    };

    /**
     * Array of RateTableRow used to consist of this table.
     */
    private RateTableRow[] table;
}
