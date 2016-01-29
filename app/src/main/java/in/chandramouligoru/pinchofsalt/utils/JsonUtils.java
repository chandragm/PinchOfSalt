package in.chandramouligoru.pinchofsalt.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.realm.RealmDao;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import rx.Subscriber;

public class JsonUtils {
	private static final String TAG = "JsonUtils";
	int count = 0;

	private ObjectMapper mapper = new ObjectMapper();
	private JsonFactory jsonFactory;
	private RealmDao realmDao;

	@Inject
	public JsonUtils(JsonFactory jsonFactory, RealmDao realmDao) {
		this.jsonFactory = jsonFactory;
		this.realmDao = realmDao;
	}

	private void processJSONObject(JsonParser parser, Subscriber subscriber)
			throws IOException {
		while (!parser.isClosed()) {
			JsonToken token = parser.nextToken();
			if (JsonToken.END_OBJECT.equals(token)) {
				//The end of the JSON object has been reached
				break;
			}
			if (!JsonToken.FIELD_NAME.equals(token)) {
				Log.e(TAG, "Error. Expected a field name");
				break;
			}
			count++;
			JsonNode node = parser.readValueAsTree();
			JsonResponse jsonResponse = new JsonResponse();
			jsonResponse.setTitle(node.get("title").textValue());
			jsonResponse.setDescription(node.get("description").textValue());
			jsonResponse.setImage(node.get("image").textValue());

			subscriber.onNext(jsonResponse);
			Log.e(TAG, " count = " + count);
			token = parser.nextToken();
			Log.e(TAG, " " + token);
			processJSONValue(token, parser, subscriber);
		}
	}

	private void processJSONArray(JsonParser parser, Subscriber subscriber)
			throws IOException {
		while (!parser.isClosed()) {
			JsonToken token = parser.nextToken();
			if (JsonToken.END_ARRAY.equals(token)) {
				// The end of the array has been reached
				break;
			}
			processJSONValue(token, parser, subscriber);
		}
	}

	private void processJSONValue(JsonToken token, JsonParser parser, Subscriber subscriber)
			throws IOException {
		if (JsonToken.START_OBJECT.equals(token)) {
			processJSONObject(parser, subscriber);
		} else if (JsonToken.START_ARRAY.equals(token)) {
			processJSONArray(parser, subscriber);
		} else {
			//End of parsing
			parser.close();
			subscriber.onCompleted();
		}
	}


	public void parseJson(InputStream inputStream, Subscriber subscriber) throws IOException {
		if (inputStream == null)
			return;
		JsonParser parser = jsonFactory.createParser(inputStream);

		while (!parser.isClosed()) {
			// read the next element
			JsonToken token = parser.nextToken();
			// if the call to nextToken returns null, the end of the file has been reached
			if (token == null)
				break;

			// Process the element
			processJSONValue(token, parser, subscriber);
		}
	}
}
