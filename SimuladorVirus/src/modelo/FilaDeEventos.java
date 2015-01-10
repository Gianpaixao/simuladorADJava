package modelo;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class FilaDeEventos {
	
	private int nosInfectados;
	private int eventosNaFila;
	private List<Evento> fila = new LinkedList<Evento>();
	
	
	public int getNosInfectados() {
		return nosInfectados;
	}
	public void setNosInfectados(int nosInfectados) {
		this.nosInfectados = nosInfectados;
	}
	public int getEventosNaFila() {
		return eventosNaFila;
	}
	public void setEventosNaFila(int eventosNaFila) {
		this.eventosNaFila = eventosNaFila;
	}
	public void addNosInfectados(){
		this.nosInfectados++;
	}
	public void remNosInfectados(){
		this.nosInfectados--;
	}
	public List<Evento> getFila() {
		return fila;
	}
	public void setFila(List<Evento> fila) {
		this.fila = fila;
	}
	
	public Evento removeEvento(){
		setEventosNaFila(fila.size()-1);
		Evento atendimento = ((LinkedList<Evento>) fila).removeFirst();
		if(atendimento.getTipo() == TipoEvento.I_P)
		{
			addNosInfectados();
		}
		if(atendimento.getTipo() == TipoEvento.F_I ||atendimento.getTipo() == TipoEvento.R_I)
		{
			remNosInfectados();
		}
		
		return atendimento;
	}
	
	public void addEvento(Evento evento){
		((LinkedList<Evento>) fila).addLast(evento);
		Collections.sort(fila);
		setEventosNaFila(fila.size());		
	}

}
