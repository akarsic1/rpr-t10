package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("bs","BA"));
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("program.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle("Države i gradovi");
        primaryStage.setScene(new Scene(root, 600, 420));
        primaryStage.show();

    }

    public static void main(String[] args) {
        System.out.println("Gradovi su:\n" + ispisiGradove());
        //glavniGrad();
        launch(args);
    }


    private static void glavniGrad() {
        System.out.println("Unesite naziv drzave: ");
        Scanner scanner = new Scanner(System.in);
        String ime = scanner.nextLine();
        Drzava drzava = GeografijaDAO.getInstance().nadjiDrzavu(ime);
        Grad grad = drzava.getGlavniGrad();
        grad.toString1();
    }

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
        String rez = "";
        for(Grad g : gradovi) {
            rez += g;
        }
        return rez;
    }
}