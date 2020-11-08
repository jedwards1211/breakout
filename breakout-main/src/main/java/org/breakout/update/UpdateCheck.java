package org.breakout.update;

import java.util.NoSuchElementException;

import org.andork.semver.SemVer;
import org.breakout.BreakoutMain;
import org.breakout.release.BreakoutRelease;
import org.breakout.release.BreakoutReleaseApi;
import org.breakout.release.BreakoutReleaseAsset;

public class UpdateCheck {
	public static interface Result {
		public boolean isUpToDate();
	}

	public static Result UpToDateResult = new Result() {
		@Override
		public String toString() {
			return "UpdateCheck.UpToDateResult";
		}

		@Override
		public boolean isUpToDate() {
			return true;
		}
	};

	public static class UpdateAvailableResult implements Result {
		@Override
		public boolean isUpToDate() {
			return false;
		}

		public final BreakoutRelease release;
		public final BreakoutReleaseAsset asset;

		public UpdateAvailableResult(BreakoutRelease release, BreakoutReleaseAsset asset) {
			this.release = release;
			this.asset = asset;
		}

		@Override
		public String toString() {
			return "UpdateAvailableResult [release=" + release + ", asset=" + asset + "]";
		}
	}

	@SuppressWarnings("serial")
	public static class UpdateCheckFailedException extends Exception {
		public UpdateCheckFailedException(Throwable cause) {
			super("Update check failed", cause);
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

	public static Result checkForUpdate() throws UpdateCheckFailedException {
		try {
			BreakoutRelease latest = BreakoutReleaseApi.getLatest();

			if (new SemVer(latest.getTagName()).compareTo(new SemVer(BreakoutMain.getVersion())) > 0) {
				BreakoutReleaseAsset asset = getAssetForThisSystem(latest);
				return new UpdateAvailableResult(latest, asset);
			}
			return UpToDateResult;
		}
		catch (Exception e) {
			throw new UpdateCheckFailedException(e);
		}
	}
}
