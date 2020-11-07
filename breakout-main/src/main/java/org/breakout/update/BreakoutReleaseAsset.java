package org.breakout.update;

import java.net.URL;
import java.util.Objects;

public class BreakoutReleaseAsset {
	private Integer id;
	private String name;
	private String contentType;
	private long size;
	private URL browserDownloadUrl;
	private String os;
	private String arch;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public URL getBrowserDownloadUrl() {
		return browserDownloadUrl;
	}

	public void setBrowserDownloadUrl(URL browserDownloadUrl) {
		this.browserDownloadUrl = browserDownloadUrl;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public static final String ANY = "any";
	public static final String MACOS = "macos";
	public static final String WINDOWS = "windows";
	public static final String x86 = "x86";
	public static final String x64 = "x64";

	@Override
	public int hashCode() {
		return Objects.hash(arch, browserDownloadUrl, contentType, id, name, os, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BreakoutReleaseAsset other = (BreakoutReleaseAsset) obj;
		return Objects.equals(arch, other.arch)
			&& Objects.equals(browserDownloadUrl, other.browserDownloadUrl)
			&& Objects.equals(contentType, other.contentType)
			&& Objects.equals(id, other.id)
			&& Objects.equals(name, other.name)
			&& Objects.equals(os, other.os)
			&& size == other.size;
	}

	@Override
	public String toString() {
		return "BreakoutReleaseAsset [id="
			+ id
			+ ", name="
			+ name
			+ ", contentType="
			+ contentType
			+ ", size="
			+ size
			+ ", browserDownloadUrl="
			+ browserDownloadUrl
			+ ", os="
			+ os
			+ ", arch="
			+ arch
			+ "]";
	}

}
