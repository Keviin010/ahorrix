export function precio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(valor);
}

export type Tendencia = 'sube' | 'baja' | 'igual';

export function tendencia(inicial: number, actual: number): Tendencia {
  if (actual > inicial) return 'sube';
  if (actual < inicial) return 'baja';
  return 'igual';
}

export function porcentaje(inicial: number, actual: number): number {
  if (inicial === 0) return 0;
  return Math.round((Math.abs(actual - inicial) / inicial) * 100);
}

export function fechaCorta(iso: string): string {
  return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short' })
    .format(new Date(iso)).replace('.', '');
}

export function fechaLarga(iso: string): string {
  return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
    .format(new Date(iso)).replace('.', '');
}
