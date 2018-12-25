package ba.unsa.etf.rpr;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Scanner;


public class GeografijaDAO implements Initializable {

    private static GeografijaDAO instance = null;
    private Connection conn;
    private String url = "jdbc:oracle:thin:@ora.db.lab.ri.etf.unsa.ba:1521:ETFLAB";
    private PreparedStatement preparedStatement;
    private ArrayList<Grad> gradovi;
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


    private GeografijaDAO() {
        gradovi = new ArrayList<>();
        drzave = new ArrayList<>();
        /*try {
            PreparedStatement statement=null;
            Statement statement1 = null;
            try{
                statement1= conn.createStatement();
                statement1.execute("CREATE TABLE drzava(id INTEGER PRIMARY KEY ,naziv varchar(255) not null, glavni_grad integer )");
                statement1.execute("CREATE TABLE grad(id integer primary key, naziv varchar(255), broj_stanovnika INTEGER,drzava integer) ");
                statement1.closeOnCompletion();
            }catch (Exception e){
                // System.out.println("ovdje je greska"+e.getMessage());
                //statement1.execute("CREATE TABLE grad(id int primary key, naziv varchar, broj_stanovnika INTEGER,drzava int; ");
                try {
                    statement = conn.prepareStatement("delete from drzava");
                    statement.execute();
                    statement = conn.prepareStatement("delete from grad");
                    statement.execute();
                } catch (SQLException e1) {
                    // e1.printStackTrace();
                }
            }
        }catch (Exception e){}*/
        napuniPodacima();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, "AH18103", "HRht2wV1");
            preparedStatement = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
            statement = conn.createStatement();
            // if (!login()) throw new IllegalArgumentException();
            //String korisnik = controller.getAutor().textProperty();
            //ResultSet resultSet = statement.executeQuery("INSERT INTO grad VALUES (?, ?, ?, NULL)");
            for (var grad : gradovi) {
                try {
                    preparedStatement.setInt(1, grad.getId());
                    preparedStatement.setString(2, grad.getNaziv());
                    preparedStatement.setInt(3, grad.getBrojStanovnika());
                    preparedStatement.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            preparedStatement = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
            for (var drzava : drzave) {
                try {
                    preparedStatement.setInt(1, drzava.getId());
                    preparedStatement.setString(2, drzava.getNaziv());
                    preparedStatement.setInt(3, drzava.getGlavniGrad().getId());
                    preparedStatement.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            preparedStatement = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
            for (var grad : gradovi) {
                try {
                    preparedStatement.setInt(1, grad.getDrzava().getId());
                    preparedStatement.setInt(2, grad.getId());
                    preparedStatement.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void removeInstance() {
        instance = null;
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
        gradovi.add(pariz);
        gradovi.add(london);
        gradovi.add(bec);
        gradovi.add(manchester);
        gradovi.add(graz);
        drzave.add(francuska);
        drzave.add(engleska);
        drzave.add(austrija);
        gradovi.sort(new Comparator<Grad>() {
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
        return gradovi;
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

        if (gradovi.contains(grad)) throw new IllegalArgumentException("Grad vec postoji");
        gradovi.add(grad);

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


        tabelaGradova.setItems((ObservableList) gradovi);
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
}
