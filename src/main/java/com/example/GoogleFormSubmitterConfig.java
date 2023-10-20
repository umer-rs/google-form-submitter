package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("googleformsubmitter")
public interface GoogleFormSubmitterConfig extends Config
{
	@ConfigItem(position = 0, keyName = "accountName", name = "Whitelisted RSN", description = "The account to submit drops from. This is not used for the actual submission link.")
	default String accountName()
	{
		return "";
	}

	@ConfigItem(position = 1, keyName = "formId", name = "Google Form ID", description = "Google Form ID")
	default String formId()
	{
		return "";
	}

	@ConfigItem(position = 2, keyName = "ibbApiKey", name = "IBB Api Key", description = "api.imgbb.com")
	default String ibbApiKey()
	{
		return "";
	}

	@ConfigItem(position = 3, keyName = "imageEntry", name = "Image Entry Key", description = "Entry to use for drop image")
	default String imageEntry()
	{
		return "";
	}

	@ConfigItem(position = 4, keyName = "entrySettings", name = "Misc Key/Value Pairs", description = "Miscellaneous Entry Key/Value Pairs")
	default String entrySettings()
	{
		return "//id,name";
	}

	@ConfigItem(position = 5, keyName = "npcNameEntry", name = "NPC Name Entry Key", description = "NPC Entry")
	default String npcNameEntry()
	{
		return "";
	}

	@ConfigItem(position = 6, keyName = "itemNameEntry", name = "Item Name Entry Key", description = "Item Entry")
	default String itemNameEntry()
	{
		return "";
	}

	@ConfigItem(position = 7, keyName = "itemDropMapping", name = "Drops Mapping", description = "Mapping of drops to Google Form Name")
	default String itemDropMapping()
	{
		return "";
	}
}
