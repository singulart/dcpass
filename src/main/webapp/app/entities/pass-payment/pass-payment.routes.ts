import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PassPaymentResolve from './route/pass-payment-routing-resolve.service';

const passPaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pass-payment').then(m => m.PassPayment),
    data: {
      defaultSort: `id,${ASC}`,
    },
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pass-payment-detail').then(m => m.PassPaymentDetail),
    resolve: {
      passPayment: PassPaymentResolve,
    },
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pass-payment-update').then(m => m.PassPaymentUpdate),
    resolve: {
      passPayment: PassPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pass-payment-update').then(m => m.PassPaymentUpdate),
    resolve: {
      passPayment: PassPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default passPaymentRoute;
