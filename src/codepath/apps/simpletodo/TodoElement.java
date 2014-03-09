package codepath.apps.simpletodo;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TodoElement {
	private static final String TAG = "TodoElement";
	private String mTaskName = null;	
	private boolean mCompleted = false;
	private long mDueDate = 0;
	private long mCreateDate = 0;
	private final UUID mId;
	
	public TodoElement(String item, long dueDate, boolean completed) {
		  this(UUID.randomUUID(), item, dueDate, System.currentTimeMillis(), completed);
	}
	
	private TodoElement(UUID id, String taskName, long dueDate, long createDate, boolean completed) {
		  super();
		  mTaskName = taskName;
		  mCompleted = completed;
		  mDueDate = dueDate;
		  mId = id;
		  mCreateDate = createDate;
	}
	
	public UUID getId() {
		return mId;
	}
	
	public long getCreatedate()	{
		return mCreateDate;
	}
	
	public String getTaskName() {
		return mTaskName;
	}
	
	public void setTaskName(String taskName) {
		mTaskName = taskName;
	}
	
	public long getDueDate() {
		return mDueDate;
	}
	
	public void setDueDate(long dueDate) {
		mDueDate = dueDate;
	}
	
	public boolean isComplete() {
		  return mCompleted;
	}
	
	public void setCompleted(boolean complete) {
		mCompleted = complete;
	}
	
	public String toJson() {
		final JSONObject json = new JSONObject();
		try {
			json.put("id", mId.toString());
			json.put("dueDate", mDueDate);
			json.put("createDate", mCreateDate);
			json.put("taskName", mTaskName);
			json.put("completed", mCompleted);
			return json.toString();
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return null;
		}
	}
	
	public static TodoElement fromJson(final String jsonString) {
		try {
			final JSONObject json = new JSONObject(jsonString);
			return new TodoElement(UUID.fromString(json.getString("id")), json.getString("taskName"),
					json.getLong("dueDate"), json.getLong("createDate"), json.getBoolean("completed"));
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return null;
		}
	}
}
