# विद्यासेतु एआई (VidyaSetu AI) - प्रोजेक्ट विकास नियम और गाइडलाइंस

यह फ़ाइल प्रोजेक्ट के विकास, यूआई डिज़ाइन, वास्तुकला (architecture) और सर्वोत्तम प्रथाओं (best practices) के लिए प्रमुख नियमों को परिभाषित करती है। किसी भी नए फीचर या कोड को जोड़ने से पहले इन नियमों का पालन करना अनिवार्य है।

---

## 1. परियोजना संरचना और स्केलेबिलिटी (Project Structure & Scalability)

प्रोजेक्ट को पूरी तरह व्यवस्थित और स्केलेबल रखने के लिए पूर्व-निर्धारित पैकेज संरचना का कड़ाई से पालन करें। बिना किसी ठोस कारण के कहीं भी रैंडम फाइलें न बनाएं।

- **नियम**: कुछ भी नया बनाने से पहले, यह देखें कि वह घटक प्रोजेक्ट में कहाँ फिट बैठता है।
- **प्रमुख पैकेज संदर्भ (Package References)**:
  - **कोर ऑथ (Core Auth)**: [SessionManager.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/auth/SessionManager.kt) (यूज़र लॉगिन सत्र और टोकन स्टोर करने के लिए)
  - **नेटवर्क क्लाइंट (Network Client)**: [SupabaseClient.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/network/SupabaseClient.kt) (डेटाबेस और एपीआई इंटरेक्शन के लिए)
  - **थीम और रंग (Theme & Colors)**: [Color.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/ui/theme/Color.kt) और `AppColors` (कस्टम कलर पैलेट के लिए)
  - **साझा यूआई घटक (Shared UI Components)**: [DashboardScreen.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/ui/components/DashboardScreen.kt) और [VidyaSetuComponents.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/core/ui/components/VidyaSetuComponents.kt)
  - **फ़ीचर मॉड्यूल (Feature Modules)**: `feature_auth`, `feature_institution`, `feature_feed`, `feature_journey`, `feature_profile`, `feature_tournament` (प्रत्येक फ़ीचर के अपने `data`, `domain`, और `presentation` पैकेज हैं)।

---

## 2. यूआई डिज़ाइन सिद्धांत (UI Design Principles)

विद्यासेतु ऐप का लक्ष्य एक बेहद प्रीमियम और आधुनिक अनुभव प्रदान करना है।

- **सरलता और प्रीमियम लुक (Simple & Premium Design)**:
  - डिज़ाइन को हमेशा साफ़, न्यूनतम (minimalist) रखें। गैर-ज़रूरी रंगों या जटिलताओं से बचें।
  - **व्हाइट स्पेस (Whitespace)**: पर्याप्त खाली जगह (margin & padding) का उपयोग करें ताकि स्क्रीन सांस ले सके और उपयोगकर्ता पर दबाव न बने।
- **डार्क और लाइट थीम (Dark & Light Theme Support)**:
  - हर यूआई घटक को इस तरह डिज़ाइन किया जाना चाहिए कि वह लाइट थीम और डार्क थीम दोनों में सही दिखे।
  - रंगों को हार्डकोड न करें। हमेशा `MaterialTheme.colorScheme` या गतिशील रिसोर्स वैल्यूज़ का उपयोग करें।
- **द्विभाषी समर्थन (English & Hindi Language Support)**:
  - ऐप की हर स्क्रीन अंग्रेजी और हिंदी दोनों भाषाओं के लिए अनुकूल होनी चाहिए।
  - हार्डकोडेड स्ट्रिंग्स के बजाय रिसोर्स फाइलों (`strings.xml`) का उपयोग करें ताकि भाषा बदलने पर यूआई सही ढंग से ट्रांसलेट हो सके।

---

## 3. नेविगेशन और बैक प्रेस हैंडलिंग (Back Press Handling)

यूज़र एक्सपीरियंस को निर्बाध बनाने के लिए सही बैक नेविगेशन बहुत महत्वपूर्ण है।

- **नियम**: हर स्क्रीन या आंतरिक फ्लो में आवश्यकतानुसार `androidx.activity.compose.BackHandler` का उपयोग अनिवार्य रूप से करें।
- **अपेक्षित व्यवहार**:
  - सब-स्क्रीन या गैर-होम टैब (जैसे: `Campus`, `Notifications`, `Institute` आदि) पर बैक बटन दबाने पर सीधे ऐप बंद नहीं होना चाहिए, बल्कि यूज़र को मुख्य होम स्क्रीन या पिछले सक्रिय टैब पर जाना चाहिए।
  - केवल मुख्य स्क्रीन (`Home` टैब) या लॉगिन स्क्रीन पर ही बैक दबाने पर ऐप बंद होना चाहिए।

---

## 4. संदर्भ और कोर कॉन्सेप्ट्स (Core Concepts References)

यह समझने के लिए कि ऐप में कौन सी चीजें कैसे काम कर रही हैं, निम्नलिखित फाइलों का संदर्भ लें:
1. **सत्र बहाली (Session Restoration)**: [AuthRepositoryImpl.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_auth/data/repository/AuthRepositoryImpl.kt) (देखें कि पुराना सत्र रीफ्रेश टोकन के साथ कैसे बहाल होता है)।
2. **सक्रिय संस्थान लिंकिंग (Active Institute Check)**: [InstitutionRepositoryImpl.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_institution/data/repository/InstitutionRepositoryImpl.kt) (तीनों डेटाबेस तालिकाओं में अप्रूवल स्टेटस चेक करने की क्वेरी का संदर्भ लें)।
3. **नेविगेशन फ्लो**: [MainActivity.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/MainActivity.kt) (देखें कि स्प्लैश, लॉगिन, साइनअप और होम स्क्रीन के बीच स्विचिंग कैसे नियंत्रित होती है)।

---

## 5. प्रीमियम लक्ज़री मिनिमलिस्ट डिज़ाइन सिद्धांत (Premium Luxury Minimalist Design Logic)

भविष्य में किसी भी प्रोफ़ाइल या एक्शन स्क्रीन को डिज़ाइन करते समय निम्नलिखित 'लक्ज़री फ़्लैट' डिज़ाइन नियमों का संदर्भ लें:

1. **सफ़ेद बैकग्राउंड (Pure White Background)**:
   - स्क्रीन का मुख्य बैकग्राउंड हमेशा शुद्ध सफ़ेद (`Color.White`) होना चाहिए। इससे ऐप में खुलापन और सांस लेने की जगह (Whitespace) मिलती है।

2. **बारीक ग्रे आउटलाइन (Thin Gray Outline)**:
   - सफ़ेद बैकग्राउंड पर किसी कार्ड या ब्लॉक को अलग करने के लिए भारी शैडो (shadow) के बजाय बहुत ही बारीक ग्रे रंग की बॉर्डर/आउटलाइन (`1.dp` चौड़ाई और `Color(0xFFE5E5EA)`) का उपयोग किया जाना चाहिए।

3. **सीमित और न्यूट्रल रंग (Limited & Neutral Colors)**:
   - अनावश्यक चमकीले रंगों का उपयोग करने से बचें।
   - स्टेटस पिल्स और बैज को हल्के न्यूट्रल बैकग्राउंड (जैसे `Color(0xFFF2F2F7)`) और हल्के डार्क या न्यूट्रल टेक्स्ट कलर के साथ डिज़ाइन करें ताकि विज़ुअल शोर (Visual Noise) न हो।

4. **छोटा और संतुलित प्राथमिक बटन (Compact Primary Action Button)**:
   - मुख्य बटनों (जैसे Approve या Submit) को अत्यधिक बड़ा करने के बजाय, उनके टेक्स्ट के आकार के अनुसार छोटा रखें (जैसे `width(180.dp)` या `width(200.dp)` और `height(42.dp)`)।
   - बटन को गोल पिल-शेप (`21.dp` कोना) दें जो लॉगिन बटन के आकार के समान संतुलित दिखे।

5. **क्लासी और सूक्ष्म अवतार (Minimal Initials Avatar)**:
   - प्रोफाइल में रंग-बिरंगे या बहुत गहरे ग्रेडिएंट्स के बजाय संस्थान के शुरुआती दो अक्षरों के साथ एक सूक्ष्म अवतार (जैसे हल्के सैटल बैकग्राउंड `Color(0xFFF4F6F5)` और मुख्य हरे रंग `AppColors.EmeraldGreen` के टेक्स्ट के साथ) एक उत्तम दर्जे का और प्रीमियम रूप देता है।

