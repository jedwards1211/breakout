package org.breakout.update;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class BreakoutReleaseApi {
	public static BreakoutRelease getLatest()
		throws JsonSyntaxException,
		JsonIOException,
		UnsupportedEncodingException,
		MalformedURLException,
		IOException {
		return BreakoutReleaseGson.instance
			.fromJson(
				new InputStreamReader(
					new URL("https://breakoutcavesurvey.com/api/release/latest").openStream(),
					"UTF-8"),
				BreakoutRelease.class);
	}
}
