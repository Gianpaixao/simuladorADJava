package modelo;


public class No 
{
	private int numeroNo;
	private String estado;
	private double t_0 = 0.0, t_p = 0.0, t_r = 0.0,	t_f = 0.0;
	private double aux = 0.0;
	private double tempoInfeccao;
	private double tempoCura;
	private double mediaCura;
	private double qtdeCura = 1;
	
	public void calculaMediaCura(){
		mediaCura += (tempoCura - tempoInfeccao);
		qtdeCura++;
		//System.out.println("mediaCura  "+mediaCura);
	}
	
	public double getMediaCura() {
		return mediaCura/qtdeCura;
	}

	public void setMediaCura(double mediaCura) {
		this.mediaCura = mediaCura;
	}



	public double getTempoInfeccao() {
		return tempoInfeccao;
	}

	public void setTempoInfeccao(double tempoInfeccao) {
		this.tempoInfeccao = tempoInfeccao;
	}

	public double getTempoCura() {
		return tempoCura;
	}

	public void setTempoCura(double tempoCura) {
		this.tempoCura = tempoCura;
	}

	public No(int numeroNo, String estado)
	{
		this.numeroNo = numeroNo;
		this.estado = estado;
	}
	
	public String getEstado() {
		return this.estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public double getT_0() {
		return t_0;
	}

	public int getNumeroNo() {
		return numeroNo;
	}
	
	public void setNumeroNo(int numeroNo) {
		this.numeroNo = numeroNo;
	}
	
	public void setT_0(double t_0) {
		this.t_0 = t_0;
	}

	public double getT_p() {
		return t_p;
	}

	public void setT_p(double t_p) {
		this.t_p = t_p;
	}

	public double getT_r() {
		return t_r;
	}

	public void setT_r(double t_r) {
		this.t_r = t_r;
	}

	public double getT_f() {
		return t_f;
	}

	public void setT_f(double t_f) {
		this.t_f = t_f;
	}
	
	public void setAux(double aux) {
		this.aux = aux;
	}
	
	public double getAux() {
		return aux;
	}
	
}
