package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddBookController implements Initializable{
	@FXML
	private TextField bTitle,bId,bAuthor,bPublisher;
	@FXML
	private Button saveButton,cancelButton;
	
	@FXML
	private AnchorPane root;
	
	DatabaseHandler handler;
	
	public void addBook(ActionEvent e) {
		String id = bId.getText().replace(" ", "_").trim();
		String title = bTitle.getText().replace(" ", "_").trim();
		String author = bAuthor.getText().replace(" ", "_").trim();
		String publisher = bPublisher.getText().replace(" ", "_").trim();

		String sql = "Insert Into bookshelf "
				+ "Values('"+id+"',"
						+ "'"+title+"',"
						+ "'"+author+"',"
						+ "'"+publisher+"'"
						+ ","+true+")";
		
		//System.out.println(sql);
		
		if(id.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("You Missed Some Field Empty !!");
			alert.showAndWait();
			return;
		}
		
		if(handler.executeAction(sql)){
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText("Succesfully Inserted");
			alert.showAndWait();
			
			bId.setText("");
			bAuthor.setText("");
			bPublisher.setText("");
			bTitle.setText("");
			
		}else{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Insertion Failed");
			alert.showAndWait();
		}
	}
	
	public void cancel(ActionEvent e) {
		Stage stage = (Stage) root.getScene().getWindow();
		stage.close();
	}

	public void initialize(URL location, ResourceBundle resources) {
		handler = DatabaseHandler.getInstance();
	}
}
