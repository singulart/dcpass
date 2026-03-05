import { Routes } from '@angular/router';

import passwordRoute from './password/password.route';
import passwordResetFinishRoute from './password-reset/finish/password-reset-finish.route';
import settingsRoute from './settings/settings.route';

const accountRoutes: Routes = [passwordRoute, passwordResetFinishRoute, settingsRoute];

export default accountRoutes;
