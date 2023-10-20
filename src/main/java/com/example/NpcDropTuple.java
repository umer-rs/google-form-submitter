package com.example;

import lombok.Getter;

@Getter
public class NpcDropTuple
{
	private final String npcName;
	private final String itemName;

	public NpcDropTuple(String npcName, String itemName)
	{
		this.npcName = npcName;
		this.itemName = itemName;
	}
}
