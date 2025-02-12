package application;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Extends Task. Class describing a main task in the agenda. Holds a list of subTasks.
 * Contains methods for updating and deleting its entry in the SQLite database.
 * Holds a reference to a MainTaskPane for display of its variables.
 */
public class MainTask extends Task implements Comparable<MainTask> {

	private PlanDate planDate;
	private LocalTime time;
	private Boolean expanded;
	private Boolean editMode;

	private ArrayList<SubTask> subTaskList = new ArrayList<SubTask>();

	/*
	 * Constructor for new MainTask on a PlanDate.
	 * @param planDate Date the task should be planned on
	 */
	public MainTask(PlanDate planDate) {
		this.iD = 0;
		this.name = "";
		this.planDate = planDate;
		this.time = null;
		this.completed = false;
		this.expanded = false;
		this.editMode = false;
		this.taskPane = new MainTaskPane(this);
	}

	/*
	 * Constructor for MainTask from SQLite database.
	 * @param iD Unique MainTask ID number
	 * @param name MainTask name
	 * @param planDate Date MainTask is planned on
	 * @param time Time MainTask is planned on, can be null
	 * @param completed MainTask completion
	 * @param expanded MainTaskPane expanded
	 * @param editMode MainTaskPane editMode
	 */
	public MainTask(int iD, String name, PlanDate planDate, LocalTime time, Boolean completed, Boolean expanded, Boolean editMode) {
		this.iD = iD;
		this.name = name;
		this.planDate = planDate;
		this.time = time;
		this.completed = completed;
		this.expanded = expanded;
		this.editMode = editMode;
		this.taskPane = new MainTaskPane(this);
	}

	/**
	 * Update SQLite database entry of this MainTask with new values
	 */
	public void updateSQL() {
		
		//Get data to insert
		String[] data = new String[6];
		data[0] = this.getName();
		data[1] = this.getPlanDate().toString();
		data[2] = null;
		if (this.getTime() != null) {
			data[2] = this.getTime().toString().substring(0,5);
		}
		data[3] = this.isCompleted().toString();
		data[4] = this.isExpanded().toString();
		data[5] = this.isEditMode().toString();

		//If new task, add to database
		if (this.getID() == 0) {
			String insertString = "INSERT INTO tasks (Name, Date, Time, Completed, Expanded, Editmode) VALUES (?,?,?,?,?,?)";
			this.setID(SQLConnector.insert(insertString, data));

		//If already exists, update records
		} else {
			String updateString = "UPDATE tasks SET Name = ?, Date = ?, Time = ?, Completed = ?, Expanded = ?, Editmode = ? WHERE ID = " + this.getID();
			SQLConnector.update(updateString, data);
		}
	}
	
	/**
	 * Delete SQLite database entry of this MainTask and all its SubTasks
	 */
	public void deleteSQL() {
		SQLConnector.delete("DELETE FROM tasks WHERE ID = '" + this.getID() + "'");
		SQLConnector.delete("DELETE FROM subtasks WHERE MainTaskID = '" + this.getID() + "'");
	}
	
	public PlanDate getPlanDate() {
		return planDate;
	}

	public void setPlanDate(PlanDate planDate) {
		this.planDate = planDate;
		this.updateSQL();
	}

	public LocalTime getTime() {
		return this.time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
		this.updateSQL();
		((MainTaskPane)this.taskPane).setTime(time);
	}
	
	public Boolean isExpanded() {
		return this.expanded;
	}
	
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
		this.updateSQL();
		((MainTaskPane)this.taskPane).setExpanded(expanded);
	}
	
	public Boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
		this.updateSQL();
		((MainTaskPane)this.taskPane).setEditMode(editMode);
	}
	
	public ArrayList<SubTask> getSubTaskList() {
		return this.subTaskList;
	}

	public void setSubTaskList(ArrayList<SubTask> subTaskList) {
		this.subTaskList = subTaskList;
		((MainTaskPane)this.taskPane).addSubTaskPanes();
	}

	public void addToSubTaskList(SubTask subTask) {
		this.subTaskList.add(subTask);
		((MainTaskPane)this.taskPane).addSubTaskPanes();
	}
	
	public void removeFromSubTaskList(SubTask subTask) {
		this.subTaskList.remove(subTask);
		((MainTaskPane)this.taskPane).addSubTaskPanes();
	}
	
	@Override
	public int compareTo(MainTask another) {
		int outcome = 0;

		if (this.time == null && another.time != null)
		{
			outcome = 1;
		} else if (this.time != null && another.time == null) {
			outcome = -1;
		} else if (this.time != null && another.time != null) {
			outcome = this.time.compareTo(another.time);
		}

		if (outcome == 0) {
			outcome = this.name.compareTo(another.name);
		}

		return outcome;
	}
}
