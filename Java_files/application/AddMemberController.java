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
import mailServer.MailDispatcher;

public class AddMemberController implements Initializable{
	@FXML
	private TextField mName,mId,mMobile,mEmail;
	@FXML
	private Button saveButton,cancelButton;
	
	@FXML
	private AnchorPane root;
	
	public DatabaseHandler handler;
	
	public void addMember(ActionEvent e) {
		String id = mId.getText().replace(" ", "_").trim();
		String name = mName.getText().replace(" ", "_").trim();
		String mobile = mMobile.getText().replace(" ", "_").trim();
		String email = mEmail.getText().replace(" ", "_").trim();

		String sql = "Insert Into LIBRARY_MEMBERS "
						+ "Values('"+id+"',"
							   + "'"+name+"',"
							   + "'"+mobile+"',"
							   + "'"+email+"')";
		
		
		if(id.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty()){
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
			
			new Thread(new Runnable() {
				@Override
				public void run() {	
					MailDispatcher.send("looterapro@gmail.com", "eaypcmanongobyik", email, "Mad Library", ""
							+ "You are now the Member of Mad Library");
				}
			}).start();
			
			mId.setText("");
			mName.setText("");
			mMobile.setText("");
			mEmail.setText("");
			
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
