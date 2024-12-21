import { defineStore } from 'pinia';
import { ref } from 'vue';

function initState() {
  return {
    isCollapse: false,
  };
}

export const useAllDataStore = defineStore('allData', () => {
  //ref state
  //computed getters
  //funciton actions
  const state = ref(initState());

  return {
    state,
  };
})
