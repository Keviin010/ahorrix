import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { porcentaje, precio } from '../../formato';

@Component({
  selector: 'app-escena',
  templateUrl: './escena.html',
  styleUrl: './escena.css'
})
export class EscenaComponent implements OnInit, OnDestroy {
  precios = [2499000, 2390000, 2260000, 2150000, 1999000];
  objetivo = 2000000;

  tic = signal(0);
  temporizador: ReturnType<typeof setInterval> | undefined;

  paso = computed(() => Math.min(this.tic(), this.precios.length - 1));
  precioActual = computed(() => precio(this.precios[this.paso()]));
  avance = computed(() => this.paso() / (this.precios.length - 1));
  llegoAlFinal = computed(() => this.paso() === this.precios.length - 1);
  ahorro = porcentaje(this.precios[0], this.precios[this.precios.length - 1]);
  objetivoTexto = precio(this.objetivo);

  trazo = this.precios.map((valor, i) => `${i === 0 ? 'M' : 'L'}${this.x(i)} ${this.y(valor)}`).join(' ');
  yObjetivo = this.y(this.objetivo);

  punto = computed(() => {
    const i = this.paso();
    return `translate(${this.x(i)}px, ${this.y(this.precios[i])}px)`;
  });

  ngOnInit(): void {
    this.temporizador = setInterval(() => {
      this.tic.update(t => (t >= this.precios.length + 2 ? 0 : t + 1));
    }, 1400);
  }

  ngOnDestroy(): void {
    clearInterval(this.temporizador);
  }

  private x(indice: number): number {
    return 10 + indice * 70;
  }

  private y(valor: number): number {
    return Math.round(20 + ((this.precios[0] - valor) / 500000) * 90);
  }
}
