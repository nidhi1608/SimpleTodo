package codepath.apps.simpletodo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStore {	
	private static final String PREFS_NAME = "SimpleTodoPrefs";
	private static final String ITEMS_KEY = "Items";
	private static final String ITEM_PREFIX = "Item_";
	
	public static ArrayList<TodoElement> getAllItems(final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
		final Set<String> ids = preferences.getStringSet(ITEMS_KEY, new HashSet<String>());
		final ArrayList<TodoElement> items = new ArrayList<TodoElement>();
		final HashSet<String> confirmedIds = new HashSet<String>();
		for (final String id : ids) {
			final TodoElement item = getItem(context, UUID.fromString(id));
			if (item != null) {
				items.add(item);
				confirmedIds.add(id);
			}
		}
		preferences.edit().putStringSet(ITEMS_KEY, confirmedIds).commit();
		return items;
	}
	
	public static void putAllItems(final Context context, final ArrayList<TodoElement> items) {
		final HashSet<String> ids = new HashSet<String>();
		for (final TodoElement item : items) {
			ids.add(item.getId().toString());
		}
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
		preferences.edit().putStringSet(ITEMS_KEY, ids).commit();
	}
	
	public static TodoElement getItem(final Context context, final UUID id) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
		final String json = preferences.getString(ITEM_PREFIX + id.toString(), null);
		if (json == null) return null;
		return TodoElement.fromJson(json);
	}
	
	public static void commitItem(final Context context, final TodoElement item) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
		preferences.edit().putString(ITEM_PREFIX + item.getId().toString(), item.toJson()).commit();
	}
}
