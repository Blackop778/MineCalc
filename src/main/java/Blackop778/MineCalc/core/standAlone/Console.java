package Blackop778.MineCalc.core.standAlone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Console extends JPanel {

    private static final long serialVersionUID = -8126442231468996619L;
    private JDialog dialog;
    private JTextField input;
    private JTextArea output;
    private final int textWidth = 30;

    public Console() {
	dialog = new JDialog((JDialog) null);
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	input = new JTextField(textWidth);
	input.setEditable(true);
	input.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		output.append("\n" + actionOccured(input.getText()));
		input.setText("");
	    }
	});

	output = new JTextArea("Type 'help' for a list of available commands", 5, textWidth);
	output.setLineWrap(true);

	JScrollPane jsp = new JScrollPane();
	jsp.setViewportView(output);
	jsp.createVerticalScrollBar();
	add(jsp);
	add(input);
	dialog.add(this);

	dialog.setTitle("MineCalc Console");
	dialog.pack();
	input.requestFocusInWindow();
	dialog.setResizable(false);
	dialog.setLocationRelativeTo(null);
	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	dialog.setModal(true);
	dialog.setVisible(true);
    }

    private String actionOccured(String input) {
	return input;
    }

}
