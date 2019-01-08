package ba.unsa.etf.rpr;

public class Grad {

    private Drzava drzava;
    private int id;
    private String naziv;
    private int brojStanovnika;

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.naziv = naziv;
        this.id = id;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
    }


    public Grad(){}

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String toString1() {
        return "Glavni grad " + drzava.getNaziv() + " je " + naziv + "\n";

    }
    @Override
    public String toString() {
        return naziv + "(" + drzava.getNaziv() + ") - " + brojStanovnika + "\n";
    }
}