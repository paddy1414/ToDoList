
package pdesigns.com.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import pdesigns.com.todolist.DbStuffs.TaskDbHelper;

public class ReminderEditActivity extends Activity {

	// 
	// Dialog Constants
	//
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;

	// 
	// Date Format 
	//
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "kk:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

	private EditText mTitleText;
	private EditText mBodyText;
	private Button mDateButton;
	private Button mTimeButton;
	private Switch mySwitch;
	private Button mConfirmButton;
	private Button mCancel;
	private Button mResetButton;
	private Long mRowId;
	private TaskDbHelper mDbHelper;
	private Calendar mCalendar;
	ProgressDialog myDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new TaskDbHelper(this);

		setContentView(R.layout.reminder_edit);

		mCalendar = Calendar.getInstance();
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mDateButton = (Button) findViewById(R.id.reminder_date);
		mTimeButton = (Button) findViewById(R.id.reminder_time);
		mySwitch = (Switch) findViewById(R.id.switch1);
		mConfirmButton = (Button) findViewById(R.id.confirm);
		mCancel = (Button) findViewById(R.id.cancel_action);
		mResetButton = (Button) findViewById(R.id.reset);


		mRowId = savedInstanceState != null ? savedInstanceState.getLong(TaskDbHelper._ID)
				: null;

		registerButtonListenersAndSetDefaultText();

		mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				if (isChecked) {
					mySwitch.setText("Done");
				} else {
					mySwitch.setText("Not Done");
				}
			}
		});
	}

	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(TaskDbHelper._ID)
					: null;

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		setRowIdFromIntent();
		populateFields();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DATE_PICKER_DIALOG:
				return showDatePicker();
			case TIME_PICKER_DIALOG:
				return showTimePicker();
		}
		return super.onCreateDialog(id);
	}

	private DatePickerDialog showDatePicker() {


		DatePickerDialog datePicker = new DatePickerDialog(ReminderEditActivity.this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDateButtonText();
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		return datePicker;
	}

	private TimePickerDialog showTimePicker() {

		TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute);
				updateTimeButtonText();
			}
		}, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

		return timePicker;
	}

	private void registerButtonListenersAndSetDefaultText() {

		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});


		mTimeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG);
			}
		});

		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

					saveState();
					setResult(RESULT_OK);


				}


		});

		mCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
				finish();
			}

		});

		mResetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mTitleText.setText("");
				mBodyText.setText("");

				Toast.makeText(ReminderEditActivity.this, getString(R.string.task_reset), Toast.LENGTH_SHORT).show();

			}

		});

		updateDateButtonText();
		updateTimeButtonText();
	}

	private void populateFields()  {

		// Only populate the text boxes and change the calendar date
		// if the row is not null from the database.
		if (mRowId != null) {
			Cursor reminder = mDbHelper.fetchReminder(mRowId);
			startManagingCursor(reminder);
			mTitleText.setText(reminder.getString(
					reminder.getColumnIndexOrThrow(TaskDbHelper.TASK)));
			mBodyText.setText(reminder.getString(
					reminder.getColumnIndexOrThrow(TaskDbHelper.KEY_BODY)));


			if (reminder.getString(reminder.getColumnIndexOrThrow(TaskDbHelper.DONE)).equals("Done")) {
				mySwitch.setChecked(true);
			} else {
				mySwitch.setChecked(false);
			}





			// Get the date from the database and format it for our use.
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
			Date date = null;
			try {
				String dateString = reminder.getString(reminder.getColumnIndexOrThrow(TaskDbHelper.KEY_DATE_TIME));
				date = dateTimeFormat.parse(dateString);
				mCalendar.setTime(date);
			} catch (ParseException e) {
				Log.e("ReminderEditActivity", e.getMessage(), e);
			}
		} else {
			// This is a new task - add defaults from preferences if set.
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String defaultTitleKey = getString(R.string.pref_task_title_key);
			String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

			String defaultTitle = prefs.getString(defaultTitleKey, null);
			String defaultTime = prefs.getString(defaultTimeKey, null);


			if(defaultTitle != null)
				mTitleText.setText(defaultTitle);
			mTitleText.setText("");
			mBodyText.setText("");

			if(defaultTime != null)
				mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));


		}

		updateDateButtonText();
		updateTimeButtonText();

	}

	private void updateTimeButtonText() {
		// Set the time button text based upon the value from the database
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		String timeForButton = timeFormat.format(mCalendar.getTime());
		mTimeButton.setText(timeForButton);
	}

	private void updateDateButtonText() {
		// Set the date button text based upon the value from the database 
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String dateForButton = dateFormat.format(mCalendar.getTime());
		mDateButton.setText(dateForButton);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(TaskDbHelper._ID, mRowId);
	}



	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String doneOrWhat = "Not Done";
		if (mySwitch.isChecked()) {
			doneOrWhat = "Done";
		} else {
			doneOrWhat ="Not Done";
		}
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

		if (mRowId == null) {
			if (title.equals("")) {

				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setTitle("Attention");
				builder1.setMessage("Please enter a title");
				builder1.setCancelable(true);
				builder1.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();
			} else {
				long id = mDbHelper.createReminder(title, body, reminderDateTime);
				if (id > 0) {
					mRowId = id;
				}
				Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
				finish();
			}

		} else {
			if (title.equals("")) {

				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setTitle("Attention");
				builder1.setMessage("Please enter a title");
				builder1.setCancelable(false);
				builder1.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();

			}
			else {
				mDbHelper.updateReminder(mRowId, title, body, reminderDateTime, doneOrWhat);
				Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
				finish();
			}

		}

	}

}
