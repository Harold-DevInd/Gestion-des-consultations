package hepl.faad.serveurs_java.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class Specialty implements Entity, Serializable{

    private Integer idSpecialty;
    private String nom;

    public Specialty() {}

    public Specialty(Integer id, String nom) {
        this.idSpecialty = id;
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "idSpecialty=" + idSpecialty +
                ", nom='" + nom + '\'' +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Specialty specialty = (Specialty) o;
        return Objects.equals(idSpecialty, specialty.idSpecialty) && Objects.equals(nom, specialty.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSpecialty, nom);
    }
}
