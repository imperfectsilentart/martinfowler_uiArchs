package imperfectsilentart.martinfowler.uiArchs.formsandcontrols;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * "Forms and Controls" version of assessment form from https://www.martinfowler.com/eaaDev/uiArchs.html .
 * 
 * @note    For simplicity DB table locking is done pessimistic.
 */
public class IceCreamAssessmentForm extends Application{


	@Override
	public void start(final Stage stage)
	{
		final HBox rootPane = new HBox(10);
		rootPane.setPadding(new Insets(7,7,7,7));
		rootPane.setAlignment(Pos.BASELINE_LEFT);
		
		/*
		 * Mutually register listener/event handler for changes of selected station either in the list or in the reading data sheet.
		 * 
		 * On change of selected station in list, the value of the text field "Station ID" is updated.
		 * On change of the text field "Station ID", the selected station in the list is updated as well.
		 * IMPORTANT: avoid circular listener update notifications by only changing "Station ID" text or list selection if the new value is different from the currently present value.
		 */
		MonitoringStationList.getInstance().registerStationChangeListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String oldStationName, String newStationName) {
						// don't propagate null
						// if there is no selection (because of wrong or partial station name) or selection disappears, the new value is null, which must be ignored.
						if( null == newStationName ) return;
						ReadingDataSheet.getInstance().changeReadingRecord(newStationName);
					}
				}
			);
		ReadingDataSheet.getInstance().registerStationChangeListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String oldStationName, String newStationName) {
						// don't propagate null
						if( null == newStationName ) return;
						MonitoringStationList.getInstance().changeSelection(newStationName);
					}
				}
			);
		


		/*
		 * Construct new scene consisting of two elements and link the scene to the stage.
		 * Leftmost element: a list of monitoring stations
		 * Rightmost element: fields & entries of an ice cream reading record
		 */
		MonitoringStationList.getInstance().integrateIntoPane(rootPane);
		ReadingDataSheet.getInstance().integrateIntoPane(rootPane);
		HBox.setHgrow(ReadingDataSheet.getInstance().getDataSheetPane(), Priority.ALWAYS);
		stage.setScene(new Scene(rootPane, 470, 200));
		// set stage attributes
		stage.setTitle("Assessment Record (\"Forms and Controls\" version)");
		stage.setResizable(true);  
		stage.centerOnScreen();
		// make stage visible
		stage.show();
	}

	public static void main(final String[] args){
		launch(args);
	}

}