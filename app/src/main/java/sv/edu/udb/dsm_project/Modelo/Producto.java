package sv.edu.udb.dsm_project.Modelo;

public class Producto {
    private String Nomb;
    private String Desc;
    private String Url;
    private Double Prec;
    private boolean Esta;
    private String Estado;
    String key;

    public Producto() {}
    public Producto(String Nomb,String Desc,Double Prec,boolean Esta)
    {
        this.Nomb = Nomb;
        this.Desc=Desc;
        this.Prec=Prec;
        this.Esta=Esta;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
