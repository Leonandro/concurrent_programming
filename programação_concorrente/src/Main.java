import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	//1742866 [Size of the test dataset ]
	//exec em 12,2 minutos [734,513 ms] no serial {5, 7526883, 1742866, 1000}
	//exec em 6,8 minutos [411,636 ms] no Mutex {5, 7526883, 1742866, 1000, 2}
	//exec em 6,3 minutos [383,244 ms] no Atomic {5, 7526883, 1742866, 1000, 2} 
	//exec em 6,7 minutos [406,771 ms] no Callable/Atomic {5, 7526883, 1742866, 1000, 2}
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		 long tempoInicial = System.currentTimeMillis();
		
		CallableKnnClassifier knn = new CallableKnnClassifier(5, 7526883, 1742866, 1000, 2);
	
		
		//System.out.println("------------[Data Loaded]-----------");
		
		knn.predict();
		 long tempoFinal = System.currentTimeMillis();
//		
		System.out.printf("Acc -> %.2f \n", (float) knn.getAccuracy());
//		for (int a : hue) {
//			System.out.print(a + " ");
//		}
		System.out.printf("\nTime Required:  %.3f ms%n", (tempoFinal - tempoInicial) / 1000d);
	}

}
