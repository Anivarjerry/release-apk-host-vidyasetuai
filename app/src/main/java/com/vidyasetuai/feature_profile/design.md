# Feature Profile - Design Tokens & HIG Specifications

## 1. Design Overview & Principles
- **Theme:** Apple Minimalist Flat HIG (iOS 18 / Telegram Neo Aesthetic)
- **Base Canvas Background:** Pure Crisp White (`#FFFFFF`)
- **Accent & Badges:** Emerald Green (`#10B981`) & Crisp Slate (`#0F172A`)
- **Component Styling:** Symmetrical Soft-Gray Rounded Capsules (`#F1F5F9` / `#F8FAFC`) with subtle 0.5dp borders (`#E2E8F0`)
- **Rendering Performance:** 60 FPS Virtualized `LazyColumn` with stable keys (`key = { it.id }`)

---

## 2. Color Palette & Design Tokens

| Token Name | Hex Code | Purpose |
| :--- | :---: | :--- |
| `ProfilePageBg` | `#FFFFFF` | Pure White Page Background |
| `ProfileCapsuleBg` | `#F1F5F9` | Soft Gray Background for Buttons, Stats Pills & Chips |
| `ProfileCapsuleBorder` | `#E2E8F0` | Hairline 0.5dp - 1dp Border for Capsules & Cards |
| `ProfileTextPrimary` | `#0F172A` | Bold & High-Contrast Typography |
| `ProfileTextSecondary` | `#64748B` | Subtitles, @username, Bio & Helper Labels |
| `ProfileEmerald` | `#10B981` | Verified Badge, Active Tab Indicator, Save Actions |
| `ProfileEmeraldLight` | `#ECFDF5` | Verified Chip & Mutual Connection Badges |
| `ProfileCoverScrim` | `Color.Black.copy(0.35f)` | Translucent Top Gradient Scrim for Status Bar Icons |

---

## 3. Visual Layout Architecture

```
┌────────────────────────────────────────────────────────┐
│ [ Cover Photo (Edge-to-Edge, 170dp) ]     [⚙️ Settings] │
│   📷 (Edit Cover Badge - Translucent Dark Pill)        │
│                                                        │
│       ┌──────────┐                                     │
│       │   (DP)   │ 📷 (Edit DP Badge - Emerald Circle) │
│       └──────────┘                                     │
│   (92dp Circle with 3dp Pure White Ring Border)        │
│                                                        │
│   Harsh Sharma  ✓ (Emerald Verified Badge)             │
│   @harsh_sharma                                        │
│   "Building VidyaSetu AI • Research in Physics"        │
│                                                        │
│   ┌──────────────────────┐    ┌─────────────────────┐  │
│   │  ✏️ Edit Profile      │    │   ↗️ Share Profile  │  │
│   │  (Gray Capsule Pill) │    │ (Gray Capsule Pill) │  │
│   └──────────────────────┘    └─────────────────────┘  │
├────────────────────────────────────────────────────────┤
│   ┌──────────────────┐    ┌──────────────────┐         │
│   │       128        │    │       450        │         │
│   │   Inspirations   │    │     Inspired     │         │
│   │   (Following)    │    │   (Followers)    │         │
│   │  (Gray Capsule)  │    │  (Gray Capsule)  │         │
│   └──────────────────┘    └──────────────────┘         │
├────────────────────────────────────────────────────────┤
│   [ 📚 Case Studies (12) ]    [ 💡 Experiences (8) ]   │
│   ════════════════════════                             │
│                                                        │
│   ┌────────────────────┐      ┌─────────────────────┐  │
│   │ Case Study Card 1  │      │ Case Study Card 2   │  │
│   └────────────────────┘      └─────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

---

## 4. Component Specifications

### 1. Cover Photo & Avatar Header
- **Cover Photo:**
  - Height: `170dp`, Edge-to-Edge behind status bar.
  - Fallback: Smooth modern gradient (Slate/Emerald) when no cover image is uploaded.
  - Top-Right: Glass Capsule Settings button (`Lucide.Settings`).
  - Top-Left: Translucent Camera pill for instant cover photo replacement.
- **Profile Avatar (DP):**
  - Size: `92dp` circular frame, horizontally centered, overlapping cover (`offset(y = -46.dp)`).
  - Border: `3.dp` solid `#FFFFFF` ring.
  - Bottom-Right: Emerald camera button (`28dp` circle) for instant DP upload.
  - **On Tap:** Opens Fullscreen Lightbox Image Viewer with zoom & dark backdrop.

### 2. User Info & Capsule Action Buttons
- Full Name: `20.sp`, `FontWeight.Bold`, `#0F172A` with Emerald Verified tick if `isVerified = true`.
- Username: `14.sp`, `FontWeight.Medium`, `#64748B` (`@username`).
- Bio: `14.sp`, `lineHeight = 20.sp`, `#334155`.
- **Action Buttons:**
  - Shape: `RoundedCornerShape(24.dp)` (Capsule Pill).
  - Background: `Color(0xFFF1F5F9)` (Soft Gray).
  - Border: `BorderStroke(1.dp, Color(0xFFE2E8F0))`.
  - Buttons: `[ ✏️ Edit Profile ]` and `[ ↗️ Share Profile ]`.

### 3. Interactive Stats Counter Pills
- Horizontal Row of 2 equal-width Soft Gray Capsule Cards:
  1. **Inspirations (Following):** Shows count (e.g. `128`) + "Inspirations" label. Tapping opens `InspirationsListScreen` (Tab 0).
  2. **Inspired (Followers):** Shows count (e.g. `450`) + "Inspired" label. Tapping opens `InspirationsListScreen` (Tab 1).
- Card Shape: `RoundedCornerShape(20.dp)`, Background: `Color(0xFFF1F5F9)`.

### 4. Parallax Collapsing Dynamic Glass Top Bar
- When scrolling down:
  - The cover collapses smoothly.
  - A Dynamic Glass Capsule Top Bar fades into view at the top of the screen (`Modifier.statusBarsPadding()`).
  - Contains small user DP (`32dp`), compact name, and Settings button.
  - When scrolled to the very top, top bar becomes completely transparent.

### 5. Segmented Content Switcher & 60 FPS Feed
- Two modern pill tabs:
  - **Tab 1: `Case Studies`** (Shows count badge e.g. `12`).
  - **Tab 2: `Experiences`** (Shows count badge e.g. `8`).
- Content: Virtualized 60 FPS cards rendered via `LazyColumn` / `LazyVerticalGrid`.

### 6. Edit Profile Bottom Sheet (`EditProfileBottomSheet.kt`)
- Apple Modal Bottom Sheet with drag handle.
- Fields: First Name, Last Name, Bio, Date of Birth Picker, Gender Dropdown, Preferred Language.
- Mandatory Native `BackHandler(enabled = true)` to dismiss sheet before screen exit.
