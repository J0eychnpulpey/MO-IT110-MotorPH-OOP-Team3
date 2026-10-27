package motorphemployee.oop;

/**
 * PagIbigCalculator - Pag-IBIG tiered contribution with a PHP 100 cap
 * (MS1 Week 3 CRC: "PagIbigCalculator (Concrete Subclass)").
 *
 * Kept identical to CP2 (MainMenu.java:1396 / Dashboard.java:1312): employee share 1% for
 * 1,000-1,500 and 2% above 1,500, plus the employer share, capped at 100.
 */
public class PagIbigCalculator extends GovernmentContributionCalculator {

    private static final double LOWER_BAND_MIN = 1000.0;
    private static final double LOWER_BAND_MAX = 1500.0;
    private static final double EMPLOYEE_RATE_LOW = 0.01;
    private static final double EMPLOYER_RATE_LOW = 0.02;
    private static final double EMPLOYEE_RATE_HIGH = 0.02;
    private static final double EMPLOYER_RATE_HIGH = 0.02;
    private static final double MAX_CONTRIBUTION = 100.0;

    public PagIbigCalculator() {
        super("Pag-IBIG Contribution", "PAGIBIG");
    }

    @Override
    public double calculate(double grossSalary) {
        double salary = requireValidSalary(grossSalary);

        double employeeRate = 0.0;
        double employerRate = 0.0;

        if (salary >= LOWER_BAND_MIN && salary <= LOWER_BAND_MAX) {
            employeeRate = EMPLOYEE_RATE_LOW;
            employerRate = EMPLOYER_RATE_LOW;
        } else if (salary > LOWER_BAND_MAX) {
            employeeRate = EMPLOYEE_RATE_HIGH;
            employerRate = EMPLOYER_RATE_HIGH;
        }

        double totalRate = employeeRate + employerRate;
        return Math.min(salary * totalRate, MAX_CONTRIBUTION);
    }
}
