# 🧠 Minecraft AI Companion

A smart Forge-based AI companion mod for Minecraft (1.20.x).  
The bot follows the player, remembers their name and inventory across sessions, picks up items, respawns after death, and will soon learn from player actions.

## 📦 Features

- 👤 Follows and watches the player with natural movement  
- 🧠 Remembers name and inventory even after death or logout  
- 🎒 Picks up dropped items and informs the player  
- ⚰️ Drops inventory when killed  
- ♻️ Automatically respawns after 2 seconds  
- 🔧 In-game commands for resetting, clearing or summoning

## More info in my telegram: https://t.me/+x3quqHSX6PRjZDcy

## 📁 Requirements

- Java 17+
- Minecraft Forge 1.20.1
- Gradle 8.x+
- IntelliJ IDEA or VSCode (recommended)

## 🚀 How to Build and Run

1. Clone the repo
   ```
   git clone https://github.com/YOUR_USERNAME/minecraft-ai-companion.git
   cd minecraft-ai-companion
   ```

2. Open in IntelliJ IDEA  
   - Select `build.gradle` as project  
   - Let it import all dependencies

3. Generate run configs
   ```
   ./gradlew genIntellijRuns
   ```

4. Run the client  
   - Use the `runClient` configuration from the Gradle panel or "Edit Configurations"

5. Build the mod JAR
   ```
   ./gradlew build
   ```
   Output: `build/libs/`

## 🔧 In-Game Commands

| Command                   | Description                              |
|---------------------------|------------------------------------------|
| /companion reset_name     | Reset companion name                     |
| /companion clear_inventory| Clear all inventory items                |
| /companion die            | Kill the companion (drops inventory)     |
| /companion summon         | Save old one and spawn new near player   |

## 🧪 Tested On

- Minecraft 1.20.1 (Forge)
- Java 17 (Adoptium)
- IntelliJ IDEA 2023.2

## 📘 License

This project is for educational and personal use.  
If you want to use it in a public modpack or distribute it, please credit and link the repository.

## ✨ Coming Soon

- 🎤 Voice interaction (Whisper + TTS)
- 📜 Logging player actions
- 💬 Dialog system and learning
- 🧱 Teaching your bot how to build & craft
