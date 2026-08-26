# VidyaSetu AI - Campus Engineering Rules & Production Standards

---

## 🏛️ 1. द 5 पिलर्स ऑफ कैंपस रेजिलिएंस (MANDATORY 5 PILLARS)

### Pillar 1: Single-Flight Auth Gatekeeper (Zero Race Conditions)
- कभी भी चैट या बैकग्राउंड सिंक लूप से बार-बार मैन्युअल टोकन रिफ्रेश न करें।
- सभी टोकन रिफ्रेश `AuthManager.tokenRefreshMutex` गेटकीपर से गुजरेंगे ताकि सुपाबेस को डुप्लिकेट रिफ्रेश टोकन न मिले (`Invalid Refresh Token: Already Used`)।

### Pillar 2: Centralized Resilient Remote Invoker (`safeSupabaseCall`)
- सभी मैसेज सेंडिंग, फॉलो/अनफॉलो म्यूटेशन, और RPC कॉल्स `SafeSupabaseInvoker.safeSupabaseCall` के जरिए ही होंगे।
- 401 JWT एक्सपायरी पर यह साइलेंटली टोकन रिफ्रेश करके रिक्वेस्ट को री-फ्लाई करेगा बिना यूज़र को टोके।

### Pillar 3: Zero Data Loss (Outbox & Draft Preservation)
- अगर यूज़र बिना इंटरनेट के मैसेज भेजता है, तो मैसेज को कभी ड्रॉप न करें। वह Room DB में `status = 'PENDING'` में सेव रहेगा और इंटरनेट आते ही स्वतः जाएगा।
- जब तक मैसेज सफल न हो, इनपुट या ड्राफ्ट कभी नष्ट नहीं होगा।

### Pillar 4: Zero Technical Error Leakage in UI
- **कभी भी स्क्रीन पर कच्चा एरर कोड, स्टैकट्रेस, सुपाबेस RLS डंप या HTTP स्टेटस न दिखाएं।**
- अगर यूज़र को किसी ने ब्लॉक/अनफॉलो कर दिया है, तो चैट में मैसेज के बगल में साफ और विनम्र **लाल विस्मयादिबोधक `⚠️` (Delivery Failed)** दिखेगा।

### Pillar 5: Mandatory Native Layered BackHandler
- कैंपस की प्रत्येक स्क्रीन, फुल-स्क्रीन इमेज प्रीव्यूअर, चैट रूम और प्रोफ़ाइल बॉटम शीट में `BackHandler(enabled = true)` अनिवार्य है।
- बैक जेस्चर करने पर पहले टॉप-मोस्ट बॉटम शीट/इमेज व्यूअर बंद होगा, फिर चैट स्क्रीन और फिर होम डैशबोर्ड।

---

## 📱 2. डेटा व रेंडरिंग नियम (Data & UI Rendering Rules)

### Rule 1: 0ms Reactive Room DB Reads
- चैट लिस्ट, फ्रेंड्स लिस्ट और मैसेज हिस्ट्री हमेशा `Room Database Flow<List<T>>` पर `Dispatchers.IO` से पढ़ी जाएगी।
- कभी भी UI लोडिंग को सुपाबेस नेटवर्क कॉल पर ब्लॉक न करें।

### Rule 2: Mandatory Virtualized Rendering (Zero UI Choke)
- मैसेजेस की लंबी लिस्ट को कभी भी साधारण `Column` या `forEach` में रेंडर न करें।
- हमेशा Jetpack Compose `LazyColumn` का उपयोग करें और यूनिक स्टेबल की (`key = { message.id }`) प्रदान करें।
- `reverseLayout = true` का उपयोग करें ताकि नए मैसेजेस नीचे से स्वाभाविक रूप से ऊपर आएं (WhatsApp Style 60 FPS)।

### Rule 3: 0ms Local Disk Image Storage
- जब भी किसी फ्रेंड की DP या चैट इमेज डाउनलोड हो, उसे फोन के इंटरनल स्टोरेज (`context.filesDir/campus_avatars/`) में राइट करें और Room DB में `peer_avatar_local_path` सेव करें।
- UI हमेशा पहले लोकल फाइल से इमेज लोड करेगा, ताकि बिना इंटरनेट भी फोटो तुरंत 0ms में खुले।

### Rule 4: Apple Minimalist Flat HIG Design Tokens
- कार्ड्स के लिए 12-16dp कॉर्नर रेडियस।
- ट्रांसलूसेंट बैज और एमराल्ड ग्रीन (`#10B981`) एक्सेंट कलर।
- Lucide एवं Material 3 क्लीन आइकॉनोग्राफी।
- चैट बबल्स: सेंडर के लिए हल्का एमराल्ड ग्रीन (`#10B981`), रिसीवर के लिए न्यूट्रल सरफेस कार्ड (`#1E293B` / `#F1F5F9`)।

---

## 🚫 3. प्रोहिबिटेड एंटी-पैटर्न्स (Anti-Patterns to AVOID)

1. ❌ **ग्लोबल बैकग्राउंड सॉकेट्स:** पूरी ऐप में लगातार WebSocket कनेक्टेड न रखें। केवल चैट स्क्रीन पर हल्का कमरा-स्पेसिफिक चैनल लगाएं।
2. ❌ **हार्डकोडेड डमी चैट्स:** कभी भी मॉक या हार्डकोडेड चैट लिस्ट्स न बनाएं; सब कुछ शुद्ध डायनामिक Room DB से चलेगा।
3. ❌ **सीधे सर्वर से चैट लोड करना:** स्क्रीन खोलने पर कभी गोल लोडर न घुमाएं, पहले Room DB से 0ms में पुरानी बातचीत दिखाएं, फिर बैकग्राउंड सिंक करें।
