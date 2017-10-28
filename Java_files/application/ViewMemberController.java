package application;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewMemberController implements Initializable{
	
	@FXML
	private TableView<Members> tableView;
	@FXML
	private TableColumn<Members, String> tableId;
	@FXML
	private TableColumn<Members, String> tableName;
	@FXML
	private TableColumn<Members, String> tableMobile;
	@FXML
	private TableColumn<Members, String> tableEmail;
	
	DatabaseHandler handler;
	ObservableList<Members> list = FXCollections.observableArrayList();
	
	public void initialize(URL location, ResourceBundle resources) {
		try {
			InitCol();
			handler = DatabaseHandler.getInstance();
			String sql = "select * from library_members";
			ResultSet set = handler.executeQuery(sql);
			
			while(set.next()){
				String Name = set.getString("name");
				String Mobile = set.getString("mobile");
				String Id = set.getString("id");
				String Email = set.getString("email");
				
				list.add(new Members(Name,Mobile,Id,Email));
			}
		} catch (SQLException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Failed To Retrieve Data");
			alert.showAndWait();
			return;
		}
		
		tableView.getItems().addAll(list);
	}
	
	private void InitCol() {
		tableId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableMobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));
		tableEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
	}

	public static class Members{
		
		private final SimpleStringProperty id;
		private final SimpleStringProperty name;
		private final SimpleStringProperty mobile;
		private final SimpleStringProperty email;
		
		public Members(String name, String mobile, String id, String email) {
			this.name = new SimpleStringProperty(name);
			this.mobile = new SimpleStringProperty(mobile);
			this.id = new SimpleStringProperty(id);
			this.email = new SimpleStringProperty(email);
		}

		public String getId() {
			return id.get();
		}

		public String getName() {
			return name.get();
		}

		public String getMobile() {
			return mobile.get();
		}

		public String getEmail() {
			return email.get();
		}

		
		
	}
	
}
