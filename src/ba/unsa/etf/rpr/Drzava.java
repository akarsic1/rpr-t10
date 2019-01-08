package ba.unsa.etf.rpr;

public class Drzava {
    private Integer id;
    private String naziv;
    private Grad glavniGrad;

    public Drzava(int drzavaId, String naziv , Grad gradId) {
        this.id = drzavaId;
        this.naziv = naziv;
        this.glavniGrad = gradId;
    }

    public Drzava(){}

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
