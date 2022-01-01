/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.queue.FairQueue;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand
{
	private final static String LOAD = "\uD83D\uDCE5"; // 📥
	private final static String CANCEL = "\uD83D\uDEAB"; // 🚫

	private final String loadingEmoji;

	public PlayCmd(Bot bot)
	{
		super(bot);
		this.loadingEmoji = bot.getConfig().getLoading();
		this.name = "play";
		this.arguments = "<title|URL|subcommand>";
		this.help = "plays the provided song";
		this.aliases = bot.getConfig().getAliases(this.name);
		this.guildOnly = true;
		this.beListening = true;
		this.bePlaying = false;
		this.children = new Command[]{new PlaylistCmd(bot)};
	}

	@Override
	public void doCommand(CommandEvent event) 
	{
		if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
		{
			AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
			if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused())
			{
				if(DJCommand.checkDJPermission(event))
				{
					handler.getPlayer().setPaused(false);
					event.replySuccess("Resumed **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**.");
				}
				else
					event.replyError("Only DJs can unpause the player!");
				return;
			}
			StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" Play Commands:\n");
			builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <song title>` - plays the first result from Youtube");
			builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - plays the provided song, playlist, or stream");
			for(Command cmd: children)
				builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
			event.reply(builder.toString());
			return;
		}
		if(!event.getArgs().isEmpty()) {
			String args = event.getArgs().replaceAll("^<|>$", "");
			event.reply(loadingEmoji+" Loading… `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
		} else {
			for (Message.Attachment attachment: event.getMessage().getAttachments()) {
				event.reply(loadingEmoji+" Loading… `["+attachment.getUrl()+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), attachment.getUrl(), new ResultHandler(m,event,false)));
			}
		}
	}

	private class ResultHandler implements AudioLoadResultHandler
	{
		private final Message m;
		private final CommandEvent event;
		private final boolean ytsearch;

		private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
		{
			this.m = m;
			this.event = event;
			this.ytsearch = ytsearch;
		}

		private void loadSingle(AudioTrack track, AudioPlaylist playlist)
		{
			if(bot.getConfig().isTooLong(track))
			{
				m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
						+TimeUtil.formatTime(track.getDuration())+"` > `"+TimeUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).override(true).queue();
				return;
			}
			AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
			if (!checkDJLock(event, handler)) {
				return;
			}
			int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
			String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" Added **"+track.getInfo().title
					+"** (`"+TimeUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
			if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
				m.editMessage(addMsg).override(true).queue();
			else
			{
				m.editMessage(addMsg+"\n"+event.getClient().getWarning()+" This track has a playlist of **"+playlist.getTracks().size()+"** tracks attached. Select \"Load\" to load playlist.")
					.setActionRow(new Component[]{Button.primary("QUEUE_PLAYLIST:ACCEPT", "Load").withEmoji(Emoji.fromUnicode(LOAD)), Button.danger("QUEUE_PLAYLIST:REJECT", "Cancel").withEmoji(Emoji.fromUnicode(CANCEL))})
					.override(true).queue((msg) -> {
						bot.getWaiter().waitForEvent(ButtonClickEvent.class, (ev) -> {
							return ev.getMessage().getId().equals(m.getId());
						} ,(msgev) -> {
							if (msgev.getComponentId().startsWith("QUEUE_PLAYLIST")) {
								if(msgev.getComponentId().split(":")[1].equals("ACCEPT")){
									msg.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" Loaded **"+loadPlaylist(playlist, track)+"** additional tracks!").override(true).queue();
								} else {
									msg.editMessage("The playlist has NOT been loaded.").override(true).queue();
								}
							}
						}, 40, TimeUnit.SECONDS, () -> {
							msg.editMessage("No response detected within 40 seconds. Did not load playlist.").override(true).queue();
						});
					});
			}
		}

		private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
		{
			int[] count = {0};
			playlist.getTracks().stream().forEach((track) -> {
				if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
				{
					AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
					if (!checkDJLock(event, handler)) {
						return;
					}
					handler.addTrack(new QueuedTrack(track, event.getAuthor()));
					count[0]++;
				}
			});
			return count[0];
		}

		@Override
		public void trackLoaded(AudioTrack track)
		{
			loadSingle(track, null);
		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist)
		{
			if(playlist.getTracks().size()==1 || playlist.isSearchResult())
			{
				AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
				loadSingle(single, null);
			}
			else if (playlist.getSelectedTrack()!=null)
			{
				AudioTrack single = playlist.getSelectedTrack();
				loadSingle(single, playlist);
			}
			else
			{
				int count = loadPlaylist(playlist, null);
				if(count==0)
				{
					m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" All entries in this playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
							+"**) ")+"were longer than the allowed maximum (`"+bot.getConfig().getMaxTime()+"`)")).queue();
				}
				else
				{
					m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" Found "
							+(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
							+ playlist.getTracks().size()+"` entries; added to the queue!"
							+ (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" Tracks longer than the allowed maximum (`"
							+ bot.getConfig().getMaxTime()+"`) have been omitted." : ""))).override(true).queue();
				}
			}
		}

		@Override
		public void noMatches()
		{
			if(ytsearch)
				m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`.")).queue();
			else
				bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
		}

		@Override
		public void loadFailed(FriendlyException throwable)
		{
			if(throwable.severity==Severity.COMMON)
				m.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
			else
				m.editMessage(event.getClient().getError()+" Error loading track.").queue();
		}

		private boolean checkDJLock(CommandEvent event, AudioHandler handler) {
			FairQueue<QueuedTrack> queue = handler.getQueue();
			if (queue.isLocked()) {
				if (!DJCommand.checkDJPermission(event)) {
					event.replyError("The queue is currently locked. Only DJs can add tracks.");
					return false;
				}
			}
			return true;
		}
	}

	public class PlaylistCmd extends MusicCommand
	{
		public PlaylistCmd(Bot bot)
		{
			super(bot);
			this.name = "playlist";
			this.aliases = new String[]{"pl"};
			this.arguments = "<name>";
			this.help = "plays the provided playlist";
			this.beListening = true;
			this.bePlaying = false;
		}

		@Override
		public void doCommand(CommandEvent event) 
		{
			if(event.getArgs().isEmpty())
			{
				event.replyError("Please include a playlist name.");
				return;
			}
			Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
			if(playlist==null)
			{
				event.replyError("I could not find `"+event.getArgs()+".txt` in the Playlists folder.");
				return;
			}
			event.getChannel().sendMessage(loadingEmoji+" Loading playlist **"+event.getArgs()+"**… ("+playlist.getItems().size()+" item"+(playlist.getItems().size()==1 ? ")" : "s)")).queue(m -> 
			{
				AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
				playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
					StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
							? event.getClient().getWarning()+" No tracks were loaded!" 
							: event.getClient().getSuccess()+" Loaded **"+playlist.getTracks().size()+"** track"+(playlist.getTracks().size()==1 ? "!" : "s!"));
					if(!playlist.getErrors().isEmpty())
						builder.append("\nThe following tracks failed to load:");
					playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
					String str = builder.toString();
					if(str.length()>2000)
						str = str.substring(0,1994)+" (…)";
					m.editMessage(FormatUtil.filter(str)).override(true).queue();
				});
			});
		}
	}
}
