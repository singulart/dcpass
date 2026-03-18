import { Pipe, PipeTransform } from '@angular/core';

import dayjs from 'dayjs/esm';

/**
 * Formats a date for display. Accepts dayjs objects (from REST API) or ISO date strings (from MCP tool JSON).
 */
@Pipe({
  name: 'formatDate',
  standalone: true,
})
export default class FormatDatePipe implements PipeTransform {
  transform(value: dayjs.Dayjs | string | null | undefined): string {
    if (value == null) {
      return '';
    }
    const d = typeof value === 'string' ? dayjs(value) : value;
    return d.isValid() ? d.format('D MMM YYYY') : String(value);
  }
}
