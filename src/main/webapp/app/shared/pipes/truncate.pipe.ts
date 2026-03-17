import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate',
  standalone: true,
})
export default class TruncatePipe implements PipeTransform {
  transform(value: string | null | undefined, limit = 80, suffix = '…'): string {
    if (value == null || value === '') {
      return '';
    }
    const str = String(value);
    if (str.length <= limit) {
      return str;
    }
    return str.slice(0, limit).trim() + suffix;
  }
}
