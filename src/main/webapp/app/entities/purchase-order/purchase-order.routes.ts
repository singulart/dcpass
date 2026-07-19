import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PurchaseOrderResolve from './route/purchase-order-routing-resolve.service';

const purchaseOrderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchase-order').then(m => m.PurchaseOrder),
    data: {
      defaultSort: `id,${ASC}`,
    },
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchase-order-detail').then(m => m.PurchaseOrderDetail),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchase-order-update').then(m => m.PurchaseOrderUpdate),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchase-order-update').then(m => m.PurchaseOrderUpdate),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchaseOrderRoute;
