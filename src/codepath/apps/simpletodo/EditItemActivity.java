package codepath.apps.simpletodo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class EditItemActivity extends Activity {
	private EditText etTaskName;
	private EditText etDueDate;
	Calendar mCalendar = Calendar.getInstance();
	private Date mDate;
	TodoElement mItem;
	
	private final SimpleDateFormat DF = new SimpleDateFormat("EEE, dd.MM.yyyy", java.util.Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final String id = getIntent().getStringExtra("itemId");
		final TodoElement item = DataStore.getItem(this, UUID.fromString(id));
		mItem = item;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);
		etTaskName = (EditText) findViewById(R.id.etTaskName);
		etDueDate = (EditText) findViewById(R.id.etDueDate);
		etTaskName.setText(item.getTaskName());
		etTaskName.setSelection(item.getTaskName().length());
		if (item.getDueDate() > 0) {
			final Date dueDate = new Date(item.getDueDate());
			etDueDate.setText(DF.format(dueDate));
		}
		etDueDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(EditItemActivity.this, date, mCalendar
						.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
						mCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	}

	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}

	};

	private void updateLabel() {
		final Date date = mCalendar.getTime();
		mDate = date;
		etDueDate.setText(DF.format(date));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		commitChanges();
		super.onPause();
	}

	public void commitChanges() {
		etTaskName = (EditText) findViewById(R.id.etTaskName);
		mItem.setTaskName(etTaskName.getText().toString().trim());
		if (mDate != null) {
			mItem.setDueDate(mDate.getTime());
		}
		DataStore.commitItem(this, mItem);
	}
}
