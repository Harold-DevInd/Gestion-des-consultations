import type { specialty } from "./specialty"

export interface Doctor {
  idDoctor?: number | null
  firstName: string
  lastName: string
  specialty: specialty
}