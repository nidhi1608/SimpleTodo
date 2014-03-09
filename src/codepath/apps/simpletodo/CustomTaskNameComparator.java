package codepath.apps.simpletodo;

import java.util.Comparator;

public class CustomTaskNameComparator implements Comparator<TodoElement> {

	@Override
	public int compare(TodoElement todoElement1, TodoElement todoElement2) {
		return todoElement1.getTaskName().compareTo(todoElement2.getTaskName());
	}

}
