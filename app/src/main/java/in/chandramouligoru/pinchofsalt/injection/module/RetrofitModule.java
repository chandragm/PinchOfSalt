package in.chandramouligoru.pinchofsalt.injection.module;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import dagger.Module;
import dagger.Provides;
import in.chandramouligoru.pinchofsalt.BuildConfig;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module
public class RetrofitModule {

	private PinchOfSaltApplication mApplication;

	public RetrofitModule(PinchOfSaltApplication application) {
		mApplication = application;
	}

	@Provides
	JsonFactory provideJackson() {
		return new MappingJsonFactory();
	}

	@Provides
	RetrofitService provideRetrofitService(OkHttpClient okHttpClient) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BuildConfig.END_POINT)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//				.addConverterFactory(JacksonConverterFactory.create())
				.client(okHttpClient)
				.build();
		return retrofit.create(RetrofitService.class);
	}

	@Provides
	OkHttpClient provideOkHttpClient() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		return new OkHttpClient
				.Builder()
				.addInterceptor(httpLoggingInterceptor)
//				.addNetworkInterceptor(ADD_HEADER_INTERCEPTOR)
//				.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
				.cache(new Cache(mApplication.getCacheDir(), 1024 * 1024 * 10))
				.build();
	}

//	private static final Interceptor ADD_HEADER_INTERCEPTOR = chain -> {
//		Request request = chain.request().newBuilder()
//				.addHeader("Content-Type", "text/plain; charset=utf-8")
//				.addHeader("Accept-Encoding", "gzip")
//				.addHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6").build();
//		return chain.proceed(request);
//	};

	private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
		Response originalResponse = chain.proceed(chain.request());
		return originalResponse.newBuilder()
//				.header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", 120, 0))
				.header("Content-Type", "text/plain; charset=utf-8")
				.build();
	};

	@Provides
	NetworkConnectionUtils provideNetworkConnectionUtils() {
		return new NetworkConnectionUtils(mApplication);
	}
}
