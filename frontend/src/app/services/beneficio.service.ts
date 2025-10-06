import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio, CreateUpdateBeneficioDto, TransferRequest } from '../models/beneficio';

@Injectable({ providedIn: 'root' })
export class BeneficioService {
  private baseUrl = '/api/v1/beneficios';

  constructor(private http: HttpClient) {}

  list(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.baseUrl}`);
    }

  get(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.baseUrl}/${id}`);
  }

  create(dto: CreateUpdateBeneficioDto): Observable<Beneficio> {
    return this.http.post<Beneficio>(`${this.baseUrl}`, dto);
  }

  update(id: number, dto: CreateUpdateBeneficioDto): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  transfer(req: TransferRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/transfer`, req);
  }
}
