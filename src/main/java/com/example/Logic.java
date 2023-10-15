package com.example;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemStack;

import java.util.*;

public class Logic
{
	private static final HashSet<Integer> corruptedGauntletDrops = new HashSet<>(
		List.of(ItemID.GAUNTLET_CAPE, ItemID.CRYSTAL_WEAPON_SEED, ItemID.CRYSTAL_ARMOUR_SEED,
				ItemID.ENHANCED_CRYSTAL_WEAPON_SEED, ItemID.YOUNGLLEF, ItemID.CLUE_SCROLL_ELITE,
				ItemID.CLUE_SCROLL_ELITE_12075, ItemID.CLUE_SCROLL_ELITE_12076, ItemID.CLUE_SCROLL_ELITE_12077
		));

	static int handleCorruptedGauntletDrops(Collection<ItemStack> itemStackCollection)
	{
		itemStackCollection.removeIf(e -> corruptedGauntletDrops.contains(e.getId()));
		return itemStackCollection.size();
	}
}
