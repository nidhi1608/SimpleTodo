package codepath.apps.simpletodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class TodoActivity extends Activity {
	private final int REQUEST_CODE = 20;
	private int pos;
	CustomTodoListAdapter itemsAdapter;
	ExpandableListView lvItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		DataStore.getAllItems(this);
		lvItems = (ExpandableListView) findViewById(R.id.lvItems);
		lvItems.setItemsCanFocus(false);
		lvItems.setLongClickable(true);
		itemsAdapter = new CustomTodoListAdapter(this);
		itemsAdapter.sortByDueDate();
		itemsAdapter.setHost(lvItems);
		lvItems.setAdapter(itemsAdapter);
		setupListViewListener(); 
		setupEditListViewListener();
	}

	private void setupListViewListener() {
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
					int groupPosition = ExpandableListView.getPackedPositionGroup(id);
					int childPosition = ExpandableListView.getPackedPositionChild(id);
		            removeItemFromList(groupPosition, childPosition);
		            return true;
		        }
		        return false;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lvItems.expandGroup(0);
	}

	protected void removeItemFromList(final int groupPosition, final int childPosition) {
		AlertDialog.Builder alert = new AlertDialog.Builder(TodoActivity.this);
		alert.setTitle("Delete");
		alert.setMessage("Do you want delete this item?");
		alert.setPositiveButton("YES", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				itemsAdapter.removeItem(groupPosition, childPosition);
			}
		});
		alert.setNegativeButton("CANCEL", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}

	private void setupEditListViewListener() {
		lvItems.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
				if(groupPosition == 0){
				Intent intent = new Intent(TodoActivity.this,
						EditItemActivity.class);
				intent.putExtra("itemId", ((TodoElement)itemsAdapter.getChild(groupPosition, childPosition)).getId().toString());
				pos = childPosition;
				startActivityForResult(intent, REQUEST_CODE);
				}
				return true;
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		itemsAdapter.editItem(pos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_actions, menu); 
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sortAlphabetically:
			itemsAdapter.sortAlphabetically();
			return true;
		case R.id.sortByDueDate:
			itemsAdapter.sortByDueDate();
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}

	public void addTodoItem(View v) {
		EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
		itemsAdapter.addItem(new TodoElement(etNewItem.getText().toString(), System.currentTimeMillis(), false));
		etNewItem.setText("");
	}
}
