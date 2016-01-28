package in.chandramouligoru.pinchofsalt.response;

public class JsonResponse {
	public String title;
	public String description;

	@Override
	public String toString() {
		return "JsonResponse{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", image='" + image + '\'' +
				'}';
	}

	public String image;
}
