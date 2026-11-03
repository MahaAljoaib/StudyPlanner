# Lecture 8 — verification results

Verified on 3 November 2026, Riyadh time, using the existing Medium_Phone emulator (Android 17 / API 37.1).

| Check | Result |
|---|---|
| Debug APK assembly | PASS |
| JVM unit tests | PASS: 1 test, 0 failures |
| Final connected tests | PASS: 12 tests, 0 failures, 0 errors, 0 skipped |
| Lint | PASS: 0 errors, 17 warnings |
| Temporary guide experiments | PASS: slow load, blocking comparison, changed message, fourth task |
| Restore after experiments | Original repository and ViewModel restored byte-for-byte; temporary test removed |
| Single Activity | Manifest still contains only MainActivity |
| Previous screens | Existing home, task details, sharing and Lab 7 previews retained |

## Final connected-test classes

- `TodayViewModelTest`: 1 test for initial loading, delayed save, immutable updates, completion message, clearing and undo.
- `TodayScreenTest`: 2 tests for loading/loaded previews and responsive Help while loading.
- `TodayIntegrationTest`: 1 end-to-end test for the real Activity, loading, completion Snackbar, Help, landscape/portrait rotation, Activity recreation, no reload or replay of an acknowledged message, and returning to previous features.
- `StudyPlannerLab7Test`: 7 unchanged regression tests, including old previews, completion, navigation and share Intent.
- `StudyPlannerRotationTest`: 1 unchanged rotation/recreation regression test.

The final build finished successfully in 44 seconds. The connected-test XML reported `tests="12" failures="0" errors="0" skipped="0"`.

## Lint notes

Four warnings recommend putting `modifier` before other optional parameters. The code keeps it last to retain the Lab 7 coursework convention. The other thirteen concern the existing tool/dependency versions, an existing redundant Activity label, and existing unused color resources. No functional Lint errors were reported, and dependencies unrelated to the requested addition were not upgraded.

## Reproduce

From the existing project, with an emulator connected:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=sa.edu.iau.cshj.studyplanner.TodayViewModelTest,sa.edu.iau.cshj.studyplanner.TodayScreenTest,sa.edu.iau.cshj.studyplanner.TodayIntegrationTest,sa.edu.iau.cshj.studyplanner.StudyPlannerLab7Test,sa.edu.iau.cshj.studyplanner.StudyPlannerRotationTest
```

The existing untracked local Lab 5 test was left intact and is not part of this selected test suite or the commit. The tracked Lab 7 test already verifies sharing.

## Evidence

Images 01–03 show actual Compose preview functions rendered by tests. Images 04–08 show the running application through MainActivity. The screenshots are app content captures from the emulator; they are not generated mockups. The Today loading preview was also inspected in Android Studio after syncing the project and refreshing previews.

## Scope of state retention

Rotation and Activity recreation are covered. Process-death restoration and permanent database persistence are outside this guide's fake-repository implementation. Leaving Today by Back removes that destination and its ViewModel; reopening it loads the sample data again.
