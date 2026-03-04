import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPassContract, NewPassContract } from '../pass-contract.model';

export type PartialUpdatePassContract = Partial<IPassContract> & Pick<IPassContract, 'id'>;

type RestOf<T extends IPassContract | NewPassContract> = Omit<
  T,
  'awardDate' | 'endDate' | 'startDate' | 'lastModified' | 'recCreatedDate' | 'recUpdatedDate' | 'dcsLastModDttm'
> & {
  awardDate?: string | null;
  endDate?: string | null;
  startDate?: string | null;
  lastModified?: string | null;
  recCreatedDate?: string | null;
  recUpdatedDate?: string | null;
  dcsLastModDttm?: string | null;
};

export type RestPassContract = RestOf<IPassContract>;

export type NewRestPassContract = RestOf<NewPassContract>;

export type PartialUpdateRestPassContract = RestOf<PartialUpdatePassContract>;

export type EntityResponseType = HttpResponse<IPassContract>;
export type EntityArrayResponseType = HttpResponse<IPassContract[]>;

@Injectable({ providedIn: 'root' })
export class PassContractService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pass-contracts');

  create(passContract: NewPassContract): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(passContract);
    return this.http
      .post<RestPassContract>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(passContract: IPassContract): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(passContract);
    return this.http
      .put<RestPassContract>(`${this.resourceUrl}/${encodeURIComponent(this.getPassContractIdentifier(passContract))}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(passContract: PartialUpdatePassContract): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(passContract);
    return this.http
      .patch<RestPassContract>(`${this.resourceUrl}/${encodeURIComponent(this.getPassContractIdentifier(passContract))}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPassContract>(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPassContract[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' });
  }

  getPassContractIdentifier(passContract: Pick<IPassContract, 'id'>): number {
    return passContract.id;
  }

  comparePassContract(o1: Pick<IPassContract, 'id'> | null, o2: Pick<IPassContract, 'id'> | null): boolean {
    return o1 && o2 ? this.getPassContractIdentifier(o1) === this.getPassContractIdentifier(o2) : o1 === o2;
  }

  addPassContractToCollectionIfMissing<Type extends Pick<IPassContract, 'id'>>(
    passContractCollection: Type[],
    ...passContractsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const passContracts: Type[] = passContractsToCheck.filter(isPresent);
    if (passContracts.length > 0) {
      const passContractCollectionIdentifiers = passContractCollection.map(passContractItem =>
        this.getPassContractIdentifier(passContractItem),
      );
      const passContractsToAdd = passContracts.filter(passContractItem => {
        const passContractIdentifier = this.getPassContractIdentifier(passContractItem);
        if (passContractCollectionIdentifiers.includes(passContractIdentifier)) {
          return false;
        }
        passContractCollectionIdentifiers.push(passContractIdentifier);
        return true;
      });
      return [...passContractsToAdd, ...passContractCollection];
    }
    return passContractCollection;
  }

  protected convertDateFromClient<T extends IPassContract | NewPassContract | PartialUpdatePassContract>(passContract: T): RestOf<T> {
    return {
      ...passContract,
      awardDate: passContract.awardDate?.format(DATE_FORMAT) ?? null,
      endDate: passContract.endDate?.format(DATE_FORMAT) ?? null,
      startDate: passContract.startDate?.format(DATE_FORMAT) ?? null,
      lastModified: passContract.lastModified?.toJSON() ?? null,
      recCreatedDate: passContract.recCreatedDate?.toJSON() ?? null,
      recUpdatedDate: passContract.recUpdatedDate?.toJSON() ?? null,
      dcsLastModDttm: passContract.dcsLastModDttm?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPassContract: RestPassContract): IPassContract {
    return {
      ...restPassContract,
      awardDate: restPassContract.awardDate ? dayjs(restPassContract.awardDate) : undefined,
      endDate: restPassContract.endDate ? dayjs(restPassContract.endDate) : undefined,
      startDate: restPassContract.startDate ? dayjs(restPassContract.startDate) : undefined,
      lastModified: restPassContract.lastModified ? dayjs(restPassContract.lastModified) : undefined,
      recCreatedDate: restPassContract.recCreatedDate ? dayjs(restPassContract.recCreatedDate) : undefined,
      recUpdatedDate: restPassContract.recUpdatedDate ? dayjs(restPassContract.recUpdatedDate) : undefined,
      dcsLastModDttm: restPassContract.dcsLastModDttm ? dayjs(restPassContract.dcsLastModDttm) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPassContract>): HttpResponse<IPassContract> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPassContract[]>): HttpResponse<IPassContract[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
