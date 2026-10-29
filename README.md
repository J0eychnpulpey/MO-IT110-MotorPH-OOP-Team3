# MotorPH Payroll System — OOP Refactor

**MO-IT110 — Object-Oriented Programming** · Term 1, SY 2026–2027 · Section **S2101** · **Team 3**

| Team member |
|---|
| John Paul Pore |
| Jan Fermin Valencia |
| Ethan Caleb Racimo |
| Myles Tumbaga |

---

## What this repository is

The **refactor of the MotorPH Payroll System** — from a procedural Java + CSV application (the MO-IT103 Computer Programming 2 output) into a well-designed **object-oriented** system, following the approved **Milestone 1** design and applying the four core OOP principles: **encapsulation, abstraction, inheritance, and polymorphism**.

This repository is the **single source of truth** for the OOP implementation: source code, worksheet revisions, console/smoke test evidence, and the final documentation deliverable.

---

## Baseline — the CP2 application being refactored

The current `src/motorphemployee/` package, `nbproject/`, `build.xml`, and the CSV data files are the **CP2 baseline** (procedural, unmodified):

- **Source project:** MO-IT103 | Computer Programming 2 | **A1101** | **Group 4**
  *Members: John Paul P., Eliakim Set, El Chad Chavez, Emersson Aporado, Nesty Loy*
- **Original repository:** <https://github.com/eliakimset/MO-IT103-A1101-CP2-Group-4>
- **Baseline copy:** <https://github.com/J0eychnpulpey/MO-IT103-A1101-CP2-Group-4>

### Baseline characteristics (as documented in the Milestone 1 design)

| Finding | Location |
|---|---|
| God classes holding UI **and** payroll logic | `MainMenu.java` (~1.6k LOC), `Dashboard.java` (~1.4k LOC) |
| Duplicated payroll computation (same 4 `calculate*()` methods, twice) | `MainMenu.java` (1279, 1378, 1396, 1418) and `Dashboard.java` (1195, 1294, 1312, 1334) |
| Employee fields are private, but **setters accept any value** (e.g. `setBasicSalary(-5000)` passes) | `Employee.java` (19 fields) |
| **Plaintext passwords** stored in the data layer | `HRLogin.csv`, `data.csv` |
| Data storage: CSV files loaded/saved per module | `data.csv`, `leaves.csv`, `pendingleaves.csv`, `HRLogin.csv` |

---

## Deliverable status

| Deliverable | Status |
|---|---|
| **MS1** — MotorPH OOP Design Package | ✅ Complete — 16 unique classes (35 CRC rows · 97 method rows) across W2–W5 |
| **MS2** — MotorPH OOP Implementation Package | ✅ Code complete — 13 files in `oop/src/motorphemployee/oop/` (12 classes + console harness) · `javac -Xlint:all` 0 warnings / 0 errors · console suite **91 of 91 checks pass** · GUI rewired to `PayrollCalculator` (8 duplicated `calculate*` methods removed) · smoke test **5 of 5** · gross pay unchanged against the CP2 baseline (**408 of 408** employee-months, 0 differences) |
| **TA** — MotorPH Documentation | ⏳ External QA + final documentation |

---

## Planned class design (per approved MS1 design)

| Week | Principle | Class groups |
|---|---|---|
| W2 | Encapsulation | `Employee`, `PayrollCalculator`, `AttendanceLog`, `LeaveManagement`, `Credentials`, `HRManager` |
| W3 | Abstraction | `Employee` (abstract base), `RegularEmployee`, `ProbationaryEmployee`, `GovernmentContributionCalculator` (abstract) + `SSSCalculator`, `PhilHealthCalculator`, `PagIbigCalculator`, `WithholdingTaxCalculator`, `PayrollCalculator` |
| W4 | Inheritance | `Person` → `Employee` → `RegularEmployee` \| `ProbationaryEmployee`; calculator hierarchy |
| W5 | Polymorphism | Parent-type references (`Employee`, `GovernmentContributionCalculator`) dispatch overridden behavior at runtime; one loop for all four calculators |

Structure rules for the implementation:

1. **GUI = presentation only.** All payroll logic moves out of the event handlers into the backend classes.
2. **Preserve the required CP2 payroll functionality** — every existing feature must still work.
3. Console-test the backend **before** GUI integration.

---

## Running the project

📘 **Full setup instructions — VS Code, Eclipse and terminal: [`SETUP.md`](SETUP.md)**

The repository opens directly in **Eclipse** (`.project` / `.classpath` are included) and in **VS Code** (Extension Pack for Java).

- **Two source roots:** `src` (CP2 GUI baseline) and `oop/src` (new OOP backend)
- Main entry point: `motorphemployee.LoadingScreen` → `LandingPage` → `Login`
- Console test harness: `motorphemployee.oop.ConsoleBackendTest`
- Sample login (fictional MotorPH data): employee number `1-34`, username `admin`, password `password`

> ⚠️ The credentials above are the CP2 baseline's **demo/sample data** — fictional employee records for the course. The MS1 design defines `Credentials.authenticate` / `changePassword` (hashed storage); that migration is not part of this refactor and is recorded as an open item in the MS2 Known Issues list, so the sample data is unchanged.

---

## What is in the OOP backend

| Path | Content |
|---|---|
| `oop/src/motorphemployee/oop/` | 12 backend classes + the `ConsoleBackendTest` harness |
| `oop/console-test-output.txt` | Console suite output — 91 of 91 checks pass |
| `oop/cp2-parity-output.txt` | The CP2 baseline output the refactor is compared against |
| `oop/parity-report.txt` | Gross pay for all 34 employees × 12 months — 408 comparisons, 0 differences |
| `oop/Q4-matrix-verification.md` | The four statutory deductions checked against the official government matrices |
| `oop/NOTES.md` | Scope, documented deviations from the CP2 formulas, and open items |

**Documented deviation:** the four statutory deductions follow the official government matrices. Where that differs from the CP2 formulas the difference is intentional and documented in `oop/NOTES.md` and in the MS2 Known Issues list — gross pay itself is unchanged for all 408 employee-months.

---

## Repository conventions

- **Branches:** `main` carries the refactor chain for this milestone; each step is built and tested before it is pushed.
- **Commits:** short imperative subject + what changed (`Add Employee setter validation`).
- **Never commit:** `build/`, `bin/`, `dist/`, `*.class`, `nbproject/private/`, `.vscode/` (local editor settings).
- **Committed on purpose:** `.project`, `.classpath`, `.settings/` — portable Eclipse project settings so every member can import the repository in one step.
- **Documentation consistency:** the app, the Milestone worksheet, the test evidence, and this repository must tell the **same story**.

---

*Baseline code © the original MO-IT103 Group 4 authors; reused here as the required refactoring source for MO-IT110 (the original README is preserved in [`README.CP2.md`](README.CP2.md)).*
