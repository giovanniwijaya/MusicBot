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

[![Setup](http://i.imgur.com/VvXYp5j.png)](https://github.com/jagrosh/MusicBot/wiki/Setup)

## Features
  * Easy to run (just make sure Java is installed, and run!)
  * Fast loading of songs
  * No external keys needed (besides a Discord Bot token)
  * Smooth playback
  * Server-specific setup for the "DJ" role that can moderate the music
  * Clean and beautiful menus
  * Channel-topic playback bar
  * Supports many sites, including Youtube, Soundcloud, and more
  * Supports many online radio/streams
  * Supports local files
  * Playlist support (both web/youtube, and local)

## Example
![Loading Example...](https://i.imgur.com/kVtTKvS.gif)

## Setup
Please see the [Setup Page](https://github.com/jagrosh/MusicBot/wiki/Setup) in the wiki to run this bot yourself!

## Questions/Suggestions/Bug Reports
**Please read the [Suggested/Planned Features List](https://github.com/jagrosh/MusicBot/projects/1) before suggesting a feature**. If you'd like to suggest changes to how the bot functions, recommend more customization options, or report bugs, feel free to either open an [Issue](https://github.com/jagrosh/MusicBot/issues) on this repository, or join [my Discord server](https://discord.gg/0p9LSGoRLu6Pet0k). (Note: I will not accept any feature requests that will require additional API keys, nor any non-music features). If you like this bot, be sure to add a star to the libraries that make this possible: [**JDA**](https://github.com/DV8FromTheWorld/JDA) and [**lavaplayer**](https://github.com/sedmelluq/lavaplayer)

## Editing
Even though this bot (and the source code here) might not be easy to edit for inexperienced programmers, the main purpose of having the source public is to show the capabilities of the libraries, to allow others to understand how the bot works, and to allow those knowledgeable about java, JDA, and Discord bot development to contribute, and there has been no support provided for people looking to make changes on their own, I have managed to modify some bot behaviour, including making the `lyrics` command available anytime anywhere and reducing delay between `nowplaying` updates to 3 seconds, a skill I may or may not have picked up from precisely two hours of following a freely available “Java for beginners” tutorial back in 2018 and moving on to Python afterwards. I digress. Anyway, if you choose to make edits, please do so in accordance with the Apache 2.0 License.

## Building from source
`mvn clean install`
