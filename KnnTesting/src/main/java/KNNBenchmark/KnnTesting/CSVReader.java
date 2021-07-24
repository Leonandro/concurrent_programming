import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.ArrayList;


//Classe para ler arquivos CSV 
// */ ELA LEVA EM CONSIDERAÇÃO QUE O ARQUIVO CSV CONTÉM APENAS DOUBLES E INTEIROS 
// [SENDO UMA ÚNICA COLUNA DE INTEIROS, A ÚLTIMA (PARA RERESENTAR AS CLASSES)]/* 

//TODO TERMINAR A CLASSE
public class CSVReader {
	
	private String path;
	private double[][] dataset;
	private int n_instances;
//	private ArrayList<ArrayList<Float>> rawData;
//	private ArrayList<Integer> targets;

	public CSVReader(String path, int N_INSTANCES) {
		// TODO Auto-generated constructor stub
		this.path = path;
		this.n_instances = N_INSTANCES;
		this.dataset = new double [N_INSTANCES][9];
//		this.rawData = new ArrayList<ArrayList<Float>> ();
//		this.targets = new ArrayList<Integer> ();
	}

	


	public void load() {
		
        try {
        	 FileReader fileReader = new FileReader(this.path);

        	 try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
        		 int lcount = 0;
                 int ccount = 0;
        		 String line;
	        	 while((line = bufferedReader.readLine()) != null) {
	        		 if(lcount >= this.n_instances)
	        			 break;
	        		 ccount = 0;
	        		 String [] arr = line.split(",");
	        		 if(this.n_instances == 222 || lcount != 0) {
		        		 for(String a : arr) {
		                 	dataset[lcount][ccount] = Double.parseDouble(a);
		                 	ccount++;
		                 }
	        		 }
	        		 //System.out.println(lcount);
	                 lcount++;
	        	 }
        	 }
        	 fileReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
   	 	//return this.dataset;
	}
	
	public void clear () {
		this.dataset = null;
		System.gc();
	}
	
	public double [][] getTrain (){
		//System.out.println("[EXPORTING...]: " + this.rawData.size());
		return this.dataset;
		
	}
	
	public int [] getOutcomes () {
		//System.out.println("[EXPORTING...]: " + this.targets.size());
		int [] targets = new int [this.dataset.length];
		for(int i = 0; i<this.dataset.length; i++) {
			targets[i] = (int)this.dataset[i][8];
		}
		
		return targets;
	}
}
