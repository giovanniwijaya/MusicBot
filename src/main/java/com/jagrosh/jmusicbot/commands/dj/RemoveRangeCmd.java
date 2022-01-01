package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.queue.FairQueue;

public class RemoveRangeCmd extends DJCommand 
{
	public RemoveRangeCmd(final Bot bot)
	{
		super(bot);
		this.name = "removerange";
		this.help = "removes a range of songs from the queue";
		this.arguments = "<from> <to>";
		this.aliases = bot.getConfig().getAliases(this.name);
		this.bePlaying = true;
	}

	@Override
	public void doCommand(final CommandEvent event) 
	{
		final AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
		final FairQueue<QueuedTrack> queue = handler.getQueue();
		if(queue.isEmpty())
		{
			event.replyError("There is nothing in the queue!");
			return;
		}

		final String[] parts = event.getArgs().split("\\s+", 2);

		if(parts.length < this.arguments.split("\\s+").length)
		{
			event.replyError("Please include two valid indexes.");
			return;
		}

		int fromIndex;
		int toIndex;

		try
		{
			// Validate the args
			fromIndex = Integer.parseInt(parts[0]);
			toIndex = Integer.parseInt(parts[1]);
			if (fromIndex > toIndex)
			{
				event.replyError("From position cannot be greater than the to position.");
				return;
			}

			// Validate that from and to are available
			if (isUnavailablePosition(queue, fromIndex))
			{
				event.replyError(fromIndex+" is not a valid position in the queue!");
				return;
			}
			if (isUnavailablePosition(queue, toIndex))
			{
				event.replyError(toIndex+" is not a valid position in the queue!");
				return;
			}
		}
		catch (NumberFormatException e)
		{
			event.replyError("Please provide two valid indexes.");
			return;
		}

		queue.removeRange(fromIndex, toIndex);
		final String reply = String.format("Removed all songs from the queue from position `%d` to `%d`.", fromIndex, toIndex);
		event.replySuccess(reply);
	}


	private static boolean isUnavailablePosition(final FairQueue<QueuedTrack> queue, final int position)
	{
		return position < 1 || position > queue.size();
	}
}