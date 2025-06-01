<h1 align="center">🧠 AI Minecraft Companion</h1>

<p align="center">
  <img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExc245Z2c3NmR4dTY4amI0enFsaHJwazdzMjE4aGtrdnVkMnV0MThjZCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/I5k4ixA8x9jjS40QVx/giphy.gif" width="220" />
</p>

<p align="center">
  A smart Forge-based AI companion mod for Minecraft (1.20.x).<br>
  The bot follows the player, remembers their name and inventory across sessions, picks up items, respawns after death, and will soon learn from player actions.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-under%20active%20development-orange?style=for-the-badge" />
  <img src="https://img.shields.io/github/stars/Yameteshka/ai-minecraft-companion?style=for-the-badge" />
  <img src="https://img.shields.io/github/issues/Yameteshka/ai-minecraft-companion?style=for-the-badge" />
  <a href="https://github.com/Yameteshka/ai-minecraft-companion/blob/main/LICENSE.md">
    <img src="https://img.shields.io/badge/license-Custom%20%2F%20Contact%20Required-blueviolet?style=for-the-badge" />
  </a>
</p>

---

### 📦 Features

- 👤 Follows and watches the player with natural movement  
- 🧠 Remembers name and inventory even after death or logout  
- 🎒 Picks up dropped items and informs the player  
- ⚰️ Drops inventory when killed  
- ♻️ Automatically respawns after 2 seconds  
- 🔧 In-game commands for resetting, clearing or summoning

---

### 💬 More Info

More info in my telegram: https://t.me/+x3quqHSX6PRjZDcy

---

### 📁 Requirements

- Java 17+  
- Minecraft Forge 1.20.1  
- Gradle 8.x+  
- IntelliJ IDEA or VSCode (recommended)

---

### 🚀 How to Build and Run

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

4. Build the mod JAR
   ```
   ./gradlew build
   ```
   Output: `build/libs/`

5. Run the client  
   - Use the `runClient` configuration from the Gradle panel or "Edit Configurations"

---

### 🔧 In-Game Commands

| Command                    | Description                              |
|---------------------------|------------------------------------------|
| /companion reset_name     | Reset companion name                     |
| /companion clear_inventory| Clear all inventory items                |
| /companion die            | Kill the companion (drops inventory)     |
| /companion summon         | Save old one and spawn new near player   |

---

### 🧪 Tested On

- Minecraft 1.20.1 (Forge)  
- Java 17 (Oracle JDK)  
- IntelliJ IDEA 2023.2

---

### ✨ Coming Soon

- 🎤 Voice interaction  
- 📜 Logging player actions  
- 💬 Dialog system and learning  
- 🧱 Teaching system to your bot how to pvp & build & craft

---

### 🛠 Tech Stack Used

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Forge-303030?style=for-the-badge&logo=gradle&logoColor=white" />
  <img src="https://img.shields.io/badge/Minecraft-62b47a?style=for-the-badge&logo=minecraft&logoColor=white" />
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
  <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white" />
  <img src="https://img.shields.io/badge/VS%20Code-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white" />
</p>

---

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=100&section=footer"/>
</p>
