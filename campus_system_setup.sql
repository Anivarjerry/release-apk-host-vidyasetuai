-- ================================================================
-- विद्यासेतु AI — कैंपस चर्चा कक्ष (Campus Discussion Rooms) SQL स्क्रिप्ट
-- संस्करण: 1.0.0
-- इस स्क्रिप्ट को सुपबेस (Supabase) के SQL Editor में चलाएं।
-- ================================================================

-- ================================================================
-- चरण 1: पुरानी तालिकाओं और फ़ंक्शंस को हटाना (यदि मौजूद हों)
-- ================================================================
-- CASCADE का उपयोग करने से इन तालिकाओं से जुड़े सभी ट्रिगर्स स्वतः ही नष्ट हो जाएंगे।
DROP TABLE IF EXISTS public.reports CASCADE;
DROP TABLE IF EXISTS public.messages CASCADE;
DROP TABLE IF EXISTS public.rooms CASCADE;
DROP TABLE IF EXISTS public.moderation_settings CASCADE;

DROP FUNCTION IF EXISTS public.check_message_cooldown() CASCADE;
DROP FUNCTION IF EXISTS public.clean_old_messages_trigger() CASCADE;
DROP FUNCTION IF EXISTS public.handle_updated_at() CASCADE;

-- ================================================================
-- चरण 2: साझा टाइमस्टैम्प ट्रिगर फ़ंक्शन (updated_at के लिए)
-- ================================================================
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ================================================================
-- चरण 3: चर्चा कक्ष (rooms) तालिका का निर्माण
-- ================================================================
CREATE TABLE public.rooms (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  category text,
  description text,
  message_cooldown_seconds integer NOT NULL DEFAULT 5, -- संदेश भेजने का कूलडाउन अंतराल (सेकंड में)
  is_active boolean NOT NULL DEFAULT true,
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  
  CONSTRAINT rooms_pkey PRIMARY KEY (id)
);

CREATE TRIGGER trg_update_rooms_updated_at
  BEFORE UPDATE ON public.rooms
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- ================================================================
-- चरण 4: संदेश (messages) तालिका का निर्माण
-- ================================================================
CREATE TABLE public.messages (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  room_id uuid NOT NULL,
  user_id uuid NOT NULL,
  content text NOT NULL,
  is_hidden boolean NOT NULL DEFAULT false,
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  
  CONSTRAINT messages_pkey PRIMARY KEY (id),
  CONSTRAINT messages_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.rooms(id) ON DELETE CASCADE,
  CONSTRAINT messages_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_update_messages_updated_at
  BEFORE UPDATE ON public.messages
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- ================================================================
-- चरण 5: रिपोर्ट (reports) तालिका का निर्माण
-- ================================================================
CREATE TABLE public.reports (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  message_id uuid NOT NULL,
  reporter_user_id uuid NOT NULL,
  reason text NOT NULL,
  status text NOT NULL DEFAULT 'pending', -- pending, resolved, dismissed
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  
  CONSTRAINT reports_pkey PRIMARY KEY (id),
  CONSTRAINT reports_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE,
  CONSTRAINT reports_reporter_user_id_fkey FOREIGN KEY (reporter_user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_update_reports_updated_at
  BEFORE UPDATE ON public.reports
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- ================================================================
-- चरण 6: संयम सेटिंग्स (moderation_settings - सिंगल-रो कीवर्ड टेबल)
-- ================================================================
CREATE TABLE public.moderation_settings (
  id integer NOT NULL DEFAULT 1,
  blocked_keywords jsonb NOT NULL DEFAULT '[]'::jsonb,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  
  CONSTRAINT moderation_settings_pkey PRIMARY KEY (id),
  CONSTRAINT only_one_row CHECK (id = 1) -- यह सुनिश्चित करता है कि केवल 1 रो ही रहे
);

CREATE TRIGGER trg_update_moderation_updated_at
  BEFORE UPDATE ON public.moderation_settings
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- ================================================================
-- चरण 7: स्पैम प्रोटेक्शन ट्रिगर (Spam Protection & Cooldown Trigger)
-- ================================================================
-- यदि कोई हैकर सीधे API से फ़्लड (spam) करता है, तो यह फ़ंक्शन रोकेगा।
-- यह यूज़र को कमरे के लिए निर्धारित सेकंड्स (जैसे 5 या 10) से पहले दूसरा संदेश भेजने से रोकेगा।
-- ================================================================
CREATE OR REPLACE FUNCTION public.check_message_cooldown()
RETURNS TRIGGER AS $$
DECLARE
  cooldown_limit integer;
  last_message_time timestamp with time zone;
BEGIN
  -- सुरक्षा के लिए क्लाइंट के भेजे टाइमस्टैम्प को ओवरराइड करके सर्वर का वर्तमान समय सेट करें
  NEW.created_at = now();
  NEW.updated_at = now();
  NEW.is_deleted = false;
  NEW.is_hidden = false;

  -- चर्चा कक्ष का कूलडाउन लिमिट प्राप्त करें
  SELECT message_cooldown_seconds INTO cooldown_limit
  FROM public.rooms
  WHERE id = NEW.room_id;

  -- यदि कूलडाउन सेट नहीं है, तो डिफ़ॉल्ट 5 सेकंड लें
  IF cooldown_limit IS NULL THEN
    cooldown_limit := 5;
  END IF;

  -- यूज़र द्वारा इस कक्ष में भेजे गए आखिरी एक्टिव संदेश का समय प्राप्त करें
  SELECT created_at INTO last_message_time
  FROM public.messages
  WHERE room_id = NEW.room_id
    AND user_id = NEW.user_id
    AND is_deleted = false
  ORDER BY created_at DESC
  LIMIT 1;

  -- यदि पिछला संदेश भेजा गया है, तो जाँच करें कि क्या कूलडाउन का उल्लंघन हुआ है
  IF last_message_time IS NOT NULL THEN
    IF now() < last_message_time + (cooldown_limit * INTERVAL '1 second') THEN
      RAISE EXCEPTION 'Spam Protection: कृपया % सेकंड प्रतीक्षा करें। संदेश भेजने की सीमा का उल्लंघन हुआ है।', cooldown_limit;
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_message_cooldown
  BEFORE INSERT ON public.messages
  FOR EACH ROW EXECUTE FUNCTION public.check_message_cooldown();

-- ================================================================
-- चरण 8: संदेश स्वतः हटाने का ट्रिगर (Auto-Delete 15 Minutes Trigger)
-- ================================================================
-- यह ट्रिगर प्रत्येक संदेश इंसर्ट होने के बाद चलता है (Statement Level पर)
-- और 15 मिनट से पुराने सभी संदेशों को स्वतः हटा देता है ताकि स्टोरेज न भरे।
-- ================================================================
CREATE OR REPLACE FUNCTION public.clean_old_messages_trigger()
RETURNS TRIGGER AS $$
BEGIN
  DELETE FROM public.messages
  WHERE created_at < now() - INTERVAL '15 minutes';
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_clean_old_messages
  AFTER INSERT ON public.messages
  FOR EACH STATEMENT EXECUTE FUNCTION public.clean_old_messages_trigger();

-- ================================================================
-- चरण 9: टेस्टिंग के लिए RLS को डिसेबल करना (Disable RLS for Testing)
-- ================================================================
ALTER TABLE public.rooms DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.messages DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.reports DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.moderation_settings DISABLE ROW LEVEL SECURITY;

-- ================================================================
-- चरण 10: रीयल-टाइम पब्लिकेशन चालू करना (Enable Real-Time)
-- ================================================================
-- सुपबेस के Realtime पब्लिकेशन में कमरे और संदेश तालिकाओं को जोड़ें
ALTER PUBLICATION supabase_realtime ADD TABLE public.rooms;
ALTER PUBLICATION supabase_realtime ADD TABLE public.messages;

-- ================================================================
-- चरण 11: टेस्ट डेटा सम्मिलित करना (Insert Test Rooms & Keywords)
-- ================================================================

-- 2 रूम्स बनाएं: "Global Room" (10 सेकंड कूलडाउन) और "Study Focus" (5 सेकंड कूलडाउन)
INSERT INTO public.rooms (name, category, description, message_cooldown_seconds) VALUES
('Global Room', 'General', 'सभी विद्यार्थियों के लिए सामान्य चर्चा कक्ष (10 सेकंड कूलडाउन)', 10),
('Study Focus', 'Education', 'पढ़ाई और प्रतियोगी परीक्षाओं से संबंधित चर्चा कक्ष (5 सेकंड कूलडाउन)', 5);

-- 250+ प्रतिबंधित कीवर्ड्स (Spam, Sexual, Advertising, Hindi/Hinglish Abuse words) सिंगल रो में डालें
INSERT INTO public.moderation_settings (id, blocked_keywords, is_active) VALUES
(
  1,
  '[
    "sex", "xxx", "porn", "adult", "dating", "casino", "lottery", "free money", "earn online", 
    "crypto", "bitcoin", "hack", "bypass", "abuse", "vulgar", "double money", "subscribe", 
    "whatsapp group", "telegram channel", "click here", "cash prize", "jackpot", "viagra", 
    "escort", "nude", "naked", "sex chat", "horny", "erotic", "kamdev", "sambhog", "gupt rog", 
    "kamshastra", "hot video", "call girls", "make money fast", "get rich quick", "binary option", 
    "gambling", "invest now", "guaranteed profit", "no risk", "earn quick", "free bonus", 
    "win cash", "giveaway", "follow me", "earn easy money", "work from home job", "stock tip", 
    "forex", "wealth creation", "rich cheat", "drug", "marijuana", "weed", "cocaine", "heroin", 
    "meth", "pill", "mdma", "ecstasy", "weapon", "gun", "knife", "bomb", "kill", "suicide", 
    "terrorist", "hate speech", "racist", "slur", "bitch", "bastard", "fuck", "asshole", 
    "dick", "pussy", "vagina", "boobs", "breast", "orgasm", "masturbate", "penis", "semen", 
    "randi", "saala", "kameena", "harami", "chutiya", "bhadwa", "kamina", "madarchod", 
    "behenchod", "lodu", "gandu", "lowde", "laude", "cunt", "whore", "slut", "rape", "molest", 
    "pedophile", "child porn", "incest", "zoophilia", "bestiality", "blowjob", "deepthroat", 
    "handjob", "rimjob", "anal", "cum", "ejaculation", "hardcore", "softcore", "fetish", 
    "bondage", "bdsm", "kinky", "swingers", "threesome", "orgy", "gangbang", "strip club", 
    "sensual", "massage parlour", "adult toy", "dildo", "vibrator", "webcam show", "camgirl", 
    "playboy", "penthouse", "xxx chat", "adult dating", "hookup", "one night stand", 
    "milf", "lolita", "hentai", "doujinshi", "yaoi", "yuri", "nsfw", "18plus", "uncensored", 
    "spam link", "telegram link", "whatsapp link", "discord link", "instagram follow", 
    "buy views", "buy followers", "fake followers", "cheap price", "discount deal", 
    "gift card code", "amazon voucher", "free iphone", "spin the wheel", "lotto", "roulette", 
    "poker", "blackjack", "slot machine", "sports betting", "cricket betting", "ipl betting", 
    "dream11 hack", "my11circle hack", "earn daily", "paytm cash", "gpay transfer", 
    "direct message me", "dm for details", "inbox me", "join now link", "click below", 
    "limited time offer", "hurry up buy", "diet pill", "weight loss supplement", "skin whitening", 
    "penis enlargement", "breast enhancement", "erectile dysfunction", "potency booster", 
    "cbd oil", "weed online", "buy drug no prescription", "counterfeit bills", "fake passport", 
    "clone card", "cvv shop", "carding group", "hacked accounts", "netflix free account", 
    "premium account mod", "cracked software", "pirated movie", "torrent link", "bypass limit",
    "bhosdike", "bhosadi", "bhosadike", "bhosda", "bhosdi", "choot", "choote", "ma ki chut", 
    "maa ki chut", "maki chut", "makichut", "behen ki chut", "gaand", "gand", "saale", 
    "chutye", "chutiyo", "gandi", "bhadua", "bhadue", "katuya", "katua", "katuwa", "saali", 
    "sali", "gandya", "gandia", "chudail", "chudaail", "kutta", "kutte", "kutto", "kutti", 
    "kuttiya", "harampana", "haramzada", "haramzade", "haramzadi", "chutiyaap", "chutiyaapa", 
    "chodu", "chodo", "chodna", "chudna", "chudana", "gaandmaru", "gaandmasti", "gaandfaad", 
    "gaandfaadna", "lawda", "lode", "lowde", "chuchi", "chuchiya", "chuchiyan", "chuchiyo", 
    "चूत", "चूतिया", "गांड", "गांडू", "लौड़ा", "लौड़े", "लोड़ा", "लोडू", "मादरचोद", 
    "बहनचोद", "भोसड़ीके", "भोसड़ी", "भोसड़ा", "रंडी", "भड़वा", "साला", "साले", "साली", 
    "हरामी", "कमीना", "कमीने", "कुत्ता", "कुत्ते", "कुतिया", "हरामजादा", "हरामजादे", 
    "चोदू", "चोदना", "चुदवाना", "गांडू", "गांडमस्ती", "गांडफाड़", "गांडमरा", "गांडमराओ", "गांड़"
  ]'::jsonb,
  true
);
