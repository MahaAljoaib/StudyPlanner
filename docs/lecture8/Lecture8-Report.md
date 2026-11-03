# تقرير تنفيذ Lecture 8 على StudyPlanner

التاريخ: 3 نوفمبر 2026، بتوقيت الرياض.

## النتيجة وطريقة الوصول

أُضيفت شاشة **Today** إلى مشروع StudyPlanner الحالي بعد Lab 7. افتحي التطبيق ثم اضغطي **Today** أعلى الصفحة الرئيسية. عند أول فتح للشاشة يظهر مؤشر تحميل، ثم المهام الثلاث تلقائيًا. تحديد مهمة يعرض **Marked as done**، وزر **Help** يعرض **Tick a task when you finish it**. يعيد زر **Back** المستخدم إلى الرئيسية السابقة.

أُبقيت الرئيسية وقائمة Lab 7 وشاشة التفاصيل والمشاركة والثيم ومعاينات العمل السابق. بقي التطبيق ذا Activity واحدة. لم يُنشأ مشروع جديد، ولم يتغير اسم الحزمة أو معرّف التطبيق أو المستودع.

## تكييف الدليل مع المشروع الموجود

الدليل مكتوب لمشروع جديد ويستبدل محتوى MainActivity بعرض TodayRoute مباشرة. بناءً على طلبك، بقيت `MainActivity` تعرض `StudyPlannerApp` وبقيت الرئيسية هي البداية؛ أُضيفت وجهة Today داخل NavHost وزر للوصول إليها. أُضيف `enableEdgeToEdge()` كما في الدليل، مع استهلاك هوامش النظام داخل الملاحة لمنع مضاعفة المسافات في Scaffold المتداخل.

أُعيد استخدام `model/StudyTask.kt` الموجود بدل تعريف نسخة جديدة. تبقى قائمة Lab 7 ذات الخمس مهام مستقلة عن المهام الثلاث التعليمية في Today. لا تنقل هذه الإضافة حالة الرئيسية القديمة إلى ViewModel؛ ذلك يحافظ على تنفيذ Lab 7 السابق كما طلبتِ.

استُخدمت بطاقات وألوان الثيم السابق، مع `LazyColumn` ومفاتيح ثابتة بدل `forEach` في Today حتى تبقى المهام قابلة للتمرير في العرض الأفقي. هذه تعديلات دمج وعرض؛ تسلسل الحالة والتحميل والرسائل مطابق للدليل.

## المرحلة الأولى: المكتبات والبيانات

**لماذا:** لفصل البيانات التي تحتاجها الشاشة عن الكود الذي يرسمها، وتجهيز أدوات Lifecycle المستخدمة في الدليل.

- أُضيفت `lifecycle-viewmodel-compose` و`lifecycle-runtime-compose` بإصدار `2.11.0` عبر Version Catalog، بما يطابق إصدار Lifecycle الموجود.
- `TodayUiState` يجمع `isLoading = true` و`tasks = emptyList()` و`message = null`.
- `FakeTaskRepository.loadTodayTasks()` يستخدم `delay(1500L)` ثم يرجع: Read Chapter 11، Lab 8 report، Revise SQL joins.
- `setDone(id, done)` يحاكي الحفظ بـ`delay(300L)`؛ لا يكتب إلى قاعدة بيانات.

**كيف يعمل:** كلمة `suspend` تسمح بانتظار `delay` داخل coroutine دون تعطيل خيط الواجهة. عند انتهاء الانتظار تُعاد البيانات إلى ViewModel.

**النتيجة:** نجح تجميع المرحلة باستخدام `:app:compileDebugKotlin`، مع إبقاء `StudyTask` القديم دون تعديل.

## المرحلة الثانية: TodayViewModel

**لماذا:** ليصبح ViewModel مالك حالة Today، وتبقى الحالة عند تدوير الجهاز.

- `_uiState` هو `MutableStateFlow` خاص؛ تُعرض خارجيًا نسخة `StateFlow` للقراءة فقط باستخدام `asStateFlow()`.
- يبدأ التحميل في `init` داخل `viewModelScope.launch`، ثم ينفّذ `copy(isLoading = false, tasks = tasks)`.
- `onTaskCheckedChange(task, done)` ينتظر الحفظ الوهمي، ثم يُنشئ قائمة جديدة باستخدام `map` و`copy` للمهمة المطابقة فقط.
- يقرر ViewModel رسالة الإنجاز؛ وعند انتهاء عرضها يستقبل `onMessageShown()` فيجعل `message = null`.

**النتيجة:** تحقق اختبار ViewModel من التحميل المؤجل، وأسماء المهام، وتأخير الحفظ، وعدم تعديل القائمة القديمة، وتغيير مهمة واحدة فقط، ومسح الرسالة وإلغاء الإنجاز.

## المرحلة الثالثة: TodayRoute وTodayScreen وTaskRow

**لماذا:** لفصل ربط الحالة عن رسم الواجهة، واستخدام أدوات Compose المناسبة للأحداث المؤجلة.

- `TodayRoute` يحصل على ViewModel بواسطة `viewModel()` ويقرأ `uiState` باستخدام `collectAsStateWithLifecycle()`؛ يمرر الحالة والأحداث إلى الشاشة.
- `TaskRow` يعرض Checkbox والعنوان والمقرر؛ لا يحتفظ بحالة المهمة داخله. يرفع الحدث إلى ViewModel عبر callbacks.
- أثناء التحميل تعرض الشاشة `CircularProgressIndicator`؛ وبعده تعرض قائمة المهام.
- يراقب `LaunchedEffect(uiState.message)` الرسالة، ويستدعي `snackbarHostState.showSnackbar(message)`، ثم يبلغ ViewModel بانتهاء العرض.
- يستخدم Help `rememberCoroutineScope()` لتشغيل `showSnackbar` من حدث النقر. هذا النطاق مرتبط بعمر الواجهة؛ لا ينفّذ الحفظ أو يملك بيانات المهام.
- أُضيفت معاينتان: `TodayLoadingPreview` و`TodayLoadedPreview`، دون الحاجة إلى تشغيل Repository في المعاينة.

**النتيجة:** نجحت اختبارات العرض والتحميل والـSnackbar وHelp. ظهرت معاينة التحميل داخل Android Studio بعد المزامنة والتحديث، واختُبرت معاينة القائمة على المحاكي. الصور المرفقة لعرض Compose الحقيقي وليست صورًا مولّدة.

## المرحلة الرابعة: التدوير والمحافظة على الميزات

اختُبر التطبيق عبر `MainActivity` الحقيقية:

1. تحديد مهمة في الرئيسية ثم فتح Today.
2. مشاهدة التحميل ثم ظهور مهام Today.
3. تحديد مهمة Today وانتظار ظهور رسالة الإنجاز واختفائها.
4. التدوير إلى الوضع الأفقي ثم العمودي؛ بقي التحديد ولم يعد مؤشر التحميل.
5. إعادة إنشاء Activity؛ بقيت وجهة Today وحالة المهمة، ولم تُعرض رسالة إنجاز قديمة سبق التعامل معها.
6. عرض Help ثم الرجوع؛ بقي تقدم Lab 7، واستمرت التفاصيل والمشاركة في العمل.

نجحت اختبارات Lab 7 الثمانية المتتبعة بعد الدمج، بما فيها معايناته الثلاث والمشاركة والتدوير السابق.

**حدود الحفظ:** ViewModel يحافظ على حالة Today أثناء تغيّر إعدادات الجهاز وإعادة إنشاء Activity المرتبطة بالـViewModelStore. لا توجد قاعدة بيانات أو SavedStateHandle لهذا الجزء؛ بعد إغلاق وجهة Today بالرجوع وفتحها من جديد، أو بعد إنهاء عملية التطبيق، قد يبدأ التحميل من جديد. هذا هو نطاق المستودع الوهمي في الدليل.

## تجارب الصفحة 14

أُجريت التعديلات التعليمية مؤقتًا، وأُعيدت الملفات الأساسية بعد كل تجربة:

| التجربة | التحقق | النسخة النهائية |
|---|---|---|
| تدوير الهاتف | بقاء التحديد وعدم إعادة التحميل | مدعوم |
| تحميل خمس ثوانٍ | Help يستجيب بينما مؤشر التحميل ما زال ظاهرًا | أُعيد `delay(1500L)` |
| استخدام Thread.sleep | تحقق أن إنشاء ViewModel يحجب الخيط الرئيسي لمدة لا تقل عن 1.4 ثانية | أُزيل وأُعيد `delay` |
| تغيير الرسالة | ظهرت Well done! بعد تحديد المهمة | أُعيدت Marked as done |
| إضافة مهمة رابعة | ظهرت Prepare presentation وأربعة مربعات اختيار دون تغيير الشاشة | أُعيدت المهام الثلاث |

ملف اختبار التجارب المؤقت أُزيل بعد الانتهاء؛ لا تتضمن النسخة النهائية Thread.sleep أو تأخير خمس ثوانٍ أو المهمة الرابعة.

## الملفات المضافة

جميع المسارات التالية نسبةً إلى مجلد المشروع الأصلي:

| الملف | دوره |
|---|---|
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/TodayUiState.kt` | حالة Today |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/FakeTaskRepository.kt` | تحميل وحفظ وهميان |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/TodayViewModel.kt` | إدارة الحالة والمهام والرسالة |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/TodayScreen.kt` | Route وScreen وTaskRow ومعاينتان |
| `app/src/androidTest/java/sa/edu/iau/cshj/studyplanner/TodayViewModelTest.kt` | تحقق البيانات والتحديثات غير المعدّلة للقائمة السابقة |
| `app/src/androidTest/java/sa/edu/iau/cshj/studyplanner/TodayScreenTest.kt` | تحقق المعاينات وHelp أثناء التحميل |
| `app/src/androidTest/java/sa/edu/iau/cshj/studyplanner/TodayIntegrationTest.kt` | تحقق التطبيق الفعلي والرسائل والتدوير والرجوع |
| `docs/lecture8/` | التقرير والصور وملخص التحقق ومسودة الإفصاح |

## الملفات السابقة المعدّلة

| الملف | التغيير |
|---|---|
| `app/build.gradle.kts` | إضافة مكتبتَي Lifecycle Compose |
| `gradle/libs.versions.toml` | تعريف المكتبتين بالإصدار الموجود |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/MainActivity.kt` | إضافة enableEdgeToEdge؛ الإبقاء على StudyPlannerApp |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/PlannerHomeScreen.kt` | زر Today وحدث onTodayClick فقط؛ بقاء قائمة Lab 7 وحالتها |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/navigation/Routes.kt` | إضافة الوجهة Today |
| `app/src/main/java/sa/edu/iau/cshj/studyplanner/navigation/StudyPlannerApp.kt` | ربط Today بالـNavHost والرجوع ومعالجة الهوامش |
| `README.md` | توثيق Lecture 8 وطريقة فتح الشاشة وتشغيل الاختبارات |

لم تُعدّل `TaskItem.kt` أو `TaskDetailScreen.kt` أو `StudyTask.kt` أو ملفات الثيم أو Manifest أو اختبارات Lab 7 السابقة. تُركت ملفات إعدادات Android Studio والاختبار المحلي غير المتتبع لـLab 5 كما كانت، واستُبعدت من Commit.

## صور النتائج

- [التحميل](screenshots/01-loading.png)
- [Help أثناء التحميل](screenshots/02-help-during-loading.png)
- [معاينة القائمة](screenshots/03-loaded-preview.png)
- [Today بعد التحميل الفعلي](screenshots/04-running-loaded.png)
- [Marked as done](screenshots/05-marked-as-done.png)
- [الوضع الأفقي بعد التدوير](screenshots/06-landscape-after-rotation.png)
- [Help بعد التحميل](screenshots/07-help.png)
- [الرئيسية السابقة بعد العودة](screenshots/08-lab7-preserved.png)

## قراءة الكود بنفسكِ

- نقطة بدء التحميل: `TodayViewModel.kt`، كتلة `init`.
- مالك الحالة: `TodayViewModel`؛ محتواها موصوف في `TodayUiState`.
- قارئ الحالة: `TodayRoute`، سطر `collectAsStateWithLifecycle`.
- راسم الحالة: `TodayScreen` و`TaskRow`.
- نقطة عرض رسالة الإنجاز: `TodayScreen`، كتلة `LaunchedEffect`.
- نقطة تشغيل نصيحة Help: `scope.launch` داخل حدث الزر.
- سبب بقاء الإنجاز عند التدوير: إعادة استخدام ViewModel المرتبط بوجهة الملاحة؛ لا يعتمد على متغير محلي داخل الصف.

راجعِي `Verification.md` لنتيجة الفحوص النهائية. يظهر Commit هذا العمل بعنوان **Lecture 8 - Today ViewModel, loading and snackbar flow** في [سجل المستودع](https://github.com/MahaAljoaib/StudyPlanner/commits/main). تُسجّل نتيجة الرفع والمطابقة مع الفرع البعيد في تقرير التسليم بعد إجراء Push.

## المراجع

- الدليل المحلي المرفق: CSC402_Lecture08_Build_Guide.pdf، الصفحات 4 و6–14 و16.
- [توثيق Lifecycle الرسمي](https://developer.android.com/jetpack/androidx/releases/lifecycle): تعريف مكتبتَي Compose والإصدار 2.11.0.
- [توثيق ملكية الحالة في Compose](https://developer.android.com/develop/ui/compose/state-hoisting): فصل حالة الشاشة عن حالة عناصر الواجهة.
