/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public abstract class DJCommand extends MusicCommand
{
	public DJCommand(Bot bot)
	{
		super(bot);
		this.category = new Category("DJ", DJCommand::checkDJPermission);
	}

	public static boolean checkDJPermission(CommandEvent event)
	{
		if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
			return true;
		if(event.getGuild()==null)
			return true;
		if(event.getMember().hasPermission(Permission.MANAGE_SERVER))
			return true;
		Settings settings = event.getClient().getSettingsFor(event.getGuild());
		Role dj = settings.getRole(event.getGuild());
		return dj!=null && (event.getMember().getRoles().contains(dj) || dj.getIdLong()==event.getGuild().getIdLong());
	}
}
