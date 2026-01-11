export interface Patient {
  idPatient?: number | null
  firstName: string
  lastName: string
  dateNaissance?: Date | null
  estNouveau?: boolean
}