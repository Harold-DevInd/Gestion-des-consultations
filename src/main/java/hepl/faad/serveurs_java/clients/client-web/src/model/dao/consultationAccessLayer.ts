import type { Consultation } from "../entity/consultation"
import type { ConsultationVM } from "../viewmodel/consultationVM"

export interface ConsultationAccessLayer {
    load(consultationVM?: ConsultationVM): Promise<Array<Consultation>>
    getList(): Array<Consultation>
    save(consultation: Consultation): Promise<void>
    delete(item: number | Consultation): Promise<void>
}