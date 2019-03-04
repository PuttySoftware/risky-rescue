package com.puttysoftware.riskyrescue.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.riskyrescue.assets.ImageManager;

public class PCImagePickerDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final JLabel imageLabel = new JLabel();
    private static PCImagePickerDialog dialog;
    private static int clothingValue, skinValue, hairValue;
    private static boolean cancel = false;
    private final JComboBox<String> clothing, skin, hair;

    /**
     * Set up and show the dialog. The first Component argument determines which
     * frame the dialog depends on; it should be a component in the dialog's
     * controlling frame. The second Component argument should be null if you
     * want the dialog to come up with its left corner in the center of the
     * screen; otherwise, it should be the component on top of which the dialog
     * should appear.
     */
    public static PCImage showDialog(Component frameComp, String title) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new PCImagePickerDialog(frame, title);
        dialog.setVisible(true);
        if (PCImagePickerDialog.cancel) {
            return null;
        }
        return new PCImage(PCImagePickerDialog.clothingValue,
                PCImagePickerDialog.skinValue, PCImagePickerDialog.hairValue);
    }

    private PCImagePickerDialog(Frame frame, String title) {
        super(frame, title, true);
        // Initialize the combo boxes
        this.clothing = new JComboBox<>(
                new DefaultComboBoxModel<>(PCImage.getClothingNames()));
        this.clothing.setSelectedIndex(0);
        this.clothing.addItemListener(il -> {
            int c = this.clothing.getSelectedIndex();
            PCImagePickerDialog.clothingValue = c;
            this.imageLabel.setIcon(ImageManager.getPCPickerImage(
                    PCImage.getPCImageName(c, PCImagePickerDialog.skinValue,
                            PCImagePickerDialog.hairValue)));
        });
        this.skin = new JComboBox<>(
                new DefaultComboBoxModel<>(PCImage.getSkinNames()));
        this.skin.setSelectedIndex(0);
        this.skin.addItemListener(il -> {
            int s = this.skin.getSelectedIndex();
            PCImagePickerDialog.skinValue = s;
            this.imageLabel.setIcon(ImageManager.getPCPickerImage(
                    PCImage.getPCImageName(PCImagePickerDialog.clothingValue, s,
                            PCImagePickerDialog.hairValue)));
        });
        this.hair = new JComboBox<>(
                new DefaultComboBoxModel<>(PCImage.getHairNames()));
        this.hair.setSelectedIndex(0);
        this.hair.addItemListener(il -> {
            int h = this.hair.getSelectedIndex();
            PCImagePickerDialog.hairValue = h;
            this.imageLabel.setIcon(ImageManager.getPCPickerImage(
                    PCImage.getPCImageName(PCImagePickerDialog.clothingValue,
                            PCImagePickerDialog.skinValue, h)));
        });
        BufferedImageIcon image = ImageManager.getPCPickerImage("000");
        // Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        final JButton setButton = new JButton("OK");
        setButton.setActionCommand("OK");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);
        // image preview thing
        JPanel imagePane = new JPanel();
        imagePane.setLayout(new FlowLayout());
        this.imageLabel.setIcon(image);
        imagePane.add(this.imageLabel);
        // main part of the dialog
        JPanel pickerPane = new JPanel();
        pickerPane.setLayout(new BoxLayout(pickerPane, BoxLayout.PAGE_AXIS));
        JLabel clothingLabel = new JLabel("Clothing:");
        clothingLabel.setLabelFor(this.clothing);
        pickerPane.add(clothingLabel);
        pickerPane.add(this.clothing);
        pickerPane.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel skinLabel = new JLabel("Skin:");
        skinLabel.setLabelFor(this.skin);
        pickerPane.add(skinLabel);
        pickerPane.add(this.skin);
        pickerPane.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel hairLabel = new JLabel("Hair:");
        hairLabel.setLabelFor(this.hair);
        pickerPane.add(hairLabel);
        pickerPane.add(this.hair);
        pickerPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);
        // Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(imagePane, BorderLayout.WEST);
        contentPane.add(pickerPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        // Finalize layout
        pack();
    }

    // Handle clicks on the Set and Cancel buttons.
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            PCImagePickerDialog.cancel = false;
        } else if ("Cancel".equals(e.getActionCommand())) {
            PCImagePickerDialog.cancel = true;
        }
        PCImagePickerDialog.dialog.setVisible(false);
    }
}
