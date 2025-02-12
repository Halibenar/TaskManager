package application;

/**
 * Extends Task. Class describing a subtask belonging to a main task.
 * Contains methods for updating and deleting its entry in the SQLite database.
 * Holds a reference to a TaskPane for display of its variables.
 */
public class SubTask extends Task implements Comparable<SubTask> {

	private MainTask mainTask;

	/*
	 * Constructor for new SubTask for a MainTask.
	 * @param manTask MainTask this SubTask belongs to
	 */
	public SubTask(MainTask mainTask) {
		this.mainTask = mainTask;
		this.iD = 0;
		this.name = "";
		this.completed = false;
		this.taskPane = new TaskPane(this);
	}

	/**
	 * Constructor for SubTask from SQLite database.
	 * @param mainTask MainTask this SubTask belongs to
	 * @param iD Unique SubTask ID number
	 * @param name SubTask name
	 * @param completed SubTask completion
	 */
	public SubTask(MainTask mainTask, int iD, String name, Boolean completed) {
		this.mainTask = mainTask;
		this.iD = iD;
		this.name = name;
		this.completed = completed;
		this.taskPane = new TaskPane(this);
	}

	/**
	 * Update SQLite database entry of this MainTask with new values
	 */
	public void updateSQL() {

		//Get data to insert
		String[] data = new String[3];
		data[0] = this.getName();
		data[1] = String.valueOf(this.mainTask.getID());
		data[2] = this.isCompleted().toString();
		
		//If new task, add to database
		if (this.getID() == 0) {
			String insertString = "INSERT INTO subtasks (Name, MainTaskID, Completed) VALUES (?,?,?)";
			this.setID(SQLConnector.insert(insertString, data));
			
		//If already exists, update records
		} else {
			String updateString = "UPDATE subtasks SET Name = ?, MainTaskID = ?, Completed = ? WHERE ID = " + this.getID();
			SQLConnector.update(updateString, data);
		}
	}

	/**
	 * Delete SQLite database entry of this MainTask and all its SubTasks
	 */
	public void deleteSQL() {
		SQLConnector.delete("DELETE FROM subtasks WHERE ID = '" + this.getID() + "'");
	}
	
	@Override
	public int compareTo(SubTask another) {
		return this.name.compareTo(another.name);
	}
	
	public MainTask getMainTask() {
		return mainTask;
	}

	public void setMainTask(MainTask mainTask) {
		this.mainTask = mainTask;
	}
}
