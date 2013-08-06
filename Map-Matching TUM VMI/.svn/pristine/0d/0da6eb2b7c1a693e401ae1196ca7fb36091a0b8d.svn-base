package com.vmi.mapmatching_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SQLSettingsActivity extends Activity {

	static SQLConfiguration newSQLConfiguration = null;
	static boolean settingsSet = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.psql_settings);

		final EditText editAddress = (EditText) findViewById(R.id.edit_address);
		final EditText editDatabaseName = (EditText) findViewById(R.id.edit_database_name);
		final EditText editPassword = (EditText) findViewById(R.id.edit_password);
		final EditText editUsername = (EditText) findViewById(R.id.edit_username);

		final Button ApplyNewSettingsButton = (Button) findViewById(R.id.apply_new_settings);

		ApplyNewSettingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String newAddress = editAddress.getText().toString();
				String newDBName = editDatabaseName.getText().toString();
				String newUsername = editUsername.getText().toString();
				String newPassword = editPassword.getText().toString();

				setNewSQLSettings(new SQLConfiguration(newDBName, newAddress, newUsername, newPassword));
				finish();
			}
		});
	}

	//sets the initial SQL configuration data (VMI-TUM server)
	public static void initSQLConfig(){
		String newAddress = "129.187.223.154";
		String newDBName = "4326gis";
		String newUsername = "ermin";
		String newPassword = "";

		setNewSQLSettings(new SQLConfiguration(newDBName, newAddress, newUsername, newPassword));
	}

	public static void setNewSQLSettings(SQLConfiguration settings){
		newSQLConfiguration = settings;
		settingsSet = true;
	}

	public static boolean areSettingsSet(){
		return settingsSet;
	}

	public static SQLConfiguration getSQLSettings(){
		return newSQLConfiguration;
	}
}
