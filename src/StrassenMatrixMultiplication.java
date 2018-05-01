import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StrassenMatrixMultiplication {

	public String multiply(SimpleAbstractMatrix<Integer> A, SimpleAbstractMatrix<Integer> B, int size, int p1, int p2) throws InterruptedException, ExecutionException, Exception {

		long startTime1 = System.currentTimeMillis();
		int N = calculateSquaredSize(size);

		long stopTime1 = System.currentTimeMillis();
		long timeTaken1 = stopTime1 - startTime1;

		SimpleAbstractMatrix<Integer> squareA = new SimpleIntegerMatrix(N);
		SimpleAbstractMatrix<Integer> squareB = new SimpleIntegerMatrix(N);

		squareA.setSubMatrix(A, 0, size, 0, size);
		squareB.setSubMatrix(B, 0, size, 0, size);

		// beginning of crux of Strassen algorithm so again we begin timer
		long startTime2 = System.currentTimeMillis();

		//Populating A and B with zeroes to make square (if not already)
		for (int i = size; i < N; i++) {
			for (int j = size; j < N; j++) {
				squareA.setValueAt(i, j, 0);
				squareB.setValueAt(i, j, 0);
			}
		}

		//Matrices now both square and in form N = 2^power
		//Next we split both A and B into 4 quadrants

		SimpleAbstractMatrix<Integer> a = squareA.getSubMatrix(0, N/2, 0, N/2);
		SimpleAbstractMatrix<Integer> b = squareA.getSubMatrix(0, N/2, N/2, N);
		SimpleAbstractMatrix<Integer> c = squareA.getSubMatrix(N/2, N, 0, N/2);
		SimpleAbstractMatrix<Integer> d = squareA.getSubMatrix(N/2, N, N/2, N);

		SimpleAbstractMatrix<Integer> e = squareB.getSubMatrix(0, N/2, 0, N/2);
		SimpleAbstractMatrix<Integer> f = squareB.getSubMatrix(0, N/2, N/2, N);
		SimpleAbstractMatrix<Integer> g = squareB.getSubMatrix(N/2, N, 0, N/2);
		SimpleAbstractMatrix<Integer> h = squareB.getSubMatrix(N/2, N, N/2, N);

		//********************************************************************************************************

		// Next step is to apply Strassen to quadrants using THREADS
		// We need to generate P1..P7
		// 1. Each one of the seven computations can be executed in parallel in a different thread
		// 2. The four C quadrants can also be executed in parallel
		//    However there are dependencies moving from step 1 to step 2
		//		i.e. P2, P4, P5 & P6 must be computed before we calculate C11
		//           P1 & P2 must be computed before we calculate C12
		//			 P3 & P4 must be computed before we calculate C21
		//			 P1, P3, P5 & P7 must be computed before we calculate C22
		//	To do this we will run two different executors: one for step 1 and one for step 2

		ExecutorService executor1=Executors.newFixedThreadPool(p1);
		Callable<SimpleAbstractMatrix<Integer>> P1 = new generateExpressions(a, aggregate(f, h, AggregationType.SUBTRACTION), AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P2 = new generateExpressions(aggregate(a, b, AggregationType.ADDITION), h, AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P3 = new generateExpressions(aggregate(c, d, AggregationType.ADDITION), e, AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P4 = new generateExpressions(d, aggregate(g, e, AggregationType.SUBTRACTION), AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P5 = new generateExpressions(aggregate(a, d, AggregationType.ADDITION), aggregate(e, h, AggregationType.ADDITION), AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P6 = new generateExpressions(aggregate(b, d, AggregationType.SUBTRACTION), aggregate(g, h, AggregationType.ADDITION), AggregationType.MULTIPLICATION);
		Callable<SimpleAbstractMatrix<Integer>> P7 = new generateExpressions(aggregate(a, c, AggregationType.SUBTRACTION), aggregate(e, f, AggregationType.ADDITION), AggregationType.MULTIPLICATION);

		List<Callable<SimpleAbstractMatrix<Integer>>> callables1List = Arrays.asList(P1, P2, P3, P4, P5, P6, P7);

		List<Future<SimpleAbstractMatrix<Integer>>> futures1List = new ArrayList<Future<SimpleAbstractMatrix<Integer>>>();
		for (Callable<SimpleAbstractMatrix<Integer>> callable : callables1List) {
			futures1List.add(executor1.submit(callable));
		}
		executor1.shutdown();
		while (!executor1.isTerminated())
		{
		}


		ExecutorService executor2=Executors.newFixedThreadPool(p2);

		Callable<SimpleAbstractMatrix<Integer>> C11 = new generateExpressions(aggregate(futures1List.get(4).get(), futures1List.get(5).get(), AggregationType.ADDITION), aggregate(futures1List.get(3).get(), futures1List.get(1).get(), AggregationType.SUBTRACTION), AggregationType.ADDITION);
		Callable<SimpleAbstractMatrix<Integer>> C12 = new generateExpressions(futures1List.get(0).get(), futures1List.get(1).get(), AggregationType.ADDITION);
		Callable<SimpleAbstractMatrix<Integer>> C21 = new generateExpressions(futures1List.get(2).get(), futures1List.get(3).get(), AggregationType.ADDITION);
		Callable<SimpleAbstractMatrix<Integer>> C22 = new generateExpressions(aggregate(futures1List.get(0).get(), futures1List.get(2).get(), AggregationType.SUBTRACTION), aggregate(futures1List.get(4).get(), futures1List.get(6).get(), AggregationType.SUBTRACTION), AggregationType.ADDITION);

		List<Callable<SimpleAbstractMatrix<Integer>>> callables2List = Arrays.asList(C11, C12, C21, C22);

		List<Future<SimpleAbstractMatrix<Integer>>> futures2List = new ArrayList<Future<SimpleAbstractMatrix<Integer>>>();
		for (Callable<SimpleAbstractMatrix<Integer>> callable : callables2List) {
			futures2List.add(executor2.submit(callable));
		}
		executor2.shutdown();
		while (!executor2.isTerminated())
		{
		}
		//Assigning values to C
		SimpleAbstractMatrix<Integer> squareC = new SimpleIntegerMatrix(N);
		squareC.setSubMatrix(futures2List.get(0).get(), 0, N/2, 0, N/2);
		squareC.setSubMatrix(futures2List.get(1).get(), 0, N/2, N/2, N);
		squareC.setSubMatrix(futures2List.get(2).get(), N/2, N, 0, N/2);
		squareC.setSubMatrix(futures2List.get(3).get(), N/2, N, N/2, N);

		SimpleAbstractMatrix<Integer> C = squareC.getSubMatrix(0, size, 0, size);

		long stopTime2 = System.currentTimeMillis();
		long totalTimeTaken = timeTaken1 + stopTime2-startTime2;

		return "<html>STRASSEN METHOD<br>time: " +totalTimeTaken + " milliseconds<br>matrix size: " + N +"<br>pool1 size: " +p1 +"<br>pool2 size: " +p2 +"</html>";

	}

	private int calculateSquaredSize(int size) {
		int power = 0;
		while (size != 1){
			if (size%2!=0){
				size=size+1;
			}
			size = size/2;
			power=power+1;
		}

		// compute N
		int squareSize = 1;
		while (power != 0){
			squareSize =2*squareSize;
			power = power - 1;
		}
		return squareSize ;
	}

	public static synchronized SimpleAbstractMatrix<Integer> aggregate(SimpleAbstractMatrix<Integer> firstMatrix, SimpleAbstractMatrix<Integer> secondMatrix, AggregationType aggregationType) {
		try {
			return SimpleIntegerMatrix.aggregateMatrices(firstMatrix, secondMatrix, aggregationType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public class generateExpressions implements Callable<SimpleAbstractMatrix<Integer>> {
		private SimpleAbstractMatrix<Integer> firstMatrix;
		private SimpleAbstractMatrix<Integer> secondMatrix;
		private AggregationType aggregationType;

		public generateExpressions(SimpleAbstractMatrix<Integer> firstMatrix, SimpleAbstractMatrix<Integer> secondMatrix, AggregationType aggregationType){
			this.firstMatrix = firstMatrix;
			this.secondMatrix = secondMatrix;
			this.aggregationType = aggregationType;
		}

		@Override
		public SimpleAbstractMatrix<Integer> call() {
			try {
				return aggregate(firstMatrix, secondMatrix, aggregationType);
			} catch (Exception e) {
				return null;
			}
		}
	}

}