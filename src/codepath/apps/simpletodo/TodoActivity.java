package codepath.apps.simpletodo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TodoActivity extends Activity {
	private final int REQUEST_CODE = 20;
	private int pos;
	ArrayList<String> items;
	ArrayAdapter<String> itemsAdapter;
	ListView lvItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		readItems();
		lvItems = (ListView)findViewById(R.id.lvItems);
		lvItems.setLongClickable(true);
		items = new ArrayList<String>();
		itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		lvItems.setAdapter(itemsAdapter);
		setupListViewListener();
		setupEditListViewListener();
	}
	
	private void setupListViewListener() {
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {        
				items.remove(pos);
				itemsAdapter.notifyDataSetInvalidated();
				saveItems();
                return true;
            }
		});
	}
	
	private void setupEditListViewListener() {
		lvItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(TodoActivity.this, EditItemActivity.class);
               intent.putExtra("item_to_edit", items.get(position));               
               pos = position;
               startActivityForResult(intent, REQUEST_CODE);
            }
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {			
			items.set(pos, data.getExtras().getString("edited_item"));
			itemsAdapter.notifyDataSetChanged();
			saveItems();
		}
	}
	
	private void readItems() {
		File filesDir = getFilesDir();
		File todoFile = new File(filesDir, "todo.txt");
		try {
			items = new ArrayList<String>(FileUtils.readLines(todoFile));
		} catch (IOException e) {
			items = new ArrayList<String>();
			e.printStackTrace();
		}
	}
	
	private void saveItems() {
		File filesDir = getFilesDir();
		File todoFile = new File(filesDir, "todo.txt");
		try {
			FileUtils.writeLines(todoFile, items);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}
	
	public void addTodoItem(View v) {
		EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
		itemsAdapter.add(etNewItem.getText().toString());
		etNewItem.setText("");
	}

}
