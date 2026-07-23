import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPassPayment, NewPassPayment } from '../pass-payment.model';

type RestOf<T extends IPassPayment | NewPassPayment> = Omit<
  T,
  'paymentDate' | 'invoiceDate' | 'estPaymentDate' | 'recordUpdatedDate' | 'recordCreated' | 'dcsRecCrtDttm' | 'dcsLastModDttm'
> & {
  paymentDate?: string | null;
  invoiceDate?: string | null;
  estPaymentDate?: string | null;
  recordUpdatedDate?: string | null;
  recordCreated?: string | null;
  dcsRecCrtDttm?: string | null;
  dcsLastModDttm?: string | null;
};

export type RestPassPayment = RestOf<IPassPayment>;

export type NewRestPassPayment = RestOf<NewPassPayment>;

export type EntityResponseType = HttpResponse<IPassPayment>;
export type EntityArrayResponseType = HttpResponse<IPassPayment[]>;

@Injectable({ providedIn: 'root' })
export class PassPaymentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pass-payments');

  create(passPayment: NewPassPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(passPayment);
    return this.http
      .post<RestPassPayment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(passPayment: IPassPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(passPayment);
    return this.http
      .put<RestPassPayment>(`${this.resourceUrl}/${encodeURIComponent(this.getPassPaymentIdentifier(passPayment))}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPassPayment>(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPassPayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' });
  }

  getPassPaymentIdentifier(passPayment: Pick<IPassPayment, 'id'>): number {
    return passPayment.id;
  }

  comparePassPayment(o1: Pick<IPassPayment, 'id'> | null, o2: Pick<IPassPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getPassPaymentIdentifier(o1) === this.getPassPaymentIdentifier(o2) : o1 === o2;
  }

  addPassPaymentToCollectionIfMissing<Type extends Pick<IPassPayment, 'id'>>(
    passPaymentCollection: Type[],
    ...passPaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const passPayments: Type[] = passPaymentsToCheck.filter(isPresent);
    if (passPayments.length > 0) {
      const passPaymentCollectionIdentifiers = passPaymentCollection.map(passPaymentItem => this.getPassPaymentIdentifier(passPaymentItem));
      const passPaymentsToAdd = passPayments.filter(passPaymentItem => {
        const passPaymentIdentifier = this.getPassPaymentIdentifier(passPaymentItem);
        if (passPaymentCollectionIdentifiers.includes(passPaymentIdentifier)) {
          return false;
        }
        passPaymentCollectionIdentifiers.push(passPaymentIdentifier);
        return true;
      });
      return [...passPaymentsToAdd, ...passPaymentCollection];
    }
    return passPaymentCollection;
  }

  protected convertDateFromClient<T extends IPassPayment | NewPassPayment>(passPayment: T): RestOf<T> {
    return {
      ...passPayment,
      paymentDate: passPayment.paymentDate?.toJSON() ?? null,
      invoiceDate: passPayment.invoiceDate?.toJSON() ?? null,
      estPaymentDate: passPayment.estPaymentDate?.toJSON() ?? null,
      recordUpdatedDate: passPayment.recordUpdatedDate?.toJSON() ?? null,
      recordCreated: passPayment.recordCreated?.toJSON() ?? null,
      dcsRecCrtDttm: passPayment.dcsRecCrtDttm?.toJSON() ?? null,
      dcsLastModDttm: passPayment.dcsLastModDttm?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPassPayment: RestPassPayment): IPassPayment {
    return {
      ...restPassPayment,
      paymentDate: restPassPayment.paymentDate ? dayjs(restPassPayment.paymentDate) : undefined,
      invoiceDate: restPassPayment.invoiceDate ? dayjs(restPassPayment.invoiceDate) : undefined,
      estPaymentDate: restPassPayment.estPaymentDate ? dayjs(restPassPayment.estPaymentDate) : undefined,
      recordUpdatedDate: restPassPayment.recordUpdatedDate ? dayjs(restPassPayment.recordUpdatedDate) : undefined,
      recordCreated: restPassPayment.recordCreated ? dayjs(restPassPayment.recordCreated) : undefined,
      dcsRecCrtDttm: restPassPayment.dcsRecCrtDttm ? dayjs(restPassPayment.dcsRecCrtDttm) : undefined,
      dcsLastModDttm: restPassPayment.dcsLastModDttm ? dayjs(restPassPayment.dcsLastModDttm) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPassPayment>): HttpResponse<IPassPayment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPassPayment[]>): HttpResponse<IPassPayment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
