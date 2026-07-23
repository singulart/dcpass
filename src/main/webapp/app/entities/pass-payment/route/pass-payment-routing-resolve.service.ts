import { HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPassPayment } from '../pass-payment.model';
import { PassPaymentService } from '../service/pass-payment.service';

const passPaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | IPassPayment> => {
  const id = route.params.id;
  if (id) {
    return inject(PassPaymentService)
      .find(id)
      .pipe(
        mergeMap((passPayment: HttpResponse<IPassPayment>) => {
          if (passPayment.body) {
            return of(passPayment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default passPaymentResolve;
