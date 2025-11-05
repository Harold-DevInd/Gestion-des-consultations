package hepl.faad.serveurs_java.model.viewmodel;

public class SpecialtySearchVM {
    private Integer idSpecialty;
    private String nom;

    public SpecialtySearchVM() {};

    public SpecialtySearchVM(Integer idSpecialty, String nom) {
        this.idSpecialty = idSpecialty;
        this.nom = nom;
    }

    public Integer getIdSpecialty() {
        return idSpecialty;
    }

    public void setIdSpecialty(Integer idSpecialty) {
        this.idSpecialty = idSpecialty;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
