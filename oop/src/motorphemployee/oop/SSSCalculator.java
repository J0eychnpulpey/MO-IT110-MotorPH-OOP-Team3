package motorphemployee.oop;

/**
 * SSSCalculator - SSS contribution using the official 45-bracket table, capped at PHP 1,125
 * (MS1 Week 3 CRC: "SSSCalculator (Concrete Subclass)").
 *
 * The bracket table is private: outside code only sees calculate(). The table is identical
 * to the CP2 implementation that lived in both MainMenu.java:1279 and Dashboard.java:1195 -
 * lower bound 3,250 stepping by 500, contribution 135.00 stepping by 22.50, max 1,125.00.
 */
public class SSSCalculator extends GovernmentContributionCalculator {

    private static final double FIRST_LOWER_BOUND = 3250.0;
    private static final double LOWER_BOUND_STEP = 500.0;
    private static final double FIRST_AMOUNT = 135.0;
    private static final double AMOUNT_STEP = 22.5;
    private static final int BRACKETS = 44;              // + the "above the table" bracket = 45
    private static final double MAX_CONTRIBUTION = 1125.0;

    private static final double[] LOWER_BOUNDS = new double[BRACKETS];
    private static final double[] AMOUNTS = new double[BRACKETS];

    static {
        for (int i = 0; i < BRACKETS; i++) {
            LOWER_BOUNDS[i] = FIRST_LOWER_BOUND + LOWER_BOUND_STEP * i;
            AMOUNTS[i] = FIRST_AMOUNT + AMOUNT_STEP * i;
        }
    }

    public SSSCalculator() {
        super("SSS Contribution", "SSS");
    }

    @Override
    public double calculate(double grossSalary) {
        double salary = requireValidSalary(grossSalary);
        for (int i = 0; i < BRACKETS; i++) {
            if (salary <= LOWER_BOUNDS[i]) {
                return AMOUNTS[i];
            }
        }
        return MAX_CONTRIBUTION;
    }

    /** Read-only inspection of the table (used by the console test, never by payroll logic). */
    public int getBracketCount() {
        return BRACKETS + 1;
    }
}
