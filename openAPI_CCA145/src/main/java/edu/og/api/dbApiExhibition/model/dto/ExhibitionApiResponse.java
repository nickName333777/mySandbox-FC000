package edu.og.api.dbApiExhibition.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



//API Response Wrapper DTO
@Getter
@Setter
@ToString
public class ExhibitionApiResponse {

	private Response response;

	@Getter
	@Setter
	@ToString
	public static class Response {
		private Header header;
		private Body body;

		@Getter
		@Setter
		@ToString
		public static class Header {
			private String resultCode;
			private String resultMsg;
		}

		@Getter
		@Setter
		@ToString
		public static class Body {
			private Items items;
			private int numOfRows;
			private int pageNo;
			private int totalCount;

			@Getter
			@Setter
			@ToString
			public static class Items {
				private List<ExhibitionApi> item;
			}
		}
	}

}

