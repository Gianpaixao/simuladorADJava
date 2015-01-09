package modelo;
import java.util.Random;

public class Gerador {

	private static Gerador geradorAleatorio;
	private Random random;

	public Gerador() {
		
		Random rand = new Random();
		long semente = rand.nextLong();
		
		random = new Random(semente);
	}

	public Gerador getGeradorAleatorio() {
		
		if (geradorAleatorio == null) {
			geradorAleatorio = new Gerador();
		}
		
		return geradorAleatorio;
	}
	
	public double getGeraAmostra(double taxa) {
		
		return -(Math.log(random.nextDouble()) / taxa);
	}
	
	public double geradorExponencial(double taxa)
	{
		Random rand = new Random();
		double semente = rand.nextDouble();
		return -(Math.log(1-semente)/taxa);
	}

	
}

