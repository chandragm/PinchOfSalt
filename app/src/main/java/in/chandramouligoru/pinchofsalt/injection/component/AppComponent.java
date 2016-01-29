package in.chandramouligoru.pinchofsalt.injection.component;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.eventbus.EventBus;

import dagger.Component;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.injection.module.AppModule;
import in.chandramouligoru.pinchofsalt.utils.JsonUtils;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import in.chandramouligoru.pinchofsalt.view.activity.BaseActivity;
import in.chandramouligoru.pinchofsalt.view.activity.ItemListActivity;
import in.chandramouligoru.pinchofsalt.view.activity.SplashActivity;

@Component(modules = AppModule.class)
public interface AppComponent {
	void initialize(ItemListActivity itemListActivity);

	void initialize(BaseActivity baseActivity);

	void initialize(SplashActivity splashActivity);

	RetrofitService getRetrofitService();

	NetworkConnectionUtils getNetworkConnectionUtils();

	JsonFactory getJsonFactory();

	EventBus getEventBus();

	JsonUtils getJsonUtils();
}
