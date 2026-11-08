import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from '../api';

export interface Producto {
  id: string;
  nombre: string;
  url: string;
  tienda: string;
  precioInicial: number;
  precioActual: number;
  fechaRegistro: string;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  constructor(private http: HttpClient) {}

  listar() {
    return this.http.get<Producto[]>(`${API}/productos`);
  }

  crear(url: string, nombre: string) {
    return this.http.post<Producto>(`${API}/productos`, { url, nombre });
  }

  eliminar(id: string) {
    return this.http.delete<void>(`${API}/productos/${id}`);
  }
}
