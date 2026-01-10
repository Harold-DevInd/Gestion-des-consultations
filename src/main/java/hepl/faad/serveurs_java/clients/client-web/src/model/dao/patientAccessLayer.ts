import type { Patient } from "../entity/patient"
import type { PatientVM } from "../viewmodel/patientVM"

export interface PatientAccessLayer {
    load(patientVM?: PatientVM): Promise<Patient>
    save(patient: Patient): Promise<number>
}