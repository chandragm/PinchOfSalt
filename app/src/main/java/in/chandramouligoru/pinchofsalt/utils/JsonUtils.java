package in.chandramouligoru.pinchofsalt.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import in.chandramouligoru.pinchofsalt.response.JsonResponse;

public class JsonUtils {
	private static final String TAG = "JsonUtils";
	private static ObjectMapper mapper = new ObjectMapper();

	public static void processJSONObject(JsonParser parser)
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
			Log.e(TAG, "Field: " + mapper.readValue(parser, JsonResponse.class));
			token = parser.nextToken();
			Log.e(TAG, " " + token);
			processJSONValue(token, parser);
		}
	}

	public static void processJSONArray(JsonParser parser)
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

	private static void processJSONValue(JsonToken token, JsonParser parser)
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


	public static void parseJson(JsonParser parser) throws IOException {
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
