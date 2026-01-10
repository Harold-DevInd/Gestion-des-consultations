import type { Doctor } from "./doctor"
import type { Patient } from "./patient"

export interface Consultation {
    idConsultattion?: number | null
    doctor: Doctor
    patient?: Patient | null
    heureConsultation: string
    dateConsultation: string
    raison?: string | null
}