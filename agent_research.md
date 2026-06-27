# विद्यासेतु AI — सुपबेस कनेक्टिविटी एवं डेटा फ़ेच समस्या निवारण (Agent Research Document)

यह दस्तावेज़ सुपबेस (Supabase) से डेटा न आने की समस्या, उसके कारणों, और उनके समाधान की विस्तृत जानकारी प्रदान करता है, ताकि भविष्य में इस तरह की समस्या आने पर संदर्भ के रूप में इसका उपयोग किया जा सके।

---

## 1. समस्या का विवरण (Problem Statement)
ऐप का बिल्ड सफलतापूर्वक पूरा होने के बाद भी, होम फ़ीड (केस स्टडीज़) और प्रोफ़ाइल स्क्रीन पर सुपबेस से डेटा लोड नहीं हो रहा था। यूआई पूरी तरह से खाली ("No case studies available" या खाली फ़ील्ड्स) और लोडिंग स्टेट पर रुका हुआ दिखाई दे रहा था।

---

## 2. मूल कारण विश्लेषण (Root Cause Analysis)

### कारण A: एंड्रॉइड मेनिफेस्ट में इंटरनेट अनुमति का न होना (Missing INTERNET Permission)
* **विश्लेषण:** रूम डेटाबेस का स्कीमा वर्जन `1` से `2` करने पर local cache क्लीन हो गई (Destructive Migration के कारण)। इसके बाद जब ऐप ने नेटवर्क से डेटा मंगाने की कोशिश की, तो मेनिफेस्ट में `<uses-permission android:name="android.permission.INTERNET" />` और `ACCESS_NETWORK_STATE` अनुमतियां न होने के कारण सुरक्षा कारणों से नेटवर्क रिक्वेस्ट ब्लॉक हो गई।
* **प्रभाव:** सुपबेस को की जाने वाली सभी नेटवर्क कॉल्स फ़ेल हो गईं।

### कारण B: त्रुटि ट्रेस की कमी (Absence of Error Logs in Repositories)
* **विश्लेषण:** `CaseStudyRepositoryImpl` और `ProfileRepositoryImpl` में नेटवर्क कॉल्स `try-catch` और `runCatching` ब्लॉक्स के अंदर थीं, लेकिन एक्सेप्शन के लिए कोई `Log.e` नहीं लगा था, जिससे नेटवर्क एरर साइलेंटली इग्नोर हो रहे थे।

### कारण C: रूम डेटाबेस स्कीमा वर्जन असंगति (Room DB Schema Integrity Exception)
* **विश्लेषण:** पूर्ववर्ती परिवर्तनों में डेटाबेस वर्जन को `2` कर दिया गया था और एंटिटीज़ (`UserProfileEntity` और `ContributorVerificationEntity`) में नए फ़ील्ड्स जोड़े गए थे। लेकिन चूंकि एमुलेटर में वर्जन `2` की पुरानी स्कीमा वाली डेटाबेस फ़ाइल पहले से बनी हुई थी, रूम ने अखंडता (Integrity) की जाँच करते समय `java.lang.IllegalStateException: Room cannot verify the data integrity` फेंक दिया।
* **प्रभाव:** इस अपवाद (exception) के कारण लोकल डेटाबेस ओपन नहीं हो पा रहा था, जिससे डेटा रीड/राइट ब्लॉक हो गया और सुपबेस से फ़ेच होने के बावजूद लोकल रूम में स्टोर नहीं हो सका।

---

## 3. किए गए समाधान (Steps Taken to Fix)

### चरण 1: इंटरनेट अनुमति जोड़ना
[AndroidManifest.xml](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/AndroidManifest.xml) फ़ाइल में निम्नलिखित अनुमतियाँ जोड़ी गईं:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### चरण 2: विस्तृत त्रुटि लॉगिंग जोड़ना
1. **[CaseStudyRepositoryImpl.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_case_study/data/repository/CaseStudyRepositoryImpl.kt):** कैच ब्लॉक्स में `android.util.Log.e("VidyaSetu_CaseStudyRepo", ...)` जोड़ा गया।
2. **[ProfileRepositoryImpl.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_profile/data/repository/ProfileRepositoryImpl.kt):** सभी `runCatching` पर `.onFailure { e -> android.util.Log.e(...) }` ब्लॉक जोड़ा गया।

### चरण 3: फ़्लो ट्रेस लॉग्स जोड़ना (Debugging User ID)
यदि डेटा लोड न होने का कारण `userId` का खाली होना हो, तो उसे ट्रेस करने के लिए निम्नलिखित फाइलों में debug logs जोड़े गए:
1. **[DashboardScreen.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/ui/components/DashboardScreen.kt):**
   `android.util.Log.d("VidyaSetu_Dashboard", "Active userId is: '$userId'")`
2. **[CaseStudyListScreen.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_case_study/presentation/screen/CaseStudyListScreen.kt):**
   `android.util.Log.d("VidyaSetu_CaseStudyList", "loadCaseStudies called for userId: '$userId'")`
3. **[ProfileScreen.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_profile/presentation/screen/ProfileScreen.kt):**
   `android.util.Log.d("VidyaSetu_ProfileScreen", "LoadProfile called for userId: '$userId'")`

### चरण 4: रूम डेटाबेस वर्जन बढ़ाना (Room DB Version Upgrade)
- **[AppDatabase.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/database/AppDatabase.kt):** रूम डेटाबेस का वर्जन `2` से बढ़ाकर `3` किया गया। इससे रूम `fallbackToDestructiveMigration()` को ट्रिगर करेगा और पुराने असंगत डेटाबेस स्कीमा को सफलतापूर्वक नए स्कीमा से बदल देगा।

---

## 4. भविष्य के लिए समस्या निवारण गाइड (Troubleshooting Guide)

यदि दोबारा कभी डेटा न आने की समस्या होती है, तो टर्मिनल में इन कमांड्स को चलाकर लॉग्स देखें:

### 1. कनेक्टिविटी और यूज़र आईडी जाँचने की कमांड:
```bash
adb logcat -d | findstr VidyaSetu
```
*(यह कमांड एक्टिव `userId` और फ़ंक्शंस ट्रिगर होने की स्थिति बताएगी)*

### 2. सुपबेस नेटवर्क एरर या एक्सेप्शन देखने की कमांड:
```bash
adb logcat -d -s VidyaSetu_CaseStudyRepo:E VidyaSetu_ProfileRepo:E
```
*(यह कमांड किसी भी डेटाबेस/RLS पॉलिसी/नेटवर्क एक्सेप्शन को लाल रंग (Error) में प्रिंट करेगी)*

---

## 5. विद्या जर्नी V2 फ़ीचर - वास्तुकला एवं विकास नोट्स (Vidya Journey V2 Feature Notes)

विद्या जर्नी के दूसरे चरण (V2) के विकास के दौरान निम्नलिखित वास्तुकला परिवर्तनों और त्रुटि निवारण प्रक्रियाओं को शामिल किया गया है:

### A. डेटाबेस वास्तुकला (V2 Flat SQL Architecture)
* **जेएसओएन (JSON) का त्याग:** बड़े जर्नी टेम्प्लेट्स के प्रश्नों (MCQs) और दैनिक कार्यों (Tasks) को एक बड़े JSON में सहेजने के बजाय पूरी तरह से फ्लैट एसक्यूएल तालिकाओं (Flat SQL Tables) में विभाजित किया गया ताकि भविष्य में यूज़र एनालिटिक्स और लीडरबोर्ड की गणना बहुत तेज़ और आसान हो सके।
* **क्षेत्र पृथक्करण (Scope Separation):** नामकरण नियमों (Naming Conventions) के अनुसार ग्लोबल जर्नी (`global_`), ऑर्गनाइजेशन जर्नी (`organization_`) और पेरेंट ऑर्गनाइजेशन जर्नी के लिए अलग-अलग विशिष्ट टेबल्स बनाई गईं।
* **लीडरबोर्ड कैशिंग (Leaderboard Caching):** यूज़र प्रोग्रेस से हर बार हज़ारों रिकॉर्ड्स को समेटने की जगह सीधे `user_journeys` टेबल में `total_score` और `streaks` को स्टोर किया गया ताकि क्वेरी लोड कम हो सके।

### B. संकलन (Compilation) त्रुटियाँ और उनके समाधान
1. **लापता मॉडल्स इम्पोर्ट त्रुटि (Unresolved References):**
   * **समस्या:** `JourneyScreen.kt` में क्लीन आर्किटेक्चर के डोमेन मॉडल्स (जैसे `JourneyTask`, `JourneyMcq`, `UserJourney`) और स्टेट मॉडल्स (`JourneyUiState`) अनसुलझे (Unresolved) आ रहे थे।
   * **समाधान:** फ़ाइल के शीर्ष पर `com.vidyasetuai.feature_journey.domain.model.*` और `.presentation.state.JourneyUiState` के विशिष्ट इम्पोर्ट्स जोड़े गए।
2. **प्रोग्रेस बार लैम्ब्डा आर्गुमेंट त्रुटि (LinearProgressIndicator mismatch):**
   * **समस्या:** `LinearProgressIndicator` में `progress = { float }` (लैम्ब्डा) पास करने पर संकलन एरर आ रही थी।
   * **समाधान:** चूंकि यह प्रोजेक्ट मटेरियल 3 के एक पुराने संस्करण का उपयोग कर रहा है, इसलिए इसे लैम्ब्डा के बजाय सीधे `progress = float` (फ़्लोटिंग पॉइंट वैल्यू) के रूप में बदला गया।

### C. भारी टेस्ट डेटा जेनरेशन (PCM Test Data)
* टोकन सीमाओं और भारी मैनुअल राइटिंग से बचने के लिए, एक स्वचालित पायथन जनरेटर स्क्रिप्ट ([generate_sql.py](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/generate_sql.py)) बनाई गई।
* इसके द्वारा 10 क्लास 11/12 PCM जर्नी टेम्प्लेट्स, 20 दिन की अवधि, और प्रतिदिन 20 एमसीक्यू के साथ कुल **4,000 एमसीक्यू रिकॉर्ड्स** की 2.5 MB की SQL स्क्रिप्ट ([insert_test_data.sql](file:///d:/VidyaSetu%20AI/insert_test_data.sql)) का निर्माण किया गया।

---

## 6. योगदानकर्ता सत्यापन एवं बहु-छवि अपलोड (Contributor Verification & Multi-Image Upload V2)

योगदानकर्ता सुविधाओं और उन्नत अपलोडिंग के विकास में निम्नलिखित महत्वपूर्ण तकनीकी अनुभवों और विकास पद्धतियों को शामिल किया गया है:

### A. रीयल-टाइम डेटाबेस सत्यापन (Real-Time DB Verification Checks)
* **सुरक्षित पहुंच:** केवल स्थानीय प्रोफाइल बूलियन पर निर्भर रहने के बजाय सीधे सुपबेस की `content_contributor_verifications` टेबल से यूज़र की वर्तमान स्थिति को रीयल-टाइम सिंक करके सत्यापित किया जाता है।
* **पॉलाइट डायलॉग प्रतिक्रिया:** विभिन्न स्थितियों (लंबित, अस्वीकृत, निलंबित, अप्रवेशित) के लिए देवनागरी और अंग्रेजी दोनों भाषाओं में त्रुटि संदेश संवाद प्रदर्शित किए जाते हैं।

### B. क्लाइंट-साइड स्क्वायर इमेज क्रॉपिंग (Aspect Crop & Compress)
* **संसाधन अनुकूलन:** अपलोड करने से पहले `processSquareImage` द्वारा एमुलेटर/फोन मेमोरी में ही इमेज को 1:1 स्क्वायर में सेंटर-क्रॉप करके `600x600` पिक्सल साइज में स्केल किया जाता है और JPEG फॉर्मेट में 85% कंप्रेस करके अपलोड किया जाता है।
* **सुपबेस स्टोरेज बकेट स्ट्रक्चर:** `case-study-images` बकेट का उपयोग करके कवर फ़ोटो को `covers/` पाथ में और संदर्भ चित्रों को `content/` फ़ोल्डर के तहत सिंक किया जाता है।

### C. समृद्ध डेटा फ़ील्ड्स (Rich Fields Integration)
* केस स्टडी अपलोड में मुख्य टाइटल, शार्ट डिस्क्रिप्शन के अलावा भाषा, टैग्स की एरे, अनुमानित पढ़ने का समय, multiline विस्तृत सामग्री को JSONB ब्लॉक के अंदर पैराग्राफ फॉर्मेट में कन्वर्ट करके सुपबेस में सुरक्षित किया जाता है।

---

## 7. कैंपस चर्चा कक्ष (Learning Campus) - विकास, कीबोर्ड इनसेट एवं अपशब्द नियंत्रण

चर्चा कक्ष (Campus Rooms) फीचर को Senior-level Architect और Founder की दृष्टि से परिष्कृत करते समय निम्नलिखित प्रमुख वास्तुकला अनुभवों और समस्याओं का निवारण किया गया:

### A. स्थिर हेडर कीबोर्ड इनसेट डिज़ाइन (Static Header Keyboard Layout)
* **समस्या:** कीबोर्ड खुलने पर एंड्रॉइड की डिफ़ॉल्ट व्यवहार के कारण पूरी स्क्रीन (चैट का शीर्ष हेडर/TopAppBar सहित) ऊपर की ओर खिसक जाती थी, जिससे रूम का नाम और सक्रिय सदस्य संख्या छिप जाती थी।
* **समाधान:** 
  - `AndroidManifest.xml` में `windowSoftInputMode="adjustResize"` रखा गया।
  - `ChatRoomScreen.kt` में `.navigationBarsPadding().imePadding()` संशोधक को संदेश प्रविष्टि बार (input Row) से हटाकर, मुख्य स्क्रॉल योग्य संदेश कंटेनर (`Column`) के स्तर पर लागू किया गया। 
  - इससे केवल संदेशों का क्षेत्र और इनपुट बार ही कीबोर्ड के अनुसार सिकुड़कर ऊपर खिसकते हैं, जबकि शीर्ष `TopAppBar` (हेडर) बिल्कुल स्थिर (fixed) रहता है।

### B. व्हाट्सएप iOS-स्टाइल प्रीमियम डूडल वॉलपेपर और टेल्स (WhatsApp iOS Style UI)
* **व्हाट्सएप डूडल वॉलपेपर:** कैनवास पर रीयल-टाइम में शैक्षिक और चैट डूडल ड्रा करने के लिए `whatsappWallpaper` नामक एक ड्रॉ-बिहाइंड मॉडिफायर विकसित किया गया, जो लाइट/डार्क थीम के अनुकूल 1.5% सूक्ष्म ओपेसिटी के साथ ड्रा होता है।
* **व्हाट्सएप बबल्स विद टेल (Triangular Tails):** एक कस्टम कॉटलिन `ChatBubbleShape` बनाया गया जो मटीरियल डिज़ाइन के गोल किनारों के बजाय एक सुंदर कोणीय त्रिकोणीय पूंछ (tail) बनाता है (भेजे गए संदेशों में दाईं ओर तथा प्राप्त संदेशों में बाईं ओर)।

### C. लाइव लाइफसाइकिल-आधारित सॉकेट नियंत्रण (Lifecycle-driven Realtime Observer)
* **समस्या:** जब यूज़र स्क्रीन बंद कर देता था या ऐप को बैकग्राउंड में भेजता था, तब भी Supabase Realtime WebSocket कनेक्शन खुला रहता था और उपस्थिति (Presence Count) में यूज़र "सक्रिय" बना रहता था, जिससे बैटरी और इंटरनेट डेटा का अत्यधिक व्यय होता था।
* **समाधान:** 
  - `ChatRoomScreen.kt` में एंड्रॉइड आर्किटेक्चर का `LifecycleEventObserver` जोड़ा गया।
  - **ON_PAUSE** इवेंट पर `CampusEvent.PauseRealtime` ट्रिगर होता है जो सॉकेट्स और प्रेजेंस फ्लो को तुरंत कैंसिल कर देता है।
  - **ON_RESUME** इवेंट पर `CampusEvent.ResumeRealtime` ट्रिगर होकर पुनः सॉकेट्स को रीस्टार्ट कर देता है।
  - **ON_DESTROY / Exit** पर `CampusEvent.CloseActiveRoom` सभी जॉब्स को निरस्त करके स्टेट को पूरी तरह खाली कर देता है।
  - इससे डेटा ट्रांसमिशन की अत्यधिक बचत होती है और रीयल-टाइम उपस्थिति 100% सटीक रहती है।

### D. स्पेस-स्ट्रिपिंग मॉडरेशन फ़िल्टर (Space-Stripping Abuse Validation)
* **बाईपास सुरक्षा:** संदेश को मॉडरेट करते समय यूज़र द्वारा प्रयुक्त सभी रिक्त स्थान (Spaces) और विशेष विराम चिह्नों (जैसे `.`, `-`, `_`, `*`) को हटाकर सामान्य अक्षरों की एक स्ट्रिंग बनाकर जाँच की जाती है। यह `m.a.k.i.c.h.u.t` या `m a k i   c h u t` जैसे प्रत्येक छद्म (masked) अपशब्द को पूर्णतः ब्लॉक कर देता है।
* **JSONB डिकोडिंग क्रैश प्रिवेंशन:** सुपबेस से मॉडरेशन सेटिंग्स लोड करते समय, `blocked_keywords` के डेटा प्रारूप (कच्चा JSONB ऐरे या स्ट्रिंगिफाइड ऐरे) में विसंगति के कारण होने वाले क्रैश को रोकने के लिए `BlockedKeywordsSerializer` लागू किया गया, जो दोनों स्वरूपों को स्वतः संभाल लेता है।
* **मैसेज इनपुट रिस्टोरेशन:** यदि कोई संदेश किसी अपशब्द के कारण ब्लॉक किया जाता है, तो उसे इनपुट बॉक्स से डिलीट करने के बजाय रिस्टोर कर दिया जाता है ताकि यूज़र अनुचित शब्दों को एडिट करके पुनः संदेश भेज सके।
