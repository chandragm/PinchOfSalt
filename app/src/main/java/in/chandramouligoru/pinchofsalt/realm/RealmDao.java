package in.chandramouligoru.pinchofsalt.realm;

import com.google.common.eventbus.EventBus;

import in.chandramouligoru.pinchofsalt.events.ItemAddedEvent;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import io.realm.Realm;

public class RealmDao {

	private EventBus eventBus;
	private Realm realm;

	public RealmDao(EventBus eventBus) {
		this.eventBus = eventBus;

	}

	public void addItem(JsonResponse jsonResponse) {
		realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		realm.copyToRealm(jsonResponse);
		realm.commitTransaction();
		eventBus.post(new ItemAddedEvent(jsonResponse));
	}

//	private JsonResponse makeACopy(JsonResponse jsonResponse) {
//		JsonResponse response = new JsonResponse();
//		response.setTitle(jsonResponse.getTitle());
//		response.setImage(jsonResponse.getImage());
//		response.setDescription(jsonResponse.getDescription());
//
//		return response;
//	}
}
