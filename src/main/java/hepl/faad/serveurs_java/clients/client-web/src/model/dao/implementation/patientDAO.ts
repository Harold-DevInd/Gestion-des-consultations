import type { Patient } from "@/model/entity/patient";
import type { PatientVM } from "@/model/viewmodel/patientVM";
import type { PatientAccessLayer } from "../patientAccessLayer";

export class PatientNotFoundError extends Error {
    constructor(message: string) {
        super(message);
        this.name = "PatientNotFoundError";
    }
}

export class PatientDAO implements PatientAccessLayer {
    private API_ENDPOINT: string = "http://localhost:8088/api/patients";

    public async load(patientVM?: PatientVM): Promise<Patient> {
        const requette = await fetch(`${this.API_ENDPOINT}/${patientVM?.patientId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })

        if(requette.ok) {
            const response = await requette.json();
            return response;
        } else {
            throw new PatientNotFoundError("Patient non trouve");
        }
    }

    public async save(patient: Patient): Promise<number> {
        const requette = await fetch(`${this.API_ENDPOINT}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(patient)
        });
        
        if(requette.ok){
            const response = await requette.json();
            return response.id;
        } else {
            throw new Error("Erreur lors de la sauvegarde du patient");
        }
        
    }
}