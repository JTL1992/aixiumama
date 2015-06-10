package com.harmazing.aixiumama.model;

import java.util.List;

public class RightUserFollowLabel {
	public String count;
	public List<RightUserFollowLabelResults> results;
	
	public class RightUserFollowLabelResults {
		public String create_date;
		public String follow;
		public FollowDetail follow_detail;
		
		public String id;
		public String user;

        @Override
        public String toString() {
            return "RightUserFollowLabelResults [create_date=" + create_date + ", follow=" + follow + ", follow_detail=" + follow_detail + ", id=" + id + ", user=" + user + "]";
        }
    }
	
	public class FollowDetail {
		public boolean certificate;
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
            return "FollowDetail [certificate=" + certificate + ", create_date=" + create_date + ", description=" + description + ", follow_count=" + follow_count + ", id=" + id + ", image=" + image + ", image_small=" + image_small + ", name=" + name + ", usage_count=" + usage_count + "]";
        }
	}

    @Override
    public String toString() {
        return "RightUserFollowLabel [count=" + count + ", results=" + results + "]";
    }
}
