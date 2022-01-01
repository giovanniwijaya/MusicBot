<img align="right" src="https://i.imgur.com/zrE80HY.png" height="200" width="200">

# JMusicBot

[![Downloads](https://img.shields.io/github/downloads/giovanniwijaya/MusicBot/total.svg)](https://github.com/giovanniwijaya/MusicBot/releases/latest)
[![Stars](https://img.shields.io/github/stars/giovanniwijaya/MusicBot.svg)](https://github.com/giovanniwijaya/MusicBot/stargazers)
[![Release](https://img.shields.io/github/release/giovanniwijaya/MusicBot.svg)](https://github.com/giovanniwijaya/MusicBot/releases/latest)
[![License](https://img.shields.io/github/license/giovanniwijaya/MusicBot.svg)](https://github.com/giovanniwijaya/MusicBot/blob/master/LICENSE)
[![CircleCI](https://img.shields.io/circleci/project/github/giovanniwijaya/MusicBot/main.svg)](https://circleci.com/gh/giovanniwijaya/MusicBot)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/io5nv2xalvqo39qn/branch/main?svg=true)](https://ci.appveyor.com/project/giovanniwijaya/musicbot/branch/master)
[![CodeFactor](https://www.codefactor.io/repository/github/giovanniwijaya/musicbot/badge)](https://www.codefactor.io/repository/github/giovanniwijaya/musicbot)

A cross-platform Discord music bot with a clean interface, and that is easy to set up and run yourself!

[![Setup](http://i.imgur.com/VvXYp5j.png)](https://jmusicbot.com/setup)

## Features
  * Easy to run (just make sure Java is installed, and run!)
  * Fast loading of songs
  * No external keys needed (besides a Discord Bot token)
  * Smooth playback
  * Server-specific setup for the "DJ" role that can moderate the music
  * Clean and beautiful menus
  * Supports many sites, including Youtube, Soundcloud, and more
  * Supports many online radio/streams
  * Supports local files
  * Playlist support (both web/youtube, and local)

## Supported sources and formats
JMusicBot supports all sources and formats supported by [lavaplayer](https://github.com/sedmelluq/lavaplayer#supported-formats):
### Sources
  * YouTube
  * SoundCloud
  * Bandcamp
  * Vimeo
  * Twitch streams
  * Local files
  * HTTP URLs
### Formats
  * MP3
  * FLAC
  * WAV
  * Matroska/WebM (AAC, Opus or Vorbis codecs)
  * MP4/M4A (AAC codec)
  * OGG streams (Opus, Vorbis and FLAC codecs)
  * AAC streams
  * Stream playlists (M3U and PLS)

## Example
![Loading Example…](https://i.imgur.com/kVtTKvS.gif)

## Setup
Please see the [Setup Page](https://jmusicbot.com/setup) to run this bot yourself!

## Questions/Suggestions/Bug Reports
**Please read the [Issues List](https://github.com/jagrosh/MusicBot/issues) before suggesting a feature**. If you have a question, need troubleshooting help, or want to brainstorm a new feature, please start a [Discussion](https://github.com/jagrosh/MusicBot/discussions). If you'd like to suggest a feature or report a reproducible bug, please open an [Issue](https://github.com/jagrosh/MusicBot/issues) on this repository. If you like this bot, be sure to add a star to the libraries that make this possible: [**JDA**](https://github.com/DV8FromTheWorld/JDA) and [**lavaplayer**](https://github.com/sedmelluq/lavaplayer)!

## Editing
Even though this bot (and the source code here) might not be easy to edit for inexperienced programmers, the main purpose of having the source public is to show the capabilities of the libraries, to allow others to understand how the bot works, and to allow those knowledgeable about java, JDA, and Discord bot development to contribute, and there has been no support provided for people looking to make changes on their own, I have managed to modify some bot behaviour, including making the `lyrics` command available anytime anywhere and reducing delay between `nowplaying` updates to 3 seconds, a skill I may or may not have picked up from precisely two hours of following an old free-of-charge “Java for beginners” tutorial back in 2018 and moving on to Python afterwards. I digress. Anyway, if you choose to make edits, please do so in accordance with the Apache 2.0 License.

## Building from source
`mvn clean install`