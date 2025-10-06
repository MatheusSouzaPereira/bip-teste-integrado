import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from './services/beneficio.service';
import { Beneficio, CreateUpdateBeneficioDto, TransferRequest } from './models/beneficio';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private service = inject(BeneficioService);

  title = 'BIP Benefícios';
  beneficios: Beneficio[] = [];
  loading = false;
  error?: string;
  success?: string;

  novo: CreateUpdateBeneficioDto = { nome: '', descricao: '', valor: 0, ativo: true };
  transferencia: TransferRequest = { fromId: 0, toId: 0, amount: 0 };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true; this.error = undefined;
    this.service.list().subscribe({
      next: (data) => { this.beneficios = data; this.loading = false; },
      error: (err) => { this.error = this.parseError(err); this.loading = false; }
    });
  }

  criar() {
    this.clearMessages();
    if (!this.novo.nome || this.novo.valor == null) { this.error = 'Preencha nome e valor'; return; }
    this.service.create(this.novo).subscribe({
      next: (b) => { this.success = `Criado ID ${b.id}`; this.novo = { nome: '', descricao: '', valor: 0, ativo: true }; this.load(); },
      error: (err) => { this.error = this.parseError(err); }
    });
  }

  remover(id?: number) {
    if (!id) return;
    this.clearMessages();
    this.service.delete(id).subscribe({
      next: () => { this.success = 'Removido'; this.load(); },
      error: (err) => { this.error = this.parseError(err); }
    });
  }

  transferir() {
    this.clearMessages();
    if (!this.transferencia.fromId || !this.transferencia.toId || !this.transferencia.amount) {
      this.error = 'Preencha origem, destino e valor'; return;
    }
    this.service.transfer(this.transferencia).subscribe({
      next: () => { this.success = 'Transferência realizada'; this.load(); },
      error: (err) => { this.error = this.parseError(err); }
    });
  }

  private parseError(err: any): string {
    if (err?.error?.message) return err.error.message;
    if (typeof err?.error === 'string') return err.error;
    return 'Ocorreu um erro ao processar a requisição.';
  }

  private clearMessages() { this.error = undefined; this.success = undefined; }
}
