import type { Doctor } from "./doctor"
import type { Patient } from "./patient"

export interface Consultation {
    idConsultattion?: number | null
    doctor: string
    patient?: string | null
    specialty: string
    heureConsultation: string
    dateConsultation: string
    raison?: string | null
}