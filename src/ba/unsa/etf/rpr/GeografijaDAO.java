package ba.unsa.etf.rpr;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Scanner;


public class GeografijaDAO implements Initializable {

    private static GeografijaDAO instance = null;
    public static Connection conn;
    private String url = "jdbc:sqlite:baza.db";
    private PreparedStatement preparedStatement;
    private ArrayList<Grad> grad;
    private ArrayList<Drzava> drzave;
    Statement statement;
    private ResultSet resultSet;


    //Za fxml
    public TableView tabelaDrzava;
    public TableColumn<Drzava, Integer> idD;
    public TableColumn<Drzava, String> nazivD;
    public TableColumn<Drzava, Grad> glavniGrad;


    public TableView tabelaGradova;
    public TableColumn<Grad, Integer> id;
    public TableColumn<Grad, String> naziv;
    public TableColumn<Grad, Integer> brojStan;
    public TableColumn<Grad, Integer> drzava;
    private static void initialize() {
        instance = new GeografijaDAO();
    }


    private GeografijaDAO(){
        grad = new ArrayList<>();
        drzave = new ArrayList<>();
        napuniPodacima();
        conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");

            Statement statement = null;
            try{
                statement = conn.createStatement();
                statement.execute("select id from drzava");

            }catch (Exception e){
                try{
                    Statement statement2=null;
                    statement2 = conn.createStatement();
//                    connection.setAutoCommit(false);
                    statement2.execute("CREATE TABLE grad(id integer primary key, naziv varchar(255), broj_stanovnika integer)");
                    statement2.execute("CREATE TABLE drzava(id integer primary key, naziv varchar(255), glavni_grad integer unique references grad(id))");
                    statement2.execute("ALTER TABLE grad ADD drzava integer references drzava(id)");

                    statement2.execute("INSERT INTO drzava values (1,'Velika Britanija',1)");
                    statement2.execute("INSERT INTO drzava values (2,'Austrija',2)");
                    statement2.execute("INSERT INTO drzava values (3,'Francuska',3)");
                    statement2.execute("INSERT INTO grad values (1,'London',8825000,1)");
                    statement2.execute("INSERT INTO grad values (2,'Beč',1899055,2)");
                    statement2.execute("INSERT INTO grad values (3,'Pariz',2206488,3)");
                    statement2.execute("INSERT INTO grad values (4,'Manchester',545500,1)");
                    statement2.execute("INSERT INTO grad values (5,'Graz',280200,2)");
                }catch (Exception ex){

                }
            }

        } catch (SQLException e) {
        }
    }



    public static void removeInstance() {
        instance = null;
    }

    public static Connection getConnection() {
        return conn;
    }

    private void napuniPodacima() {
        Grad pariz = new Grad(1, "Pariz", 2229621 , null);
        Grad london = new Grad(2, "London",  	7355400 , null);
        Grad bec = new Grad(3, "Beč", 1867582, null);
        Grad manchester = new Grad(4, "Manchester",  	441200, null);
        Grad graz = new Grad(5, "Graz",  	286686, null);
        Drzava francuska = new Drzava(1, "Francuska", pariz);
        Drzava engleska = new Drzava(2, "Engleska", london);
        Drzava austrija = new Drzava(3, "Austrija", bec);
        pariz.setDrzava(francuska);
        london.setDrzava(engleska);
        bec.setDrzava(austrija);
        manchester.setDrzava(engleska);
        graz.setDrzava(austrija);
        grad.add(pariz);
        grad.add(london);
        grad.add(bec);
        grad.add(manchester);
        grad.add(graz);
        drzave.add(francuska);
        drzave.add(engleska);
        drzave.add(austrija);
        grad.sort(new Comparator<Grad>() {
            @Override
            public int compare(Grad o1, Grad o2) {
                Integer brojStanovnika1 = o2.getBrojStanovnika();
                Integer brojStanovnika2 = o1.getBrojStanovnika();
                return brojStanovnika1.compareTo(brojStanovnika2);
            }
        });
    }


    public static GeografijaDAO getInstance() {
        if(instance == null) initialize();
        return instance;
    }


    public ArrayList<Grad> gradovi() {
        return grad;
    }

    public Grad glavniGrad(String drzava) {

        String upit = "select g.* from grad g, drzava d where d.naziv=? and d.glavni_grad = g.id";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(upit);
            preparedStatement.setString(1, drzava);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = resultSet.getInt("g.id");
            String naziv = resultSet.getString("g.naziv");
            int brojStanovnika = resultSet.getInt("g.broj_stanovnika");
            return new Grad(id, naziv, brojStanovnika, null);

        } catch (SQLException e) {
            //e.printStackTrace();
        }

        return null;
    }

    public void obrisiDrzavu(String drzava) {

        String upit = "DELETE FROM drzava WHERE naziv = '" + drzava + "';";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(upit);
            preparedStatement.setString(1, drzava);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }

    }

    public Drzava nadjiDrzavu(String drzava) {

        String upit = "select * from drzava d where d.naziv=?";

        try {

            PreparedStatement preparedStatement = conn.prepareStatement(upit);
            preparedStatement.setString(1, drzava);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.isClosed())
                return null;

            int drzavaId = resultSet.getInt("d.id");
            Integer gradId = resultSet.getInt("d.glavni_grad");
            Drzava novaDrzava = new Drzava(drzavaId, drzava, null);
            Grad glavniGrad = glavniGrad(drzava);
            glavniGrad.setDrzava(novaDrzava);
            novaDrzava.setGlavniGrad(glavniGrad);
            return novaDrzava;

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public void dodajGrad(Grad grad) {
        String upit = "insert into grad values(?, ?, ?, ?);";

       /* try {
            PreparedStatement preparedStatement = conn.prepareStatement(upit);
            preparedStatement.setInt(1, grad.getId());
            preparedStatement.setString(2, grad.getNaziv());
            preparedStatement.setInt(3, grad.getBrojStanovnika());
            preparedStatement.setNull(4, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }*/

        if (this.grad.contains(grad)) throw new IllegalArgumentException("Grad vec postoji");
        this.grad.add(grad);

    }

    public void dodajDrzavu(Drzava drzava) {
        String upit = "insert into drzava values(?, ?, ?);";

     /*try {
            PreparedStatement preparedStatement = conn.prepareStatement(upit);
            preparedStatement.setInt(1, drzava.getId());
            preparedStatement.setString(2, drzava.getNaziv());
            preparedStatement.setInt(3, drzava.getGlavniGrad().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }*/
        if (drzave.contains(drzava)) throw new IllegalArgumentException("Drzava vec postoji");
        drzave.add(drzava);
    }

    public void izmijeniGrad(Grad grad) {

        /*PreparedStatement statement = null;
        try {
            dajGradove = connection.prepareStatement("select * from grad");
            drzava = conn.prepareStatement("select * from drzava");
            getGrad = connection.prepareStatement("select * from grad where id=?");
            getDrzava = connection.prepareStatement("select * from drzava where naziv=?");
            PreparedStatement drzava;
            drzava.setString(1,grad.getDrzava().getNaziv());
            ResultSet res = getDrzava.executeQuery();
            int id=0;
            while(res.next()){
                id=res.getInt(3);
            }
            getGrad.setInt(1,id);
            ResultSet set = getGrad.executeQuery();
            statement = connection.prepareStatement("update grad set naziv=? where id =?");
            while(set.next()){
                statement.setInt(2,id);
                statement.setString(1,grad.getNaziv());
                set.close();
                break;
            }
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

        try{
            String upit1 = "UPDATE grad set broj_stanovnika = ? where id = ?";
            String upit2 = "UPDATE grad set name = ? where id = ?";
            String upit3 = "UPDATE grad set drzava = ? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(upit1);
            preparedStatement.setInt(1, grad.getBrojStanovnika());
            PreparedStatement preparedStatement2 = conn.prepareStatement(upit2);
            preparedStatement.setString(2, grad.getNaziv());
            PreparedStatement preparedStatement3 = conn.prepareStatement(upit3);
            preparedStatement.setInt(1, grad.getId());
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();
            preparedStatement3.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void izmijeniGrad1(ActionEvent actionEvent) {
        izmijeniGrad((Grad) tabelaGradova.getSelectionModel().getSelectedItem());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabelaDrzava.setItems((ObservableList) drzave);
        idD.setCellValueFactory(new PropertyValueFactory<>("idD"));
        nazivD.setCellValueFactory(new PropertyValueFactory<>("nazivD"));
        glavniGrad.setCellValueFactory(new PropertyValueFactory<>("glavniGrad"));


        tabelaGradova.setItems((ObservableList) grad);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        naziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        brojStan.setCellValueFactory(new PropertyValueFactory<>("brojStan"));
        drzava.setCellValueFactory(new PropertyValueFactory<>("drzava"));

    }

    public void dodajDrzavu(ActionEvent actionEvent) {
        dodajDrzavu(new Drzava(123, "Nova", new Grad()));
    }


    public void dodajGrad(ActionEvent actionEvent) {
        dodajGrad(new Grad(123, "Novi", 1000, new Drzava()));
    }

    public void obrisiDrzavu(ActionEvent actionEvent) {
        drzave.remove(tabelaGradova.getSelectionModel().getSelectedItem());
    }

    public void nadiDrzavu(ActionEvent actionEvent) {
        Scanner scanner = new Scanner(System.in);
        String drzavaZaPronaci = scanner.nextLine();
        nadjiDrzavu(drzavaZaPronaci);
    }


    public Connection getConn() {
        return conn;
    }


}