public class UnthreadedMatrixMultiplication {

	// naive method without threads
	public String multiply(SimpleAbstractMatrix<Integer> A, SimpleAbstractMatrix<Integer> B, int N){

		long startTime = System.currentTimeMillis();
		try {
			SimpleAbstractMatrix<Integer> C = SimpleIntegerMatrix.aggregateMatrices(A, B, AggregationType.MULTIPLICATION);
		} catch (Exception e) {
			System.out.println("Invalid dimensions");
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		return "<html>NO THREADS METHOD<br>time: " + timeTaken + " milliseconds<br>matrix size: " + N +"</html>";
	}

}
