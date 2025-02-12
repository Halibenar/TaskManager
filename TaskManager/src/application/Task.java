package application;

/**
 * Abstract class describing a task in the agenda. Extended by SubTask and MainTask.
 */
public abstract class Task {
	
	protected int iD;
	protected String name;
	protected Boolean completed;
	
	TaskPane taskPane;

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
		this.updateSQL();
		this.taskPane.setName(name);
	}

	public Boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		this.updateSQL();
		this.taskPane.setCompleted(completed);
	}
	
	public abstract void updateSQL();
	
	public abstract void deleteSQL();

	@Override
	public String toString() {
		return this.name;
	}
}
