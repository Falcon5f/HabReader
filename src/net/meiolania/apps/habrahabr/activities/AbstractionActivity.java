/*
Copyright 2012 Andrey Zaytsev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package net.meiolania.apps.habrahabr.activities;

import net.meiolania.apps.habrahabr.Preferences;
import net.meiolania.apps.habrahabr.R;
import net.meiolania.apps.habrahabr.slidemenu.MenuFragment;
import net.meiolania.apps.habrahabr.utils.ConnectionUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public abstract class AbstractionActivity extends SlidingFragmentActivity
{
	protected SlidingMenu slidingMenu;
	protected PowerManager.WakeLock wakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Preferences preferences = Preferences.getInstance(this);
		if(preferences.getFullScreen())
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if(preferences.getKeepScreen())
		{
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wakeLock");
			wakeLock.acquire();
		}

		if(!ConnectionUtils.isConnected(this))
		{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.error);
			dialog.setMessage(getString(R.string.no_connection));
			dialog.setPositiveButton(R.string.close, getConnectionDialogListener());
			dialog.setCancelable(false);
			dialog.show();
		}
		
		/*
		 * Workaround for "java.lang.IllegalStateException: Both setBehindContentView must be called in onCreate in addition to setContentView."
		 */
		setContentView(R.layout.empty_for_slidemenu);
		
		showSlideMenu();
		
		getSupportActionBar().setHomeButtonEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	protected void onResume()
	{
		Preferences preferences = Preferences.getInstance(this);
		if(preferences.getFullScreen())
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		if(wakeLock != null)
			wakeLock.release();
		
		super.onPause();
	}
	
	@Override
	public void onBackPressed()
	{
		if(slidingMenu.isMenuShowing())
			toggle();
		else
			super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				toggle();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void showSlideMenu()
	{
		setBehindContentView(R.layout.slide_menu);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.slide_menu, new MenuFragment()).commit();
		
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setMenu(R.layout.slide_menu);
	}

	protected abstract OnClickListener getConnectionDialogListener();

}