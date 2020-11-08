package org.breakout.release;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BreakoutReleaseGson {
	public static final Gson instance;

	static {
		instance =
			new GsonBuilder()
				.registerTypeAdapter(BreakoutRelease.class, new BreakoutReleaseTypeAdapter())
				.registerTypeAdapter(BreakoutReleaseAsset.class, new BreakoutReleaseAssetTypeAdapter())
				.create();
	}
}
