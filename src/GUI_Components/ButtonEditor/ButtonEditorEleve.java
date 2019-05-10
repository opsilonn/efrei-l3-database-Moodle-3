package GUI_Components.ButtonEditor;

import GUI.GUI_Groupe;

import javax.swing.*;

public class ButtonEditorEleve extends ButtonEditor {

    private GUI_Groupe gui;

    public ButtonEditorEleve(JCheckBox checkBox, GUI_Groupe gui) {
        super(checkBox);
        this.gui = gui;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            gui.deleteEleves(Integer.parseInt(label));
        }
        isPushed = false;
        return label;
    }
}
