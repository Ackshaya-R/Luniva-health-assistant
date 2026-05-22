# 🌙 Luniva — Your Digital Sanctuary for Mindful Health

Luniva is an Android health assistant app designed to be a compassionate companion for your everyday wellness. It combines an AI-powered chatbot, medication reminders, emergency SOS, and multilingual support into a single, accessible application.

---

## ✨ Features

### 🤖 AI Health Chatbot
- Powered by **Google Gemini 2.5 Flash** API
- Supports **voice input** (Speech-to-Text) and **voice responses** (Text-to-Speech)
- Conversational health Q&A with real-time AI responses

### 💊 Medication Reminder
- Set medication reminders by name and time (in minutes)
- **Voice-based reminder entry** for hands-free use
- Alarm triggers even when the app is in the background via `AlarmReceiver`

### 🚨 Emergency Center (SOS)
- Save a trusted emergency contact number
- One-tap **SOS call** to your saved contact
- Persistent contact storage via `SharedPreferences`

### 🌐 Multilingual Support
- Available in **English**, **Tamil (தமிழ்)**, and **Hindi**
- Full UI localization including navigation labels and action strings
- Language can be changed anytime from the settings screen

### 🧭 Bottom Navigation
- Smooth tab-based navigation across: **Home**, **Reminder**, **Emergency**, **Chatbot**, **Language**

---

## 🗂️ Project Structure

```
src/main/
├── java/com/acksha/healthassistant/
│   ├── MainActivity.java          # App home screen
│   ├── SplashActivity.java        # Splash/launch screen
│   ├── ChatbotActivity.java       # AI chatbot with voice I/O
│   ├── MedicationActivity.java    # Medication reminder setup
│   ├── EmergencyActivity.java     # SOS and emergency contact
│   ├── LanguageActivity.java      # Language selection
│   ├── ChatAdapter.java           # RecyclerView adapter for chat
│   ├── ChatMessage.java           # Chat message data model
│   ├── AlarmReceiver.java         # Background alarm handler
│   └── LocaleHelper.java          # Runtime language switching
└── res/
    ├── layout/                    # XML layouts for each screen
    ├── values/strings.xml         # English strings
    ├── values-ta/strings.xml      # Tamil strings
    ├── drawable/                  # Custom UI backgrounds and bubbles
    └── menu/bottom_nav_menu.xml   # Bottom navigation menu
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Arctic Fox or later)
- Android SDK 21+
- A valid **Google Gemini API key**

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Ackshaya-R/Luniva-health-assistant.git
   cd Luniva-health-assistant
   ```

2. Open in **Android Studio**

3. Add your Gemini API key in `ChatbotActivity.java`:
   ```java
   private static final String GEMINI_API_KEY = "YOUR_API_KEY_HERE";
   ```

4. Build and run on an emulator or physical Android device

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java | Core Android development |
| Android SDK | UI, activities, intents |
| Google Gemini 2.5 Flash API | AI chatbot responses |
| OkHttp | HTTP client for API calls |
| Android Speech API | Voice input (STT) |
| TextToSpeech API | Voice responses (TTS) |
| SharedPreferences | Local persistent storage |
| AlarmManager | Background medication reminders |
| Material Bottom Navigation | Tab-based navigation |

---

## 📱 Screens

| Screen | Description |
|--------|-------------|
| Splash | Branded loading screen |
| Home | Dashboard with health tagline |
| Chatbot | AI chat with voice input/output |
| Medication Reminder | Add reminders by name & time |
| Emergency Center | Save contact + SOS button |
| Language | Choose app language |

---

## 🌍 Localization

The app supports:
- 🇬🇧 English
- 🇮🇳 Tamil (`values-ta/strings.xml`)
- 🇮🇳 Hindi

Language preference is persisted and applied on restart using `LocaleHelper`.

---

> Made with 💜 by [Ackshaya R](https://github.com/Ackshaya-R)
