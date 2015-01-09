package modelo;

public class No 
{
	private String numeroNo;
	private String estado;
	
	public No(String numeroNo, String estado)
	{
		this.numeroNo = numeroNo;
		this.estado = estado;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
}
