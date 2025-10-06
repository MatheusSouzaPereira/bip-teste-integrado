export interface Beneficio {
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo: boolean;
  version?: number;
}

export interface CreateUpdateBeneficioDto {
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
}

export interface TransferRequest {
  fromId: number;
  toId: number;
  amount: number;
}