package com.example;

import com.google.inject.Provides;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
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
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.DrawManager;
import net.runelite.http.api.loottracker.LootRecordType;

@Slf4j
@PluginDescriptor(name = "Google Forms Submitter")
public class GoogleFormSubmitterPlugin extends Plugin implements KeyListener
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
	private KeyManager keyManager;
	@Inject
	private DrawManager drawManager;
	@Inject
	private ScheduledExecutorService executor;

	enum KillType
	{
		BARROWS, COX, COX_CM, TOB, TOB_SM, TOB_HM, TOA_ENTRY_MODE, TOA, TOA_EXPERT_MODE, REGULAR_GAUNTLET, CORRUPTED_GAUNTLET
	}

	private static final HashMap<String, LinkedHashSet<String>> onLootReceivedMap = new HashMap<>(
		Map.of("Chambers of Xeric", new LinkedHashSet<>(Arrays.asList("COX", "COX_CM")), "Theatre of Blood",
			   new LinkedHashSet<>(Arrays.asList("TOB", "TOB:SM", "TOB:HM")), "Tombs of Amascut",
			   new LinkedHashSet<>(Arrays.asList("TOA", "TOA_ENTRY_MODE", "TOA_EXPERT_MODE"))
		));

	private ImageCapture imageCapture;
	private HashMap<String, HashMap<String, String>> npcNameMapping;
	private KillType killType;

	@Provides
	GoogleFormSubmitterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoogleFormSubmitterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		var itemDropMapping = config.itemDropMapping();
		keyManager.registerKeyListener(this);
		this.imageCapture = new ImageCapture(config.ibbApiKey());
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(this);
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived)
	{
		if (!isWhitelistedCharacter())
		{
			return;
		}
		if (lootReceived.getType() == LootRecordType.NPC)
		{
			return;
		}
		processOnLootReceived(lootReceived);
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived)
	{
		if (!isWhitelistedCharacter())
		{
			return;
		}
		handleLootReceived(npcLootReceived.getNpc().getName(), );
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_G)
		{
			System.err.println("G");
			handleLootReceived("Abyssal Sire", null);

		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("googleformsubmitter"))
		{
			return;
		}
		if (configChanged.getKey().equals("itemDropMapping"))
		{
			var newMapping = configChanged.getNewValue();
		}
		if (configChanged.getKey().equals("ibbApiKey"))
		{
			this.imageCapture.updateApiKey(configChanged.getNewValue());
		}

//        var parsedMapping = parseItemDropMapping(newMapping);
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
			killType = KillType.COX;
		}
		if (chatMessage.startsWith("Your completed Chambers of Xeric Challenge Mode count is:"))
		{
			killType = KillType.COX_CM;
		}
		if (chatMessage.startsWith("Your completed Theatre of Blood"))
		{
			killType = chatMessage.contains("Hard Mode") ? KillType.TOB_HM : KillType.TOB;
		}
		if (chatMessage.startsWith("Your completed Tombs of Amascut"))
		{
			killType = chatMessage.contains("Expert Mode") ? KillType.TOA_EXPERT_MODE : chatMessage.contains(
				"Entry Mode") ? KillType.TOA_ENTRY_MODE : KillType.TOA;
		}
	}

	private void processOnLootReceived(LootReceived lootReceived)
	{
		String npcName = lootReceived.getName();
		if (onLootReceivedMap.containsKey(npcName))
		{
			npcName = handleSpecialChests(npcName);
			killType = null;
		}
	}

	private String handleSpecialChests(String npcName)
	{
		if (onLootReceivedMap.get(npcName).contains(killType.toString()))
		{
			return killType.toString();
		}
		return onLootReceivedMap.get(npcName).
	}

	private void handleLootReceived(String npcName, Collection<ItemStack> itemStackCollection)
	{
		this.openGameChatbox();
		CompletableFuture<String> screenshotUrl = this.takeScreenshot("Beans");

		screenshotUrl.thenAccept(url -> {
			if (url.isEmpty())
			{
				return;
			}
			var googleFormUrl = constructSubmissionUrl(url);
			submitScreenshot(googleFormUrl);
		});

		switch (npcName)
		{
			case "The Gauntlet":
				var nonUniqueDrops = Logic.handleCorruptedGauntletDrops(itemStackCollection);
				if (nonUniqueDrops == 4)
				{
					npcName = "Corrupted Gauntlet";
				}
				else if (nonUniqueDrops == 3)
				{
					npcName = "Regular Gauntlet";
				}
				break;
			case "Theatre of Blood":
			case "Chambers of Xeric":
			case "Tombs of Amascut":

		}
	}

	private String constructSubmissionUrl(String screenshotUrl)
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
				var message = new ChatMessageBuilder().append("Key/Value pairs are malformed");
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

		String imageUrlEntry = config.imageEntry();
		if (imageUrlEntry.matches("\\d*"))
		{
			sb.append("&entry.").append(imageUrlEntry).append("=").append(screenshotUrl);
		}

//		String rsn = config.accountName().strip();
//		if (!rsn.matches("^[a-zA-Z0-9-]{1,12}")) {
//			notifier.notify("Whitelisted RSN is malformed");
//			return null;
//		}
		return sb.toString().replaceAll("\\s", "%20");
	}

	private void submitScreenshot(String googleFormUrl)
	{
		try
		{
			var url = new URL(googleFormUrl);
			var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() != 200)
			{
				var message = new ChatMessageBuilder().append("Google Form was submitted unsuccessfully.");
				chatMessageManager.queue(QueuedMessage.builder()
													  .type(ChatMessageType.ITEM_EXAMINE)
													  .runeLiteFormattedMessage(message.build())
													  .build());
				log.info(connection.getResponseMessage());
			}
			else
			{
				var message = new ChatMessageBuilder().append("Drop was submitted successfully.");
				chatMessageManager.queue(QueuedMessage.builder()
													  .type(ChatMessageType.ITEM_EXAMINE)
													  .runeLiteFormattedMessage(message.build())
													  .build());
			}
		}
		catch (MalformedURLException e)
		{
			var message = new ChatMessageBuilder().append("The URL constructed was invalid.");
			chatMessageManager.queue(QueuedMessage.builder()
												  .type(ChatMessageType.ITEM_EXAMINE)
												  .runeLiteFormattedMessage(message.build())
												  .build());
			log.info(googleFormUrl);
		}
		catch (IOException e)
		{
			var message = new ChatMessageBuilder().append("There was an issue with the connection to the Google Form.");
			chatMessageManager.queue(QueuedMessage.builder()
												  .type(ChatMessageType.ITEM_EXAMINE)
												  .runeLiteFormattedMessage(message.build())
												  .build());
			log.info(e.toString());
		}
	}

	private CompletableFuture<String> takeScreenshot(String itemName)
	{
		CompletableFuture<String> screenshotUrl = new CompletableFuture<>();

		Consumer<Image> imageCallback = (img) -> executor.submit(() -> screenshotUrl.complete(
			imageCapture.processScreenshot(img, itemName, client.getLocalPlayer().getName())));
		drawManager.requestNextFrameListener(imageCallback);

		return screenshotUrl;
	}

	private void openGameChatbox()
	{
		if (getChatboxId() == 1)
		{
			return;
		}
		clientThread.invokeLater(() -> client.runScript(175, 1, 1));
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

	private int getChatboxId()
	{
		return client.getVarcIntValue(41);
	}
}
