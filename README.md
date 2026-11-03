# 📚 StudyPlanner

**A Personal Study Task Management App**

StudyPlanner is an Android application developed as part of the **CSC402 – Mobile Application Programming** course at Imam Abdulrahman Bin Faisal University.

The application helps students organize their study tasks, track their progress, and stay focused on their academic goals.

## ✨ Features

- Personalized home screen with study progress tracking.
- Interactive task completion and progress indicator.
- Study task list with navigation to task details.
- Task sharing through the Android Share Sheet.
- Material 3 user interface.
- State-driven UI built with Jetpack Compose.

## 🛠 Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **UI Framework:** Jetpack Compose
- **Design System:** Material 3
- **Navigation:** Navigation Compose
- **Version Control:** Git & GitHub
- **Minimum SDK:** API 26

## 📌 Development Progress

| Lab | Implementation | Status |
|---|---|---|
| Lab 4 | Home Screen, UI State, Stateless Composables & Previews | ✅ Completed |
| Lab 5 | Task Details, Navigation Compose & Task Sharing | ✅ Completed |
| Lab 6 | No implementation in this project | — |
| Lab 7 | Part 4: reusable task rows, keyed LazyColumn, derived progress, saved state & previews | ✅ Code implemented and tested; paper sketch and submission pending |
| Lecture 8 Build Guide | Today screen, ViewModel, StateFlow, loading, Snackbar & Help | ✅ Implemented and tested |
| Labs 8–15 | Future lab assignments | 🔜 Upcoming |

## 🏗 Project Architecture

The application follows a state-driven approach:

- **UI State:** Holds information displayed on the screen.
- **Screens:** Stateless composables responsible for presenting the UI.
- **Routes:** Handle state updates and user actions.
- **Navigation:** Manages transitions between screens using a single Activity.

## 🚀 Development Roadmap

The project will be expanded throughout the semester with additional features, including task persistence, DataStore, Room Database, notifications, background processing, and testing.

## 👩🏻‍💻 Developer

**Maha AlJoaib**  
Computer Science Student  
Imam Abdulrahman Bin Faisal University

---

*Developed for CSC402 – Mobile Application Programming | Academic Year 2026–2027*

## Lab 7 — Part 4

The existing Lab 5 project now uses five `StudyTask` items, stateless task rows, immutable completion updates and ID-based navigation. State is saved across rotation and navigation. The original theme, progress-card styling, task details and sharing are retained.

See the [implementation and verification report](docs/lab7/Lab7-Report.md), [evidence screenshots](docs/lab7/screenshots), [paper-sketch guide](docs/lab7/P4.1-Paper-Sketch-Guide.md), and [AI disclosure draft](docs/lab7/AI-Use-Disclosure.md). The actual paper sketch/photo, student review of the disclosure log, and Blackboard submission remain pending.

Run the current Lab 7 UI tests on a connected emulator with:

```sh
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=sa.edu.iau.cshj.studyplanner.StudyPlannerLab7Test,sa.edu.iau.cshj.studyplanner.StudyPlannerRotationTest
```

## Lecture 8 — Today

Open **Today** from the top of the existing home screen. It loads three lecture sample tasks after 1.5 seconds, simulates saving for 300 ms, shows **Marked as done** on completion, and displays a tip through **Help**. The Today ViewModel survives rotation. **Back** returns to the existing Lab 7 home screen; its tasks, details and sharing remain available.

Today has its own lecture sample list, separate from the existing five home tasks. Its repository simulates work and does not persist data. Closing the Today destination and opening it again creates a fresh ViewModel and loads again.

Read the [stage-by-stage report and file list](docs/lecture8/Lecture8-Report.md), [verification results](docs/lecture8/Verification.md), [screenshots](docs/lecture8/screenshots), and [AI disclosure draft](docs/lecture8/AI-Use-Disclosure.md).

Run the current lecture and Lab 7 regression tests on a connected emulator:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=sa.edu.iau.cshj.studyplanner.TodayViewModelTest,sa.edu.iau.cshj.studyplanner.TodayScreenTest,sa.edu.iau.cshj.studyplanner.TodayIntegrationTest,sa.edu.iau.cshj.studyplanner.StudyPlannerLab7Test,sa.edu.iau.cshj.studyplanner.StudyPlannerRotationTest
```
