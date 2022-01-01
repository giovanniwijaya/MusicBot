package com.jagrosh.jmusicbot.commands.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import org.json.JSONArray;
import org.json.JSONObject;
import net.dv8tion.jda.api.Permission;

public class SkipSegCmd extends MusicCommand {
	public SkipSegCmd(Bot bot) {
		super(bot);
		this.name = "skipsegment";
		this.help = "skips to the end of the current non-music segment";
		this.aliases = bot.getConfig().getAliases(this.name);
		this.beListening = true;
		this.guildOnly = true;
		this.bePlaying = true;
	}

	private String extractYTIDRegex(String youtubeVideoURL) {
		String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(youtubeVideoURL);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return "fail";
		}
	}

	private long getCurrentTimeStamp(AudioHandler handler) {
		return handler.getPlayer().getPlayingTrack().getPosition();
	}

	private long parseMusicJSON(String jsonString, long curTime) {
		JSONObject obj = new JSONObject(jsonString);
		JSONArray segmentArr = obj.getJSONArray("segments");
		for (int i = 0; i < segmentArr.length(); i++) {
			Float segStart = segmentArr.getJSONObject(i).getFloat("startTime");
			Float segEnd = segmentArr.getJSONObject(i).getFloat("endTime");
			if (curTime >= segStart && curTime <= segEnd) {
				return (long) (segEnd * 1000);
			}
		}
		return -1;
	}

	public long connectToAPI(String videoID, AudioHandler handler, CommandEvent event)
			throws UnsupportedEncodingException, IOException {
		HttpURLConnection con = null;
		long curTime = getCurrentTimeStamp(handler) / 1000;
		boolean musicSegment = false;
		try {
			String nonMusicURLString = "https://sponsor.ajay.app/api/searchSegments?videoID=";
			nonMusicURLString += videoID;
			URL nonMusicURL = new URL(nonMusicURLString);
			String apiResponse = null;
			try {
				con = (HttpURLConnection) nonMusicURL.openConnection();
				con.setRequestMethod("GET");
				int responsecode = con.getResponseCode();
				if (responsecode == 200) {
					event.replySuccess("Found non-music segments in database!");
					musicSegment = true;
					BufferedReader in = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					while ((apiResponse = in.readLine()) != null) {
						return parseMusicJSON(apiResponse, curTime);
					}
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (!musicSegment) {
			return -2;

		} else {
			return -1;
		}
	}

	@Override
	public void doCommand(CommandEvent event) {
		AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
		try {
			String videoId = extractYTIDRegex(handler.getPlayer().getPlayingTrack().getInfo().uri);
			if (videoId != "fail") {
				long reply = connectToAPI(videoId, handler, event);
				if (reply == -2) {
					event.replyError("No segments found!");
				} else if (reply == -1) {
					event.replyError("Cannot skip here because no segment is currently playing!");
				} else {
					handler.getPlayer().getPlayingTrack().setPosition(reply);
					
					long absSeconds = reply / 1000;
					long minutes = absSeconds / 60;
					long modSeconds = absSeconds % 60;
					String formattedSeconds = String.format("%02d", modSeconds);
					String msg = "Skipping ahead to " + minutes + ":" + formattedSeconds + "!";
					event.replySuccess(msg);
				}
			} else {
				event.replyError("Could not get video ID!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}