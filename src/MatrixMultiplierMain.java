import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.util.concurrent.ExecutionException;

public class MatrixMultiplierMain {

	// GUI to select # threads and output time for each method
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel NoThreads;
	private JLabel standard;
	private JLabel Strassen;
	private JPanel controlPanel;
	private JPanel resultsPanel;
	private int threads;
	private int pool1;
	private int pool2;


	public static void main(String[] args){
		MatrixMultiplierMain matrixGUI = new MatrixMultiplierMain();
		matrixGUI.GUIelements();
	}

	public MatrixMultiplierMain(){
		prepareGUI();
	}

	private void prepareGUI(){
		mainFrame = new JFrame("Matrix Multiplication");
		mainFrame.setSize(400,500);
		mainFrame.setLayout(new GridLayout(3,0));

		headerLabel = new JLabel("<html>Select: <br>1. matrix size <br>2. number of threads in pool 1 <br>3. number threads in pool 2</html>",JLabel.CENTER );

		NoThreads = new JLabel("Result for No Threads",JLabel.CENTER);
		NoThreads.setForeground(Color.white);
		standard = new JLabel("Result for Standard Method",JLabel.CENTER);
		standard.setForeground(Color.white);
		Strassen = new JLabel("Result for Strassen",JLabel.CENTER);
		Strassen.setForeground(Color.white);

		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}
		});
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBackground(Color.darkGray);

		resultsPanel = new JPanel();
		resultsPanel.setLayout(new GridLayout(0,3));
		resultsPanel.setBackground(Color.blue);
		resultsPanel.add(NoThreads);
		resultsPanel.add(standard);
		resultsPanel.add(Strassen);

		mainFrame.add(headerLabel);
		mainFrame.add(controlPanel);
		mainFrame.add(resultsPanel);
		mainFrame.setVisible(true);
	}
	private void GUIelements(){
		//headerLabel.setText("Control in action: Button");

		// list of matrix size
		final DefaultListModel<Integer> matrixSizes = new DefaultListModel<Integer>();

		matrixSizes.addElement(10);
		matrixSizes.addElement(50);
		matrixSizes.addElement(100);
		matrixSizes.addElement(500);
		matrixSizes.addElement(1000);


		JList<Integer> matrixSize = new JList<Integer>(matrixSizes);
		matrixSize.setName("Select matrix size");
		matrixSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matrixSize.setSelectedIndex(0);
		matrixSize.setVisibleRowCount(3);

		JScrollPane matrixSizeScrollPane = new JScrollPane(matrixSize);
		matrixSizeScrollPane.setSize(100, 100);

		// list of first thread pool size
		final DefaultListModel<Integer> Pool_1 = new DefaultListModel<Integer>();

		Pool_1.addElement(1);
		Pool_1.addElement(7);
		Pool_1.addElement(15);


		JList<Integer> Pool1 = new JList<Integer>(Pool_1);
		Pool1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Pool1.setSelectedIndex(0);
		Pool1.setVisibleRowCount(3);

		JScrollPane Pool1ScrollPane = new JScrollPane(Pool1);
		Pool1ScrollPane.setSize(100, 100);

		// list of second thread pool size
		final DefaultListModel<Integer> Pool_2 = new DefaultListModel<Integer>();

		Pool_2.addElement(1);
		Pool_2.addElement(4);
		Pool_2.addElement(12);


		JList<Integer> Pool2 = new JList<Integer>(Pool_2);
		Pool2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Pool2.setSelectedIndex(0);
		Pool2.setVisibleRowCount(3);

		JScrollPane Pool2ScrollPane = new JScrollPane(Pool2);
		Pool2ScrollPane.setSize(100, 100);

		// buttons for different methods

		JButton computeButton = new JButton("Compute");

		computeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (matrixSize.getSelectedIndex() != -1) {
					threads = matrixSize.getSelectedValue();
					pool1 = Pool1.getSelectedValue();
					pool2 = Pool2.getSelectedValue();

					UnthreadedMatrixMultiplication unthreadedMatrixMultiplication = new UnthreadedMatrixMultiplication();
					StandardMatrixMultiplication threadedMatrixMultiplication = new StandardMatrixMultiplication();
					StrassenMatrixMultiplication strassenMatrixMultiplication = new StrassenMatrixMultiplication();

					SimpleAbstractMatrix<Integer> A = SimpleIntegerMatrix.generateRandomMatrix(threads);
					SimpleAbstractMatrix<Integer> B = SimpleIntegerMatrix.generateRandomMatrix(threads);


					NoThreads.setText(unthreadedMatrixMultiplication.multiply(A, B , threads));
					standard.setText(threadedMatrixMultiplication.multiply(A, B, threads, pool1));
					try {
						Strassen.setText(strassenMatrixMultiplication.multiply(A, B, threads, pool1, pool2));
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		controlPanel.add(matrixSizeScrollPane, gbc);
		gbc.gridx = 1;
		controlPanel.add(Pool1ScrollPane, gbc);
		gbc.gridx = 2;
		controlPanel.add(Pool2ScrollPane, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		controlPanel.add(computeButton, gbc);

		mainFrame.setVisible(true);
	}
}