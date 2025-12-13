package hepl.faad.serveurs_java.model.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Report implements Entity,Serializable {
    private Integer idReport;
    private Integer doctorId;
    private Integer patientId;
    private LocalDate dateReport;
    private String content;

    public Report() {}

    public Report(Integer id, Integer doctorId, Integer patientId, LocalDate dateReport, String content) {
        this.idReport = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.dateReport = dateReport;
        this.content = content;
    }

    public static Report convertByteToReport(byte[] rapportByte) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(rapportByte);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            return (Report) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur de conversion byte[] -> Report", e);
        }
    }

    public static byte[] convertReportToByte(Report newReport) {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
             java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos)) {

            oos.writeObject(newReport);
            oos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erreur de conversion Report -> byte[]", e);
        }
    }

    public Integer getIdReport() {
        return idReport;
    }
    public Integer getDoctorId() {
        return doctorId;
    }
    public Integer getPatientId() {
        return patientId;
    }
    public LocalDate getDateReport() {
        return dateReport;
    }
    public String getContent() {
        return content;
    }

    public void setIdReport(Integer idReport) {
        this.idReport = idReport;
    }
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
    public void setDateReport(LocalDate dateReport) {
        this.dateReport = dateReport;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Report{" +
                "idReport=" + idReport +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", dateReport=" + dateReport +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (idReport != report.idReport) return false;
        if (doctorId != report.doctorId) return false;
        if (patientId != report.patientId) return false;
        if (dateReport != null ? !dateReport.equals(report.dateReport) : report.dateReport != null) return false;
        return content != null ? content.equals(report.content) : report.content == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReport, doctorId, patientId, dateReport, content);
    }
}
