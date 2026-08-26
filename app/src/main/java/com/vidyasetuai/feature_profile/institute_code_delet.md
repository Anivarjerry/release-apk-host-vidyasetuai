# InstitutionScreen.kt - Removed Profile Code Record

## 1. Description & Purpose
यह दस्तावेज़ उस कोड का पूर्ण रिकॉर्ड है जो `InstitutionScreen.kt` से `UserProfileDao` और `user_profiles` टेबल को डीकपल (Unlink) करने के लिए हटाया गया था।

### इस कोड का वास्तविक काम क्या था?
- जब यूज़र **Institute Tab** खोलता था, तो यह कोड केवल यूज़र का **`username`** (उपयोगकर्ता नाम) प्रदर्शित करने के लिए इस्तेमाल होता था।
- यह सबसे पहले पुराने `AppDatabase` के `userProfileDao()` में देखता था कि क्या यूज़र का नाम लोकल कैश में है।
- यदि लोकल में नहीं मिलता था, तो Supabase की `user_profiles` टेबल से `username` फ़ेच करके पुराने SQLite डेटाबेस में आधा-अधूरा `UserProfileEntity` इन्सर्ट करता था।

---

## 2. हटाया गया सटीक कोड (Exact Removed Code)

```kotlin
    val profileState = remember(userId) { db.userProfileDao().getProfileFlow(userId) }
        .collectAsState(initial = null)
    var username by remember { mutableStateOf(userId) }

    LaunchedEffect(userId) {
        if (userId.isEmpty()) return@LaunchedEffect
        
        // 1. Check local DB first to prevent redundant network requests
        val cachedProfile = db.userProfileDao().getProfile(userId)
        if (cachedProfile != null && !cachedProfile.username.isNullOrEmpty()) {
            username = cachedProfile.username
            return@LaunchedEffect
        }
        
        // 2. Fetch from network only if missing locally
        try {
            val response = com.vidyasetuai.core.network.SupabaseClient.client.from("user_profiles")
                .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("user_id, username")) {
                    filter { eq("user_id", userId) }
                }.decodeSingleOrNull<UserProfileDto>()
            if (response?.username != null) {
                username = response.username
                db.userProfileDao().insertProfile(
                    com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity(
                        userId = userId,
                        username = response.username,
                        firstName = null,
                        lastName = null,
                        fullName = null,
                        profilePictureUrl = null,
                        coverPhotoUrl = null,
                        bio = null,
                        preferredLanguage = null,
                        isVerified = false,
                        gender = null,
                        dateOfBirth = null
                    )
                )
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error fetching profile from Supabase", e)
        }
    }
```

---

## 3. नये आर्किटेक्चर में इसे कैसे हैंडल किया जाएगा? (Production Solution)
- नये आर्किटेक्चर में **Institute Tab** को प्रोफ़ाइल डेटाबेस से सीधे क्वेरी करने की आवश्यकता नहीं होगी।
- यूज़र का नाम व बायो सीधे `SessionManager` या `ProfileModuleFacade` के 0ms रिएक्टिव प्रोवाइडर से मिल जाएगा, जिससे मॉड्यूलरिटी और स्वतंत्रता (Zero Coupling) बनी रहेगी।
