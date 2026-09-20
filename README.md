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

## Milestone status

| Milestone | Weight | Due | Status |
|---|---|---|---|
| **MS1** — MotorPH OOP Design Package | 20% | Oct 5, 2026 | ✅ Design complete — 33 classes across W2–W5 (CRC Cards, Method Dictionary, Architecture Overview) |
| **MS2** — MotorPH OOP Implementation Package | 40% | Target Week 10 | ⏳ Not started — refactoring begins Week 7 (Refactoring Plan + backend) |
| **TA** — MotorPH Documentation | 40% | Nov 13, 2026 | ⏳ External QA + final documentation |

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

## Running the baseline

NetBeans / Ant project (`build.xml`, `nbproject/`).

- Main entry point: `motorphemployee.LandingPage`
- Sample login from the CP2 baseline: employee number `1-34`, username `admin`, password `password`

> ⚠️ The credentials above are the CP2 baseline's **demo/sample data**. The refactor replaces plaintext credential storage with hashed storage (see the `Credentials` class in the MS1 design).

---

## Repository conventions

- **Branches:** `main` = stable; work in feature branches, merge via pull request.
- **Commits:** short imperative subject + what changed (`Add Employee setter validation`).
- **Never commit:** `build/`, `dist/`, `nbproject/private/`, IDE-local files.
- **Documentation consistency:** the app, the Milestone worksheet, the test evidence, and this repository must tell the **same story**.

---

*Baseline code © the original MO-IT103 Group 4 authors; reused here as the required refactoring source for MO-IT110 (the original README is preserved in [`README.CP2.md`](README.CP2.md)).*
