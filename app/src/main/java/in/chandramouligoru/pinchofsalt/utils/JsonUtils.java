package in.chandramouligoru.pinchofsalt.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.realm.RealmDao;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;

public class JsonUtils {
	private static final String TAG = "JsonUtils";

	private ObjectMapper mapper = new ObjectMapper();
	private JsonFactory jsonFactory;
	private RealmDao realmDao;

	@Inject
	public JsonUtils(JsonFactory jsonFactory, RealmDao realmDao) {
		this.jsonFactory = jsonFactory;
		this.realmDao = realmDao;
	}

	private void processJSONObject(JsonParser parser)
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
			JsonResponse item = mapper.readValue(parser, JsonResponse.class);
			realmDao.addItem(item);
			token = parser.nextToken();
			Log.e(TAG, " " + token);
			processJSONValue(token, parser);
		}
	}

	private void processJSONArray(JsonParser parser)
			throws IOException {
		while (!parser.isClosed()) {
			JsonToken token = parser.nextToken();
			if (JsonToken.END_ARRAY.equals(token)) {
				// The end of the array has been reached
				break;
			}
			processJSONValue(token, parser);
		}
	}

	private void processJSONValue(JsonToken token, JsonParser parser)
			throws IOException {
		if (JsonToken.START_OBJECT.equals(token)) {
			processJSONObject(parser);
		} else if (JsonToken.START_ARRAY.equals(token)) {
			processJSONArray(parser);
		} else {
			//End of parsing
			parser.close();
		}
	}


	public void parseJson(InputStream inputStream) throws IOException {
		if(inputStream == null)
			return;
		JsonParser parser = jsonFactory.createParser(inputStream);

		while (!parser.isClosed()) {
			// read the next element
			JsonToken token = parser.nextToken();
			// if the call to nextToken returns null, the end of the file has been reached
			if (token == null)
				break;

			// Process the element
			processJSONValue(token, parser);
		}
	}
}
