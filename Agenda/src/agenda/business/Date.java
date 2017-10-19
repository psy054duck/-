package agenda.business;

/**
 * Used to represent dates.
 */
public class Date implements Comparable<Date> {
    static private int[] dayOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

    /**
     * Constructor with detailed information.
     * 
     * @param pYear year of this date
     * @param pMonth month of this date
     * @param pDay day of this date
     * @param pHour hour of this date
     * @param pMinute minute of this date
     */
    public Date (int pYear, int pMonth, int pDay, int pHour, int pMinute) {
        if (!isValid(pYear, pMonth, pDay, pHour, pMinute)) {
            throw new IllegalArgumentException("Illegal date");
        }

        year = pYear;
        month = pMonth;
        day = pDay;
        hour = pHour;
        minute = pMinute;       
    }

    /**
     * Implement interface 'Comparable'
     * 
     * @param other right hand operand
     * @return -1 if this is earlier than other
     *          1 if this is later than other
     *          0 if this is the same day as other
     */
    public int compareTo(Date other) {
        if (year < other.year) {
            return -1;
        } else if (year > other.year) {
            return 1;
        } else if (month < other.month) {
            return -1;
        } else if (month > other.month) {
            return 1;
        } else if (day < other.day) {
            return -1;
        } else if (day > other.day) {
            return 1;
        } else if (hour < other.hour) {
            return -1;
        } else if (hour > other.hour) {
            return 1;
        } else if (minute < other.minute) {
            return -1;
        } else if (minute > other.minute) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Represent this Date if the format of 'yyyy/mm/dd hh:mm'.
     * 
     * @return the corresponding string in the format as described above.
     */
    public String toString() {
        return String.format("%4d/%02d/%02d/%02d:%02d",
                             year, month, day, hour, minute);
    }
    
    /**
     * Judge whether the given year is a leap year.
     * 
     * @param pYear the year to be judged.
     * 
     * @return true if pYear is a leap year.
     */
    static private boolean isLeapYear(int pYear) {
        return pYear % 400 == 0 || (pYear % 4 == 0 && pYear % 100 != 0);
    }

    /**
     * Judge whether the given parameters are valid to construct a Date
     * 
     * @param pYear year
     * @param pMonth month
     * @param pDay day
     * @param pHour hour
     * @param pMinute minute
     * 
     * @return true if the parameters is valid.
     */
    static private boolean isValid(int pYear, int pMonth,
                                   int pDay, int pHour, int pMinute) {
        if (pYear < 1000 || pYear > 9999) {
            return false;
        }
        if (pMonth < 1 || pMonth > 12) {
            return false;
        }
        if (pHour < 0 || pHour > 23) {
            return false;
        }
        if (pMinute < 0 || pMinute > 59) {
            return false;
        }
        if (isLeapYear(pYear)) {
            dayOfMonth[1] = 29;
        } else {
            dayOfMonth[1] = 28;
        }
        if (pDay < 1 || pDay > dayOfMonth[pMonth-1]) {
            return false;
        }
        return true;
    }

    /**
     * Convert a String into Date.
     * 
     * @param sDate the string to be converted
     * 
     * @return date specified by sDate
     */
    public static Date stringToDate(String sDate) {
        String[] components = sDate.split("[/|:]");
        if (components.length != 5) {
            throw new IllegalArgumentException("Date format: yyyy/mm/dd/hh:mm");
        }
        return new Date(Integer.valueOf(components[0]),
                        Integer.valueOf(components[1]),
                        Integer.valueOf(components[2]),
                        Integer.valueOf(components[3]),
                        Integer.valueOf(components[4]));
    }
}
