package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Extends TextField. TextField that only allows number input with a max character length of 4 to input 24-hour time.
 */
public class TimeTextField extends TextField {

	private SimpleIntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", -1);
	private SimpleStringProperty restrict = new SimpleStringProperty(this, "restrict");
	
	/**
	 * Creates a TextField that only allows number input with a max character length of 4 to input 24-hour time.
	 */
	public TimeTextField() {

		//Maximum text length is 4
		this.maxLength.set(4);
		//Character restrictions in Regex syntax: only numbers
		this.restrict.set("[0-9]");
		//Add a change listener to change the text based on length and character restrictions
		textProperty().addListener(new ChangeListener<String>() {

			//Ignore input if true
			private boolean ignore;

			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				//If text is currently being changed by this method or the new text is null, ignore the input and do nothing
				if (ignore || newValue == null)
					return;
				//If length of the new text exceeds maxLength, trim it to maxLength
				if (maxLength.get() > -1 && newValue.length() > maxLength.get()) {
					ignore = true;
					setText(newValue.substring(0, maxLength.get()));
					ignore = false;
				}

				//If there are restrictions and the new text doesn't match the restriction, display the old text instead
				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					ignore = true;
					setText(oldValue);
					ignore = false;
				}
			}
		});
	}
}
