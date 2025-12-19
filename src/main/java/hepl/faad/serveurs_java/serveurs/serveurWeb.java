package hepl.faad.serveurs_java.serveurs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import hepl.faad.serveurs_java.model.dao.ConsultationDAO;
import hepl.faad.serveurs_java.model.dao.DoctorDAO;
import hepl.faad.serveurs_java.model.dao.PatientDAO;
import hepl.faad.serveurs_java.model.dao.SpecialtyDAO;
import hepl.faad.serveurs_java.model.entity.Consultation;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.entity.Specialty;
import hepl.faad.serveurs_java.model.viewmodel.ConsultationSearchVM;
import hepl.faad.serveurs_java.model.viewmodel.DoctorSearchVM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class serveurWeb {
    static int port;
    static ConsultationDAO consultationDAO;
    static DoctorDAO doctorDAO;
    static PatientDAO patientDAO;
    static SpecialtyDAO specialtyDAO;

    public static void main(String[] args) throws IOException
    {
        String chemin = "C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\serveurs\\serveur.conf";
        try {
            Map<String, String> config = lireConfiguration(chemin);
            port = Integer.parseInt(config.get("PORT_WEB"));
        } catch (IOException e)  {
            System.err.println("Erreur de lecture du fichier : " + chemin + " : " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans le fichier de configuration  : " + e.getMessage());
            return;
        }

        System.out.println("API Rest demarree sur le port " + port + " ...");
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        consultationDAO = new ConsultationDAO();
        doctorDAO = new DoctorDAO();
        patientDAO = new PatientDAO();
        specialtyDAO = new SpecialtyDAO();

        server.createContext("/api/specialities", new SpecialitiesHandler());
        server.createContext("/api/doctors", new DoctorsHandler());
        server.createContext("/api/patients", new PatientsHandler());
        server.createContext("/api/consultations", new ConsultationsHandler());
        server.start();
    }

    static class SpecialitiesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET"))
            {
                System.out.println("--- Requête GET reçue (obtenir la liste des consultations) ---");
                ArrayList<Specialty> specialities;
                specialities = specialtyDAO.load();
                String response = convertSpecialitiesToJson(specialities);
                sendResponse(exchange, 200, response);
            }
            else sendResponse(exchange, 405, "Methode non autorisee");
        }
    }

    static class DoctorsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET"))
            {
                System.out.println("--- Requête GET reçue (obtenir la liste des docteurs) ---");

                Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

                ArrayList<Doctor> doctors;
                DoctorSearchVM dsvm = new DoctorSearchVM();
                String specialtyName;

                if (queryParams.containsKey("specialty"))
                {
                    specialtyName = queryParams.get("specialty");
                    System.out.println("Filtrage par specialtyName ");
                }
                else {
                    specialtyName = "";
                    if (queryParams.containsKey("name"))
                    {
                        String lastName = queryParams.get("name");
                        System.out.println("Filtrage par lastName ");
                        dsvm.setLastName(lastName);
                    }
                    else
                        System.out.println("Aucun filtre applique");
                }

                doctors = doctorDAO.load(dsvm);
                if(!specialtyName.isEmpty())
                {
                    doctors.removeIf(doctor -> !doctor.getSpecialty().getNom().equalsIgnoreCase(specialtyName));
                }
                String response = convertDoctorsToJson(doctors);
                sendResponse(exchange, 200, response);
            }
            else sendResponse(exchange, 405, "Methode non autorisee");
        }
    }

    static class PatientsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("POST"))
            {
                System.out.println("--- Requête POST reçue (ajout d un Patient) ---");
                List<Patient> oldPatients = patientDAO.load();
                Map<String, String> requestBodyMap = readRequestBody(exchange);

                String isNew = requestBodyMap.get("newPatient");
                String patientId = requestBodyMap.get("patientId");
                String firstName = requestBodyMap.get("firstName");
                String lastName = requestBodyMap.get("lastName");
                String birthDate = requestBodyMap.get("birthDate");
                Patient newPatient = new Patient();
                newPatient.setFirstName(firstName);
                newPatient.setLastName(lastName);
                newPatient.setDateNaissance(LocalDate.parse(birthDate));
                if(!isNew.equalsIgnoreCase("true")){
                    if(!patientId.isEmpty()) {
                        newPatient.setIdPatient(Integer.parseInt(patientId));
                    }
                }

                patientDAO.save(newPatient);
                List<Patient> newPatients = patientDAO.load();

                if(!isNew.equalsIgnoreCase("true"))
                {
                    sendResponse(exchange, 200, "Patient (id = " + newPatient.getIdPatient() +
                            ") mis a jour avec succes");
                }
                else{
                    if(oldPatients.size() == newPatients.size()){
                        sendResponse(exchange, 400, "Erreur lors de l'ajout du patient");
                        return;
                    }
                    sendResponse(exchange, 201, "Patient (id = " + newPatients.getLast().getIdPatient() +
                            ") ajoutee avec succes");
                }
            }
            else sendResponse(exchange, 405, "Methode non autorisee");
        }
    }

    static class ConsultationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET"))
            {
                System.out.println("--- Requête GET reçue (obtenir la liste des consultations) ---");
                Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
                ArrayList<Consultation> consultations;
                ConsultationSearchVM dsvm = new ConsultationSearchVM();
                Patient patient = new Patient();
                Doctor doctor = new Doctor();

                if (queryParams.containsKey("date"))
                {
                    LocalDate dateDebut = LocalDate.parse(queryParams.get("date"));
                    System.out.println("Filtrage par dateDebut ");
                    dsvm.setDateDebutConsultation(dateDebut);
                }
                if (queryParams.containsKey("doctor"))
                {
                    String doctorName = queryParams.get("doctor");
                    System.out.println("Filtrage par doctorName ");
                    doctor.setLastName(doctorName);
                    dsvm.setDoctor(doctor);
                }
                if (queryParams.containsKey("specialty"))
                {
                    String specialtyName = queryParams.get("specialty");
                    System.out.println("Filtrage par specialtyName");
                    doctor.getSpecialty().setNom(specialtyName);
                    dsvm.setDoctor(doctor);
                }
                if (queryParams.containsKey("patientId"))
                {
                    int patientId = Integer.parseInt(queryParams.get("patientId"));
                    System.out.println("Filtrage par patientId ");
                    patient.setIdPatient(patientId);
                    dsvm.setPatient(patient);
                }

                consultations = consultationDAO.load(dsvm);
                String response = convertConsultationsToJson(consultations);
                sendResponse(exchange, 200, response);
            } else if (requestMethod.equalsIgnoreCase("PUT"))
            {
                System.out.println("--- Requête PUT reçue (mise a jour) ---");
                // Mettre à jour une tâche existante
                Map<String, String> queryParams =
                        parseQueryParams(exchange.getRequestURI().getQuery());
                if (queryParams.containsKey("id"))
                {
                    int taskId = Integer.parseInt(queryParams.get("id"));
                    System.out.println("Mise a jour tache id=" + taskId);
                    //String requestBody = readRequestBody(exchange);
                    //System.out.println("requestNody = " + requestBody);
                    //updateTask(taskId, requestBody);
                    sendResponse(exchange, 200, "Tache mise a jour avec succes");
                }
                else sendResponse(exchange, 400, "ID de tache manquant dans les parametres");
            }
            else if (requestMethod.equalsIgnoreCase("DELETE"))
            {
                System.out.println("--- Requête DELETE reçue (suppression) ---");
                // Supprimer une tâche
                Map<String, String> queryParams =
                        parseQueryParams(exchange.getRequestURI().getQuery());
                if (queryParams.containsKey("id"))
                {
                    int taskId = Integer.parseInt(queryParams.get("id"));
                    System.out.println("Suppression tache id=" + taskId);
                    //deleteTask(taskId);
                    sendResponse(exchange, 200, "Tache supprimee avec succes");
                }
                else sendResponse(exchange, 400, "ID de tache manquant dans les parametres");
            }
            else sendResponse(exchange, 405, "Methode non autorisee");
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        System.out.println("Envoi de la réponse, code : " + statusCode + " ");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        String body = sb.toString().trim();

        Map<String, String> result = new HashMap<>();
        if (body.isEmpty()) return result;

        // Match values between quotes: "key": "value"
        java.util.regex.Pattern pQuoted = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        java.util.regex.Matcher m = pQuoted.matcher(body);
        while (m.find()) {
            result.put(m.group(1), m.group(2));
        }

        // Match non-quoted values: "key": 123  or "key": true  or "key": null
        java.util.regex.Pattern pUnquoted = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:\\s*([^,}\\s]+)");
        m = pUnquoted.matcher(body);
        while (m.find()) {
            String key = m.group(1);
            if (!result.containsKey(key)) {
                result.put(key, m.group(2));
            }
        }

        // Simple unescape commun pour les valeurs déjà capturées
        result.replaceAll((k, v) -> v
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\t", "\t"));

        return result;
    }

    private static Map<String, String> parseQueryParams(String query){
        Map<String, String> queryParams = new HashMap<>();
        if (query != null)
        {
            String[] params = query.split("&");
            for (String param : params)
            {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2)
                {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }

    private static String convertSpecialitiesToJson(ArrayList<Specialty> specialties){
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < specialties.size(); i++)
        {
            Specialty specialty = specialties.get(i);
            json.append("{")
                    .append("\"id\":").append(specialty.getIdSpecialty()).append(",")
                    .append("\"nom\":\"").append(specialty.getNom()).append("\"")
                    .append("}");
            if (i < specialties.size() - 1)
            {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    private static String convertDoctorsToJson(ArrayList<Doctor> doctors){
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < doctors.size(); i++)
        {
            Doctor doctor = doctors.get(i);
            json.append("{")
                    .append("\"id\":").append(doctor.getIdDoctor()).append(",")
                    .append("\"firstName\":\"").append(doctor.getFirstName()).append("\",")
                    .append("\"lastName\":\"").append(doctor.getLastName()).append("\",")
                    .append("\"specialty\":").append(doctor.getSpecialty().getNom()).append(",")
                    .append("}");
            if (i < doctors.size() - 1)
            {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    private static String convertConsultationsToJson(ArrayList<Consultation> consultations){
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < consultations.size(); i++)
        {
            Consultation consultation = consultations.get(i);
            json.append("{")
                    .append("\"id\":").append(consultation.getIdConsultation()).append(",")
                    .append("\"doctor\":").append(consultation.getDoctor().getFirstName()).append(" ").append(consultation.getDoctor().getLastName()).append(",")
                    .append("\"patient\":").append(consultation.getPatient().getFirstName()).append(" ").append(consultation.getPatient().getLastName()).append(",")
                    .append("\"date\":\"").append(consultation.getDateConsultation()).append("\",")
                    .append("\"hour\":\"").append(consultation.getHeureConsultation()).append("\",")
                    .append("\"reason\":\"").append(consultation.getRaison()).append("\"")
                    .append("}");
            if (i < consultations.size() - 1)
            {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    public static Map<String, String> lireConfiguration(String cheminFichier) throws IOException {
        return serveurConsultation.lireConfiguration(cheminFichier);
    }
}
