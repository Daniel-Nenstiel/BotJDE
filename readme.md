# BotJDE - A bot for your friends

## Features
NYTimes Puzzle link consolidation \
Notifications on important dates like Holidays, birthdays and anniversaries \
***(Coming Soon)*** - Username color changing commands \

## How to run
Generate a .env file in /docker (there is an example) \
Populate it with your choice of user and password
 * POSTGRES_USER=abc
 * POSTGRES_PASSWORD=abc

Generate an application.yml file in src/main/resources (there is an example) \
Populate it with
* token: abcdef 
  * This is your bot token acquired by going to the discord developer panel
* defaultChannelId: 12345678
  * This is the channel ID of the location you want your scheduled posts to go. Acquired by right clicking and hitting "Copy Channel ID" in discord
* postgresUser: abc
  * Must match what is in your docker .env
* postgresPassword: abc
  * Must match what is in your docker.env

When both of these files are generated, navigate to /docker and run `docker compose up`

## Database
### Flyway
Flyway runs automatically on application start up and applies all relevant Migrations scripts in src/main/resources/db/migrations 
