package org.breakout.mapbox;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class MapboxClient {
	private final String basePath = "https://api.mapbox.com";
	private String accessToken;
	public Fetcher fetcher = new DefaultFetcher();
	
	public static interface Fetcher {
		InputStream open(URL url) throws IOException;
	}
	
	public static class DefaultFetcher implements Fetcher {
		@Override
		public InputStream open(URL url) throws IOException {
			return url.openStream();
		}
	}

	public MapboxClient(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static enum ImageTileFormat {
		PNG("png"), PNGRAW("pngraw");
		
		public final String extension;

		private ImageTileFormat(String extension) {
			this.extension = extension;
		}
	}
	
	public static final String TERRAIN_RGB = "mapbox.terrain-rgb";
	public static final String SATELLITE = "mapbox.satellite";
	
	private static int getFileSize(URL url) {
	    URLConnection conn = null;
	    try {
	        conn = url.openConnection();
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).setRequestMethod("HEAD");
	        }
	        conn.getInputStream();
	        return conn.getContentLength();
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    } finally {
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).disconnect();
	        }
	    }
	}

	public URL getTileURL(String mapId, int zoom, long x, long y, boolean highDpi, ImageTileFormat format) throws IOException {
		return new URL(String.format("%s/v4/%s/%d/%d/%d%s.%s?access_token=%s",
			basePath, mapId, zoom, x, y, highDpi ? "@2x" : "", format.extension, accessToken));
	}
	
	public int getTileSizeBytes(String mapId, int zoom, long x, long y, boolean highDpi, ImageTileFormat format) throws IOException {
		return getFileSize(getTileURL(mapId, zoom, x, y, highDpi, format));
	}
		
	public int getTileSizeBytes(String mapId, long[] tile, boolean highDpi, ImageTileFormat format) throws IOException {
		return getFileSize(getTileURL(mapId, (int) tile[2], tile[0], tile[1], highDpi, format));
	}
	
	public InputStream getTileStream(String mapId, long[] tile, boolean highDpi, ImageTileFormat format) throws IOException {
		return getTileStream(mapId, (int) tile[2], tile[0], tile[1], highDpi, format);
	}

	public InputStream getTileStream(String mapId, int zoom, long x, long y, boolean highDpi, ImageTileFormat format) throws IOException {
		return fetcher.open(getTileURL(mapId, zoom, x, y, highDpi, format));
	}
	
	public BufferedImage getTile(String mapId, long[] tile, boolean highDpi, ImageTileFormat format) throws IOException {
		return getTile(mapId, (int) tile[2], tile[0], tile[1], highDpi, format);
	}
	
	public BufferedImage getTile(String mapId, int zoom, long x, long y, boolean highDpi, ImageTileFormat format) throws IOException {
		return ImageIO.read(fetcher.open(getTileURL(mapId, zoom, x, y, highDpi, format)));
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
