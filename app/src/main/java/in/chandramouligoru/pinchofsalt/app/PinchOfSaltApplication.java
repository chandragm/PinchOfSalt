package in.chandramouligoru.pinchofsalt.app;

import android.app.Application;

import in.chandramouligoru.pinchofsalt.injection.component.AppComponent;
import in.chandramouligoru.pinchofsalt.injection.module.RetrofitModule;

public class PinchOfSaltApplication extends Application {

	private AppComponent mAppComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppComponent = in.chandramouligoru.pinchofsalt.injection.component.DaggerAppComponent
				.builder().retrofitModule(new RetrofitModule(this)).build();
	}

	public AppComponent getAppComponent() {
		return mAppComponent;
	}
}
