import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin
 *
 */
public abstract class SimpleAbstractMatrix<T extends Object> {
	protected Integer numberOfRows;
	protected Integer numberOfColumns;
	protected List<List<T>> listOfRows;
	protected Boolean isSquareMatrix;

	public SimpleAbstractMatrix(Integer squareSize) {
		this.setNumberOfRows(squareSize);
		this.setNumberOfColumns(squareSize);
		this.setListOfRows(new ArrayList<List<T>>());
		this.setIsSquareMatrix(true);
		init();
	}

	public SimpleAbstractMatrix(Integer numberOfRows, Integer numberOfColumns) {
		this.setNumberOfRows(numberOfRows);
		this.setNumberOfColumns(numberOfColumns);
		this.setListOfRows(new ArrayList<List<T>>());
		this.setIsSquareMatrix(numberOfRows.equals(numberOfColumns));
		init();
	}

	public SimpleAbstractMatrix(List<List<T>> listOfRows) {
		this.setNumberOfRows(listOfRows.size());
		this.setNumberOfColumns(listOfRows.get(0).size());
		this.setListOfRows(listOfRows);
		this.setIsSquareMatrix(getNumberOfRows().equals(getNumberOfColumns()));
		init();
	}

	public abstract T getDefaultValue();
	public abstract SimpleAbstractMatrix<T> getTranspose();
	public abstract SimpleAbstractMatrix<T> getSubMatrix(int rowStart, int rowEnd, int colStart, int colEnd);

	public void init() {
		int rowIndex = 0;
		while (rowIndex < numberOfRows) {
			List<T> row = new ArrayList<T>();
			int columnIndex = 0;
			while (columnIndex < numberOfColumns) {
				row.add(getDefaultValue());
				columnIndex++;
			}
			listOfRows.add(row);
			rowIndex++;
		}
	}

	public List<List<T>> getRows() {
		return listOfRows;
	}

	public void setSubMatrix(SimpleAbstractMatrix<T> sourceMatrix, int destRowStart, int destRowEnd, int destColStart, int destColEnd) {
		for (int i = destRowStart; i < destRowEnd; i++) {
			List<T> row = this.getRowAt(i);
			for (int j = destColStart; j < destColEnd; j++) {
				row.set(j, sourceMatrix.getValueAt(i - destRowStart, j - destColStart));
			}
		}
	}

	public List<List<T>> getColumns() {
		return getTranspose().getRows();
	}

	public List<T> getRowAt(int rowIndex) {
		return getListOfRows().get(rowIndex);
	}

	public void setRowAt(int rowIndex, List<T> row) {
		this.getListOfRows().set(rowIndex, row);
	}

	public List<T> getColumnAt(int columnIndex) {
		List<T> column = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < getListOfRows().size(); rowIndex++) {
			column.add(getListOfRows().get(rowIndex).get(columnIndex));
		}
		return column;
	}

	public void setColumnAt(int columnIndex, List<T> columnValues) {
		for (int rowIndex = 0; rowIndex < getListOfRows().size(); rowIndex++) {
			T columnElement = columnValues.get(rowIndex);
			getListOfRows().get(rowIndex).set(columnIndex, columnElement);
		}
	}

	public T getValueAt(int rowIndex, int columnIndex) {
		List<T> row = getRowAt(rowIndex);
		return row.get(columnIndex);
	}

	public void setValueAt(int rowIndex, int columnIndex, T value) {
		List<T> row = this.getRowAt(rowIndex);
		row.set(columnIndex, value);
	}

	public Integer getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(Integer numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public List<List<T>> getListOfRows() {
		return listOfRows;
	}

	public void setListOfRows(List<List<T>> listOfRows) {
		this.listOfRows = listOfRows;
	}

	public Boolean getIsSquareMatrix() {
		return isSquareMatrix;
	}

	public void setIsSquareMatrix(Boolean isSquareMatrix) {
		this.isSquareMatrix = isSquareMatrix;
	}


}
