package application;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.effects.JFXDepthManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mailServer.MailDispatcher;

public class MainController implements Initializable{
		
	private DatabaseHandler handler;
	
	@FXML
	private AnchorPane pane;
	
	@FXML
	private HBox bookSearch,memberSearch;
	
	@FXML
	private ListView<String> issueBookList;
	
	@FXML
	private TextField eBookId,eMemberId,eBookId2;
	
	@FXML
	private Text bookName,bookAuthor,bookStatus,memberName,memberContact;
	
	public MailDispatcher mailD;
	
	private Boolean bookIssueOrNot = false;
	
	public void AddBook(ActionEvent e) throws Exception {
		CreateUi("addBook.fxml", "Add Book");
	}
	
	public void ShowTable(ActionEvent e) throws Exception {
		CreateUi("table.fxml", "View Books");
	}
	
	public void AddMember(ActionEvent e) throws Exception {
		CreateUi("addMember.fxml", "Add Member");
	}
	

	public void ViewMember(ActionEvent e) throws Exception {
		CreateUi("ViewMembers.fxml", "View Members");
	}
	
	public void CreateUi(String address,String title) throws IOException{
		Stage primaryStage = new Stage(StageStyle.DECORATED); 
		
		Parent root = FXMLLoader.load(getClass().getResource(address));
		Scene scene = new Scene(root);
		
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(primaryStage.getOwner());
		
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
	} 
	
	
	public void issueOperation(ActionEvent e) throws Exception {
		if(!eBookId.getText().isEmpty() && !eMemberId.getText().isEmpty()){
			String bookid = eBookId.getText();
			String memberid = eMemberId.getText();
			
			if(!CheckAvailable(bookid)){
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Availability");
				alert.setHeaderText(null);
				alert.setContentText("Book Is Not Available");
				alert.showAndWait();
				return;
			}
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirm Issue Operation");
			alert.setHeaderText(null);
			alert.setContentText("Are You Sure You Want To Issue a Book "+bookName.getText()+" to "+memberName.getText()+"?");
			
			Optional<ButtonType> option = alert.showAndWait();
			if(option.get() == ButtonType.OK){
				String sql = "Insert Into BookIssue(bookId,memberId) Values("
						+ "'"+bookid+"',"
						+ "'"+memberid+"')";
				
				String sql1 = "update BOOKSHELF set isAvail = false where id = '"+bookid+"'";
				
				if(handler.executeAction(sql) && handler.executeAction(sql1)){
					Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
					alert1.setTitle("Success");
					alert1.setHeaderText(null);
					alert1.setContentText("Book Issue Complete");
					alert1.showAndWait();
					
					
					new Thread(()->{
						String sql2 = "select * from bookshelf where id = '"+bookid+"'";
						ResultSet res2 = handler.executeQuery(sql2);
						
						String sql3 = "select * from library_members where id = '"+memberid+"'";
						ResultSet res3 = handler.executeQuery(sql3);
						
						String sql4 = "select issueTime from bookissue where bookId = '"+bookid+"' and memberId = '"+memberid+"'";
						ResultSet res4 = handler.executeQuery(sql4);
						
						String book_name = null;
						String member_name = null;
						String email = null;
						String issueTime = null;
						
						try {
							while(res2.next()){
								book_name = res2.getString("title");
							}
							while(res3.next()){
								member_name = res3.getString("name");
								email = res3.getString("email");		
							}
							while(res4.next()){
								issueTime = res4.getString("issueTime");
							}
							
							MailDispatcher.send("looterapro@gmail.com", "eaypcmanongobyik", email, "Regarding"
											+ " Book Issue From MAD Library","Hi! "+member_name+"\nYou Recently issued"
											+ " book "+book_name+" at "+issueTime+""
											+ "\nEnjoy Reading");
							
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}).start();
					
				}else{
					Alert alert2 = new Alert(Alert.AlertType.ERROR);
					alert2.setTitle("Failed");
					alert2.setHeaderText(null);
					alert2.setContentText("Book Issue Failed");
					alert2.showAndWait();
				}
				
			}else{
				Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
				alert3.setTitle("Cancelled");
				alert3.setHeaderText(null);
				alert3.setContentText("Issue Operation Cancelled");
				alert3.showAndWait();
			}
			
		}
		clearFields();
	}
	
	private boolean CheckAvailable(String id) throws SQLException {
		
		String sql = "select isAvail from bookshelf where id = '"+id+"'";
		
		ResultSet res = handler.executeQuery(sql);
		
		if(res.next()){
			return res.getBoolean("isAvail");
		}
		
		return false;
	}

	public void clearFields(){
		eBookId.setText("");
		eMemberId.setText("");
		eBookId2.setText("");
	}
	
	public void checkBook(ActionEvent e) throws SQLException{
		String BookId = eBookId.getText();
		
		if(BookId.isEmpty()){
			bookName.setText("Book Name");
			bookAuthor.setText("Author");
			bookStatus.setText("Status");
			return;
		}
		
		Boolean haveRow = false;
		
		String sql = "select * from bookshelf where id = '"+BookId+"'";
		ResultSet res = handler.executeQuery(sql);
		
		
		while(res.next()){
			String book_name = res.getString("title");
			String book_author = res.getString("author");
			Boolean book_status = res.getBoolean("isAvail");
		
			bookName.setText(book_name);
			bookAuthor.setText(book_author);
		
			if(book_status == true){
				bookStatus.setText("Available");
			}else{
				bookStatus.setText("Not Available");
			}
			haveRow = true;
		}
		if(!haveRow){
			bookName.setText("This Is Not Available");
			bookAuthor.setText("");
			bookStatus.setText("");
		}
	}
	
	public void checkMember(ActionEvent e) throws SQLException{
		String MemberId = eMemberId.getText();
		
		if(MemberId.isEmpty()){
			memberName.setText("Member Name");
			memberContact.setText("Contact");
			return;
		}
		
		Boolean haveRow = false;
		
		String sql = "select * from library_members where id = '"+MemberId+"'";
		ResultSet res = handler.executeQuery(sql);
		
		
		while(res.next()){
			String member_name = res.getString("name");
			String member_contact = res.getString("mobile");	 
			
			memberName.setText(member_name);
			memberContact.setText(member_contact);
			haveRow = true;
		}
		if(!haveRow){
			memberName.setText("This Is Not Available");
			memberContact.setText("");
		}
	}
	
	
	public void checkIssuedBook(ActionEvent e) throws SQLException{
		
		bookIssueOrNot = false;
		
		String BookId = eBookId2.getText();
		
		if(BookId.isEmpty()){
			return;
		}
		
		ObservableList<String> list = FXCollections.observableArrayList();
		//Boolean haveRow = false;
		
		String sql = "select * from bookissue where bookId = '"+BookId+"'";
		ResultSet res = handler.executeQuery(sql);
		
		list.add("-------Book Issue InFormation-------");
		
		while(res.next()){
			String memberId = res.getString("memberid");
			
			@SuppressWarnings("deprecation")
			String issuedTime = res.getTimestamp("issueTime").toGMTString();
			
			int renewCount = res.getInt("renew_count");
			
			list.add("Issue Time :- "+issuedTime);
			list.add("Renew Count :- "+renewCount);
			
			String sql1 = "select * from bookshelf where id = '"+BookId+"'";
			ResultSet res1 = handler.executeQuery(sql1);
			
			list.add("-------Book InFormation-------");
			
			while(res1.next()){
				String bookName = res1.getString("title");
				String bookAuthor = res1.getString("author");
				String bookPublisher= res1.getString("publisher");
				
				list.add("Name :- "+bookName);
				list.add("ID :- "+BookId);
				list.add("Author :- "+bookAuthor);
				list.add("Publisher :- "+bookPublisher);
			}
			
			String sql2 = "select * from library_members where id = '"+memberId+"'";
			ResultSet res2 = handler.executeQuery(sql2);
			
			list.add("-------Member InFormation-------");
			
			while(res2.next()){
				String memberName = res2.getString("name");
				String memberMobile = res2.getString("mobile");
				String memberEmail= res2.getString("email");
				
				list.add("Name :- "+memberName);
				list.add("ID :- "+memberId);
				list.add("Mobile :- "+memberMobile);
				list.add("Email :- "+memberEmail);
				
				bookIssueOrNot = true;
			}
			
		}
		
		issueBookList.getItems().setAll(list);
		
	}
	
	public void BookSubmission(ActionEvent e){
		String BookID = eBookId2.getText();
		
		if(!bookIssueOrNot){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Book Selection");
			alert.setHeaderText(null);
			alert.setContentText("Book Selection Is Incorrect Or Not Done");
			alert.showAndWait();
			return;
		}
		
		Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
		alert1.setTitle("Confirm Submission Operation");
		alert1.setHeaderText(null);
		alert1.setContentText("Are You Sure You Want To Submit Book having "+BookID+" ID?");
		
		Optional<ButtonType> option = alert1.showAndWait();
		if(option.get() == ButtonType.OK){
			String Action1 = "Delete from bookissue where bookId = '"+BookID+"'";
			String Action2 = "Update bookshelf set isAvail = TRUE where id = '"+BookID+"'";
			
			if(handler.executeAction(Action1) && handler.executeAction(Action2)){
				Alert alert5 = new Alert(Alert.AlertType.INFORMATION);
				alert5.setTitle("Submission");
				alert5.setHeaderText(null);
				alert5.setContentText("Book Has Been Submitted");
				alert5.showAndWait();
			}else{
				Alert alert4 = new Alert(Alert.AlertType.ERROR);
				alert4.setTitle("Failed");
				alert4.setHeaderText(null);
				alert4.setContentText("Book Submission Failed");
				alert4.showAndWait();
			}
			
		}else{
			Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
			alert3.setTitle("Cancelled");
			alert3.setHeaderText(null);
			alert3.setContentText("Submission Operation Cancelled");
			alert3.showAndWait();
		}
	}
	
	public void BookRenew(ActionEvent e){
		String BookID = eBookId2.getText();
		
		if(!bookIssueOrNot){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Book Selection");
			alert.setHeaderText(null);
			alert.setContentText("Book Selection Is Incorrect Or Not Done");
			alert.showAndWait();
			return;
		}
		
		Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
		alert1.setTitle("Confirm Renew Operation");
		alert1.setHeaderText(null);
		alert1.setContentText("Are You Sure You Want To Renew Book having "+BookID+" ID?");
		
		Optional<ButtonType> option = alert1.showAndWait();
		if(option.get() == ButtonType.OK){
			String Action1 = "Update bookissue set issueTime = CURRENT_TIMESTAMP,renew_count = renew_count+1"
					+ "  where bookId = '"+BookID+"'";
			
			if(handler.executeAction(Action1)){
				Alert alert5 = new Alert(Alert.AlertType.INFORMATION);
				alert5.setTitle("Renew Process");
				alert5.setHeaderText(null);
				alert5.setContentText("Book Has Been Renewed");
				alert5.showAndWait();
			}else{
				Alert alert4 = new Alert(Alert.AlertType.ERROR);
				alert4.setTitle("Failed");
				alert4.setHeaderText(null);
				alert4.setContentText("Book Renew Process Failed");
				alert4.showAndWait();
			}
			
		}else{
			Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
			alert3.setTitle("Cancelled");
			alert3.setHeaderText(null);
			alert3.setContentText("Renew Operation Cancelled");
			alert3.showAndWait();
		}
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		JFXDepthManager.setDepth(bookSearch, 1);
		JFXDepthManager.setDepth(memberSearch, 1);
		handler = DatabaseHandler.getInstance();
	}
}
