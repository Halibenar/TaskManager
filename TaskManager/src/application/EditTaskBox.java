package application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class EditTaskBox extends VBox {

	private MainTask task;
	private MainTask copyTask;
	
	private TextField taskNameField;
	private RestrictedTextField taskTimeField;
	private RestrictedTextField taskDateField;
	private HBox taskDateBox;
	private HBox taskCategoryBox;
	ComboBox<Category> categoryComboBox;
	private VBox subTaskBox;

	private Boolean taskHasDate;
	
	/**
	 * Creates EditTaskBox to edit a MainTask and its SubTasks.
	 * @param mainTask MainTask to display
	 */
	public EditTaskBox() {
		HBox.setHgrow(this, Priority.ALWAYS);
		this.setMaxWidth(Double.MAX_VALUE);
		this.getStyleClass().add("hBoxTask");
		this.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 1 1;");
		
		//Name title label
		Label taskNameLabel = new Label("Name:");
		taskNameLabel.setMinWidth(45);
		taskNameLabel.setMaxWidth(45);
		
		//Name field
		this.taskNameField = new TextField();
		HBox.setHgrow(this.taskNameField, Priority.ALWAYS);
		this.taskNameField.setMaxWidth(Double.MAX_VALUE);
		HBox.setMargin(this.taskNameField, new Insets(0, 3, 0, 0));
		
		//Confirm button confirms editing
		Button confirmButton = new Button();
		//confirmButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 1;");
		confirmButton.setMinSize(34, 34);
		confirmButton.setMaxSize(34, 34);
		Image confirmImage = new Image(getClass().getResourceAsStream("/icons/ButtonConfirm.png"), 34, 34, true, true);
		ImageView confirmImageView = new ImageView(confirmImage);
		confirmImageView.fitHeightProperty().bind(confirmButton.heightProperty());
		confirmImageView.fitWidthProperty().bind(confirmButton.widthProperty());
		confirmButton.setGraphic(confirmImageView);
		confirmButton.setOnAction(e -> {

			//Set name
			this.copyTask.setName(this.taskNameField.getText());

			//Set date and time if task is on the agenda, otherwise set both to null
			if (this.taskHasDate) {
				//Set time
				LocalTime time = null;
				try {
					time = LocalTime.parse(this.taskTimeField.getText(), DateTimeFormatter.ofPattern("HHmm"));
				} catch (Exception ex) {
				} finally {
					this.copyTask.setTime(time);
				}

				//Set date
				this.copyTask.setPlanDate(this.copyTask.getPlanDate());
			} else {
				//Set time
				this.copyTask.setTime(null);
				//Set date
				this.copyTask.setPlanDate(null);
			}
			
			//Set category
			this.copyTask.setCategory(this.categoryComboBox.getValue());
			
			//Set SubTask names and remove deleted SubTasks
			for (Node node : this.subTaskBox.getChildren()) {
				if (node instanceof EditSubTaskBox) {
					if (((EditSubTaskBox)node).getDeleted()) {
						((EditSubTaskBox)node).getSubTask().deleteSQL();
					} else {
						((EditSubTaskBox)node).getSubTask().setName(((EditSubTaskBox)node).getSubTaskNameField().getText());
					}
				}
			}
			
			//Copy changes to original task
			this.task = new MainTask(copyTask);
			for (SubTask subTask : this.task.getSubTaskList()) {
				subTask.updateSQL();
			}
			this.task.updateSQL();

			this.setVisible(false);
			this.setManaged(false);
		});
		
		//Cancel button cancels editing
		Button cancelButton = new Button();
		//cancelButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 1;");
		cancelButton.setMinSize(34, 34);
		cancelButton.setMaxSize(34, 34);
		Image cancelImage = new Image(getClass().getResourceAsStream("/icons/ButtonCancel.png"), 34, 34, true, true);
		ImageView cancelImageView = new ImageView(cancelImage);
		cancelImageView.fitHeightProperty().bind(cancelButton.heightProperty());
		cancelImageView.fitWidthProperty().bind(cancelButton.widthProperty());
		cancelButton.setGraphic(cancelImageView);
		cancelButton.setOnAction(e -> {
			this.setVisible(false);
			this.setManaged(false);
		});
		
		//HBox for task name, cancel and confirm buttons
		HBox taskNameBox = new HBox();
		taskNameBox.setPadding(new Insets(0,0,0,3));
		taskNameBox.setAlignment(Pos.CENTER_LEFT);
		taskNameBox.getChildren().addAll(taskNameLabel, this.taskNameField, confirmButton, cancelButton);
		
		//Time title label
		Label taskTimeLabel = new Label("Time:");
		taskTimeLabel.setMinWidth(45);
		taskTimeLabel.setMaxWidth(45);

		//Time field
		this.taskTimeField = new RestrictedTextField("[0-9]", 4);
		this.taskTimeField.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		this.taskTimeField.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
		HBox.setMargin(this.taskTimeField, new Insets(0, 10, 0, 0));

		//Date title label
		Label taskDateLabel = new Label("Date:");
		taskDateLabel.setMinWidth(45);
		taskDateLabel.setMaxWidth(45);

		//Previous date button
		Button previousDateButton = new Button();
		previousDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 0;");
		previousDateButton.setMinSize(34, 34);
		previousDateButton.setMaxSize(34, 34);
		Image previousImage = new Image(getClass().getResourceAsStream("/icons/ButtonPrevious.png"), 34, 34, true, true);
		ImageView previousImageView = new ImageView(previousImage);
		previousImageView.fitHeightProperty().bind(previousDateButton.heightProperty());
		previousImageView.fitWidthProperty().bind(previousDateButton.widthProperty());
		previousDateButton.setGraphic(previousImageView);
		previousDateButton.setOnAction(e -> {
			this.setNewDate(this.copyTask.getPlanDate().minusDays(1));
		});

		//Date field
		this.taskDateField = new RestrictedTextField("([0-9]|-)", 10);
		this.taskDateField.setMinWidth(new Text("00-00-0000").getLayoutBounds().getWidth() + 14);
		this.taskDateField.setMaxWidth(new Text("00-00-0000").getLayoutBounds().getWidth() + 14);
		this.taskDateField.textProperty().addListener((observable, oldValue, newValue) -> {
			try {	
				LocalDate newDate = LocalDate.parse(newValue, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				this.setNewDate(newDate);
			} catch (Exception e) {
			}
		});

		//Next date button
		Button nextDateButton = new Button();
		nextDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 0;");
		nextDateButton.setMinSize(34, 34);
		nextDateButton.setMaxSize(34, 34);
		Image nextImage = new Image(getClass().getResourceAsStream("/icons/ButtonNext.png"), 34, 34, true, true);
		ImageView nextImageView = new ImageView(nextImage);
		nextImageView.fitHeightProperty().bind(nextDateButton.heightProperty());
		nextImageView.fitWidthProperty().bind(nextDateButton.widthProperty());
		nextDateButton.setGraphic(nextImageView);
		nextDateButton.setOnAction(e -> {
			this.setNewDate(this.copyTask.getPlanDate().plusDays(1));
		});

		//Empty region
		Region emptyRegion2 = new Region();
		HBox.setHgrow(emptyRegion2, Priority.ALWAYS);
		emptyRegion2.setMaxWidth(Double.MAX_VALUE);
		
		//Add button adds new subtask
		Button addButton = new Button();
		//addButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 0 1;");
		addButton.setMinSize(34, 34);
		addButton.setMaxSize(34, 34);
		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonPlus.png"), 34, 34, true, true);
		ImageView addImageView = new ImageView(addImage);
		addImageView.fitHeightProperty().bind(addButton.heightProperty());
		addImageView.fitWidthProperty().bind(addButton.widthProperty());
		addButton.setGraphic(addImageView);
		addButton.setOnAction(e -> {
			if (this.subTaskBox.getChildren().size() < 10) {
			SubTask newTask = new SubTask(this.copyTask);
			this.copyTask.addToSubTaskList(newTask);
			this.subTaskBox.getChildren().add(new EditSubTaskBox(newTask));
			}
		});
		
		//Delete button deletes maintask
		Button deleteButton = new Button();
		//deleteButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
		deleteButton.setMinSize(34, 34);
		deleteButton.setMaxSize(34, 34);
		deleteButton.setOnAction(e -> {
			this.delete();
		});
		Image deleteImage = new Image(getClass().getResourceAsStream("/icons/ButtonDelete.png"), 34, 34, true, true);
		ImageView deleteImageView = new ImageView(deleteImage);
		deleteImageView.fitHeightProperty().bind(deleteButton.heightProperty());
		deleteImageView.fitWidthProperty().bind(deleteButton.widthProperty());
		deleteButton.setGraphic(deleteImageView);
		
		Button switchDateButton = new Button("S");
		switchDateButton.setMinSize(34, 34);
		switchDateButton.setMaxSize(34, 34);
		switchDateButton.setOnAction(e -> {
			this.setTaskHasDate(!this.taskHasDate);
		});
		
		//HBox for task time and date
		this.taskDateBox = new HBox();
		this.taskDateBox.setPadding(new Insets(0,0,0,3));
		this.taskDateBox.setAlignment(Pos.CENTER_LEFT);
		this.taskDateBox.getChildren().addAll(taskTimeLabel, this.taskTimeField, taskDateLabel, previousDateButton, this.taskDateField, nextDateButton);
		
		//Category label
		Label taskCategoryLabel = new Label("List:");
		taskCategoryLabel.setMinWidth(45);
		taskCategoryLabel.setMaxWidth(45);
		
		this.categoryComboBox = new ComboBox<>();
		this.categoryComboBox.setConverter(new StringConverter<Category>() {

			@Override
			public String toString(Category object) {
				if (object != null) {
					return object.getName();
				} else {
					return "None";
				}
			}

			@Override
			public Category fromString(String string) {
				return categoryComboBox.getItems().stream().filter(ap -> 
				ap.getName().equals(string)).findFirst().orElse(null);
			}
		});

		this.categoryComboBox.setVisibleRowCount(3);
		//Set category selection list
		this.setPickList();

		//HBox for task category
		this.taskCategoryBox = new HBox();
		this.taskCategoryBox.setPadding(new Insets(0,0,0,3));
		this.taskCategoryBox.setAlignment(Pos.CENTER_LEFT);
		this.taskCategoryBox.getChildren().addAll(taskCategoryLabel, this.categoryComboBox);

		//HBox for task time and date
		HBox taskButtonBox = new HBox();
		taskButtonBox.setPadding(new Insets(0,0,0,3));
		taskButtonBox.setAlignment(Pos.CENTER_LEFT);
		taskButtonBox.getChildren().addAll(switchDateButton, addButton, deleteButton);

		//Hbox for optional date and time
		HBox taskSecondRowBox = new HBox();
		taskSecondRowBox.getChildren().addAll(this.taskDateBox, this.taskCategoryBox, emptyRegion2, taskButtonBox);

		//VBox for subtasks
		this.subTaskBox = new VBox();
		this.subTaskBox.setPadding(new Insets(0,0,0,34));
		this.getChildren().addAll(taskNameBox, taskSecondRowBox, this.subTaskBox);
	}
	
	/**
	 * Make a copy of a MainTask and display its variables on fields for editing.
	 * @param MainTask MainTask to copy and display for editing
	 */
	public void setTask(MainTask task) {
		//Clear subtasks
		this.subTaskBox.getChildren().clear();
		
		//Create copy of task for editing
		this.task = task;
		this.copyTask = new MainTask(task);
		
		//Set fields
		if (this.copyTask.getID() == 0) {
			this.taskNameField.setText("New task");
		} else {
			this.taskNameField.setText(this.copyTask.getName());
		}
		if (this.copyTask.getTime() != null) {
			this.taskTimeField.setText(this.copyTask.getTime().format(DateTimeFormatter.ofPattern("HHmm")));
		} else {
			this.taskTimeField.setText("");
		}
		if (this.copyTask.getPlanDate() != null) {
			this.setNewDate(this.copyTask.getPlanDate());
			this.categoryComboBox.setValue(null);
			this.setTaskHasDate(true);
		} else {
			this.setNewDate(LocalDate.now());
			this.setTaskHasDate(false);
		}
		
		AtomicInteger categoryID = new AtomicInteger(0);
		if (this.copyTask.getCategory() != null ) {
			categoryID.set(this.copyTask.getCategory().getID());
		}
		Category.getCategoryList().stream().filter(c -> c.getID() == categoryID.get()).forEach(Category -> { this.categoryComboBox.setValue(Category); });

		for (SubTask subTask : this.copyTask.getSubTaskList()) {
			this.subTaskBox.getChildren().add(new EditSubTaskBox(subTask));
		}

		//Set this as visible in UI
		this.setVisible(true);
		this.setManaged(true);
	}
	
	/**
	 * Adds the TaskPanes of SubTasks belonging to the MainTask to the MainTaskPane.
	 */
	public void addSubTaskBox(EditSubTaskBox editSubTaskBox) {		
			this.subTaskBox.getChildren().add(editSubTaskBox);
	}
	
	public void removeSubTaskBox(EditSubTaskBox editSubTaskBox) {
		
	}
	
	/**
	 * Set PlanDate of the MainTask to the date selected in the dateLabel.
	 * @param date
	 */
	public void setNewDate(LocalDate date) {
		this.copyTask.setPlanDate(date);
		this.taskDateField.setText(this.copyTask.getPlanDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	}
	
	public void setTaskHasDate(Boolean hasDate) {
		this.taskHasDate = hasDate;
				
		this.taskDateBox.setVisible(hasDate);
		this.taskDateBox.setManaged(hasDate);
		
		this.taskCategoryBox.setVisible(!hasDate);
		this.taskCategoryBox.setManaged(!hasDate);
	}
	
	public void setPickList() {
		ArrayList<Category> pickList = new ArrayList<Category>();
		for (Category category : Category.getCategoryList()) {
			if (category.getID() >= 0) {
				pickList.add(category);
			}
		}
		this.categoryComboBox.setItems(FXCollections.observableList(pickList));
	}
	
	/**
	 * Sets TaskPane as invisible and deletes Task from SQLite database.
	 */
	public void delete() {
		this.task.deleteSQL();
		this.setVisible(false);
		this.setManaged(false);
	}
	
	public class EditSubTaskBox extends HBox {
		
		private SubTask subTask;
		private TextField subTaskNameField;
		private Boolean deleted = false;
		
		public EditSubTaskBox (SubTask subTask) {
			this.subTask = subTask;
			this.setSpacing(3);
			this.setAlignment(Pos.CENTER_LEFT);
			
			//Name title label
			Label subTaskNameLabel = new Label("Sub Task:");
			subTaskNameLabel.setPrefWidth(60);
			
			//Name field
			this.subTaskNameField = new TextField(this.subTask.getName());
			HBox.setHgrow(subTaskNameField, Priority.ALWAYS);
			subTaskNameField.setMaxWidth(Double.MAX_VALUE);
			
			//Delete button deletes subtask
			Button deleteButton = new Button();
			//deleteButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 0 1;");
			deleteButton.setMinSize(34, 33);
			deleteButton.setMaxSize(34, 33);
			deleteButton.setOnAction(e -> {
				this.subTask.getMainTask().removeFromSubTaskList(this.subTask);
				this.deleted = true;
				this.setVisible(false);
				this.setManaged(false);
			});
			Image cancelImage = new Image(getClass().getResourceAsStream("/icons/ButtonDelete.png"), 34, 34, true, true);
			ImageView cancelImageView = new ImageView(cancelImage);
			cancelImageView.fitHeightProperty().bind(deleteButton.heightProperty());
			cancelImageView.fitWidthProperty().bind(deleteButton.widthProperty());
			deleteButton.setGraphic(cancelImageView);
			
			this.getChildren().addAll(subTaskNameLabel, subTaskNameField, deleteButton);
		}
		
		public void setSubTaskName() {
			this.subTask.setName(subTaskNameField.getText());
		}
		
		public SubTask getSubTask() {
			return this.subTask;
		}
		
		public TextField getSubTaskNameField() {
			return this.subTaskNameField;
		}
		
		public Boolean getDeleted() {
			return this.deleted;
		}
	}
}