# रूम डेटाबेस पुनर्गठन योजना (Room Database Reorganization Plan)

इस दस्तावेज़ में हम विद्यासेतु ऐप की रूम डेटाबेस एंटिटीज (Room Database Entities), डीएओ (DAOs), और डेटाबेस संरचना का चरण-दर-चरण विश्लेषण और डिज़ाइन करेंगे। हम हर एक एंटिटी को उपयोगकर्ताओं (Users) की वास्तविक आवश्यकताओं के अनुसार अनुकूलित करेंगे, ताकि ऐप ऑफ़लाइन-फर्स्ट और बेहद स्मूथ (Smooth) काम कर सके।

---

## 📌 डिज़ाइन मार्गदर्शिका (Design Guidelines)
1. **सिंगल सोर्स ऑफ ट्रुथ (Single Source of Truth):** ऐप का यूआई हमेशा रूम डेटाबेस को ऑब्जर्व करेगा।
2. **न्यूनतम तालिकाएं (Normalized & Optimized Tables):** फालतू की लुुकअप/स्थैतिक तालिकाओं को हटाकर मुख्य एंटिटीज में शामिल किया जाएगा।
3. **पैरेंट-आधारित वर्क्सपेस (Parent-Centric Workspace):** वर्क्सपेस एंटिटी में पैरेंट आर्गेनाइजेशन (Parent Organization) मुख्य आधार होगा।

---

## 1. उपयोगकर्ता प्रोफ़ाइल एंटिटी (User Profile Entity - `user_profiles`)

**उद्देश्य:** लॉग-इन उपयोगकर्ता की व्यक्तिगत प्रोफ़ाइल जानकारी और वर्तमान सक्रिय अवस्था (Active State) को संग्रहीत करना ताकि ऐप खुलते ही तुरंत सही स्क्रीन लोड हो सके।

### स्कीमा डिज़ाइन (Schema Design):

| फ़ील्ड का नाम | डेटा प्रकार (Kotlin Type) | सुपबेस तालिका और स्रोत | शून्य स्वीकार्य (Nullable) | विवरण और नियम |
| :--- | :--- | :--- | :--- | :--- |
| `user_id` | `String` (UUID) | `public.user_profiles.user_id` | **नहीं (Primary Key)** | यह रूम डेटाबेस की प्राइमरी की होगी। सुपबेस की Auth ID के समान। |
| `email` | `String` | `auth.users.email` | नहीं | उपयोगकर्ता का ईमेल पता। |
| `is_active` | `Boolean` | `auth.users.is_active` | नहीं | क्या उपयोगकर्ता का खाता सक्रिय है। |
| `is_deleted` | `Boolean` | `public.user_profiles.is_deleted` | नहीं | सॉफ्ट डिलीट स्थिति। `true` होने पर यूजर प्रोफाइल ऐप से हट जाएगी। |
| `username` | `String?` | `public.user_profiles.username` | हाँ | उपयोगकर्ता का यूनिक यूजरनेम। |
| `first_name` | `String?` | `public.user_profiles.first_name` | हाँ | पहला नाम। |
| `last_name` | `String?` | `public.user_profiles.last_name` | हाँ | उपनाम। |
| `full_name` | `String?` | `public.user_profiles.full_name` | हाँ | पूरा नाम (प्रदर्शन के लिए)। |
| `profile_picture_url` | `String?` | `public.user_profiles.profile_picture_url` | हाँ | प्रोफाइल इमेज का सुपबेस यूआरएल (तुलना/सिंक के लिए)। |
| `profile_picture_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | फोन मेमोरी में सेव की गई प्रोफाइल इमेज का लोकल पाथ (UI लोड करने के लिए)। |
| `cover_photo_url` | `String?` | `public.user_profiles.cover_photo_url` | हाँ | कवर इमेज का सुपबेस यूआरएल (तुलना/सिंक के लिए)। |
| `cover_photo_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | फोन मेमोरी में सेव की गई कवर इमेज का लोकल पाथ (UI लोड करने के लिए)। |
| `gender` | `String?` | `public.user_profiles.gender` | हाँ | लिंग (Gender)। |
| `date_of_birth` | `String?` | `public.user_profiles.date_of_birth` | हाँ | जन्म तिथि (Date)। |
| `bio` | `String?` | `public.user_profiles.bio` | हाँ | बायोग्राफी (अधिकतम 100 वर्ण)। |
| `preferred_language` | `String?` | `public.user_profiles.preferred_language` | हाँ | उपयोगकर्ता की पसंदीदा भाषा। |
| `is_verified_status` | `String?` | `public.content_contributor_verifications.status` | हाँ | वेरिफिकेशन स्टेटस (`pending`, `approved`, `rejected`, `suspended`)। |
| `active_workspace_id` | `String?` (UUID) | स्थानीय रूम डेटाबेस से | हाँ | वर्तमान में सक्रिय पैरेंट आर्गेनाइजेशन/वर्क्सपेस की आईडी। |
| `active_journey_id` | `String?` (UUID) | स्थानीय रूम डेटाबेस से | हाँ | वर्तमान में सक्रिय जर्नी की आईडी। |
| `total_inspiring_count` | `Int` | `public.user_profiles.total_inspiring_count` | नहीं (Default: 0) | उपयोगकर्ता को कितने लोग फॉलो कर रहे हैं (फॉलोअर्स संख्या)। |
| `total_inspired_count` | `Int` | `public.user_profiles.total_inspired_count` | नहीं (Default: 0) | उपयोगकर्ता कितने लोगों से प्रेरित है (फॉलोइंग संख्या)। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर उत्पन्न | नहीं | सर्वर के साथ अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर उत्पन्न | नहीं | सिंक की स्थिति (`SYNCED`, `PENDING_UPDATE`)। |

### 📈 प्रेरणा काउंट सिंक और डेटा पढ़ने का नियम (Sync & Reading Logic):
* **डेटा सिंक प्रक्रिया (Sync Logic):** 
  * सुपबेस पर लिखे गए डेटाबेस ट्रिगर के कारण, जब भी `user_inspirations` में कोई नया फॉलो/अनफॉलो (सॉफ्ट डिलीट सहित) होता है, तो सुपबेस के `user_profiles` में ये दोनों काउंट ऑटो-अपडेट हो जाते हैं।
  * मोबाइल ऐप जब भी सिंक प्रोसेस चलाएगा, तो प्रोफाइल सिंक एपीआई के माध्यम से अपडेटेड काउंट्स मोबाइल की स्थानीय `user_profiles` रूम टेबल में अपने आप सहेज लिए जाएंगे।
* **डेटा पढ़ने का तरीका (Data Reading):**
  * प्रोफाइल स्क्रीन (Profile Tab) को लोड करते समय, ऐप का `ProfileViewModel` रूम डेटाबेस से वर्तमान यूजर की प्रोफाइल रो को `Flow<UserProfileEntity>` के माध्यम से सीधे ऑब्जर्व करेगा।
  * जैसे ही रूम में सिंक हुआ डेटा सेव होगा, यूआई पर बिना किसी जॉइन क्वेरी (SQL Join) या अतिरिक्त नेटवर्क कॉल के `total_inspiring_count` और `total_inspired_count` तुरंत और रियल-टाइम में रेंडर हो जाएंगे।

---

## 2. वर्क्सपेस एंटिटी (Workspace Entity - `workspaces`)

**डिज़ाइन सिद्धांत:** 
पैरेंट आर्गेनाइजेशन (Parent Organization) इस एंटिटी का मुख्य आधार (Core Base) है। बिना पैरेंट आर्गेनाइजेशन के कोई भी चाइल्ड आर्गेनाइजेशन (यानी सामान्य आर्गेनाइजेशन) संभव नहीं है। हाँ, पैरेंट आर्गेनाइजेशन बिना किसी चाइल्ड आर्गेनाइजेशन के स्वतंत्र रूप से काम कर सकती है। इसलिए, `parent_organization_id` अनिवार्य (Not Null) रहेगा, जबकि चाइल्ड आर्गेनाइजेशन आईडी (`child_organization_id`) वैकल्पिक (Nullable) रहेगी।

### स्कीमा डिज़ाइन (Schema Design):

| फ़ील्ड का नाम | डेटा प्रकार (Kotlin Type) | सुपबेस तालिका और स्रोत | शून्य स्वीकार्य (Nullable) | विवरण और नियम |
| :--- | :--- | :--- | :--- | :--- |
| `workspace_id` | `String` (UUID) | स्थानीय रूम डेटाबेस से | **नहीं (Primary Key 1)** | रूम में प्राइमरी की का पहला भाग। यह एक्टिव वर्क्सपेस की मुख्य आईडी होगी। |
| `workspace_role` | `String` | `organization_..._user_links` | **नहीं (Primary Key 2)** | प्राइमरी की का दूसरा भाग। यूजर की मुख्य श्रेणी (जैसे: `STAFF`, `STUDENT`, `GUARDIAN`)। |
| `workspace_sub_role` | `String` | `global_staff_roles` और लिंकेज से | नहीं | विशिष्ट भूमिका। छात्र/अभिभावक के लिए समान रहेगा। स्टाफ के मामले में `TEACHER`, `ADMIN`, `DRIVER` आदि रहेगा। |
| `parent_organization_id` | `String` (UUID) | `public.organizations.parent_organization_id` | **नहीं (मुख्य आधार)** | पैरेंट आर्गेनाइजेशन की आईडी। यह कभी भी नल (Null) नहीं हो सकती। |
| `parent_organization_name` | `String?` | `public.organization_parents.name` | हाँ | पैरेंट आर्गेनाइजेशन का नाम। |
| `parent_org_logo_url` | `String?` | `public.organization_parents_profiles.logo_url` | हाँ | पैरेंट आर्गेनाइजेशन का लोगो सुपबेस यूआरएल (तुलना/सिंक के लिए)। |
| `parent_org_logo_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | फोन मेमोरी में सेव की गई पैरेंट लोगो इमेज का लोकल पाथ। |
| `parent_org_email` | `String?` | `public.organization_parents_profiles.email` | हाँ | पैरेंट आर्गेनाइजेशन का ईमेल। |
| `parent_org_mobile` | `String?` | `public.organization_parents_profiles.mobile_number` | हाँ | पैरेंट आर्गेनाइजेशन का संपर्क नंबर। |
| `parent_org_active_session_id` | `String` (UUID) | `public.organization_parents_profiles.active_session_id` | नहीं | पैरेंट आर्गेनाइजेशन का चालू शैक्षणिक सत्र आईडी। |
| `parent_org_active_session_name` | `String?` | `public.global_sessions.name` से | हाँ | पैरेंट आर्गेनाइजेशन के चालू शैक्षणिक सत्र का नाम (जैसे: `"2026-2027"`)। |
| `parent_org_website_url` | `String?` | `public.parent_organization_websites.subdomain` से कंप्यूटेड | हाँ | `"https://" + subdomain + "/vidyasetuai.com"` |
| `child_organization_id` | `String?` (UUID) | `public.organizations.id` | **हाँ** | चाइल्ड आर्गेनाइजेशन (शाखा/स्कूल) की आईडी। अगर केवल पैरेंट आर्गेनाइजेशन एक्टिव है, तो यह नल रहेगी। |
| `child_organization_name` | `String?` | `public.organizations.name` | हाँ | चाइल्ड आर्गेनाइजेशन का नाम। |
| `child_org_logo_url` | `String?` | `public.organization_profiles.logo_url` | हाँ | चाइल्ड आर्गेनाइजेशन का लोगो सुपबेस यूआरएल (तुलना/सिंक के लिए)। |
| `child_org_logo_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | फोन मेमोरी में सेव की गई चाइल्ड लोगो इमेज का लोकल पाथ। |
| `child_org_email` | `String?` | `public.organization_profiles.email` | हाँ | चाइल्ड आर्गेनाइजेशन का ईमेल। |
| `child_org_mobile_number` | `String?` | `public.organization_profiles.mobile_number` | हाँ | चाइल्ड आर्गेनाइजेशन का मोबाइल नंबर। |
| `child_org_alternate_mobile` | `String?` | `public.organization_profiles.alternate_mobile_number` | हाँ | चाइल्ड आर्गेनाइजेशन का वैकल्पिक नंबर। |
| `child_org_active_session_id` | `String?` (UUID) | `public.organization_profiles.active_session_id` | हाँ | चाइल्ड आर्गेनाइजेशन का चालू शैक्षणिक सत्र आईडी। |
| `child_org_active_session_name` | `String?` | `public.global_sessions.name` से | हाँ | चाइल्ड आर्गेनाइजेशन के चालू शैक्षणिक सत्र का नाम। |
| `child_org_address_line1` | `String?` | `public.organization_profiles.address_line1` | हाँ | चाइल्ड आर्गेनाइजेशन का पता (पंक्ति 1)। |
| `child_org_address_line2` | `String?` | `public.organization_profiles.address_line2` | हाँ | चाइल्ड आर्गेनाइजेशन का पता (पंक्ति 2)। |
| `child_org_city` | `String?` | `public.organization_profiles.city` | हाँ | शहर। |
| `child_org_state` | `String?` | `public.organization_profiles.state` | हाँ | राज्य (Default: Rajasthan)। |
| `child_org_pincode` | `String?` | `public.organization_profiles.pincode` | हाँ | पिनकोड। |
| `staff_id` | `String?` (UUID) | `public.organization_parent_staff.id` | हाँ | यदि स्टाफ रोल है, तो स्टाफ की आईडी। |
| `student_id` | `String?` (UUID) | `public.organization_students.id` | हाँ | यदि स्टूडेंट रोल है, तो स्टूडेंट की आईडी। |
| `guardian_id` | `String?` (UUID) | `public.organization_guardians.id` | हाँ | यदि गार्जियन रोल है, तो गार्जियन की आईडी। |
| `role_display_name` | `String?` | सम्बंधित तालिका (Staff/Student/Guardian) से | हाँ | वर्क्सपेस के स्वागत हेडर के लिए USER का नाम। |
| `role_mobile_number` | `String?` | सम्बंधित तालिका (Staff/Student/Guardian) से | हाँ | वर्क्सपेस रोल से जुड़ा मोबाइल नंबर। |
| `role_image_url` | `String?` | `public.organization_students.image_url` या अन्य | हाँ | यूजर की रोल-विशिष्ट प्रोफाइल इमेज का सुपबेस यूआरएल (तुलना/सिंक के लिए)। |
| `role_image_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | फोन मेमोरी में सेव की गई यूजर रोल प्रोफाइल इमेज का लोकल पाथ। |
| `is_approved` | `Boolean` | `organization_..._user_links` | नहीं | क्या यह वर्क्सपेस अनुमोदित (Approved) हो चुका है। `false` होने पर यह पेंडिंग रहेगा। |
| `approved_by` | `String?` | `organization_..._user_links` | हाँ | अप्रूव करने वाले एडमिन/यूजर की आईडी। |
| `approved_at` | `String?` | `organization_..._user_links` | हाँ | अप्रूवल मिलने का समय/तारीख। |
| `is_active_now` | `Boolean` | स्थानीय रूम डेटाबेस से | नहीं | क्या वर्तमान में उपयोगकर्ता इसी वर्क्सपेस का उपयोग कर रहा है। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर उत्पन्न | नहीं | अंतिम सिंक का टाइमस्टैम्प। |

---

## 📝 समीक्षा और खुली चर्चा (Open for Discussion)
*   **वर्क्सपेस सिंक प्रक्रिया:** जब यूजर ऐप खोलेगा, तो हम सुपबेस के तीन लिंकेज टेबल्स (`organization_guardian_user_links`, `organization_parent_staff_user_links`, `organization_student_user_links`) से वर्तमान यूजर आईडी के सभी रिकॉर्ड्स मंगाएंगे और इस वर्क्सपेस टेबल को पॉप्युलेट करेंगे।
*   **अनुमोदन (Approval) नियंत्रण:** हम इन तीनों लिंकेज टेबल्स से `is_approved`, `approved_by` और `approved_at` फ़ील्ड्स को सीधे `workspaces` टेबल में स्टोर करेंगे।
    *   यदि किसी वर्क्सपेस के लिए `is_approved = false` है, तो यूजर उस वर्क्सपेस को सक्रिय नहीं कर सकेगा। वह केवल "लंबित अनुमोदन" (Pending Approvals) सूची में दिखाई देगा।
    *   जैसे ही एडमिन उसे सुपबेस पर अप्रूव करेगा, बैकग्राउंड सिंक के दौरान स्थानीय डेटाबेस में `is_approved = true` हो जाएगा और वह वर्क्सपेस तुरंत इस्तेमाल के लिए सक्रिय हो जाएगा।
*   **रोल स्विचिंग (Role Switching):** जब यूजर किसी वर्क्सपेस के अंदर अपना रोल बदलता है (जैसे स्टाफ से गार्डियन), तो रूम डीबी में केवल `is_active_now` को तदनुसार अपडेट किया जाएगा।
*   **सब-रोल रेजोल्यूशन (Sub-role Resolution):** 
    *   जब यूजर का रोल `STUDENT` या `GUARDIAN` होगा, तो `workspace_sub_role` क्रमशः `"STUDENT"` और `"GUARDIAN"` रहेगा।
    *   जब यूजर का रोल `STAFF` होगा, तो सिंक प्रोसेस के दौरान ऐप:
        1. `organization_parent_staff_user_links` से `staff_id` निकालेगी।
        2. `organization_parent_staff` से उस `staff_id` का `role_id` प्राप्त करेगी।
        3. `global_staff_roles` से उस `role_id` का कोड/नाम (जैसे `"TEACHER"`, `"ADMIN"`, `"DRIVER"`, `"ACCOUNTANT"`) प्राप्त करके `workspace_sub_role` में सहेजेगी।
    *   ऐप में प्रवेश करते ही सक्रिय वर्क्सपेस की `workspace_sub_role` के अनुसार उचित डैशबोर्ड (StudentDashboard, GuardianDashboard, TeacherDashboard, DriverDashboard आदि) तुरंत खोला जा सकेगा।
*   **रोल-विशिष्ट प्रोफ़ाइल एकीकरण (Role-specific Profile Integration):**
    *   यूजर की रोल आईडी (`staff_id`, `student_id`, `guardian_id`), नाम (`role_display_name`), मोबाइल नंबर (`role_mobile_number`), और फोटो (`role_image_url`) को सीधे `workspaces` टेबल में जोड़ दिया गया है।
    *   **फायदा:** इससे वर्क्सपेस स्विच करते ही ऐप बिना किसी देरी या जॉइन (Join) क्वेरी के यूजर का स्वागत संदेश (जैसे: *"नमस्ते, अमित सिंह (शिक्षक)"*) और उसका प्रोफाइल चित्र तुरंत दिखा सकती है।
    *   सभी विस्तृत कार्य (जैसे फीस भुगतान, सैलरी स्लिप, होमवर्क) करने के लिए संबंधित विस्तृत तालिकाओं (`LocalStudentEntity`, `LocalParentStaffEntity`, `LocalGuardianEntity`) में मुख्य आईडी के साथ डेटा उपलब्ध रहेगा।
*   **संस्थागत प्रोफ़ाइल एवं एक्टिव सेशन एकीकरण (Org Profiles & Active Session Integration):**
    *   **सक्रिय सत्र (Active Session):** प्रत्येक वर्क्सपेस की सत्र आईडी (`parent_org_active_session_id` और `child_org_active_session_id`) के साथ-साथ अब हम उनके शैक्षणिक सत्र का नाम (`parent_org_active_session_name` और `child_org_active_session_name`) भी `workspaces` तालिका में सीधे सहेजेंगे (जैसे: `"सत्र: 2026-27"`)। 
        *   इससे यूआई (UI) पर वर्तमान सक्रिय सत्र को तुरंत बिना किसी दूसरी टेबल की क्वेरी के दिखाया जा सकता है।
        *   ऐप के भीतर सभी रिकॉर्ड्स (जैसे परीक्षा अंक, अटेंडेंस, फीस रसीद) इसी एक्टिव सेशन आईडी के आधार पर ही फ़िल्टर होंगे।
    *   **प्रोफ़ाइल विवरण (लोगो, ईमेल, पता):** पैरेंट और चाइल्ड संगठन के आवश्यक विवरण (लोगो, ईमेल, मोबाइल नंबर, पता, पिनकोड आदि) भी वर्क्सपेस में सीधे डीनॉर्मलाइज किए गए हैं।
        *   **फायदा:** रसीद प्रिंट करने (Receipt generation), इनवॉइस प्रिंटिंग, स्कूल प्रोफाइल पेज या संपर्क जानकारी दिखाने के लिए बिना इंटरनेट और बिना जॉइन क्वेरी के ये विवरण तुरंत स्थानीय रूप से उपलब्ध रहेंगे।
*   **ऑफ़लाइन छवि भंडारण एवं अद्यतन नीति (Offline Image Caching & Update Policy):**
    *   **दोहरे कॉलम (Dual Columns):** डेटाबेस में प्रत्येक छवि के लिए दो कॉलम रखे गए हैं:
        1. **रिमोट यूआरएल (`..._url`):** सुपबेस का रिमोट एड्रेस।
        2. **लोकल पाथ (`..._local_path`):** फोन के इंटरनल स्टोरेज (जैसे `context.filesDir`) में सेव की गई इमेज फाइल का पाथ।
    *   **डाउनलोड और सिंक प्रक्रिया (Download & Sync Process):**
        *   सिंक के दौरान, यदि सुपबेस से प्राप्त नया यूआरएल रूम में सेव पुराने यूआरएल से भिन्न होता है, तो ऐप बैकग्राउंड में नई इमेज फाइल डाउनलोड करेगी, पुराना फाइल डिलीट करेगी और नए लोकल पाथ एवं रिमोट यूआरएल को डेटाबेस में सेव करेगी।
        *   यदि रिमोट यूआरएल समान रहता है, तो डाउनलोड दोबारा नहीं किया जाएगा और ऑफलाइन सेव की गई लोकल पाथ फाइल से ही इमेज लोड होगी।
    *   **फायदा:** इससे मोबाइल डेटा बचेगा, सुपबेस स्टोरेज पर अनपेक्षित रीड-क्वेरी (Read Queries) कम होंगी, और बिना इंटरनेट के भी प्रोफाइल पिक्चर्स और लोगो तुरंत दिखाई देंगे।

---

## 3. चाइल्ड आर्गेनाइजेशन सेटअप एंटिटी (Child Organization Setup Entity - `local_child_org_setups`)

**डिज़ाइन दृष्टिकोण (एकल एंटिटी बनाम अलग-अलग तालिकाएं):**
चूंकि यह सेटअप डेटा मोबाइल ऐप में **केवल प्रदर्शन (Read-only UI Display)** के लिए सिंक किया जा रहा है और मोबाइल ऐप में इसे कभी भी एडिट (संपादित) नहीं किया जाएगा, इसलिए हर चरण के लिए 8 अलग-अलग तालिकाएं बनाना और उन्हें जॉइन (Join) करना डेटाबेस को बहुत भारी और धीमा बना देगा।

इसके स्थान पर हम **एक ही एकल एंटिटी (Single Consolidated Entity)** बनाएंगे। हम सुपबेस से पूरे सेटअप स्ट्रक्चर (कक्षाएं, सेक्शन, विषय, टाइमटेबल और फीस) को एक ही एपीआई कॉल या सुपबेस कस्टम आरपीसी (RPC) के माध्यम से एक संगठित JSON ट्री के रूप में मंगाएंगे और उसे रूम के **`TypeConverter`** की मदद से एक ही पंक्ति (Row) में सेव कर लेंगे।

### स्कीमा डिज़ाइन (`local_child_org_setups`):

| फ़ील्ड का नाम | डेटा प्रकार (Kotlin Type) | सुपबेस तालिका और स्रोत | शून्य स्वीकार्य (Nullable) | विवरण और नियम |
| :--- | :--- | :--- | :--- | :--- |
| `organization_id` | `String` (UUID) | `public.organizations.id` | **नहीं (Primary Key)** | रूम की प्राइमरी की। प्रत्येक स्कूल शाखा का केवल एक सक्रिय सेटअप रिकॉर्ड होगा। |
| `session_id` | `String` (UUID) | `public.global_sessions.id` | नहीं | सक्रिय शैक्षणिक सत्र की आईडी। |
| `session_name` | `String` | `public.global_sessions.name` | नहीं | शैक्षणिक सत्र का नाम (जैसे: `"2026-27"`)। |
| `boards_json` | `String` (JSON) | `organization_boards` + `global_boards` | नहीं | बोर्डों की सूची। उदा: `[{"id":"...", "name":"CBSE"}]` |
| `mediums_json` | `String` (JSON) | `organization_mediums` + `global_mediums` | नहीं | माध्यमों की सूची। उदा: `[{"id":"...", "name":"English"}]` |
| `languages_json` | `String` (JSON) | `organization_languages` + `global_languages` | नहीं | भाषाओं की सूची। उदा: `[{"id":"...", "name":"Sanskrit"}]` |
| `class_structure_json`| `String` (JSON) | classes + sections + subjects + teachers | नहीं | **पूरा कक्षा-सेक्शन-विषय ढांचा (ट्री स्ट्रक्चर)।** नीचे इसका उदाहरण प्रारूप देखें। |
| `periods_json` | `String` (JSON) | `public.organization_periods` | नहीं | दैनिक टाइमटेबल पीरियड्स की सूची (नाम, समय, ब्रेक स्थिति)। |
| `fees_structure_json` | `String` (JSON) | `organization_fee_assignments` + global | नहीं | प्रत्येक कक्षा के लिए फीस आवंटन और राशि की सूची। |
| `is_setup_complete` | `Boolean` | `public.organization_profiles.is_setup_complete` | नहीं | क्या सेटअप पूरा हो चुका है। |
| `last_synced_at` | `Long` | स्थानीय | नहीं | अंतिम सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय | नहीं | सिंक की स्थिति (`SYNCED`, `PENDING_UPDATE`)। |

---

### `class_structure_json` का उदाहरण प्रारूप (Structure Example):
इस एकल फ़ील्ड के भीतर हम कक्षा, सेक्शन, विषय और उनसे जुड़े शिक्षकों की पूरी जानकारी इस प्रारूप में रखेंगे:
```json
[
  {
    "class_id": "class-uuid-1",
    "class_name": "Class 10",
    "sections": [
      {
        "section_id": "sec-uuid-a",
        "section_name": "Section A",
        "max_capacity": 40,
        "class_teacher_id": "teacher-uuid-1",
        "class_teacher_name": "Amit Sharma"
      }
    ],
    "subjects": [
      {
        "subject_id": "sub-uuid-math",
        "subject_name": "Mathematics",
        "subject_code": "MATH101",
        "subject_teacher_id": "teacher-uuid-2",
        "subject_teacher_name": "Neha Gupta",
        "is_elective": false
      }
    ]
  }
]
```

### एकल एंटिटी के लाभ:
1. **अल्ट्रा फ़ास्ट लोड स्पीड:** यूआई पर क्लास/सेक्शन सूची दिखाने के लिए डेटाबेस को कोई SQL Join नहीं करना पड़ेगा। एक साधारण सेलेक्ट क्वेरी से पूरी संरचना तुरंत मेमोरी में आ जाएगी।
2. **आसान सिंक:** सर्वर से पूरा सेटअप डेटा एक बार में सिंक होगा और डेटाबेस में पुरानी पंक्ति को ओवरराइट (overwrite) कर देगा।
3. **कम जटिलता:** रूम डीबी में 8 टेबल और 8 डीएओ बनाने की आवश्यकता नहीं होगी, केवल एक ही टेबल से सारा काम हो जाएगा।


---

## 4. भूमिका आधारित सिंक एवं एक्सेस नियम (Role-based Sync & Access Rules)

**महत्वपूर्ण सिद्धांत:** ऐप पर किसी भी प्रकार का सेटअप (Setup Configuration) नहीं किया जाएगा। सेटअप हमारे वेब सॉफ़्टवेयर के माध्यम से किया जाता है। मोबाइल ऐप केवल सुपबेस से इस डेटा को सिंक करके सहेजेगी ताकि ऑफ़लाइन रहते हुए इसे तुरंत प्रदर्शित किया जा सके।

### क) सिंक की पहली शर्त (`is_setup_complete = true`):
*   हम केवल उन चाइल्ड आर्गेनाइजेशन्स (शाखाओं) का सेटअप डेटा सिंक करेंगे जिनकी `organization_profiles.is_setup_complete` वैल्यू सुपबेस में `true` होगी।
*   यदि किसी शाखा का सेटअप पूरा नहीं है (`false` है), तो हम रूम डेटाबेस में उसका कॉन्फ़िगरेशन (Classes, Sections, Subjects, Fees) लोड नहीं करेंगे।

### ख) भूमिका-आधारित सिंक स्कोप (Role-based Sync Scope):

हम डेटा सिंक करने की नीति (Sync Policy) को केवल वर्तमान वर्क्सपेस की स्थिति तक सीमित न रखकर **यूजर के सभी उपलब्ध वर्क्सपेस (All Linked Workspaces)** के आधार पर तय करेंगे। इससे रोल बदलते (Switch करते) समय सुपबेस पर दोबारा क्वेरी चलाने की आवश्यकता नहीं होगी।

#### 1. यदि यूजर के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:
*   यदि उपयोगकर्ता का कोई भी स्वीकृत वर्क्सपेस `STAFF` रोल वाला है (भले ही वह वर्तमान में `STUDENT` या `GUARDIAN` के रूप में ऐप चला रहा हो):
*   **सिंक नियम (व्यापक सिंक):** हम पैरेंट आर्गेनाइजेशन के तहत आने वाली **सभी चाइल्ड आर्गेनाइजेशन्स (शाखाओं)** का सेटअप डेटा रूम डेटाबेस में डाउनलोड करके सहेज लेंगे।
*   **यूआई फ़िल्टरिंग (UI Filtering):** 
    *   जब यूजर **`STAFF` वर्क्सपेस** पर रहेगा, तो उसे सभी शाखाओं का डेटा दिखाई देगा।
    *   जब यूजर **`STUDENT` या `GUARDIAN` वर्क्सपेस** पर स्विच करेगा, तो रूम डेटाबेस से कोई भी डेटा डिलीट नहीं होगा। यूआई (UI) केवल एक्टिव वर्क्सपेस की `child_organization_id` के आधार पर डेटा को फ़िल्टर करके प्रदर्शित करेगा।
    *   **लाभ:** इससे रोल स्विचिंग पूरी तरह से ऑफ़लाइन और त्वरित (Instant) हो जाएगी।

#### 2. यदि यूजर के पास केवल `STUDENT` या `GUARDIAN` वर्क्सपेस लिंक्स हैं (कोई `STAFF` रोल नहीं):
*   **सिंक नियम (सीमित सिंक):** हम केवल उन विशिष्ट `organization_id` (शाखाओं) का सेटअप डेटा सिंक करेंगे जिनसे वह सीधे छात्र या अभिभावक के रूप में संबद्ध है।

### ग) स्टाफ डेटा सिंक नियम (Staff Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:**
    *   हम उस **पैरेंट आर्गेनाइजेशन (Parent Organization)** के तहत आने वाले **सभी स्टाफ सदस्यों (All Staff Members)** का डेटा सुपबेस से रूम डेटाबेस की `local_parent_staff` टेबल में सिंक करके ऑफलाइन सुरक्षित कर लेंगे।
    *   **लाभ:** इससे स्टाफ लिस्ट, स्टाफ प्रोफाइल, बस असाइनमेंट, छुट्टी की जानकारी या सैलरी विवरण लोड करने के लिए बार-बार सुपबेस पर कॉल करने की आवश्यकता नहीं होगी।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   सुरक्षा, प्राइवेसी और बैंडविड्थ बचाने के लिए, हम रूम डेटाबेस में अन्य स्टाफ मेंबर्स की सूची सिंक नहीं करेंगे। केवल आवश्यक होने पर संबंधित टीचर की ही प्रोफाइल लोड की जाएगी।

### घ) छात्र डेटा सिंक नियम (Student Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:**
    *   हम उन **सभी चाइल्ड आर्गेनाइजेशन्स (शाखाओं)** के **सभी छात्रों (All Students)** का डेटा सुपबेस से रूम डेटाबेस की `local_students` टेबल में सिंक करेंगे, जिनसे वह स्टाफ संबद्ध है।
    *   **लाभ:** शिक्षकों/स्टाफ़ को अटेंडेंस लेने, परीक्षा अंक अपलोड करने, या छात्रों की सूची देखने के लिए किसी भी इंटरनेट की आवश्यकता नहीं होगी।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   हम केवल उन **विशिष्ट छात्रों (Only Linked Students)** का डेटा रूम डेटाबेस में डाउनलोड करेंगे, जिनसे वह लॉग-इन अभिभावक/छात्र सीधे जुड़ा हुआ है।

### ङ) शुल्क डेटा सिंक नियम (Fee Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है (विशेषकर Admin/Finance Staff):**
    *   हम संबंधित शाखा (`child_organization_id`) के **सभी छात्रों के अतिरिक्त शुल्क (Additional Fees) और शुल्क भुगतान (Fee Payments)** रिकॉर्ड सुपबेस से रूम डेटाबेस में सिंक करेंगे।
    *   **लाभ:** फ़ीस स्टाफ़ या प्रबंधन स्कूल के कुल दैनिक कलेक्शन और फ़ीस डिफ़ॉल्टरों की सूची ऑफ़लाइन भी सुचारू रूप से देख सकेगा।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   हम केवल लॉग-इन अकाउंट से जुड़े **विशिष्ट छात्रों** के ही अतिरिक्त शुल्क और भुगतान इतिहास को स्थानीय रूप से सिंक करेंगे।

### च) व्यय डेटा सिंक नियम (Expense Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है (विशेषकर Admin/Management Staff):**
    *   हम उस **पैरेंट आर्गेनाइजेशन (Parent Organization)** के तहत चालू शैक्षणिक सत्र के **सभी व्यय (All Expenses)** सुपबेस से रूम डेटाबेस की `local_parent_expenses` टेबल में सिंक करेंगे।
    *   **लाभ:** स्कूल प्रबंधन या एडमिन बिना इंटरनेट के भी दैनिक/मासिक खर्चों की सूची और कुल खर्चों की गणना ऑफ़लाइन देख सकते हैं।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   सुरक्षा और गोपनीयता कारणों से, हम छात्र/अभिभावक ऐप में स्कूल के आंतरिक खर्चों (Expenses) का डेटा सिंक नहीं करेंगे।

### छ) उपस्थिति डेटा सिंक नियम (Attendance Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:**
    *   हम चालू शैक्षणिक सत्र में उस स्टाफ से संबंधित शाखा (`child_organization_id`) के **पिछले 30 दिनों के सभी छात्रों के उपस्थिति (Attendance) रिकॉर्ड्स** सुपबेस से स्थानीय डेटाबेस में सिंक करेंगे।
    *   **लाभ:** शिक्षक ऑफ़लाइन रहकर भी पिछले कुछ दिनों की उपस्थिति रिपोर्ट देख सकते हैं और नई अटेंडेंस दर्ज कर सकते हैं, जो बाद में इंटरनेट आने पर सुपबेस से सिंक हो जाएगी।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   हम केवल लॉग-इन अकाउंट से जुड़े **विशिष्ट छात्रों के चालू सत्र की पूरी उपस्थिति का इतिहास (Attendance History)** सिंक करेंगे ताकि अभिभावक अपने बच्चों का संपूर्ण अटेंडेंस प्रतिशत ऑफ़लाइन देख सकें।

### ज) बस ट्रिप एवं लॉग्स सिंक नियम (Bus Trip & Attendance Logs Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है (विशेषकर Driver/Conductor):**
    *   हम ड्राइवर से जुड़ी बस के **आज और कल के ट्रिप शेड्यूल (Bus Trips)** और उन ट्रिप्स के **सभी छात्रों के क्यूआर बोर्डिंग लॉग (Boarded/Dropped Logs)** स्थानीय रूप से सिंक करेंगे।
    *   **लाभ:** ड्राइवर/कंडक्टर ट्रिप को ऑफ़लाइन स्टार्ट और एंड कर सकते हैं, तथा मार्ग में नेटवर्क न होने पर भी बच्चों के क्यूआर कोड को स्कैन करके 'Boarded' या 'Dropped' दर्ज कर सकते हैं। यह पूरा डेटा बाद में इंटरनेट आने पर सुपबेस से सिंक हो जाएगा।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   हम छात्र के असाइन किए गए बस रूट की **सक्रिय/चालू ट्रिप स्थिति (Ongoing Trip Status)** और उस छात्र के **वर्तमान सत्र के ट्रिप बोर्डिंग और ड्रॉपिंग लॉग (GPS लोकेशन के साथ)** को ऑफ़लाइन सिंक करेंगे, जिससे अभिभावक बच्चों की सुरक्षा ट्रैक कर सकें।

### झ) अवकाश डेटा सिंक नियम (Leave Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:**
    *   *यदि Admin/Principal/Management Staff है:* हम पैरेंट आर्गेनाइजेशन के चालू शैक्षणिक सत्र के **सभी स्टाफ़ और छात्रों के अवकाश आवेदन (All Leaves)** सिंक करेंगे, जिससे वे ऑफ़लाइन समीक्षा और स्वीकृत/अस्वीकृत (Approve/Reject) कर सकें।
    *   *यदि शिक्षक/अन्य स्टाफ़ है:* हम स्वयं द्वारा लिए गए अवकाश और संबंधित वर्ग (Class/Section) के छात्रों के सभी अवकाश रिकॉर्ड्स सिंक करेंगे।
    *   **लाभ:** कर्मचारी ऑफ़लाइन भी अवकाश आवेदन कर सकते हैं और प्रधानाचार्य/एडमिन ऑफ़लाइन रहकर भी निर्णय (Approve/Reject) ले सकते हैं।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   हम केवल लॉग-इन अभिभावक से सम्बद्ध **छात्रों के चालू सत्र के अवकाश रिकॉर्ड्स (Leave Applications)** को ऑफ़लाइन सिंक करेंगे, तथा ऑफ़लाइन नया अवकाश आवेदन प्रस्तुत करने की सुविधा देंगे।

### ञ) रिमार्क डेटा सिंक नियम (Remark Data Sync Rule):
*   **यदि उपयोगकर्ता के पास कम से कम एक `STAFF` वर्क्सपेस लिंक है:**
    *   *यदि Admin/Principal/Management Staff है:* हम संस्था के चालू शैक्षणिक सत्र के **सभी रिमार्क्स (All Remarks)** रूम डेटाबेस की `local_organization_remarks` टेबल में सिंक करेंगे।
    *   *यदि शिक्षक/अन्य स्टाफ़ है:* हम शिक्षक द्वारा लिखे गए स्वयं के रिमार्क्स और उनकी आवंटित कक्षाओं के छात्रों को मिले सभी रिमार्क्स सिंक करेंगे।
    *   **लाभ:** शिक्षक ऑफ़लाइन रहकर भी नए रिमार्क जोड़ सकते हैं (जैसे क्लास में किसी छात्र के व्यवहार के प्रति नोट लिखना), जो बाद में इंटरनेट मिलने पर सिंक हो जाएंगे।
*   **यदि उपयोगकर्ता के पास कोई `STAFF` वर्क्सपेस लिंक नहीं है (केवल STUDENT या GUARDIAN है):**
    *   गोपनीयता और डेटा सुरक्षा कारणों से, हम केवल लॉग-इन अभिभावक से सम्बद्ध **छात्रों को जारी किए गए विशिष्ट रिमार्क्स (Only Target Remarks)** ही ऑफ़लाइन सिंक करेंगे। अभिभावक को अन्य किसी छात्र का रिमार्क डाउनलोड नहीं होगा।

---

## 5. एकीकृत स्टाफ एंटिटी (Unified Staff Entity - `local_parent_staff`)

**डिज़ाइन दृष्टिकोण:** 
स्टाफ से जुड़ी सभी बुनियादी एवं व्यावसायिक जानकारियों (सैलरी, लीव कोटा और बस ड्यूटी आवंटन) को एक ही टेबल में समेकित किया गया है। इससे यूआई पर स्टाफ लिस्ट और प्रोफाइल लोड करते समय बिना किसी जटिल SQL Join या सुपबेस कॉल्स के, माइक्रो-सेकंड्स में पूरी जानकारी प्राप्त हो जाएगी।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण और मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_staff.id` | **नहीं (Primary Key)** | स्टाफ की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_staff.parent_organization_id` | नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_staff.active_session_id` | नहीं | शैक्षणिक सत्र आईडी। |
| `name` | `String` | `public.organization_parent_staff.name` | नहीं | स्टाफ का नाम। |
| `mobile_number` | `String` | `public.organization_parent_staff.mobile_number` | नहीं | मोबाइल नंबर। |
| `email` | `String?` | `public.organization_parent_staff.email` | हाँ | ईमेल पता। |
| `gender` | `String?` | `public.organization_parent_staff.gender` | हाँ | लिंग (`Male`, `Female`, `Other`)। |
| `date_of_joining` | `String` | `public.organization_parent_staff.date_of_joining` | नहीं | शामिल होने की तिथि (Format: Date/String)। |
| `date_of_birth` | `String?` | `public.organization_parent_staff.date_of_birth` | हाँ | जन्म तिथि। |
| `pan_number` | `String?` | `public.organization_parent_staff.pan_number` | हाँ | पैन नंबर। |
| `aadhaar_number` | `String?` | `public.organization_parent_staff.aadhaar_number` | हाँ | आधार संख्या। |
| `license_number` | `String?` | `public.organization_parent_staff.license_number` | हाँ | ड्राइविंग लाइसेंस नंबर (ड्राइवर स्टाफ हेतु)। |
| `license_expiry_date` | `String?` | `public.organization_parent_staff.license_expiry_date` | हाँ | लाइसेंस समाप्ति तिथि। |
| `is_active` | `Boolean` | `public.organization_parent_staff.is_active` | नहीं | क्या स्टाफ सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_staff.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| **`role_id`** | `String?` (UUID) | `public.organization_parent_staff.role_id` | हाँ | स्टाफ की भूमिका की आईडी। |
| **`role_name`** | `String?` | `public.global_staff_roles.name` | हाँ | **(Resolved Name):** `role_id` के आधार पर रिज़ॉल्व्ड नाम (जैसे: `"Teacher"`, `"Principal"`)। |
| **`subject_id`** | `String?` (UUID) | `public.organization_parent_staff.subject_id` | हाँ | शिक्षक का विषय आईडी। |
| **`subject_name`** | `String?` | `public.global_subjects.name` | हाँ | **(Resolved Name):** `subject_id` के आधार पर रिज़ॉल्व्ड विषय नाम (जैसे: `"Mathematics"`)। |
| **`address_area_id`** | `String?` (UUID) | `public.organization_parent_staff.address_area_id` | हाँ | पता क्षेत्र आईडी। |
| **`address_area_name`** | `String?` | `public.global_areas.name` | हाँ | **(Resolved Name):** `address_area_id` के आधार पर रिज़ॉल्व्ड क्षेत्र का नाम। |
| **`leave_quota_id`** | `String?` (UUID) | `public.organization_parent_staff_leave_quotas.id` | हाँ | लीव कोटा रिकॉर्ड आईडी (जहाँ `staff_id = id` और `active_session_id = active_session_id`)। |
| **`total_leaves`** | `Double` | `public.organization_parent_staff_leave_quotas.total_leaves` | नहीं (Default `12.0`) | आवंटित कुल छुट्टियाँ। |
| **`salary_id`** | `String?` (UUID) | `public.organization_parent_staff_salaries.id` | हाँ | सैलरी रिकॉर्ड आईडी (जहाँ `staff_id = id` और `active_session_id = active_session_id`)। |
| **`monthly_salary`** | `Double` | `public.organization_parent_staff_salaries.monthly_salary` | नहीं (Default `0.0`) | मासिक सैलरी। |
| **`bank_name`** | `String?` | `public.organization_parent_staff_salaries.bank_name` | हाँ | बैंक का नाम। |
| **`bank_account_number`**| `String?` | `public.organization_parent_staff_salaries.bank_account_number`| हाँ | बैंक खाता संख्या। |
| **`ifsc_code`** | `String?` | `public.organization_parent_staff_salaries.ifsc_code` | हाँ | बैंक IFSC कोड। |
| **`upi_id`** | `String?` | `public.organization_parent_staff_salaries.upi_id` | हाँ | UPI आईडी। |
| **`bus_assignment_id`**| `String?` (UUID) | `public.organization_parent_bus_staff_assignments.id` | हाँ | बस आवंटन असाइनमेंट आईडी (जहाँ `staff_id = id` और `active_session_id = active_session_id`)। |
| **`bus_id`** | `String?` (UUID) | `public.organization_parent_bus_staff_assignments.bus_id` | हाँ | आवंटित बस की आईडी। |
| **`bus_number`** | `String?` | `public.organization_parent_buses.bus_number` | हाँ | **(Joined Name):** बस का नंबर प्लेट (जहाँ `public.organization_parent_buses.id = bus_id`)। |
| **`bus_name`** | `String?` | `public.organization_parent_buses.bus_name` | हाँ | **(Joined Name):** बस का नाम। |
| **`route_name`** | `String?` | `public.organization_parent_buses.route_name` | हाँ | **(Joined Name):** बस का मार्ग/रूट नाम। |
| **`role_in_bus`** | `String?` | `public.organization_parent_bus_staff_assignments.role_in_bus`| हाँ | बस में स्टाफ की भूमिका (`Driver`, `Conductor`, `Helper`)। |
| **`last_synced_at`** | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| **`sync_state`** | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_staff")
data class LocalParentStaffEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_staff.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "mobile_number")
    val mobileNumber: String,
    
    @ColumnInfo(name = "email")
    val email: String?,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "date_of_joining")
    val dateOfJoining: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    
    @ColumnInfo(name = "pan_number")
    val panNumber: String?,
    
    @ColumnInfo(name = "aadhaar_number")
    val aadhaarNumber: String?,
    
    @ColumnInfo(name = "license_number")
    val licenseNumber: String?,
    
    @ColumnInfo(name = "license_expiry_date")
    val licenseExpiryDate: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Resolved Global Details
    @ColumnInfo(name = "role_id")
    val roleId: String?,
    @ColumnInfo(name = "role_name")
    val roleName: String?,
    @ColumnInfo(name = "subject_id")
    val subjectId: String?,
    @ColumnInfo(name = "subject_name")
    val subjectName: String?,
    @ColumnInfo(name = "address_area_id")
    val addressAreaId: String?,
    @ColumnInfo(name = "address_area_name")
    val addressAreaName: String?,

    // Leave Quota Details
    @ColumnInfo(name = "leave_quota_id")
    val leaveQuotaId: String?,
    @ColumnInfo(name = "total_leaves")
    val totalLeaves: Double,

    // Salary & Bank Details
    @ColumnInfo(name = "salary_id")
    val salaryId: String?,
    @ColumnInfo(name = "monthly_salary")
    val monthlySalary: Double,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "bank_account_number")
    val bankAccountNumber: String?,
    @ColumnInfo(name = "ifsc_code")
    val ifscCode: String?,
    @ColumnInfo(name = "upi_id")
    val upiId: String?,

    // Bus Assignment Details
    @ColumnInfo(name = "bus_assignment_id")
    val busAssignmentId: String?,
    @ColumnInfo(name = "bus_id")
    val busId: String?,
    @ColumnInfo(name = "bus_number")
    val busNumber: String?,
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    @ColumnInfo(name = "route_name")
    val routeName: String?,
    @ColumnInfo(name = "role_in_bus")
    val roleInBus: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 6. एकीकृत बस एंटिटी (Unified Bus Entity - `local_parent_buses`)

**डिज़ाइन दृष्टिकोण:** 
बस के दस्तावेज़ों (Insurance, Fitness, Pollution Certificate) को ऑफ़लाइन एक्सेस करने के लिए इमेज यूआरएल के साथ-साथ लोकल इमेज पाथ का भी उपयोग किया जाएगा। इसके अतिरिक्त, छात्रों/अभिभावकों के त्वरित कॉल और संपर्क के लिए ड्राइवर एवं कंडक्टर के मोबाइल नंबर और नाम को इसी एंटिटी में समाहित (Denormalize) किया गया है।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण और मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_buses.id` | **नहीं (Primary Key)** | बस की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_buses.parent_organization_id` | नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_buses.active_session_id` | नहीं | सक्रिय शैक्षणिक सत्र की आईडी। |
| `bus_number` | `String` | `public.organization_parent_buses.bus_number` | नहीं | बस का रजिस्ट्रेशन/प्लेट नंबर। |
| `bus_name` | `String?` | `public.organization_parent_buses.bus_name` | हाँ | बस का नाम। |
| `route_name` | `String?` | `public.organization_parent_buses.route_name` | हाँ | बस का रूट/मार्ग नाम। |
| `max_capacity` | `Int?` | `public.organization_parent_buses.max_capacity` | हाँ | बस की अधिकतम बैठक क्षमता। |
| `is_active` | `Boolean` | `public.organization_parent_buses.is_active` | नहीं | क्या बस सेवा सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_buses.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `insurance_expiry_date` | `String?` | `public.organization_parent_buses.insurance_expiry_date` | हाँ | बीमा वैधता समाप्त होने की तारीख। |
| `insurance_image_url` | `String?` | `public.organization_parent_buses.insurance_image_url` | हाँ | बीमा सर्टिफिकेट इमेज का सुपबेस यूआरएल। |
| `insurance_image_local_path`| `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन देखने के लिए बीमा इमेज का लोकल पाथ। |
| `fitness_expiry_date` | `String?` | `public.organization_parent_buses.fitness_expiry_date` | हाँ | फिटनेस सर्टिफिकेट की समाप्ति तारीख। |
| `fitness_image_url` | `String?` | `public.organization_parent_buses.fitness_image_url` | हाँ | फिटनेस सर्टिफिकेट इमेज का सुपबेस यूआरएल। |
| `fitness_image_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन देखने के लिए फिटनेस इमेज का लोकल पाथ। |
| `pollution_expiry_date` | `String?` | `public.organization_parent_buses.pollution_expiry_date` | हाँ | प्रदूषण प्रमाण पत्र की समाप्ति तारीख। |
| `pollution_image_url` | `String?` | `public.organization_parent_buses.pollution_image_url` | हाँ | प्रदूषण सर्टिफिकेट इमेज का सुपबेस यूआरएल। |
| `pollution_image_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन देखने के लिए प्रदूषण इमेज का लोकल पाथ। |
| **`driver_id`** | `String?` (UUID) | `public.organization_parent_bus_staff_assignments.staff_id`| हाँ | **(Resolved):** ड्राइवर की स्टाफ आईडी (जहाँ `role_in_bus = 'Driver'`)। |
| **`driver_name`** | `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** आवंटित ड्राइवर का नाम। |
| **`driver_mobile`** | `String?` | `public.organization_parent_staff.mobile_number` | हाँ | **(Resolved Mobile):** ड्राइवर का संपर्क मोबाइल नंबर। |
| **`conductor_id`** | `String?` (UUID) | `public.organization_parent_bus_staff_assignments.staff_id`| हाँ | **(Resolved):** कंडक्टर की स्टाफ आईडी (जहाँ `role_in_bus = 'Conductor'`)। |
| **`conductor_name`** | `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** आवंटित कंडक्टर का नाम। |
| **`conductor_mobile`** | `String?` | `public.organization_parent_staff.mobile_number` | हाँ | **(Resolved Mobile):** कंडक्टर का संपर्क मोबाइल नंबर। |
| **`last_latitude`** | `Double?` | स्थानीय सेंसर/ट्रैकिंग से | हाँ | ऑफ़लाइन प्रदर्शित करने के लिए अंतिम ज्ञात अक्षांश (Latitude)। |
| **`last_longitude`** | `Double?` | स्थानीय सेंसर/ट्रैकिंग से | हाँ | ऑफ़लाइन प्रदर्शित करने के लिए अंतिम ज्ञात देशांतर (Longitude)। |
| **`last_location_updated_at`**| `Long?` | स्थानीय सेंसर/ट्रैकिंग से | हाँ | लोकेशन अपडेट होने का अंतिम समय। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_buses")
data class LocalParentBusEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_buses.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "bus_number")
    val busNumber: String,
    
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    
    @ColumnInfo(name = "route_name")
    val routeName: String?,
    
    @ColumnInfo(name = "max_capacity")
    val maxCapacity: Int?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Insurance Details & Cache
    @ColumnInfo(name = "insurance_expiry_date")
    val insuranceExpiryDate: String?,
    @ColumnInfo(name = "insurance_image_url")
    val insuranceImageUrl: String?,
    @ColumnInfo(name = "insurance_image_local_path")
    val insuranceImageLocalPath: String?,

    // Fitness Details & Cache
    @ColumnInfo(name = "fitness_expiry_date")
    val fitnessExpiryDate: String?,
    @ColumnInfo(name = "fitness_image_url")
    val fitnessImageUrl: String?,
    @ColumnInfo(name = "fitness_image_local_path")
    val fitnessImageLocalPath: String?,

    // Pollution Details & Cache
    @ColumnInfo(name = "pollution_expiry_date")
    val pollutionExpiryDate: String?,
    @ColumnInfo(name = "pollution_image_url")
    val pollutionImageUrl: String?,
    @ColumnInfo(name = "pollution_image_local_path")
    val pollutionImageLocalPath: String?,

    // Resolved Driver Details
    @ColumnInfo(name = "driver_id")
    val driverId: String?,
    @ColumnInfo(name = "driver_name")
    val driverName: String?,
    @ColumnInfo(name = "driver_mobile")
    val driverMobile: String?,

    // Resolved Conductor Details
    @ColumnInfo(name = "conductor_id")
    val conductorId: String?,
    @ColumnInfo(name = "conductor_name")
    val conductorName: String?,
    @ColumnInfo(name = "conductor_mobile")
    val conductorMobile: String?,

    // Last Location Tracking Cache
    @ColumnInfo(name = "last_latitude")
    val lastLatitude: Double?,
    @ColumnInfo(name = "last_longitude")
    val lastLongitude: Double?,
    @ColumnInfo(name = "last_location_updated_at")
    val lastLocationUpdatedAt: Long?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 7. एकीकृत छात्र एंटिटी (Unified Student Entity - `local_students`)

**डिज़ाइन दृष्टिकोण:** 
छात्र प्रोफ़ाइल, अतिरिक्त परिवार/अकादमिक/बैंक विवरण, वर्तमान कक्षा नामांकन (Enrollment), सक्रिय क्यूआर कोड (QR Identity), छात्र आईडी कार्ड और घर की लोकेशन को एक ही विस्तृत एंटिटी में समेकित किया गया है। 
इसके अतिरिक्त, छात्र की फ़ोटो (`image_local_path`) और अभिभावक की फ़ोटो (`guardian_image_local_path`) को ऑफ़लाइन उपलब्धता सुनिश्चित करने के लिए स्थानीय स्तर पर कैश (Cache) किया जाएगा।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण और मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_students.id` | **नहीं (Primary Key)** | छात्र की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_students.organization_id` | नहीं | चाइल्ड आर्गेनाइजेशन (शाखा) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_students.active_session_id` | नहीं | सक्रिय शैक्षणिक सत्र आईडी। |
| `name` | `String` | `public.organization_students.name` | नहीं | छात्र का नाम। |
| `gender` | `String?` | `public.organization_students.gender` | हाँ | लिंग (`Male`, `Female`, `Other`)। |
| `sr_number` | `String` | `public.organization_students.sr_number` | नहीं | Scholar Register (SR) नंबर। |
| `admission_date` | `String` | `public.organization_students.admission_date` | नहीं | प्रवेश तिथि। |
| `date_of_birth` | `String` | `public.organization_students.date_of_birth` | नहीं | जन्म तिथि। |
| `enrollment_number` | `String?` | `public.organization_students.enrollment_number` | हाँ | स्कूल पंजीकरण/नामांकन नंबर। |
| `image_url` | `String?` | `public.organization_students.image_url` | हाँ | छात्र की फ़ोटो का सुपबेस स्टोरेज यूआरएल। |
| `image_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन फ़ोटो का लोकल पाथ। |
| `guardian_image_url` | `String?` | `public.organization_students.guardian_image_url` | हाँ | अभिभावक की फ़ोटो का सुपबेस स्टोरेज यूआरएल। |
| `guardian_image_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन अभिभावक फ़ोटो का लोकल पाथ। |
| `is_active` | `Boolean` | `public.organization_students.is_active` | नहीं | क्या छात्र सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_students.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| **`guardian_id`** | `String` (UUID) | `public.organization_students.guardian_id` | नहीं | अभिभावक की यूनिक आईडी। |
| **`guardian_name`** | `String?` | `public.organization_guardians.name` | हाँ | **(Resolved):** अभिभावक का नाम (जहाँ `id = guardian_id`)। |
| **`guardian_mobile`** | `String?` | `public.organization_guardians.mobile_number` | हाँ | **(Resolved):** अभिभावक का मोबाइल नंबर (जहाँ `id = guardian_id`)। |
| **`guardian_relationship_name`**| `String?` | `public.global_relationship_types.name` | हाँ | **(Resolved):** अभिभावक से संबंध का नाम। |
| `category_id` | `String?` (UUID) | `public.organization_students.category_id` | हाँ | छात्र श्रेणी आईडी। |
| **`category_name`** | `String?` | `public.global_student_categories.name` | हाँ | **(Resolved):** छात्र की श्रेणी (जैसे: `"OBC"`, `"GEN"`)। |
| `blood_group_id` | `String?` (UUID) | `public.organization_students.blood_group_id` | हाँ | ब्लड ग्रुप आईडी। |
| **`blood_group_name`** | `String?` | `public.global_blood_groups.name` | हाँ | **(Resolved):** ब्लड ग्रुप (जैसे: `"O+"`, `"AB-"`)। |
| `student_status_id` | `String?` (UUID) | `public.organization_students.student_status_id` | हाँ | छात्र की वर्तमान स्थिति की आईडी। |
| **`student_status_name`**| `String?` | `public.global_student_status.name` | हाँ | **(Resolved):** छात्र स्टेटस (जैसे: `"Studying"`, `"TC Issued"`)। |
| `address_area_id` | `String?` (UUID) | `public.organization_students.address_area_id` | हाँ | पता क्षेत्र आईडी। |
| **`address_area_name`** | `String?` | `public.global_areas.name` | हाँ | **(Resolved Name):** क्षेत्र का नाम (जैसे: `"Malviya Nagar"`)। |
| `address_details` | `String?` | `public.organization_students.address_details` | हाँ | पते का विस्तृत विवरण। |
| **`class_id`** | `String?` (UUID) | `public.organization_student_enrollments.class_id` | हाँ | सत्र नामांकन से कक्षा आईडी (जहाँ `student_id = id`)। |
| **`class_name`** | `String?` | `public.global_classes.name` / `custom_class_name` | हाँ | **(Resolved Name):** कक्षा का नाम (जैसे: `"Class 10"`)। |
| **`section_id`** | `String?` (UUID) | `public.organization_student_enrollments.section_id` | हाँ | सत्र नामांकन से सेक्शन आईडी। |
| **`section_name`** | `String?` | `public.organization_sections.name` | हाँ | **(Resolved Name):** सेक्शन का नाम (जैसे: `"Section A"`)। |
| **`roll_number`** | `Int?` | `public.organization_student_enrollments.roll_number`| हाँ | छात्र का रोल नंबर। |
| **`qr_identity_id`** | `String?` (UUID) | `public.organization_student_qr_identities.id` | हाँ | सक्रिय क्यूआर आईडी (जहाँ `status = 'Active'`)। |
| **`qr_token_hash`** | `String?` | `public.organization_student_qr_identities.qr_token_hash`| हाँ | क्यूआर टोकन हैश। |
| **`qr_status`** | `String?` | `public.organization_student_qr_identities.status` | हाँ | क्यूआर स्टेटस (`Active`, `Revoked`, `Expired`)। |
| **`qr_expiry_date`** | `String?` | `public.organization_student_qr_identities.expiry_date` | हाँ | क्यूआर कार्ड समाप्ति तिथि। |
| **`id_card_id`** | `String?` (UUID) | `public.organization_student_id_cards.id` | हाँ | वर्तमान आईडी कार्ड की आईडी। |
| **`card_number`** | `String?` | `public.organization_student_id_cards.card_number`| हाँ | आईडी कार्ड नंबर। |
| **`id_card_status`** | `String?` | `public.organization_student_id_cards.status` | हाँ | आईडी कार्ड का प्रिंट/सक्रिय स्टेटस। |
| **`id_card_reissue_reason`**| `String?` | `public.organization_student_id_cards.reason_for_reissue`| हाँ | आईडी कार्ड दोबारा जारी करने का कारण। |
| **`home_latitude`** | `Double?` | `public.organization_student_home_locations.latitude`| हाँ | छात्र के घर का अक्षांश (Latitude)। |
| **`home_longitude`** | `Double?` | `public.organization_student_home_locations.longitude`| हाँ | छात्र के घर का देशांतर (Longitude)। |
| **`mother_tongue_id`** | `String?` (UUID) | `additional_details.mother_tongue_id` | हाँ | मातृभाषा आईडी। |
| **`mother_tongue_name`**| `String?` | `public.global_languages.name` | हाँ | **(Resolved):** मातृभाषा का नाम (जैसे: `"Hindi"`)। |
| `religion` | `String?` | `additional_details.religion` | हाँ | धर्म (`Hindu`, `Muslim`, `Sikh` आदि)। |
| `nationality` | `String` | `additional_details.nationality` | नहीं (Default `Indian`)| राष्ट्रीयता। |
| `identification_mark` | `String?` | `additional_details.identification_mark` | हाँ | पहचान चिन्ह। |
| `is_single_girl_child` | `Boolean` | `additional_details.is_single_girl_child` | नहीं (Default `false`)| क्या वह इकलौती कन्या संतान है। |
| `caste_certificate_number`| `String?` | `additional_details.caste_certificate_number`| हाँ | जाति प्रमाण पत्र संख्या। |
| `father_name` | `String?` | `additional_details.father_name` | हाँ | पिता का नाम। |
| `father_mobile` | `String?` | `additional_details.father_mobile` | हाँ | पिता का मोबाइल नंबर। |
| `father_email` | `String?` | `additional_details.father_email` | हाँ | पिता का ईमेल। |
| `father_qualification` | `String?` | `additional_details.father_qualification` | हाँ | पिता की शैक्षणिक योग्यता। |
| `father_occupation` | `String?` | `additional_details.father_occupation` | हाँ | पिता का व्यवसाय। |
| `parents_aadhaar_father`| `String?` | `additional_details.parents_aadhaar_father` | हाँ | पिता का आधार नंबर। |
| `mother_name` | `String?` | `additional_details.mother_name` | हाँ | माता का नाम। |
| `mother_mobile` | `String?` | `additional_details.mother_mobile` | हाँ | माता का मोबाइल नंबर। |
| `mother_email` | `String?` | `additional_details.mother_email` | हाँ | माता का ईमेल। |
| `mother_qualification` | `String?` | `additional_details.mother_qualification` | हाँ | माता की योग्यता। |
| `mother_occupation` | `String?` | `additional_details.mother_occupation` | हाँ | माता का व्यवसाय। |
| `parents_aadhaar_mother`| `String?` | `additional_details.parents_aadhaar_mother` | हाँ | माता का आधार नंबर। |
| `family_annual_income` | `Double?` | `additional_details.family_annual_income` | हाँ | पारिवारिक वार्षिक आय। |
| `permanent_address_details`| `String?` | `additional_details.permanent_address_details`| हाँ | स्थायी पते का विवरण। |
| `permanent_address_area`| `String?` | `additional_details.permanent_address_area` | हाँ | स्थायी पता क्षेत्र का नाम। |
| `permanent_address_area_id`| `String?` (UUID) | `additional_details.permanent_address_area_id`| हाँ | स्थायी पता क्षेत्र आईडी। |
| **`permanent_area_name`**| `String?` | `public.global_areas.name` | हाँ | **(Resolved):** स्थायी क्षेत्र का नाम (जहाँ `id = permanent_address_area_id`)। |
| `previous_school_name` | `String?` | `additional_details.previous_school_name` | हाँ | पूर्व विद्यालय का नाम। |
| `previous_class` | `String?` | `additional_details.previous_class` | हाँ | पूर्व कक्षा। |
| `previous_board` | `String?` | `additional_details.previous_board` | हाँ | पूर्व बोर्ड का नाम। |
| `tc_number` | `String?` | `additional_details.tc_number` | हाँ | TC नंबर। |
| `tc_date` | `String?` | `additional_details.tc_date` | हाँ | TC की तारीख (Format: Date/String)। |
| `previous_marks` | `Double?` | `additional_details.previous_marks` | हाँ | पूर्व कक्षा के प्राप्तांक। |
| `height` | `Double?` | `additional_details.height` | हाँ | छात्र की लंबाई (cm/inch)। |
| `weight` | `Double?` | `additional_details.weight` | हाँ | छात्र का वजन (kg)। |
| `medical_conditions` | `String?` | `additional_details.medical_conditions` | हाँ | चिकित्सीय स्थिति। |
| `regular_medications` | `String?` | `additional_details.regular_medications` | हाँ | नियमित दवाएं। |
| `emergency_contact_name`| `String?` | `additional_details.emergency_contact_name` | हाँ | आपातकालीन संपर्क व्यक्ति का नाम। |
| `emergency_contact_phone`| `String?` | `additional_details.emergency_contact_phone`| हाँ | आपातकालीन संपर्क फोन। |
| `bank_account_number` | `String?` | `additional_details.bank_account_number` | हाँ | छात्र/अभिभावक बैंक खाता नंबर। |
| `bank_name` | `String?` | `additional_details.bank_name` | हाँ | बैंक का नाम। |
| `bank_branch` | `String?` | `additional_details.bank_branch` | हाँ | बैंक शाखा का नाम। |
| `bank_ifsc` | `String?` | `additional_details.bank_ifsc` | हाँ | IFSC कोड। |
| `bank_account_holder` | `String?` | `additional_details.bank_account_holder` | हाँ | खाताधारक का नाम। |
| `student_aadhar` | `String?` | `additional_details.student_aadhar` | हाँ | छात्र का आधार कार्ड नंबर। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_students")
data class LocalStudentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_students.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "sr_number")
    val srNumber: String,
    
    @ColumnInfo(name = "admission_date")
    val admissionDate: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String,
    
    @ColumnInfo(name = "enrollment_number")
    val enrollmentNumber: String?,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "image_local_path")
    val imageLocalPath: String?,
    
    @ColumnInfo(name = "guardian_image_url")
    val guardianImageUrl: String?,
    
    @ColumnInfo(name = "guardian_image_local_path")
    val guardianImageLocalPath: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Guardian Core Details
    @ColumnInfo(name = "guardian_id")
    val guardianId: String,
    @ColumnInfo(name = "guardian_name")
    val guardianName: String?,
    @ColumnInfo(name = "guardian_mobile")
    val guardianMobile: String?,
    @ColumnInfo(name = "guardian_relationship_name")
    val guardianRelationshipName: String?,

    // Status, Category & Blood Group
    @ColumnInfo(name = "category_id")
    val categoryId: String?,
    @ColumnInfo(name = "category_name")
    val categoryName: String?,
    @ColumnInfo(name = "blood_group_id")
    val bloodGroupId: String?,
    @ColumnInfo(name = "blood_group_name")
    val bloodGroupName: String?,
    @ColumnInfo(name = "student_status_id")
    val studentStatusId: String?,
    @ColumnInfo(name = "student_status_name")
    val studentStatusName: String?,

    // Contact & Area Details
    @ColumnInfo(name = "address_area_id")
    val addressAreaId: String?,
    @ColumnInfo(name = "address_area_name")
    val addressAreaName: String?,
    @ColumnInfo(name = "address_details")
    val addressDetails: String?,

    // Enrollment & Class details
    @ColumnInfo(name = "class_id")
    val classId: String?,
    @ColumnInfo(name = "class_name")
    val className: String?,
    @ColumnInfo(name = "section_id")
    val sectionId: String?,
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,

    // QR Identity details
    @ColumnInfo(name = "qr_identity_id")
    val qrIdentityId: String?,
    @ColumnInfo(name = "qr_token_hash")
    val qrTokenHash: String?,
    @ColumnInfo(name = "qr_status")
    val qrStatus: String?,
    @ColumnInfo(name = "qr_expiry_date")
    val qrExpiryDate: String?,

    // ID Card details
    @ColumnInfo(name = "id_card_id")
    val idCardId: String?,
    @ColumnInfo(name = "card_number")
    val cardNumber: String?,
    @ColumnInfo(name = "id_card_status")
    val idCardStatus: String?,
    @ColumnInfo(name = "id_card_reissue_reason")
    val idCardReissueReason: String?,

    // Home Location coordinates
    @ColumnInfo(name = "home_latitude")
    val homeLatitude: Double?,
    @ColumnInfo(name = "home_longitude")
    val homeLongitude: Double?,

    // Additional Details
    @ColumnInfo(name = "mother_tongue_id")
    val motherTongueId: String?,
    @ColumnInfo(name = "mother_tongue_name")
    val motherTongueName: String?,
    @ColumnInfo(name = "religion")
    val religion: String?,
    @ColumnInfo(name = "nationality")
    val nationality: String,
    @ColumnInfo(name = "identification_mark")
    val identificationMark: String?,
    @ColumnInfo(name = "is_single_girl_child")
    val isSingleGirlChild: Boolean,
    @ColumnInfo(name = "caste_certificate_number")
    val casteCertificateNumber: String?,
    
    // Parents Additional Info
    @ColumnInfo(name = "father_name")
    val fatherName: String?,
    @ColumnInfo(name = "father_mobile")
    val fatherMobile: String?,
    @ColumnInfo(name = "father_email")
    val fatherEmail: String?,
    @ColumnInfo(name = "father_qualification")
    val fatherQualification: String?,
    @ColumnInfo(name = "father_occupation")
    val fatherOccupation: String?,
    @ColumnInfo(name = "parents_aadhaar_father")
    val parentsAadhaarFather: String?,
    @ColumnInfo(name = "mother_name")
    val motherName: String?,
    @ColumnInfo(name = "mother_mobile")
    val motherMobile: String?,
    @ColumnInfo(name = "mother_email")
    val motherEmail: String?,
    @ColumnInfo(name = "mother_qualification")
    val motherQualification: String?,
    @ColumnInfo(name = "mother_occupation")
    val motherOccupation: String?,
    @ColumnInfo(name = "parents_aadhaar_mother")
    val parentsAadhaarMother: String?,
    @ColumnInfo(name = "family_annual_income")
    val familyAnnualIncome: Double?,

    // Permanent Address
    @ColumnInfo(name = "permanent_address_details")
    val permanentAddressDetails: String?,
    @ColumnInfo(name = "permanent_address_area")
    val permanentAddressArea: String?,
    @ColumnInfo(name = "permanent_address_area_id")
    val permanentAddressAreaId: String?,
    @ColumnInfo(name = "permanent_area_name")
    val permanentAreaName: String?,

    // Previous Schooling & TC
    @ColumnInfo(name = "previous_school_name")
    val previousSchoolName: String?,
    @ColumnInfo(name = "previous_class")
    val previousClass: String?,
    @ColumnInfo(name = "previous_board")
    val previousBoard: String?,
    @ColumnInfo(name = "tc_number")
    val tcNumber: String?,
    @ColumnInfo(name = "tc_date")
    val tcDate: String?,
    @ColumnInfo(name = "previous_marks")
    val previousMarks: Double?,

    // Physical & Medical Info
    @ColumnInfo(name = "height")
    val height: Double?,
    @ColumnInfo(name = "weight")
    val weight: Double?,
    @ColumnInfo(name = "medical_conditions")
    val medicalConditions: String?,
    @ColumnInfo(name = "regular_medications")
    val regularMedications: String?,
    @ColumnInfo(name = "emergency_contact_name")
    val emergencyContactName: String?,
    @ColumnInfo(name = "emergency_contact_phone")
    val emergencyContactPhone: String?,

    // Bank Account Details
    @ColumnInfo(name = "bank_account_number")
    val bankAccountNumber: String?,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "bank_branch")
    val bankBranch: String?,
    @ColumnInfo(name = "bank_ifsc")
    val bankIfsc: String?,
    @ColumnInfo(name = "bank_account_holder")
    val bankAccountHolder: String?,
    
    // Aadhaar number
    @ColumnInfo(name = "student_aadhar")
    val studentAadhar: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 8. अतिरिक्त शुल्क एंटिटी (Additional Fee Entity - `local_student_additional_fees`)

**डिज़ाइन दृष्टिकोण:** 
छात्र-विशिष्ट अतिरिक्त शुल्क रिकॉर्ड को स्टोर करने के लिए। सिंक के दौरान ही `global_fee_heads` टेबल से शुल्क का नाम (Name) और कोड (Code) रिज़ॉल्व करके सीधे इसी टेबल में संग्रहीत (Denormalize) कर लिया जाएगा, जिससे यूआई पर बिना जॉइन के अतिरिक्त शुल्कों की विस्तृत सूची दिखाई जा सके।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_student_additional_fees.id` | **नहीं (Primary Key)** | अतिरिक्त शुल्क रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_student_additional_fees.organization_id`| नहीं | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_student_additional_fees.active_session_id`| नहीं | शैक्षणिक सत्र आईडी। |
| `student_id` | `String` (UUID) | `public.organization_student_additional_fees.student_id` | नहीं | छात्र की आईडी। |
| `global_fee_head_id` | `String` (UUID) | `public.organization_student_additional_fees.global_fee_head_id`| नहीं | फ़ीस हेड आईडी। |
| **`global_fee_head_name`**| `String?` | `public.global_fee_heads.name` | हाँ | **(Resolved Name):** फ़ीस का नाम (जैसे: `"Late Fee"`, `"Activity Fee"`)। |
| **`global_fee_head_code`**| `String?` | `public.global_fee_heads.code` | हाँ | **(Resolved Code):** फ़ीस का कोड (जैसे: `"LATE_FEE"`)। |
| `amount` | `Double` | `public.organization_student_additional_fees.amount` | नहीं (Default `0.0`) | आवंटित शुल्क राशि। |
| `is_active` | `Boolean` | `public.organization_student_additional_fees.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_student_additional_fees.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_additional_fees")
data class LocalStudentAdditionalFeeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_additional_fees.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "global_fee_head_id")
    val globalFeeHeadId: String,
    
    @ColumnInfo(name = "global_fee_head_name")
    val globalFeeHeadName: String?,
    
    @ColumnInfo(name = "global_fee_head_code")
    val globalFeeHeadCode: String?,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 9. शुल्क भुगतान एंटिटी (Fee Payment Entity - `local_student_fee_payments`)

**डिज़ाइन दृष्टिकोण:** 
प्रत्येक छात्र द्वारा भुगतान की गई रसीदों के इतिहास को सुरक्षित रखने के लिए। यह ऑफ़लाइन रसीद जनरेशन और भुगतान रिपोर्ट देखने के लिए अत्यंत आवश्यक है। कैश ट्रांजैक्शन रिकॉर्ड करने के लिए कलेक्ट करने वाले स्टाफ़ का नाम सीधे रिज़ॉल्व करके स्टोर किया गया है।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_student_fee_payments.id` | **नहीं (Primary Key)** | भुगतान रसीद रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_student_fee_payments.organization_id`| नहीं | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_student_fee_payments.active_session_id`| नहीं | सक्रिय शैक्षणिक सत्र आईडी। |
| `student_id` | `String` (UUID) | `public.organization_student_fee_payments.student_id` | नहीं | छात्र की आईडी। |
| `receipt_number` | `String` | `public.organization_student_fee_payments.receipt_number`| नहीं | रसीद संख्या (यूनिक)। |
| `payment_mode` | `String` | `public.organization_student_fee_payments.payment_mode` | नहीं | भुगतान मोड (`Cash`, `Cheque`, `UPI`, `Online`)। |
| `payment_date` | `String` | `public.organization_student_fee_payments.payment_date` | नहीं | भुगतान की तारीख (YYYY-MM-DD)। |
| `amount_paid` | `Double` | `public.organization_student_fee_payments.amount_paid` | नहीं | कुल जमा की गई राशि। |
| `discount_amount` | `Double` | `public.organization_student_fee_payments.discount_amount`| नहीं (Default `0.0`) | भुगतान पर दी गई छूट। |
| `fine_amount` | `Double` | `public.organization_student_fee_payments.fine_amount` | नहीं (Default `0.0`) | लेट फीस/जुर्माना राशि। |
| `discount_reason` | `String?` | `public.organization_student_fee_payments.discount_reason`| हाँ | छूट देने का कारण। |
| `cash_received_by_user_id`| `String?` (UUID) | `public.organization_student_fee_payments.cash_received_by_user_id`| हाँ | फीस कलेक्ट करने वाले कर्मचारी की आईडी। |
| **`cash_received_by_user_name`**| `String?` | `public.users.raw_user_meta_data -> name` या `profiles` | हाँ | **(Resolved Name):** फ़ीस कलेक्ट करने वाले यूज़र/स्टाफ़ का नाम। |
| `cheque_number` | `String?` | `public.organization_student_fee_payments.cheque_number`| हाँ | चेक संख्या। |
| `cheque_date` | `String?` | `public.organization_student_fee_payments.cheque_date` | हाँ | चेक जारी तिथि। |
| `cheque_bank_name` | `String?` | `public.organization_student_fee_payments.cheque_bank_name`| हाँ | बैंक का नाम। |
| `online_transaction_id`| `String?` | `public.organization_student_fee_payments.online_transaction_id`| हाँ | ऑनलाइन/UPI ट्रांजैक्शन आईडी। |
| `online_payment_app` | `String?` | `public.organization_student_fee_payments.online_payment_app`| हाँ | पेमेंट एप्लीकेशन (जैसे: `Paytm`, `UPI`)। |
| `remarks` | `String?` | `public.organization_student_fee_payments.remarks` | हाँ | भुगतान टिप्पणियाँ। |
| `status` | `String` | `public.organization_student_fee_payments.status` | नहीं | रसीद स्टेटस (`Completed`, `Cancelled`)। |
| `is_active` | `Boolean` | `public.organization_student_fee_payments.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_student_fee_payments.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_INSERT` - ऑफ़लाइन कैश/चेक रसीद काटने के बाद ट्रैक करने के लिए)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_fee_payments")
data class LocalStudentFeePaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_fee_payments.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "receipt_number")
    val receiptNumber: String,
    
    @ColumnInfo(name = "payment_mode")
    val paymentMode: String,
    
    @ColumnInfo(name = "payment_date")
    val paymentDate: String,
    
    @ColumnInfo(name = "amount_paid")
    val amountPaid: Double,
    
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Double,
    
    @ColumnInfo(name = "fine_amount")
    val fineAmount: Double,
    
    @ColumnInfo(name = "discount_reason")
    val discountReason: String?,
    
    @ColumnInfo(name = "cash_received_by_user_id")
    val cashReceivedByUserId: String?,
    
    @ColumnInfo(name = "cash_received_by_user_name")
    val cashReceivedByUserName: String?,
    
    @ColumnInfo(name = "cheque_number")
    val chequeNumber: String?,
    
    @ColumnInfo(name = "cheque_date")
    val chequeDate: String?,
    
    @ColumnInfo(name = "cheque_bank_name")
    val chequeBankName: String?,
    
    @ColumnInfo(name = "online_transaction_id")
    val onlineTransactionId: String?,
    
    @ColumnInfo(name = "online_payment_app")
    val onlinePaymentApp: String?,
    
    @ColumnInfo(name = "remarks")
    val remarks: String?,
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 10. एकीकृत व्यय एंटिटी (Unified Expense Entity - `local_parent_expenses`)

**डिज़ाइन दृष्टिकोण:** 
स्कूल के दैनिक, मासिक और श्रेणी-वार खर्चों को ट्रैक करने के लिए। सिंक के दौरान ही `global_expense_types` से व्यय का नाम और कोड रिज़ॉल्व किया जाएगा। साथ ही, `reference_id` (जैसे कोई बस, चाइल्ड स्कूल शाखा, या स्टाफ वेतन भुगतान) के नाम को सीधे `reference_name` कॉलम में रिज़ॉल्व करके डीनॉर्मलाइज़ कर दिया जाएगा ताकि बिना किसी अतिरिक्त जॉइन क्वेरी के, सीधे स्क्रीन पर रिफरेन्स दिखाया जा सके। 
बिल या रसीद की फोटो को ऑफ़लाइन देखने के लिए `receipt_local_path` का भी उपयोग किया जाएगा।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_expenses.id` | **नहीं (Primary Key)** | व्यय रिकॉर्ड की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_expenses.parent_organization_id`| नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_expenses.active_session_id`| नहीं | सक्रिय शैक्षणिक सत्र आईडी। |
| `expense_type_id` | `String` (UUID) | `public.organization_parent_expenses.expense_type_id`| नहीं | व्यय प्रकार की आईडी। |
| **`expense_type_name`** | `String?` | `public.global_expense_types.name` | हाँ | **(Resolved Name):** खर्च का नाम (जैसे: `"Fuel Expense"`, `"Maintenance"`)। |
| **`expense_type_code`** | `String?` | `public.global_expense_types.code` | हाँ | **(Resolved Code):** खर्च का कोड। |
| `payment_method` | `String` | `public.organization_parent_expenses.payment_method`| नहीं | भुगतान विधि (`Cash`, `Cheque`, `UPI`, `Online`)। |
| `amount` | `Double` | `public.organization_parent_expenses.amount` | नहीं (Default `0.0`) | कुल व्यय राशि। |
| `receipt_uuid` | `String?` (UUID) | `public.organization_parent_expenses.receipt_uuid` | हाँ | बिल रसीद की अटैचमेंट आईडी। |
| `receipt_local_path` | `String?` | स्थानीय स्टोरेज | हाँ | **(Local Cache):** ऑफलाइन बिल फोटो देखने के लिए लोकल फ़ाइल पाथ। |
| `admin_note` | `String?` | `public.organization_parent_expenses.admin_note` | हाँ | व्यय का विवरण/टिप्पणी। |
| `expense_date` | `String` | `public.organization_parent_expenses.expense_date` | नहीं | खर्च की तारीख (YYYY-MM-DD)। |
| `reference_id` | `String?` (UUID) | `public.organization_parent_expenses.reference_id` | हाँ | संबंधित ऑब्जेक्ट की आईडी। |
| `reference_type` | `String?` | `public.organization_parent_expenses.reference_type` | हाँ | रिफरेन्स प्रकार (`Bus`, `ChildOrganization`, `Salary`)। |
| **`reference_name`** | `String?` | संबंधित रिफरेन्स टेबल से | हाँ | **(Resolved Name):** रिफरेन्स ऑब्जेक्ट का प्रदर्शन नाम। (उदा: बस होने पर बस नंबर, चाइल्ड शाखा होने पर स्कूल का नाम, वेतन होने पर स्टाफ का नाम)। |
| `cash_paid_to` | `String?` | `public.organization_parent_expenses.cash_paid_to` | हाँ | कैश भुगतान पाने वाले का नाम (यदि नकद भुगतान है)। |
| `cheque_number` | `String?` | `public.organization_parent_expenses.cheque_number` | हाँ | चेक संख्या। |
| `cheque_date` | `String?` | `public.organization_parent_expenses.cheque_date` | हाँ | चेक की तारीख। |
| `cheque_bank_name` | `String?` | `public.organization_parent_expenses.cheque_bank_name` | हाँ | चेक बैंक का नाम। |
| `online_transaction_id`| `String?` | `public.organization_parent_expenses.online_transaction_id`| हाँ | ऑनलाइन/UPI ट्रांजैक्शन आईडी। |
| `online_payment_app` | `String?` | `public.organization_parent_expenses.online_payment_app`| हाँ | ऑनलाइन पेमेंट ऐप (जैसे: `GooglePay`)। |
| `vendor_name` | `String?` | `public.organization_parent_expenses.vendor_name` | हाँ | विक्रेता/दुकानदार का नाम। |
| `bill_number` | `String?` | `public.organization_parent_expenses.bill_number` | हाँ | बिल/इनवॉइस संख्या। |
| `is_active` | `Boolean` | `public.organization_parent_expenses.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_expenses.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `created_by` | `String?` (UUID) | `public.organization_parent_expenses.created_by` | हाँ | खर्च प्रविष्ट करने वाले एडमिन/यूज़र की आईडी। |
| **`created_by_name`** | `String?` | `public.users` से resolved | हाँ | **(Resolved Name):** खर्च दर्ज करने वाले एडमिन/स्टाफ़ का नाम। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED` या `PENDING_INSERT` - ऑफ़लाइन नया खर्च प्रविष्ट करने पर ट्रैक करने हेतु)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_expenses")
data class LocalParentExpenseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_expenses.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "expense_type_id")
    val expenseTypeId: String,
    
    @ColumnInfo(name = "expense_type_name")
    val expenseTypeName: String?,
    
    @ColumnInfo(name = "expense_type_code")
    val expenseTypeCode: String?,
    
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "receipt_uuid")
    val receiptUuid: String?,
    
    @ColumnInfo(name = "receipt_local_path")
    val receiptLocalPath: String?,
    
    @ColumnInfo(name = "admin_note")
    val adminNote: String?,
    
    @ColumnInfo(name = "expense_date")
    val expenseDate: String,
    
    // Poly-reference details
    @ColumnInfo(name = "reference_id")
    val referenceId: String?,
    @ColumnInfo(name = "reference_type")
    val referenceType: String?,
    @ColumnInfo(name = "reference_name")
    val referenceName: String?,
    
    @ColumnInfo(name = "cash_paid_to")
    val cashPaidTo: String?,
    
    @ColumnInfo(name = "cheque_number")
    val chequeNumber: String?,
    
    @ColumnInfo(name = "cheque_date")
    val chequeDate: String?,
    
    @ColumnInfo(name = "cheque_bank_name")
    val chequeBankName: String?,
    
    @ColumnInfo(name = "online_transaction_id")
    val onlineTransactionId: String?,
    
    @ColumnInfo(name = "online_payment_app")
    val onlinePaymentApp: String?,
    
    @ColumnInfo(name = "vendor_name")
    val vendorName: String?,
    
    @ColumnInfo(name = "bill_number")
    val billNumber: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "created_by")
    val createdBy: String?,
    @ColumnInfo(name = "created_by_name")
    val createdByName: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
```

---

## 11. छात्र उपस्थिति एंटिटी (Student Attendance Entity - `local_student_attendance`)

**डिज़ाइन दृष्टिकोण:** 
दैनिक उपस्थिति को ट्रैक करने के लिए। उपस्थित रहने वाले छात्रों की लिस्ट तेज़ी से लोड करने के लिए, हमने छात्र का नाम, रोल नंबर, और वर्ग/अनुभाग (Class/Section) विवरण को सीधे इसी तालिका में समाहित (Denormalize) कर लिया है। इससे शिक्षक को किसी वर्ग की उपस्थिति रिपोर्ट देखने के लिए अन्य किसी टेबल से जॉइन नहीं करना पड़ेगा। 
ऑफ़लाइन उपस्थिति दर्ज करने पर `sync_state` (`PENDING_INSERT` / `PENDING_UPDATE`) कॉलम द्वारा स्थानीय परिवर्तनों को ट्रैक किया जाएगा।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_student_attendance.id` | **नहीं (Primary Key)** | उपस्थिति रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_student_attendance.organization_id`| नहीं | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_student_attendance.active_session_id`| नहीं | सक्रिय शैक्षणिक सत्र आईडी। |
| **`student_id`** | `String` (UUID) | `public.organization_student_attendance.student_id` | नहीं | छात्र की आईडी। |
| **`student_name`** | `String?` | `public.organization_students.name` | हाँ | **(Resolved Name):** छात्र का नाम (जहाँ `public.organization_students.id = student_id`)। |
| **`roll_number`** | `Int?` | `public.organization_student_enrollments.roll_number`| हाँ | **(Resolved):** छात्र का रोल नंबर। |
| **`class_id`** | `String?` (UUID) | `public.organization_student_enrollments.class_id` | हाँ | **(Resolved):** कक्षा आईडी (नामांकन से)। |
| **`class_name`** | `String?` | Resolved Class Name | हाँ | **(Resolved):** कक्षा का नाम (जैसे: `"Class 10"`)। |
| **`section_id`** | `String?` (UUID) | `public.organization_student_enrollments.section_id` | हाँ | **(Resolved):** सेक्शन आईडी (नामांकन से)। |
| **`section_name`** | `String?` | `public.organization_sections.name` | हाँ | **(Resolved):** सेक्शन का नाम (जैसे: `"Section A"`)। |
| `attendance_date` | `String` | `public.organization_student_attendance.attendance_date`| नहीं | उपस्थिति की तारीख (Format: YYYY-MM-DD)। |
| `status` | `String` | `public.organization_student_attendance.status` | नहीं | उपस्थिति स्थिति (`Present`, `Absent`, `Late`, `Half Day`, `On Leave`)। |
| `remarks` | `String?` | `public.organization_student_attendance.remarks` | हाँ | यदि अनुपस्थित या छुट्टी पर है, तो विवरण/टिप्पणी। |
| `marked_by_staff_id` | `String?` (UUID) | `public.organization_student_attendance.marked_by_staff_id`| हाँ | उपस्थिति दर्ज करने वाले स्टाफ़ की आईडी। |
| **`marked_by_staff_name`**| `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** उपस्थिति दर्ज करने वाले स्टाफ़ का नाम (जैसे: `"Amit Sharma"` शिक्षक)। |
| `is_active` | `Boolean` | `public.organization_student_attendance.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_student_attendance.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_student_attendance",
    indices = [
        Index(value = ["student_id", "attendance_date"], unique = true)
    ]
)
data class LocalStudentAttendanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_attendance.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "class_id")
    val classId: String?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_id")
    val sectionId: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "status")
    val status: String, // Present, Absent, Late, Half Day, On Leave
    
    @ColumnInfo(name = "remarks")
    val remarks: String?,
    
    @ColumnInfo(name = "marked_by_staff_id")
    val markedByStaffId: String?,
    
    @ColumnInfo(name = "marked_by_staff_name")
    val markedByStaffName: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```

---

## 12. बस यात्रा उपस्थिति लॉग एंटिटी (Bus Trip Attendance Entity - `local_parent_bus_trip_attendance_logs`)

**डिज़ाइन दृष्टिकोण:** 
बस में छात्रों के चढ़ने (Boarded) और उतरने (Dropped) की स्थिति को क्यूआर कोड स्कैनिंग द्वारा ट्रैक करने के लिए। मार्ग में इंटरनेट न होने पर ऑफ़लाइन स्कैनिंग को सुगम और त्वरित बनाने के लिए हमने छात्र का नाम, रोल नंबर, कक्षा, अनुभाग, और क्यूआर स्कैन करने वाले स्टाफ़ (ड्राइवर/कंडक्टर) का नाम सीधे इसी टेबल में संग्रहीत (Denormalize) कर लिया है। 
इससे ड्राइवर को ट्रिप के दौरान यह देखने के लिए कि किस छात्र को चढ़ाया जा चुका है, किसी भी SQL Join या इंटरनेट कनेक्शन की आवश्यकता नहीं होगी।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.id` | **नहीं (Primary Key)** | ट्रिप लॉग रिकॉर्ड की विशिष्ट आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.parent_organization_id`| नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.organization_id`| नहीं | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.active_session_id`| नहीं | शैक्षणिक सत्र आईडी। |
| `trip_id` | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.trip_id`| नहीं | बस ट्रिप की आईडी (Scheduled or Ongoing trip)। |
| **`student_id`** | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.student_id`| नहीं | छात्र की विशिष्ट आईडी। |
| **`student_name`** | `String?` | `public.organization_students.name` | हाँ | **(Resolved Name):** छात्र का नाम (ऑफ़लाइन तुरंत प्रदर्शित करने के लिए)। |
| **`roll_number`** | `Int?` | `public.organization_student_enrollments.roll_number`| हाँ | **(Resolved):** छात्र का रोल नंबर। |
| **`class_name`** | `String?` | Resolved Class Name | हाँ | **(Resolved):** कक्षा का नाम। |
| **`section_name`** | `String?` | `public.organization_sections.name` | हाँ | **(Resolved):** अनुभाग/सेक्शन का नाम। |
| `status` | `String` | `public.organization_parent_bus_trip_attendance_logs.status`| नहीं (Default `Boarded`)| उपस्थिति स्थिति (`Boarded`, `Dropped`, `Absent`)। |
| `scan_latitude` | `Double?` | `public.organization_parent_bus_trip_attendance_logs.scan_latitude`| हाँ | क्यूआर स्कैनिंग के समय जीपीएस अक्षांश (Latitude)। |
| `scan_longitude` | `Double?` | `public.organization_parent_bus_trip_attendance_logs.scan_longitude`| हाँ | क्यूआर स्कैनिंग के समय जीपीएस देशांतर (Longitude)। |
| `scanned_at` | `String` | `public.organization_parent_bus_trip_attendance_logs.scanned_at`| नहीं | स्कैन करने का समय (ISO timestamp)। |
| `scanned_by_staff_id` | `String` (UUID) | `public.organization_parent_bus_trip_attendance_logs.scanned_by_staff_id`| नहीं | स्कैन करने वाले चालक/संवाहक (Staff) की आईडी। |
| **`scanned_by_staff_name`**| `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** क्यूआर स्कैन करने वाले कर्मचारी का नाम। |
| `is_active` | `Boolean` | `public.organization_parent_bus_trip_attendance_logs.is_active`| नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_bus_trip_attendance_logs.is_deleted`| नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT` - ऑफ़लाइन स्कैन होने के बाद सिंक स्थिति ट्रैक करने हेतु)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_bus_trip_attendance_logs")
data class LocalParentBusTripAttendanceLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_bus_trip_attendance_logs.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "status")
    val status: String, // Boarded, Dropped, Absent
    
    @ColumnInfo(name = "scan_latitude")
    val scanLatitude: Double?,
    
    @ColumnInfo(name = "scan_longitude")
    val scanLongitude: Double?,
    
    @ColumnInfo(name = "scanned_at")
    val scannedAt: String, // ISO timestamp
    
    @ColumnInfo(name = "scanned_by_staff_id")
    val scannedByStaffId: String,
    
    @ColumnInfo(name = "scanned_by_staff_name")
    val scannedByStaffName: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT
)
```

---

## 13. बस यात्रा शेड्यूल एंटिटी (Bus Trip Entity - `local_parent_bus_trips`)

**डिज़ाइन दृष्टिकोण:** 
बस ट्रिप (जैसे: Morning Pickup, Evening Drop) के शेड्यूल और उसकी वर्तमान स्थिति (Scheduled, Ongoing, Completed, Cancelled) को ट्रैक करने के लिए। 
ट्रिप शुरू/समाप्त करने और ट्रिप की स्थिति देखने की प्रक्रिया को ऑफ़लाइन सहज बनाने के लिए, हमने बस का नंबर, ड्राइवर का नाम, और ड्राइवर का फोन नंबर सिंक के दौरान ही इस टेबल में रिज़ॉल्व करके डीनॉर्मलाइज़ कर लिया है। 
इसके कारण ट्रिप के 1-टू-मेनी रिलेशनशिप (1 ट्रिप में कई अटेंडेंस लॉग्स) को बिना किसी जॉइंट ओवरहेड के मैनेज किया जा सकता है।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_bus_trips.id` | **नहीं (Primary Key)** | बस ट्रिप की विशिष्ट आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_bus_trips.parent_organization_id`| नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_bus_trips.active_session_id`| नहीं | शैक्षणिक सत्र आईडी। |
| `bus_id` | `String` (UUID) | `public.organization_parent_bus_trips.bus_id` | नहीं | बस आईडी। |
| **`bus_number`** | `String?` | `public.organization_parent_buses.bus_number`| हाँ | **(Resolved Name):** बस का नंबर (जैसे: `"RJ 14 PC 1234"`)। |
| **`bus_name`** | `String?` | `public.organization_parent_buses.bus_name` | हाँ | **(Resolved Name):** बस का नाम। |
| `driver_id` | `String` (UUID) | `public.organization_parent_bus_trips.driver_id` | नहीं | ड्राइवर (स्टाफ़) की आईडी। |
| **`driver_name`** | `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** ड्राइवर का पूरा नाम। |
| **`driver_phone`** | `String?` | `public.organization_parent_staff.mobile_number`| हाँ | **(Resolved):** ड्राइवर का संपर्क सूत्र। |
| `trip_type` | `String` | `public.organization_parent_bus_trips.trip_type`| नहीं | ट्रिप का प्रकार (`Morning_Pickup`, `Evening_Drop`, `Special`)। |
| `status` | `String` | `public.organization_parent_bus_trips.status` | नहीं | ट्रिप की स्थिति (`Scheduled`, `Ongoing`, `Completed`, `Cancelled`)। |
| `start_time` | `String?` | `public.organization_parent_bus_trips.start_time`| हाँ | ट्रिप शुरू होने का समय (ISO Timestamp - ऑफ़लाइन ट्रिप शुरू करने पर स्थानीय स्तर पर दर्ज होगा)। |
| `end_time` | `String?` | `public.organization_parent_bus_trips.end_time` | हाँ | ट्रिप समाप्त होने का समय (ISO Timestamp - ऑफ़लाइन ट्रिप बंद करने पर स्थानीय स्तर पर दर्ज होगा)। |
| `is_active` | `Boolean` | `public.organization_parent_bus_trips.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_bus_trips.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_UPDATE` - ऑफ़लाइन ट्रिप स्टार्ट/एंड करने पर सुपबेस पर स्टेटस सिंक करने के लिए)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_bus_trips")
data class LocalParentBusTripEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_bus_trips.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "bus_id")
    val busId: String,
    
    @ColumnInfo(name = "bus_number")
    val busNumber: String?,
    
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    
    @ColumnInfo(name = "driver_id")
    val driverId: String,
    
    @ColumnInfo(name = "driver_name")
    val driverName: String?,
    
    @ColumnInfo(name = "driver_phone")
    val driverPhone: String?,
    
    @ColumnInfo(name = "trip_type")
    val tripType: String, // Morning_Pickup, Evening_Drop, Special
    
    @ColumnInfo(name = "status")
    val status: String, // Scheduled, Ongoing, Completed, Cancelled
    
    @ColumnInfo(name = "start_time")
    val startTime: String?,
    
    @ColumnInfo(name = "end_time")
    val endTime: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_UPDATE
)
```

---

## 14. एकीकृत अवकाश एंटिटी (Unified Leave Entity - `local_organization_leaves`)

**डिज़ाइन दृष्टिकोण:** 
स्टाफ़ और छात्रों दोनों के अवकाश आवेदनों (Leave Applications) को एक ही टेबल में सुव्यवस्थित करने के लिए। 
ऑफ़लाइन आवेदन करने, स्थिति समीक्षा (Review) करने और यूआई पर तुरंत नाम दिखाने के लिए आवेदक का नाम, रोल/वर्ग/अनुभाग, और स्वीकृति देने वाले अधिकारी का नाम सिंक के दौरान ही इस टेबल में रिज़ॉल्व करके स्टोर कर दिया जाएगा। 
यह ऑफ़लाइन आवेदन करने पर `PENDING_INSERT` और अवकाश स्वीकृत/अस्वीकृत होने पर `PENDING_UPDATE` की स्थिति का समर्थन करेगी।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_leaves.id` | **नहीं (Primary Key)** | अवकाश रिकॉर्ड की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_leaves.parent_organization_id` | नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `organization_id` | `String?` (UUID) | `public.organization_leaves.organization_id` | हाँ | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी (छात्र अवकाश के लिए)। |
| `active_session_id` | `String` (UUID) | `public.organization_leaves.active_session_id` | नहीं | शैक्षणिक सत्र आईडी। |
| `applicant_type` | `String` | `public.organization_leaves.applicant_type` | नहीं | आवेदक प्रकार (`staff`, `student`)। |
| `staff_id` | `String?` (UUID) | `public.organization_leaves.staff_id` | हाँ | स्टाफ़ आईडी (यदि स्टाफ़ छुट्टी ले रहा है)। |
| **`staff_name`** | `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved Name):** छुट्टी पर जाने वाले स्टाफ़ का नाम। |
| **`staff_role_name`** | `String?` | Resolved Staff Role Name | हाँ | **(Resolved):** स्टाफ़ का पद (जैसे: `"Driver"`, `"Maths Teacher"`)। |
| `student_id` | `String?` (UUID) | `public.organization_leaves.student_id` | हाँ | छात्र आईडी (यदि छात्र छुट्टी ले रहा है)। |
| **`student_name`** | `String?` | `public.organization_students.name` | हाँ | **(Resolved Name):** छुट्टी लेने वाले छात्र का नाम। |
| **`class_name`** | `String?` | Resolved Class Name | हाँ | **(Resolved):** छात्र की कक्षा। |
| **`section_name`** | `String?` | `public.organization_sections.name` | हाँ | **(Resolved):** छात्र का सेक्शन। |
| `leave_type` | `String` | `public.organization_leaves.leave_type` | नहीं | छुट्टी का प्रकार (CL, SL, Casual, Sick etc.)। |
| `start_date` | `String` | `public.organization_leaves.start_date` | नहीं | प्रारंभ तिथि (YYYY-MM-DD)। |
| `end_date` | `String` | `public.organization_leaves.end_date` | नहीं | अंतिम तिथि (YYYY-MM-DD)। |
| `is_half_day` | `Boolean` | `public.organization_leaves.is_half_day` | नहीं | क्या यह आधे दिन की छुट्टी है। |
| `half_day_period` | `String?` | `public.organization_leaves.half_day_period` | हाँ | हाफ डे अवधि (`First Half`, `Second Half`)। |
| `reason` | `String?` | `public.organization_leaves.reason` | हाँ | छुट्टी लेने का कारण। |
| `status` | `String` | `public.organization_leaves.status` | नहीं | अवकाश स्थिति (`Pending`, `Approved`, `Rejected`)। |
| `action_remarks` | `String?` | `public.organization_leaves.action_remarks` | हाँ | स्वीकृति देने वाले अधिकारी की टिप्पणी/रिमार्क्स। |
| `action_by` | `String?` (UUID) | `public.organization_leaves.action_by` | हाँ | स्वीकृति/अस्वीकृति देने वाले यूज़र की आईडी। |
| **`action_by_name`** | `String?` | `profiles.name` या `staff.name` से resolved | हाँ | **(Resolved Name):** निर्णय लेने वाले प्रिंसिपल/प्रबंधक का नाम। |
| `action_at` | `String?` | `public.organization_leaves.action_at` | हाँ | समीक्षा की तिथि/समय (ISO Timestamp)। |
| `is_active` | `Boolean` | `public.organization_leaves.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_leaves.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_leaves")
data class LocalOrganizationLeaveEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_leaves.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String?,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "applicant_type")
    val applicantType: String, // staff, student
    
    @ColumnInfo(name = "staff_id")
    val staffId: String?,
    
    @ColumnInfo(name = "staff_name")
    val staffName: String?,
    
    @ColumnInfo(name = "staff_role_name")
    val staffRoleName: String?,
    
    @ColumnInfo(name = "student_id")
    val studentId: String?,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "leave_type")
    val leaveType: String,
    
    @ColumnInfo(name = "start_date")
    val startDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "is_half_day")
    val isHalfDay: Boolean,
    
    @ColumnInfo(name = "half_day_period")
    val halfDayPeriod: String?, // First Half, Second Half
    
    @ColumnInfo(name = "reason")
    val reason: String?,
    
    @ColumnInfo(name = "status")
    val status: String, // Pending, Approved, Rejected
    
    @ColumnInfo(name = "action_remarks")
    val actionRemarks: String?,
    
    @ColumnInfo(name = "action_by")
    val actionBy: String?,
    
    @ColumnInfo(name = "action_by_name")
    val actionByName: String?,
    
    @ColumnInfo(name = "action_at")
    val actionAt: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```

---

## 15. एकीकृत रिमार्क एंटिटी (Unified Remark Entity - `local_organization_remarks`)

**डिज़ाइन दृष्टिकोण:** 
रिमार्क से जुड़े सभी विवरणों (मूल रिमार्क, उसका टारगेट, और उससे जुड़े फ़ाइल अटैचमेंट्स) को एक ही टेबल में सुव्यवस्थित करने के लिए। 
4 टेबल्स (Remarks, Targets, Attachments, History) को एक साथ रूम में रखने के बजाय, प्रत्येक टारगेट के लिए रिमार्क की जानकारी को फ़्लैट (Deneormalize) करके इस टेबल में सहेजा जाएगा। 
यह यूआई पर छात्र-वार, क्लास-वार, या अभिभावक-वार रिमार्क प्रदर्शित करने की प्रक्रिया को 100% जॉइन-फ्री और तत्काल (Instant) बना देगा।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_remarks.id` | **नहीं (Primary Key)** | मुख्य रिमार्क की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_remarks.parent_organization_id` | नहीं | पैरेंट आर्गेनाइजेशन आईडी। |
| `organization_id` | `String?` (UUID) | `public.organization_remarks.organization_id` | हाँ | शाखा (चाइल्ड आर्गेनाइजेशन) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_remarks.active_session_id` | नहीं | शैक्षणिक सत्र आईडी। |
| `content` | `String` | `public.organization_remarks.content` | नहीं | रिमार्क का टेक्स्ट/विषय-वस्तु। |
| `category` | `String` | `public.organization_remarks.category` | नहीं | रिमार्क की श्रेणी (जैसे: `"Behavior"`, `"Academic"`, `"Warning"`)। |
| `priority` | `String` | `public.organization_remarks.priority` | नहीं (Default `"Medium"`)| प्राथमिकता स्तर (`Low`, `Medium`, `High`, `Critical`)। |
| `creator_user_id` | `String` (UUID) | `public.organization_remarks.creator_user_id` | नहीं | रिमार्क लिखने वाले यूज़र की आईडी। |
| **`creator_user_name`** | `String?` | `profiles.name` या `staff.name` से resolved | हाँ | **(Resolved Name):** रिमार्क लिखने वाले शिक्षक/कर्मचारी का नाम। |
| **`creator_role_name`** | `String?` | `global_staff_roles.name` से resolved | हाँ | **(Resolved Name):** रिमार्क लेखक का पद (जैसे: `"Class Teacher"`)। |
| `visibility_type` | `String` | `public.organization_remarks.visibility_type` | नहीं (Default `"Public"`)| रिमार्क की दृश्यता सीमा (`Public`, `Private`, `Internal`)। |
| `visibility_audience_json`| `String` (JSON) | `public.organization_remarks.visibility_audience`| नहीं | विशिष्ट रोल/ऑडियन्स की सूची (JSON Array String)। |
| `is_pinned` | `Boolean` | `public.organization_remarks.is_pinned` | नहीं | क्या रिमार्क पिन किया हुआ है। |
| `pin_expires_at` | `String?` | `public.organization_remarks.pin_expires_at`| हाँ | पिन समाप्त होने का समय (ISO Timestamp)। |
| `expires_at` | `String?` | `public.organization_remarks.expires_at` | हाँ | रिमार्क समाप्ति की तारीख। |
| **`target_id`** | `String` (UUID) | `public.organization_remark_targets.id` | नहीं | **(Resolved Target ID):** टारगेट रिमार्क रिकॉर्ड की यूनिक आईडी। |
| **`target_type`** | `String` | `public.organization_remark_targets.target_type` | नहीं | टारगेट प्रकार (`student`, `guardian`, `staff`, `user`)। |
| **`target_student_id`** | `String?` (UUID) | `public.organization_remark_targets.target_student_id`| हाँ | टारगेट छात्र की आईडी। |
| **`target_student_name`**| `String?` | `public.organization_students.name` | हाँ | **(Resolved):** टारगेट छात्र का नाम। |
| **`target_class_name`** | `String?` | Resolved Class Name | हाँ | **(Resolved):** टारगेट छात्र की कक्षा। |
| **`target_section_name`**| `String?` | `public.organization_sections.name` | हाँ | **(Resolved):** टारगेट छात्र का सेक्शन। |
| **`target_guardian_id`** | `String?` (UUID) | `public.organization_remark_targets.target_guardian_id`| हाँ | टारगेट अभिभावक की आईडी। |
| **`target_guardian_name`**| `String?` | Resolved Guardian Name | हाँ | **(Resolved):** टारगेट अभिभावक का नाम। |
| **`target_staff_id`** | `String?` (UUID) | `public.organization_remark_targets.target_staff_id` | हाँ | टारगेट कर्मचारी की आईडी। |
| **`target_staff_name`** | `String?` | `public.organization_parent_staff.name` | हाँ | **(Resolved):** टारगेट स्टाफ़ का नाम। |
| **`target_user_id`** | `String?` (UUID) | `public.organization_remark_targets.target_user_id` | हाँ | टारगेट यूज़र की आईडी। |
| **`attachments_json`** | `String` (JSON) | `public.organization_remark_attachments` | नहीं (Default `"[]"`)| **(Collapsed Attachments):** रिमार्क की सभी फ़ाइलों की सूची (उदा: `[{"file_url": "...", "local_path": "...", "file_type": "..."}]`)। |
| `is_active` | `Boolean` | `public.organization_remarks.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_remarks.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_remarks")
data class LocalOrganizationRemarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "target_id")
    val targetId: String, // from organization_remark_targets.id
    
    @ColumnInfo(name = "id")
    val id: String, // from organization_remarks.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String?,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "content")
    val content: String,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "priority")
    val priority: String, // Low, Medium, High, Critical
    
    @ColumnInfo(name = "creator_user_id")
    val creatorUserId: String,
    
    @ColumnInfo(name = "creator_user_name")
    val creatorUserName: String?,
    
    @ColumnInfo(name = "creator_role_name")
    val creatorRoleName: String?,
    
    @ColumnInfo(name = "visibility_type")
    val visibilityType: String, // Public, Private, Internal
    
    @ColumnInfo(name = "visibility_audience_json")
    val visibilityAudienceJson: String, // JSON Array of roles
    
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean,
    
    @ColumnInfo(name = "pin_expires_at")
    val pinExpiresAt: String?,
    
    @ColumnInfo(name = "expires_at")
    val expiresAt: String?,
    
    // Target details
    @ColumnInfo(name = "target_type")
    val targetType: String, // student, guardian, staff, user
    
    @ColumnInfo(name = "target_student_id")
    val targetStudentId: String?,
    
    @ColumnInfo(name = "target_student_name")
    val targetStudentName: String?,
    
    @ColumnInfo(name = "target_class_name")
    val targetClassName: String?,
    
    @ColumnInfo(name = "target_section_name")
    val targetSectionName: String?,
    
    @ColumnInfo(name = "target_guardian_id")
    val targetGuardianId: String?,
    
    @ColumnInfo(name = "target_guardian_name")
    val targetGuardianName: String?,
    
    @ColumnInfo(name = "target_staff_id")
    val targetStaffId: String?,
    
    @ColumnInfo(name = "target_staff_name")
    val targetStaffName: String?,
    
    @ColumnInfo(name = "target_user_id")
    val targetUserId: String?,
    
    // Attachments
    @ColumnInfo(name = "attachments_json")
    val attachmentsJson: String, // JSON Array of attachments with remote/local paths
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```

---

## 16. आयोजित परीक्षा एंटिटी (Organization Exam Entity - `local_organization_exams`)

**डिज़ाइन दृष्टिकोण:** 
संस्था/स्कूल द्वारा आयोजित की जाने वाली परीक्षाओं की सूची को संग्रहीत करने के लिए। सिंक के दौरान ही `global_exam_types` से परीक्षा के प्रकार का नाम (Name) और कोड (Code) रिज़ॉल्व करके सीधे इसी टेबल में डीनॉर्मलाइज कर दिया जाएगा ताकि बिना किसी अतिरिक्त जॉइन क्वेरी के सीधे स्क्रीन पर परीक्षा सूची दिखाई जा सके।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_exams.id` | **नहीं (Primary Key)** | परीक्षा रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_exams.organization_id` | नहीं | चाइल्ड आर्गेनाइजेशन (शाखा) की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_exams.active_session_id` | नहीं | सक्रिय शैक्षणिक सत्र आईडी। |
| `exam_type_id` | `String` (UUID) | `public.organization_exams.exam_type_id` | नहीं | परीक्षा प्रकार की आईडी। |
| **`exam_type_name`** | `String?` | `public.global_exam_types.name` | हाँ | **(Resolved Name):** परीक्षा का सामान्य प्रकार (उदा: `"Half Yearly"`, `"Annual"`)। |
| **`exam_type_code`** | `String?` | `public.global_exam_types.code` | हाँ | **(Resolved Code):** परीक्षा प्रकार का कोड (उदा: `"HALF_YEARLY"`)। |
| `name` | `String` | `public.organization_exams.name` | नहीं | परीक्षा का कस्टम नाम (उदा: `"प्रथम परख 2026"`)। |
| `start_date` | `String?` | `public.organization_exams.start_date` | हाँ | परीक्षा शुरू होने की तिथि (YYYY-MM-DD)। |
| `end_date` | `String?` | `public.organization_exams.end_date` | हाँ | परीक्षा समाप्त होने की तिथि (YYYY-MM-DD)। |
| `is_active` | `Boolean` | `public.organization_exams.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_exams.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक की स्थिति (`SYNCED`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_exams")
data class LocalOrganizationExamEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_exams.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_type_id")
    val examTypeId: String,
    
    @ColumnInfo(name = "exam_type_name")
    val examTypeName: String?, // resolved from global_exam_types.name
    
    @ColumnInfo(name = "exam_type_code")
    val examTypeCode: String?, // resolved from global_exam_types.code
    
    @ColumnInfo(name = "name")
    val name: String, // exam name like "Term 1 Exam"
    
    @ColumnInfo(name = "start_date")
    val startDate: String?, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String?, // YYYY-MM-DD
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_UPDATE
)
```

---

## 17. परीक्षा विषय सेटिंग्स एंटिटी (Exam Subject Settings Entity - `local_exam_subject_settings`)

**डिज़ाइन दृष्टिकोण:** 
प्रत्येक परीक्षा, कक्षा, और विषय के लिए अधिकतम अंक (Max Marks) और न्यूनतम पासिंग अंक (Passing Marks) के नियमों को सेव करने के लिए। सुपबेस के ग्लोबल नियमों (`global_exam_subject_rules`) और स्कूल-स्तर की सेटिंग्स (`organization_exam_subject_settings`) को बैकएंड पर रिज़ॉल्व करके सीधे अंतिम नियम के रूप में सिंक किया जाएगा ताकि ऑफलाइन मार्क्स भरते समय सही नियमों की तुरंत जांच की जा सके।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_exam_subject_settings.id` | **नहीं (Primary Key)** | सेटिंग्स रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_exam_subject_settings.organization_id` | नहीं | शाखा की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_exam_subject_settings.active_session_id` | नहीं | सत्र आईडी। |
| `exam_id` | `String` (UUID) | `public.organization_exam_subject_settings.exam_id` | नहीं | आयोजित परीक्षा की आईडी। |
| `class_id` | `String` (UUID) | `public.organization_exam_subject_settings.class_id` | नहीं | कक्षा की आईडी। |
| `subject_id` | `String` (UUID) | `public.organization_exam_subject_settings.subject_id` | नहीं | विषय की आईडी। |
| **`class_name`** | `String?` | Resolved Class Name | हाँ | **(Resolved Name):** कक्षा का नाम (उदा: `"Class 10"`)। |
| **`subject_name`** | `String?` | `public.global_subjects.name` | हाँ | **(Resolved Name):** विषय का नाम (उदा: `"Mathematics"`)। |
| `max_marks` | `Double?` | Resolved Max Marks | हाँ | अधिकतम मार्क्स (डिफ़ॉल्ट या कस्टम ओवरराइड)। |
| `minimum_passing_marks` | `Double?` | Resolved Passing Marks | हाँ | पासिंग मार्क्स। |
| `grading_system` | `String?` | Resolved Grading System | हाँ | ग्रेडिंग सिस्टम (JSON String प्रारूप में)। |
| `is_deleted` | `Boolean` | `public.organization_exam_subject_settings.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक की स्थिति (`SYNCED`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_exam_subject_settings")
data class LocalExamSubjectSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_exam_subject_settings.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_id")
    val examId: String, // links to local_organization_exams.id
    
    @ColumnInfo(name = "class_id")
    val classId: String,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    
    @ColumnInfo(name = "class_name")
    val className: String?, // resolved class name like "Class 10"
    
    @ColumnInfo(name = "subject_name")
    val subjectName: String?, // resolved subject name like "Mathematics"
    
    @ColumnInfo(name = "max_marks")
    val maxMarks: Double?,
    
    @ColumnInfo(name = "minimum_passing_marks")
    val minimumPassingMarks: Double?,
    
    @ColumnInfo(name = "grading_system")
    val gradingSystem: String?, // JSON string of grading rules (A, B, C etc.)
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED
)
```

---

## 18. छात्र परीक्षा मार्क्स एंटिटी (Student Exam Marks Entity - `local_student_exam_marks`)

**डिज़ाइन दृष्टिकोण:** 
प्रत्येक छात्र द्वारा किसी विशिष्ट परीक्षा के अंतर्गत विषयों में प्राप्त अंकों को सेव करने के लिए। रिपोर्ट कार्ड ऑफलाइन लोड करने की गति को अधिकतम करने और ऑफलाइन मार्क्स एंट्री स्क्रीन को सॉर्ट करने के लिए छात्र का नाम, रोल नंबर, परीक्षा नाम, विषय नाम, और अधिकतम/पासिंग मार्क्स को सीधे इसी टेबल में डीनॉर्मलाइज किया गया है।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_student_exam_marks.id` | **नहीं (Primary Key)** | मार्क्स रिकॉर्ड की यूनिक आईडी। |
| `organization_id` | `String` (UUID) | `public.organization_student_exam_marks.organization_id` | नहीं | शाखा की आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_student_exam_marks.active_session_id` | नहीं | सत्र आईडी। |
| `exam_id` | `String` (UUID) | `public.organization_student_exam_marks.exam_id` | नहीं | परीक्षा आईडी। |
| `class_id` | `String` (UUID) | `public.organization_student_exam_marks.class_id` | नहीं | कक्षा आईडी। |
| `subject_id` | `String` (UUID) | `public.organization_student_exam_marks.subject_id` | नहीं | विषय आईडी। |
| `student_id` | `String` (UUID) | `public.organization_student_exam_marks.student_id` | नहीं | छात्र आईडी। |
| `obtained_marks` | `Double?` | `public.organization_student_exam_marks.obtained_marks` | हाँ | छात्र द्वारा प्राप्त अंक (अनुपस्थित होने पर Null)। |
| `is_absent` | `Boolean` | `public.organization_student_exam_marks.is_absent` | नहीं | क्या छात्र परीक्षा में अनुपस्थित था। |
| `is_medical_leave` | `Boolean` | `public.organization_student_exam_marks.is_medical_leave` | नहीं | क्या छात्र चिकित्सा अवकाश (Medical Leave) पर था। |
| `teacher_remarks` | `String?` | `public.organization_student_exam_marks.teacher_remarks` | हाँ | शिक्षक की रिमार्क/टिप्पणी। |
| `is_active` | `Boolean` | `public.organization_student_exam_marks.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_student_exam_marks.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| **`student_name`** | `String?` | `public.organization_students.name` | हाँ | **(Resolved Name):** छात्र का नाम (ऑफलाइन तुरंत दिखाने के लिए)। |
| **`roll_number`** | `Int?` | `public.organization_student_enrollments.roll_number` | हाँ | **(Resolved):** छात्र का रोल नंबर (मार्क्स प्रविष्टि सॉर्टिंग के लिए)। |
| **`subject_name`** | `String?` | `public.global_subjects.name` | हाँ | **(Resolved Name):** विषय का नाम (उदा: `"Mathematics"`)। |
| **`exam_name`** | `String?` | `public.organization_exams.name` | हाँ | **(Resolved Name):** परीक्षा का नाम (उदा: `"Term 1 Exam"`)। |
| **`max_marks`** | `Double?` | Resolved Max Marks from settings | हाँ | **(Resolved):** अधिकतम अंक (प्रतिशत ऑफलाइन कैलकुलेट करने के लिए)। |
| **`minimum_passing_marks`** | `Double?` | Resolved Passing Marks from settings | हाँ | **(Resolved):** पासिंग अंक (उत्तीर्ण/अनुत्तीर्ण ऑफलाइन जांचने के लिए)। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक की स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_student_exam_marks",
    indices = [
        Index(value = ["exam_id", "class_id", "subject_id", "student_id"], unique = true)
    ]
)
data class LocalStudentExamMarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_exam_marks.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_id")
    val examId: String,
    
    @ColumnInfo(name = "class_id")
    val classId: String,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "obtained_marks")
    val obtainedMarks: Double?,
    
    @ColumnInfo(name = "is_absent")
    val isAbsent: Boolean,
    
    @ColumnInfo(name = "is_medical_leave")
    val isMedicalLeave: Boolean,
    
    @ColumnInfo(name = "teacher_remarks")
    val teacherRemarks: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    // Denormalized Fields for fast offline rendering
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "subject_name")
    val subjectName: String?,
    
    @ColumnInfo(name = "exam_name")
    val examName: String?,
    
    @ColumnInfo(name = "max_marks")
    val maxMarks: Double?, // cached from settings to compute offline percentages easily
    
    @ColumnInfo(name = "minimum_passing_marks")
    val minimumPassingMarks: Double?, // cached to display pass/fail status immediately
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```

---

## 19. एकीकृत कैलेंडर एवं इवेंट एंटिटी (Consolidated Calendar & Event Entity - `local_calendar_events`)

**डिज़ाइन दृष्टिकोण:** 
एकेडमिक कैलेंडर के सभी आयोजनों (छुट्टियों, मीटिंग्स, परीक्षा तिथियों, खेलकूद, वार्षिक उत्सवों) को संग्रहीत करने के लिए। सिंक के दौरान ही संबंधित टेबल्स से पैरेंट संगठन का नाम और चाइल्ड शाखा (विद्यालय) का नाम रिज़ॉल्व करके सीधे इसी टेबल में डीनॉर्मलाइज कर दिया जाएगा ताकि बिना किसी अतिरिक्त जॉइन क्वेरी के सीधे मासिक कैलेंडर और आगामी इवेंट्स की लिस्ट को तेजी से ऑफलाइन रेंडर किया जा सके।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_calendar_events.id` | **नहीं (Primary Key)** | इवेंट रिकॉर्ड की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_calendar_events.parent_organization_id` | नहीं | पैरेंट संगठन (Parent Organization) की आईडी। |
| **`parent_organization_name`** | `String?` | `public.organization_parents.name` से resolved | हाँ | **(Resolved Name):** पैरेंट संगठन का नाम (उदा: `"विद्यापीठ ग्रुप"`)। |
| `organization_id` | `String` (UUID) | `public.organization_calendar_events.organization_id` | नहीं | चाइल्ड आर्गेनाइजेशन (शाखा/विद्यालय) की आईडी। |
| **`organization_name`** | `String?` | `public.organizations.name` से resolved | हाँ | **(Resolved Name):** विशिष्ट स्कूल शाखा का नाम (उदा: `"विद्यापीठ सीनियर सेकेंडरी स्कूल"`)। |
| `active_session_id` | `String` (UUID) | `public.organization_calendar_events.active_session_id` | नहीं | शैक्षणिक सत्र आईडी। |
| `name` | `String` | `public.organization_calendar_events.name` | नहीं | इवेंट/अवकाश का नाम (उदा: `"दीपावली अवकाश"`, `"वार्षिक खेलकूद"`, `"PTM 1"` )। |
| `description` | `String?` | `public.organization_calendar_events.description` | हाँ | इवेंट का विस्तृत विवरण या सूचना। |
| `start_date` | `String` | `public.organization_calendar_events.start_date` | नहीं | इवेंट प्रारंभ होने की तिथि (YYYY-MM-DD)। |
| `end_date` | `String` | `public.organization_calendar_events.end_date` | नहीं | इवेंट समाप्त होने की तिथि (YYYY-MM-DD)। |
| `event_type` | `String` | `public.organization_calendar_events.event_type` | नहीं | इवेंट का प्रकार (`Holiday`, `PTM`, `Exam`, `Event`, `Academic`)। |
| `is_school_closed` | `Boolean` | `public.organization_calendar_events.is_school_closed` | नहीं | क्या इस दिन विद्यालय बंद रहेगा। |
| `is_active` | `Boolean` | `public.organization_calendar_events.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_calendar_events.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_calendar_events")
data class LocalCalendarEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_calendar_events.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "parent_organization_name")
    val parentOrganizationName: String?, // resolved from organization_parents.name
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "organization_name")
    val organizationName: String?, // resolved from organizations.name
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String, // event name like "Diwali Break" or "PTM 1"
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "start_date")
    val startDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "event_type")
    val eventType: String, // Holiday, PTM, Exam, Event, Academic
    
    @ColumnInfo(name = "is_school_closed")
    val isSchoolClosed: Boolean,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```

---

## 20. एकीकृत स्टाफ उपस्थिति एंटिटी (Consolidated Staff Attendance Entity - `local_parent_staff_attendance`)

**डिज़ाइन दृष्टिकोण:** 
स्टाफ की दैनिक उपस्थिति (Attendance) रिकॉर्ड्स को संग्रहीत करने के लिए। सिंक के दौरान ही संबंधित कर्मचारियों की टेबल्स से स्टाफ का नाम (`staff_name`), उनका पद (`staff_role`) और उपस्थिति स्थिति के कोड/नाम (`status_code` / `status_name`) को रिज़ॉल्व करके सीधे इसी टेबल में डीनॉर्मलाइज कर दिया जाएगा ताकि बिना किसी अतिरिक्त जॉइन क्वेरी के सीधे दैनिक स्टाफ लिस्ट और अटेंडेंस रिपोर्ट स्क्रीन को ऑफ़लाइन लोड किया जा सके।

### स्कीमा डिज़ाइन और सुपबेस मैपिंग (Schema Design & Supabase Mapping):

| फ़ील्ड का नाम (Room/Kotlin) | डेटा प्रकार (Kotlin Type) | सुपबेस स्रोत (Table & Column) | शून्य स्वीकार्य (Nullable) | विवरण व मैपिंग नियम |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | `String` (UUID) | `public.organization_parent_staff_attendance.id` | **नहीं (Primary Key)** | उपस्थिति रिकॉर्ड की यूनिक आईडी। |
| `parent_organization_id` | `String` (UUID) | `public.organization_parent_staff_attendance.parent_organization_id` | नहीं | पैरेंट संगठन आईडी। |
| `active_session_id` | `String` (UUID) | `public.organization_parent_staff_attendance.active_session_id` | नहीं | शैक्षणिक सत्र आईडी। |
| `staff_id` | `String` (UUID) | `public.organization_parent_staff_attendance.staff_id` | नहीं | स्टाफ सदस्य की आईडी। |
| **`staff_name`** | `String?` | `public.organization_parent_staff.name` से resolved | हाँ | **(Resolved Name):** स्टाफ का नाम (यूआई पर बिना SQL Join के दिखाने के लिए)। |
| **`staff_role`** | `String?` | Resolved from `global_staff_roles` | हाँ | **(Resolved Role):** स्टाफ का पद/रोल (उदा: `"Teacher"`, `"Accountant"`)। |
| `attendance_date` | `String` | `public.organization_parent_staff_attendance.attendance_date` | नहीं | उपस्थिति की तारीख (YYYY-MM-DD)। |
| `status_id` | `String` (UUID) | `public.organization_parent_staff_attendance.status_id` | नहीं | उपस्थिति स्टेटस आईडी। |
| **`status_code`** | `String` | `public.global_attendance_status.code` से resolved | नहीं | **(Resolved Code):** उपस्थिति स्टेटस कोड (उदा: `"PR"`, `"AB"`, `"HD"`, `"LV"`)। |
| **`status_name`** | `String` | `public.global_attendance_status.name` से resolved | नहीं | **(Resolved Name):** स्टेटस का नाम (उदा: `"Present"`, `"Absent"`, `"Half Day"`)। |
| `is_paid_leave` | `Boolean` | `public.organization_parent_staff_attendance.is_paid_leave` | नहीं | क्या यह सवैतनिक अवकाश (Paid Leave) है। |
| `check_in_time` | `String?` | `public.organization_parent_staff_attendance.check_in_time` | हाँ | चेक-इन का समय (HH:MM:SS)। |
| `check_out_time` | `String?` | `public.organization_parent_staff_attendance.check_out_time` | हाँ | चेक-आउट का समय (HH:MM:SS)। |
| `remarks` | `String?` | `public.organization_parent_staff_attendance.remarks` | हाँ | उपस्थिति दर्जकर्ता की टिप्पणी/रिमार्क। |
| `is_active` | `Boolean` | `public.organization_parent_staff_attendance.is_active` | नहीं | क्या रिकॉर्ड सक्रिय है। |
| `is_deleted` | `Boolean` | `public.organization_parent_staff_attendance.is_deleted` | नहीं | सॉफ्ट डिलीट रिकॉर्ड स्थिति। |
| `last_synced_at` | `Long` | स्थानीय स्तर पर जनरेटेड | नहीं | अंतिम सफल सिंक का टाइमस्टैम्प। |
| `sync_state` | `String` | स्थानीय स्तर पर जनरेटेड | नहीं | सिंक स्थिति (`SYNCED`, `PENDING_INSERT`, `PENDING_UPDATE`)। |

---

### 🛠️ Kotlin रूम डेटा क्लास प्रारूप:

```kotlin
package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_parent_staff_attendance",
    indices = [
        Index(value = ["staff_id", "attendance_date"], unique = true)
    ]
)
data class LocalParentStaffAttendanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_staff_attendance.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "staff_id")
    val staffId: String,
    
    @ColumnInfo(name = "staff_name")
    val staffName: String?, // resolved from organization_parent_staff.name
    
    @ColumnInfo(name = "staff_role")
    val staffRole: String?, // resolved from global_staff_roles.name
    
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "status_id")
    val statusId: String,
    
    @ColumnInfo(name = "status_code")
    val statusCode: String, // PR, AB, HD, LV
    
    @ColumnInfo(name = "status_name")
    val statusName: String, // Present, Absent, Half Day
    
    @ColumnInfo(name = "is_paid_leave")
    val isPaidLeave: Boolean,
    
    @ColumnInfo(name = "check_in_time")
    val checkInTime: String?, // HH:MM:SS
    
    @ColumnInfo(name = "check_out_time")
    val checkOutTime: String?, // HH:MM:SS
    
    @ColumnInfo(name = "remarks")
    val remarks: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
```













