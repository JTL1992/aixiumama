package com.harmazing.aixiumama.model;

import java.util.List;

public class SearchResultUser {
	public int count;
	public String next;
	public String previous;
	public List<SearchResultUserResult> results;
	
	public class SearchResultUserResult {
		public String name;
		public String image_small;
		public List<Babies> babies;
        public int auth_user;
		@Override
		public String toString() {
			return "SearchResultUserResult [name=" + name + ", image_small=" + image_small + ", babies=" + babies + "]";
		}
	}
	
	public class Babies {
		public String birthday;

		@Override
		public String toString() {
			return "Babies [birthday=" + birthday + "]";
		}
	}

	@Override
	public String toString() {
		return "SearchResultUser [count=" + count + ", next=" + next + ", previous=" + previous + ", results=" + results + "]";
	}

}
