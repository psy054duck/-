package business;
import java.io.Serializable;

/**
 * Row of a rate table.
 */
public class RateTableRow implements Serializable {

    /**
     * Implements the serializable.
     */
    private static final long serialVersionUID = 1;

    /**
     * The level this row belongs to.
     */
    private int level;

    /**
     * Left endpoint of the taxable interval.
     */
    private double leftPoint;

    /**
     * Right endpoint of the taxable interval.
     */
    private double rightPoint;

    /**
     * Tax Rate of this level.
     */
    private double rate;

    /**
     * Quick deduction of this level.
     */
    private double deduction;

    /**
     * Default constructor which sets all attributes to default value.
     */
    public RateTableRow() {
        level = 0;
        leftPoint = 0;
        rightPoint = 0;
        rate = 0;
    }

    /**
     * Constructor with detailed information.
     * 
     * @param level the level that this row belongs to
     * @param leftPoint left point of the interval
     * @param rightPoint right point of the interval
     * @param rate the tax rate of this row
     */
    RateTableRow(int level, double leftPoint, double rightPoint, double rate) {
        this.level = level;
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
        this.rate = rate;
    }

    /**
     * Getter of level.
     * 
     * @return level of this row
     */
    public int getLevel() { return level; }

    /**
     * Getter of left endpoint.
     * 
     * @return left point of this row's interval
     */
    public double getLeft() { return leftPoint; }

    /**
     * Getter of right endpoint.
     * 
     * @return right point of this row's interval
     */
    public double getRight() { return rightPoint; }

    /**
     * Getter of rate.
     * 
     * @return rate of this row
     */
    public double getRate() { return rate; }
    /**
     * Getter of quick deduction.
     * 
     * @return quick deduction of this row
     */
    public double getDeduction() { return deduction; }
    
    /**
     * Set the information of this row.
     * 
     * @param level level of this row
     * @param leftPoint left point of this row's interval
     * @param rightPoint right point of this row's interval
     * @param rate rate of this row
     */
    public void set(int level, double leftPoint, double rightPoint, double rate) {
        setLevel(level);
        setLeft(leftPoint);
        setRight(rightPoint);
        setRate(rate);
    }

    /**
     * Setter of level.
     * 
     * @param num value that you want to set
     */
    public void setLevel(int num) { level = num ; }

    /**
     * Setter of left endpoint.
     * 
     * @param num value that you want to set
     */
    public void setLeft(double num) { leftPoint = num; }

    /**
     * Setter of right endpoint.
     * 
     * @param num value that you want to set
     */
    public void setRight(double num) { rightPoint = num; }

    /**
     * Setter of rate.
     * 
     * @param num value that you want to set
     */
    public void setRate(double num) { rate = num; }

    /**
     * Setter of quick deduction.
     * 
     * @param num value that you want to set
     */
    public void setDeduction(double num) { deduction = num; }
}
