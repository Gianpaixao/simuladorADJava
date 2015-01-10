package controle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
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
	
public class SimuladorMesh extends ApplicationFrame
{
	
	static Gerador gerador = new Gerador();
	final static XYSeries series  = new XYSeries ("custo médio");
	final static XYSeries seriesPi0  = new XYSeries ("PI0");
	final static XYSeries seriesCs  = new XYSeries ("CS");
	final static XYSeries seriesCv  = new XYSeries ("CV");
	static double r1, r2, r3, r4, lambda, pi_0, pi_p, pi_r, pi_f, beta;
	static double media = 0.0, variancia = 0.0, desvio, mediaCv = 0.0, mediaCs = 0.0, confiancaMenor, confiancaMaior;
	static char estado = '0';
	static int i, j, k,l, qtde, random, amostra = 10, iteracoes = 50;
	static double t_0 = 0.0, t_p = 0.0, t_r = 0.0, t_f = 0.0, tempo = 0.0,tempoAtual, cv, cs, custo[] = new double[amostra],custoNo[] = new double[amostra],custoAmostra[] = new double[amostra];
	static BufferedWriter buffWrite;
	public static Random rand;
	
	
	public SimuladorMesh(XYSeries series, String nomeGrafico, String variavel) {
		super("Grafico de Simulação");  
		final JFreeChart chart;
		final XYSeriesCollection data = new XYSeriesCollection(series);  
	    chart = ChartFactory.createXYLineChart(nomeGrafico, "R4", variavel,  
	           data,PlotOrientation.VERTICAL , true,true, false); 
    
	   final ChartPanel chartPanel = new ChartPanel(chart);  
	   chartPanel.setPreferredSize(new java.awt.Dimension (500, 270));  
	   setContentPane(chartPanel);  
	   
	   File file = new File("./"+nomeGrafico+".png");
       try {
			ChartUtilities.saveChartAsPNG(file,chart,1920,1080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void plotarGrafico(XYSeries series, String nomeGrafico, String variavel)
	{
		final SimuladorMesh demo = new SimuladorMesh(series, nomeGrafico, variavel);  
		demo.pack();  
		RefineryUtilities.centerFrameOnScreen(demo);  
		demo.setVisible(true);
		demo.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public static void main(String[] args) throws IOException{
		/*FilaDeEventos filaDeEventos = new FilaDeEventos();
		filaDeEventos.getFila().add(new Evento(2.0,TipoEvento.CURA,new No(1,"0")));
		filaDeEventos.getFila().add(new Evento(1.0,TipoEvento.CURA,new No(1,"0")));
		filaDeEventos.getFila().add(new Evento(4.0,TipoEvento.CURA,new No(1,"0")));
		filaDeEventos.getFila().add(new Evento(3.0,TipoEvento.CURA,new No(1,"0")));
		
		for(Evento e : filaDeEventos.getFila()){
			System.out.println("tempo "+e.getInstanteEvento());
		}
		
		Collections.sort(filaDeEventos.getFila());
		for(Evento e : filaDeEventos.getFila()){
			System.out.println("tempo "+e.getInstanteEvento());
		}
		System.exit(0);*/
		buffWrite = new BufferedWriter(new FileWriter("output.txt"));
		execucao();
		buffWrite.close();
		
		Scanner s = new Scanner(System.in);
		while(true){
			System.out.println("1 - custo\n2 - cv\n3 - cs\n4 - pi0\n5 - sair ");
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
					System.out.println("Fim da simulação2");
					System.exit(0);
					break;
				default:
					continue;
			}
			System.out.println("========================================\n");
		}
	    
		/*String[] chartStrings = { "cv", "cs", "pi0", "custo", "pif" };

		JComboBox chartList = new JComboBox(chartStrings);
		chartList.setSelectedIndex(4);
		chartList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        String chartName = (String)cb.getSelectedItem();
		        if(chartName.equalsIgnoreCase("custo"));
			}
		});*/
	}
	
	private static void execucao() throws IOException
	{
		rand = new Random();
		
		r4 = 0.0;
		qtde = 50;
		r1 = 2.0;
		r2 = 0.8;
		r3 = 3.0;
		
		lambda = 1.0/8640;
		cv = 10.0;
		cs = 9.0;
		beta = 0.08;
		
		for(k=0;k<qtde;k++)
		{
			media = 0.0;
			variancia = 0.0;
			r4 += (1.0/qtde);
			
			
			
			buffWrite.append("============================= r4 = "+r4+"============================\n");
			
			System.out.println("\n========================== r4 = "+r4+" ==========================");
			for(j=0; j<amostra;j++)
			{
				t_0 = 0.0;
				t_p = 0.0;
				t_r = 0.0;
				t_f = 0.0;
				tempo = 0.0;
				tempoAtual = 0.0;
				
				FilaDeEventos filaDeEventos = new FilaDeEventos();
				filaDeEventos.setNosInfectados(1);
				No no[] = new No[10];
				int noInfectado = rand.nextInt(10);
				for(l=0;l<10;l++){
					if(l==noInfectado) no[l] = new No(l,"p");
					else no[l] = new No(l,"0");
				}
				
				for(i=0;i<10;i++)
				{
					//int indice = (noInfectado+i)%10;
					
					agendarEvento(no[i], filaDeEventos);
				}
				System.out.println("============================");
				
				for(Evento e : filaDeEventos.getFila()){
					System.out.println(e.getInstanteEvento()+" - "+e.getNo().getNumeroNo()+" "+e.getTipo());
				}
				
				System.out.println("============================");
				Evento evento = null;
				for(i=0;i<iteracoes;i++)
				{
					if(filaDeEventos.getEventosNaFila()>0)
					{
						evento = filaDeEventos.removeEvento();
						tempoAtual = evento.getInstanteEvento();
						switch(evento.getTipo()){
							case I_P:
								evento.getNo().setEstado("p");
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
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							case F_I:
								evento.getNo().setT_f(evento.getNo().getAux());
								evento.getNo().setEstado("0");
								agendarEvento(evento.getNo(), filaDeEventos);
								break;
							default:
								System.out.println("Tipo de Evento inválido!");
								System.exit(0);
								break; 
						}
					}
					
				}
				System.out.println((evento.getNo().getT_f()+evento.getNo().getT_r()+evento.getNo().getT_p()+evento.getNo().getT_0())/tempoAtual);
				
				pi_0 = evento.getNo().getT_0()/tempoAtual;
				pi_p = evento.getNo().getT_p()/tempoAtual;
				
				custoNo[j] = ((1.0 - pi_0) * cv);
				custoAmostra[j] = ((pi_0 + pi_p) * r4 * cs);
				custo[j] = custoNo[j] + custoAmostra[j];
				media += custo[j];
				mediaCv += custoNo[j];
				mediaCs += custoAmostra[j];
				System.out.println("O custo foi de "+custo[j]);
			}
			
			media = media / j;
			buffWrite.append("O custo medio foi de "+ media+"\n");
			System.out.println("\nO custo medio foi de "+ media);
			
			// variancia, desvio e intervalo de confianca para o custo
			
			variancia = variancia(custo,media,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("A variancia foi "+variancia+" e o desvio padrão foi de "+desvio);
			
			confiancaMenor = media - intervaloConfianca(desvio,amostra);
			confiancaMaior = media + intervaloConfianca(desvio,amostra);
			
			System.out.println("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")");
			buffWrite.append("O intervalo de confiança para 95% é: ("+confiancaMenor+" < u < "+confiancaMaior+")\n");
			
			// variancia, desvio e intervalo de confianca para o custo do no CV
			
			mediaCv = mediaCv / amostra;
			buffWrite.append("O custo medio de Cv foi de "+ mediaCv+"\n");
			variancia = variancia(custoNo,mediaCv,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("A variancia foi "+variancia+" e o desvio padrão foi de "+desvio);
			
			confiancaMenor = mediaCv - intervaloConfianca(desvio,amostra);
			confiancaMaior = mediaCv + intervaloConfianca(desvio,amostra);
			
			buffWrite.append("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")\n");
			System.out.println("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")");

			// variancia, desvio e intervalo de confianca para o custo do no CS
			
			mediaCs = mediaCs / amostra;
			buffWrite.append("O custo medio de Cs foi "+mediaCs+"\n");
			variancia = variancia(custoAmostra,mediaCs,amostra);
			desvio = Math.sqrt(variancia);
			
			System.out.println("A variancia foi "+variancia+" e o desvio padrão foi de "+desvio);
			
			confiancaMenor = mediaCs - intervaloConfianca(desvio,amostra);
			confiancaMaior = mediaCs + intervaloConfianca(desvio,amostra);
			
			System.out.println("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")");
			buffWrite.append("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")\n");
			
			series.add(r4, media);
			seriesPi0.add(r4,pi_0);
			seriesCs.add(r4,mediaCs);
			seriesCv.add(r4,mediaCv);
			
			
			System.out.println("");
			
		}	
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
				double taxa = eventos.getNosInfectados()*beta;
				//System.out.println(taxa);
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
		//System.out.println(proxEvento);
	}

}
