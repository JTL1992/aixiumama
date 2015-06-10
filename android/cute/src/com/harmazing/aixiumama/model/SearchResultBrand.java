package com.harmazing.aixiumama.model;

import java.util.List;

public class SearchResultBrand {
	public int count;
	public List<SearchResultBrandResult> results;
	
	public class SearchResultBrandResult {
		public String certificate;
		public String create_date;
		public String description;
		public String follow_count;
		public String id;
		public String image;
		public String image_small;
		public String name;
		public String usage_count;
		@Override
		public String toString() {
			return "SearchResultBrandResult [certificate=" + certificate + ", create_date=" + create_date + ", description=" + description + ", follow_count=" + follow_count + ", id=" + id + ", image=" + image + ", image_small=" + image_small + ", name=" + name + ", usage_count=" + usage_count + "]";
		}
		
	}

	@Override
	public String toString() {
		return "SearchResultBrand [count=" + count + ", results=" + results + "]";
	}
	
}
