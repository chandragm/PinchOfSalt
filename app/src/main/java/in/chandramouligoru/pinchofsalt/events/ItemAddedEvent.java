package in.chandramouligoru.pinchofsalt.events;

import in.chandramouligoru.pinchofsalt.response.JsonResponse;

public class ItemAddedEvent {

	private JsonResponse jsonResponse;

	public ItemAddedEvent(JsonResponse jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public JsonResponse getJsonResponse() {
		return jsonResponse;
	}
}
