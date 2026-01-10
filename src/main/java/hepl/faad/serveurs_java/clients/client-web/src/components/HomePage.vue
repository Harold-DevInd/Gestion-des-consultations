<script>
export default {
  data() {
    return {
      count: 0,
    };
  },
};
</script>

<template>
  <div class="cadre">
    <span id="texte">
      Mon compteur :
      <span class="compteur">{{ count }}</span>
    </span>
    <button @click="count++">incrémente</button>
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
  background-color: #f9f9f9; /* couleur de fond légère */
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
