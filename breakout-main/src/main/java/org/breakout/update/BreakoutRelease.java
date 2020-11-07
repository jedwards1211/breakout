package org.breakout.update;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BreakoutRelease {
	private Integer id;
	private String tagName;
	private String name;
	private Date publishedAt;
	private List<BreakoutReleaseAsset> assets;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}

	public List<BreakoutReleaseAsset> getAssets() {
		return assets;
	}

	public void setAssets(List<BreakoutReleaseAsset> assets) {
		this.assets = assets;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assets, id, name, publishedAt, tagName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BreakoutRelease other = (BreakoutRelease) obj;
		return Objects.equals(assets, other.assets)
			&& Objects.equals(id, other.id)
			&& Objects.equals(name, other.name)
			&& Objects.equals(publishedAt, other.publishedAt)
			&& Objects.equals(tagName, other.tagName);
	}

	@Override
	public String toString() {
		return "BreakoutRelease [id="
			+ id
			+ ", tagName="
			+ tagName
			+ ", name="
			+ name
			+ ", publishedAt="
			+ publishedAt
			+ ", assets="
			+ assets
			+ "]";
	}
}
