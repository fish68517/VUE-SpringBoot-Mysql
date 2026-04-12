import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTravelStore = defineStore('travel', () => {
  const records = ref([])
  const currentRecord = ref(null)

  const setRecords = (data) => {
    records.value = data
  }

  const setCurrentRecord = (record) => {
    currentRecord.value = record
  }

  const addRecord = (record) => {
    records.value.push(record)
  }

  const updateRecord = (id, updatedRecord) => {
    const index = records.value.findIndex(r => r.id === id)
    if (index !== -1) {
      records.value[index] = updatedRecord
    }
  }

  const deleteRecord = (id) => {
    records.value = records.value.filter(r => r.id !== id)
  }

  return {
    records,
    currentRecord,
    setRecords,
    setCurrentRecord,
    addRecord,
    updateRecord,
    deleteRecord
  }
})
