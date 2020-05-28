import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI implements ActionListener {
public GUI() {
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	JButton jb = new JButton("Terminate");
	panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
	panel.setLayout(new GridLayout(0,1));
	panel.add(jb);
	jb.addActionListener(this);
	frame.add(panel, BorderLayout.CENTER);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
}

@Override
public void actionPerformed(ActionEvent arg0) {
	System.exit(0);
	
}
}
