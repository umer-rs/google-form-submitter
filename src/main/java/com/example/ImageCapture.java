package com.example;

import com.google.gson.Gson;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.Data;
import static net.runelite.client.RuneLite.SCREENSHOT_DIR;
import net.runelite.client.util.ImageUtil;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageCapture
{
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static final String IBB_IMAGE_UPLOAD_URL = "https://api.imgbb.com/1/upload?key=";

	@Inject
	private OkHttpClient okHttpClient;
	@Inject
	private Gson gson;
	private HttpUrl ibbImageUploadUrl;

	@Inject
	ImageCapture(final String ibbApiKey)
	{
		this.ibbImageUploadUrl = HttpUrl.get(IBB_IMAGE_UPLOAD_URL + ibbApiKey);

	}

	String processScreenshot(Image img, String playerName, String suffix)
	{
		BufferedImage screenshot = ImageUtil.bufferedImageFromImage(img);
		File playerFolder = new File(SCREENSHOT_DIR, playerName + File.separator + "Google Form Submitter");
		File screenshotFile = new File(playerFolder, String.format("%s-%s.png", format(new Date()), suffix));
		playerFolder.mkdirs();
		try
		{
			ImageIO.write(screenshot, "png", screenshotFile);
			return this.uploadScreenshot(screenshotFile);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private String uploadScreenshot(File screenshotFile) throws IOException, NullPointerException
	{
		RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), screenshotFile);
		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
															 .addFormDataPart("image", screenshotFile.getName(),
																			  imageRequestBody)
															 .build();
		Request request = new Request.Builder().url(this.ibbImageUploadUrl).post(requestBody).build();

		try (Response response = okHttpClient.newCall(request).execute())
		{
			if (response.body() == null)
			{
				return "";
			}
			try (InputStream in = response.body().byteStream())
			{
				ImageUploadResponse imageUploadResponse = gson.fromJson(
					new InputStreamReader(in, StandardCharsets.UTF_8), ImageUploadResponse.class);

				if (imageUploadResponse.isSuccess())
				{
					return imageUploadResponse.getData().getUrl();
				}
			}
		}
		return "";
	}

	void updateApiKey(String ibbApiKey)
	{
		this.ibbImageUploadUrl = HttpUrl.get(IBB_IMAGE_UPLOAD_URL + ibbApiKey);
	}

	private String format(Date date)
	{
		synchronized (TIME_FORMAT)
		{
			return TIME_FORMAT.format(date);
		}
	}

	@Data
	private static class ImageUploadResponse
	{
		private Data data;
		private boolean success;

		@lombok.Data
		private static class Data
		{
			private String url;
		}
	}
}
