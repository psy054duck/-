package business;

/**
 * Tax calculator
 */
public class Calculator {
    public Calculator() {}

    /**
     * Calculate tax according to the table that is currently used.
     * 
     * @param income income used to calculate corresponding tax
     * 
     * @return taxes should be paid for the income
     * 
     * @throws IllegalArgumentException if income is negative
     * @throws Exception if something unexpected occur.
     */
    public double calculateTax(double income) throws Exception {
        if (income < 0) {
            throw new IllegalArgumentException(
                          "Your income shouldn't be negative");
        }
        TaxRateTable table = TableManager.getInstance().getTable();
        double taxable = income - table.getExemption();
        taxable = (taxable < 0) ? 0 : taxable;
        for (RateTableRow i : table) {
            if (i.getLeft() <= taxable && taxable < i.getRight()) {
                return taxable * i.getRate() - i.getDeduction();
            }
        }
        throw new Exception(
            "Couldn't find a suitable range in the tax table to calculate tax");
    }

}
