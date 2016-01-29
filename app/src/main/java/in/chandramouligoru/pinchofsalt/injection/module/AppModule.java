package in.chandramouligoru.pinchofsalt.injection.module;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.google.common.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import in.chandramouligoru.pinchofsalt.BuildConfig;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.realm.RealmDao;
import in.chandramouligoru.pinchofsalt.utils.JsonUtils;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module
public class AppModule {

	private PinchOfSaltApplication mApplication;

	public AppModule(PinchOfSaltApplication application) {
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
				.cache(new Cache(mApplication.getCacheDir(), 1024 * 1024 * 10))
				.build();
	}

	@Provides
	NetworkConnectionUtils provideNetworkConnectionUtils() {
		return new NetworkConnectionUtils(mApplication);
	}

	@Provides
	EventBus provideEventBus() {
		return new EventBus();
	}

	@Provides
	RealmDao provideRealmDao(EventBus eventBus) {
		return new RealmDao(eventBus);
	}

	@Provides
	JsonUtils provideJsonUtils(JsonFactory jsonFactory, RealmDao realmDao) {
		return new JsonUtils(jsonFactory, realmDao);
	}
}
