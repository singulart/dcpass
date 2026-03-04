import { HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPassContract } from '../pass-contract.model';
import { PassContractService } from '../service/pass-contract.service';

const passContractResolve = (route: ActivatedRouteSnapshot): Observable<null | IPassContract> => {
  const id = route.params.id;
  if (id) {
    return inject(PassContractService)
      .find(id)
      .pipe(
        mergeMap((passContract: HttpResponse<IPassContract>) => {
          if (passContract.body) {
            return of(passContract.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default passContractResolve;
