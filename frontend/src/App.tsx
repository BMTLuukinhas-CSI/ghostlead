import { useEffect, useId, useRef, useState } from 'react';
import type { FormEvent, ReactNode } from 'react';
import { ArrowRight, ArrowUpRight, Building2, Check, ChevronLeft, ChevronRight, CircleDollarSign, CircleHelp, Columns3, LayoutDashboard, LogOut, Mail, MoreHorizontal, Plus, Search, Settings2, ShieldCheck, Sparkles, Trash2, Users, X } from 'lucide-react';
import { api, ApiError } from './api';
import { stages, money } from './types';
import type { Company, Dashboard, Lead, LeadPage, Profile } from './types';

type View = 'dashboard' | 'leads' | 'pipeline' | 'companies' | 'account';
type Editor = { kind: 'lead'; value?: Lead } | { kind: 'company'; value?: Company } | null;
const errorText = (e: unknown) => e instanceof Error ? e.message : 'Não foi possível conectar ao servidor.';
const initials = (name: string) => name.split(' ').filter(Boolean).slice(0, 2).map(x => x[0]).join('').toUpperCase();
function Ghost({ size = 30 }: { size?: number }) { return <svg width={size} height={size} viewBox="0 0 48 48" fill="none" aria-hidden="true"><path d="M8 39V22a16 16 0 0 1 32 0v17c0 3-4 1-6-1-3 4-6 5-10 1-4 4-7 3-10-1-2 2-6 4-6 1Z" fill="currentColor"/><ellipse cx="18" cy="23" rx="2.2" ry="3" fill="#7654d8"/><ellipse cx="30" cy="23" rx="2.2" ry="3" fill="#7654d8"/><path d="M21 30q3 3 6 0" stroke="#7654d8" strokeWidth="2" strokeLinecap="round"/></svg>; }
function Brand() { return <div className="brand"><span className="brand-icon"><Ghost /></span><span>ghostlead<span className="brand-dot">.</span></span></div>; }
function Loading() { return <div className="loading" role="status"><span className="spinner" /> Carregando seus dados…</div>; }
function Empty({ title, children, action }: { title: string; children: ReactNode; action?: ReactNode }) { return <div className="empty"><div className="empty-ghost"><Ghost size={42} /></div><h3>{title}</h3><p>{children}</p>{action}</div>; }
function Dialog({ title, children, close }: { title: string; children: ReactNode; close: () => void }) {
  const titleId = useId(); const ref = useRef<HTMLDialogElement>(null);
  useEffect(() => { const dialog = ref.current!; dialog.showModal(); return () => dialog.close(); }, []);
  return <dialog aria-labelledby={titleId} ref={ref} onCancel={e => { e.preventDefault(); close(); }}><div className="dialog-heading"><h2 id={titleId}>{title}</h2><button className="icon-button" type="button" aria-label="Fechar janela" onClick={close}><X size={20} /></button></div>{children}</dialog>;
}
function Auth({ done }: { done: (p: Profile) => void }) {
  const [register, setRegister] = useState(false); const [busy, setBusy] = useState(false); const [error, setError] = useState('');
  async function submit(e: FormEvent<HTMLFormElement>) { e.preventDefault(); setBusy(true); setError(''); const data = Object.fromEntries(new FormData(e.currentTarget)); try { done(await api<Profile>(`/auth/${register ? 'register' : 'login'}`, 'POST', data)); } catch (e) { setError(errorText(e)); } finally { setBusy(false); } }
  return <div className="auth"><section className="auth-story"><Brand /><div><span className="eyebrow">MENOS COMPLICAÇÃO. MAIS CONEXÕES.</span><h1>Grandes negócios.<br />Uma rotina<br /><em>mais leve.</em></h1><p>Organize seus contatos, acompanhe cada conversa e dê espaço para a sua próxima conquista.</p><div className="auth-art"><div className="ghost-orbit"><Ghost size={140} /></div><span className="art-chip chip-one"><Check size={18} /> Tudo no seu lugar</span><span className="art-chip chip-two"><Sparkles size={18} /> Sua próxima oportunidade</span></div></div><span className="auth-foot">Seu CRM, com um toque de leveza.</span></section><section className="auth-panel"><div className="auth-form"><span className="pill"><ShieldCheck size={15} /> Seu espaço de trabalho</span><h2>{register ? 'Vamos começar?' : 'Bom ter você por aqui.'}</h2><p>{register ? 'Crie sua conta e organize suas primeiras oportunidades.' : 'Entre para continuar de onde parou.'}</p><form on