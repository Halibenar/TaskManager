package application;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Extends TaskPane. Belongs to a MainTask. Displays MainTask name, time and completion and allows it to be edited and deleted.
 * TaskPanes of SubTasks are grouped on the UI under the MainTaskPane of the MainTask.
 */
public class MainTaskPane extends TaskPane {
	
	private HBox mainTaskBox = new HBox();
	private VBox subTaskBox = new VBox();
	private HBox buttonBox = new HBox();
	private Label taskTimeLabel = new Label();
	private Button editButton = new Button();

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
		this.subTaskBox.setPadding(new Insets(0,0,0,34));
		
		//Time label
		this.taskTimeLabel.setAlignment(Pos.CENTER_LEFT);
		this.taskTimeLabel.setPadding(new Insets(0,7,0,7));
		this.taskTimeLabel.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeLabel.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);

		//Rebuild Hbox
		this.taskBox.getChildren().clear();
		this.taskBox.getChildren().addAll(this.completeCheckBox, this.taskTimeLabel, this.taskNameLabel);

		//Button to hold the HBox
		Button taskButton = new Button();
		taskButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
		taskButton.setAlignment(Pos.CENTER_LEFT);
		taskButton.setContentDisplay(ContentDisplay.LEFT);
		taskButton.setGraphic(taskBox);
		HBox.setHgrow(taskButton, Priority.ALWAYS);
		taskButton.setMaxWidth(Double.MAX_VALUE);
		taskButton.setPrefHeight(34);
		this.getChildren().add(taskButton);
		
		//Button toggles expanded property and visibility of subtasks, if there are any
		taskButton.setOnAction(e -> {
			if (((MainTask)this.task).getSubTaskList().size() > 0) {
				((MainTask)this.task).setExpanded(!((MainTask)this.task).isExpanded());
			}
		});
		
		//Edit button toggles edit mode for main and subtasks
		this.editButton.setMinSize(34, 34);
		this.editButton.setMaxSize(34, 34);
		Image editImage = new Image(getClass().getResourceAsStream("/icons/ButtonEdit.png"), 34, 34, true, true);
		ImageView editImageView = new ImageView(editImage);
		editImageView.fitHeightProperty().bind(this.editButton.heightProperty());
		editImageView.fitWidthProperty().bind(this.editButton.widthProperty());
		this.editButton.setGraphic(editImageView);
		this.editButton.setOnAction(e -> {
			Main.calendarPane.editTaskBox.setTask((MainTask)this.task);
		});

		this.mainTaskBox.getChildren().addAll(taskButton, this.editButton);
		
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
		
		//Add subtaskpanes, set editmode
		for (SubTask subTask : ((MainTask)this.task).getSubTaskList()) {
			this.subTaskBox.getChildren().add(subTask.taskPane);
		}
	}
	
	public void setExpanded(Boolean expanded) {
		this.subTaskBox.setVisible(expanded);
		this.subTaskBox.setManaged(expanded);
	}
}
