package motorphemployee.oop;

/**
 * PhilHealthCalculator - PhilHealth premium: 3% rate with a PHP 300 floor and PHP 1,800 ceiling
 * (MS1 Week 3 CRC: "PhilHealthCalculator (Concrete Subclass)").
 *
 * Formula carried over 1:1 from CP2 (MainMenu.java:1378 / Dashboard.java:1294) so payroll
 * results stay identical: flat 300 up to 10,000, then 300 + 3% of the excess, capped at 1,800.
 */
public class PhilHealthCalculator extends GovernmentContributionCalculator {

    private static final double PREMIUM_RATE = 0.03;
    private static final double MIN_CONTRIBUTION = 300.0;
    private static final double MAX_CONTRIBUTION = 1800.0;
    private static final double LOWER_CAP_SALARY = 10000.0;
    private static final double UPPER_CAP_SALARY = 59999.99;

    public PhilHealthCalculator() {
        super("PhilHealth Contribution", "PH");
    }

    @Override
    public double calculate(double grossSalary) {
        double salary = requireValidSalary(grossSalary);
        if (salary <= LOWER_CAP_SALARY) {
            return MIN_CONTRIBUTION;
        }
        if (salary <= UPPER_CAP_SALARY) {
            return Math.min(MIN_CONTRIBUTION + (salary - LOWER_CAP_SALARY) * PREMIUM_RATE, MAX_CONTRIBUTION);
        }
        return MAX_CONTRIBUTION;
    }
}
