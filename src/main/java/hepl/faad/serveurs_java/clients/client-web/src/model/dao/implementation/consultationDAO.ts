import type { Consultation } from "@/model/entity/consultation";
import type { ConsultationVM } from "@/model/viewmodel/consultationVM";
import type { ConsultationAccessLayer } from "../consultationAccessLayer";

export class ConsultationNotFoundError extends Error {
    constructor(message: string) {
        super(message);
        this.name = "ConsultationNotFoundError";
    }
}

export class ConsultationDAO implements ConsultationAccessLayer {
    private selectedConsultations: Array<Consultation>
    private API_ENDPOINT: string = "http://localhost:8088/api/consultations";

    constructor() {
        this.selectedConsultations = [];
    }

    public getList(): Array<Consultation> {
        return this.selectedConsultations;
    }

    public async load(consultationVM?: ConsultationVM): Promise<Array<Consultation>> {
        this.selectedConsultations = [];

        if(consultationVM) {
            const param = new URLSearchParams();
            if(consultationVM.dateConsultation) {
                param.append("date", consultationVM.dateConsultation);
            }
            if(consultationVM.doctorName) {
                param.append("doctor", consultationVM.doctorName);
            }
            if(consultationVM.specialtyName) {
                param.append("specialty", consultationVM.specialtyName);
            }
            if(consultationVM.patientId) {
                param.append("patientId", consultationVM.patientId.toString());
            }   
            const requette = await fetch(`${this.API_ENDPOINT}?${param.toString()}`);

            if(requette.ok) {
                this.selectedConsultations = await requette.json();
            } else {
                throw new ConsultationNotFoundError(`Consultation pas trouve avec les criteres fournis.`);
            }
        } else {
            const requette = await fetch(`${this.API_ENDPOINT}`);
            if(requette.ok) {
                this.selectedConsultations = await requette.json();
            }
            else {
                throw new ConsultationNotFoundError(`Consultation non trouve`);
            }   
        }

        return this.selectedConsultations;
    }

    public async save(consultation: Consultation): Promise<void> {
        if(consultation.idConsultattion != null) {
            const newConsultation : Consultation = {
                dateConsultation: consultation.dateConsultation,
                heureConsultation: consultation.heureConsultation,
                doctor: consultation.doctor,
                patient: consultation.patient
            };

            const requette = await fetch(`${this.API_ENDPOINT}/${consultation.idConsultattion}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newConsultation)
            });
        } else {
            const requette = await fetch(`${this.API_ENDPOINT}/${consultation.idConsultattion}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(consultation)
            });
        } 
    }

    public async delete(item: number | Consultation): Promise<void> {
        if(item == null) {
            throw new Error("l item passe est null");
        }
        let id: number
        if(typeof item === 'number') {
            id = item
        } else if(item.idConsultattion != null) {
            id = item.idConsultattion
        } else {
            throw new Error("l id est null");
        }

        const requette = await fetch(`${this.API_ENDPOINT}/${id}`, {
            method: 'DELETE'
        });
        if(!requette.ok){   
            throw new Error("Echec de la suppression de la consultation");
        }   
    }
}