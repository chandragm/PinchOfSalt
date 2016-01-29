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
		realm = Realm.getDefaultInstance();

	}

	public void addItem(JsonResponse jsonResponse) {
		realm.executeTransaction(realm1 -> realm1.copyToRealm(jsonResponse));
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
