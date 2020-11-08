package org.breakout.release;

import java.io.IOException;
import java.net.URL;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BreakoutReleaseAssetTypeAdapter extends TypeAdapter<BreakoutReleaseAsset> {
	private static enum Field {
		id,
		name,
		content_type,
		size,
		browser_download_url,
		os,
		arch,
	}

	@Override
	public void write(JsonWriter out, BreakoutReleaseAsset value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BreakoutReleaseAsset read(JsonReader in) throws IOException {
		in.beginObject();
		BreakoutReleaseAsset result = new BreakoutReleaseAsset();
		while (in.hasNext()) {
			Field field;
			try {
				field = Field.valueOf(in.nextName());
			}
			catch (IllegalArgumentException ex) {
				continue;
			}
			switch (field) {
			case id:
				result.setId(in.nextInt());
				break;
			case name:
				result.setName(in.nextString());
				break;
			case content_type:
				result.setContentType(in.nextString());
				break;
			case size:
				result.setSize(in.nextLong());
				break;
			case browser_download_url:
				result.setBrowserDownloadUrl(new URL(in.nextString()));
				break;
			case os:
				result.setOs(in.nextString());
				break;
			case arch:
				result.setArch(in.nextString());
				break;
			}
		}
		in.endObject();
		return result;
	}

}
