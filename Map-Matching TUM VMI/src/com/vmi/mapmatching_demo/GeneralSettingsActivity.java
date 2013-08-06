package com.vmi.mapmatching_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class GeneralSettingsActivity extends Activity implements OnItemSelectedListener {

	//A skeleton-class for additional settings (tileset, sample rate and range-query radius chooser)
	String DEBUG = "debug";
	Spinner spinner;
	EditText setNewSampleRateBox;
	EditText setRangeQueryRadiusBox;
	Button applyNewSettingsButton;
	String RangeValue = "250";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		//final EditText editAddress = (EditText) findViewById(R.id.edit_address);
		spinner = (Spinner) findViewById(R.id.tileset_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.tileset_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		setNewSampleRateBox = (EditText) findViewById(R.id.edit_sampling_rate);

		if(setRangeQueryRadiusBox==null)
			setRangeQueryRadiusBox = (EditText) findViewById(R.id.set_range_query_radius);
		setRangeQueryRadiusBox.setText(RangeValue);

		applyNewSettingsButton = (Button) findViewById(R.id.apply_general_settings);

		applyNewSettingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RangeValue = setRangeQueryRadiusBox.getText().toString();
				FetchSQL.setRangeQueryRadius(Integer.parseInt(RangeValue));
				finish();
			}
		});

	}

	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
}
