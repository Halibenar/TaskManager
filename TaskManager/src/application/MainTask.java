package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extends Task. Class describing a main task in the agenda. Holds a list of subTasks.
 * Contains methods for updating and deleting its entry in the SQLite database.
 * Holds a reference to a MainTaskPane for display of its variables.
 */
public class MainTask extends Task implements Comparable<MainTask> {

	private LocalDate date;
	private LocalTime time;
	private Boolean expanded;
	private Category category;

	private ArrayList<SubTask> subTaskList = new ArrayList<SubTask>();

	/*
	 * Constructor for new MainTask on a PlanDate.
	 * @param planDate Date the task should be planned on
	 */
	public MainTask(LocalDate date) {
		this.iD = 0;
		this.name = "";
		this.date = date;
		this.time = null;
		this.category = null;
		this.completed = false;
		this.expanded = false;
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
	public MainTask(int iD, String name, LocalDate date, LocalTime time, Boolean completed, Boolean expanded) {
		this.iD = iD;
		this.name = name;
		this.date = date;
		this.time = time;
		this.completed = completed;
		this.expanded = expanded;
	}
	
	/*
	 * Constructor for copying another MainTask.
	 * @param MainTask MainTask to be copied
	 */
	public MainTask(MainTask another) {
		this.iD = another.iD;
		this.name = another.name;
		this.date = another.date;
		this.time = another.time;
		this.category = another.category;
		this.completed = another.completed;
		this.expanded = another.expanded;
		this.taskPane = another.taskPane;
		
		//Deep copy of subtasks
		for (SubTask subTask : another.subTaskList) {
			SubTask newSubTask = new SubTask(subTask);
			newSubTask.setMainTask(this);
			this.subTaskList.add(newSubTask);
		}
	}

	/**
	 * Update SQLite database entry of this MainTask with new values
	 */
	public void updateSQL() {
		
		//Get data to insert
		String[] data = new String[6];
		data[0] = this.getName();
		data[1] = null;
		if (this.getPlanDate() != null) {
			data[1] = this.getPlanDate().toString();
		}
		data[2] = null;
		if (this.getTime() != null) {
			data[2] = this.getTime().toString().substring(0,5);
		}
		data[3] = Integer.toString(0);
		if (this.getCategory() != null) {
			data[3] = Integer.toString(this.getCategory().getID());
		}
		data[4] = this.isCompleted().toString();
		data[5] = this.isExpanded().toString();

		//If new task, add to database
		if (this.getID() == 0) {
			String insertString = "INSERT INTO tasks (Name, Date, Time, Category, Completed, Expanded) VALUES (?,?,?,?,?,?)";
			this.setID(SQLConnector.insert(insertString, data));

		//If already exists, update records
		} else {
			String updateString = "UPDATE tasks SET Name = ?, Date = ?, Time = ?, Category = ?, Completed = ?, Expanded = ? WHERE ID = " + this.getID();
			SQLConnector.update(updateString, data);
		}
		
		//Update UI
		Main.toDoPane.getTasks(Main.toDoPane.getCategory());
		Main.calendarPane.setPlanDate(Main.calendarPane.getPlanDate());
	}
	
	/**
	 * Delete SQLite database entry of this MainTask and all its SubTasks
	 */
	public void deleteSQL() {
		SQLConnector.delete("DELETE FROM tasks WHERE ID = '" + this.getID() + "'");
		SQLConnector.delete("DELETE FROM subtasks WHERE MainTaskID = '" + this.getID() + "'");
		
		//Update UI
		Main.toDoPane.getTasks(Main.toDoPane.getCategory());
		Main.calendarPane.setPlanDate(Main.calendarPane.getPlanDate());
	}
	
	public LocalDate getPlanDate() {
		return date;
	}

	public void setPlanDate(LocalDate planDate) {
		this.date = planDate;
	}

	public LocalTime getTime() {
		return this.time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
		((MainTaskPane)this.taskPane).setTime(time);
	}
	
	public Category getCategory() {
		return this.category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Boolean isExpanded() {
		return this.expanded;
	}
	
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
		((MainTaskPane)this.taskPane).setExpanded(expanded);
		this.updateSQL();
	}
	
	public ArrayList<SubTask> getSubTaskList() {
		this.subTaskList.sort(null);
		return this.subTaskList;
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

		//Sort based on task completion first
		if (this.isCompleted() && !another.isCompleted()) {
			outcome = 1;
		} else if (!this.isCompleted() && another.isCompleted()) {
			outcome = -1;
		}

		//Sort based on time second
		if (outcome == 0) {
			if (this.time == null && another.time != null)
			{
				outcome = 1;
			} else if (this.time != null && another.time == null) {
				outcome = -1;
			} else if (this.time != null && another.time != null) {
				outcome = this.time.compareTo(another.time);
			}
		}

		//Sort based on name last
		if (outcome == 0) {
			outcome = this.name.compareTo(another.name);
		}

		return outcome;
	}
	
	public static ArrayList<MainTask> getMainTaskList(LocalDate date, ReadMode readMode, Category category) {
		//New tasklist
		ArrayList<MainTask> taskList = new ArrayList<MainTask>();
		
		//Set SQLString
		String SQLString = "SELECT * FROM tasks WHERE Date ";
		if (date == null) {
			SQLString += "IS null";
		} else if (readMode == ReadMode.equals) {
			SQLString += "= '" + date.toString() + "'";
		} else if (readMode == ReadMode.lessThan) {
			SQLString += "< '" + date.toString() + "'";
		}
		
		if (category != null) {
			SQLString += " AND Category = " + category.getID();
		}

		//Get tasks from database
		SQLConnector.read(SQLString, rs -> {
			try {
				while (rs.next()) {
					//Parse string result from time query to LocalTime if it's not null
					LocalTime taskTime = null;
					if (rs.getString("Time") != null) {
						taskTime = LocalTime.parse(rs.getString("Time"));
					}

					//Create new main task
					MainTask newMainTask = new MainTask(rs.getInt("ID"), rs.getString("Name"), date, taskTime, Boolean.parseBoolean(rs.getString("Completed")), Boolean.parseBoolean(rs.getString("Expanded")));

					//Get category for main task; default is zero ("To Do"), but replaced if database contains a category matching tasks category
					AtomicInteger categoryID = new AtomicInteger(0);
					SQLConnector.read("SELECT * FROM categories WHERE ID = " + rs.getInt("Category"), rsCategory -> {
						try {
							while (rsCategory.next()) {
								categoryID.set(rsCategory.getInt("ID"));
							}
						} catch (SQLException e) {
							System.out.println(e);
						}
					});
					Category.getCategoryList().stream().filter(c -> c.getID() == categoryID.get()).forEach(Category -> { newMainTask.setCategory(Category); });

					//Get subtasks for main task
					SQLConnector.read("SELECT * FROM subtasks WHERE MainTaskID = " + newMainTask.getID(), rsSubTask -> {
						try {
							while (rsSubTask.next()) {
								//Create subtask
								SubTask newSubTask = new SubTask(newMainTask, rsSubTask.getInt("ID"), rsSubTask.getString("Name"), Boolean.parseBoolean(rsSubTask.getString("Completed")));
								//Add to subtasklist
								newMainTask.addToSubTaskList(newSubTask);
							}
						} catch (SQLException e) {
							System.out.println(e);
						}
					});
					//Create TaskPane
					newMainTask.taskPane = new MainTaskPane(newMainTask);
					
					//Add main task to tasklist
					taskList.add(newMainTask);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		});
		return taskList;
	}
}
