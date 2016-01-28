package in.chandramouligoru.pinchofsalt.injection.component;

import dagger.Component;
import in.chandramouligoru.pinchofsalt.injection.module.RetrofitModule;
import in.chandramouligoru.pinchofsalt.view.activity.BaseActivity;
import in.chandramouligoru.pinchofsalt.view.activity.ItemListActivity;
import in.chandramouligoru.pinchofsalt.view.activity.SplashActivity;

@Component(modules = RetrofitModule.class)
public interface AppComponent {
	void initialize(ItemListActivity itemListActivity);

	void initialize(BaseActivity baseActivity);

	void initialize(SplashActivity splashActivity);
}
