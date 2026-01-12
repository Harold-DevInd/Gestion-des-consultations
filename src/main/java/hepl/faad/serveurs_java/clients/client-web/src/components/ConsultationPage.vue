<script setup lang="ts">
    import type { Consultation } from "@/model/entity/consultation";
    import { ref } from "vue";

    const props = defineProps<{
        consultations: Consultation[];
        patientName: string;
    }>();

    const emits = defineEmits<{
        (e: "logout"): void;
        (e: "go-to-reservation"): void;
        (e: "delete-consultation", consultationId: number): void;
    }>();

    function suppprimerConsultation(consultationId: number) {
        const confirmation = window.confirm("Êtes-vous sûr de vouloir supprimer cette consultation ?");

        if (confirmation) {
            emits("delete-consultation", consultationId);
        }
    }
</script>

<template>
  <div class="list-container">
    <div class="header">
      <h2>Rendez-vous pris</h2>
      <p v-if="patientName">Bienvenue, {{ patientName }}</p>
    </div>

    <div v-if="consultations.length === 0" class="empty-msg">
      <p>Aucun rendez-vous prévu pour le moment.</p>
    </div>

    <table v-else class="appt-table">
      <thead>
        <tr>
          <th>Date</th>
          <th>Heure</th>
          <th>Médecin</th>
          <th>Spécialité</th>
          <th>Raison</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="consult in consultations" :key="consult.idConsultattion ?? ''" >
          <td>{{ consult.dateConsultation }}</td>
          <td>{{ consult.heureConsultation }}</td>
          <td>{{ consult.doctor }}</td>
          <td>{{ consult.specialty }}</td>
          <td>{{ consult.raison }}</td>
          <td>
            <button class="btn-delete" @click="suppprimerConsultation(consult.idConsultattion ?? 0)">
              Supprimer
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="actions-bar">
      <button class="btn-logout" @click="$emit('logout')">Logout</button>

      <button class="btn-new" @click="$emit('go-to-reservation')">
        Prendre un autre rendez-vous
      </button>
    </div>
  </div>
</template>

<style scoped>
.list-container {
  max-width: 800px;
  margin: 0 auto;
}

.header {
  margin-bottom: 20px;
  text-align: center;
}

/* Style du tableau */
.appt-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.appt-table th,
.appt-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.appt-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.appt-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

/* Boutons */
.actions-bar {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn-delete {
  background-color: #ff4d4d;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-logout {
  background-color: #666;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-new {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.empty-msg {
  text-align: center;
  font-style: italic;
  color: #888;
  margin: 20px 0;
}
</style>
