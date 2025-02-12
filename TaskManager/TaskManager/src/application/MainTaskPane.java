package application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Extends TaskPane. Belongs to a MainTask. Displays MainTask name, time and completion and allows it to be edited and deleted.
 * TaskPanes of SubTasks are grouped on the UI under the MainTaskPane of the MainTask.
 */
public class MainTaskPane extends TaskPane {
	
	private LocalDate taskDate;	
	
	private HBox mainTaskBox = new HBox();
	private VBox subTaskBox = new VBox();
	private HBox buttonBox = new HBox();
	private Label taskTimeLabel = new Label();
	private TextField taskTimeField = new TimeTextField();
	private Button editButton = new Button();
	private Label dateLabel = new Label();

	/**
	 * Creates MainTaskPane to display information on a MainTask.
	 * @param task MainTask to display
	 */
	public MainTaskPane(MainTask task) {
		super(task);
		this.getStyleClass().add("hBoxTask");
		this.setStyle("-fx-border-color: black; -fx-border-width: 1;");
		this.setPadding(new Insets(0,0,0,0));
		this.getChildren().clear();
		this.getChildren().addAll(this.buttonBox, this.mainTaskBox, this.subTaskBox);
		
		//Time label
		this.taskTimeLabel.setAlignment(Pos.CENTER_LEFT);
		this.taskTimeLabel.setPadding(new Insets(0,7,0,7));
		this.taskTimeLabel.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeLabel.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);

		//Time field
		this.taskTimeField.setAlignment(Pos.CENTER_LEFT);
		this.taskTimeField.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeField.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);

		//Disable fields by default
		this.taskTimeField.setVisible(false);
		this.taskTimeField.setManaged(false);

		//Rebuild Hbox
		this.taskBox.getChildren().clear();
		this.taskBox.getChildren().addAll(this.completeCheckBox, this.taskTimeLabel, this.taskNameLabel, this.taskTimeField, this.taskNameField);

		//Button to hold the HBox
		Button taskButton = new Button();
		taskButton.setAlignment(Pos.CENTER_LEFT);
		taskButton.setPadding(new Insets(3,9,3,8));
		taskButton.setContentDisplay(ContentDisplay.LEFT);
		taskButton.setGraphic(taskBox);
		HBox.setHgrow(taskButton, Priority.ALWAYS);
		taskButton.setMaxWidth(Double.MAX_VALUE);
		this.getChildren().add(taskButton);
		
		//Button toggles expanded property and visibility of subtasks, if there are any
		taskButton.setOnAction(e -> {
			if (!this.editMode && ((MainTask)this.task).getSubTaskList().size() > 0) {
				((MainTask)this.task).setExpanded(!((MainTask)this.task).isExpanded());
			}
		});

		//Edit button toggles edit mode for main and subtasks
		this.editButton.setText("E");
		this.editButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
		this.editButton.setMinSize(34, 33);
		this.editButton.setOnAction(e -> {
			((MainTask)this.task).setExpanded(true);
			((MainTask)this.task).setEditMode(true);
		});
		
		//Add button adds new subtask
		Button addButton = new Button("+");
		addButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
		addButton.setMinSize(34, 33);
		addButton.setOnAction(e -> {
			SubTask newTask = new SubTask((MainTask)this.task);
			((MainTask)this.task).addToSubTaskList(newTask);
			newTask.taskPane.setEditMode(true);
		});
		
		//Confirm button
		Button confirmButton = new Button("C");
		confirmButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
		confirmButton.setMinSize(34, 33);
		confirmButton.setOnAction(e -> {
			((MainTask)this.task).setEditMode(false);
			
			//Set task variables
			this.task.setName(this.taskNameField.getText());
			LocalTime time = null;
			try {
				time = LocalTime.parse(this.taskTimeField.getText(), DateTimeFormatter.ofPattern("HHmm"));
			} catch (Exception ex) {
				
			} finally {
				((MainTask)this.task).setTime(time);
			}
			
			//Move task
			((MainTask)this.task).setPlanDate(new PlanDate(taskDate));
			
			//Set subtask variables
			for(SubTask subTask : ((MainTask)this.task).getSubTaskList()) {
				subTask.setName(subTask.taskPane.taskNameField.getText());
			}
			
			//Expand task if there are any subtasks
			if (((MainTask)this.task).getSubTaskList().size() > 0) {
				((MainTask)this.task).setExpanded(true);
			}
			
			//Updte UI
			Main.calendarPane.update(Main.calendarPane.currentViewMode);
		});
		
		//Date label
		this.dateLabel.setAlignment(Pos.BASELINE_CENTER);
		this.dateLabel.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(dateLabel, Priority.ALWAYS);
		this.dateLabel.setAlignment(Pos.BASELINE_CENTER);
		this.setNewPlanDate(((MainTask)this.task).getPlanDate().date);
		
		//Previous date button
		Button previousDateButton = new Button("<");
		previousDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
		previousDateButton.setMinSize(34, 33);
		previousDateButton.setAlignment(Pos.BASELINE_CENTER);
		previousDateButton.setOnAction(e -> {
			this.setNewPlanDate(this.taskDate.minusDays(1));
		});

		//Next date button
		Button nextDateButton = new Button(">");
		nextDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
		nextDateButton.setMinSize(34, 33);
		nextDateButton.setAlignment(Pos.BASELINE_CENTER);
		nextDateButton.setOnAction(e -> {
			this.setNewPlanDate(this.taskDate.plusDays(1));
		});

		this.mainTaskBox.getChildren().addAll(taskButton, this.deleteButton, this.editButton);
		
		//Box for buttons
		this.buttonBox.getChildren().addAll(addButton, previousDateButton, this.dateLabel, nextDateButton, confirmButton);
		this.buttonBox.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		this.buttonBox.setAlignment(Pos.CENTER_LEFT);
		this.buttonBox.setVisible(false);
		this.buttonBox.setManaged(false);
		
		//Set time, completed, expanded, editmode
		this.setTime(task.getTime());
		this.setCompleted(task.isCompleted());
		this.setExpanded(task.isExpanded());
		this.setEditMode(task.isEditMode());
	}
	
	/**
	 * Sets time label and field to display time belonging to a MainTask.
	 * Sets label and field to invisible if time is null.
	 * @param time
	 */
	public void setTime(LocalTime time) {
		if (time != null) {
			this.taskTimeField.setText(time.format(DateTimeFormatter.ofPattern("HHmm")));
			this.taskTimeLabel.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
		} else {
			this.taskTimeField.setText("");
			this.taskTimeLabel.setText("");
			this.taskTimeLabel.setVisible(false);
			this.taskTimeLabel.setManaged(false);
		}
	}
	
	@Override
	public void setCompleted(Boolean completed) {
		super.setCompleted(completed);
		
		//Strikethrough time label if completed
		if (completed) {
			this.taskTimeLabel.getStyleClass().clear();
			this.taskTimeLabel.getStyleClass().add("labelStrikethrough");
		} else {
			this.taskTimeLabel.getStyleClass().clear();
		}
	}
	
	/**
	 * Adds the TaskPanes of SubTasks belonging to the MainTask to the MainTaskPane.
	 */
	public void addSubTaskPanes() {
		this.subTaskBox.getChildren().clear();
		
		//Add subtaskpanes, set editmode
		for (SubTask subTask : ((MainTask)this.task).getSubTaskList()) {
			this.subTaskBox.getChildren().add(subTask.taskPane);
			subTask.taskPane.setEditMode(((MainTask)this.task).isEditMode());
		}
	}

	
	/**
	 * Sets MainTaskPane and linked SubTaskPanes editMode. Hides labels and shows fields to enable editing of name and time.
	 * @param editMode
	 */
	public void setEditMode(Boolean editMode) {
		super.setEditMode(editMode);
		
		//Set certain buttons as visible or invisible for editing task
		this.taskTimeLabel.setVisible(!editMode);
		this.taskTimeLabel.setManaged(!editMode);
		this.editButton.setVisible(!editMode);
		this.editButton.setManaged(!editMode);
		
		this.taskTimeField.setVisible(editMode);
		this.taskTimeField.setManaged(editMode);
		this.buttonBox.setVisible(editMode);
		this.buttonBox.setManaged(editMode);

		//Set subtaskpanes to editmode
		for (SubTask subTask : ((MainTask)this.task).getSubTaskList()) {
			subTask.taskPane.setEditMode(editMode);
		}
	}
	
	public void setExpanded(Boolean expanded) {
		this.subTaskBox.setVisible(expanded);
		this.subTaskBox.setManaged(expanded);
	}

	
	/**
	 * Set PlanDate of the MainTask to the date selected in the dateLabel.
	 * @param date
	 */
	public void setNewPlanDate(LocalDate date) {
		this.taskDate = date;
		this.dateLabel.setText(taskDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	}
}
