package com.example;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GoogleFormSubmitterTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GoogleFormSubmitterPlugin.class);
		RuneLite.main(args);
	}
}