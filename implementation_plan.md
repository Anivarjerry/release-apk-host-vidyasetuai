# Implementation Plan - Student Delta Sync & Workspace Isolation

यह योजना Supabase में बनाए गए `get_student_delta_updates` RPC फ़ंक्शन को एंड्रॉइड ऐप में जोड़ने और वर्कस्पेस स्विच करने पर पूरी तरह से पुराने छात्र डेटा को साफ़ (Wipe) करके नए सिरे से सिंक करने की है।

---

## Proposed Changes

हम मुख्य रूप से तीन फाइलों में बदलाव करेंगे:

### 1. Remote Data Source Layer

#### [MODIFY] [InstitutionRemoteDataSource.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_institution/data/remote/datasource/InstitutionRemoteDataSource.kt)
* **बदलाव:** हम एक नया फ़ंक्शन `fetchStudentDeltaUpdates` जोड़ेंगे जो Supabase RPC को कॉल करेगा। यह पूर्ववर्ती सभी अलग-अलग फ़ंक्शंस (जैसे `fetchStudents`, `fetchStudentEnrollments`, `fetchStudentAdditionalDetails` आदि) की जगह लेगा।
* **कोड संरचना:**
  ```kotlin
  suspend fun fetchStudentDeltaUpdates(
      orgId: String,
      userRole: String,
      userId: String,
      lastSyncedAt: String?
  ): List<LocalStudentEntity> { // या DTO क्लास जिसे सीधे मैप किया जा सके
      return try {
          val response = SupabaseClient.client.postgrest.rpc(
              "get_student_delta_updates",
              buildJsonObject {
                  put("p_organization_id", orgId)
                  put("p_user_role", userRole)
                  put("p_user_id", userId)
                  if (lastSyncedAt != null) {
                      put("p_last_synced_at", lastSyncedAt)
                  }
              }
          ).decodeList<LocalStudentEntity>() // फ़ंक्शन सीधे LocalStudentEntity की मैपिंग को सपोर्ट करने वाला JSON भेजता है
          response
      } catch (e: Exception) {
          android.util.Log.e("OfflineSync", "Error fetching student delta updates", e)
          emptyList()
      }
  }
  ```

---

### 2. Repository Layer

#### [MODIFY] [InstitutionRepositoryImpl.kt](file:///d:/VidyaSetu%20AI/vidyastu_mboile_app/app/src/main/java/com/vidyasetuai/feature_institution/data/repository/InstitutionRepositoryImpl.kt)
* **बदलाव 1 (डेटाबेस वाइप):** `syncWorkspaceData` फ़ंक्शन के आरंभ में (लाइन 837), नया डेटा फ़ेच करने से पहले Room Dao की मदद से पिछले वर्कस्पेस के सभी छात्र रिकॉर्ड्स को डिलीट कर दिया जाएगा।
* **बदलाव 2 (नया सिंक फ़्लो):**
  पुराने लंबे मल्टिपल एपीआई कॉल्स के ब्लॉक (लाइन 961-1282) को हटाकर सिंगल RPC कॉल (`fetchStudentDeltaUpdates`) से रिप्लेस कर दिया जाएगा।
* **लॉजिक संरचना:**
  ```kotlin
  override suspend fun syncWorkspaceData(userId: String, workspace: Workspace, sessionId: String): Result<Unit> = runCatching {
      val parentOrgId = workspace.parentOrgId
      val orgId = workspace.childOrgId ?: ""
      
      // 0. डेटाबेस वाइप (Option 1: Complete Wipe on Switch)
      dao.clearStudents()
      dao.clearStudentAdditionalFees()
      dao.clearStudentFeePayments()
      dao.clearStudentAttendance()
      
      // 1. Setup details (as is)
      ...
      
      // 2. Buses & Routes (as is)
      ...
      
      // 3. Students Sync (Optimized using RPC)
      val userRole = workspace.workspaceRole // 'Student', 'Guardian', 'Teacher', 'DRIVER', etc.
      // चूँकि हम पूर्ण डेटा वाइप कर रहे हैं, इसलिए 'p_last_synced_at' को null भेजकर फुल सिंक करेंगे (ताकि वाइप हुआ डेटा आ सके)
      val updatedStudents = remoteDataSource.fetchStudentDeltaUpdates(
          orgId = orgId,
          userRole = userRole,
          userId = userId,
          lastSyncedAt = null // Fresh Workspace switch पर fresh pull
      )
      
      if (updatedStudents.isNotEmpty()) {
          dao.insertStudents(updatedStudents)
      }
      
      // 4. Staff Profiles, Calendar, Exams (as is)
      ...
  }
  ```

---

## Verification Plan

### Automated Tests
हम डेटाबेस डिलीट और सिंक के लॉजिक का परीक्षण करने के लिए यूनिट टेस्ट चला सकते हैं।

### Manual Verification
1. ऐप में लॉगिन करें और **'Student' या 'Guardian' वर्कस्पेस** पर स्विच करें। 
2. चेक करें कि केवल असाइन किया गया छात्र विवरण ही ऑफलाइन लिस्ट में दिख रहा है।
3. **'Driver' वर्कस्पेस** पर स्विच करें। चेक करें कि पुराना छात्र डेटा पूरी तरह से डिलीट हो गया है और डेटाबेस वाइप होने के बाद केवल उस ड्राइवर की बस रूट के छात्रों का डेटा ही मोबाइल में सिंक हुआ है।
4. **'Teacher' या 'Admin' वर्कस्पेस** पर स्विच करें और चेक करें कि उस स्कूल के सभी छात्र सिंक होकर ऑफलाइन विज़िबल हैं।
