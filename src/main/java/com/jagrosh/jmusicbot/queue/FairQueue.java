/*
 * Copyright 2016 John Grosh (jagrosh).
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
package com.jagrosh.jmusicbot.queue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.entities.Message;

/**
 *
 * @author John Grosh (jagrosh)
 * @param <T>
 */
public class FairQueue<T extends Queueable> {
	private final List<T> list = new ArrayList<>();
	private final Set<Long> set = new HashSet<>();
	private boolean isLocked = false;
	private Message last = null;

	public int add(T item)
	{
		int lastIndex;
		for(lastIndex=list.size()-1; lastIndex>-1; lastIndex--)
			if(list.get(lastIndex).getIdentifier()==item.getIdentifier())
				break;
		lastIndex++;
		set.clear();
		for(; lastIndex<list.size(); lastIndex++)
		{
			if(set.contains(list.get(lastIndex).getIdentifier()))
				break;
			set.add(list.get(lastIndex).getIdentifier());
		}
		list.add(lastIndex, item);
		return lastIndex;
	}

	public void addAt(int index, T item)
	{
		if(index >= list.size())
			list.add(item);
		else
			list.add(index, item);
	}

	public void append(T item)
	{
		list.add(item);
	}

	public int size()
	{
		return list.size();
	}

	public T pull()
	{
		return list.remove(0);
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public boolean isLocked()
	{
		return isLocked;
	}

	public void toggleLock()
	{
		this.isLocked = !isLocked;
	}

	public List<T> getList()
	{
		return list;
	}

	public T get(int index)
	{
		return list.get(index);
	}

	public T remove(int index)
	{
		return list.remove(index);
	}

	public void removeRange(int fromIndex, int toIndex)
	{
		list.subList(fromIndex-1, toIndex).clear();
	}

	public int removeAll(long identifier)
	{
		int count = 0;
		for(int i=list.size()-1; i>=0; i--)
		{
			if(list.get(i).getIdentifier()==identifier)
			{
				list.remove(i);
				count++;
			}
		}
		this.removeControls();
		return count;
	}

	public void clear()
	{
		list.clear();
		this.removeControls();
	}

	public int shuffle(long identifier)
	{
		List<Integer> iset = new ArrayList<>();
		for(int i=0; i<list.size(); i++)
		{
			if(list.get(i).getIdentifier()==identifier)
				iset.add(i);
		}
		for(int j=0; j<iset.size(); j++)
		{
			int first = iset.get(j);
			int second = iset.get((int)(Math.random()*iset.size()));
			T temp = list.get(first);
			list.set(first, list.get(second));
			list.set(second, temp);
		}
		return iset.size();
	}

	public void skip(int number)
	{
		for(int i=0; i<number; i++)
			list.remove(0);
	}

	/**
	 * Move an item to a different position in the list
	 * @param from The position of the item
	 * @param to The new position of the item
	 * @return the moved item
	 */
	public T moveItem(int from, int to)
	{
		T item = list.remove(from);
		list.add(to, item);
		return item;
	}

	public int getNumberOfPages() {
		return (this.list.size() / 10) + 1;
	}

	public void setLastMessage(Message msg) {
		if (msg != null)
			this.removeControls();
		this.last = msg;
	}

	public Message getLastMessage() {
		return this.last;
	}

	public void removeControls() {
		if (this.last != null)
			this.last.editMessage(this.last.getEmbeds().get(0)).override(true).queue();
	}
}
