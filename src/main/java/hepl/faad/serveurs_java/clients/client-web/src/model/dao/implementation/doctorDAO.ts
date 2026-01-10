import type { Doctor } from "@/model/entity/doctor";
import type { DoctorVM } from "@/model/viewmodel/doctorVM";
import type { DoctorAccessLayer } from "../doctorAccessLayer";

export class DoctorNotFoundError extends Error {
    constructor(message: string) {
        super(message);
        this.name = "DoctorNotFoundError";
    }
}

export class DoctorDAO implements DoctorAccessLayer {
    private selectedDoctors: Array<Doctor>
    private API_ENDPOINT: string = "http://localhost:8088/api/doctors";

    constructor() {
        this.selectedDoctors = [];
    }

    public getList(): Array<Doctor> {
        return this.selectedDoctors;
    }

    public async load(doctorVM?: DoctorVM): Promise<Array<Doctor>> {
        this.selectedDoctors = [];

        if(doctorVM) {
            const param = new URLSearchParams();
            if(doctorVM.doctorName) {
                param.append("doctor", doctorVM.doctorName);
            }
            if(doctorVM.specialtyName) {
                param.append("specialty", doctorVM.specialtyName);
            }

            const requette = await fetch(`${this.API_ENDPOINT}?${param.toString()}`);

            if(requette.ok) {
                this.selectedDoctors = await requette.json();
            } else {
                throw new DoctorNotFoundError(`Doctor not found with provided criteria.`);
            }
        } else {
            const requette = await fetch(`${this.API_ENDPOINT}`);
            if(requette.ok) {
                this.selectedDoctors = await requette.json();
            } else {
                throw new DoctorNotFoundError(`Doctor not found.`);
            }   
        }

        return this.selectedDoctors;
    }
}