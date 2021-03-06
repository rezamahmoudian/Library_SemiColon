package controllers.user;


import controllers.*;
import controllers.login.LoginPageCtrl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Amanat;
import model.Book;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Controlleritem implements Initializable {

    @FXML
    private Label item_dateAmntgiri;

    @FXML
    private Label item_mohlat;

    @FXML
    private Label item_writername;

    @FXML
    private Label item_namebook;

    @FXML
    private Button btn_tamdid;

    @FXML
    private Button btn_odatBook;

    @FXML
    private HBox itemC;

    @FXML
    private Label item_bookID;
    //ست کردن اطلاعات کتاب درون item(یک Hbox برای نمایش اطلاعات کتاب های امانت گرفته شده توسط کاربر در صفحه ی home)
    public void setitemBook(Book book) throws SQLException, ParseException {
        item_bookID.setText(String.valueOf(book.getKtbID()));
        item_mohlat.setText(String.valueOf("10"));
        item_namebook.setText(book.getKtbName());
        item_writername.setText(book.getKtbNevisande());
        //گرفتن تاریخ امانتگیری و مهلت از طریق متد مربوطه در دیتابیس
        item_dateAmntgiri.setText(Database.getAmanatgiriDate(book.getAmtTarakoneshID()));
        item_mohlat.setText(Database.getMohlatTahvil(book.getAmtTarakoneshID()));

        //عدم نمایش دکمه ی تمدید درصورتی که کاربر قبلا درخواست تمدید داده باشد
        try {
            String amntid = Database.getAmntTarakoneshID(book.getKtbID());
            if(Database.getDarkhastTamdidStatus(amntid).equals("1")){
                btn_tamdid.setVisible(false);
            }
            if( Database.getEmkanTamdidStatus(amntid).equals("0")){
                btn_tamdid.setVisible(false);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //تمدید کتاب مورد نظر
        btn_tamdid.setOnAction(e -> {
            try {
                Database.updateAmanat(Database.getAmntTarakoneshID(item_bookID.getText()) , 1);
                System.out.println("btntamdid bookid" +item_bookID.getText());
          //      alert.informationAlert("درخواست تمدید ثبت شد");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //رفرش کردن لیست
            Stage stage = (Stage) btn_tamdid.getScene().getWindow();
            switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
            try {
                switchSenceCtrl.sceneSwitchUserPage("Home");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        //برگرداندن کتاب موردنظر به کتابخانه
        btn_odatBook.setOnAction(e -> {
            try {
                Date date = new Date();
                SimpleDateFormat fr = new SimpleDateFormat("yyyy/MM/dd");
                String[] dates = fr.format(date).split("/");
                Roozh roozh = new Roozh();
                roozh.gregorianToPersian(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]),Integer.parseInt(dates[2]));

                Database.updateAmanat(Database.getAmntTarakoneshID(item_bookID.getText()), String.valueOf(roozh) ,"عودت" );
                Database.updateBookAmntStatus("" , "" , "موجود" , item_bookID.getText());
                System.out.println("btn odat bookid " +item_bookID.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Stage stage = (Stage) btn_odatBook.getScene().getWindow();
            switchSenceCtrl switchSenceCtrl = new switchSenceCtrl(stage);
            try {
                switchSenceCtrl.sceneSwitchUserPage("Home");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }
}
