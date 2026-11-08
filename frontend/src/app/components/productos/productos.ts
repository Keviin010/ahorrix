import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavComponent } from '../nav/nav';
import { Producto, ProductoService } from '../../services/producto';
import { fechaLarga, precio } from '../../formato';

@Component({
  selector: 'app-productos',
  imports: [FormsModule, NavComponent],
  templateUrl: './productos.html',
  styleUrl: './productos.css'
})
export class ProductosComponent implements OnInit {
  productos = signal<Producto[]>([]);
  cargando = signal(false);
  error = signal('');
  url = '';
  nombre = '';

  constructor(private servicio: ProductoService) {}

  ngOnInit(): void {
    this.servicio.listar().subscribe({
      next: lista => this.productos.set(lista),
      error: err => this.error.set(err.error?.mensaje || 'No pudimos cargar tus productos.')
    });
  }

  agregar(): void {
    this.error.set('');
    if (!this.url.trim()) {
      this.error.set('Pega el enlace del producto.');
      return;
    }

    this.cargando.set(true);
    this.servicio.crear(this.url.trim(), this.nombre.trim()).subscribe({
      next: producto => {
        this.productos.update(lista => [producto, ...lista]);
        this.url = '';
        this.nombre = '';
        this.cargando.set(false);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(err.error?.mensaje || 'No pudimos agregar el producto.');
      }
    });
  }

  eliminar(producto: Producto): void {
    this.servicio.eliminar(producto.id).subscribe({
      next: () => this.productos.update(lista => lista.filter(p => p.id !== producto.id)),
      error: err => this.error.set(err.error?.mensaje || 'No pudimos quitar el producto.')
    });
  }

  precio(valor: number): string {
    return precio(valor);
  }

  fecha(iso: string): string {
    return fechaLarga(iso);
  }
}
