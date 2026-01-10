<script setup lang="ts">
import { ref, onMounted } from "vue";
import MyCounter4 from "./components/MyCounter4.vue";
// Typage des paramètres du compteur et des événements
type CounterParam = {
  initialValue: number;
  text: string;
  finalValue: number;
};
type CounterEvent = {
  text: string;
  value: number;
};
// Etats réactifs typés
const params = ref<CounterParam[]>([]);
const message = ref<string>("");
// Méthodes
function handleIncrement(e: CounterEvent): void {
  message.value = `${e.text} a incrémenté sa valeur (${e.value})`;
  console.log(`[APP] ${message.value}`);
}
function handleReachFinalValue(e: CounterEvent): void {
  message.value = `${e.text} a atteint sa valeur finale (${e.value})`;
  console.log(`[APP] ${message.value}`);
}
// Cycle de vie
onMounted(() => {
  params.value = [
    { initialValue: 3, text: "Wagner", finalValue: 10 },
    { initialValue: 1, text: "Caprasse", finalValue: 5 },
    { initialValue: 12, text: "Charlet", finalValue: 18 },
  ];
});
</script>
<template>
  <div class="page">
    <MyCounter4
      v-for="param in params"
      :initialValue="param.initialValue"
      :text="param.text"
      :finalValue="param.finalValue"
      @increment="handleIncrement"
      @reach-final-value="handleReachFinalValue"
    />
    <h2>{{ message }}</h2>
  </div>
</template>
<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(to bottom, #e0f7fa, #ffffff);
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: center;
  align-items: center;
}
</style>
