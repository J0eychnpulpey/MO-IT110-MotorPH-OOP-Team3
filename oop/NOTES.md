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

## Decision B — official matrix over CP2 (2026-10-27, decided by Paul)

The 4 statutory calculators now follow the **official government matrices**, not CP2. The Q4
verification (`Q4-matrix-verification.md`) proved CP2 is wrong in 4 places; those 4 fixes are
KI-02 … KI-05 below. Consequence: `parity-report.txt` is **no longer an all-MATCH file** — every
mismatch row in it is one of the documented deviations, and the proof that the new numbers are
correct lives in **section H** of `ConsoleBackendTest` (expected values copied from the matrix
cells, e.g. PhilHealth SAMPLE `B10` = 375, WHT SAMPLE `B20` = 513.40, SSS 3,250 = 157.50).

Impact on the 34-employee sample (basic salary used as the proxy for gross): PhilHealth
**34/34** employees (PHP 18,676.80/month over-deducted by CP2), WHT 18/34 (PHP 0.20 each),
SSS 4/34 (CP2 *under*-deducted PHP 22.50 each), Pag-IBIG 0/34 (all salaries are above the
point where the PHP 100 cap flattens the difference).

## Known issues found by the console run

| ID | Issue | Evidence | Status |
|---|---|---|---|
| **KI-01** | At gross pay **0.00** the statutory minimums still apply (SSS 135 + PhilHealth 150 = 285), so net pay would be **-285.00** (a negative payslip). The calculators refuse a negative taxable income instead. | `cp2-parity-output.txt` line `NET\|0.00\|435.000000\|-435.000000` (CP2, before the PhilHealth fix) | ❓ fix = decision **D3**: `PayrollCalculator.calculatePayroll` rejects a non-positive gross. Pending the P4 orchestrator. |
| **KI-02** | PhilHealth deducted **100% of the premium**; the official employee share is **50%** (`Home_Philhealth_Matrix.xlsx` note `A6`, SAMPLE `B10` = 375 on 25,000). Every payslip was over-deducted by 2x. | `Q4-matrix-verification.md` §2 | ✅ **RESOLVED** (B) — `PhilHealthCalculator` returns `premium / 2`; section H asserts 150 / 375 / 900 |
| **KI-03** | Pag-IBIG deducted **employee + employer share** (3%/4%); the official employee share is **1%/2%**, cap 100 (`B2:B3` vs `D2:D3`, note `A5`). | `Q4-matrix-verification.md` §3 | ✅ **RESOLVED** (B) — `PagIbigCalculator` uses the employee rate only; salary < 1,000 stays 0.00 and UNVERIFIED (no matrix row) |
| **KI-04** | Withholding tax bracket 1 used the excess base **20,832** instead of **20,833** (`B3`, SAMPLE `B20` = 513.40) → +PHP 0.20 over-withheld in [20,833, 33,333]. | `Q4-matrix-verification.md` §4 | ✅ **RESOLVED** (B) — `WithholdingTaxCalculator` uses 20,833 (and clamps the 20,832–20,833 gap so tax can never go negative) |
| **KI-05** | SSS brackets were **upper-inclusive**; the official schedule is **lower-inclusive** ("3,250 - 3,749.99"), so a salary exactly on a boundary was under-deducted by PHP 22.50. | `Q4-matrix-verification.md` §1 | ✅ **RESOLVED** (B) — `SSSCalculator` treats bracket *i* as `[lower, lower + 499.99)`; section H asserts 3,250 → 157.50 |

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
