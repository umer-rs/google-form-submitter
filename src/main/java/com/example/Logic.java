package com.example;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemStack;

public class Logic
{
	private static final HashSet<Integer> corruptedGauntletDrops = new HashSet<>(
		List.of(ItemID.GAUNTLET_CAPE, ItemID.CRYSTAL_SHARD, ItemID.CRYSTAL_WEAPON_SEED, ItemID.CRYSTAL_ARMOUR_SEED,
				ItemID.ENHANCED_CRYSTAL_WEAPON_SEED, ItemID.YOUNGLLEF, ItemID.CLUE_SCROLL_ELITE,
				ItemID.CLUE_SCROLL_ELITE_12074, ItemID.CLUE_SCROLL_ELITE_12075, ItemID.CLUE_SCROLL_ELITE_12076,
				ItemID.CLUE_SCROLL_ELITE_12077, ItemID.CLUE_SCROLL_ELITE_12078, ItemID.CLUE_SCROLL_ELITE_12079,
				ItemID.CLUE_SCROLL_ELITE_12080, ItemID.CLUE_SCROLL_ELITE_12081, ItemID.CLUE_SCROLL_ELITE_12082,
				ItemID.CLUE_SCROLL_ELITE_12083
		));

	static int handleCorruptedGauntletDrops(Collection<ItemStack> itemStackCollection)
	{
		itemStackCollection.removeIf(e -> corruptedGauntletDrops.contains(e.getId()));
		return itemStackCollection.size();
	}

	private static final HashSet<String> raidsNameSet = new HashSet<>(
		List.of("Chambers of Xeric", "Theatre of Blood", "Tombs of Amascut"));

	static String handleRaidsType(String npcName, String killType)
	{
		switch (npcName)
		{
			case "Chambers of Xeric":
				return handleChambers(killType);
			case "Theatre of Blood":
				return handleTob(killType);
			case "Tombs of Amascut":
				return handleToa(killType);
			default:
				return npcName;
		}
	}

	static boolean isRaid(String npcName)
	{
		return raidsNameSet.contains(npcName);
	}

	private static String handleChambers(String killType)
	{
		if (killType.equals("COX_CM"))
		{
			return "COX_CM";
		}
		return "COX";
	}

	private static String handleTob(String killType)
	{
		if (killType.equals("TOB_SM"))
		{
			return "TOB_SM";
		}
		if (killType.equals("TOB_HM"))
		{
			return "TOB_HM";
		}
		return "TOB";
	}

	private static String handleToa(String killType)
	{
		if (killType.equals("TOA_EM"))
		{
			return "TOA_EM";
		}
		if (killType.equals("TOA_XM"))
		{
			return "TOA_XM";
		}
		return "TOA";
	}
}
