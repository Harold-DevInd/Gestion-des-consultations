import type { specialty } from "../entity/specialty"
import type { SpecialtyVM } from "../viewmodel/specialiteVM"

export interface SpecialtyAccessLayer {
    load(): Promise<Array<specialty>>
    getList(): Array<specialty>
}