package com.example;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemStack;

public class Logic
{
	private static final HashSet<Integer> gauntletUniques = new HashSet<>(
		List.of(ItemID.GAUNTLET_CAPE, ItemID.CRYSTAL_SHARD, ItemID.CRYSTAL_WEAPON_SEED, ItemID.CRYSTAL_ARMOUR_SEED,
				ItemID.ENHANCED_CRYSTAL_WEAPON_SEED, ItemID.YOUNGLLEF, ItemID.CLUE_SCROLL_ELITE,
				ItemID.CLUE_SCROLL_ELITE_12074, ItemID.CLUE_SCROLL_ELITE_12075, ItemID.CLUE_SCROLL_ELITE_12076,
				ItemID.CLUE_SCROLL_ELITE_12077, ItemID.CLUE_SCROLL_ELITE_12078, ItemID.CLUE_SCROLL_ELITE_12079,
				ItemID.CLUE_SCROLL_ELITE_12080, ItemID.CLUE_SCROLL_ELITE_12081, ItemID.CLUE_SCROLL_ELITE_12082,
				ItemID.CLUE_SCROLL_ELITE_12083, ItemID.CLUE_SCROLL_ELITE_12085, ItemID.CLUE_SCROLL_ELITE_12086,
				ItemID.CLUE_SCROLL_ELITE_12087, ItemID.CLUE_SCROLL_ELITE_12088, ItemID.CLUE_SCROLL_ELITE_12089,
				ItemID.CLUE_SCROLL_ELITE_12090, ItemID.CLUE_SCROLL_ELITE_12091, ItemID.CLUE_SCROLL_ELITE_12092,
				ItemID.CLUE_SCROLL_ELITE_12093, ItemID.CLUE_SCROLL_ELITE_12094, ItemID.CLUE_SCROLL_ELITE_12095,
				ItemID.CLUE_SCROLL_ELITE_12096, ItemID.CLUE_SCROLL_ELITE_12097, ItemID.CLUE_SCROLL_ELITE_12098,
				ItemID.CLUE_SCROLL_ELITE_12099, ItemID.CLUE_SCROLL_ELITE_12100, ItemID.CLUE_SCROLL_ELITE_12101,
				ItemID.CLUE_SCROLL_ELITE_12102, ItemID.CLUE_SCROLL_ELITE_12103, ItemID.CLUE_SCROLL_ELITE_12104,
				ItemID.CLUE_SCROLL_ELITE_12105, ItemID.CLUE_SCROLL_ELITE_12106, ItemID.CLUE_SCROLL_ELITE_12107,
				ItemID.CLUE_SCROLL_ELITE_12108, ItemID.CLUE_SCROLL_ELITE_12109, ItemID.CLUE_SCROLL_ELITE_12110,
				ItemID.CLUE_SCROLL_ELITE_12111, ItemID.CLUE_SCROLL_ELITE_12113, ItemID.CLUE_SCROLL_ELITE_12114,
				ItemID.CLUE_SCROLL_ELITE_12115, ItemID.CLUE_SCROLL_ELITE_12116, ItemID.CLUE_SCROLL_ELITE_12117,
				ItemID.CLUE_SCROLL_ELITE_12118, ItemID.CLUE_SCROLL_ELITE_12119, ItemID.CLUE_SCROLL_ELITE_12120,
				ItemID.CLUE_SCROLL_ELITE_12121, ItemID.CLUE_SCROLL_ELITE_12122, ItemID.CLUE_SCROLL_ELITE_12123,
				ItemID.CLUE_SCROLL_ELITE_12124, ItemID.CLUE_SCROLL_ELITE_12125, ItemID.CLUE_SCROLL_ELITE_12126,
				ItemID.CLUE_SCROLL_ELITE_12127, ItemID.CLUE_SCROLL_ELITE_12130, ItemID.CLUE_SCROLL_ELITE_12132,
				ItemID.CLUE_SCROLL_ELITE_12133, ItemID.CLUE_SCROLL_ELITE_12134, ItemID.CLUE_SCROLL_ELITE_12135,
				ItemID.CLUE_SCROLL_ELITE_12136, ItemID.CLUE_SCROLL_ELITE_12137, ItemID.CLUE_SCROLL_ELITE_12138,
				ItemID.CLUE_SCROLL_ELITE_12140, ItemID.CLUE_SCROLL_ELITE_12141, ItemID.CLUE_SCROLL_ELITE_12142,
				ItemID.CLUE_SCROLL_ELITE_12143, ItemID.CLUE_SCROLL_ELITE_12144, ItemID.CLUE_SCROLL_ELITE_12145,
				ItemID.CLUE_SCROLL_ELITE_12146, ItemID.CLUE_SCROLL_ELITE_12147, ItemID.CLUE_SCROLL_ELITE_12148,
				ItemID.CLUE_SCROLL_ELITE_12149, ItemID.CLUE_SCROLL_ELITE_12150, ItemID.CLUE_SCROLL_ELITE_12151,
				ItemID.CLUE_SCROLL_ELITE_12152, ItemID.CLUE_SCROLL_ELITE_12153, ItemID.CLUE_SCROLL_ELITE_12154,
				ItemID.CLUE_SCROLL_ELITE_12155, ItemID.CLUE_SCROLL_ELITE_12156, ItemID.CLUE_SCROLL_ELITE_12157,
				ItemID.CLUE_SCROLL_ELITE_12158, ItemID.CLUE_SCROLL_ELITE_12159, ItemID.CLUE_SCROLL_ELITE_19782,
				ItemID.CLUE_SCROLL_ELITE_19783, ItemID.CLUE_SCROLL_ELITE_19784, ItemID.CLUE_SCROLL_ELITE_19785,
				ItemID.CLUE_SCROLL_ELITE_19786, ItemID.CLUE_SCROLL_ELITE_19787, ItemID.CLUE_SCROLL_ELITE_19788,
				ItemID.CLUE_SCROLL_ELITE_19789, ItemID.CLUE_SCROLL_ELITE_19790, ItemID.CLUE_SCROLL_ELITE_19791,
				ItemID.CLUE_SCROLL_ELITE_19792, ItemID.CLUE_SCROLL_ELITE_19793, ItemID.CLUE_SCROLL_ELITE_19794,
				ItemID.CLUE_SCROLL_ELITE_19795, ItemID.CLUE_SCROLL_ELITE_19796, ItemID.CLUE_SCROLL_ELITE_19797,
				ItemID.CLUE_SCROLL_ELITE_19798, ItemID.CLUE_SCROLL_ELITE_19799, ItemID.CLUE_SCROLL_ELITE_19800,
				ItemID.CLUE_SCROLL_ELITE_19801, ItemID.CLUE_SCROLL_ELITE_19802, ItemID.CLUE_SCROLL_ELITE_19803,
				ItemID.CLUE_SCROLL_ELITE_19804, ItemID.CLUE_SCROLL_ELITE_19805, ItemID.CLUE_SCROLL_ELITE_19806,
				ItemID.CLUE_SCROLL_ELITE_19807, ItemID.CLUE_SCROLL_ELITE_19808, ItemID.CLUE_SCROLL_ELITE_19809,
				ItemID.CLUE_SCROLL_ELITE_19810, ItemID.CLUE_SCROLL_ELITE_19811, ItemID.CLUE_SCROLL_ELITE_19813,
				ItemID.CLUE_SCROLL_ELITE_21524, ItemID.CLUE_SCROLL_ELITE_21525, ItemID.CLUE_SCROLL_ELITE_22000,
				ItemID.CLUE_SCROLL_ELITE_23144, ItemID.CLUE_SCROLL_ELITE_23145, ItemID.CLUE_SCROLL_ELITE_23146,
				ItemID.CLUE_SCROLL_ELITE_23147, ItemID.CLUE_SCROLL_ELITE_23148, ItemID.CLUE_SCROLL_ELITE_23770,
				ItemID.CLUE_SCROLL_ELITE_24253, ItemID.CLUE_SCROLL_ELITE_24773, ItemID.CLUE_SCROLL_ELITE_25498,
				ItemID.CLUE_SCROLL_ELITE_25499, ItemID.CLUE_SCROLL_ELITE_25786, ItemID.CLUE_SCROLL_ELITE_25787,
				ItemID.CLUE_SCROLL_ELITE_26943, ItemID.CLUE_SCROLL_ELITE_26944));

	static String getGauntletType(Collection<ItemStack> itemStackCollection)
	{
		var nonUniqueDrops = Logic.handleGauntletDrops(itemStackCollection);
		if (nonUniqueDrops == 2)
		{
			return NpcType.GAUNTLET_REGULAR;
		}
		if (nonUniqueDrops == 3)
		{
			return NpcType.GAUNTLET_CORRUPTED;
		}
		return NpcType.GAUNTLET_DIED;
	}

	private static long handleGauntletDrops(Collection<ItemStack> itemStackCollection)
	{
		return itemStackCollection.stream().filter(e -> !gauntletUniques.contains(e.getId())).count();
	}

	private static final HashSet<String> raidsNameSet = new HashSet<>(
		List.of("Chambers of Xeric", "Theatre of Blood", "Tombs of Amascut"));

	static boolean isRaid(String npcName)
	{
		return raidsNameSet.contains(npcName);
	}

	static String getRaidsType(String npcName, String killType)
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

	private static String handleChambers(String killType)
	{
		if (killType.equals(NpcType.COX_CM))
		{
			return NpcType.COX_CM;
		}
		return NpcType.COX_REGULAR;
	}

	private static String handleTob(String killType)
	{
		if (killType.equals(NpcType.TOB_SM))
		{
			return NpcType.TOB_SM;
		}
		if (killType.equals(NpcType.TOB_HM))
		{
			return NpcType.TOB_HM;
		}
		return NpcType.TOB_REGULAR;
	}

	private static String handleToa(String killType)
	{
		if (killType.equals(NpcType.TOA_EM))
		{
			return NpcType.TOA_EM;
		}
		if (killType.equals(NpcType.TOA_XM))
		{
			return NpcType.TOA_XM;
		}
		return NpcType.TOA_REGULAR;
	}
}
