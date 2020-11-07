package org.breakout.update;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BreakoutReleaseGsonTest {
	@Test
	public void testParse() {
		BreakoutRelease actual =
			BreakoutReleaseGson.instance
				.fromJson(
					"{\"id\":33426814,\"tag_name\":\"v1.0.0\",\"name\":\"v1.0.0\",\"published_at\":\"2020-11-03T22:10:12Z\",\"assets\":[{\"id\":27904936,\"name\":\"breakout-1.0.0-all-platforms.jar\",\"content_type\":\"application/java-archive\",\"size\":10559323,\"browser_download_url\":\"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/breakout-1.0.0-all-platforms.jar\",\"os\":\"any\",\"arch\":\"any\"},{\"id\":27904635,\"name\":\"Breakout-1.0.0-x64.msi\",\"content_type\":\"application/octet-stream\",\"size\":81932288,\"browser_download_url\":\"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x64.msi\",\"os\":\"windows\",\"arch\":\"x64\"},{\"id\":27955958,\"name\":\"Breakout-1.0.0-x86.msi\",\"content_type\":\"application/octet-stream\",\"size\":83947520,\"browser_download_url\":\"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x86.msi\",\"os\":\"windows\",\"arch\":\"x86\"},{\"id\":27904602,\"name\":\"Breakout-1.0.0.dmg\",\"content_type\":\"application/octet-stream\",\"size\":88388809,\"browser_download_url\":\"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0.dmg\",\"os\":\"macos\",\"arch\":\"any\"}]}",
					BreakoutRelease.class);

		assertEquals(33426814, (int) actual.getId());
		assertEquals("v1.0.0", actual.getTagName());
		assertEquals(120, actual.getPublishedAt().getYear());
		assertEquals(10, actual.getPublishedAt().getMonth());
		assertEquals(3, actual.getPublishedAt().getDate());

		// assets: [
		// {
		// id: 27904936,
		// name: 'breakout-1.0.0-all-platforms.jar',
		// content_type: 'application/java-archive',
		// size: 10559323,
		// browser_download_url:
		// 'https://github.com/jedwards1211/breakout/releases/download/v1.0.0/breakout-1.0.0-all-platforms.jar',
		// os: 'any',
		// arch: 'any'
		// },
		assertEquals(27904936, (int) actual.getAssets().get(0).getId());
		assertEquals("breakout-1.0.0-all-platforms.jar", actual.getAssets().get(0).getName());
		assertEquals("application/java-archive", actual.getAssets().get(0).getContentType());
		assertEquals(10559323, actual.getAssets().get(0).getSize());
		assertEquals(
			"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/breakout-1.0.0-all-platforms.jar",
			actual.getAssets().get(0).getBrowserDownloadUrl().toString());
		assertEquals(BreakoutReleaseAsset.ANY, actual.getAssets().get(0).getOs());
		assertEquals(BreakoutReleaseAsset.ANY, actual.getAssets().get(0).getArch());
		// {
		// id: 27904635,
		// name: 'Breakout-1.0.0-x64.msi',
		// content_type: 'application/octet-stream',
		// size: 81932288,
		// browser_download_url:
		// 'https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x64.msi',
		// os: 'windows',
		// arch: 'x64'
		// },
		assertEquals(27904635, (int) actual.getAssets().get(1).getId());
		assertEquals("Breakout-1.0.0-x64.msi", actual.getAssets().get(1).getName());
		assertEquals("application/octet-stream", actual.getAssets().get(1).getContentType());
		assertEquals(81932288, actual.getAssets().get(1).getSize());
		assertEquals(
			"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x64.msi",
			actual.getAssets().get(1).getBrowserDownloadUrl().toString());
		assertEquals(BreakoutReleaseAsset.WINDOWS, actual.getAssets().get(1).getOs());
		assertEquals(BreakoutReleaseAsset.x64, actual.getAssets().get(1).getArch());

		// {
		// id: 27955958,
		// name: 'Breakout-1.0.0-x86.msi',
		// content_type: 'application/octet-stream',
		// size: 83947520,
		// browser_download_url:
		// 'https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x86.msi',
		// os: 'windows',
		// arch: 'x86'
		// },
		assertEquals(27955958, (int) actual.getAssets().get(2).getId());
		assertEquals("Breakout-1.0.0-x86.msi", actual.getAssets().get(2).getName());
		assertEquals("application/octet-stream", actual.getAssets().get(2).getContentType());
		assertEquals(83947520, actual.getAssets().get(2).getSize());
		assertEquals(
			"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0-x86.msi",
			actual.getAssets().get(2).getBrowserDownloadUrl().toString());
		assertEquals(BreakoutReleaseAsset.WINDOWS, actual.getAssets().get(2).getOs());
		assertEquals(BreakoutReleaseAsset.x86, actual.getAssets().get(2).getArch());

		// {
		// id: 27904602,
		// name: 'Breakout-1.0.0.dmg',
		// content_type: 'application/octet-stream',
		// size: 88388809,
		// browser_download_url:
		// 'https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0.dmg',
		// os: 'macos',
		// arch: 'any'
		// }
		// ]
		assertEquals(27904602, (int) actual.getAssets().get(3).getId());
		assertEquals("Breakout-1.0.0.dmg", actual.getAssets().get(3).getName());
		assertEquals("application/octet-stream", actual.getAssets().get(3).getContentType());
		assertEquals(88388809, actual.getAssets().get(3).getSize());
		assertEquals(
			"https://github.com/jedwards1211/breakout/releases/download/v1.0.0/Breakout-1.0.0.dmg",
			actual.getAssets().get(3).getBrowserDownloadUrl().toString());
		assertEquals(BreakoutReleaseAsset.MACOS, actual.getAssets().get(3).getOs());
		assertEquals(BreakoutReleaseAsset.ANY, actual.getAssets().get(3).getArch());

		System.out.println(actual);
	}
}
