package xyz.vee-anti.discordbotrelay.constants

object DiscordConfig {
    // Appwrite configuration
    const val APPWRITE_ENDPOINT = "https://ide.vee-anti.gay/v1" // For Android emulator localhost
    const val APPWRITE_PROJECT_ID = "6a0e26f50037390cdaf4" // Your Appwrite project ID
    const val APPWRITE_FUNCTION_ID = "" // Your deployed function ID
    
    // Legacy Discord webhook (not recommended - no message fetching)
    const val DISCORD_WEBHOOK_URL = ""
}