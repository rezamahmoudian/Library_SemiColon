package controllers.user;

import com.jfoenix.controls.JFXTextField;
import controllers.Database;
import controllers.login.LoginPageCtrl;
import controllers.switchSenceCtrl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;

import model.Amanat;
import model.Book;
import model.User;

public class HomePageCtrl implements Initializable {

    @FXML
    public VBox pnItems = null;

    @FXML
    private VBox pnItems_booklist = null;

    @FXML
    private Button btn_Signout;

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_BookList;

    @FXML
    private Label lbl_fullname;

    @FXML
    private Pane searchwith_bookname_pane;

    @FXML
    private JFXTextField ktbNametxt_search;

    @FXML
    private Pane searchwith_bookid_pane;

    @FXML
    private JFXTextField ktbIDtxt_search;

    @FXML
    private Pane searchwith_bookvaziyat_pane;

    @FXML
    private JFXTextField ktbwritertxt_search;

    @FXML
    private Pane searchwith_bookwriter_pane;


    String amanatgirande = LoginPageCtrl.get_id();
    //ست کردن اطلاعات نمایش داده شده توسط برنامه با کاربر واردشده
    public void setInfo() {
        User user = new User();
        try {
            Database.makeConnection();
            user = Database.set_home_items();
            Database.closeConnection();
            lbl_fullname.setText(user.getFirstName() + " " + user.getLastName());
        } catch (SQLException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controlleritem2.setUserName(lbl_fullname.getText());
        setInfo();
        createhomeList();
    }

    public void btn_Home_clicked(ActionEvent actionEvent) {
            createhomeList();
    }

    //ساختن و نمایش لیست کتاب های امانت گرفته شده توسط کاربر در صفحه ی home
        public void createhomeList() {
            pnItems.getChildren().clear();
            int i = 0;
            try {
                //اتصال به دیتابیس
                Database.makeConnection();
                //ساختن تیبل مورد نیاز در دیتابیس
                Database.create_book_table();
                String mysql = "SELECT * FROM book where ktbAmntGirande = " + "\"" + amanatgirande + "\" and status = '1'";
                showBooksHomepage(Database.createBookList(mysql));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Database.closeConnection();
        }

        //متد نمایش لیست کتابها در pnItem صفحه ی home
        public void showBooksHomepage(List<Book> books) {
            pnItems.getChildren().clear();
            Node[] nodes = new Node[1000];
            int i = 0;
            if(books != null) {
                try {
                    for (Book book : books) {
                        final int j = i;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/fxmls/user/Item.fxml"));
                        Parent root = (Parent) loader.load();
                        Controlleritem bookitem = loader.getController();
                        bookitem.setitemBook(book);
                        nodes[i] = root;
                        //دادن افکت به آیتم های جدول
                        nodes[i].setOnMouseEntered(event -> {
                            nodes[j].setStyle("-fx-background-color : #0A0E3F");
                        });
                        nodes[i].setOnMouseExited(event -> {
                            nodes[j].setStyle("-fx-background-color : #02030A");
                        });
                        pnItems.getChildren().add(nodes[i]);
                        i++;
                    }
                } catch (IOException | SQLException | ParseException e) {
                   e.printStackTrace();
                }
            }
        }
//اکشن دکمه های منوی سمت راست برنامه
    public void btn_BookList_clicked(ActionEvent actionEvent) throws ClassNotFoundException {
        Stage stage = (Stage) btn_BookList.getScene().getWindow();
        switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
        try {
            switchSenceCtrl.sceneSwitchUserPage("BookList");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void btn_info_clicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btn_info.getScene().getWindow();
        switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
        try {
            switchSenceCtrl.sceneSwitchUserPage("UserInfo");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void btn_Signout_clicked(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) btn_Signout.getScene().getWindow();
        switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
        try {
            switchSenceCtrl.sceneSwitchLogin("loginPage");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public void searchwithbookid(ActionEvent actionEvent) {
        searchwith_bookwriter_pane.setVisible(false);
        searchwith_bookname_pane.setVisible(false);
        searchwith_bookid_pane.setVisible(true);
    }

    public void searchwithbookwriter(ActionEvent actionEvent) {
        searchwith_bookid_pane.setVisible(false);
        searchwith_bookname_pane.setVisible(false);
        searchwith_bookwriter_pane.setVisible(true);
    }

    public void searchwithbookname(ActionEvent actionEvent) {
        searchwith_bookwriter_pane.setVisible(false);
        searchwith_bookid_pane.setVisible(false);
        searchwith_bookname_pane.setVisible(true);
    }

    public void searchwith_showallbooks(ActionEvent actionEvent) {
        searchwith_bookwriter_pane.setVisible(false);
        searchwith_bookid_pane.setVisible(false);
        searchwith_bookname_pane.setVisible(false);
        createhomeList();
    }

    //جستجو براساس نام کتاب
    public void search_bookname_btn_action(ActionEvent actionEvent) {
        String bookname = ktbNametxt_search.getText();
        try {
            pnItems.getChildren().clear();
            Node[] nodes = new Node[1000];
            Database.makeConnection();
            String mysql = "SELECT * FROM book where ktbName = "+ "\""+ bookname +"\" and ktbAmntGirande = " +"\"" + amanatgirande + "\" and  status = '1'";
            showBooksHomepage(Database.createBookList(mysql));
            Database.getStatement().close();
            Database.closeConnection();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void search_bookID_btn_actoin(ActionEvent actionEvent) {
        String bookid = ktbIDtxt_search.getText();
        try {
            pnItems.getChildren().clear();
            Node[] nodes = new Node[1000];
            Database.makeConnection();
            String mysql = "SELECT * FROM book where ktbID = "+ "\""+ bookid + "\" and ktbAmntGirande = " + "\"" + amanatgirande + "\" and status = '1'";
            showBooksHomepage(Database.createBookList(mysql));
            Database.getStatement().close();
            Database.closeConnection();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    //جستجو براساس نویسنده ی کتاب
    public void search_writer_btn_action(ActionEvent actionEvent) {
        String bookwriter = ktbwritertxt_search.getText();
        try {
            pnItems.getChildren().clear();
            Node[] nodes = new Node[1000];
            Database.makeConnection();
            String mysql = "SELECT * FROM book where ktbNevisandeh = "+ "\""+ bookwriter +"\" and ktbAmntGirande = " + "\"" + amanatgirande + "\" and status = '1'";
            showBooksHomepage(Database.createBookList(mysql));
            Database.getStatement().close();
            Database.closeConnection();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
