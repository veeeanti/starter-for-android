# Discord Bot Client for Android

A Discord bot client app built with Appwrite's starter kit for Android. This app uses Appwrite Functions as a backend to relay messages to Discord.

## Architecture

```
Android App ↔ Appwrite Function (Python) ↔ Discord Webhook
```

## Setup

### 1. Deploy Appwrite

Self-host Appwrite (see [Appwrite Docs](https://appwrite.io/docs)):
```bash
docker run -it --rm \
    --volume /var/run/docker.sock:/var/run/docker.sock \
    --volume ${PWD}/appwrite-data:/storage \
    appwrite/appwrite:1.6.1
```

### 2. Create Appwrite Database

In Appwrite Console:
1. Create a database named `discord-bot-db`
2. Create a collection named `messages` with attributes:
   - `content` (string, required)
   - `sender` (string, required)
   - `timestamp` (string)
   - `isBot` (boolean)

### 3. Get Discord Webhook URL

1. Go to your Discord server → Channel Settings → Integrations → Webhooks
2. Create a webhook and copy the URL

### 4. Deploy Python Function

```bash
# Install Appwrite CLI
npm install -g appwrite-cli

# Login
appwrite login

# Create function in Appwrite Console, then deploy:
cd functions/discord-bot
appwrite deploy function
```

Set environment variables for the function:
- `DISCORD_WEBHOOK_URL` - Your Discord webhook URL
- `APPWRITE_DATABASE_ID` - Your database ID
- `MESSAGES_COLLECTION_ID` - Your collection ID

### 5. Configure Android App

Edit `DiscordConfig.kt`:
```kotlin
object DiscordConfig {
    const val APPWRITE_ENDPOINT = "http://YOUR_APPWRITE_URL/v1"
    const val APPWRITE_PROJECT_ID = "YOUR_PROJECT_ID"
    const val APPWRITE_FUNCTION_ID = "YOUR_FUNCTION_ID"
}
```

For emulator testing, use `http://10.0.2.2/v1` for localhost.

### 6. Run the App

Open in Android Studio and run on emulator or device.

## Files

- `app/` - Android app source
- `functions/discord-bot/` - Python Appwrite function
  - `main.py` - Main function code
  - `requirements.txt` - Python dependencies

## API Endpoints

The Python function exposes:

| Endpoint | Description |
|----------|-------------|
| `POST /send` | Send message to Discord |
| `GET /messages` | Get recent messages |
| `POST /webhook` | Receive Discord interactions |