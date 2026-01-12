<script setup lang="ts">
import { ref, onMounted } from "vue";
import MyCounter4 from "./components/MyCounter4.vue";
import ConsultationPage from "./components/ConsultationPage.vue";
import LoginPage from "./components/LoginPage.vue";
import RendezVousPage from "./components/RendezVousPage.vue";
import type { Patient } from "./model/entity/patient";
import type { Consultation } from "./model/entity/consultation";
import { ConsultationDAO } from "./model/dao/implementation/consultationDAO";
import { DoctorDAO } from "./model/dao/implementation/doctorDAO";
import { PatientDAO } from "./model/dao/implementation/patientDAO";
import { SpecialtyDAO } from "./model/dao/implementation/specialiteDAO";
import type { ConsultationAccessLayer } from "./model/dao/consultationAccessLayer";
import type { DoctorAccessLayer } from "./model/dao/doctorAccessLayer";
import type { PatientAccessLayer } from "./model/dao/patientAccessLayer";
import type { SpecialtyAccessLayer } from "./model/dao/specialiteAccessLayer";
import { C } from "vue-router/dist/router-CWoNjPRp.mjs";
import type { PatientVM } from "./model/viewmodel/patientVM";

const estConnecte = ref<boolean>(false);
const modeReservation = ref<boolean>(false);
const patientConnecte = ref<Patient | null>(null);
const consultations = ref<Array<Consultation>>([]);
const updateConsultation = ref<Consultation | null>(null);
const consultationDAO : ConsultationAccessLayer = new ConsultationDAO();
const patientDAO : PatientAccessLayer = new PatientDAO();
const specialtyDAO : SpecialtyAccessLayer = new SpecialtyDAO();
const doctorDAO : DoctorAccessLayer = new DoctorDAO();

function gererConnexion(patient: Patient) {
  console.log("App.vue - gererConnexion - patient connecté :", patient);

  if(patient.estNouveau) {
    patientDAO.save(patient).then((idNewPatient) => {
      window.alert(`Nouveau patient sauvegardé, id : ${idNewPatient}`);
      patientConnecte.value = patient;
      patientConnecte.value!.idPatient = idNewPatient;
      estConnecte.value = true;
      chargerConsultations();
    })
    .catch((error) => {
      window.alert(`Erreur lors de la sauvegarde du nouveau patient : ${error}`);
    });
  } else {
    const pvm: PatientVM = { patientId: patient.idPatient ?? 0, }
    patientDAO.load(pvm).then((loadedPatient) => {
      console.log("App.vue - gererConnexion - Données du patient chargées :");
      if(loadedPatient.lastName == patient.lastName && loadedPatient.firstName == patient.firstName) {
        window.alert("Connexion réussie !");
        if(patientConnecte.value) {
          patientConnecte.value.idPatient = loadedPatient.idPatient;
          patientConnecte.value.lastName = loadedPatient.lastName;
          patientConnecte.value.firstName = loadedPatient.firstName;
          patientConnecte.value.dateNaissance = loadedPatient.dateNaissance;
        }
        estConnecte.value = true;
        chargerConsultations();
      } else {
        window.alert("Vos données ne correspondent pas !");
      }
    })
    .catch((error) => {
      window.alert(`App.vue - gererConnexion - Erreur lors du chargement des données du patient : ${error}`);
    });
  }
}

function gererDeconnexion() {
  console.log("App.vue - gererDeconnexion - déconnexion en cours");
  patientConnecte.value = null;
  estConnecte.value = false;
  consultations.value = [];
  modeReservation.value = false;
}

function basculerModeReservation() {
  modeReservation.value = !modeReservation.value;
  console.log("App.vue - basculerModeReservation - modeReservation :", modeReservation.value);
}

function gererRetourAccueil() {
  modeReservation.value = false;
  console.log("App.vue - gererRetourAccueil - Retour au mode consultation");
}

async function chargerConsultations() {
  console.log("App.vue - chargerConsultations - patientConnecte.value :", patientConnecte.value);
  if (patientConnecte.value && patientConnecte.value.idPatient != null) {
    // Appel à l'API pour charger les consultations du patient
    try{
      //await specialtyDAO.load();
      //await doctorDAO.load();
      await consultationDAO.load();

      consultations.value = consultationDAO.getList();
    } catch(error){
      console.error("App.vue - chargerConsultations - Erreur lors du chargement des consultations :", error);
    }
  }
}

function finReservation(payload: { idConsultation: number; raison: string }) {
  console.log("App.vue - finReservation - Réservation terminée, rechargement des consultations");

  updateConsultation.value = consultations.value.find(c => c.idConsultattion === payload.idConsultation) || null;
  updateConsultation.value!.raison = payload.raison;
  
  consultationDAO.save(updateConsultation.value!)
    .then(() => {
      console.log("App.vue - finReservation - Consultation mise à jour avec succès");
    })
    .catch((error) => {
      console.error("App.vue - finReservation - Erreur lors de la mise à jour de la consultation :", error);
    });
  modeReservation.value = false;
  chargerConsultations();
}

function deleteConsultation(idConsultation: number) {
  console.log("App.vue - deleteConsultation - Suppression de la consultation ID :", idConsultation);
  consultationDAO.delete(idConsultation)
    .then(() => {
      console.log("App.vue - deleteConsultation - Consultation supprimée avec succès");
      consultations.value = consultations.value.filter(c => c.idConsultattion !== idConsultation);
      chargerConsultations();
    })
    .catch((error) => {
      console.error("App.vue - deleteConsultation - Erreur lors de la suppression de la consultation :", error);
    });
}
</script>

<template>
  <div class="container">
    <h1 class="text-center my-4">Gestion des Consultations Médicales</h1>

    <LoginPage 
      v-if="!estConnecte"
      @login-patient="gererConnexion"
    />

    <div v-else>
      <ConsultationPage
        v-if="!modeReservation"
        :consultations="consultations"
        :patient-name="patientConnecte?.lastName ?? ''"
        @logout="gererDeconnexion"
        @go-to-reservation="modeReservation = true"
        @delete-consultation="chargerConsultations"
      />
      
      <div v-else>
        <RendezVousPage
          :patient="patientConnecte"
          @return-to-consultations="gererRetourAccueil"
          @reservation-effectue="finReservation"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 900px;
  margin: 2rem auto;
  font-family: Arial, sans-serif;
}
</style>
