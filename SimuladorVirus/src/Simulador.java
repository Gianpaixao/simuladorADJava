import java.util.Random;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class Simulador extends ApplicationFrame{
	final static XYSeries series  = new XYSeries ("custo médio");
	final static XYSeries seriesPi0  = new XYSeries ("PI0");
	final static XYSeries seriesCs  = new XYSeries ("CS");
	final static XYSeries seriesCv  = new XYSeries ("CV");
	static double r1, r2, r3, r4, lambda, pi_0, pi_p, pi_r, pi_f;
	static double media = 0.0, variancia = 0.0, desvio, confiancaMenor, confiancaMaior;
	static char estado = '0';
	static int i, j, k, qtde, random, amostra = 50, iteracoes = 50;
	static double t_0 = 0.0, t_p = 0.0, t_r = 0.0, t_f = 0.0, tempo = 0.0, cv, cs, custo[] = new double[amostra];
	
	public Simulador(XYSeries series, String nomeGrafico, String variavel) {
		super("Grafico de Simulação");  
		final JFreeChart chart;
		final XYSeriesCollection data = new XYSeriesCollection(series);  
	    chart = ChartFactory.createXYLineChart(nomeGrafico, "R4", variavel,  
	           data,PlotOrientation.VERTICAL , true,true, false); 
    
	   final ChartPanel chartPanel = new ChartPanel(chart);  
	   chartPanel.setPreferredSize(new java.awt.Dimension (500, 270));  
	   setContentPane(chartPanel);  
	}
	
	private static void plotarGrafico(XYSeries series, String nomeGrafico, String variavel)
	{
		final Simulador demo = new Simulador(series, nomeGrafico, variavel);  
		demo.pack();  
		RefineryUtilities.centerFrameOnScreen(demo);  
		demo.setVisible(true);
		demo.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public static void main(String[] args){
		execucao();
		 
		
		Scanner s = new Scanner(System.in);
		while(true){
			System.out.println("1 - custo\n2 - cv\n3 - cs\n4 - pi0\n5 - sair ");
		    int grafico = s.nextInt();

			switch (grafico) {
				case 1:
					plotarGrafico(series, "custo x R4", "custo");
					break;
				case 2:
					plotarGrafico(seriesCv, "cv x R4", "cv");		
					break;
				case 3:
					plotarGrafico(seriesCs, "cs x R4", "cs");
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
	
	private static void execucao(){
		Random rand = new Random();
		r4 = 0.0;
		qtde = 20;
		r1 = 2.0;
		r2 = 0.8;
		r3 = 3.0;
		
		lambda = 1.0/8640;
		cv = 9.0;
		cs = 10.0;
		
		for(k=0;k<qtde;k++)
		{
			estado = '0';
			t_0 = 0.0;
			t_p = 0.0;
			t_r = 0.0;
			t_f = 0.0;
			tempo = 0.0;
			media = 0.0;
			variancia = 0.0;
			
			System.out.println("\n========================== r4 = "+r4+" ==========================");
			for(j=0; j<amostra;j++)
			{
				for(i=0;i<iteracoes;i++)
				{
					random = rand.nextInt(1000);
					switch (estado) {
						case '0':	
							if(random > 800){
								estado = 'p';
							}
							t_0 += 1.0;
							break;
						case 'p':
							if(random > 900){
								estado = 'r';
							}
							else if(random < amostra){
								estado = 'f';
							}
							t_p += 1.0;
							break;
						case 'r':
							if(random > 100){
								estado = '0';
							}
							t_r += 1.0;
							break;
						case 'f':
							if(random > 300){
								estado = '0';
							}
							t_p += 1.0;
							break;
						default:
							System.out.println("Fail!");
							System.exit(0);
							break;
					}
					tempo += 1.0;
				}
				
				pi_0 = t_0/tempo;
				pi_p = t_p/tempo;
				
				custo[j] = (1.0 - pi_0) * cv + (pi_0 + pi_p) * r4 * cs;
				media += custo[j];
				System.out.println("O custo foi de "+custo[j]);
			}
			
			media = media / j;
			
			System.out.println("\nO custo medio foi de "+ media);
			
			for(j=0;j<amostra;j++){
				variancia += (custo[j]-media)*(custo[j]-media);
			}
			
			variancia = (variancia / j);
			
			desvio = Math.sqrt(variancia);
			
			System.out.println("A variancia foi "+variancia+" e o desvio padrão foi de "+desvio);
			
			confiancaMenor = media - 1.96*(variancia/Math.sqrt(amostra));
			confiancaMaior = media + 1.96*(variancia/Math.sqrt(amostra));
			
			System.out.println("O intervalo de confiança para 95% é: ("+confiancaMenor+"< u <"+confiancaMaior+")");
			
			series.add(r4, media);
			seriesPi0.add(r4,pi_0);
			seriesCs.add(r4,cs);
			seriesCv.add(r4,cv);
			
			r4 += (1.0/qtde);
			System.out.println("");
			
		}
		
		
		
	}
}
