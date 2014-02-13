package codepath.apps.simpletodo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends Activity {
	private EditText etEditItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	String itemToEdit = getIntent().getStringExtra("item_to_edit");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(itemToEdit);
        etEditItem.setSelection(itemToEdit.length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_item, menu);
        return true;
    }
    
    public void editTodoItem(View v) {
    	etEditItem = (EditText) findViewById(R.id.etEditItem);
    	Intent data = new Intent();
    	data.putExtra("edited_item", etEditItem.getText().toString());
    	setResult(RESULT_OK, data);
    	finish();
	}
    
}
