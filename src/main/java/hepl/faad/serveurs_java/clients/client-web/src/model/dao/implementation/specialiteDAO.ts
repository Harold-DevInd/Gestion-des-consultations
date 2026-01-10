import type { specialty } from "@/model/entity/specialty"
import type { SpecialtyAccessLayer } from "../specialiteAccessLayer"
import type { SpecialtyVM } from "@/model/viewmodel/specialiteVM"

export class SpecialtyNotFoundError extends Error {
    constructor(message: string) {
        super(message)
        this.name = "SpecialtyNotFoundError"
    }
}

export class SpecialtyDAO implements SpecialtyAccessLayer {
    private selectedSpecialties: Array<specialty>
    private API_ENDPOINT: string = "http://localhost:8088/api/specialities"

    constructor() {
        this.selectedSpecialties = []
    }

    public getList(): Array<specialty> {
        return this.selectedSpecialties;
    }

    public async load(): Promise<Array<specialty>> {
        this.selectedSpecialties = []

        const requette = await fetch(`${this.API_ENDPOINT}`)

        if(requette.ok) {
            this.selectedSpecialties = await requette.json()
        } else {
            throw new SpecialtyNotFoundError(`Specialty not found with provided criteria.`)
        }

        return this.selectedSpecialties;
    }
}