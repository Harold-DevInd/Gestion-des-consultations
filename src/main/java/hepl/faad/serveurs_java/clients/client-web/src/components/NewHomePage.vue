<script lang="ts" setup>
import { ref, onMounted } from "vue";

// Typage des props
interface Props {
  initialValue: number;
  text: string;
  finalValue: number;
}
const props = defineProps<Props>();
// Typage des emits
interface Emits {
  (e: "increment", payload: { text: string; value: number }): void;
  (e: "reach-final-value", payload: { text: string; value: number }): void;
}
const emit = defineEmits<Emits>();
// État réactif typé
const count = ref<number>(0);
// Méthodes
function increment(): void {
  if (count.value < props.finalValue) {
    count.value++;
    emit("increment", { text: props.text, value: count.value });
    if (count.value === props.finalValue) {
      emit("reach-final-value", { text: props.text, value: count.value });
    }
  } else {
    console.log(`${props.text} a atteint sa valeur finale`);
  }
}
// Cycle de vie
onMounted(() => {
  count.value = props.initialValue;
  console.log(`Compteur initial pour ${props.text} = ${props.initialValue}`);
});
</script>
<template>
  <div class="cadre">
    <span id="texte">
      {{ text }} :
      <span class="compteur">{{ count }}</span>
    </span>
    <button @click="increment">incrémente</button>
  </div>
</template>
<style scoped>
.cadre {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 10px;
  border: 2px solid #ccc;
  border-radius: 12px;
  background-color: #f9f9f9;
  width: 300px;
  height: 50px;
}
#texte {
  font-weight: bold;
  font-size: 16px;
  font-family: "Arial", sans-serif;
  width: 170px;
}
.compteur {
  color: red;
}
button {
  font-weight: bold;
  width: 120px;
  height: 40px;
  border-radius: 10px;
  font-size: 16px;
  background-color: #fff9c4;
}
</style>
