/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.formsandcontrols;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.dbAccess.DbAccessException;
import imperfectsilentart.martinfowler.uiArchs.dbAccess.MonitoringStationDao;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Wrapper class for scrollable list of monitoring stations.
 */
public class MonitoringStationList{
	private static final Logger logger = Logger.getLogger(MonitoringStationList.class.getName());
	/*
	 * static members for singleton pattern
	 */
	private static MonitoringStationList instance = new MonitoringStationList();
	public static MonitoringStationList getInstance() {
		return MonitoringStationList.instance;
	}
	/*
	 * dynamic members
	 */
	final  VBox listContextNode = new VBox(2);
	final ListView<String> stationList = new ListView<String>();

	private MonitoringStationList() {
		/*
		 * Load data for scrollable list of monitoring stations.
		 */
		final MonitoringStationDao dao = new MonitoringStationDao();
		
		ObservableList<String> stationListData = null;
		try {
			stationListData = FXCollections.observableArrayList( dao.findAll() );
		} catch (DbAccessException e) {
			final ArrayList<String> data = new ArrayList<String>();
			data.add("- data access error -");
			stationListData = FXCollections.observableArrayList( data );
			logger.log(Level.SEVERE, "Failed to load data from DB.", e);
		}
		this.stationList.setItems(stationListData);
		
		/*
		 * Fill context node holding the GUI-section for the list.
		 */
		final Text columnNames = new Text("id | station_external_id | station_name | target_concentration");
		listContextNode.getChildren().addAll(columnNames, this.stationList);
	}
	
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.stationList.getSelectionModel().selectedItemProperty().addListener(changeListener);
	}
	public void integrateIntoPane(final Pane parentPane) {
		parentPane.getChildren().add(this.listContextNode);
	}
	
	/**
	 * Set new selection for monitoring station list.
	 * 
	 * @param newStationName    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void changeSelection(final String newStationName) {
		if(null == newStationName) {
			return;
		}
		this.stationList.getSelectionModel().select(newStationName);
		//FIXME handle incomplete partial names or missing hits
	}


}
