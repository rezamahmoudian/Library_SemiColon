package controllers.user;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.Amanat;
import model.Book;
import java.net.URL;
import java.util.ResourceBundle;

public class Controlleritem implements Initializable {
    ///////////////////////////////////////////////
    //item
    @FXML
    private Button btn_odat;

    @FXML
    private Label item_dateAmntgiri;

    @FXML
    private Label item_mohlat;

    @FXML
    private Label item_writername;

    @FXML
    private Label item_namebook;

    @FXML
    private HBox itemC;

    @FXML
    private Label item_bookID;

    static String bookID = null;

    public void setitemBook(Book book){
        item_bookID.setText(String.valueOf(book.getKtbID()));
        item_mohlat.setText(String.valueOf("10"));
        item_namebook.setText(book.getKtbName());
        item_writername.setText(book.getKtbNevisande());
    }
    public void setItemsAmnt(Amanat amnt){
        item_dateAmntgiri.setText(amnt.getAmtDateGet());
        item_mohlat.setText(amnt.getAmtDateRtrn());
    }


    public void setname(String a){
        item_namebook.setText(a);
    }
    public void setwriter(String a){
        item_writername.setText(a);
    }
    public void setdate(String a){
        item_dateAmntgiri.setText(a);
    }
    public void setmohlat(String a){
        item_mohlat.setText(a);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void set_bookID(String a) {
        item_bookID.setText(a);
    }
}