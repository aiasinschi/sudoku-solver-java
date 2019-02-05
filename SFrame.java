import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/***
 * Sudoku Solver version 1.0
 * @author Adrian
 * -Not fully tested
 * -Needs some interface optimization
 */

class SThread extends Thread {
	private SFrame parent;
	private boolean forceDone = false;

	SThread(SFrame parent) {
		this.parent = parent;
	}

	public void run() {
		while ((!forceDone) && (!parent.isDone())) {
			parent.parse();
			parent.intToStringArray();
		}
	}

	void forceDone() {
		this.forceDone = true;
	}
}

public class SFrame extends JFrame implements ActionListener {
	static final long serialVersionUID = 1;
	//	JTable tabel=new JTable();
	private JTextField[][] tabel = new JTextField[9][9];
	private JButton startBtn = new JButton("Start");
	private JButton loadBtn = new JButton("Load Grid");
	private JButton clearBtn = new JButton("Clear Grid");
	//public JButton Generate = new JButton("Generate Grid");
	private JButton switchBtn = new JButton("Switch");
	//public JButton Stop = new JButton("Stop");
	private int[][] data = {
			{0, 7, 0, 5, 0, 2, 0, 4, 0},
			{0, 3, 8, 6, 0, 4, 9, 7, 0},
			{0, 0, 9, 0, 0, 0, 2, 0, 0},//first 3 lines
			{0, 5, 0, 0, 6, 0, 0, 2, 0},
			{0, 0, 0, 1, 0, 3, 0, 0, 0},
			{0, 1, 0, 0, 7, 0, 0, 3, 0},//next 3 lines
			{0, 0, 5, 0, 0, 0, 1, 0, 0},
			{0, 2, 7, 8, 0, 9, 3, 6, 0},
			{0, 6, 0, 4, 0, 1, 0, 9, 0}};
	private long[][] idata = new long[9][9];
	private String[][] sdata = new String[9][9];
	private SThread solvethread;

	public void actionPerformed(ActionEvent e) {
		stringToDataArray();
		dataToIntArray();
		if (e.getSource() == startBtn) {
			solvethread = new SThread(this);
			solvethread.start();
		} else if (e.getSource() == switchBtn) {
			switchAll(2);
			dataToStringArray();
		} else if (e.getSource() == loadBtn) {
			loadData("grid.txt");
			dataToStringArray();
		} else if (e.getSource() == clearBtn) {
			clearData();
			dataToStringArray();
		}
	}

	private void switchCol(int i, int j) {
		for (int k = 0; k < 9; k++) {
			int tmp = data[k][j];
			data[k][j] = data[k][i];
			data[k][i] = tmp;
		}
	}

	private void switchRow(int i, int j) {
		for (int k = 0; k < 9; k++) {
			int tmp = data[j][k];
			data[j][k] = data[i][k];
			data[i][k] = tmp;
		}
	}

	private int nextInCell(java.util.Random RND, int cc) {
		int a = (cc / 3) * 3;
		int dd = a + RND.nextInt(3);
		while (dd == cc)
			dd = a + RND.nextInt(3);
		return dd;
	}

	private void switchAll(int no) {
		java.util.Random RND = new java.util.Random();
		for (int k = 0; k < no; k++) {
			int i = RND.nextInt(9);
			int j = nextInCell(RND, i);
			switchCol(i, j);
			i = RND.nextInt(9);
			j = nextInCell(RND, i);
			switchRow(i, j);
		}
	}

	public void genData(int NUM) {
		for (int k = 0; k < 9; k++)
			for (int l = 0; l < 9; l++)
				data[k][l] = 0;
		for (int k = 0; k < NUM; k++) {
			java.util.Random RND = new java.util.Random();
			int posi = RND.nextInt(9), posj = RND.nextInt(9);
			while (data[posi][posj] != 0) {
				posi = RND.nextInt(9);
				posj = RND.nextInt(9);
			}
			int val = RND.nextInt(9);
			while (!legal(posi, posj, val))
				val = RND.nextInt(9);
			data[posi][posj] = val;
		}
	}

	private boolean legal(int i, int j, int v) {
		for (int k = 0; k < i; k++)
			if (data[k][j] == v) return false;
		for (int k = i + 1; k < 9; k++)
			if (data[k][j] == v) return false;
		for (int k = 0; k < j; k++)
			if (data[i][k] == v) return false;
		for (int k = j + 1; k < 9; k++)
			if (data[i][k] == v) return false;
		int ci = (i / 3) * 3, cj = (j / 3) * 3;
		for (int k = 0; k < 3; k++)
			for (int l = 0; l < 3; l++) {
				if (((k + ci) != i) || ((l + cj) != j))
					if (data[k + ci][l + cj] == v)
						return false;
			}
		return true;
	}

	private void clearData() {
		for (int k = 0; k < 9; k++) {
			for (int j = 0; j < 9; j++) {
				data[k][j] = 0;
			}
		}
	}

	private void loadData(String file) {
		java.io.DataInputStream infile;
		try {
			infile = new java.io.DataInputStream(new java.io.FileInputStream(file));
			for (int k = 0; k < 9; k++) {
				String line = infile.readLine();
				java.util.StringTokenizer st = new java.util.StringTokenizer(line, ",");
				for (int j = 0; j < 9; j++) {
					data[k][j] = Integer.parseInt(st.nextToken());
					//consola(data[k][j]+" ");
				}
				//consola("\n");
			}
			infile.close();
		} catch (java.io.IOException e) {
			consola("Nu pot scrie in fisier");
		}

	}

	private void consola(String ss) {
		System.out.print(ss);
	}

	void intToStringArray() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				sdata[i][j] = name(idata[i][j]);
				tabel[i][j].setText(sdata[i][j]);
			}
	}

	private void dataToStringArray() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (data[i][j] != 0) {
					sdata[i][j] = String.valueOf(data[i][j]);
					tabel[i][j].setText(sdata[i][j]);
				} else {
					sdata[i][j] = "";
					tabel[i][j].setText("");
				}
	}

	private void stringToDataArray() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (tabel[i][j].getText().length() == 1) {
					data[i][j] = Integer.parseInt(tabel[i][j].getText());
				} else {
					data[i][j] = 0;
				}
	}

	private void dataToIntArray() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				if (data[i][j] != 0) {
					idata[i][j] = (0x01 << (data[i][j]) - 1);
					//consola(idata[i][j]+" , ");
				} else {
					idata[i][j] = 0x1FF;
					//consola(idata[i][j]+" , ");
				}
			//consola("\n");		
		}
	}

	private String name(long l) {
		StringBuilder s = new StringBuilder();
		for (int k = 0; k < 9; k++)
			if ((l & (0x01 << k)) != 0)
				s.append(k + 1);
		return s.toString();
	}

	private boolean isFixed(long l) {
		int s = 0;
		for (int k = 0; k < 9; k++)
			s += ((l >> k) & (0x01));
		return (s == 1);
	}

	boolean isDone() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (!isFixed(idata[i][j]))
					return false;
		return true;
	}

	void parse() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (data[i][j] == 0) {
					long l = idata[i][j];
					int ci = (i / 3) * 3;
					int cj = (j / 3) * 3;
					for (int a = 0; a < i; a++)
						if (isFixed(idata[a][j])) l = l & (~idata[a][j]);
					for (int a = i + 1; a < 9; a++)
						if (isFixed(idata[a][j])) l = l & (~idata[a][j]);
					for (int a = 0; a < j; a++)
						if (isFixed(idata[i][a])) l = l & (~idata[i][a]);
					for (int a = j + 1; a < 9; a++)
						if (isFixed(idata[i][a])) l = l & (~idata[i][a]);

					for (int a = 0; a < 3; a++)
						for (int b = 0; b < 3; b++) {
							if ((a + ci != i) || (b + cj != j))
								if (isFixed(idata[a + ci][b + cj])) l = l & (~idata[a + ci][b + cj]);
						}
					idata[i][j] = l;
					//consola(""+idata[i][j]);
				}
	}

	public SFrame() {

		JPanel center = new JPanel();
		//center.setSize(450, 450);
		center.setLayout(new GridLayout(9, 9));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", center);
		JPanel south = new JPanel();
		south.setLayout(new GridBagLayout());
		south.add(startBtn);
		south.add(loadBtn);
		south.add(switchBtn);
		south.add(clearBtn);
		//south.add(Stop); 
		getContentPane().add("South", south);
		startBtn.addActionListener(this);
		loadBtn.addActionListener(this);
		switchBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		//Stop.addActionListener(this);
		loadData("initial.txt");
		solvethread = new SThread(this);
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				tabel[i][j] = new JTextField("", 1);
				tabel[i][j].setBorder(BorderFactory.createEtchedBorder());
				//tabel[i][j].setHorizontalAlignment(JTextField.CENTER);
				tabel[i][j].setFont(new java.awt.Font("Arial", Font.BOLD, 20));
				center.add(tabel[i][j]);
			}
		dataToStringArray();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				solvethread.forceDone();
				dispose();
				System.exit(0);
			}
		});

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Could not set look and feel!");
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		SFrame mainFrame = new SFrame();
		mainFrame.setSize(450, 450);
		mainFrame.setTitle("Sudoku Solver");
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
	}

}
