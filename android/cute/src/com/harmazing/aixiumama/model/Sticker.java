package com.harmazing.aixiumama.model;

import java.util.List;

public class Sticker {
	public String count;
	public List<StickerResults> results;
	
	public class StickerResults {
		public String banner_imager;
		public String create_date;
		public String description;
		public String icon;
		public String id;
		public String image;
		public String name;
		public String remain_count;
		public String total_count;
		public String usage_count;
		@Override
		public String toString() {
			return "StickerResults [banner_imager=" + banner_imager + ", create_date=" + create_date + ", description=" + description + ", icon=" + icon + ", id=" + id + ", image=" + image + ", name=" + name + ", remain_count=" + remain_count + ", total_count=" + total_count + ", usage_count=" + usage_count + "]";
		}
		
	}

	@Override
	public String toString() {
		return "Sticker [count=" + count + ", results=" + results + "]";
	}
	
}
