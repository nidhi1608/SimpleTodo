package codepath.apps.simpletodo;

import java.util.Comparator;
import java.util.Date;

public class CustomDueDateComparator implements Comparator<TodoElement> {

	@Override
	public int compare(TodoElement todoElement1, TodoElement todoElement2) {
		return new Date(todoElement1.getDueDate()).compareTo(new Date(todoElement2.getDueDate()));
	}

}
