# विद्यासेतु AI — केस स्टडीज़ कंटेंट एवं SQL निर्देशिका

यह फ़ाइल यह समझाने के लिए बनाई गई है कि केस स्टडीज़ फ़ीचर डेटाबेस स्तर पर कैसे काम करता है, तालिकाओं (tables) का ढांचा कैसा है, और Supabase/PostgreSQL में नई केस स्टडी जोड़ने के लिए SQL इन्सर्ट क्वेरीज़ कैसे तैयार की जाती हैं।

---

## 1. डेटाबेस तालिका संरचना (Table Structure)

केस स्टडीज़ का पूरा डेटा `public.case_studies` तालिका में स्टोर होता है। इसमें होम स्क्रीन पर दिखने वाली जानकारी (जैसे शीर्षक, कवर इमेज, संक्षिप्त विवरण) और विस्तार स्क्रीन (Detail Screen) पर दिखने वाली मुख्य सामग्री (जैसे पैराग्राफ, वीडियो, चेकलिस्ट आदि) दोनों सुरक्षित की जाती हैं।

### मुख्य कॉलम (Main Columns)

| कॉलम का नाम | डेटा टाइप (Data Type) | विवरण |
| :--- | :--- | :--- |
| `id` | `uuid` | केस स्टडी की अद्वितीय पहचान (Primary Key) |
| `title` | `text` | केस स्टडी का शीर्षक (जैसे: "एंड्रॉइड ऐप डेवलपमेंट...") |
| `slug` | `text` | यूआरएल-फ्रेंडली नाम (जैसे: `android-app-development-roadmap`) |
| `cover_image_url` | `text` | कवर इमेज का पूरा यूआरएल (1:1 अनुपात) |
| `short_description` | `text` | कार्ड पर दिखने वाला छोटा विवरण (अधिकतम 300 अक्षर) |
| `language` | `text` | भाषा विकल्प: `'hindi'`, `'english'`, या `'bilingual'` |
| `tags` | `text[]` | टैग्स की सूची (जैसे: `{'AI', 'Coding', 'Career'}`) |
| `read_time_minutes` | `integer` | पढ़ने में लगने वाला अनुमानित समय (मिनटों में) |
| `schema_version` | `integer` | JSON पार्सर का संस्करण (डिफ़ॉल्ट `1`) |
| `content_blocks` | `jsonb` | मुख्य सामग्री ब्लॉक्स (विस्तृत JSON संरचना) |
| `additional_image_urls`| `text[]` | अतिरिक्त इमेज के यूआरएल (अधिकतम 4) |
| `author_type` | `text` | लेखक का प्रकार: `'platform'`, `'user'`, `'organization'`, या `'parent_organization'` |
| `author_user_id` | `uuid` | लेखक यूज़र की आईडी (यदि यूज़र द्वारा अपलोड किया गया है) |
| `status` | `text` | स्थिति: `'draft'`, `'under_review'`, `'published'`, या `'rejected'` |
| `is_deleted` | `boolean` | क्या रिकॉर्ड हटा दिया गया है (`true`/`false`) |
| `published_at` | `timestamp` | प्रकाशित होने का समय |

---

## 2. कंटेंट ब्लॉक्स की संरचना (JSON Format of `content_blocks`)

केस स्टडी की मुख्य सामग्री को एक लचीले JSON प्रारूप में सहेजा जाता है, जिसे `content_blocks` कॉलम में रखा जाता है। इस JSON का फॉर्मेट नीचे दिए गए अनुसार होता है:

```json
{
  "schema_version": 1,
  "blocks": [
    {
      "type": "title",
      "text": "मुख्य शीर्षक यहाँ लिखें"
    },
    {
      "type": "section",
      "text": "मुख्य अनुभाग (Section) का नाम"
    },
    {
      "type": "subsection",
      "text": "उप-अनुभाग (Sub-section) का नाम"
    },
    {
      "type": "paragraph",
      "text": "यहाँ आपका मुख्य पैराग्राफ कंटेंट आएगा जो यूज़र पढ़ेगा।"
    },
    {
      "type": "quote",
      "text": "कहावत या कोई महत्वपूर्ण विचार यहाँ लिखें",
      "author": "लेखक या विचारक का नाम (वैकल्पिक)"
    },
    {
      "type": "checklist",
      "items": [
        "पहला काम या पॉइंट",
        "दूसरा काम या पॉइंट",
        "तीसरा काम या पॉइंट"
      ]
    },
    {
      "type": "tip",
      "text": "यहाँ यूज़र के लिए कोई उपयोगी सुझाव (Tip) दें।"
    },
    {
      "type": "warning",
      "text": "यहाँ ध्यान रखने योग्य बातें या चेतावनी (Warning) लिखें।"
    },
    {
      "type": "video_link",
      "url": "वीडियो का यूट्यूब या अन्य लिंक",
      "thumbnail_url": "वीडियो का थंबनेल इमेज यूआरएल",
      "title": "वीडियो का शीर्षक"
    },
    {
      "type": "resource_link",
      "url": "दस्तावेज़ या वेबसाइट का लिंक",
      "label": "बटन पर दिखने वाला नाम (जैसे: 'ऑफिशियल वेबसाइट देखें')"
    },
    {
      "type": "image",
      "url": "इमेज का यूआरएल",
      "caption": "इमेज के नीचे दिखने वाला कैप्शन (वैकल्पिक)"
    }
  ]
}
```

---

## 3. SQL इन्सर्ट क्वेरी का उदाहरण (SQL Insert Example)

नीचे दिए गए SQL कमांड का उपयोग करके आप सीधे Supabase SQL Editor या किसी भी PostgreSQL डेटाबेस में एक नई केस स्टडी जोड़ सकते हैं। 

> **ध्यान दें:** SQL क्वेरी में JSONB ब्लॉक डालने के लिए सिंगल-कोट (`'`) के अंदर JSON लिखना होता है। यदि आपके JSON टेक्स्ट के अंदर सिंगल-कोट आता है, तो उसे दो बार सिंगल-कोट (`''`) लिखकर एस्केप करें।

```sql
INSERT INTO public.case_studies (
  title,
  slug,
  cover_image_url,
  short_description,
  language,
  tags,
  read_time_minutes,
  content_blocks,
  author_type,
  status,
  published_at
) VALUES (
  'Full Stack Web Development: करियर गाइड',
  'full-stack-web-development-career-guide',
  'https://images.unsplash.com/photo-1547658719-da2b81169be6?q=80&w=600&auto=format&fit=crop',
  'इस केस स्टडी में हम वेब डेवलपमेंट सीखने के सही रोडमैप, आवश्यक टूल्स, और बेस्ट यूट्यूब चैनल्स के बारे में विस्तार से जानेंगे।',
  'hindi',
  ARRAY['Web Development', 'Roadmap', 'Coding'],
  10,
  '{
    "schema_version": 1,
    "blocks": [
      {
        "type": "title",
        "text": "फुल स्टैक वेब डेवलपर कैसे बनें?"
      },
      {
        "type": "paragraph",
        "text": "वेब डेवलपमेंट आज के समय में सबसे लोकप्रिय करियर विकल्पों में से एक है। इसमें फ्रंटएंड और बैकएंड दोनों का विकास शामिल होता है।"
      },
      {
        "type": "section",
        "text": "1. फ्रंटएंड डेवलपमेंट (Frontend Development)"
      },
      {
        "type": "paragraph",
        "text": "फ्रंटएंड वह हिस्सा है जिसे यूज़र सीधे ब्राउज़र में देखता है। इसे सीखने की शुरुआत इन बेसिक चीज़ों से करें:"
      },
      {
        "type": "checklist",
        "items": [
          "HTML5 - वेबपेज का ढांचा बनाने के लिए",
          "CSS3 - स्टाइलिंग और सुंदर डिज़ाइन के लिए",
          "JavaScript - इंटरैक्टिविटी जोड़ने के लिए"
        ]
      },
      {
        "type": "tip",
        "text": "शुरुआत में सीधे React या Angular जैसे फ्रेमवर्क न सीखें। पहले बेसिक जावास्क्रिप्ट पर अपनी पकड़ मजबूत करें।"
      },
      {
        "type": "section",
        "text": "सीखने के लिए सबसे अच्छे यूट्यूब चैनल्स"
      },
      {
        "type": "paragraph",
        "text": "यदि आप हिंदी में वेब डेवलपमेंट सीखना चाहते हैं, तो आप इन चैनलों को देख सकते हैं:"
      },
      {
        "type": "video_link",
        "url": "https://www.youtube.com/watch?v=6mbwJ2ElqyM",
        "thumbnail_url": "https://images.unsplash.com/photo-1618401471353-b98aedd07871?q=80&w=400&auto=format&fit=crop",
        "title": "HTML, CSS और JS का पूरा कोर्स (Hindi)"
      },
      {
        "type": "resource_link",
        "url": "https://developer.mozilla.org/en-US/docs/Learn",
        "label": "MDN वेब डॉक्स पर पढ़ें"
      },
      {
        "type": "warning",
        "text": "ट्यूटोरियल हेल (Tutorial Hell) से बचें! सिर्फ वीडियो न देखें, बल्कि साथ में कोड भी लिखें और छोटे प्रोजेक्ट्स बनाएं।"
      },
      {
        "type": "quote",
        "text": "कोड केवल लिखने का नाम नहीं है, यह समस्याओं को सुलझाने की कला है।",
        "author": "वेब डेवलपमेंट एक्सपर्ट"
      }
    ]
  }'::jsonb,
  'platform',
  'published',
  now()
);
```

---

## 4. नई केस स्टडी जोड़ने की चरण-दर-चरण प्रक्रिया

यदि आपको किसी नए विषय (जैसे AI, Machine Learning, Cyber Security) पर केस स्टडी अपलोड करनी है, तो इन चरणों का पालन करें:

1. **विवरण तैयार करें:** केस स्टडी का शीर्षक, आकर्षक संक्षिप्त विवरण, और 1:1 रेश्यो वाली कवर इमेज का यूआरएल तय करें।
2. **सामग्री को ब्लॉक्स में बांटें:** अपनी पूरी सामग्री को अनुभागों (Sections), पैराग्राफों (Paragraphs), सुझावों (Tips), और चेतावनियों (Warnings) में बांट लें।
3. **JSONB सामग्री बनाएं:** ऊपर दिए गए JSON फॉर्मेट के अनुसार `content_blocks` का निर्माण करें। ध्यान रखें कि सभी कोट्स और ब्रैकेट सही तरीके से बंद हों।
4. **SQL तैयार करें:** ऊपर दिए गए SQL उदाहरण में अपने वेरिएबल्स और JSON को रिप्लेस करें।
5. **डेटाबेस में चलाएं:** Supabase SQL Editor में इस क्वेरी को रन (Execute) करें। यह तुरंत लाइव हो जाएगी और ऐप में दिखाई देने लगेगी।
