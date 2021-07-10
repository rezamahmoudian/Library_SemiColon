package controllers;


import com.mysql.cj.protocol.Resultset;
import controllers.login.LoginPageCtrl;
import javafx.scene.control.Alert;
import model.Admin;
import model.Amanat;
import model.Book;
import model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class Database {
    private static Connection connection = null;

    public static Statement getStatement() {
        return statement;
    }

    private static Statement statement = null;

    private Database() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load MySQL Driver");
        }
    }

    public static void makeConnection() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "1234");
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean login_user(String txtusername, String txtpassword) {
        boolean login = false;
        try {
            makeConnection();
            String mysql = "SELECT * FROM user";

            ResultSet result = Database.getStatement().executeQuery(mysql);
            while (result.next()) {
                //   String ID = result.getString("id");
                String username = result.getString("userName");
                String password = result.getString("usrPass");
                String name = result.getString("usrFName");
                String family = result.getString("usrLName");
                if (txtusername.compareTo(username) == 0 && txtpassword.compareTo(password) == 0) {
                    // ست کردن اطلاعات در کلاس person مطابق با اطلاعات کاربر
                    login = true;
                    String id = result.getString("usrID");
                    LoginPageCtrl.set_id(id);
                    System.out.println("id geted from databace =" + id);
                    break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return login;
    }

    public static void registerUser(User p) {
        try {
            //ساختن تیبل مورد نیاز در دیتابیس
            String crtbl = "CREATE TABLE  IF NOT EXISTS user ( `usrID` VARCHAR(11) NOT NULL , `usrFName` varchar(80) NOT NULL , `usrLName` varchar(80) NOT NULL , `userName` varchar(40) NOT NULL , `usrPass` varchar(45) NOT NULL ,`usrCodeMeli` varchar(11) ,`usrBookList` TEXT , PRIMARY KEY (`usrID`) ,UNIQUE (`userName`))";
            getStatement().execute(crtbl);
            //مشکل(ارور) در ثبت نام
        } catch (Exception ex) {
            System.out.println(ex);
            Alert alert2 = new Alert(Alert.AlertType.WARNING);
            alert2.setTitle("ERROR");
            alert2.setHeaderText(null);
            alert2.setContentText("Registration Failed pleaes TryAgain");
            alert2.showAndWait();
        }
        //ارسال اطلاعات ثبت نام به دیتابیس

        Random rnd = new Random();
        String id = String.valueOf(rnd.nextInt(9000) + 1000);
        System.out.println("id = " + id);
        String setinfo = "INSERT INTO user (usrID ,usrFName, usrLName , userName , usrPass)  values ('%s','%s','%s','%s','%s')";
        setinfo = String.format(setinfo, p.getID(), p.getFirstName(), p.getLastName(), p.getUserName(), p.getPassword());
        System.out.println(setinfo);

        try {
            getStatement().execute(setinfo);
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Registration");
            alert2.setHeaderText(null);
            alert2.setContentText("Successfully Registration!\nyour id is : " + p.getID());
            alert2.showAndWait();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        closeConnection();
    }

    public static User getItemUserDB(String userID) throws SQLException {
        makeConnection();
        String sql = String.format("Select * FROM user WHERE usrID = '%s' ", userID);
        ResultSet resultSet = getStatement().executeQuery(sql);
        User user = new User();
        resultSet.next();
        user.setID(String.valueOf(resultSet.getInt("usrID")));
        user.setUserName(resultSet.getString("userName"));
        user.setPassword(resultSet.getString("usrPass"));
        user.setFirstName(resultSet.getString("usrFName"));
        user.setLastName(resultSet.getString("usrLName"));
        user.setCodeMeli(resultSet.getString("usrCodeMeli"));
        closeConnection();

        return user;
    }

    public static void updateUser(User user, String usrIDTXT) throws SQLException {
        makeConnection();
        String sql = String.format("UPDATE user SET usrFName = '%s', usrLName = '%s' , usrPass = '%s' , userName = '%s' , usrCodeMeli = '%s'" +
                " WHERE usrID = '%s'", user.getFirstName(), user.getLastName(), user.getPassword(), user.getUserName(), user.getCodeMeli(), usrIDTXT);
        getStatement().executeUpdate(sql);
        closeConnection();
    }


    // گرفتن اطلاعات مدیران کتابخانه از جدول ادمین موجود در دیتابیس
    public static List<Admin> getInfoAdmin() throws SQLException {
        List<Admin> admins = new ArrayList<>();
        makeConnection();
        ResultSet resultSet = Database.getStatement().executeQuery("SELECT * FROM admin");
        Admin admin;
        while (resultSet.next()) {
            admin = new Admin();
            admin.setID(resultSet.getString("admID"));
            admin.setUserName(resultSet.getString("admUserName"));
            admin.setPassword(resultSet.getString("admPass"));
            admin.setFirstName(resultSet.getString("admFName"));
            admin.setLastName(resultSet.getString("admLName"));
            admin.setCodeMeli(resultSet.getString("admCodeMeli"));
            admins.add(admin);
        }
        Database.closeConnection();
        return admins;
    }

    public static User set_home_items() {
        String id = LoginPageCtrl.get_id();
        System.out.println("id in dabase class = " + id);
        User user = new User();
        try {
            String mysql = "SELECT usrFName, usrLName , userName , usrPass FROM user WHERE usrID =" + id;
            System.out.println("mysql=" + mysql);
            ResultSet result = Database.statement.executeQuery(mysql);
            result.next();
            //   String ID = result.getString("id");
            String username = result.getString("userName");
            String password = result.getString("usrPass");
            String name = result.getString("usrFName");
            String family = result.getString("usrLName");
            String fullname = (name + " " + family);
            System.out.println("fullname =" + fullname);
            user.setFirstName(name);
            user.setLastName(family);
            user.setID(id);
            user.setUserName(username);
        } catch (Exception e) {
            System.out.println(e);
        }
        return user;
    }

    ///////////////////////////////////<<BOOK>>/////////////////////////////////////

    public static void create_book_table() {
        try {
            //ساختن تیبل مورد نیاز در دیتابیس
            String crtbl = "CREATE TABLE  IF NOT EXISTS `book` ( `ktbID` INT NOT NULL , `ktbEhdaKonande` varchar(80) NOT NULL , `ktbName` varchar(80) NOT NULL ,  `ktbNevisandeh` varchar(80) NOT NULL ,  `ktbTedad` int NOT NULL ,  `ktbVazeiat` varchar(10) NOT NULL , `amtTarakoneshID` varchar(11) , `ktbEhdaDate` TEXT NOT NULL , `ktbAmntGirande` TEXT , PRIMARY KEY (`ktbID`))";
            Database.statement.execute(crtbl);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void add_book(Book book) throws SQLException {
        Date date = new Date();
        SimpleDateFormat fr = new SimpleDateFormat("yyyy/MM/dd");
        String dateformat = fr.format(date);

        //delete date_ms later if dont use
        String addbook = "INSERT INTO book (ktbName, ktbNevisandeh , ktbEhdaKonande , ktbEhdaDate , ktbVazeiat ,ktbTedad , ktbAmntGirande , ktbID)  values ('%s','%s','%s','%s','%s','%s','%s','%d')";

        Random rnd = new Random();
        int book_id = rnd.nextInt(9000) + 1000;
        System.out.println("bookid = " + book_id);
        System.out.println("namebook = " + book.getKtbName());
        //int book_id = Integer.parseInt(String.valueOf(state.executeQuery(getid)));
        addbook = String.format(addbook, book.getKtbName(), book.getKtbNevisande(), book.getKtbEhdaKonandeh(), dateformat, "موجود", 1, "", book_id);
        System.out.println(addbook);
        Database.getStatement().execute(addbook);
        Database.closeConnection();
    }


    public static List<Book> create_bookList(String sql) {

        List<Book> booklist1 = null;
        try {
            System.out.println(sql);
            ResultSet result = Database.statement.executeQuery(sql);
            int i = 0;
            booklist1 = new ArrayList<>();
            while (result.next()) {
                int bookid = result.getInt("ktbID");
                String bookname = result.getString("ktbName");
                String bookwriter = result.getString("ktbNevisandeh");
                String ehdakonande = result.getString("ktbEhdaKonande");
                String vaziyat = result.getString("ktbVazeiat");

                Book book = new Book();
                book.setKtbName(bookname);
                book.setKtbNevisande(bookwriter);
                book.setKtbID(String.valueOf(bookid));
                book.setKtbEhdaKonandeh(ehdakonande);
                book.setKtbTedad("1");
                book.setKtbVazeit(vaziyat);
                booklist1.add(book);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(booklist1);
        return booklist1;
    }


    public static void createBook(Book book) throws SQLException {
        makeConnection();
        Date date = new Date();
        SimpleDateFormat fr = new SimpleDateFormat("yyyy/MM/dd");
        String dateformat = fr.format(date);
        String sql = String.format("INSERT INTO book VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')", book.getKtbID(), book.getKtbName(),
                book.getKtbNevisande(), book.getKtbEhdaKonandeh(), Integer.valueOf(book.getKtbTedad()), book.getKtbVazeit(),
                dateformat, book.getAmtTarakoneshID(), "");
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public static List<Book> readBooksDB() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        Database.makeConnection();
        ResultSet resultSet = Database.getStatement().executeQuery("SELECT * FROM book");
        Book book;
        while (resultSet.next()) {
            book = new Book();
            book.setKtbID(String.valueOf(resultSet.getInt("ktbID")));
            book.setKtbName(resultSet.getString("ktbName"));
            book.setKtbNevisande(resultSet.getString("ktbNevisandeh"));
            book.setKtbTedad(resultSet.getString("ktbTedad"));
            book.setKtbVazeit(resultSet.getString("ktbVazeiat"));
            bookList.add(book);
        }

        Database.closeConnection();
        return bookList;
    }


    public static List<Book> readBooksDB(String txet, String lable) throws SQLException {
        List<Book> bookList = new ArrayList<>();
        Database.makeConnection();
        String sql;
        if (lable.equals("name")) {
            sql = String.format("SELECT * FROM book WHERE ktbName = '%s'", txet);
        } else {
            sql = String.format("SELECT * FROM book WHERE ktbID = '%s'", txet);
        }

        ResultSet resultSet = Database.getStatement().executeQuery(sql);
        Book book;
        while (resultSet.next()) {
            book = new Book();
            book.setKtbID(String.valueOf(resultSet.getInt("ktbID")));
            book.setKtbName(resultSet.getString("ktbName"));
            book.setKtbNevisande(resultSet.getString("ktbNevisandeh"));
            book.setKtbTedad(resultSet.getString("ktbTedad"));
            book.setKtbVazeit(resultSet.getString("ktbVazeiat"));
            book.setAmtTarakoneshID(resultSet.getString("amtTarakoneshID"));
            bookList.add(book);
        }
        return bookList;
    }

    public static List<Book> readBooksDB(int vazeiat) throws SQLException {
        List<Book> bookList = new ArrayList<>();
        Database.makeConnection();
        String vaz;
        if (vazeiat == 1) {
            vaz = "موجود";
        } else {
            vaz = "ناموجود";
        }
        String sql = String.format("SELECT * FROM book WHERE ktbVazeiat = '%s'", vaz);
        ResultSet resultSet = Database.getStatement().executeQuery(sql);
        Book book;
        while (resultSet.next()) {
            book = new Book();
            book.setKtbID(String.valueOf(resultSet.getInt("ktbID")));
            book.setKtbName(resultSet.getString("ktbName"));
            book.setKtbNevisande(resultSet.getString("ktbNevisandeh"));
            book.setKtbTedad(resultSet.getString("ktbTedad"));
            book.setKtbVazeit(resultSet.getString("ktbVazeiat"));
            bookList.add(book);
        }
        return bookList;
    }

    public static void deleteBook(String id) throws SQLException {
        makeConnection();
        String sql = String.format("DELETE FROM book WHERE ktbID = %s", id);
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public static Book getItemBookDB(String bookID) throws SQLException {
        makeConnection();
        String sql = String.format("Select * FROM book WHERE ktbID = '%s' ", bookID);
        ResultSet resultSet = getStatement().executeQuery(sql);
        Book book = new Book();
        resultSet.next();
        book.setKtbID(resultSet.getString("ktbID"));
        book.setKtbName(resultSet.getString("ktbName"));
        book.setKtbNevisande(resultSet.getString("ktbNevisandeh"));
        book.setKtbEhdaKonandeh(resultSet.getString("KtbEhdaKonandeh"));
        book.setKtbTedad(resultSet.getString("ktbTedad"));
        book.setKtbVazeit(resultSet.getString("ktbVazeiat"));
        book.setAmtTarakoneshID(resultSet.getString("amtTarakoneshID"));
        closeConnection();

        return book;
    }


    public static void updateBook(Book book, String ktbIDTXT) throws SQLException {
        makeConnection();
        String sql = String.format("UPDATE book SET ktbName = '%s', ktbTedad = '%s', ktbNevisandeh = '%s'," +
                        " ktbEhdaKonande = '%s' WHERE ktbID = '%s'", book.getKtbName(), book.getKtbTedad(), book.getKtbNevisande(),
                book.getKtbEhdaKonandeh(), ktbIDTXT);
        getStatement().executeUpdate(sql);
        closeConnection();
    }

    public static void createUser(User user) throws SQLException {
        makeConnection();
        String sql = String.format("INSERT INTO user (usrID, userName, usrFName, usrLName, usrCodeMeli, usrPass)" +
                        " VALUES ('%s','%s','%s','%s','%s','%s')", user.getID(), user.getUserName(), user.getFirstName(),
                user.getLastName(), user.getCodeMeli(), user.getPassword());
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public static List<User> readUsersDB() throws SQLException {
        List<User> userList = new ArrayList<>();
        Database.makeConnection();
        ResultSet resultSet = Database.getStatement().executeQuery("SELECT * FROM user");
        User user;
        while (resultSet.next()) {
            user = new User();
            user.setID(resultSet.getString("usrID"));
            user.setUserName(resultSet.getString("userName"));
            user.setPassword(resultSet.getString("usrPass"));
            user.setFirstName(resultSet.getString("usrFName"));
            user.setLastName(resultSet.getString("usrLName"));
            user.setCodeMeli(resultSet.getString("usrCodeMeli"));
            userList.add(user);
        }

        Database.closeConnection();
        return userList;
    }

    public static List<User> readUsersDB(String text, String lable) throws SQLException {
        List<User> userList = new ArrayList<>();
        Database.makeConnection();
        String sql;
        if (lable == "numOzv") {
            sql = String.format("SELECT * FROM user WHERE usrID = '%s'", text);
        } else {
            sql = String.format("SELECT * FROM user WHERE usrCodeMeli = '%s'", text);
        }
        ResultSet resultSet = Database.getStatement().executeQuery(sql);
        User user;
        while (resultSet.next()) {
            user = new User();
            user.setID(resultSet.getString("usrID"));
            user.setUserName(resultSet.getString("userName"));
            user.setPassword(resultSet.getString("usrPass"));
            user.setFirstName(resultSet.getString("usrFName"));
            user.setLastName(resultSet.getString("usrLName"));
            user.setCodeMeli(resultSet.getString("usrCodeMeli"));
            userList.add(user);
        }

        Database.closeConnection();
        return userList;
    }

    public static void deleteUser(String id) throws SQLException {
        makeConnection();
        String sql = String.format("DELETE FROM user WHERE usrID = %s", id);
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public static List<Amanat> readAmanatsDB() throws SQLException, ParseException {
        List<Amanat> amanatList = new ArrayList<>();
        Database.makeConnection();
        ResultSet resultSet = Database.getStatement().executeQuery("SELECT * FROM amanat");
        Amanat amanat;
        while (resultSet.next()) {
            amanat = new Amanat();
            amanat.setAmtID(resultSet.getString("amtID"));
            amanat.setKtbID(resultSet.getString("ktbID"));
            amanat.setUsrID(resultSet.getString("usrID"));
            amanat.setAmtDateGet(resultSet.getString("amtDateGet"));
            amanat.setAmtDateRtrn(resultSet.getString("amtDateRtrn"));
            amanat.setAmtDarkhastUsr(resultSet.getString("amtDarkhastUsr"));
            amanat.setAmtEmkanTamdid(resultSet.getString("amtEmkanTamdid"));
            amanat.setKtbName(getKtbName(resultSet.getString("ktbID")));
            amanat.setUsrName(getUsrName(resultSet.getString("usrID")));
            amanatList.add(amanat);
        }

        Database.closeConnection();
        return amanatList;
    }


    public static List<Amanat> readAmanatsDB(String text, String lable) throws SQLException, ParseException {
        List<Amanat> amanatList = new ArrayList<>();
        Database.makeConnection();
        String sql;
        if (lable.equals("ktbID")) {
            sql = String.format("SELECT * FROM amanat WHERE ktbID = '%s'", text);
        } else {
            sql = String.format("SELECT * FROM amanat WHERE usrID = '%s'", text);
        }
        ResultSet resultSet = Database.getStatement().executeQuery(sql);
        Amanat amanat;
        while (resultSet.next()) {
            amanat = new Amanat();
            amanat.setAmtID(resultSet.getString("amtID"));
            amanat.setKtbID(resultSet.getString("ktbID"));
            amanat.setUsrID(resultSet.getString("usrID"));
            amanat.setAmtDateGet(resultSet.getString("amtDateGet"));
            amanat.setAmtDateRtrn(resultSet.getString("amtDateRtrn"));
            amanat.setAmtDarkhastUsr(resultSet.getString("amtDarkhastUsr"));
            amanat.setAmtEmkanTamdid(resultSet.getString("amtEmkanTamdid"));
            amanat.setKtbName(getKtbName(resultSet.getString("ktbID")));
            amanat.setUsrName(getUsrName(resultSet.getString("usrID")));
            amanatList.add(amanat);
        }

        Database.closeConnection();
        return amanatList;
    }

    public static String getKtbName(String ktbID) throws SQLException {
        makeConnection();
        String sqlKtb = String.format("SELECT ktbName FROM book WHERE ktbID = '%s'", ktbID);
        ResultSet resultSetKtb = Database.getStatement().executeQuery(sqlKtb);
        resultSetKtb.next();
        String ktbName = resultSetKtb.getString("ktbName");
        resultSetKtb.close();
        return ktbName;
    }

    public static boolean getBookVaziyat(String ktbid) throws SQLException {
        makeConnection();
        boolean vaziyat ;
        String testvaziyat = "select ktbVazeiat from book where ktbID = '%s' ";
        testvaziyat = String.format(testvaziyat , ktbid);
        ResultSet rs = Database.getStatement().executeQuery(testvaziyat);
        rs.next();
        String vazeiat = rs.getString("ktbVazeiat") ;
        rs.close();
        if (vazeiat.equals("موجود") ) {
            vaziyat = true;
        }
        else{
            vaziyat = false;
        }
        closeConnection();
        return vaziyat;
    }

    public static void amanatgiri(Amanat amanat) throws SQLException {
        boolean vaziyat = getBookVaziyat(amanat.getKtbID());
        if(vaziyat==true) {
            makeConnection();
            String amanatgiri = "INSERT INTO amanat (ktbID, usrID , amtDateGet , amtDateRtrn ,amtDarkhastUsr , amtEmkanTamdid)  values ('%s','%s','%s','%s','%s','%s')";
            amanatgiri = String.format(amanatgiri, amanat.getKtbID(), amanat.getUsrID(), amanat.getAmtDateGet(),
                    amanat.getAmtDateRtrn(), amanat.getAmtDarkhastUsr(), amanat.getAmtEmkanTamdid());
            System.out.println(amanatgiri);
            Database.getStatement().execute(amanatgiri, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int amtID = resultSet.getInt(1);
            closeConnection();
            updateBookAmntStatus(getUsrName(LoginPageCtrl.get_id()), String.valueOf(amtID), "ناموجود", amanat.getKtbID());
        }
        else if(vaziyat==false){
            alert.errorAlert("کتاب مورد نظر شما درحال حاضر موجود نمیباشد");
        }
    }

    public static void updateBookAmntStatus(String ktbamntgirande , String amnttarakoneshid , String ktbvaziyat , String ktbid) throws SQLException {
        makeConnection();
        String updateBook = String.format("UPDATE book SET ktbAmntGirande = '%s' ,amtTarakoneshID = '%s',  ktbVazeiat = '%s' WHERE ktbID = '%s' ",
                ktbamntgirande, amnttarakoneshid, ktbvaziyat , ktbid);
        getStatement().execute(updateBook);
        closeConnection();
    }


    public static String getUsrName(String usrID) throws SQLException {
        makeConnection();
        String sqlUsr = String.format("SELECT usrFName,usrLName FROM user WHERE usrID = '%s'", usrID);
        ResultSet resultSetUsr = Database.getStatement().executeQuery(sqlUsr);
        resultSetUsr.next();
        String usrName = resultSetUsr.getString("usrFName") + " " + resultSetUsr.getString("usrLName");
        resultSetUsr.close();
        return usrName;
    }

    public static String getAmntTarakoneshID(String ktbID) throws SQLException {
        makeConnection();
        String sqlUsr = String.format("SELECT amtTarakoneshID FROM book WHERE ktbID = '%s'", ktbID);
        ResultSet resultSetUsr = Database.getStatement().executeQuery(sqlUsr);
        resultSetUsr.next();
        String amntID = resultSetUsr.getString("amtTarakoneshID");
        resultSetUsr.close();
        return amntID;
    }

    public static void updateAmanat(String amtID, int lable) throws SQLException {
        makeConnection();
        String sql = null;
        if (lable == 0) {
            sql = String.format("UPDATE amanat SET amtEmkanTamdid = '0', amtDarkhastUsr = '3' WHERE amtID = '%s'", amtID);
        } else if (lable == 1) {
            sql = String.format("UPDATE amanat SET amtEmkanTamdid = '0', amtDarkhastUsr = '1' WHERE amtID = '%s'", amtID);
        }
        getStatement().executeUpdate(sql);
        closeConnection();
    }

    public static void updateAmanat(String amtID, String amtDateRtrn) throws SQLException {
        makeConnection();
        String sql = String.format("UPDATE amanat SET amtDateRtrn = '%s', amtEmkanTamdid = '0', amtDarkhastUsr = '2'" +
                " WHERE amtID = '%s'", amtDateRtrn, amtID);
        getStatement().executeUpdate(sql);
        closeConnection();
    }

    public static Amanat getItemAmanatDB(String amtID) throws SQLException, ParseException {
        makeConnection();
        String sql = String.format("Select * FROM amanat WHERE amtID = '%d' ", Integer.parseInt(amtID));
        ResultSet resultSet = getStatement().executeQuery(sql);
        Amanat amanat = new Amanat();
        resultSet.next();
        amanat.setAmtID(String.valueOf(resultSet.getInt("amtID")));
        amanat.setKtbID(resultSet.getString("ktbID"));
        amanat.setUsrID(resultSet.getString("usrID"));
        amanat.setAmtDateGet(resultSet.getString("amtDateGet"));
        amanat.setAmtDateRtrn(resultSet.getString("amtDateRtrn"));
        amanat.setAmtDarkhastUsr(resultSet.getString("amtDarkhastUsr"));
        amanat.setAmtEmkanTamdid(resultSet.getString("amtEmkanTamdid"));
        closeConnection();

        return amanat;
    }


    public static void updateAdmin(Admin admin, String adminID) throws SQLException {
        makeConnection();
        String sql = String.format("UPDATE admin SET admFName = '%s', admLName = '%s' , admCodeMeli = '%s',admUserName ='%s'," +
                        " admPass = '%s' WHERE admID = '%s'", admin.getFirstName(), admin.getLastName(), admin.getCodeMeli(),
                admin.getUserName(), admin.getPassword(), adminID);
        getStatement().executeUpdate(sql);
        closeConnection();
    }

    public static List getAmanatRecent(String typeAmanat) throws SQLException {
        makeConnection();
        ArrayList<Amanat> amanatRecent = new ArrayList<>();
        String sql;
        if (typeAmanat.equals("عودت")) {
            sql = String.format("SELECT * FROM amanat WHERE amtDarkhastUsr = '%s' ORDER BY amtID DESC LIMIT 4 ", typeAmanat);
        } else {
            sql = String.format("SELECT * FROM amanat WHERE NOT amtDarkhastUsr = '%s' ORDER BY amtID DESC LIMIT 4 ", "عودت");
        }

        ResultSet resultSet = Database.getStatement().executeQuery(sql);
        Amanat amanat;
        while (resultSet.next()) {
            amanat = new Amanat();
            amanat.setAmtID(resultSet.getString("amtID"));
            amanat.setUsrName(getUsrName(resultSet.getString("usrID")));
            amanat.setKtbName(getKtbName(resultSet.getString("ktbID")));
            amanatRecent.add(amanat);
        }
        ArrayList str = new ArrayList();
        for (Amanat amt : amanatRecent) {
            String strAmt;
            if (typeAmanat.equals("عودت")) {
                strAmt = amt.getUsrName() + " کتاب " + amt.getKtbName() + " را با شماره " + amt.getAmtID() + " عودت داد.";
            } else {
                strAmt = amt.getUsrName() + " کتاب " + amt.getKtbName() + " را با شماره " + amt.getAmtID() + " به امانت برد.";
            }
            str.add(strAmt);
        }
        return str;
    }


    public static String counter(String tableName) throws SQLException {
        makeConnection();
        String sql = String.format("SELECT COUNT(*) FROM %s ", tableName);
        ResultSet resultSet = getStatement().executeQuery(sql);
        resultSet.next();
        String num = resultSet.getString(1);
        closeConnection();
        return num;
    }

    public static String counter(String tableName, String feildName, String feildValue) throws SQLException {
        makeConnection();
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s' ", tableName, feildName, feildValue);
        ResultSet resultSet = getStatement().executeQuery(sql);
        resultSet.next();
        String num = resultSet.getString(1);
        closeConnection();
        return num;
    }


}


