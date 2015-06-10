package com.harmazing.aixiumama.model;

import java.util.List;

public class RightUserlabels {
	public String count;
	public String next;
	public List<RightUserlabelsResults> results;
	
	public class RightUserlabelsResults {
		public String create_date;
		public String id;
		public String label;
		public LabelDetail label_detail;
		
		@Override
		public String toString() {
			return "RightUserlabelsResults [create_date=" + create_date + ", id=" + id + ", label=" + label + ", label_detail=" + label_detail + "]";
		}
	}
	
	public class LabelDetail {
		public boolean certificate;
		public String create_date;
		public String follow_count;
		public String id;
		public String image;
		public String image_small;
		public String name;
		public String usage_count;
		
		@Override
		public String toString() {
			return "LabelDetail [certificate=" + certificate + ", create_date=" + create_date + ", follow_count=" + follow_count + ", id=" + id + ", image=" + image + ", image_small=" + image_small + ", name=" + name + ", usage_count=" + usage_count + "]";
		}
	}

	@Override
	public String toString() {
		return "RightUserlabels [count=" + count + ", next=" + next + ", results=" + results + "]";
	}
}
