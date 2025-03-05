package application;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
	private Label subTaskCountLabel = new Label();
	private Button expandButton = new Button();
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
		
		//Subtask expand labels for subtaskcount and expansion status on the expand button
		Label expandLabel = new Label();
		if (((MainTask)this.task).isExpanded()) {
			expandLabel.setText("V");
		} else {
			expandLabel.setText("^");
		}
		HBox expandButtonBox = new HBox();
		expandButtonBox.setAlignment(Pos.CENTER_LEFT);
		expandButtonBox.getChildren().addAll(this.subTaskCountLabel, expandLabel);

		//Subtask expand button, only visible if subtasks are added in addSubTaskPanes
		this.expandButton.setMinSize(34, 34);
		this.expandButton.setMaxSize(34, 34);
		this.expandButton.setGraphic(expandButtonBox);
		this.expandButton.setOnAction(e -> {
			if (((MainTask)this.task).getSubTaskList().size() > 0) {
				((MainTask)this.task).setExpanded(!((MainTask)this.task).isExpanded());
			}
		});
		this.expandButton.setVisible(false);
		
		//Time label
		this.taskTimeLabel.setAlignment(Pos.CENTER_LEFT);
		this.taskTimeLabel.setPadding(new Insets(0,7,0,7));
		this.taskTimeLabel.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeLabel.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);

		//Rebuild Hbox
		this.taskBox.getChildren().clear();
		this.taskBox.getChildren().addAll(expandButton, this.completeCheckBox, this.taskTimeLabel, this.taskNameLabel);

		//Button to hold the HBox
		this.taskBox.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
		this.taskBox.setPadding(new Insets(0, 0, 0, 0));
		this.taskBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(this.taskBox, Priority.ALWAYS);
		this.taskBox.setMaxWidth(Double.MAX_VALUE);
		this.taskBox.setMinHeight(34);
		this.taskBox.setMaxHeight(34);
		
		//Edit button opens editTaskPane for this task
		this.editButton.setMinSize(34, 34);
		this.editButton.setMaxSize(34, 34);
		Image editImage = new Image(getClass().getResourceAsStream("/icons/ButtonEdit.png"), 34, 34, true, true);
		ImageView editImageView = new ImageView(editImage);
		editImageView.fitHeightProperty().bind(this.editButton.heightProperty());
		editImageView.fitWidthProperty().bind(this.editButton.widthProperty());
		this.editButton.setGraphic(editImageView);
		this.editButton.setOnAction(e -> {
			if (((MainTask)this.task).getPlanDate() != null) {
				Main.calendarPane.editTaskBox.setTask((MainTask)this.task);
			} else {
				Main.toDoPane.editTaskBox.setTask((MainTask)this.task);
			}
		});

		this.mainTaskBox.getChildren().addAll(taskBox, this.editButton);
		
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
		} else {
			this.expandButton.setVisible(true);
		}
	}
	
	public void setExpanded(Boolean expanded) {
		this.subTaskBox.setVisible(expanded);
		this.subTaskBox.setManaged(expanded);
	}
}
