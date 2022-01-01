package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.User;

/**
 * DJ Command for skipping a song without removing it from the repeating queue
 * https://github.com/chr-ibb
 */
public class NextCmd extends DJCommand {
	public NextCmd(Bot bot)
	{
		super(bot);
		this.name = "next";
		this.help = "skips the current song. If in repeat mode, re-add it to the queue";
		this.aliases = bot.getConfig().getAliases(this.name);
		this.bePlaying = true;
	}

	@Override
	public void doCommand(CommandEvent event)
	{
		AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
		RequestMetadata rm = handler.getRequestMetadata();

		AudioTrack track = handler.getPlayer().getPlayingTrack();
		handler.addTrackIfRepeat(track);

		event.replySuccess("Skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title
				+"** "+(rm.getOwner() == 0L ? "(autoplay)" : "(requested by **" + rm.user.username + "**)"));
		handler.getPlayer().stopTrack();
	}
}
