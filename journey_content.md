# विद्यासेतु AI — विद्या जर्नी V2 कंटेंट एवं SQL निर्देशिका

यह फ़ाइल यह समझाने के लिए बनाई गई है कि **विद्या जर्नी (V2) फ़ीचर** डेटाबेस स्तर पर कैसे काम करता है, तालिकाओं (tables) का ढांचा कैसा है, द्विभाषी (Bilingual) समर्थन कैसे कार्य करता है, और Supabase में नई जर्नी तथा एमसीक्यू जोड़ने के लिए SQL इन्सर्ट क्वेरीज़ कैसे तैयार की जाती हैं।

---

## 1. डेटाबेस तालिका संरचना (Table Structure)

विद्या जर्नी का पूरा डेटा तीन प्रमुख ग्लोबल तालिकाओं में स्टोर होता है। विदेशी कुंजी (Foreign Key) संबंधों के कारण इनका एक-दूसरे से गहरा जुड़ाव है:

### क. `public.global_journey_templates` (जर्नी का मुख्य ढांचा)
इसमें जर्नी का शीर्षक, श्रेणी, और अवधि की जानकारी होती है।

| कॉलम का नाम | डेटा टाइप | विवरण |
| :--- | :--- | :--- |
| `id` | `uuid` | जर्नी टेम्पलेट की अद्वितीय पहचान (Primary Key) |
| `title` | `text` | जर्नी का शीर्षक (जैसे: "कक्षा 11 भौतिक विज्ञान - मैकेनिक्स मास्टरी (हिंदी)") |
| `description` | `text` | जर्नी का संक्षिप्त विवरण और उद्देश्य |
| `duration_days`| `integer` | जर्नी की कुल अवधि (डिफ़ॉल्ट `20` दिन) |
| `category` | `text` | जर्नी की श्रेणी (जैसे: `Physics Class 11`, `Chemistry Class 12`) |
| `version` | `integer` | टेम्पलेट का संस्करण (डिफ़ॉल्ट `1`) |
| `is_active` | `boolean` | क्या जर्नी सक्रिय है (`true`/`false`) |
| `is_deleted` | `boolean` | सॉफ्ट डिलीट फ्लैग |

---

### ख. `public.global_journey_tasks` (दैनिक कार्य / Daily Tasks)
इसमें प्रत्येक जर्नी के लिए प्रतिदिन किए जाने वाले कार्य और उनके अंतर्गत पढ़े जाने वाले विषय (Topics) होते हैं।

| कॉलम का नाम | डेटा टाइप | विवरण |
| :--- | :--- | :--- |
| `id` | `uuid` | कार्य की अद्वितीय आईडी (Primary Key) |
| `template_id` | `uuid` | संबंधित जर्नी टेम्पलेट की आईडी (Foreign Key) |
| `day_number` | `integer` | जर्नी का दिन (Day 1 से Day 20) |
| `task_title` | `text` | कार्य का शीर्षक (जैसे: "Day 1 Topic: Electric Charges & Coulomb's Law") |
| `task_description`| `text` | कार्य का विस्तृत विवरण (यूजर को क्या और कहाँ से पढ़ना है) |
| `task_type` | `text` | कार्य का प्रकार (जैसे: `'read'`) |
| `sort_order` | `integer` | क्रम संख्या (जैसे: `1`) |

---

### ग. `public.global_journey_mcqs` (दैनिक एमसीक्यू अभ्यास / Daily MCQs)
इसमें प्रत्येक दिन के लिए 20 बहुविकल्पीय प्रश्न (MCQs) होते हैं, जो सीधे दैनिक कार्य के विषय (Topic) पर आधारित होते हैं।

| कॉलम का नाम | डेटा टाइप | विवरण |
| :--- | :--- | :--- |
| `id` | `uuid` | प्रश्न की अद्वितीय आईडी (Primary Key) |
| `template_id` | `uuid` | संबंधित जर्नी टेम्पलेट की आईडी (Foreign Key) |
| `day_number` | `integer` | प्रश्न का दिन (Day 1 से Day 20) |
| `question_text`| `text` | प्रश्न का टेक्स्ट (जैसे: "शक्ति का SI मात्रक क्या है?") |
| `option_a` | `text` | विकल्प A |
| `option_b` | `text` | विकल्प B |
| `option_c` | `text` | विकल्प C |
| `option_d` | `text` | विकल्प D |
| `correct_option`| `text` | सही विकल्प (`'A'`, `'B'`, `'C'`, या `'D'`) |
| `explanation` | `text` | प्रश्न का सही उत्तर और स्पष्टीकरण |
| `difficulty_level`| `text` | कठिनाई स्तर: `'easy'`, `'medium'`, या `'hard'` |
| `points` | `integer` | सही उत्तर पर मिलने वाले पॉइंट्स (डिफ़ॉल्ट `1`) |

---

## 2. द्विभाषी समर्थन (Bilingual Design)

डेटाबेस स्कीमा में बिना कोई जटिल बदलाव किए, भाषा चयन को अत्यंत सरल रूप में लागू किया गया है:
1. **पृथक टेम्पलेट्स (Separate Templates):** प्रत्येक कोर्स के लिए अंग्रेजी और हिंदी के अलग-अलग टेम्पलेट बनाए गए हैं। जैसे:
   * **अंग्रेजी वर्जन:** `Class 11 Physics - Mechanics Mastery (English)`
   * **हिंदी वर्जन:** `कक्षा 11 भौतिक विज्ञान - मैकेनिक्स मास्टरी (हिंदी)`
2. **संबंधित कंटेंट:** जिस टेम्पलेट आईडी को यूजर चुनेगा, उसके अंतर्गत आने वाले कार्य (Tasks) और एमसीक्यू (MCQs) उसी भाषा में दिखाई देंगे:
   * अंग्रेजी टेम्पलेट में सभी प्रश्न, विकल्प और स्पष्टीकरण **अंग्रेजी** में होंगे।
   * हिंदी टेम्पलेट में सभी प्रश्न, विकल्प और स्पष्टीकरण **हिंदी** में होंगे।

इससे यूजर को अपनी पसंदीदा भाषा में पढ़ने और अभ्यास करने का सहज अनुभव मिलता है।

---

## 3. SQL इन्सर्ट क्वेरीज़ के उदाहरण (SQL Insert Examples)

Supabase SQL Editor के माध्यम से सीधे डेटाबेस में नया कंटेंट जोड़ने के लिए नीचे दिए गए उदाहरणों का उपयोग करें:

### क. अंग्रेजी जर्नी का उदाहरण (English Journey)
```sql
-- 1. टेम्पलेट जोड़ें
INSERT INTO public.global_journey_templates (id, title, description, duration_days, category, version, is_active, is_deleted, created_at, updated_at) 
VALUES ('e81d77a0-0001-4c12-a167-9bf461d3fa01', 'Class 11 Physics - Mechanics Mastery (English)', 'Master Kinematics, Laws of Motion, Work, Energy & Power in 20 days.', 20, 'Physics Class 11', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;

-- 2. दिन 1 का कार्य (Task) जोड़ें
INSERT INTO public.global_journey_tasks (id, template_id, day_number, task_title, task_description, task_type, sort_order, is_active, is_deleted, created_at, updated_at) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'e81d77a0-0001-4c12-a167-9bf461d3fa01', 1, 'Day 1 Topic: Physical World & Units of Measurement', 'Study physical world and units of measurement from your NCERT book.', 'read', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;

-- 3. दिन 1 का एमसीक्यू (MCQ) जोड़ें
INSERT INTO public.global_journey_mcqs (id, template_id, day_number, question_text, option_a, option_b, option_c, option_d, correct_option, explanation, difficulty_level, points, is_active, is_deleted, created_at, updated_at) 
VALUES ('770e8400-e29b-41d4-a716-446655440000', 'e81d77a0-0001-4c12-a167-9bf461d3fa01', 1, 'What is the SI unit of power?', 'Watt', 'Joule', 'Newton', 'Pascal', 'A', 'Power is work done per unit time. Unit is Watt (J/s).', 'medium', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;
```

### ख. हिंदी जर्नी का उदाहरण (Hindi Journey)
```sql
-- 1. टेम्पलेट जोड़ें
INSERT INTO public.global_journey_templates (id, title, description, duration_days, category, version, is_active, is_deleted, created_at, updated_at) 
VALUES ('e81d77a0-0101-4c12-a167-9bf461d3fa01', 'कक्षा 11 भौतिक विज्ञान - मैकेनिक्स मास्टरी (हिंदी)', '20 दिनों में किनेमैटिक्स, न्यूटन के गति के नियम, कार्य, ऊर्जा और शक्ति सीखें।', 20, 'Physics Class 11', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;

-- 2. दिन 1 का कार्य (Task) जोड़ें
INSERT INTO public.global_journey_tasks (id, template_id, day_number, task_title, task_description, task_type, sort_order, is_active, is_deleted, created_at, updated_at) 
VALUES ('550e8400-e29b-41d4-a716-446655440111', 'e81d77a0-0101-4c12-a167-9bf461d3fa01', 1, 'दिन 1 टॉपिक: भौतिक जगत और मापन', 'आज का MCQ टेस्ट इसी टॉपिक पर आधारित रहेगा। कृपया एनसीईआरटी पुस्तक से इसका अच्छी तरह अध्ययन करें।', 'read', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;

-- 3. दिन 1 का एमसीक्यू (MCQ) जोड़ें
INSERT INTO public.global_journey_mcqs (id, template_id, day_number, question_text, option_a, option_b, option_c, option_d, correct_option, explanation, difficulty_level, points, is_active, is_deleted, created_at, updated_at) 
VALUES ('770e8400-e29b-41d4-a716-446655440111', 'e81d77a0-0101-4c12-a167-9bf461d3fa01', 1, 'शक्ति का SI मात्रक क्या है?', 'वाट', 'जूल', 'न्यूटन', 'पास्कल', 'A', 'शक्ति कार्य करने की दर है। इसका मात्रक वाट (J/s) है।', 'medium', 1, true, false, now(), now())
ON CONFLICT (id) DO NOTHING;
```

---

## 4. नई जर्नी जोड़ने की चरण-दर-चरण प्रक्रिया

यदि आप भविष्य में कोई नया कोर्स या जर्नी जोड़ना चाहते हैं, तो इन चरणों का पालन करें:

1. **अद्वितीय यूयूआईडी (UUIDs) तय करें:** ऑनलाइन UUID जनरेटर से टेम्पलेट, कार्यों और सभी 400 एमसीक्यू (20 दिन * 20 एमसीक्यू) के लिए अद्वितीय आईडी जनरेट करें।
2. **भाषा के अनुसार डेटा तैयार करें:**
   - यदि इंग्लिश कोर्स है, तो इंग्लिश में शीर्षक और प्रश्न लिखें।
   - यदि हिंदी कोर्स है, तो देवनागरी में शीर्षक और प्रश्न लिखें।
3. **CSV या SQL फ़ाइल बनाएं:**
   - `generate_csv.py` स्क्रिप्ट में नए टेम्पलेट विवरण और प्रश्नों की सूची जोड़ें, फिर स्क्रिप्ट चलाकर नई CSV फाइलें बनाएं।
   - अथवा, ऊपर दी गई SQL संरचना के अनुसार सीधे बड़े डेटासेट की SQL स्क्रिप्ट बनाएं।
4. **डेटाबेस में सुरक्षित करें:**
   - सुपबेस के **Table Editor** में जाकर तीनों संबंधित तालिकाओं में नई CSV फाइलें अपलोड (इम्पोर्ट) करें। 
   - अथवा सुपबेस **SQL Editor** में इन्सर्ट क्वेरी चलाकर डेटा डालें।
