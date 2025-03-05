package application;

/**
 * Abstract class describing a task in the agenda. Extended by SubTask and MainTask.
 */
public abstract class Task {
	
	protected int iD;
	protected String name;
	protected Boolean completed;
	
	TaskPane taskPane;
	
	//Viewmode for viewing the agenda per day or week, used as parameter in getTaskList
	public static enum ReadMode {
		equals, lessThan
	}

	public int getID() {
		return iD;
	}

	public void setID(int iD) {
		this.iD = iD;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		this.taskPane.setName(name);
	}

	public Boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		this.taskPane.setCompleted(completed);
		this.updateSQL();
	}
	
	public abstract void updateSQL();
	
	public abstract void deleteSQL();

	@Override
	public String toString() {
		return this.name;
	}
}
