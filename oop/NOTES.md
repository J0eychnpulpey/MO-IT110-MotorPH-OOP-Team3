# MS2 Backend — Waves 1–3 (Employee hierarchy + contribution calculators)

> **Built:** 2026-10-26 (Mon) · backend only, **no GUI touched** (per Module 7: GUI is Week 9)
> **Design source:** `MS1 Method Dictionary + CRC Cards` (graded 35/35) — every class and method
> name below exists in the approved design. No invented identifiers.
> **Verified:** `javac` 21.0.12, `-Xlint:all`, **0 warnings, 0 errors** · console harness **50/50 PASS** ·
> CP2 parity **25/25 exact matches, 0 mismatches**.

## What is implemented (Wave 1–3 of the 5-wave build order)

| Class | Type | S1 methods implemented |
|---|---|---|
| `Person` | base class | `getFullName`, `getAge`, `updateAddress`, `updatePhoneNumber`, `toString` + validated personal setters |
| `Employee` | **abstract**, extends `Person` | 19-param constructor, `getId/setId`, gov-ID getters (read-only), `getStatus/setStatus`, `getPosition/setPosition`, `getImmediateSupervisor/set…`, `getBasicSalary/set…`, `getClothingAllowance/set…`, `getGrossSemiMonthlyRate/set…`, `getHourlyRate/set…` + 4 abstract benefit methods |
| `RegularEmployee` | subclass | `getRiceSubsidy` (1500), `getPhoneAllowance` (2000), `getAnnualLeaveCredits` (12), `isEligibleForPromotion` (yearsOfService ≥ 2) |
| `ProbationaryEmployee` | subclass | same 4 overrides with 1000 / 500 / 6 / false |
| `GovernmentContributionCalculator` | **abstract** | `calculate(double)` (abstract), `getContributionName` |
| `SSSCalculator` | subclass | `calculate` — 45 brackets, cap 1,125.00 |
| `PhilHealthCalculator` | subclass | `calculate` — 300 floor, 3% of excess, 1,800 cap |
| `PagIbigCalculator` | subclass | `calculate` — 3% / 4% tiers, 100 cap |
| `WithholdingTaxCalculator` | subclass | `calculate` — 6 BIR brackets, up to 35% |
| `PayrollRecord` | **immutable** value object | 19-param constructor + 19 getters, **no setters** |
| `AttendanceLog` | helper | `recordClockIn`, `recordClockOut`, `getDailyHours`, `getWorkedDays` |
| `ConsoleBackendTest` | test harness (not production) | 50 assertions, prints PASS/FAIL, exit code 1 on failure |

**Not built yet (next waves):** `PayrollCalculator` (Wave 4 — the orchestrator), `Credentials`,
`LeaveRequest`, `LeaveManagement`, `HRManager`, and the whole GUI rewiring (Wave 5).

## Documented adjustments (why the code differs from the sheet)

1. **`Employee` keeps the exact 19-param constructor from the Method Dictionary**, but
   `riceSubsidy` / `phoneAllowance` values are validated and then *superseded* by the
   polymorphic overrides introduced in the Week 3 abstraction design. Keeping the signature
   preserved compatibility with how `data.csv` is read; the override is the source of truth.
2. **`Person` owns the 5 personal fields.** `Employee` inherits `getFirstName`, `setFirstName`,
   `getFullName`, etc. instead of re-declaring them (the Method Dictionary lists `getFullName` on
   both rows — inheritance makes that one implementation, not two).
3. **`AttendanceLog` stores records in memory.** Open question **Q3** in the Refactoring Plan: CP2
   hardcoded `workingDays[]` (`MainMenu.java:1066`) and no timecard CSV has been imported, so no
   attendance data was invented. The API is CSV-ready — an adapter needs no change to callers.
4. **Constructors assign validated values directly** instead of calling their own setters, so a
   subclass never observes a half-constructed object (`-Xlint:this-escape` clean).

## Known issues found by the console run

| ID | Issue | Evidence | Status |
|---|---|---|---|
| **KI-01** | At gross pay **0.00** the statutory minimums still apply (SSS 135 + PhilHealth 300 = 435), so taxable income is **-435.00**. CP2 silently produced **net pay -435.00** (a negative payslip). The OOP calculators now refuse a negative taxable income instead of paying it. | `cp2-parity-output.txt` line `NET\|0.00\|435.000000\|-435.000000` | ❓ needs a business rule (clamp, or skip contributions below the statutory floor) — decide in Week 8, then log in the Known Issues tab |
| **KI-02** | PhilHealth in CP2 is `300 + 3% of the excess over 10,000`, not `3% of salary`. MS1 describes it as "3% premium, min 300, max 1800". Both the OOP class and CP2 behave identically, but neither has been checked against `Home_Philhealth_Matrix.xlsx` (open question **Q4**). | `PhilHealthCalculator.java`, `MainMenu.java:1378` | ❓ verify against the official matrix before the final submission |
| **KI-03** | Pag-IBIG in CP2 charges **employee share + employer share** (3%/4% total, not the employee's 1%/2%). Carried over unchanged. | `MainMenu.java:1396–1414` | ❓ verify against `Home_PagIBIG_Matrix.xlsx` |

## How to build and run (verified commands)

```bash
# compile the backend
javac -encoding UTF-8 -Xlint:all -d <outdir> \
      C:/Users/Paul/Documents/MO-IT110-MotorPH-OOP-Team3/oop/src/motorphemployee/oop/*.java

# run the console test (exit code 0 = all pass)
java -cp <outdir> motorphemployee.oop.ConsoleBackendTest
```

Evidence files in this folder: `console-test-output.txt` (the actual run),
`parity-report.txt` (CP2 vs OOP, per input), `cp2-parity-output.txt` (frozen CP2 formulas).
