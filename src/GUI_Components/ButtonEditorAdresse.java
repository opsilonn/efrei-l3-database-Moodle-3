package GUI_Components;

import GUI.GUI_chercherPersonne;
import GUI_Components.ButtonEditor.ButtonEditor;

import javax.swing.*;

public class ButtonEditorAdresse extends ButtonEditor {

    private GUI_chercherPersonne gui;

    public ButtonEditorAdresse(JCheckBox checkBox, GUI_chercherPersonne gui) {
        super(checkBox);
        this.gui = gui;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            JOptionPane.showMessageDialog(button, "L'adresse à été supprimé.");
            gui.deleteAddress(Integer.parseInt(label));
        }
        isPushed = false;
        return label;
    }
}