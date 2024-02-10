package com.example;

import com.google.inject.Provides;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.DrawManager;
import net.runelite.http.api.loottracker.LootRecordType;

@Slf4j
@PluginDescriptor(name = "Google Form Submitter")
public class GoogleFormSubmitterPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private GoogleFormSubmitterConfig config;
	@Inject
	private ChatMessageManager chatMessageManager;
	@Inject
	private DrawManager drawManager;
	@Inject
	private ScheduledExecutorService executor;
	@Inject
	private ImageCapture imageCapture;

	private HashMap<String, HashMap<Integer, NpcDropTuple>> nameItemMapping;
	private String killType;
	private final HashSet<WorldType> unsuitableWorldTypes = new HashSet<>(
		List.of(WorldType.BETA_WORLD, WorldType.FRESH_START_WORLD, WorldType.QUEST_SPEEDRUNNING, WorldType.SEASONAL,
				WorldType.TOURNAMENT_WORLD));

	//<editor-fold desc="Event Bus/Config Methods">
	@Provides
	GoogleFormSubmitterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoogleFormSubmitterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		this.updateNameItemMapping();
		this.imageCapture.updateApiKey(config.ibbApiKey());
		this.killType = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("googleformsubmitter"))
		{
			return;
		}
		if (configChanged.getKey().equals("itemDropMapping") || configChanged.getKey().equals("dropMappingUrl"))
		{
			this.updateNameItemMapping();
		}
		else if (configChanged.getKey().equals("ibbApiKey"))
		{
			this.imageCapture.updateApiKey(configChanged.getNewValue());
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String chatMessage = event.getMessage();

		if (chatMessage.startsWith("Your completed Chambers of Xeric count is:"))
		{
			killType = NpcType.COX_REGULAR;
		}
		if (chatMessage.startsWith("Your completed Chambers of Xeric Challenge Mode " + "count is:"))
		{
			killType = NpcType.COX_CM;
		}
		if (chatMessage.startsWith("Your completed Theatre of Blood"))
		{
			killType = chatMessage.contains("Hard Mode") ? NpcType.TOB_HM : chatMessage.contains(
				"Story Mode") ? NpcType.TOB_SM : NpcType.TOB_REGULAR;
		}
		if (chatMessage.startsWith("Your completed Tombs of Amascut"))
		{
			killType = chatMessage.contains("Expert Mode") ? NpcType.TOA_XM : chatMessage.contains(
				"Entry Mode") ? NpcType.TOA_EM : NpcType.TOA_REGULAR;
		}
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived)
	{
		if (lootReceived.getType() == LootRecordType.NPC)
		{
			return;
		}
		processOnLootReceived(lootReceived);
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived)
	{
		var npc = npcLootReceived.getNpc();
		var lootReceived = npcLootReceived.getItems();
		var npcName = npc.getName();
		if (npcName == null)
		{
			return;
		}
		this.handleLootReceived(npcName, lootReceived);
	}
	//</editor-fold>

	private void updateNameItemMapping() throws NumberFormatException
	{
		String rawConfig;
		if ((rawConfig = getMappingResource(config.dropMappingUrl())) == null)
		{
			rawConfig = config.itemDropMapping();
		}
		rawConfig = rawConfig.replaceAll("\\s*,\\s*", ",");
		String[] rows = rawConfig.split("\n");
		HashMap<String, HashMap<Integer, NpcDropTuple>> nameItemMapping = new HashMap<>();
		for (String row : rows)
		{
			String[] elements = row.split(",");
			if (elements.length != 4)
			{
				continue;
			}
			String npcName = elements[0];
			int itemId = Integer.parseInt(elements[1]);
			String npcSubmissionName = elements[2];
			String itemSubmissionName = elements[3];

			NpcDropTuple mappedTuple = new NpcDropTuple(npcSubmissionName, itemSubmissionName);
			HashMap<Integer, NpcDropTuple> value = nameItemMapping.getOrDefault(npcName, new HashMap<>());
			value.put(itemId, mappedTuple);
			nameItemMapping.put(npcName, value);
			this.nameItemMapping = nameItemMapping;
		}
	}

	private List<NpcDropTuple> handleItemStackCollection(String npcName, Collection<ItemStack> itemStackCollection)
	{
		if (!nameItemMapping.containsKey(npcName))
		{
			return null;
		}
		HashMap<Integer, NpcDropTuple> acceptableDrops = nameItemMapping.get(npcName);
		return itemStackCollection.stream()
								  .map(ItemStack::getId)
								  .map(id -> acceptableDrops.getOrDefault(id, null))
								  .filter(Objects::nonNull)
								  .collect(Collectors.toList());
	}

	// Process onLootReceived event bus to identify the drop source
	private void processOnLootReceived(LootReceived lootReceived)
	{
		String npcName = lootReceived.getName();

		if (Logic.isRaid(npcName))
		{
			npcName = Logic.getRaidsType(npcName, killType);
			killType = null;
		}
		else if (npcName.equals("The Gauntlet"))
		{
			npcName = Logic.getGauntletType(lootReceived.getItems());
		}
		this.handleLootReceived(npcName, lootReceived.getItems());
	}

	private void handleLootReceived(String npcName, Collection<ItemStack> itemStackCollection)
	{
		if (config.ibbApiKey().isBlank())
		{
			return;
		}
		if (!isWhitelistedCharacter())
		{
			return;
		}
		if (!config.allowSeasonalWorlds() && isUnsuitableWorld())
		{
			return;
		}

		List<NpcDropTuple> dropsToSubmit = this.handleItemStackCollection(npcName, itemStackCollection);
		if (dropsToSubmit == null || dropsToSubmit.isEmpty())
		{
			return;
		}

		CompletableFuture<String> screenshotUrl = this.takeScreenshot(npcName);
		screenshotUrl.thenAccept(url -> {
			if (url.isEmpty())
			{
				return;
			}
			dropsToSubmit.forEach(npcDropTuple -> submitScreenshot(this.constructSubmissionUrl(url, npcDropTuple),
																   npcDropTuple.getItemName()));
		});
	}

	private void openAllChatbox()
	{
		if (getChatboxId() == 0)
		{
			return;
		}
		clientThread.invokeLater(() -> client.runScript(175, 1, 0));
	}

	private CompletableFuture<String> takeScreenshot(String npcName)
	{
		CompletableFuture<String> screenshotUrl = new CompletableFuture<>();

		this.openAllChatbox();
		Consumer<Image> imageCallback = (img) -> executor.submit(() -> screenshotUrl.complete(
			imageCapture.processScreenshot(img, client.getLocalPlayer().getName(), npcName)));

		executor.submit(() -> {
			while (getChatboxId() != 0)
			{
			}
			drawManager.requestNextFrameListener(imageCallback);
		});

		return screenshotUrl;
	}

	private String constructSubmissionUrl(String screenshotUrl, NpcDropTuple npcDropTuple)
	{
		StringBuilder sb = new StringBuilder("https://docs.google.com/forms/d/e/");

		String formId = config.formId().strip();
		if (!formId.matches("^[a-zA-Z0-9_-]{40,}$"))
		{
			var message = new ChatMessageBuilder().append("Google Form ID is malformed");
			chatMessageManager.queue(QueuedMessage.builder()
												  .type(ChatMessageType.ITEM_EXAMINE)
												  .runeLiteFormattedMessage(message.build())
												  .build());
			return null;
		}
		sb.append(formId);

		sb.append("/formResponse?");

		String entrySettings = config.entrySettings();
		entrySettings = entrySettings.replaceAll("\\s*,\\s*", ",");
		var entryArray = entrySettings.split("\n");
		for (var keyValueString : entryArray)
		{
			var keyValueArray = keyValueString.split(",");
			if (keyValueArray.length != 2)
			{
				var message = new ChatMessageBuilder().append("Key/Value pairs are " + "malformed");
				chatMessageManager.queue(QueuedMessage.builder()
													  .type(ChatMessageType.ITEM_EXAMINE)
													  .runeLiteFormattedMessage(message.build())
													  .build());
				return null;
			}
			var key = keyValueArray[0];
			var value = keyValueArray[1];
			sb.append("&entry.").append(key).append("=").append(value);
		}

		String npcNameEntry = config.npcNameEntry().strip();
		sb.append("&entry.").append(npcNameEntry).append("=").append(npcDropTuple.getNpcName());

		String itemNameEntry = config.itemNameEntry().strip();
		sb.append("&entry.").append(itemNameEntry).append("=").append(npcDropTuple.getItemName());

		String imageUrlEntry = config.imageEntry();
		if (imageUrlEntry.matches("\\d*"))
		{
			sb.append("&entry.").append(imageUrlEntry).append("=").append(screenshotUrl);
		}

		String soloChambersEntry = config.soloChambersEntry();
		if (!soloChambersEntry.isEmpty() && this.getCoXPartySize() == 1)
		{
			sb.append("&entry.")
			  .append(soloChambersEntry)
			  .append("=")
			  .append(
				  "Yes," + "+I+completed+Nightmare+or+a+raid" + "+all+by+myself+and+received+the" + "+drop" + "+above" + ".");
		}

		return sb.toString().replaceAll("\\s", "%20");
	}

	private void submitScreenshot(String googleFormUrl, String itemName)
	{
		submitScreenshot(googleFormUrl, itemName, true);
	}

	private void submitScreenshot(String googleFormUrl, String itemName, boolean displayInChat)
	{
		try
		{
			var url = new URL(googleFormUrl);
			var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() != 200)
			{
				if (displayInChat)
				{
					var message = new ChatMessageBuilder().append("Google Form was " + "submitted unsuccessfully.");
					chatMessageManager.queue(QueuedMessage.builder()
														  .type(ChatMessageType.ITEM_EXAMINE)
														  .runeLiteFormattedMessage(message.build())
														  .build());
				}
				log.info(connection.getResponseMessage());
				log.info(googleFormUrl);
			}
			else
			{
				if (displayInChat)
				{
					var message = new ChatMessageBuilder().append(
						String.format("Submission of %s successful.", itemName));
					chatMessageManager.queue(QueuedMessage.builder()
														  .type(ChatMessageType.ITEM_EXAMINE)
														  .runeLiteFormattedMessage(message.build())
														  .build());
				}
			}
		}
		catch (MalformedURLException e)
		{
			if (displayInChat)
			{
				var message = new ChatMessageBuilder().append("The URL constructed was " + "invalid.");
				chatMessageManager.queue(QueuedMessage.builder()
													  .type(ChatMessageType.ITEM_EXAMINE)
													  .runeLiteFormattedMessage(message.build())
													  .build());
			}
			log.info(googleFormUrl);
		}
		catch (IOException e)
		{
			var message = new ChatMessageBuilder().append(
				"There was an issue with the " + "connection to the Google" + " " + "Form.");
			chatMessageManager.queue(QueuedMessage.builder()
												  .type(ChatMessageType.ITEM_EXAMINE)
												  .runeLiteFormattedMessage(message.build())
												  .build());
			log.info(googleFormUrl);
			log.info(e.toString());
		}
	}

	private String getMappingResource(String mappingUrl)
	{
		if (mappingUrl.isEmpty())
		{
			return null;
		}
		try
		{
			var url = new URL(mappingUrl);
			var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() != 200)
			{
				var message = new ChatMessageBuilder().append("Mapping retrieval " + "unsuccessful.");
				chatMessageManager.queue(QueuedMessage.builder()
													  .type(ChatMessageType.ITEM_EXAMINE)
													  .runeLiteFormattedMessage(message.build())
													  .build());
				log.info(connection.getResponseMessage());
				log.info(mappingUrl);
				return null;
			}
			return new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
																						 .collect(
																							 Collectors.joining("\n"));
		}
		catch (Exception e)
		{
			log.info(String.valueOf(e));
			return null;
		}
	}

	private int getCoXPartySize()
	{
		CompletableFuture<Integer> raidPartySize = new CompletableFuture<>();
		clientThread.invoke(() -> raidPartySize.complete(client.getVarbitValue(9540)));
		try
		{
			return Math.max(0, raidPartySize.get(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			return 0;
		}

	}

	private int getChatboxId()
	{
		return client.getVarcIntValue(41);
	}

	private boolean isWhitelistedCharacter()
	{
		if (client.getLocalPlayer().getName() == null)
		{
			return false;
		}
		if (config.accountName() == null)
		{
			return false;
		}
		return client.getLocalPlayer().getName().equalsIgnoreCase(config.accountName());
	}

	private boolean isUnsuitableWorld()
	{
		return !Collections.disjoint(client.getWorldType(), unsuitableWorldTypes);
	}
}
