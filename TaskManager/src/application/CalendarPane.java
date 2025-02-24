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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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

		//Add elements
		this.getChildren().addAll(dateBar, dayButtonBar, dateStack);

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

		//Add to dateBar
		dateBar.getChildren().addAll(this.previousDateButton, todayButton, this.weekButton, this.datePickerButton, this.nextDateButton);
		dateBar.getStyleClass().add("hbox");
		
		//Buttons for day selection
		this.dayButtons = new Button[7];
		for (int i = 0; i < 7; i++) {
			this.dayButtons[i] = new Button();
			if (i < 6) {
				this.dayButtons[i].setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
			}
			this.dayButtons[i].setPadding(new Insets(0,0,0,0));
			this.dayButtons[i].setMinSize(34, 33);
			this.dayButtons[i].setMaxSize(Double.MAX_VALUE, 33);
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
		LocalDate startDate;
		
		//Get display info, title and starting date
		if (this.currentViewMode == ViewMode.day) {
			daysShown = 1;
//			startDate = planDate.date;
//			titleString = startDate.getDayOfWeek().toString() + " " + startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		} else { //viewMode is week
			daysShown = 7;
		}
		
		startDate = planDate.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		titleString = "WEEK " + startDate.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfWeekBasedYear()) + ", " + startDate.getMonth().toString() + " " + startDate.format(DateTimeFormatter.ofPattern("yyyy"));
	
		//Clear
		this.daysBox.getChildren().clear();

		//Set date button to display date
		this.weekButton.setText(titleString);

		//Set next and previous date buttons to skip specified number of days
		this.nextDateButton.setOnAction(e -> {
			planDate = new PlanDate(planDate.date.plusDays(7));
			this.update(ViewMode.week);
		});
		this.previousDateButton.setOnAction(e -> {
			planDate = new PlanDate(planDate.date.minusDays(7));
			this.update(ViewMode.week);
		});

		//Set daybuttons to days
		for (int i = 0; i < 7; i++) {
			LocalDate buttonDate = startDate.plusDays(i);
			this.dayButtons[i].setText(buttonDate.getDayOfWeek().toString().substring(0,3) + " " + buttonDate.format(DateTimeFormatter.ofPattern("dd")));
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
					MainTask newMainTask = new MainTask(rs.getInt("ID"), rs.getString("Name"), new PlanDate(LocalDate.parse(rs.getString("Date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))), taskTime, Boolean.parseBoolean(rs.getString("Completed")), Boolean.parseBoolean(rs.getString("Expanded")), Boolean.parseBoolean(rs.getString("Editmode")));
					
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
		
		//Box in VBox for each day
		for (int i1 = 0; i1 < daysShown; i1++) {
			PlanDate showDate = new PlanDate(this.planDate.date.plusDays(i1));
			showDate.getTasks();
			VBox dayBox = showDate.createDayBox();
			showDate.updateTaskBox();
			this.daysBox.getChildren().add(dayBox);
		}
		
		//Add task button
		Button addTaskButton = new Button();
		addTaskButton.setStyle("-fx-border-color: black; -fx-border-width: 1;");
		addTaskButton.setMinSize(35, 35);
		addTaskButton.setMaxSize(35, 35);
		Image addImage = new Image(getClass().getResourceAsStream("/icons/ButtonEdit.png"));
		ImageView addImageView = new ImageView(addImage);
		addImageView.fitHeightProperty().bind(addTaskButton.heightProperty());
		addImageView.fitWidthProperty().bind(addTaskButton.widthProperty());
		addTaskButton.setGraphic(addImageView);
		addTaskButton.setOnAction(e -> {
			MainTask newTask = new MainTask(this.planDate);
			this.planDate.taskBox.getChildren().add(newTask.taskPane);
			newTask.setExpanded(true);
			newTask.setEditMode(true);
			newTask.updateSQL();
			this.update(currentViewMode);
		});
		this.daysBox.getChildren().add(addTaskButton);

//		if (daysShown == 7) {
//			VBox weekBox = new VBox();
//			weekBox.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 1 1;");
//			this.daysGridPane.add(weekBox, 7 % rowLength, 7 / rowLength);
//		}
	}

	/**
	 * Extends HBox. Holds buttons corresponding to days and weeks in a month. Buttons update the Calendarpane to show that day or week.
	 */
	class DatePicker extends HBox {
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
}