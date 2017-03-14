package ca.mcgill.ecse321.eventregistration.view;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ca.mcgill.ecse321.eventregistration.controller.EventRegistrationController;
import ca.mcgill.ecse321.eventregistration.controller.InvalidInputException;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;

public class ParticipantPage extends JFrame {

	private static final long serialVersionUID = 6398301441623263485L;
	// attributes for GUI elements
	private JTextField participantNameTextField;
	private JLabel participantNameLabel;
	private JButton addParticipantButton;
	private String error = null;
	private JLabel errorMessage;

	private RegistrationManager rm;

	public ParticipantPage (RegistrationManager rm){
		this.rm = rm;
		initComponents();
		addParticipantButtonActionPerformed();
	}

	private void initComponents(){
		participantNameLabel = new JLabel();
		participantNameTextField = new JTextField();
		addParticipantButton = new JButton();
		// elements for error message
	    errorMessage = new JLabel();
	    errorMessage.setForeground(Color.RED);

		// global settings and listeners
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Event Registration");

		participantNameLabel.setText("Name: ");
		addParticipantButton.setText("Add Participant");

		// layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
		        // error message is added here
		        layout.createParallelGroup()
		        .addComponent(errorMessage)
		        .addGroup(layout.createSequentialGroup()
		        .addComponent(participantNameLabel)
		        .addGroup(layout.createParallelGroup()
		            .addComponent(participantNameTextField, 200, 200, 400)
		            .addComponent(addParticipantButton))
		        ));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {addParticipantButton, participantNameTextField});

		layout.setVerticalGroup(
		        layout.createSequentialGroup()
		        // error message is added here
		        .addComponent(errorMessage)
		        .addGroup(layout.createParallelGroup()
		            .addComponent(participantNameLabel)
		            .addComponent(participantNameTextField))
		        .addComponent(addParticipantButton)
		        );
		addParticipantButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addParticipantButtonActionPerformed();
			}
		});
		pack();
	}

	private void refreshData(){
		 // error
	    errorMessage.setText(error);
	    if (error == null || error.length() == 0) {
	        // participant
	        participantNameTextField.setText("");
	    }
	    // this is needed because the size of the window changes depending on whether an error message is shown or not
	    pack();
	}

	private void addParticipantButtonActionPerformed(){
	    // call the controller
	    EventRegistrationController erc = new EventRegistrationController(rm);
	    error = null;
	    try {
	        erc.createParticipant(participantNameTextField.getText());
	    } catch (InvalidInputException e) {
	        error = e.getMessage();
	    }
	    // update visuals
	    refreshData();
	}
}
