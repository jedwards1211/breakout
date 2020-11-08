package org.breakout.update;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.semver.SemVer;
import org.breakout.BreakoutMain;
import org.breakout.release.BreakoutRelease;
import org.breakout.release.BreakoutReleaseApi;
import org.breakout.release.BreakoutReleaseAsset;

public class UpdateCheck {
	private static final Logger logger = Logger.getLogger(UpdateCheck.class.getName());

	public static interface Result {
	}

	public static Result UpToDateResult = new Result() {
		@Override
		public String toString() {
			return "UpdateCheck.UpToDateResult";
		}
	};

	public static class FailedResult implements Result {
		public final Exception cause;

		public FailedResult(Exception cause) {
			super();
			this.cause = cause;
		}

		@Override
		public String toString() {
			return "UpdateCheckFailedResult [cause=" + cause + "]";
		}
	}

	public static class AvailableResult implements Result {
		public final BreakoutRelease release;
		public final BreakoutReleaseAsset asset;

		public AvailableResult(BreakoutRelease release, BreakoutReleaseAsset asset) {
			this.release = release;
			this.asset = asset;
		}

		@Override
		public String toString() {
			return "UpdateAvailableResult [release=" + release + ", asset=" + asset + "]";
		}
	}

	public static BreakoutReleaseAsset getAssetForThisSystem(BreakoutRelease release) {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		if (os.toLowerCase().startsWith("windows")) {
			return release
				.getAssets()
				.stream()
				.filter(
					a -> BreakoutReleaseAsset.WINDOWS.equals(a.getOs())
						&& ("amd64".equals(arch) ? BreakoutReleaseAsset.x64 : BreakoutReleaseAsset.x86)
							.equals(a.getArch()))
				.findFirst()
				.get();
		}
		else if (os.toLowerCase().startsWith("mac")) {
			return release
				.getAssets()
				.stream()
				.filter(a -> BreakoutReleaseAsset.MACOS.equals(a.getOs()))
				.findFirst()
				.get();
		}
		throw new NoSuchElementException("No installer found for " + os + " " + arch);
	}

	public static Result checkForUpdate() {
		try {
			logger.info("Checking for update...");
			BreakoutRelease latest = BreakoutReleaseApi.getLatest();

			if (new SemVer(latest.getTagName()).compareTo(new SemVer(BreakoutMain.getVersion())) > 0) {
				BreakoutReleaseAsset asset = getAssetForThisSystem(latest);
				logger.info("Found newer version: " + asset.getBrowserDownloadUrl());
				return new AvailableResult(latest, asset);
			}
			logger.info("Breakout is up to date");
			return UpToDateResult;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Update check failed", e);
			return new FailedResult(e);
		}
	}

	public static void checkForUpdateInBackground(Consumer<Result> handleResult) {
		Thread thread = new Thread(() -> {
			handleResult.accept(checkForUpdate());
		}, "Update Checker");
		thread.setDaemon(true);
		thread.start();
	}
}
