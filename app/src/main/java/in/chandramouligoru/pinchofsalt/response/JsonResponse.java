package in.chandramouligoru.pinchofsalt.response;

import io.realm.RealmObject;

public class JsonResponse extends RealmObject {
	private String title;
	private String description;
	private String image;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
