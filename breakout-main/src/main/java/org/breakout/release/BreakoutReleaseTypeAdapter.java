package org.breakout.release;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BreakoutReleaseTypeAdapter extends TypeAdapter<BreakoutRelease> {
	private static enum Field {
		id,
		tag_name,
		name,
		published_at,
		assets,
	}

	private final BreakoutReleaseAssetTypeAdapter assetAdapter = new BreakoutReleaseAssetTypeAdapter();

	@Override
	public void write(JsonWriter out, BreakoutRelease value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BreakoutRelease read(JsonReader in) throws IOException {
		in.beginObject();
		BreakoutRelease result = new BreakoutRelease();
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
			case tag_name:
				result.setTagName(in.nextString());
				break;
			case name:
				result.setName(in.nextString());
				break;
			case published_at:
				TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(in.nextString());
				result.setPublishedAt(Date.from(Instant.from(ta)));
				break;
			case assets:
				in.beginArray();
				List<BreakoutReleaseAsset> assets = new ArrayList<>();
				while (in.hasNext()) {
					assets.add(assetAdapter.read(in));
				}
				in.endArray();
				result.setAssets(assets);
				break;
			}
		}
		in.endObject();
		return result;
	}

}
