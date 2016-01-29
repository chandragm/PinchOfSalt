package in.chandramouligoru.pinchofsalt.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import rx.Observable;

public interface RetrofitService {

	@Streaming
	@GET("/maclir/f715d78b49c3b4b3b77f/raw/8854ab2fe4cbe2a5919cea97d71b714ae5a4838d/{file}")
	Observable<ResponseBody> getJson(@Path("file") String file);
}
