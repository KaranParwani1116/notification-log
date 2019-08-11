package org.hcilab.projects.nlogx.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import org.hcilab.projects.nlogx.R;
import org.hcilab.projects.nlogx.misc.Const;
import org.hcilab.projects.nlogx.misc.DatabaseHelper;
import org.hcilab.projects.nlogx.misc.ExportTask;
import org.hcilab.projects.nlogx.service.NotificationHandler;

//Implemented SharedPreferenceListener
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG=MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		setstyle();
	}

	//changing to setted mode when app starts
	private void setstyle() {
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		String value=sharedPreferences.getString(getString(R.string.key),getString(R.string.light_theme_value));
		if(value.equals(getString(R.string.dark_theme_value)))
		{
			getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}
		else{
			getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_delete:
				confirm();
				return true;
			case R.id.menu_export:
				export();
				return true;

			case R.id.mode_dark:
				sendtodarkfragment();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void sendtodarkfragment() {
		Intent intent=new Intent(MainActivity.this,DarkActivity.class);
		startActivity(intent);
	}

	private void confirm() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
		builder.setTitle(R.string.dialog_delete_header);
		builder.setMessage(R.string.dialog_delete_text);
		builder.setNegativeButton(R.string.dialog_delete_no, (dialogInterface, i) -> {});
		builder.setPositiveButton(R.string.dialog_delete_yes, (dialogInterface, i) -> truncate());
		builder.show();
	}

	private void truncate() {
		try {
			DatabaseHelper dbHelper = new DatabaseHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED);
			db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED);
			db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED);
			db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED);
			Intent local = new Intent();
			local.setAction(NotificationHandler.BROADCAST);
			LocalBroadcastManager.getInstance(this).sendBroadcast(local);
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
	}

	private void export() {
		if(!ExportTask.exporting) {
			ExportTask exportTask = new ExportTask(this, findViewById(android.R.id.content));
			exportTask.execute();
		}
	}


	//Implemented When Shared Preferences changed
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       String value=sharedPreferences.getString(key,getString(R.string.light_theme_value));

		Log.d(TAG,value);

		if(value.equals(getString(R.string.dark_theme_value)))
		{
			Log.d(TAG,getString(R.string.settingNight));
			getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}
		else
		{
			Log.d(TAG,getString(R.string.settingDay));
			getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}

	}
}