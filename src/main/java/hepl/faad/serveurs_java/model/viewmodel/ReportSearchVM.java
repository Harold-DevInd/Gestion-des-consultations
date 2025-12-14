package hepl.faad.serveurs_java.model.viewmodel;

public class ReportSearchVM {
    private Integer idReport;
    private Integer idDoctor;
    private Integer idPatient;

    public ReportSearchVM() {};

    public ReportSearchVM(Integer idReport, Integer idDoctor, Integer idPatient) {
        this.idReport = idReport;
        this.idDoctor = idDoctor;
        this.idPatient = idPatient;
    }

    public Integer getIdReport() {
        return idReport;
    }
    public Integer getIdDoctor() {
        return idDoctor;
    }
    public Integer getIdPatient() {
        return idPatient;
    }
}
