package sv.edu.udb.dsm_project.Modelo;

public class Producto {
    private String Nomb;
    private String Desc;
    private Double Prec;
    private boolean Esta;

    public Producto() {}
    public Producto(String Nomb,String Desc,Double Prec,boolean Esta)
    {
        this.Nomb = Nomb;
        this.Desc=Desc;
        this.Prec=Prec;
        this.Esta=Esta;
    }
    public String getNomb() {
        return Nomb;
    }

    public void setNomb(String nomb) {
        Nomb = nomb;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Double getPrec() {
        return Prec;
    }

    public void setPrec(Double prec) {
        Prec = prec;
    }

    public boolean isEsta() {
        return Esta;
    }

    public void setEsta(boolean esta) {
        Esta = esta;
    }
}
