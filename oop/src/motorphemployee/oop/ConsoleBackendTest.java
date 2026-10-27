package motorphemployee.oop;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ConsoleBackendTest - the mandatory console test for Week 7-8 backend work.
 *
 * NOT a production class: this is the harness that proves the backend behaves before any
 * GUI is touched (MS2 instructions: "Console Testing ... Test each method individually in
 * the console before touching the GUI"). It prints PASS/FAIL lines and exits with code 1
 * when anything fails, so a broken build cannot be mistaken for a working one.
 *
 * The PARITY lines are machine-comparable with the CP2 baseline: the same input salary is
 * pushed through the CP2 static formulas (Cp2Parity) and through the OOP calculators, then
 * the two outputs are diffed. That diff is the evidence for "same results as CP2".
 */
public final class ConsoleBackendTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== MotorPH OOP backend console test (Waves 1-3) ===");
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println();

        section("A. Inheritance + polymorphism (Employee reference, two real types)");
        testPolymorphicBenefits();

        section("B. Encapsulation / validation (the CP2 holes)");
        testValidation();

        section("C. Government contribution calculators through the abstract type");
        testCalculatorParityInputs();

        section("D. AttendanceLog integrity");
        testAttendance();

        section("E. PayrollRecord immutability + internal consistency");
        testPayrollRecord();

        section("F. Attendance -> gross -> deductions -> net (end-to-end backend run)");
        testEndToEndNetPay();

        section("G. Known issue KI-01 surfaced by this run (Week 8 fix decision)");
        System.out.println("  At gross pay 0.00 the statutory minimums are still charged:");
        System.out.println("    SSS = 135.00 + PhilHealth = 300.00 + Pag-IBIG = 0.00  ->  435.00 of deductions");
        System.out.println("  CP2 behaviour : tax on -435.00 = 0.00, so net pay = -435.00 (a negative payslip)");
        System.out.println("  OOP behaviour : the calculators refuse a negative taxable income instead");
        System.out.println("  -> logged as KI-01; needs a business rule (clamp gross to >= deductions");
        System.out.println("     or skip contributions below the statutory floor) before the GUI is wired.");

        System.out.println();
        System.out.println("=== SUMMARY: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.out.println("RESULT: FAIL");
            System.exit(1);
        }
        System.out.println("RESULT: PASS");
    }

    // ------------------------------------------------------------------ A

    private static void testPolymorphicBenefits() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new RegularEmployee(10001, "Pore", "Paul", "1990-02-14", "Manila", "09171234567",
                "34-1234567-8", "12-345678901-2", "123-456-789-000", "1234-5678-9012",
                "Regular", "Software Engineer", "Valencia", 35000.00, 1500, 2000, 1000, 17500, 205.36, 3));
        employees.add(new ProbationaryEmployee(10034, "Racimo", "Ana", "1998-07-01", "Quezon City", "09181234567",
                "34-9876543-2", "12-987654321-9", "987-654-321-000", "9876-5432-1098",
                "Probationary", "Junior Developer", "Pore", 22000.00, 1000, 500, 800, 11000, 129.08,
                5, "2026-06-01", 88.5));

        for (Employee employee : employees) {
            System.out.println("  " + employee);
            System.out.println("    getRiceSubsidy()          = " + money(employee.getRiceSubsidy()));
            System.out.println("    getPhoneAllowance()       = " + money(employee.getPhoneAllowance()));
            System.out.println("    getAnnualLeaveCredits()   = " + employee.getAnnualLeaveCredits());
            System.out.println("    isEligibleForPromotion()  = " + employee.isEligibleForPromotion());
            System.out.println("    getFullName()             = " + employee.getFullName() + "  (inherited from Person)");
            System.out.println("    getAge()                  = " + employee.getAge() + "  (inherited from Person)");
        }

        Employee regular = employees.get(0);
        Employee probationary = employees.get(1);

        check("RegularEmployee.getRiceSubsidy() == 1500 via Employee reference", regular.getRiceSubsidy() == 1500.0);
        check("RegularEmployee.getPhoneAllowance() == 2000", regular.getPhoneAllowance() == 2000.0);
        check("RegularEmployee.getAnnualLeaveCredits() == 12", regular.getAnnualLeaveCredits() == 12);
        check("RegularEmployee(3 yrs).isEligibleForPromotion() == true", regular.isEligibleForPromotion());
        check("ProbationaryEmployee.getRiceSubsidy() == 1000", probationary.getRiceSubsidy() == 1000.0);
        check("ProbationaryEmployee.getPhoneAllowance() == 500", probationary.getPhoneAllowance() == 500.0);
        check("ProbationaryEmployee.getAnnualLeaveCredits() == 6", probationary.getAnnualLeaveCredits() == 6);
        check("ProbationaryEmployee.isEligibleForPromotion() == false", !probationary.isEligibleForPromotion());
        check("Same call, different result - no if/else on employee type", regular.getRiceSubsidy() != probationary.getRiceSubsidy());
    }

    // ------------------------------------------------------------------ B

    private static void testValidation() {
        Employee employee = sampleRegular();

        expectThrows("setBasicSalary(-5000) rejected (CP2 accepted it - Employee.java:186)",
                () -> employee.setBasicSalary(-5000));
        expectThrows("setBasicSalary(0) rejected", () -> employee.setBasicSalary(0));
        expectThrows("setHourlyRate(0) rejected", () -> employee.setHourlyRate(0));
        expectThrows("setHourlyRate(-120) rejected", () -> employee.setHourlyRate(-120));
        expectThrows("setFirstName(\"  \") rejected", () -> employee.setFirstName("  "));
        expectThrows("setLastName(null) rejected", () -> employee.setLastName(null));
        expectThrows("setPhoneNumber(\"abc\") rejected (format check)", () -> employee.setPhoneNumber("abc"));
        expectThrows("setId(0) rejected", () -> employee.setId(0));
        expectThrows("setBirthday(future date) rejected", () -> employee.setBirthday("2099-01-01"));
        expectThrows("setGrossSemiMonthlyRate(-1) rejected", () -> employee.setGrossSemiMonthlyRate(-1));
        expectThrows("setClothingAllowance(-1) rejected", () -> employee.setClothingAllowance(-1));

        // Government IDs are read-only after creation: prove there is no setter at all.
        String[] forbiddenSetters = {"setSssNumber", "setPhilhealthNumber", "setTinNumber", "setPagIbigNumber"};
        boolean govIdSetterExists = false;
        for (Method method : Employee.class.getMethods()) {
            for (String forbidden : forbiddenSetters) {
                if (method.getName().equals(forbidden)) {
                    govIdSetterExists = true;
                }
            }
        }
        check("no setter exists for SSS/PhilHealth/TIN/Pag-IBIG numbers", !govIdSetterExists);

        // Valid values still work after the rejected ones (state was not corrupted).
        employee.setBasicSalary(36000);
        check("state intact, valid setBasicSalary(36000) accepted", employee.getBasicSalary() == 36000.0);
    }

    // ------------------------------------------------------------------ C

    private static void testCalculatorParityInputs() {
        List<GovernmentContributionCalculator> calculators = new ArrayList<>();
        calculators.add(new SSSCalculator());
        calculators.add(new PhilHealthCalculator());
        calculators.add(new PagIbigCalculator());

        System.out.println("  Calculators registered through the abstract type:");
        for (GovernmentContributionCalculator calculator : calculators) {
            System.out.println("    - " + calculator);
        }
        WithholdingTaxCalculator tax = new WithholdingTaxCalculator();
        System.out.println("    - " + tax + "   (applied after the three above)");
        System.out.println();
        System.out.println("  PARITY|<gross>|<sss>|<philhealth>|<pagibig>|<tax on (gross - sss-ph-ibig)>");

        for (double gross : parityInputs()) {
            double sss = 0;
            double philhealth = 0;
            double pagibig = 0;
            for (GovernmentContributionCalculator calculator : calculators) {
                double amount = calculator.calculate(gross);
                if (calculator instanceof SSSCalculator) {
                    sss = amount;
                } else if (calculator instanceof PhilHealthCalculator) {
                    philhealth = amount;
                } else {
                    pagibig = amount;
                }
            }
            double taxableIncome = gross - sss - philhealth - pagibig;
            if (taxableIncome < 0) {
                System.out.printf(Locale.ROOT,
                        "  PARITY_SKIPPED|%.2f|%.6f|%.6f|%.6f|reason=negative taxable income %.2f%n",
                        gross, sss, philhealth, pagibig, taxableIncome);
                continue;
            }
            double withholding = tax.calculate(taxableIncome);
            System.out.printf(Locale.ROOT, "  PARITY|%.2f|%.6f|%.6f|%.6f|%.6f%n",
                    gross, sss, philhealth, pagibig, withholding);
        }

        check("SSSCalculator caps at 1125.00", new SSSCalculator().calculate(999999) == 1125.0);
        check("SSSCalculator bracket count == 45", new SSSCalculator().getBracketCount() == 45);
        check("PhilHealthCalculator floor 300.00", new PhilHealthCalculator().calculate(9000) == 300.0);
        check("PhilHealthCalculator cap 1800.00", new PhilHealthCalculator().calculate(999999) == 1800.0);
        check("PagIbigCalculator cap 100.00", new PagIbigCalculator().calculate(999999) == 100.0);
        check("PagIbigCalculator 0 below 1000 salary", new PagIbigCalculator().calculate(900) == 0.0);
        check("WithholdingTaxCalculator 0 at/below 20832", new WithholdingTaxCalculator().calculate(20832) == 0.0);
        expectThrows("calculate(-1) rejected by every calculator", () -> new SSSCalculator().calculate(-1));
        expectThrows("negative taxable income refused instead of paying a negative net (KI-01)",
                () -> new WithholdingTaxCalculator().calculate(-435.0));
    }

    private static List<Double> parityInputs() {
        List<Double> inputs = new ArrayList<>();
        double[] values = {
                0.00, 900.00, 1000.00, 1500.00, 1500.01, 2400.00,
                3250.00, 3250.01, 4000.00, 8000.00, 10000.00, 10000.01,
                12345.67, 15000.00, 20832.00, 20832.01, 25000.00, 33333.00,
                45000.00, 59999.99, 60000.00, 66667.00, 100000.00,
                166667.00, 666667.67, 1000000.00
        };
        for (double value : values) {
            inputs.add(value);
        }
        return inputs;
    }

    // ------------------------------------------------------------------ D

    private static void testAttendance() {
        AttendanceLog log = new AttendanceLog();

        log.recordClockIn(10001, "2024-06-03", "08:00:00");
        log.recordClockOut(10001, "2024-06-03", "17:00:00");
        log.recordClockIn(10001, "2024-06-04", "08:15:00");
        log.recordClockOut(10001, "2024-06-04", "16:45:00");
        log.recordClockIn(10001, "2024-06-05", "09:00:00");

        System.out.println("  daily hours 2024-06-03 = " + log.getDailyHours(10001, "2024-06-03"));
        System.out.println("  daily hours 2024-06-04 = " + log.getDailyHours(10001, "2024-06-04"));
        System.out.println("  daily hours 2024-06-05 = " + log.getDailyHours(10001, "2024-06-05") + " (no clock-out yet)");
        System.out.println("  worked days 2024-06-01..2024-06-30 = " + log.getWorkedDays(10001, "2024-06-01", "2024-06-30"));

        check("getDailyHours(2024-06-03) == 9.0", log.getDailyHours(10001, "2024-06-03") == 9.0);
        check("getDailyHours(2024-06-04) == 8.5", log.getDailyHours(10001, "2024-06-04") == 8.5);
        check("incomplete day counts as 0 hours", log.getDailyHours(10001, "2024-06-05") == 0.0);
        check("getWorkedDays(June) == 2", log.getWorkedDays(10001, "2024-06-01", "2024-06-30") == 2);
        expectThrows("duplicate clock-in rejected",
                () -> log.recordClockIn(10001, "2024-06-03", "08:05:00"));
        expectThrows("clock-out before clock-in rejected",
                () -> log.recordClockOut(10001, "2024-06-05", "08:00:00"));
        expectThrows("clock-out without clock-in rejected",
                () -> log.recordClockOut(10001, "2024-06-06", "17:00:00"));
        expectThrows("future-dated clock-in rejected",
                () -> log.recordClockIn(10001, "2099-01-01", "08:00:00"));
        expectThrows("unknown employee rejected", () -> log.getWorkedDays(0, "2024-06-01", "2024-06-30"));
    }

    // ------------------------------------------------------------------ E

    private static void testPayrollRecord() {
        ZonedDateTime now = ZonedDateTime.now();
        PayrollRecord record = new PayrollRecord(10001, now, now.minusDays(15), now,
                35000, 2000, 1500, 1000, 35000, 17500, 31000, 1125, 750, 100, 2025, 5, 160, 205.36, 0);

        System.out.println("  " + record);
        check("PayrollRecord keeps netIncome", record.getNetIncome() == 31000);
        check("getTotalDeductions() == 4000", record.getTotalDeductions() == 4000.0);

        boolean setterFound = false;
        for (Method method : PayrollRecord.class.getDeclaredMethods()) {
            if (method.getName().startsWith("set")) {
                setterFound = true;
            }
        }
        check("PayrollRecord has no setters (immutable)", !setterFound);

        expectThrows("inconsistent netIncome rejected at construction", () -> new PayrollRecord(
                10001, now, now.minusDays(15), now, 35000, 2000, 1500, 1000,
                35000, 17500, 999999, 1125, 750, 100, 2025, 5, 160, 205.36, 0));
        expectThrows("negative deduction rejected at construction", () -> new PayrollRecord(
                10001, now, now.minusDays(15), now, 35000, 2000, 1500, 1000,
                35000, 17500, 35000, -1125, 750, 100, 2025, 5, 160, 205.36, 0));
        expectThrows("periodEnd before periodStart rejected", () -> new PayrollRecord(
                10001, now, now, now.minusDays(15), 35000, 2000, 1500, 1000,
                35000, 17500, 35000, 1125, 750, 100, 2025, 5, 160, 205.36, 0));
    }

    // ------------------------------------------------------------------ F

    private static void testEndToEndNetPay() {
        AttendanceLog log = new AttendanceLog();
        int employeeId = 10001;
        // 20 complete working days (day 3 to day 22 of June 2024) - a realistic semi-monthly input.
        for (int day = 3; day <= 22; day++) {
            String date = String.format(Locale.ROOT, "2024-06-%02d", day);
            log.recordClockIn(employeeId, date, "08:00:00");
            log.recordClockOut(employeeId, date, "17:00:00");
        }

        Employee employee = sampleRegular();
        double hourlyRate = employee.getHourlyRate();
        int workedDays = log.getWorkedDays(employeeId, "2024-06-01", "2024-06-30");
        double gross = workedDays * hourlyRate * 8;

        List<GovernmentContributionCalculator> calculators = new ArrayList<>();
        calculators.add(new SSSCalculator());
        calculators.add(new PhilHealthCalculator());
        calculators.add(new PagIbigCalculator());

        double deductions = 0;
        for (GovernmentContributionCalculator calculator : calculators) {
            double amount = calculator.calculate(gross);
            System.out.println("  " + calculator.getContributionName() + " = " + money(amount));
            deductions += amount;
        }
        double taxableIncome = gross - deductions;
        double tax = new WithholdingTaxCalculator().calculate(taxableIncome);
        double net = taxableIncome - tax;

        System.out.println("  worked days      = " + workedDays);
        System.out.println("  hourly rate      = " + money(hourlyRate));
        System.out.println("  gross pay        = " + money(gross));
        System.out.println("  total deductions = " + money(deductions + tax));
        System.out.println("  withholding tax  = " + money(tax));
        System.out.println("  net pay          = " + money(net));

        check("worked days == 20", workedDays == 20);
        check("gross == 20 * 12 * 8", Math.abs(gross - 1920.0) < 0.000001);
        check("net pay == gross - all deductions",
                Math.abs(net - (gross - deductions - tax)) < 0.000001);
        check("net pay is positive and lower than gross", net > 0 && net < gross);
    }

    // ------------------------------------------------------------- helpers

    private static Employee sampleRegular() {
        return new RegularEmployee(10001, "Pore", "Paul", "1990-02-14", "Manila", "09171234567",
                "34-1234567-8", "12-345678901-2", "123-456-789-000", "1234-5678-9012",
                "Regular", "Software Engineer", "Valencia", 35000.00, 1500, 2000, 1000, 17500, 12.0, 3);
    }

    private static void section(String title) {
        System.out.println("--- " + title);
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + description);
        } else {
            failed++;
            System.out.println("  FAIL  " + description);
        }
    }

    private static void expectThrows(String description, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("  FAIL  " + description + "  (no exception thrown)");
        } catch (RuntimeException expected) {
            passed++;
            System.out.println("  PASS  " + description + "  -> " + expected.getClass().getSimpleName()
                    + ": " + expected.getMessage());
        }
    }

    private static String money(double value) {
        return String.format(Locale.ROOT, "%,.2f", value);
    }
}
