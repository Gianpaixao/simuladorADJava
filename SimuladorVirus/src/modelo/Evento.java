package modelo;

public class Evento implements Comparable<Evento>{
	private double instanteEvento;
	private TipoEvento tipo;
	private No no;
	
	public Evento(double instanteEvento, TipoEvento tipo, No no) {
		this.instanteEvento = instanteEvento;
		this.tipo = tipo;
		this.no = no;
	}
	
	public TipoEvento getTipo() {
		return this.tipo;
	}

	public double getInstanteEvento() {
		return this.instanteEvento;
	}
	
	public No getNo(){
		return this.no;
	}

	@Override
	public int compareTo(Evento o) {
		if(this.instanteEvento > o.instanteEvento) return 1;
		else if(this.instanteEvento < o.instanteEvento) return -1;
		return this.getNo().getEstado().compareToIgnoreCase(o.getNo().getEstado());
	}
}
