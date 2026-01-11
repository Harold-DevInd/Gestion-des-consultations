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

const estConnecte = ref<boolean>(false);
const modeReservation = ref<boolean>(false);
const patientConnecte = ref<Patient | null>(null);
const consultations = ref<Array<Consultation>>([]);
const consultationDAO : ConsultationAccessLayer = new ConsultationDAO();
const patientDAO : PatientAccessLayer = new PatientDAO();
const specialtyDAO : SpecialtyAccessLayer = new SpecialtyDAO();
const doctorDAO : DoctorAccessLayer = new DoctorDAO();

function gererConnexion(patient: Patient) {
  console.log("App.vue - gererConnexion - patient connecté :", patient);
  patientConnecte.value = patient;
  estConnecte.value = true;
  chargerConsultations();
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

function finReservation() {
  console.log("App.vue - finReservation - Réservation terminée, rechargement des consultations");
  modeReservation.value = false;
  chargerConsultations();
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
        :consultations="consultations"
        @logout="gererDeconnexion"
        @go-to-reservation="modeReservation = true"
        @delete-consultation="chargerConsultations"
      />

      <RendezVousPage
        v-if="modeReservation"
        :patient="patientConnecte"
        @return-to-consultations="gererRetourAccueil"
        @reservation-complete="finReservation"
      />
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
