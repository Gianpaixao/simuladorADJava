package controle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import modelo.Gerador;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;



public class Simulador extends ApplicationFrame{
	static Gerador gerador = new Gerador();
	final static XYSeries series  = new XYSeries ("custo médio");
	final static XYSeries seriesPi0  = new XYSeries ("PI0");
	final static XYSeries seriesCs  = new XYSeries ("CS");
	final static XYSeries seriesCv  = new XYSeries ("CV");
	static double r1, r2, r3, r4, lambda, pi_0, pi_p, pi_r, pi_f;
	static double media = 0.0, variancia = 0.0, desvio, mediaCv = 0.0, mediaCs = 0.0, confiancaMenor, confiancaMaior;
	static char estado = '0';
	static int i, j, k, qtde, random, amostra = 50, iteracoes = 50;
	static double t_0 = 0.0, t_p = 0.0, t_r = 0.0, t_f = 0.0, tempo = 0.0, cv, cs, custo[] = new double[amostra],custoNo[] = new double[amostra],custoAmostra[] = new double[amostra];
	static BufferedWriter buffWrite;
	
	
	public Simulador(XYSeries series, String nomeGrafico, String variavel) {
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
		final Simulador demo = new Simulador(series, nomeGrafico, variavel);  
		demo.pack();  
		RefineryUtilities.centerFrameOnScreen(demo);  
		demo.setVisible(true);
		demo.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public static void main(String[] args) throws IOException{
		
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
		Random rand = new Random();
		
		r4 = 0.0;
		qtde = 100;
		r1 = 2.0;
		r2 = 0.8;
		r3 = 3.0;
		
		lambda = 1.0/8640;
		cv = 10.0;
		cs = 9.0;
		
		for(k=0;k<qtde;k++)
		{
			estado = '0';
			
			media = 0.0;
			variancia = 0.0;
			double proxEvento = 0;
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
				for(i=0;i<iteracoes;i++)
				{
					random = rand.nextInt(100);
					switch (estado) {
						case '0':	
							estado='p';
							proxEvento = gerador.geradorExponencial(r2);
							tempo += proxEvento;
							t_0 += proxEvento;
							break;
						case 'p':
							double prob = (r4/(r4+lambda))*100;
							
							if(random < prob){
								proxEvento = gerador.geradorExponencial(r4);
								estado = 'r';
							}
							else if(random > prob){
								estado = 'f';
								proxEvento = gerador.geradorExponencial(lambda);
							}
							tempo += proxEvento;
							t_p += proxEvento;
							
							break;
						case 'r':
							estado='0';
							proxEvento = gerador.geradorExponencial(r3);
							tempo += proxEvento;
							t_r += proxEvento;
							//t_r += 1.0;
							break;
						case 'f':
							estado='0';
							proxEvento = gerador.geradorExponencial(r1);
							tempo += proxEvento;
							t_f += proxEvento;
							//t_p += 1.0;
							break;
						default:
							System.out.println("Fail!");
							System.exit(0);
							break;
					}
					
					//tempo += 1.0;
				}
	
				pi_0 = t_0/tempo;
				pi_p = t_p/tempo;
				
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
}
