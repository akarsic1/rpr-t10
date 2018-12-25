package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
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
        for (Grad g : gradovi) {
            rez += g;
        }
        return rez;
    }
}