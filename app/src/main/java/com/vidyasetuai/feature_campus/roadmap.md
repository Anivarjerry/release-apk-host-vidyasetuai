# VidyaSetu AI - Campus Implementation Roadmap

---

## 🗺️ चरणबद्ध योजना (Step-by-Step Implementation Roadmap)

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                        CAMPUS REDESIGN STEP-BY-STEP ROADMAP                            │
├────────────────────────────────────────────────────────────────────────────────────────┤
│ Phase 1: Supabase Backend Migration (Tables, Triggers, RPCs, pg_cron)                  │
│ Phase 2: Room Database 2-Table Entity & DAO Layer (CampusConnection & CampusMessage)   │
│ Phase 3: Dedicated Crypto Engine (CampusCryptoEngine - E2EE & Vault Key Derivation)    │
│ Phase 4: Offline-First Sync & Outbox Engine (CampusSyncEngine & Safe Remote Invoker)   │
│ Phase 5: UI Presentation Layer (HomeScreen, Friends Tab, ChatRoomScreen, HIG Design)   │
│ Phase 6: Hot-Swap Integration in DashboardScreen & Legacy Deprecation                  │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

### 📦 Phase 1: सुपाबेस बैकएंड निर्माण (Supabase Backend Setup)
- [ ] `campus_connections` टेबल बनाना (RLS व इंडेक्सेस सहित)।
- [ ] `campus_messages` टेबल बनाना (RLS व इंडेक्सेस सहित)।
- [ ] `trg_sync_campus_mutual` ऑटोमैटिक फॉलो ट्रिगर जोड़ना।
- [ ] `fn_send_campus_message`, `fn_mark_campus_chat_read` RPCs बनाना।
- [ ] `fn_fetch_campus_workspace_payload` (1-Roundtrip Main Sync RPC) तैयार करना।
- [ ] `campus_media` स्टोरेज बकेट व 24h `pg_cron` क्लीनअप लगाना।

---

### 📱 Phase 2: लोकल रूम डेटाबेस निर्माण (Local Room DB Setup)
- [ ] `CampusConnectionEntity.kt` (प्रोफ़ाइल कैशे व `peer_avatar_local_path` सहित)।
- [ ] `CampusMessageEntity.kt` (E2EE पेलोड, एक्सपायरी व ऑडिट कॉलम्स सहित)।
- [ ] `CampusDao.kt` (0ms अनरीड काउंट, चैट लिस्ट और मैसेज हिस्ट्री फ़्लोज़)।
- [ ] `AppDatabase.kt` में नई एंटिटीज रजिस्टर करना।

---

### 🔐 Phase 3: समर्पित क्रिप्टो इंजन (E2EE Security Engine)
- [ ] `CampusCryptoEngine.kt` बनाना (Argon2 पासवर्ड-डिराइव्ड मास्टर वॉल्ट + AES-256-GCM)।
- [ ] लॉगिन / री-लॉगिन पर मास्टर की अनलॉक एवं ऑटो-डिक्रिप्शन।

---

### ⚡ Phase 4: ऑफ़लाइन-फ़र्स्ट सिंक व आउटबॉक्स इंजन (Offline-First Sync Engine)
- [ ] `CampusSyncEngine.kt` बनाना (0ms पेलोड सिंक)।
- [ ] आउटबॉक्स मैनेजर: ऑफलाइन पेंडिंग मैसेजेस का ऑटो-रीट्राई और ब्लॉक होने पर लाल `⚠️` स्टेटस।
- [ ] `FCM` साइलेंट डेटा पुश हैंडलर व रूम-स्पेसिफिक रियल-टाइम लिसनर।

---

### 🎨 Phase 5: UI लेयर व स्क्रीन्स (Jetpack Compose HIG Screens)
- [ ] **CampusHomeScreen:**
  - टॉप टैब्स: "चैट्स (Chats)", "फॉलोअर्स / फ्रेंड्स (Connections)", "डिस्कवर (Discover)".
  - चैट कार्ड्स: डीपी, दोस्त का नाम, अंतिम मैसेज, समय, और 0ms अनरीड बैज (`🟢 3`).
- [ ] **CampusChatRoomScreen:**
  - 60 FPS `LazyColumn` मैसेज बबल्स।
  - डिलीवरी टिक्स (`🕒`, `✓`, `✓✓`, `✓✓ Blue`)।
  - 24h वैनिश बैज व ⭐️ स्टार/सेव बटन।
  - ब्लॉक/अनफॉलो पर ऑटो-लॉक्ड इनपुट बार।
- [ ] लेयर्ड `BackHandler` व फुल-स्क्रीन इमेज प्रीव्यूअर।

---

### 🔄 Phase 6: हॉट-स्वैप इंटीग्रेशन व लेगेसी क्लीनअप (Final Hot-Swap)
- [ ] `DashboardScreen.kt` में नए `CampusHomeScreen` को कनेक्ट करना।
- [ ] पुरानी 15 लेगेसी फाइल्स को साफ करना।
- [ ] पूर्ण टेस्टिंग व सत्यापन।
