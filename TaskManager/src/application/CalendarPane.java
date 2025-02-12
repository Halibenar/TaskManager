package application;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import application.Main.ViewMode;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
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
	GridPane daysGridPane;
	Button dateButton;
	Button previousDateButton;
	Button nextDateButton;
	ViewMode currentViewMode;

	public CalendarPane() {
		//Task display layer
		//Date panel
		//HBox for buttons and viewdate
		HBox dateBar = new HBox();
		dateBar.setAlignment(Pos.CENTER_LEFT);
		dateBar.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		
		//Stackpane
		StackPane dateStack = new StackPane();
		VBox.setVgrow(dateStack, Priority.ALWAYS);
		dateStack.setAlignment(Pos.TOP_LEFT);

		//DatePicker layer, set invisible at start
		DatePicker datePicker = new DatePicker(this);
		datePicker.setVisible(false);

		//Add elements
		this.getChildren().addAll(dateBar, dateStack);

		//Title button size
		int[] titleButtonSize = {25, 25};
		int fontSize = 17;

		//Central date button, sets datePicker as visible in dateStack
		this.dateButton = new Button();
		Font titleFont = new Font(dateButton.getFont().getName(), fontSize);
		this.dateButton.setFont(titleFont);
		this.dateButton.setPrefHeight(titleButtonSize[0]);
		this.dateButton.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(this.dateButton, Priority.ALWAYS);
		this.dateButton.setAlignment(Pos.BASELINE_CENTER);
		this.dateButton.setOnAction(e -> {
			datePicker.pickerDate = planDate.date;
			datePicker.Update();
			datePicker.setVisible(!datePicker.isVisible());
		});

		//Previous date button
		this.previousDateButton = new Button("<");
		this.previousDateButton.setFont(titleFont);
		this.previousDateButton.setMinSize(titleButtonSize[0], titleButtonSize[1]);
		this.previousDateButton.setAlignment(Pos.BASELINE_CENTER);

		//Next date button
		this.nextDateButton = new Button(">");
		this.nextDateButton.setFont(titleFont);
		this.nextDateButton.setMinSize(titleButtonSize[0], titleButtonSize[1]);
		this.nextDateButton.setAlignment(Pos.BASELINE_CENTER);

		//Today button
		Button todayButton = new Button("^");
		todayButton.setFont(titleFont);
		todayButton.setMinSize(titleButtonSize[0], titleButtonSize[1]);
		todayButton.setAlignment(Pos.BASELINE_CENTER);
		todayButton.setOnAction(e -> {
			planDate = new PlanDate(LocalDate.now());
			this.update(this.currentViewMode);
		});

		//Add to dateBar
		dateBar.getChildren().addAll(this.previousDateButton, todayButton, this.dateButton, this.nextDateButton);
		dateBar.getStyleClass().add("hbox");

		//GridPane with Panes for a day, week or month
		this.daysGridPane = new GridPane();

		//Enclose GridPane in ScrollPane
		ScrollPane scrollPane = new ScrollPane(daysGridPane);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		//scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		dateStack.getChildren().addAll(scrollPane, datePicker);

		//Update display elements
		this.update(ViewMode.day);
	}

	/**
	 * Updates CalendarPane to show Tasks for the current PlanDate.
	 * @param viewMode ViewMode for the CalendarPane; options are ViewMode.day for one day or ViewMode.week for seven days.
	 */
	void update(ViewMode viewMode) {
		//Set UI guidelines
		this.currentViewMode = viewMode;

		final int daysShown;
		int rowLength;
		String titleString;
		LocalDate startDate;
		
		//Get display info, title and starting date
		if (this.currentViewMode == ViewMode.day) {
			daysShown = 1;
			rowLength = 1;
			startDate = planDate.date;
			titleString = startDate.getDayOfWeek().toString() + " " + startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		} else { //viewMode is week
			daysShown = 7;
			rowLength = 2;
			startDate = planDate.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			titleString = "WEEK " + startDate.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfWeekBasedYear()) + ", " + startDate.getMonth().toString() + " " + startDate.format(DateTimeFormatter.ofPattern("yyyy"));
		}

		//Clear
		this.daysGridPane.getChildren().clear();
		this.daysGridPane.getColumnConstraints().clear();
		
		//Set column constraints
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(100d / rowLength);
		for (int i = 0; i < rowLength; i++) {
			this.daysGridPane.getColumnConstraints().add(cc);
		}

		//Set date button to display date
		this.dateButton.setText(titleString);

		//Set next and previous date buttons to skip specified number of days
		this.nextDateButton.setOnAction(e -> {
			planDate = new PlanDate(planDate.date.plusDays(daysShown));
			this.update(this.currentViewMode);
		});
		this.previousDateButton.setOnAction(e -> {
			planDate = new PlanDate(planDate.date.minusDays(daysShown));
			this.update(this.currentViewMode);
		});
		
		//Box in grid for each day
		for (int i = 0; i < daysShown; i++) {
			PlanDate showDate = new PlanDate(startDate.plusDays(i));
			showDate.getTasks();
			VBox dayBox = showDate.createDayBox();
			showDate.updateTaskBox();
			this.daysGridPane.add(dayBox, i % rowLength, i / rowLength);
		}

		if (daysShown == 7) {
			VBox weekBox = new VBox();
			weekBox.setStyle("-fx-border-color: grey; -fx-border-width: 1 0 1 1;");
			this.daysGridPane.add(weekBox, 7 % rowLength, 7 / rowLength);
		}
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