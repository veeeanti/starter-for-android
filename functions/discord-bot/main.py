import os
import json
import httpx
from appwrite.client import Client
from appwrite.services.databases import Databases
from appwrite.id import ID
from datetime import datetime

# Initialize Appwrite client
client = Client()
client.set_endpoint(os.environ.get('APPWRITE_FUNCTION_ENDPOINT', 'http://localhost/v1'))
client.set_project(os.environ.get('APPWRITE_FUNCTION_PROJECT_ID', ''))
client.set_key(os.environ.get('APPWRITE_FUNCTION_API_KEY', ''))

databases = Databases(client)

# Discord webhook URL (set in Appwrite function env vars)
DISCORD_WEBHOOK_URL = os.environ.get('DISCORD_WEBHOOK_URL', '')

# Database/collection IDs (create these in Appwrite Console)
DATABASE_ID = os.environ.get('APPWRITE_DATABASE_ID', 'discord-bot-db')
MESSAGES_COLLECTION_ID = os.environ.get('MESSAGES_COLLECTION_ID', 'messages')

def main(req, res):
    """
    Discord Bot Function for Appwrite
    Endpoints:
    - POST /send - Send a message to Discord
    - GET /messages - Get recent messages from database
    - POST /webhook - Receive Discord interactions (for bot responses)
    """
    
    method = req.method
    path = req.path
    body = req.body
    
    try:
        if method == 'POST' and path == '/send':
            return send_to_discord(body)
        elif method == 'GET' and path == '/messages':
            return get_messages(req.query)
        elif method == 'POST' and path == '/webhook':
            return handle_discord_webhook(body)
        else:
            return res.json({
                'error': 'Not found',
                'available_endpoints': [
                    'POST /send - Send message to Discord',
                    'GET /messages - Get recent messages',
                    'POST /webhook - Discord webhook receiver'
                ]
            }, 404)
    except Exception as e:
        return res.json({'error': str(e)}, 500)


def send_to_discord(body):
    """Send a message to Discord via webhook"""
    if not DISCORD_WEBHOOK_URL:
        return res.json({'error': 'Discord webhook URL not configured'}, 500)
    
    content = body.get('content', '')
    username = body.get('username', 'Appwrite Bot')
    avatar_url = body.get('avatar_url', '')
    
    if not content:
        return res.json({'error': 'Content is required'}, 400)
    
    # Send to Discord webhook
    payload = {
        'content': content,
        'username': username,
        'avatar_url': avatar_url
    }
    
    try:
        with httpx.Client() as http_client:
            response = http_client.post(
                DISCORD_WEBHOOK_URL,
                json=payload,
                headers={'Content-Type': 'application/json'},
                timeout=10.0
            )
            response.raise_for_status()
            
            # Store message in Appwrite database
            message_id = ID.unique()
            databases.create_document(
                database_id=DATABASE_ID,
                collection_id=MESSAGES_COLLECTION_ID,
                document_id=message_id,
                data={
                    'content': content,
                    'sender': username,
                    'timestamp': datetime.utcnow().isoformat(),
                    'isBot': False
                }
            )
            
            return res.json({
                'success': True,
                'message_id': message_id,
                'content': content
            })
    except httpx.HTTPStatusError as e:
        return res.json({'error': f'Discord API error: {e.response.status_code}'}, 500)
    except Exception as e:
        return res.json({'error': str(e)}, 500)


def get_messages(query):
    """Get recent messages from Appwrite database"""
    try:
        limit = int(query.get('limit', 50))
        
        # List recent messages (sorted by timestamp desc)
        result = databases.list_documents(
            database_id=DATABASE_ID,
            collection_id=MESSAGES_COLLECTION_ID,
            limit=limit
        )
        
        return res.json({
            'messages': result.get('documents', []),
            'total': result.get('total', 0)
        })
    except Exception as e:
        return res.json({'error': str(e)}, 500)


def handle_discord_webhook(body):
    """Handle incoming Discord webhooks (for bot responses)"""
    # Discord sends interaction payloads
    # Store them in database for the Android app to fetch
    
    try:
        # Extract message data from Discord interaction
        message_data = {
            'content': body.get('data', {}).get('content', ''),
            'sender': 'Discord Bot',
            'timestamp': datetime.utcnow().isoformat(),
            'isBot': True,
            'raw': json.dumps(body)
        }
        
        message_id = ID.unique()
        databases.create_document(
            database_id=DATABASE_ID,
            collection_id=MESSAGES_COLLECTION_ID,
            document_id=message_id,
            data=message_data
        )
        
        return res.json({'success': True, 'message_id': message_id})
    except Exception as e:
        return res.json({'error': str(e)}, 500)