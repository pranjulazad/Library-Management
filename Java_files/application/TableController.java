package application;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableController implements Initializable{
	
	@FXML
	private TableView<Book> tableView;
	@FXML
	private TableColumn<Book, String> tableId;
	@FXML
	private TableColumn<Book, String> tableAuthor;
	@FXML
	private TableColumn<Book, String> tableTitle;
	@FXML
	private TableColumn<Book, String> tablePublisher;
	@FXML
	private TableColumn<Book, Boolean> tableAvailability;
	
	
	DatabaseHandler handler;
	ObservableList<Book> list = FXCollections.observableArrayList();
	
	public void initialize(URL location, ResourceBundle resources) {
		try {
			InitCol();
			handler = DatabaseHandler.getInstance();
			String sql = "select * from bookshelf";
			ResultSet set = handler.executeQuery(sql);
			
			while(set.next()){
				String Title = set.getString("title");
				String Author = set.getString("author");
				String Id = set.getString("id");
				String Publisher = set.getString("publisher");
				Boolean Avail  = set.getBoolean("isAvail");
				
				list.add(new Book(Title,Author,Id,Publisher,Avail));
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
		tableAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
		tableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tablePublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
		tableAvailability.setCellValueFactory(new PropertyValueFactory<>("avail"));
	}

	public static class Book{
		
		private final SimpleStringProperty id;
		private final SimpleStringProperty title;
		private final SimpleStringProperty author;
		private final SimpleStringProperty publisher;
		private final SimpleBooleanProperty avail;
		
		public Book(String title, String author, String id, String publisher, Boolean avail) {
			this.title = new SimpleStringProperty(title);
			this.author = new SimpleStringProperty(author);
			this.id = new SimpleStringProperty(id);
			this.publisher = new SimpleStringProperty(publisher);
			this.avail = new SimpleBooleanProperty(avail);
		}

		public String getTitle() {
			return title.get();
		}

		public String getAuthor() {
			return author.get();
		}

		public String getId() {
			return id.get();
		}

		public String getPublisher() {
			return publisher.get();
		}

		public Boolean getAvail() {
			return avail.get();
		}
		
	}
	
}
