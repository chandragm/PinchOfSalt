package in.chandramouligoru.pinchofsalt.app;

import android.app.Application;

import in.chandramouligoru.pinchofsalt.injection.component.AppComponent;
import in.chandramouligoru.pinchofsalt.injection.module.AppModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PinchOfSaltApplication extends Application {

	private AppComponent mAppComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppComponent = in.chandramouligoru.pinchofsalt.injection.component.DaggerAppComponent
				.builder().appModule(new AppModule(this)).build();

		RealmConfiguration config = new RealmConfiguration.Builder(this).build();
		Realm.setDefaultConfiguration(config);
	}

	public AppComponent getAppComponent() {
		return mAppComponent;
	}
}
