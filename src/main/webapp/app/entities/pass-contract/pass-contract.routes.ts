import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PassContractResolve from './route/pass-contract-routing-resolve.service';

const passContractRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pass-contract').then(m => m.PassContract),
    data: {
      defaultSort: `id,${ASC}`,
    },
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pass-contract-detail').then(m => m.PassContractDetail),
    resolve: {
      passContract: PassContractResolve,
    },
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pass-contract-update').then(m => m.PassContractUpdate),
    resolve: {
      passContract: PassContractResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pass-contract-update').then(m => m.PassContractUpdate),
    resolve: {
      passContract: PassContractResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default passContractRoute;
