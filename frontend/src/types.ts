export type Status = 'NEW' | 'CONTACTED' | 'PROPOSAL' | 'WON' | 'LOST';
export const stages: { id: Status; label: string; color: string }[] = [
  { id: 'NEW', label: 'Novos', color: '#8b6cdd' }, { id: 'CONTACTED', label: 'Em contato', color: '#4d93bf' },
  { id: 'PROPOSAL', label: 'Proposta', color: '#d4993a' }, { id: 'WON', label: 'Ganhos', color: '#36997c' }, { id: 'LOST', label: 'Perdidos', color: '#c9787f' },
];
export interface Profile { id: string; name: string; email: string }
export interface Company { id: string; name: string; email: string }
export interface Lead { id: string; name: string; email: string; phone: string; companyId: string; companyName: string; status: Status; value: number; notes: string; createdAt: string; updatedAt: string }
export interface LeadPage { content: Lead[]; totalElements: number; totalPages: number; number: number }
export interface Dashboard { companies: number; leads: number; pipelineValue: number; wonValue: number; stages: { status: Status; count: number; value: number }[] }
export const money = (n: number) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(n);
