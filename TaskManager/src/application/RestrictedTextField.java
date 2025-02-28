package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Extends TextField. TextField that only allows input of certain characters and a certain maximum text length.
 */
public class RestrictedTextField extends TextField {

	private SimpleIntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", -1);
	private SimpleStringProperty restrict = new SimpleStringProperty(this, "restrict");
	
	/**
	 * Creates a TextField that only allows input of certain characters and a certain maximum text length.
	 * @param restrictionInput Regex string of characters to allow
	 * @param maxLengthInput Maximum length of text to allow
	 */
	public RestrictedTextField(String restrictionInput, int maxLengthInput) {

		//Set maximum text length
		this.maxLength.set(maxLengthInput);
		//Set character restrictions in Regex syntax
		this.restrict.set(restrictionInput);
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
