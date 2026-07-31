import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IFy2026AgencySpend, IFy2026ContractSpend, IFy2026PoSpend } from './fy2026.model';

@Injectable({ providedIn: 'root' })
export class Fy2026Service {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fy2026');

  getSpendByAgency(): Observable<IFy2026AgencySpend[]> {
    return this.http.get<IFy2026AgencySpend[]>(`${this.resourceUrl}/it-spend-by-agency`);
  }

  getSpendByContract(agencyAcronym: string): Observable<IFy2026ContractSpend[]> {
    const params = new HttpParams().set('agencyAcronym', agencyAcronym);
    return this.http.get<IFy2026ContractSpend[]>(`${this.resourceUrl}/it-spend-by-contract`, { params });
  }

  getSpendByPo(agencyAcronym: string, contractNumber: string | null): Observable<IFy2026PoSpend[]> {
    let params = new HttpParams().set('agencyAcronym', agencyAcronym);
    if (contractNumber != null && contractNumber !== '') {
      params = params.set('contractNumber', contractNumber);
    }
    return this.http.get<IFy2026PoSpend[]>(`${this.resourceUrl}/it-spend-by-po`, { params });
  }

  getAwardedByAgency(): Observable<IFy2026AgencySpend[]> {
    return this.http.get<IFy2026AgencySpend[]>(`${this.resourceUrl}/it-awarded-by-agency`);
  }

  getAwardedByContract(agencyAcronym: string): Observable<IFy2026ContractSpend[]> {
    const params = new HttpParams().set('agencyAcronym', agencyAcronym);
    return this.http.get<IFy2026ContractSpend[]>(`${this.resourceUrl}/it-awarded-by-contract`, { params });
  }
}
