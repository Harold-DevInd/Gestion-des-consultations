<script setup lang="ts">
import { ref } from "vue";
import type { Patient } from "../model/entity/patient";

const emit = defineEmits<{
  (e: "login-patient", donnePatient: Patient): void;
}>();

const lastName = ref<string>("");
const firstName = ref<string>("");
const numeroPatient = ref<string>("");
const estNouveau = ref<boolean>(false);

function connexion() {
  if (!lastName.value || !firstName.value) {
    alert("Veuillez remplir tous les champs.");
    return;
  }

  if (!estNouveau.value && !numeroPatient.value) {
    alert("Veuillez entrer votre numéro de patient.");
    return;
  }

  const donnePatient: Patient = {
    lastName: lastName.value,
    firstName: firstName.value,
    estNouveau: estNouveau.value,
    idPatient: estNouveau.value ? undefined : parseInt(numeroPatient.value),
  };

  console.log("LoginPage.vue - connexion - données du patient :", donnePatient);
  emit("login-patient", donnePatient);
}
</script>

<template>
  <div class="login-card">
    <h2>Identification Patient</h2>

    <form @submit.prevent="connexion">
      <div class="form-group">
        <label>Nom :</label>
        <input type="text" v-model="lastName" placeholder="Votre nom" />
      </div>

      <div class="form-group">
        <label>Prénom :</label>
        <input type="text" v-model="firstName" placeholder="Votre prénom" />
      </div>

      <div class="form-group checkbox-row">
        <input type="checkbox" id="chk-nouveau" v-model="estNouveau" />
        <label for="chk-nouveau">Je suis un nouveau patient</label>
      </div>

      <div class="form-group" v-if="!estNouveau">
        <label>Numéro de patient :</label>
        <input type="text" v-model="numeroPatient" placeholder="Ex: 12345" />
      </div>

      <p v-else class="info-text">
        Un numéro vous sera attribué après votre première consultation.
      </p>

      <button type="submit" class="btn-primary">Se connecter</button>
    </form>
  </div>
</template>

<style scoped>
.login-card {
  background: #f9f9f9;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  margin: 2rem auto; /* Centré horizontalement */
  border: 1px solid #ddd;
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  text-align: left;
}

.checkbox-row {
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

label {
  font-weight: bold;
  margin-bottom: 0.5rem;
}

input[type="text"] {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-primary {
  background-color: #42b983; /* Vert Vue.js */
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  width: 100%;
}

.btn-primary:hover {
  background-color: #3aa876;
}

.info-text {
  font-size: 0.9em;
  color: #666;
  font-style: italic;
}
</style>
