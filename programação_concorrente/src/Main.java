import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 long tempoInicial = System.currentTimeMillis();
		
		CSVReader trainReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/diabetes.csv", 7526883);
		CSVReader testReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/test_diabetes.csv", 20);
		trainReader.load();
		testReader.load();
		double [][] test = testReader.getTrain(); 
		int [] TEST_ANSWERS = testReader.getOutcomes();
		int [] KNN_ANSWERS = new int [20];
	
		
		MutexKnnClassifier knn = new MutexKnnClassifier(5, 7526883, 20, 2);
	
		
		//System.out.println(trainReader.getTrain());
		knn.fit(trainReader.getTrain(), trainReader.getOutcomes());
		
		trainReader.clear();
		testReader.clear();
		
		System.out.println("------------[Data Loaded]-----------");
		
		KNN_ANSWERS = knn.predict(test);
		
		int numberOfHits = 0;
		
		for (int i = 0; i<KNN_ANSWERS.length; i++) {
			if(KNN_ANSWERS[i] == TEST_ANSWERS[i]) {
				numberOfHits++;
			}
		}
		
		 long tempoFinal = System.currentTimeMillis();
		
		System.out.printf("Acc -> %.2f \n", (float) numberOfHits/KNN_ANSWERS.length);
		for (int a : KNN_ANSWERS) {
			System.out.print(a);
		}
		System.out.printf("Time Required:  %.3f ms%n", (tempoFinal - tempoInicial) / 1000d);
	}

}
