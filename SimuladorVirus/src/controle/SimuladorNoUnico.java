package controle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import modelo.Evento;
import modelo.FilaDeEventos;
import modelo.Gerador;
import modelo.No;
import modelo.TipoEvento;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
	
public class SimuladorNoUnico extends ApplicationFrame
{
	private static final long serialVersionUID = 1L;
	
	static Gerador gerador = new Gerador();
	final static XYSeries series  = new XYSeries ("custo m�dio");
	final static XYSeries seriesPi0  = new XYSeries ("PI0");
	final static XYSeries seriesCs  = new XYSeries ("CS");
	final static XYSeries seriesCv  = new XYSeries ("CV");
	final static XYSeries seriesCDF  = new XYSeries ("CDF");
	
	static double r1, r2, r3, r4, lambda, pi_0, pi_p, pi_r, pi_f, beta;
	static double media = 0.0, variancia = 0.0, desvio, mediaCv = 0.0, mediaCs = 0.0, confiancaMenor, confiancaMaior;
	static char estado = '0';
	static int i, j, k,l, qtde, random, amostra = 200, iteracoes = 80;
	static double t_0 = 0.0, t_p = 0.0, t_r = 0.0, t_f = 0.0, tempo = 0.0,tempoAtual, cv, cs, custo[] = new double[amostra],custoNo[] = new double[amostra],custoAmostra[] = new double[amostra], tempoCura[] = new double[amostra];
	
	static BufferedWriter buffWrite;
	static BufferedWriter buffWriteCura;
	public static Random rand;
	
	
	public SimuladorNoUnico(XYSeries series, String nomeGrafico, String variavel) {
		super("Grafico de Simula��o");  
		final JFreeChart chart;
		final XYSeriesCollection data = new XYSeriesCollection(series);  
	    chart = ChartFactory.createXYLineChart(nomeGrafico, "R4", variavel,  
	           data,PlotOrientation.VERTICAL , true,true, false); 
    
	   final ChartPanel chartPanel = new ChartPanel(chart);  
	   chartPanel.setPreferredSize(new java.awt.Dimension (500, 270));  
	   setContentPane(chartPanel);  
	   
	   File file = new File("./"+nomeGrafico+"_NoUnico.png");
       try {
			ChartUtilities.saveChartAsPNG(file,chart,1920,1080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void plotarGrafico(XYSeries series, String nomeGrafico, String variavel)
	{
		final SimuladorNoUnico demo = new SimuladorNoUnico(series, nomeGrafico, variavel);  
		demo.pack();  
		RefineryUtilities.centerFrameOnScreen(demo);  
		demo.setVisible(true);
		demo.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public static void main(String[] args) throws IOException{
		buffWrite = new BufferedWriter(new FileWriter("outputNo.txt"));
		buffWriteCura = new BufferedWriter(new FileWriter("outputCura.txt"));
		execucao();
		buffWriteCura.close();
		buffWrite.close();
		
		Scanner s = new Scanner(System.in);
		while(true){
			System.out.println("Escolha o n�mero do gr�fico a ser gerado:\n1 - custo\n2 - cv\n3 - cs\n4 - pi0\n5 - CDF\n6 - Sair ");
		    int grafico = s.nextInt();

			switch (grafico) {
				case 1:
					plotarGrafico(series, "custo x R4", "custo");
					break;
				case 2:
					plotarGrafico(seriesCv, "cv x R4", "(1.0 - pi_0) * cv");		
					break;
				case 3:
					plotarGrafico(seriesCs, "cs x R4", "(pi_0 + pi_p) * r4 * cs");
					break;
				case 4:
					plotarGrafico(seriesPi0, "Pi0 x R4", "Pi0");
					break;
				case 5:
					plotarGrafico(seriesCDF, "CDF", "tempo");
					break;
				case 6:
					System.out.println("Fim da simula��o");
					System.exit(0);
					break;
				default:
					continue;
			}
			System.out.println("========================================\n");
		}
	}
	
	private static void execucao() throws IOException
	{
		rand = new Random();
		
		r4 = 0.0;
		qtde = 200;
		r1 = 2.0;
		r2 = 0.8;
		r3 = 3.0;
		
		lambda = 1.0/8640;
		cv = 10.0;
		cs = 9.0;
		//beta = 0.08;
		
		double custoOtimo = 1000.0;
		double r4Otimo = 0.0;
		double mediaCuraOtimo = 0.0;
		
		
		// loop para execu��o da simula��o para cada r4
		for(k=0;k<qtde;k++)
		{
			media = 0.0;
			variancia = 0.0;
			r4 += (1.0/qtde);
			double mediaCura = 0.0;
			double aux[] = new double[amostra];
			
			buffWrite.append("============================= r4 = "+r4+"============================\n");
			
			System.out.println("\n========================== r4 = "+r4+" ==========================");
			
			// Repeti��o da simula��o para o mesmo R4, assim s�o geradas amostras da simula��o com msm dados
			for(j=0; j<amostra;j++)
			{
				t_0 = 0.0;
				t_p = 0.0;
				t_r = 0.0;
				t_f = 0.0;
				tempo = 0.0;
				tempoAtual = 0.0;
				
				// Cria��o da fila de eventos
				FilaDeEventos filaDeEventos = new FilaDeEventos();
				filaDeEventos.setNosInfectados(1);
				No no[] = new No[1];
				no[0] = new No(0, "0");
				
				
				// cria os eventos sementes de cada n�
				for(i=0;i<1;i++)
				{
					agendarEvento(no[i], filaDeEventos);
				}
				System.out.println("============================");
				
				/*for(Evento e : filaDeEventos.getFila()){
					System.out.println(e.getInstanteEvento()+" - "+e.getNo().getNumeroNo()+" "+e.getTipo());
				}*/
				
				System.out.println("============================");
				Evento evento = null;
				
				// execu��o do tratamento dos eventos
				for(i=0;i<iteracoes;i++)
				{
					if(filaDeEventos.getEventosNaFila()>0)
					{
						evento = filaDeEventos.removeEvento();
						tempoAtual = evento.getInstanteEvento();
						switch(evento.getTipo()){
							case I_P:
								evento.getNo().setEstado("p");
								evento.getNo().setTempoInfeccao(tempoAtual);
								evento.getNo().setT_0(evento.getNo().getAux());
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							case P_R:
								evento.getNo().setEstado("r");
								evento.getNo().setT_p(evento.getNo().getAux());
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							case P_F:
								evento.getNo().setT_p(evento.getNo().getAux());
								evento.getNo().setEstado("f");
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							case R_I:
								evento.getNo().setT_r(evento.getNo().getAux());
								evento.getNo().setEstado("0");
								evento.getNo().setTempoCura(tempoAtual);
								evento.getNo().calculaMediaCura();
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							case F_I:
								evento.getNo().setT_f(evento.getNo().getAux());
								evento.getNo().setEstado("0");
								evento.getNo().setTempoCura(tempoAtual);
								evento.getNo().calculaMediaCura();
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							default:
								System.out.println("Tipo de Evento inv�lido!");
								System.exit(0);
								break; 
						}
					}
				}
				
				// probabilidade total dos PIs
				// System.out.println((evento.getNo().getT_f()+evento.getNo().getT_r()+evento.getNo().getT_p()+evento.getNo().getT_0())/tempoAtual);
				
				double cura = 0.0;
				for(No nos : no)
				{
					cura += nos.getMediaCura();
				}
				cura = cura/1;
				mediaCura += cura;
				
				pi_0 = evento.getNo().getT_0()/tempoAtual;
				pi_p = evento.getNo().getT_p()/tempoAtual;
				
				custoNo[j] = ((1.0 - pi_0) * cv);
				custoAmostra[j] = ((pi_0 + pi_p) * r4 * cs);
				custo[j] = custoNo[j] + custoAmostra[j];
				media += custo[j];
				mediaCv += custoNo[j];
				mediaCs += custoAmostra[j];
				aux[j] = cura;
				System.out.println("O custo foi de "+custo[j]);
			}
			
			mediaCura = mediaCura / amostra;
			buffWriteCura.append(r4+" ; "+mediaCura+"\n");
		
			media = media / j;
			if(media<custoOtimo)
			{
				custoOtimo = media;
				r4Otimo = r4;
				mediaCuraOtimo = mediaCura;
				tempoCura = aux;
			}
			buffWrite.append("O custo medio foi de "+ media+"\n");
			System.out.println("\nO custo medio foi de "+ media);
			
			// variancia, desvio e intervalo de confianca para o custo
			
			variancia = variancia(custo,media,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("A variancia foi "+variancia+" e o desvio padr�o foi de "+desvio);
			
			confiancaMenor = media - intervaloConfianca(desvio,amostra);
			confiancaMaior = media + intervaloConfianca(desvio,amostra);
			
			System.out.println("O intervalo de confian�a para 95% �: ("+confiancaMenor+"< u <"+confiancaMaior+")");
			buffWrite.append("intervalo de confianca para 95%: "+2*intervaloConfianca(desvio,amostra)+"\n");
			
			// variancia, desvio e intervalo de confianca para o custo do no CV
			
			mediaCv = mediaCv / amostra;
			buffWrite.append("O custo medio de Cv foi de "+ mediaCv+"\n");
			variancia = variancia(custoNo,mediaCv,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("O custo medio de Cv foi de "+ mediaCv);
			
			confiancaMenor = mediaCv - intervaloConfianca(desvio,amostra);
			confiancaMaior = mediaCv + intervaloConfianca(desvio,amostra);
			
			buffWrite.append("intervalo de confianca para 95%: "+2*intervaloConfianca(desvio,amostra)+"\n");
			System.out.println("O intervalo de confian�a para 95% �: ("+confiancaMenor+"< u <"+confiancaMaior+")");

			// variancia, desvio e intervalo de confianca para o custo do no CS
			
			mediaCs = mediaCs / amostra;
			buffWrite.append("O custo medio de Cs foi "+mediaCs+"\n");
			variancia = variancia(custoAmostra,mediaCs,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("O custo medio de Cs foi "+mediaCs);
			
			confiancaMenor = mediaCs - intervaloConfianca(desvio,amostra);
			confiancaMaior = mediaCs + intervaloConfianca(desvio,amostra);
			
			System.out.println("O intervalo de confian�a para 95% �: ("+confiancaMenor+"< u <"+confiancaMaior+")");
			buffWrite.append("intervalo de confianca para 95%: "+2*intervaloConfianca(desvio,amostra)+"\n");
			
			series.add(r4, media);
			seriesPi0.add(r4,pi_0);
			seriesCs.add(r4,mediaCs);
			seriesCv.add(r4,mediaCv);
			
			
			System.out.println("");
			
		}	
		final double INCREMENTO = 1.0 / amostra;
        double t = INCREMENTO;
		for(double d : ord(tempoCura)){
			seriesCDF.add(d, t);
			t+=INCREMENTO;
		}
		
		buffWrite.append("\n\nO custo �timo � "+custoOtimo+" e o tempo de cura medio � "+mediaCuraOtimo+" para R4 "+r4Otimo);
		System.out.println("\n\nO custo �timo � "+custoOtimo+" e o tempo de cura medio � "+mediaCuraOtimo+" para R4 "+r4Otimo);
	}
	
	private static double intervaloConfianca(double desvio, int n)
	{
		return 1.96*(desvio/Math.sqrt(n));
	}
	
	private static double variancia(double custo[], double media, int qtde)
	{
		double var = 0.0;
		for(j=0;j<qtde;j++){
			var += (custo[j]-media)*(custo[j]-media);
		}
		
		return var/qtde;
	}
	
	private static void agendarEvento(No no, FilaDeEventos eventos){
		random = rand.nextInt(100);
		double proxEvento = 0.0;
		switch (no.getEstado()) {
			case "0":	
				double taxa = r2;

				if(taxa > 0){
					proxEvento = gerador.geradorExponencial(taxa);
					tempo =tempoAtual+ proxEvento;
					Evento evento = new Evento(tempo,TipoEvento.I_P,no);
					eventos.addEvento(evento);
					no.setAux(no.getT_0()+proxEvento);
					t_0+=proxEvento;
				}
				
				break;
			case "p":
				double prob = (r4/(r4+lambda))*100;
				
				if(random < prob){
					proxEvento = gerador.geradorExponencial(r4);
					tempo =tempoAtual+ proxEvento;
					Evento evento1 = new Evento(tempo,TipoEvento.P_R,no);
					eventos.addEvento(evento1);
					no.setAux(no.getT_p()+proxEvento);
					t_p +=proxEvento;
				}
				else if(random > prob){
					proxEvento = gerador.geradorExponencial(lambda);
					tempo =tempoAtual+ proxEvento;
					Evento evento2 = new Evento(tempo,TipoEvento.P_F,no);
					eventos.addEvento(evento2);
					no.setAux(no.getT_p()+proxEvento);
					t_p+=proxEvento;
				}
				break;
			case "r":
				proxEvento = gerador.geradorExponencial(r3);
				tempo =tempoAtual+ proxEvento;
				Evento evento3 = new Evento(tempo,TipoEvento.R_I,no);
				eventos.addEvento(evento3);
				no.setAux(no.getT_r()+proxEvento);
				break;
			case "f":
				estado='0';
				proxEvento = gerador.geradorExponencial(r1);
				tempo =tempoAtual+ proxEvento;
				Evento evento4 = new Evento(tempo, TipoEvento.F_I, no);
				eventos.addEvento(evento4);
				no.setAux(no.getT_f()+proxEvento);
				break;
			default:
				System.out.println("Fail!");
				System.exit(0);
				break;
		}
	}
	
	
	/**
	 * Met�do para ordena��o de vetor de double
	 * @param a vetor a ser ordenado
	 */
	private static double[] ord(double a[]){
		double aux;
		for (int x = 0; x < a.length; x++) {
			   for (int y = x+1; y < a.length; y++) {
			    if(a[x] > a[y] ){
			     aux = a[x];
			     a[x] = a[y];
			     a[y] = aux;
			    }
			   }
			  }
		return a;
	}

}
