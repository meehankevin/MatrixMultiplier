import java.util.List;
import java.util.Random;

/**
 *
 */

/**
 * @author kevin
 *
 */
public class SimpleIntegerMatrix extends SimpleAbstractMatrix<Integer> {

	public SimpleIntegerMatrix(Integer squareSize) {
		super(squareSize);
	}

	public SimpleIntegerMatrix(Integer numberOfRows, Integer numberOfColumns) {
		super(numberOfRows, numberOfColumns);
	}

	public SimpleIntegerMatrix(List<List<Integer>> listOfRows) {
		super(listOfRows);
	}


	public static SimpleAbstractMatrix<Integer> generateRandomMatrix(Integer size) {
		Random random = new Random();
		SimpleAbstractMatrix<Integer> matrix = new SimpleIntegerMatrix(size);

		for (List<Integer> row : matrix.getRows()) {
			for (int columnIndex = 0; columnIndex < matrix.getNumberOfColumns(); columnIndex++) {
				row.set(columnIndex, Integer.valueOf(random.nextInt(10)));
			}
		}

		return matrix;
	}

	public SimpleAbstractMatrix<Integer> transposeMatrix(SimpleAbstractMatrix<Integer> matrix) {
		SimpleAbstractMatrix<Integer> transposedMatrix = new SimpleIntegerMatrix(matrix.getNumberOfColumns(), matrix.getNumberOfRows());
		int columnIndex = 0;
		for (List<Integer> row : matrix.getRows()) {
			transposedMatrix.setColumnAt(columnIndex, row);
			columnIndex++;
		}
		return null;
	}

	public static SimpleAbstractMatrix<Integer> aggregateMatrices(SimpleAbstractMatrix<Integer> firstMatrix, SimpleAbstractMatrix<Integer> secondMatrix, AggregationType aggregationType) {
		if (!validCumulativeDimensions(firstMatrix, secondMatrix, aggregationType)) {
			return null;
		}
		SimpleAbstractMatrix<Integer> resultMatrix = new SimpleIntegerMatrix(firstMatrix.getNumberOfRows(), firstMatrix.getNumberOfColumns());
		int rowIndex = 0;
		for (List<Integer> resultRow : resultMatrix.getRows()) {
			for (int columnIndex = 0; columnIndex < resultMatrix.getNumberOfColumns(); columnIndex++) {
				resultRow.set(columnIndex, aggregateVectors(firstMatrix.getRowAt(rowIndex), secondMatrix.getColumnAt(columnIndex), aggregationType));
			}
		}
		return resultMatrix;
	}

	public static Integer aggregateVectors(List<Integer> row, List<Integer> column, AggregationType aggregationType) {
		Integer value = 0;
		switch (aggregationType) {
		case ADDITION:
			for (int i = 0; i < row.size(); i++) {
				value += row.get(i) + column.get(i);
			}
			return value;
		case SUBTRACTION:
			for (int i = 0; i < row.size(); i++) {
				value += row.get(i) - column.get(i);
			}
			return value;
		case MULTIPLICATION:
			for (int i = 0; i < row.size(); i++) {
				value += row.get(i) * column.get(i);
			}
			return value;
		case INVERSION:
			// implement Gaussian
		default:
			return 0;
		}

	}


	public static boolean validCumulativeDimensions(SimpleAbstractMatrix<Integer> firstMatrix, SimpleAbstractMatrix<Integer> secondMatrix, AggregationType aggregationType) {
		switch (aggregationType) {
		case ADDITION:
			return firstMatrix.getNumberOfRows().equals(secondMatrix.getNumberOfRows()) && firstMatrix.getNumberOfColumns().equals(secondMatrix.getNumberOfColumns());
		case SUBTRACTION:
			return validCumulativeDimensions(firstMatrix, secondMatrix, AggregationType.ADDITION);
		case MULTIPLICATION:
			return firstMatrix.getNumberOfColumns().equals(secondMatrix.getNumberOfRows());
		case INVERSION:
			return firstMatrix.getNumberOfRows().equals(secondMatrix.getNumberOfColumns());
		default:
			return false;
		}
	}

	@Override
	public Integer getDefaultValue() {
		return Integer.valueOf(0);
	}

	@Override
	public SimpleAbstractMatrix<Integer> getTranspose() {
		SimpleIntegerMatrix listOfColumns = new SimpleIntegerMatrix(numberOfColumns, numberOfRows);
		int columnIndex = 0;
		for (List<Integer> row : getRows()) {
			int rowIndex = 0;
			for (Integer rowElement : row) {
				listOfColumns.getColumnAt(columnIndex).set(rowIndex, rowElement);
				rowIndex++;
			}
			columnIndex++;
		}
		return listOfColumns;
	}

	@Override
	public SimpleAbstractMatrix<Integer> getSubMatrix(int rowStart, int rowEnd, int colStart, int colEnd) {
		SimpleIntegerMatrix result = new SimpleIntegerMatrix(rowEnd - rowStart, colEnd - colStart);
		for (int i = rowStart; i < rowEnd; i++) {
			for (int j = colStart; j < colEnd; j++) {
				result.setValueAt(i - rowStart, j - colStart, getValueAt(i, j));
			}
		}
		return result;
	}

}
