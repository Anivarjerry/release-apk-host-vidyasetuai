# VidyaSetu AI - Campus System Architecture & Technical Blueprint

---

## 🏛️ 1. कार्यकारी सारांश (Executive Summary)

**VidyaSetu Campus** एक उच्च-प्रदर्शन (High-Performance), ऑफ़लाइन-फ़र्स्ट (0ms Offline-First), एंड-टू-एंड एन्क्रिप्टेड (E2EE) और अल्ट्रा-लीन (2-Table Lean) सोशल नेटवर्किंग एवं डायरेक्ट मैसेजिंग इंजन है। यह WhatsApp एवं Telegram के आधुनिकतम सिद्धांतों पर आधारित है, जो सर्वर पर 0% फालतू लोड और मोबाइल पर शून्य बैटरी ड्रेन की गारंटी देता है।

---

## 🔒 2. द 5 पिलर्स ऑफ कैंपस आर्किटेक्चर (5 Pillars Compliance)

| पिलर | नाम | कैंपस में कार्यान्वयन (Implementation) |
|:---:|---|---|
| **Pillar 1** | **Single-Flight Auth Engine** | चैट और बैकग्राउंड सिंक के दौरान टोकन रिफ्रेश रेस कंडीशंस को रोकने के लिए `Mutex` गेटकीपर का उपयोग। |
| **Pillar 2** | **Centralized Resilient Invoker** | सभी सुपाबेस RPC व डेटाबेस म्यूटेशन `SafeSupabaseInvoker.safeSupabaseCall` से सुरक्षित रहेंगे। |
| **Pillar 3** | **Zero Data Loss (Outbox Pattern)** | ऑफलाइन रहते हुए भेजे गए मैसेजेस Room DB में `PENDING 🕒` रूप में सुरक्षित रहेंगे और इंटरनेट आने पर स्वतः सिंक होंगे। |
| **Pillar 4** | **Zero Technical Error Leakage** | यदि सामने वाले ने ब्लॉक/अनफॉलो कर दिया है, तो ऐप क्रैश या रॉ एरर दिखाने के बजाय मैसेज के पास सौम्य लाल विस्मयादिबोधक `⚠️` (Delivery Failed) दिखाएगा। |
| **Pillar 5** | **Layered BackHandler Navigation** | चैट स्क्रीन, मीडिया व्यूअर, प्रोफ़ाइल बॉटम शीट में लेयर्ड बैक-हैंडलिंग लागू रहेगी। |

---

## 🗄️ 3. सुपाबेस डेटाबेस स्कीमा (2 Core Tables Architecture)

### 1. `public.campus_connections` (फॉलो, फ्रेंडशिप व 0ms प्रोफ़ाइल कैशे)
```sql
CREATE TABLE public.campus_connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    target_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'FOLLOWING', -- 'PENDING', 'FOLLOWING', 'BLOCKED'
    is_mutual BOOLEAN NOT NULL DEFAULT false, -- क्या दोनों ने एक-दूसरे को फॉलो किया है?
    peer_name TEXT,                          -- दोस्त का नाम (0ms ऑफ़लाइन कैशे)
    peer_username TEXT,                      -- दोस्त का @username
    peer_avatar_url TEXT,                    -- सुपाबेस स्टोरेज लिंक
    peer_bio TEXT,                           -- बायो / स्टेटस
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT uq_campus_connection UNIQUE (user_id, target_user_id)
);

CREATE INDEX idx_campus_conn_user ON public.campus_connections(user_id);
CREATE INDEX idx_campus_conn_mutual ON public.campus_connections(user_id, is_mutual);
```

### 2. `public.campus_messages` (E2EE चैट, मीडिया, डिलीवरी टिक्स व 24h ऑटो-सफाई)
```sql
CREATE TABLE public.campus_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id TEXT NOT NULL,           -- दोनों यूज़र्स का सॉर्टेड यूनिक हैश ('conv_userA_userB')
    sender_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    encrypted_payload TEXT NOT NULL,         -- AES-256 डिवाइस-टू-डिवाइस एन्क्रिप्टेड डेटा
    media_url TEXT,                          -- फोटो / वॉइस नोट का स्टोरेज लिंक
    media_type TEXT NOT NULL DEFAULT 'TEXT', -- 'TEXT', 'IMAGE', 'VOICE', 'FILE'
    status TEXT NOT NULL DEFAULT 'SENT',     -- 'SENT', 'DELIVERED', 'READ'
    is_saved BOOLEAN NOT NULL DEFAULT false, -- अगर स्टार/सेव किया है तो 24h में डिलीट नहीं होगा
    expires_at TIMESTAMPTZ DEFAULT (now() + interval '24 hours'),
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ
);

CREATE INDEX idx_campus_msg_conv ON public.campus_messages(conversation_id, created_at DESC);
CREATE INDEX idx_campus_msg_unread ON public.campus_messages(recipient_id, status) WHERE status != 'READ';
CREATE INDEX idx_campus_msg_expires ON public.campus_messages(expires_at) WHERE is_saved = false;
```

---

## ⚙️ 4. ऑटोमैटिक म्यूचुअल फॉलो ट्रिगर (Mutual Follow Synchronization)

```sql
CREATE OR REPLACE FUNCTION public.fn_sync_mutual_connection_state()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.status = 'FOLLOWING') AND EXISTS (
        SELECT 1 FROM public.campus_connections 
        WHERE user_id = NEW.target_user_id 
          AND target_user_id = NEW.user_id 
          AND status = 'FOLLOWING'
          AND is_deleted = false
    ) THEN
        NEW.is_mutual := true;
        UPDATE public.campus_connections 
        SET is_mutual = true, updated_at = NOW()
        WHERE user_id = NEW.target_user_id AND target_user_id = NEW.user_id;
    ELSE
        NEW.is_mutual := false;
        UPDATE public.campus_connections 
        SET is_mutual = false, updated_at = NOW()
        WHERE user_id = NEW.target_user_id AND target_user_id = NEW.user_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_campus_mutual
BEFORE INSERT OR UPDATE ON public.campus_connections
FOR EACH ROW EXECUTE FUNCTION public.fn_sync_mutual_connection_state();
```

---

## 📱 5. लोकल Android Room DB संरचना (Local SQLite)

### (A) `CampusConnectionEntity`
```kotlin
@Entity(
    tableName = "campus_connections",
    indices = [Index(value = ["target_user_id"], unique = true)]
)
data class CampusConnectionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "target_user_id") val targetUserId: String,
    @ColumnInfo(name = "status") val status: String, // 'FOLLOWING', 'BLOCKED'
    @ColumnInfo(name = "is_mutual") val isMutual: Boolean,
    @ColumnInfo(name = "peer_name") val peerName: String?,
    @ColumnInfo(name = "peer_username") val peerUsername: String?,
    @ColumnInfo(name = "peer_avatar_url") val peerAvatarUrl: String?,
    @ColumnInfo(name = "peer_avatar_local_path") val peerAvatarLocalPath: String?, // फोन मेमोरी में सेव DP
    @ColumnInfo(name = "peer_bio") val peerBio: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "SYNCED",
    @ColumnInfo(name = "sync_version") val syncVersion: Long = 1L,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
```

### (B) `CampusMessageEntity`
```kotlin
@Entity(
    tableName = "campus_messages",
    indices = [
        Index(value = ["conversation_id", "created_at"]),
        Index(value = ["recipient_id", "status"])
    ]
)
data class CampusMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "recipient_id") val recipientId: String,
    @ColumnInfo(name = "encrypted_payload") val encryptedPayload: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String?,
    @ColumnInfo(name = "media_local_path") val mediaLocalPath: String?,
    @ColumnInfo(name = "media_type") val mediaType: String = "TEXT",
    @ColumnInfo(name = "status") val status: String, // 'PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED'
    @ColumnInfo(name = "is_saved") val isSaved: Boolean = false,
    @ColumnInfo(name = "expires_at") val expiresAt: String,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "SYNCED",
    @ColumnInfo(name = "sync_version") val syncVersion: Long = 1L,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String
)
```

---

## ⚡ 6. हाइब्रिड इंस्टेंट-सिंक इंजन (Hybrid Zero-Server-Load Engine)

1. **सक्रिय चैट स्क्रीन (Active Screen):**
   - जब यूज़र चैट स्क्रीन पर हो ➔ केवल उस 1 कमरे के लिए Supabase Realtime चैनल (`chat_room_{conv_id}`) ऑन रहेगा (लेटेंसी <80ms)।
   - स्क्रीन से बाहर निकलते ही (`onDispose`) चैनल तुरंत बंद होगा।
2. **बैकग्राउंड एवं इनएक्टिव स्क्रीन:**
   - कोई सॉकेट ऑन नहीं रहेगा।
   - **Firebase FCM High-Priority Data Push** बैकग्राउंड में सीधे Room DB में मैसेज लिखेगा और नोटिफिकेशन दिखाएगा।
3. **0ms अनरीड काउंट (Unread Badges):**
   - चैट लिस्ट में अनरीड काउंट सीधे `Room DB Flow` से 0ms में लोड होगा।
   - चैट स्क्रीन खोलते ही 1 सिंगल बैच RPC (`fn_mark_campus_chat_read`) सर्वर पर ब्लू टिक `✓✓` अपडेट करेगा।

---

## 🔐 7. E2EE मास्टर की वॉल्ट (Logout & Re-Login Chat Restoration)

- साइनअप के समय यूज़र की मास्टर प्राइवेट की `Argon2id(Password, Salt)` द्वारा एन्क्रिप्ट होकर सुपाबेस प्रोफ़ाइल में सुरक्षित रहती है।
- **री-लॉगिन पर:**
  1. यूज़र अपना पासवर्ड डालता है।
  2. ऐप सुपाबेस से एन्क्रिप्टेड वॉल्ट डाउनलोड करके मेमोरी में मास्टर की डिक्रिप्ट करता है।
  3. सुपाबेस से डाउनलोड हुए 24h/सेव्ड मैसेजेस 0ms में डिक्रिप्ट होकर Room DB में वापस दिख जाते हैं।

---

## 🧹 8. 24 घंटे का ऑटो-क्लीनअप (pg_cron Auto Ephemeral Purge)

- सुपाबेस `pg_cron` हर 1 घंटे में बिना सेव किए 24h पुराने मैसेजेस को हमेशा के लिए साफ करेगा:
  ```sql
  DELETE FROM public.campus_messages 
  WHERE is_saved = false AND expires_at < now();
  ```
- **डेटाबेस साइज हमेशा <50 MB और सुपर-फास्ट रहेगा!**
