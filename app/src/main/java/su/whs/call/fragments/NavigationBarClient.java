package su.whs.call.fragments;

import android.view.View;

public interface NavigationBarClient {
	public View getCustomView();
	public int  getCustomHomeIconId();
	public int  getCustomInfoIconId();
	public boolean onHomeIconClick();
	public void onInfoIconClick();
	public String getTitle();
	public boolean isCustomViewVisible();
	public boolean isHomeIconVisible();
}
