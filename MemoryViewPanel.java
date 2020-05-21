package projectview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import project.Loader;
import project.MachineModel;

public class MemoryViewPanel {
	private MachineModel model;
	private JScrollPane scroller;
	private JTextField[] dataHex;
	private JTextField[] dataDecimal;
	private int lower = -1;
	private int upper = -1;
	private int previousColor = -1;
	
	public MemoryViewPanel(MachineModel m, int low, int up) {
		model = m;
		lower = low;
		upper = up;
	}
	
	 public JComponent createMemoryDisplay() {
		 JPanel panel = new JPanel();
		 panel.setLayout(new BorderLayout());
		 
		 Border border = BorderFactory.createTitledBorder(
				 BorderFactory.createLineBorder(Color.BLACK),
				 "Data Memory View ["+ lower +"-"+ (upper-1) +"]",
				 TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		 panel.setBorder(border);
		 
		 JPanel innerPanel = new JPanel();
		 innerPanel.setLayout(new BorderLayout());
		 JPanel numPanel = new JPanel();
		 numPanel.setLayout(new GridLayout(0,1));
		 JPanel decimalPanel = new JPanel();
		 decimalPanel.setLayout(new GridLayout(0,1));
		 JPanel hexPanel = new JPanel();
		 hexPanel.setLayout(new GridLayout(0,1));
		 
		 innerPanel.add(numPanel, BorderLayout.LINE_START);
		 innerPanel.add(decimalPanel, BorderLayout.CENTER);
		 innerPanel.add(hexPanel, BorderLayout.LINE_END);
		 
		 dataHex = new JTextField[upper-lower];
		 dataDecimal = new JTextField[upper-lower];
		 
		 for (int i = lower; i < upper; i++) {
			 numPanel.add(new JLabel(i+": ", JLabel.RIGHT));
			 dataDecimal[i - lower] = new JTextField(10);
			 dataHex[i-lower] = new JTextField(10);
			 decimalPanel.add(dataDecimal[i-lower]);
			 hexPanel.add(dataHex[i-lower]);
		 }
		 
		 scroller = new JScrollPane(innerPanel);
		 panel.add(scroller);
		 return panel;
	 }
	 
	 public void update(String arg) {
		 for(int i = lower; i < upper; i++) {
			 int val = model.getData(i);
			 dataDecimal[i-lower].setText("" + val);
			 String s = Integer.toHexString(val);
			 if(val < 0)
				 s = "-" + Integer.toHexString(-val);
			 dataHex[i-lower].setText(s.toUpperCase());
		 }
		 if(arg.equals("Clear")) {
			 if(lower <= previousColor && previousColor < upper) {
				 dataDecimal[previousColor-lower].setBackground(Color.WHITE);
				 dataHex[previousColor-lower].setBackground(Color.WHITE);
				 previousColor = -1;
			 }
		 } else {
			 if(previousColor >= lower && previousColor < upper) {
				 dataDecimal[previousColor-lower].setBackground(Color.WHITE);
				 dataHex[previousColor-lower].setBackground(Color.WHITE);
			 }
			 previousColor = model.getChangedDataIndex();
			 if(previousColor >= lower && previousColor < upper) {
				 dataDecimal[previousColor-lower].setBackground(Color.YELLOW);
				 dataHex[previousColor-lower].setBackground(Color.YELLOW);
			 }
		 }
		 if(scroller != null && model != null) {
			 JScrollBar bar= scroller.getVerticalScrollBar();
			 if (model.getChangedDataIndex() >= lower &&
					 model.getChangedDataIndex() < upper &&
					 // the following just checks createMemoryDisplay has run
					 dataDecimal != null) {
				 Rectangle bounds =
						 dataDecimal[model.getChangedDataIndex()-lower].getBounds();
				 bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			 }
		 }
	}
	 
	 public static void main(String[] args) {
		 MachineModel model = new MachineModel(() -> {});
		 MemoryViewPanel panel = new MemoryViewPanel(model, 100, 500);
		 JFrame frame = new JFrame("TEST");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.setSize(400, 700);
		 frame.setLocationRelativeTo(null);
		 frame.add(panel.createMemoryDisplay());
		 frame.setVisible(true);
		 System.out.println(Loader.load(model, new File("pexe/test.pexe")));
		 panel.update("");
	 }



}
