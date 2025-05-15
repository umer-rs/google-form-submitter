package com.example;

import java.util.HashSet;
import java.util.List;

public class Logic
{

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
