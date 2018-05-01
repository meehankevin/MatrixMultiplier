import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StandardMatrixMultiplication {

	public String multiply(SimpleAbstractMatrix<Integer> A, SimpleAbstractMatrix<Integer> B, int N, int p1){
		SimpleAbstractMatrix<Integer> C = new SimpleIntegerMatrix(A.getNumberOfRows(), B.getNumberOfColumns());
		long startTime = System.currentTimeMillis();

		ExecutorService executor=Executors.newFixedThreadPool(p1);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				Runnable worker = new dotProduct(A.getRowAt(i), B.getColumnAt(j), i, j, C);
				executor.execute(worker);
			}
		}
		executor.shutdown();
		while (!executor.isTerminated())
		{
		}
		long timeTaken = System.currentTimeMillis() - startTime;

		return "<html>NAIVE METHOD<br>time: " + timeTaken + " milliseconds<br>matrix size: " + N +"<br>pool size: " + p1 +"</html>";
	}
}

//End Class

class dotProduct implements Runnable {

	private List<Integer> rowA;
	private List<Integer> colB;
	private int rowIndex;
	private int columnIndex;
	private SimpleAbstractMatrix<Integer> C;

	public dotProduct(List<Integer> rowA, List<Integer> colB, int rowIndex, int columnIndex, SimpleAbstractMatrix<Integer> C){
		this.rowA = rowA;
		this.colB = colB;
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.C = C;
	}


	@Override
	public void run() {
		C.setValueAt(rowIndex, columnIndex, SimpleIntegerMatrix.aggregateVectors(rowA, colB, AggregationType.MULTIPLICATION));
	}

}
