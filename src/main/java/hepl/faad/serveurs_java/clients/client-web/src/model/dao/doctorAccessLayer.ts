import type { Doctor } from "../entity/doctor"
import type { DoctorVM } from "../viewmodel/doctorVM"

export interface DoctorAccessLayer {
    load(doctorVM?: DoctorVM): Promise<Array<Doctor>>
    getList(): Array<Doctor>
}