package controllers.user;

import controllers.Database;
import controllers.DateSC;
import controllers.Roozh;
import controllers.login.LoginPageCtrl;
import controllers.switchSenceCtrl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Amanat;
import model.Book;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Controlleritem2 implements Initializable {

    @FXML
    private Button btn_amanatgiri;

    @FXML
    private Label item2_namebook;

    @FXML
    private Label item2_vaziyat;

    @FXML
    private Label item2_writername;

    @FXML
    private Label item2_bookID;

    static String userID;

    static String userName;
    //ست کردن اطلاعات کتاب درون item2(یک Hbox برای نمایش اطلاعات کتاب در صفحه ی bookList)
    public void set_items(Book book){
        this.item2_namebook.setText(book.getKtbName());
        this.item2_writername.setText(book.getKtbNevisande());
        this.item2_bookID.setText(String.valueOf(book.getKtbID()));
        this.item2_vaziyat.setText(book.getKtbVazeit());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //امانت گرفتن کتاب موردنظر در صورت موجود بودن
        btn_amanatgiri.setOnAction(e -> {
            Roozh roozh = new Roozh();
            Date date = new Date();
            //گرفتن تارخ امانت گیری به صورت میلادی
            SimpleDateFormat fr = new SimpleDateFormat("yyyy/MM/dd");
            String dateAmanatgiri = fr.format(date);
            String[] date1 = dateAmanatgiri.split("/");
            //تبدیل تاریخ میلادی به تاریخ شمسی
            roozh.gregorianToPersian(Integer.parseInt(date1[0]),Integer.parseInt(date1[1]),Integer.parseInt(date1[2]));
            dateAmanatgiri = String.valueOf(roozh);
            try {
                //ست کردن اطلاعات مربوط به امانت گیری کتاب و ارسال آن ها به دیتابیس امانت جهت ذخیره شدن
                Amanat amnt = new Amanat();
                amnt.setKtbID(item2_bookID.getText());
                amnt.setUsrID(LoginPageCtrl.get_id());
                amnt.setAmtDateGet(dateAmanatgiri);
                amnt.setAmtDateRtrn(DateSC.tamdidMohalat(dateAmanatgiri , 10));
                amnt.setMohlat(DateSC.tamdidMohalat(dateAmanatgiri,10));
                Database.amanatgiri(amnt);
                Stage stage = (Stage) btn_amanatgiri.getScene().getWindow();
                switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
                try {
                    switchSenceCtrl.sceneSwitchUserPage("BookList");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (ParseException | SQLException ioException) {
                ioException.printStackTrace();
            }
        });


    }
    public static void setUserID(String userid){
        userID = userid;
    }
    public static void setUserName(String username){
        userName = username;
    }
}
