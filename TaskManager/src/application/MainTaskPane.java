package application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Extends TaskPane. Belongs to a MainTask. Displays MainTask name, time and completion and allows it to be edited and deleted.
 * TaskPanes of SubTasks are grouped on the UI under the MainTaskPane of the MainTask.
 */
public class MainTaskPane extends TaskPane {
	
	private HBox mainTaskBox = new HBox();
	private VBox subTaskBox = new VBox();
	private Label subTaskCountLabel = new Label();
	private Label taskTimeLabel = new Label();
	private TaskButton expandButton;
	private TaskButton editButton;

	/**
	 * Creates MainTaskPane to display information on a MainTask.
	 * @param task MainTask to display
	 */
	public MainTaskPane(MainTask task) {
		super(task);
		this.getStyleClass().add("hBoxTask");
		
		Label overdueLabel = new Label("Overdue:");
		overdueLabel.setTextFill(Color.RED);
		overdueLabel.setVisible(false);
		overdueLabel.setManaged(false);
		this.setStyle("-fx-border-color: black; -fx-border-width: 1;");
		if (((MainTask)this.task).getPlanDate() != null) {
			if (((MainTask)this.task).getPlanDate().isBefore(LocalDate.now()) && !this.task.isCompleted()) {
				this.setStyle("-fx-border-color: red; -fx-border-width: 1;");
				overdueLabel.setVisible(true);
				overdueLabel.setManaged(true);
			}
		}
		this.setPadding(new Insets(0,0,0,0));
		this.getChildren().clear();
		this.getChildren().addAll(this.mainTaskBox, this.subTaskBox);
		this.subTaskBox.setPadding(new Insets(0,0,0,34));

		//Subtask expand button, only visible if subtasks are added in addSubTaskPanes
		this.expandButton = new TaskButton("/icons/ButtonRetracted.png");
		this.expandButton.setOnAction(e -> {
			if (((MainTask)this.task).getSubTaskList().size() > 0) {
				((MainTask)this.task).setExpanded(!((MainTask)this.task).isExpanded());
			}
		});
		this.expandButton.setVisible(false);
		
		//Stack subtask expand button and count label
		StackPane subTaskButtonStack = new StackPane();
		subTaskButtonStack.getChildren().addAll(this.subTaskCountLabel, this.expandButton);
		
		//Time label
		this.taskTimeLabel.setAlignment(Pos.CENTER_LEFT);
		this.taskTimeLabel.setPadding(new Insets(0,7,0,7));
		this.taskTimeLabel.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeLabel.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);

		//Rebuild Hbox
		this.taskBox.getChildren().clear();
		this.taskBox.getChildren().addAll(subTaskButtonStack, this.completeCheckBox, overdueLabel, this.taskTimeLabel, this.taskNameLabel);

		//Button to hold the HBox
		this.taskBox.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
		this.taskBox.setPadding(new Insets(0, 0, 0, 0));
		this.taskBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(this.taskBox, Priority.ALWAYS);
		this.taskBox.setMaxWidth(Double.MAX_VALUE);
		this.taskBox.setMinHeight(34);
		this.taskBox.setMaxHeight(34);
		
		//Edit button opens editTaskPane for this task
		this.editButton = new TaskButton("/icons/ButtonEdit.png");
		this.editButton.setOnAction(e -> {
			if (((MainTask)this.task).getPlanDate() != null) {
				Main.calendarPane.editTaskBox.setTask((MainTask)this.task);
			} else {
				Main.toDoPane.editTaskBox.setTask((MainTask)this.task);
			}
		});

		this.mainTaskBox.getChildren().addAll(this.taskBox, this.editButton);
		
		//Set time, completed, expanded, editmode
		this.setTime(((MainTask)this.task).getTime());
		this.setCompleted(((MainTask)this.task).isCompleted());
		this.setExpanded(((MainTask)this.task).isExpanded());
	}
	
	/**
	 * Sets time label and field to display time belonging to a MainTask.
	 * Sets label and field to invisible if time is null.
	 * @param time
	 */
	public void setTime(LocalTime time) {
		if (time != null) {
			this.taskTimeLabel.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
		} else {
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
		//Clear box
		this.subTaskBox.getChildren().clear();
		
		//Add subtaskpanes
		for (SubTask subTask : ((MainTask)this.task).getSubTaskList()) {
			this.subTaskBox.getChildren().add(subTask.taskPane);
		}
		
		//Set expand button text
		this.subTaskCountLabel.setText(Integer.toString(((MainTask)this.task).getSubTaskList().size()));
		if (((MainTask)this.task).getSubTaskList().size() == 0) {
			this.expandButton.setVisible(false);
			this.subTaskCountLabel.setVisible(false);
		} else {
			this.expandButton.setVisible(true);
			this.subTaskCountLabel.setVisible(true);
		}
	}
	
	public void setExpanded(Boolean expanded) {
		this.subTaskBox.setVisible(expanded);
		this.subTaskBox.setManaged(expanded);
		
		if (expanded) {
			this.expandButton.setImage("/icons/ButtonExpanded.png");
		} else {
			this.expandButton.setImage("/icons/ButtonRetracted.png");
		}
	}
}
