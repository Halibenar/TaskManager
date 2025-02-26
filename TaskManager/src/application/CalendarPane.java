package application;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import application.Main.ViewMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Extends VBox. Shows Tasks for a PlanDate. Holds a bar with buttons for PlanDate selection and a stackpane.
 * Stackpane holds a DatePicker and a GridPane with TaskPanes for Tasks on the selected PlanDate.
 */
public class CalendarPane extends VBox {

	PlanDate planDate = new PlanDate(LocalDate.now());
	ArrayList<PlanDate> retrievedDates = new ArrayList<PlanDate>();
	VBox daysBox;
	Button datePickerButton;
	Button previousDateButton;
	Button nextDateButton;
	Button weekButton;
	Button[] dayButtons;
	ViewMode currentViewMode;
	EditTaskBox editTaskBox;

	public CalendarPane() {
		//HBox for buttons and viewdate
		HBox dateBar = new HBox();
		dateBar.setAlignment(Pos.CENTER_LEFT);
		dateBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//HBox for day selection buttons
		HBox dayButtonBar = new HBox();
		dayButtonBar.setAlignment(Pos.CENTER_LEFT);
		dayButtonBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//Stackpane
		StackPane dateStack = new StackPane();
		VBox.setVgrow(dateStack, Priority.ALWAYS);
		dateStack.setAlignment(Pos.TOP_LEFT);

		//DatePicker layer, set invisible at start
		DatePicker datePicker = new DatePicker(this);
		datePicker.setVisible(false);
		
		//EditTaskBox for editing tasks
		this.editTaskBox = new EditTaskBox();
		this.editTaskBox.setVisible(false);
		this.editTaskBox.setManaged(false);
		
		//Add elements
		this.getChildren().addAll(dateBar, dayButtonBar, dateStack, this.editTaskBox);

		//Central date button, sets viewmode to week
		this.weekButton = new Button();
		Font titleFont = new Font(weekButton.getFont().getName(), 15);
		this.weekButton.setFont(titleFont);
		this.weekButton.setMaxHeight(33);
		this.weekButton.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(this.weekButton, Priority.ALWAYS);
		this.weekButton.setOnAction(e -> {
			this.planDate = new PlanDate(planDate.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
			this.update(ViewMode.week);
		});
		
		//Datepicker button, sets datePicker as visible in dateStack
		this.datePickerButton = new Button("D");
		this.datePickerButton.setMinSize(34, 33);
		this.datePickerButton.setMaxSize(34, 33);
		this.datePickerButton.setOnAction(e -> {
			datePicker.pickerDate = planDate.date;
			datePicker.Update();
			datePicker.setVisible(!datePicker.isVisible());
		});

		//Previous date button
		this.previousDateButton = new Button();
		this.previousDateButton.setMinSize(34, 33);
		this.previousDateButton.setMaxSize(34, 33);
		Image previousImage = new Image(getClass().getResourceAsStream("/icons/ButtonPrevious.png"));
		ImageView previousImageView = new ImageView(previousImage);
		previousImageView.fitHeightProperty().bind(this.previousDateButton.heightProperty());
		previousImageView.fitWidthProperty().bind(this.previousDateButton.widthProperty());
		this.previousDateButton.setGraphic(previousImageView);

		//Next date button
		this.nextDateButton = new Button();
		this.nextDateButton.setMinSize(34, 33);
		this.nextDateButton.setMaxSize(34, 33);
		Image nextImage = new Image(getClass().getResourceAsStream("/icons/ButtonNext.png"));
		ImageView nextImageView = new ImageView(nextImage);
		nextImageView.fitHeightProperty().bind(this.nextDateButton.heightProperty());
		nextImageView.fitWidthProperty().bind(this.nextDateButton.widthProperty());
		this.nextDateButton.setGraphic(nextImageView);

		//Today button
		Button todayButton = new Button();
		todayButton.setMinSize(34, 33);
		todayButton.setMaxSize(34, 33);
		Image todayImage = new Image(getClass().getResourceAsStream("/icons/ButtonToday.png"));
		ImageView todayImageView = new ImageView(todayImage);
		todayImageView.fitHeightProperty().bind(todayButton.heightProperty());
		todayImageView.fitWidthProperty().bind(todayButton.widthProperty());
		todayButton.setGraphic(todayImageView);
		todayButton.setOnAction(e -> {
			planDate = new PlanDate(LocalDate.now());
			this.update(this.currentViewMode);
		});
		
		
		//Add task button
		Button addTaskButton = new Button();
		addTaskButton.setMinSize(34, 33);
		addTaskButton.setMaxSize(34, 33);
		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonEdit.png"));
		ImageView addImageView = new ImageView(addImage);
		addImageView.fitHeightProperty().bind(addTaskButton.heightProperty());
		addImageView.fitWidthProperty().bind(addTaskButton.widthProperty());
		addTaskButton.setGraphic(addImageView);
		addTaskButton.setOnAction(e -> {
			MainTask newTask = new MainTask(this.planDate);
			newTask.setName("New Task");
			this.editTaskBox.setTask(newTask);
		});

		//Add to dateBar
		dateBar.getChildren().addAll(this.previousDateButton, todayButton, addTaskButton, this.weekButton, this.datePickerButton, this.nextDateButton);
		dateBar.getStyleClass().add("hbox");
		
		//Buttons for day selection
		this.dayButtons = new Button[7];
		for (int i = 0; i < 7; i++) {
			this.dayButtons[i] = new Button();
			if (i < 6) {
				this.dayButtons[i].setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
			}
			this.dayButtons[i].setPadding(new Insets(0,0,0,0));
			this.dayButtons[i].setMinSize(30, 42);
			this.dayButtons[i].setMaxSize(Double.MAX_VALUE, 42);
			HBox.setHgrow(this.dayButtons[i], Priority.ALWAYS);
			dayButtonBar.getChildren().add(this.dayButtons[i]);
		}

		//VBox with tasks for each day
		this.daysBox = new VBox(1);

		//Enclose VBox in ScrollPane
		ScrollPane scrollPane = new ScrollPane(daysBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		dateStack.getChildren().addAll(scrollPane, datePicker);

		//Update display elements
		this.update(ViewMode.week);
	}

	/**
	 * Updates CalendarPane to show Tasks for the current PlanDate.
	 * @param viewMode ViewMode for the CalendarPane; options are ViewMode.day for one day or ViewMode.week for seven days.
	 */
	void update(ViewMode viewMode) {
		//Set UI guidelines
		this.currentViewMode = viewMode;

		final int daysShown;
		String titleString;
		LocalDate weekStartDate = planDate.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		
		//Get display info, title and starting date
		if (this.currentViewMode == ViewMode.day) {
			daysShown = 1;
		} else { //viewMode is week
			daysShown = 7;
		}
		
		titleString = "WEEK " + weekStartDate.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfWeekBasedYear()) + ", " + weekStartDate.getMonth().toString() + " " + weekStartDate.format(DateTimeFormatter.ofPattern("yyyy"));
	
		//Clear
		this.daysBox.getChildren().clear();

		//Set date button to display date
		this.weekButton.setText(titleString);

		//Set next and previous date buttons to skip specified number of days
		this.nextDateButton.setOnAction(e -> {
			planDate = new PlanDate(weekStartDate.plusDays(7));
			this.update(ViewMode.week);
		});
		this.previousDateButton.setOnAction(e -> {
			planDate = new PlanDate(weekStartDate.minusDays(7));
			this.update(ViewMode.week);
		});

		//Set daybuttons to days
		for (int i = 0; i < 7; i++) {
			LocalDate buttonDate = weekStartDate.plusDays(i);
			VBox buttonDisplay = new VBox();
			buttonDisplay.setAlignment(Pos.BASELINE_CENTER);
			Label buttonDayLabel = new Label(buttonDate.getDayOfWeek().toString().substring(0,3));
			Label buttonDateLabel = new Label(buttonDate.format(DateTimeFormatter.ofPattern("dd")));
			buttonDisplay.getChildren().addAll(buttonDayLabel, buttonDateLabel);
			this.dayButtons[i].setGraphic(buttonDisplay);
			this.dayButtons[i].setOnAction(e -> {
				planDate = new PlanDate(buttonDate);
				this.update(ViewMode.day);
			});
		}

		//List of overdue tasks
		ArrayList<MainTask> overdueTaskList = new ArrayList<MainTask>();

		//Get tasks from database
		SQLConnector.read("SELECT * FROM tasks WHERE Date < '" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND Completed = 'false'", rs -> {
			try {
				while (rs.next()) {
					//Parse string result from time query to LocalTime if it's not null
					LocalTime taskTime = null;
					if (rs.getString("Time") != null) {
						taskTime = LocalTime.parse(rs.getString("Time"));
					}

					//Create new main task
					MainTask newMainTask = new MainTask(rs.getInt("ID"), rs.getString("Name"), new PlanDate(LocalDate.parse(rs.getString("Date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))), taskTime, Boolean.parseBoolean(rs.getString("Completed")), Boolean.parseBoolean(rs.getString("Expanded")));
					
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

					//Add main task to tasklist
					overdueTaskList.add(newMainTask);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		});
		
		if (overdueTaskList.size() > 0) {
			//Box in grid for overdue tasks
			VBox overdueBox = new VBox();
			overdueBox.setPadding(new Insets(0, 3, 3, 3));
			overdueBox.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 1 1;");
			overdueBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			GridPane.setHgrow(overdueBox, Priority.ALWAYS);

			//Overdue label
			Label overdueLabel = new Label("OVERDUE");
			overdueLabel.setFont(new Font(overdueLabel.getFont().getName(), 12));

			//Labels for tasks with spacing of 3 between tasks and between following add task button
			VBox taskBox = new VBox();
			taskBox.setSpacing(3);
			taskBox.setPadding(new Insets(0, 0, 3, 0));

			//Add taskPanes of overdue tasks
			for (MainTask task : overdueTaskList) {
				taskBox.getChildren().add(task.taskPane);
			}

			//Add overdue box if there are overdue tasks
			overdueBox.getChildren().addAll(overdueLabel, taskBox);
			this.daysBox.getChildren().add(overdueBox);
		}
		
		LocalDate startDate = this.planDate.date;
		if (currentViewMode == ViewMode.week) {
			startDate = weekStartDate;
		}
		
		//Box in VBox for each day
		for (int i = 0; i < daysShown; i++) {
			PlanDate showDate = new PlanDate(startDate.plusDays(i));
			showDate.getTasks();
			VBox dayBox = showDate.createDayBox();
			showDate.updateTaskBox();
			this.daysBox.getChildren().add(dayBox);
		}
	}

	/**
	 * Extends HBox. Holds buttons corresponding to days and weeks in a month. Buttons update the Calendarpane to show that day or week.
	 */
	private class DatePicker extends HBox {
		CalendarPane calendarPane;
		LocalDate pickerDate;
		GridPane dateGrid;
		Label dateLabel;

		/**
		 * @param calendarPane Calendarpane linked to this DatePicker will update to show tasks for the picked date.
		 */
		DatePicker(CalendarPane calendarPane) {
			this.calendarPane = calendarPane;
			//Get copy of date
			this.pickerDate = calendarPane.planDate.date;

			//Create transparent border so taskpane is visible behind DatePicker, when clicked on transparent borders the DatePicker disappears
			this.setStyle("-fx-background-color: transparent;");
			this.setAlignment(Pos.TOP_CENTER);
			this.setOnMouseClicked(e -> { this.setVisible(false); });

			//Create inner pane to hold date buttons
			VBox innerPane = new VBox();
			this.getChildren().add(innerPane);
			innerPane.setStyle("-fx-background-color: white; -fx-border-color: grey;");
			innerPane.setMaxHeight(249);

			//DatePicker doesn't disapear when the innerPane is clicked
			innerPane.setOnMouseClicked(e -> { e.consume(); });

			//HBox for date title buttons
			HBox datePane = new HBox();

			//Month and year label
			this.dateLabel = new Label();
			this.dateLabel.setMinHeight(31);
			this.dateLabel.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(dateLabel, Priority.ALWAYS);
			this.dateLabel.setAlignment(Pos.BASELINE_CENTER);

			//Previous month button
			Button previousDayButton = new Button("<");
			previousDayButton.setMinSize(31, 31);
			previousDayButton.setMaxSize(31, 31);
			previousDayButton.setAlignment(Pos.BASELINE_CENTER);
			previousDayButton.setOnAction(e -> {
				this.pickerDate = this.pickerDate.minusMonths(1);
				this.Update();
			});

			//Today button
			Button todayButton = new Button("^");
			todayButton.setMinSize(31, 31);
			todayButton.setMaxSize(31, 31);
			todayButton.setAlignment(Pos.BASELINE_CENTER);
			todayButton.setOnAction(e -> {
				this.pickerDate = LocalDate.now();
				this.setVisible(false);
				planDate = new PlanDate(this.pickerDate);
				this.calendarPane.update(ViewMode.day);
			});

			//Next month button
			Button nextDayButton = new Button(">");
			nextDayButton.setMinSize(31, 31);
			nextDayButton.setMaxSize(31, 31);
			nextDayButton.setAlignment(Pos.BASELINE_CENTER);
			nextDayButton.setOnAction(e -> {
				this.pickerDate = this.pickerDate.plusMonths(1);
				this.Update();
			});

			//Add datebuttons to bar
			datePane.getChildren().addAll(previousDayButton, todayButton, this.dateLabel, nextDayButton);

			//Datepicker pane
			this.dateGrid = new GridPane();	

			//Add to inner pane
			innerPane.getChildren().addAll(datePane, this.dateGrid);

		}

		/**
		 * Updates dates on the DatePicker buttons to the month selected with the month navigation buttons on the DatePicker.
		 */
		void Update() {			
			//Clear dates
			this.dateGrid.getChildren().clear();

			//Day of week headers
			String[] dayTitleList = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
			for (int i = 0; i < dayTitleList.length; i++) {
				Label dayTitleLabel = new Label(dayTitleList[i]);
				dayTitleLabel.setAlignment(Pos.BASELINE_CENTER);
				dayTitleLabel.setMinSize(30, 30);
				this.dateGrid.add(dayTitleLabel, 1 + i, 0);
			}

			//Set date title
			this.dateLabel.setText(this.pickerDate.getMonth().toString() + " " + this.pickerDate.toString().substring(0, 4));

			//Initialize starting date
			LocalDate startDate = this.pickerDate.withDayOfMonth(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

			//Create buttons for 42 dates
			for (int i = 0; i < 42; i++) {
				//Initialize PlanDate from a number of days after starting date
				LocalDate followingDate = startDate.plusDays(i);

				//Create button for PlanDate
				Button dateButton = new Button(followingDate.toString().substring(8,10));
				dateButton.setAlignment(Pos.BASELINE_CENTER);
				dateButton.setMinSize(31, 31);
				dateButton.setMaxSize(31, 31);

				//Set button text black if it matches the currently displayed month, otherwise grey, and set border black if it matches the current date
				if (followingDate.toString().substring(0,7).equals(this.pickerDate.toString().substring(0,7))) {
					dateButton.setStyle("-fx-text-fill: black;");
					if (followingDate.equals(LocalDate.now())) {
						dateButton.setStyle("-fx-border-color: black; -fx-border-width: 1;");
					} else {
						dateButton.setStyle("-fx-border-color: transparent; -fx-border-width: 1;");
					}
				} else {
					dateButton.setStyle("-fx-text-fill: grey;");
				}

				//Hide DatePicker and display TaskPane for date when button is clicked
				dateButton.setOnAction(e -> {
					this.pickerDate = followingDate;
					this.setVisible(false);
					planDate = new PlanDate(this.pickerDate);
					this.calendarPane.update(ViewMode.day);
				});
				dateGrid.add(dateButton, 1 + i % 7, 1 + i / 7);

				//Add buttons for weeks
				if (i % 7 == 0) {
					Button weekButton = new Button("Week " + followingDate.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfWeekBasedYear()));
					weekButton.setMinSize(64, 31);
					weekButton.setMaxSize(64, 31);
					weekButton.setAlignment(Pos.CENTER_LEFT);
					//Hide DatePicker and display TaskPane for week when button is clicked
					weekButton.setOnAction(e -> {
						this.pickerDate = followingDate;
						this.setVisible(false);
						planDate = new PlanDate(this.pickerDate);
						this.calendarPane.update(ViewMode.week);
					});
					dateGrid.add(weekButton, 0, 1 + i / 7);
				}
			}
		}
	}
	
	public class EditTaskBox extends VBox {

		private MainTask task;
		private MainTask copyTask;
		
		private TextField taskNameField;
		private TextField taskTimeField;
		private Label dateLabel;		
		private VBox subTaskBox;

		/**
		 * Creates EditTaskBox to edit a MainTask and its SubTasks.
		 * @param mainTask MainTask to display
		 */
		public EditTaskBox() {
			HBox.setHgrow(this, Priority.ALWAYS);
			this.setMaxWidth(Double.MAX_VALUE);
			this.getStyleClass().add("hBoxTask");
			this.setStyle("-fx-border-color: grey; -fx-border-width: 1;");

			//Name title label
			Label taskNameLabel = new Label("Main Task:");
			taskNameLabel.setMinWidth(70);
			taskNameLabel.setMaxWidth(70);
			
			//Name field
			this.taskNameField = new TextField();
			HBox.setHgrow(this.taskNameField, Priority.ALWAYS);
			this.taskNameField.setMaxWidth(Double.MAX_VALUE);
			HBox.setMargin(this.taskNameField, new Insets(0, 3, 0, 0));
			
			//Confirm button confirms editing
			Button confirmButton = new Button();
			//confirmButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 1;");
			confirmButton.setMinSize(34, 33);
			confirmButton.setMaxSize(34, 33);
			Image confirmImage = new Image(getClass().getResourceAsStream("/icons/ButtonConfirm.png"));
			ImageView confirmImageView = new ImageView(confirmImage);
			confirmImageView.fitHeightProperty().bind(confirmButton.heightProperty());
			confirmImageView.fitWidthProperty().bind(confirmButton.widthProperty());
			confirmButton.setGraphic(confirmImageView);
			confirmButton.setOnAction(e -> {

				//Set name
				this.copyTask.setName(this.taskNameField.getText());
				
				//Set time
				LocalTime time = null;
				try {
					time = LocalTime.parse(this.taskTimeField.getText(), DateTimeFormatter.ofPattern("HHmm"));
				} catch (Exception ex) {
				} finally {
					this.copyTask.setTime(time);
				}
				
				//Set date
				this.copyTask.setPlanDate(new PlanDate(this.copyTask.getPlanDate().date));
				
				//Set SubTask names and remove deleted SubTasks
				for (Node node : this.subTaskBox.getChildren()) {
					if (node instanceof EditSubTaskBox) {
						if (((EditSubTaskBox)node).deleted) {
							((EditSubTaskBox)node).subTask.deleteSQL();
						} else {
							((EditSubTaskBox)node).subTask.setName(((EditSubTaskBox)node).subTaskNameField.getText());
						}
					}
				}
				
				//Copy changes to original task
				this.task = new MainTask(copyTask);
				for (SubTask subTask : this.task.getSubTaskList()) {
					subTask.updateSQL();
				}
				
				//Rebuild maintasklist to change order
				this.task.getPlanDate().updateTaskBox();
				
				//Update UI
				Main.calendarPane.update(Main.calendarPane.currentViewMode);
				this.setVisible(false);
				this.setManaged(false);
			});
			
			//Cancel button cancels editing
			Button cancelButton = new Button();
			//cancelButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 1;");
			cancelButton.setMinSize(34, 33);
			cancelButton.setMaxSize(34, 33);
			Image cancelImage = new Image(getClass().getResourceAsStream("/icons/ButtonCancel.png"));
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
			taskTimeLabel.setMinWidth(70);
			taskTimeLabel.setMaxWidth(70);
			
			//Time field
			this.taskTimeField = new TimeTextField();
			this.taskTimeField.setMinWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
			this.taskTimeField.setMaxWidth(new Text("00:00").getLayoutBounds().getWidth() + 14);
			HBox.setMargin(this.taskTimeField, new Insets(0, 10, 0, 0));
			
			//Date title label
			Label taskDateLabel = new Label("Date:");
			taskDateLabel.setMinWidth(40);
			taskDateLabel.setMaxWidth(40);
			
			//Previous date button
			Button previousDateButton = new Button();
			previousDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 0;");
			previousDateButton.setMinSize(34, 33);
			previousDateButton.setMaxSize(34, 33);
			Image previousImage = new Image(getClass().getResourceAsStream("/icons/ButtonPrevious.png"));
			ImageView previousImageView = new ImageView(previousImage);
			previousImageView.fitHeightProperty().bind(previousDateButton.heightProperty());
			previousImageView.fitWidthProperty().bind(previousDateButton.widthProperty());
			previousDateButton.setGraphic(previousImageView);
			previousDateButton.setOnAction(e -> {
				this.setNewDate(this.copyTask.getPlanDate().date.minusDays(1));
			});
			
			//Date label
			this.dateLabel = new Label();

			//Next date button
			Button nextDateButton = new Button();
			nextDateButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 0;");
			nextDateButton.setMinSize(34, 33);
			nextDateButton.setMaxSize(34, 33);
			Image nextImage = new Image(getClass().getResourceAsStream("/icons/ButtonNext.png"));
			ImageView nextImageView = new ImageView(nextImage);
			nextImageView.fitHeightProperty().bind(nextDateButton.heightProperty());
			nextImageView.fitWidthProperty().bind(nextDateButton.widthProperty());
			nextDateButton.setGraphic(nextImageView);
			nextDateButton.setOnAction(e -> {
				this.setNewDate(this.copyTask.getPlanDate().date.plusDays(1));
			});
			
			//Empty region
			Region emptyRegion2 = new Region();
			HBox.setHgrow(emptyRegion2, Priority.ALWAYS);
			emptyRegion2.setMaxWidth(Double.MAX_VALUE);
			
			//Add button adds new subtask
			Button addButton = new Button();
			//addButton.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 0 1;");
			addButton.setMinSize(34, 33);
			addButton.setMaxSize(34, 33);
			Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonPlus.png"));
			ImageView addImageView = new ImageView(addImage);
			addImageView.fitHeightProperty().bind(addButton.heightProperty());
			addImageView.fitWidthProperty().bind(addButton.widthProperty());
			addButton.setGraphic(addImageView);
			addButton.setOnAction(e -> {
				SubTask newTask = new SubTask(this.copyTask);
				this.copyTask.addToSubTaskList(newTask);
				this.subTaskBox.getChildren().add(new EditSubTaskBox(newTask));
			});
			
			//Delete button deletes maintask
			Button deleteButton = new Button();
			//deleteButton.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
			deleteButton.setMinSize(34, 33);
			deleteButton.setMaxSize(34, 33);
			deleteButton.setOnAction(e -> {
				this.delete();
			});
			Image deleteImage = new Image(getClass().getResourceAsStream("/icons/ButtonDelete.png"));
			ImageView deleteImageView = new ImageView(deleteImage);
			deleteImageView.fitHeightProperty().bind(deleteButton.heightProperty());
			deleteImageView.fitWidthProperty().bind(deleteButton.widthProperty());
			deleteButton.setGraphic(deleteImageView);
			
			//HBox for task time and date
			HBox taskTimeBox = new HBox();
			taskTimeBox.setPadding(new Insets(0,0,0,3));
			taskTimeBox.setAlignment(Pos.CENTER_LEFT);
			taskTimeBox.getChildren().addAll(taskTimeLabel, this.taskTimeField, taskDateLabel, previousDateButton, this.dateLabel, nextDateButton, emptyRegion2, addButton, deleteButton);
			
			//VBox for subtasks
			this.subTaskBox = new VBox();
			this.subTaskBox.setPadding(new Insets(0,0,0,34));
			this.getChildren().addAll(taskNameBox, taskTimeBox, this.subTaskBox);
		}
		
		public void setTask(MainTask task) {
			this.subTaskBox.getChildren().clear();
			this.task = task;
			this.copyTask = new MainTask(task);
			this.taskNameField.setText(this.copyTask.getName());
			if (this.copyTask.getTime() != null) {
				this.taskTimeField.setText(this.copyTask.getTime().format(DateTimeFormatter.ofPattern("HHmm")));
			} else {
				this.taskTimeField.setText("");
			}
			this.setNewDate(this.copyTask.getPlanDate().date);
			for (SubTask subTask : this.copyTask.getSubTaskList()) {
				this.subTaskBox.getChildren().add(new EditSubTaskBox(subTask));
			}
			this.setVisible(true);
			this.setManaged(true);
		}
		
		/**
		 * Set PlanDate of the MainTask to the date selected in the dateLabel.
		 * @param date
		 */
		public void setNewDate(LocalDate date) {
			this.copyTask.getPlanDate().date = date;
			this.dateLabel.setText(this.copyTask.getPlanDate().date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
		 * Sets TaskPane as invisible and deletes Task from SQLite database.
		 */
		public void delete() {
			this.task.deleteSQL();
			this.setVisible(false);
			this.setManaged(false);
			
			//Update UI
			Main.calendarPane.update(Main.calendarPane.currentViewMode);
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
				Image cancelImage = new Image(getClass().getResourceAsStream("/icons/ButtonDelete.png"));
				ImageView cancelImageView = new ImageView(cancelImage);
				cancelImageView.fitHeightProperty().bind(deleteButton.heightProperty());
				cancelImageView.fitWidthProperty().bind(deleteButton.widthProperty());
				deleteButton.setGraphic(cancelImageView);
				
				this.getChildren().addAll(subTaskNameLabel, subTaskNameField, deleteButton);
			}
			
			public void setSubTaskName() {
				this.subTask.setName(subTaskNameField.getText());
			}
		}
	}
}