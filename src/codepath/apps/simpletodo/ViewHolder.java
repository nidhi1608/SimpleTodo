package codepath.apps.simpletodo;

import android.widget.CheckBox;
import android.widget.TextView;

public class ViewHolder {
	public static class Item {
		public TextView tvTaskName;
		public TextView tvDueDate;
		public CheckBox chkComplete;
	}
	
	public static class Group {
		public TextView groupItem;
		public TextView groupItemCount;
	}
}
