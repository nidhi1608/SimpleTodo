package codepath.apps.simpletodo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CustomTodoListAdapter extends BaseExpandableListAdapter {
	private static final String TAG = "CustomTodoListAdapter";
	private final LayoutInflater mInflater;
	private final ArrayList<TodoElement> mListItems;
	private final ArrayList<TodoElement> mCompletedListItems;
	private ExpandableListView mListView;
	private final Activity mActivity;
	public static final SimpleDateFormat DF = new SimpleDateFormat("EEE, dd.MM.yyyy", java.util.Locale.getDefault());

	public int selectedIndex;
	private static final int ALTERNATE_COLOR = Color.parseColor("#fbfbfb");

	public CustomTodoListAdapter(final Activity activity) {
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ArrayList<TodoElement> todoElements = DataStore.getAllItems(activity);
		mListItems = new ArrayList<TodoElement>();
		mCompletedListItems = new ArrayList<TodoElement>();
		mActivity = activity;
		for (final TodoElement todoElement : todoElements) {
			if (todoElement.isComplete()) {
				mCompletedListItems.add(todoElement);
			} else {
				mListItems.add(todoElement);
			}
		}
	}

	public void setHost(final ExpandableListView listView) {
		mListView = listView;
	}
	
	public void saveAllItems() {
		DataStore.putAllItems(mActivity, getAllItems());
	}
	
	public ArrayList<TodoElement> getAllItems() {
		final ArrayList<TodoElement> items = new ArrayList<TodoElement>(mListItems);
		items.addAll(mCompletedListItems);
		return items;
	}
	
	public ArrayList<TodoElement> getIncompleteItems() {
		return mListItems;
	}
	
	public ArrayList<TodoElement> getCompletedItems() {
		return mCompletedListItems;
	}

	public void addItem(final TodoElement todoElement) {
		if (todoElement.getTaskName() == null)
			return;
		final String taskName = todoElement.getTaskName().trim();
		if (taskName.length() == 0)
			return;
		todoElement.setTaskName(taskName);
		DataStore.commitItem(mActivity, todoElement);
		mListItems.add(todoElement);
		
		saveAllItems();
		sortByDueDate();
		notifyDataSetChanged();		
		mListView.expandGroup(0, true);
		mListView.setSelection(mListItems.indexOf(todoElement));
	}

	public void removeItem(int groupPosition, int childPosition) {		
		final ArrayList<TodoElement> src = groupPosition == 0 ? mListItems : mCompletedListItems;
		src.remove(childPosition);
		saveAllItems();
		notifyDataSetChanged();
	}
	
	public void sortAlphabetically(){
		Collections.sort(mListItems, new CustomTaskNameComparator());
		notifyDataSetChanged();
	}
	
	public void sortByDueDate(){
		Collections.sort(mListItems, new CustomDueDateComparator());
		notifyDataSetChanged(); 
	}
	
	public void editItem(final int position) {
		final TodoElement currentElement = mListItems.get(position);
		final TodoElement newElement = DataStore.getItem(mActivity, currentElement.getId());
		mListItems.remove(position);
		mListItems.add(position, newElement);
		sortByDueDate();
		notifyDataSetChanged();
	}
	
	final OnCheckedChangeListener mCheckItemCompleteChangeList = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			final TodoElement item = (TodoElement) buttonView.getTag();
			item.setCompleted(isChecked);
			final ArrayList<TodoElement> src = isChecked ? mListItems : mCompletedListItems;
			final ArrayList<TodoElement> dst = isChecked ? mCompletedListItems : mListItems;
			src.remove(item);
			dst.add(item);
			sortByDueDate();
			notifyDataSetChanged();
			DataStore.commitItem(mActivity, item);
			mListView.expandGroup(1, true);
		}
	};

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		final ArrayList<TodoElement> listItems = groupPosition == 0 ? mListItems : mCompletedListItems;
		return listItems.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		final ArrayList<TodoElement> listItems = groupPosition == 0 ? mListItems : mCompletedListItems;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_info, null);
			final ViewHolder.Item holder = new ViewHolder.Item();
			holder.tvTaskName = (TextView) convertView.findViewById(R.id.tvTaskName);
			holder.tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
			holder.chkComplete = (CheckBox) convertView
					.findViewById(R.id.chkItem);
			convertView.setTag(holder);
		}
		if (childPosition % 2 == 1) {
			convertView.setBackgroundColor(ALTERNATE_COLOR);  
		} else {
			convertView.setBackgroundColor(Color.WHITE);  
		}
		final ViewHolder.Item holder = (ViewHolder.Item) convertView.getTag();
		holder.tvTaskName.setText((listItems.get(childPosition).getTaskName()));
		holder.tvTaskName.setPaintFlags( holder.tvTaskName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
		if(groupPosition == 1){			
			holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		holder.tvTaskName.setTextColor(Color.BLACK);
		holder.tvDueDate.setTextColor(Color.BLACK);		
		holder.tvDueDate.setText(DF.format(listItems.get(childPosition).getDueDate()));
		formatDate(listItems, childPosition, holder);		
		holder.chkComplete = (CheckBox) convertView.findViewById(R.id.chkItem);
		holder.chkComplete.setOnCheckedChangeListener(null);
		holder.chkComplete.setChecked(groupPosition == 1);
		holder.chkComplete
				.setOnCheckedChangeListener(mCheckItemCompleteChangeList);
		holder.chkComplete.setTag(listItems.get(childPosition));		
		return convertView;
	}
	
	private void formatDate(ArrayList<TodoElement> listItems, int childPosition, ViewHolder.Item holder) {
		String currentDate = DF.format(new Date());
		try{
			if(DF.parse(currentDate).after(DF.parse(DF.format(listItems.get(childPosition).getDueDate())))){
				holder.tvTaskName.setTextColor(Color.RED);
				holder.tvDueDate.setTextColor(Color.RED);				
			}
			else if(DF.parse(currentDate).equals(DF.parse(DF.format(listItems.get(childPosition).getDueDate())))){
				holder.tvDueDate.setText("Due Today");
			}
			Calendar c1 = Calendar.getInstance(); 
			Calendar c2 = Calendar.getInstance();
			c2.setTime(new Date(listItems.get(childPosition).getDueDate()));
			if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
				if(c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) == 1){
					holder.tvDueDate.setText("Due Yesterday");
				}
				else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 1){
					holder.tvDueDate.setText("Due Tomorrow");
				}
			}		
		}
		catch(ParseException e)
		{
			Log.d(TAG, e.toString());
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupPosition == 0 ? mListItems.size() : mCompletedListItems
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition == 0 ? "INCOMPLETE" : "COMPLETE";
	}

	@Override
	public int getGroupCount() {
		return 2;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_info, null);
			final ViewHolder.Group holder = new ViewHolder.Group();
			holder.groupItem = (TextView) convertView
					.findViewById(R.id.groupItem);
			holder.groupItemCount = (TextView) convertView
					.findViewById(R.id.groupItemCount);
			convertView.setTag(holder);
		}
		final ArrayList<TodoElement> listItems = groupPosition == 0 ? mListItems : mCompletedListItems;
		final ViewHolder.Group holder = (ViewHolder.Group) convertView.getTag();
		holder.groupItem.setText((CharSequence) getGroup(groupPosition));
		holder.groupItemCount.setText(" (" + listItems.size() + ")");
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
