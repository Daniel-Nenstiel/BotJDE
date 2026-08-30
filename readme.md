# BotJDE - A Bot for Your Friends

A personal Discord bot for friends servers built with Spring Boot 3, Discord4J, PostgreSQL, MyBatis, and Flyway.

## Features
* **Discord Slash Commands**: Native `/` application commands with instant guild registration.
* **Scheduled Notifications**: Automatic morning posts for birthdays and anniversaries (9:00 AM) and NYTimes puzzles (6:00 AM).
* **Multi-Server Configuration**: Profile-driven configuration (`dev` and `prod`) allowing per-server channel routing and feature toggles.
* **Database Management**: Schema and database functions managed with Flyway migrations.
* ***(Coming Soon)***: Username color changing commands.

## Slash Commands

| Command | Description | Response |
|---|---|---|
| `/test` | Ping the bot to verify it is running | `pong!` |
| `/puzzle` | Get today's NYTimes puzzle links on demand | List of daily puzzle links |
| `/poke` | Poke the bot | `pika` |

## How to Run

### 1. Environment Configuration
Create environment files in `/docker`:
* `.env.dev` (for development)
* `.env.prod` (for production)

Populate with your credentials (see `example-env`):
```env
BOT_TOKEN=your_discord_bot_token
POSTGRES_USER=botjde_user
POSTGRES_PASSWORD=your_secure_password
```

### 2. Application Configuration
Server and channel configuration lives in `src/main/resources`:
* `application-dev.yml` (Snip3sh0t server)
* `application-prod.yml` (BJDE server)

Configure your server ID and channel IDs:
```yaml
app:
  config:
    source: yaml
  servers:
    - id: YOUR_GUILD_ID
      name: ServerName
      defaultChannelId: YOUR_DEFAULT_CHANNEL_ID
      puzzleChannelId: YOUR_PUZZLE_CHANNEL_ID
      birthdaysEnabled: true
      anniversariesEnabled: true
      puzzlesEnabled: false
```

### 3. Start with Docker Compose
Navigate to `/docker` and start the desired container:

```bash
# Start development instance
docker compose up -d --build botjde-dev

# Start production instance
docker compose up -d --build botjde
```

## Database
Flyway runs automatically on application startup and applies all migration scripts in `src/main/resources/db/migration`.
 
