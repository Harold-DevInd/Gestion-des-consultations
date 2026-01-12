<script setup lang="ts">
    import { computed, onMounted, ref, type Ref } from "vue";
    import type { Consultation } from "../model/entity/consultation";
    import type { Doctor } from "@/model/entity/doctor";
    import type { specialty } from "@/model/entity/specialty";
    import { DoctorDAO } from "@/model/dao/implementation/doctorDAO";
    import { SpecialtyDAO } from "@/model/dao/implementation/specialiteDAO";
    import type { DoctorAccessLayer } from "@/model/dao/doctorAccessLayer";
    import type { SpecialtyAccessLayer } from "@/model/dao/specialiteAccessLayer";
import { ConsultationDAO } from "@/model/dao/implementation/consultationDAO";
import type { ConsultationAccessLayer } from "@/model/dao/consultationAccessLayer";

    const props = defineProps<{
        patientId: number;
    }>();

    const emits = defineEmits<{
        (e: "return-to-consultations"): void;
        (e: "reservation-effectue", payload: { idConsultation: number, raison: string }): void;
    }>();

    const consultationsDisponible = ref<Consultation[]>([]);
    const doctorDAO: DoctorAccessLayer = new DoctorDAO();
    const specialtyDAO: SpecialtyAccessLayer = new SpecialtyDAO();
    const consultationDAO: ConsultationAccessLayer = new ConsultationDAO();

    const listeDoctors = ref<string[]>([]);
    const listeSpecialties = ref<string[]>([]);

    const selectedDoctor = ref<string>("");
    const selectedSpecialty = ref<string>("");

    const consultationFiltre = computed(() =>{ 
        return consultationsDisponible.value.filter((consultation) => {
            const choixDoctor = selectedDoctor.value === "" || consultation.doctor === selectedDoctor.value;
            const choixSpecialty = selectedSpecialty.value === "" || consultation.specialty === selectedSpecialty.value;
            return choixDoctor && choixSpecialty;
        });
    })

    onMounted(async () => {
        await doctorDAO.load();
        await specialtyDAO.load();
        await consultationDAO.load();

        const consultationLibre = consultationDAO.getList().filter((consultation) => consultation.raison == "" || consultation.raison == "null");

        for(const consult of consultationLibre) {
            console.log(`RendezVous libre id : ${consult.idConsultattion}`);
            consultationsDisponible.value.push(consult);
        }
        for(const spe of specialtyDAO.getList()) {
            listeSpecialties.value.push(`${spe.nom}`);
        }
        for(const doc of doctorDAO.getList()) {
            listeDoctors.value.push(`${doc.lastName} ${doc.firstName}`);
        }
    });

    function reservation(consultation: Consultation) {
        const raison = window.prompt("Veuillez entrer la raison du rendez-vous :");

        if (!raison) {
            alert("La raison du rendez-vous est obligatoire.");
            return;
        }

        if(raison.trim() === "") {
            alert("La raison du rendez-vous ne peut pas être vide.");
            return;
        }

        console.log("RendezVousPage.vue - reservation - consultation réservée :", consultation);
        emits("reservation-effectue", { idConsultation: consultation.idConsultattion ?? 0, raison: raison});
    }
</script>

<template>
  <div class="booking-panel">
    <h3>Prendre un nouveau rendez-vous</h3>

    <div class="filters">
      <div class="filter-group">
        <label>Spécialité :</label>
        <select v-model="selectedSpecialty">
          <option value="">-- Toutes --</option>
          <option v-for="spec in listeSpecialties" :key="spec" :value="spec">
            {{ spec }}
          </option>
        </select>
      </div>

      <div class="filter-group">
        <label>Médecin :</label>
        <select v-model="selectedDoctor">
          <option value="">-- Tous --</option>
          <option v-for="doc in listeDoctors" :key="doc" :value="doc">
            {{ doc }}
          </option>
        </select>
      </div>
    </div>

    <table class="consultation-table">
      <thead>
        <tr>
          <th>Date</th>
          <th>Heure</th>
          <th>Médecin</th>
          <th>Spécialité</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="consultation in consultationFiltre" :key="consultation.idConsultattion ?? 0">
          <td>{{ consultation.dateConsultation }}</td>
          <td>{{ consultation.heureConsultation }}</td>
          <td>{{ consultation.doctor }}</td>
          <td>{{ consultation.specialty }}</td>
          <td>
            <button class="btn-book" @click="reservation(consultation)">
              Réserver
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="consultationFiltre.length === 0" class="no-result">
      Aucune disponibilité pour ces critères.
    </p>

    <div class="footer">
      <button class="btn-cancel" @click="$emit('return-to-consultations')">
        Annuler / Retour
      </button>
    </div>
  </div>
</template>

<style scoped>
.booking-panel {
  border: 1px solid #ccc;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 20px;
}

.filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  background: #f0f8ff;
  padding: 15px;
  border-radius: 6px;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

select {
  padding: 5px;
  min-width: 150px;
}

.slots-table {
  width: 100%;
  border-collapse: collapse;
}

.consultation-table th, .consultation-table td {
  border-bottom: 1px solid #eee;
  padding: 10px;
  text-align: left;
}

.btn-book {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-book:hover {
  background-color: #2980b9;
}

.btn-cancel {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 20px;
}

.no-result {
  text-align: center;
  color: #888;
  margin-top: 20px;
}
</style>
