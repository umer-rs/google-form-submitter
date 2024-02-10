package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("googleformsubmitter")
public interface GoogleFormSubmitterConfig extends Config
{
	@ConfigItem(position = 0, keyName = "accountName", name = "Whitelisted RSN",
		description = "The account to submit drops from. This is not used for the actual submission.")
	default String accountName()
	{
		return "";
	}

	@ConfigItem(position = 1, keyName = "formId", name = "Google Form ID",
		description = "Google Form ID")
	default String formId()
	{
		return "";
	}

	@ConfigItem(position = 2, keyName = "ibbApiKey", name = "IBB Api Key",
		description = "Obtain one at api.imgbb.com. Plugin will not function without this field filled in.")
	default String ibbApiKey()
	{
		return "";
	}

	@ConfigItem(position = 3, keyName = "entrySettings", name = "Misc Key/Value Pairs",
		description = "Miscellaneous Entry Key/Value Pairs")
	default String entrySettings()
	{
		return "";
	}

	@ConfigItem(position = 4, keyName = "npcNameEntry", name = "NPC Name Entry Key",
		description = "NPC Entry Key")
	default String npcNameEntry()
	{
		return "";
	}

	@ConfigItem(position = 5, keyName = "itemNameEntry", name = "Item Name Entry Key",
		description = "Item Entry Key")
	default String itemNameEntry()
	{
		return "";
	}

	@ConfigItem(position = 6, keyName = "imageEntry", name = "Image Entry Key",
		description = "Entry to use for drop image")
	default String imageEntry()
	{
		return "";
	}

	@ConfigItem(position = 7, keyName = "soloChambersEntry", name = "Solo Raids Entry Key",
		description = "Entry to use for Solo Raids")
	default String soloChambersEntry()
	{
		return "";
	}

	@ConfigItem(position = 8, keyName = "allowSeasonalWorlds", name = "Allow Seasonal/Beta Worlds",
		description = "Allow drops on seasonal/beta worlds")
	default boolean allowSeasonalWorlds()
	{
		return false;
	}

	@ConfigItem(position = 9, keyName = "dropMappingUrl", name = "Drop Mapping URL",
		description = "URL of a drop mapping")
	default String dropMappingUrl()
	{
		return "";
	}

	@ConfigItem(position = 10, keyName = "itemDropMapping", name = "Drop Mapping",
		description = "NPC Name,Item ID,Submission NPC Name,Submission Item Name")
	default String itemDropMapping()
	{
		return "";
	}
}
