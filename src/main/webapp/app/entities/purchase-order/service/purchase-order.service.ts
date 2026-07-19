import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPurchaseOrder, NewPurchaseOrder } from '../purchase-order.model';

type RestOf<T extends IPurchaseOrder | NewPurchaseOrder> = Omit<T, 'orderedDate' | 'createDate' | 'dcsLastModDttm' | 'dcsRecCrtDttm'> & {
  orderedDate?: string | null;
  createDate?: string | null;
  dcsLastModDttm?: string | null;
  dcsRecCrtDttm?: string | null;
};

export type RestPurchaseOrder = RestOf<IPurchaseOrder>;

export type NewRestPurchaseOrder = RestOf<NewPurchaseOrder>;

export type EntityResponseType = HttpResponse<IPurchaseOrder>;
export type EntityArrayResponseType = HttpResponse<IPurchaseOrder[]>;

@Injectable({ providedIn: 'root' })
export class PurchaseOrderService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/purchase-orders');

  create(purchaseOrder: NewPurchaseOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseOrder);
    return this.http
      .post<RestPurchaseOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(purchaseOrder: IPurchaseOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseOrder);
    return this.http
      .put<RestPurchaseOrder>(`${this.resourceUrl}/${encodeURIComponent(this.getPurchaseOrderIdentifier(purchaseOrder))}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPurchaseOrder>(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPurchaseOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${encodeURIComponent(id)}`, { observe: 'response' });
  }

  getPurchaseOrderIdentifier(purchaseOrder: Pick<IPurchaseOrder, 'id'>): number {
    return purchaseOrder.id;
  }

  comparePurchaseOrder(o1: Pick<IPurchaseOrder, 'id'> | null, o2: Pick<IPurchaseOrder, 'id'> | null): boolean {
    return o1 && o2 ? this.getPurchaseOrderIdentifier(o1) === this.getPurchaseOrderIdentifier(o2) : o1 === o2;
  }

  addPurchaseOrderToCollectionIfMissing<Type extends Pick<IPurchaseOrder, 'id'>>(
    purchaseOrderCollection: Type[],
    ...purchaseOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const purchaseOrders: Type[] = purchaseOrdersToCheck.filter(isPresent);
    if (purchaseOrders.length > 0) {
      const purchaseOrderCollectionIdentifiers = purchaseOrderCollection.map(purchaseOrderItem =>
        this.getPurchaseOrderIdentifier(purchaseOrderItem),
      );
      const purchaseOrdersToAdd = purchaseOrders.filter(purchaseOrderItem => {
        const purchaseOrderIdentifier = this.getPurchaseOrderIdentifier(purchaseOrderItem);
        if (purchaseOrderCollectionIdentifiers.includes(purchaseOrderIdentifier)) {
          return false;
        }
        purchaseOrderCollectionIdentifiers.push(purchaseOrderIdentifier);
        return true;
      });
      return [...purchaseOrdersToAdd, ...purchaseOrderCollection];
    }
    return purchaseOrderCollection;
  }

  protected convertDateFromClient<T extends IPurchaseOrder | NewPurchaseOrder>(purchaseOrder: T): RestOf<T> {
    return {
      ...purchaseOrder,
      orderedDate: purchaseOrder.orderedDate?.toJSON() ?? null,
      createDate: purchaseOrder.createDate?.toJSON() ?? null,
      dcsLastModDttm: purchaseOrder.dcsLastModDttm?.toJSON() ?? null,
      dcsRecCrtDttm: purchaseOrder.dcsRecCrtDttm?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPurchaseOrder: RestPurchaseOrder): IPurchaseOrder {
    return {
      ...restPurchaseOrder,
      orderedDate: restPurchaseOrder.orderedDate ? dayjs(restPurchaseOrder.orderedDate) : undefined,
      createDate: restPurchaseOrder.createDate ? dayjs(restPurchaseOrder.createDate) : undefined,
      dcsLastModDttm: restPurchaseOrder.dcsLastModDttm ? dayjs(restPurchaseOrder.dcsLastModDttm) : undefined,
      dcsRecCrtDttm: restPurchaseOrder.dcsRecCrtDttm ? dayjs(restPurchaseOrder.dcsRecCrtDttm) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPurchaseOrder>): HttpResponse<IPurchaseOrder> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPurchaseOrder[]>): HttpResponse<IPurchaseOrder[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
