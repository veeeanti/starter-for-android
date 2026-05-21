# Discord Bot Function for Appwrite

A serverless Python function to act as a Discord bot backend for your Android app.

## Setup Instructions

### 1. Create Appwrite Database

In your Appwrite Console, create:
- **Database**: `discord-bot-db` (or any name)
- **Collection**: `messages` with attributes:
  - `content` (string)
  - `sender` (string) 
  - `timestamp` (string)
  - `isBot` (boolean)

### 2. Create Discord Webhook

1. Go to your Discord server → Channel Settings → Integrations → Webhooks
2. Create a new webhook
3. Copy the webhook URL

### 3. Deploy the Function

Using Appwrite CLI:

```bash
# Install Appwrite CLI
npm install -g appwrite-cli

# Login
appwrite login

# Create function
appwrite init function

# Set environment variables
appwrite functions update discord-bot \
  --env DISCORD_WEBHOOK_URL="your_webhook_url" \
  --env APPWRITE_DATABASE_ID="discord-bot-db" \
  --env APPWRITE_PROJECT_ID="your_project_id" \
  --env APPWRITE_API_KEY="your_api_key"

# Deploy
appwrite deploy function
```

### 4. Update Android App

Update `DiscordConfig.kt` to call your Appwrite function instead of Discord directly:

```kotlin
object DiscordConfig {
    const val APPWRITE_ENDPOINT = "your-appwrite-url/v1"
    const val APPWRITE_PROJECT_ID = "your_project_id"
    const val FUNCTION_ID = "discord-bot"
}
```

## API Endpoints

### POST /send
Send a message to Discord:
```json
{
  "content": "Hello from Android!",
  "username": "MyAndroidApp",
  "avatar_url": "optional"
}
```

### GET /messages
Get recent messages:
```
GET /messages?limit=50
```

### POST /webhook
Receive Discord interactions (for two-way communication).

## Environment Variables

| Variable | Description |
|----------|-------------|
| `DISCORD_WEBHOOK_URL` | Discord webhook URL |
| `APPWRITE_DATABASE_ID` | Database ID for messages |
| `MESSAGES_COLLECTION_ID` | Collection ID for messages |