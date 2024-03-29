package GUI;

import static UsefulFunctions.BEGEOT_BUNOUF_CountRows_TableCell.getRows;
import static GUI.BEGEOT_BUNOUF_GUI_USER_Admin.WindowClosingVisible;

import GUI_Components.BEGEOT_BUNOUF_CustomJFrame;
import UsefulFunctions.BEGEOT_BUNOUF_Database_Connection;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BEGEOT_BUNOUF_GUI_addCours extends BEGEOT_BUNOUF_CustomJFrame {

    private static final int DIM_X = 400;
    private static final int DIM_Y = 300;

    private JPanel panel;
    private JComboBox comboBoxItem;
    private JButton buttonSave;
    private JLabel labelThingtoadd;
    private JLabel labelError;

    private BEGEOT_BUNOUF_GUI_chercherPersonne gui;
    private BEGEOT_BUNOUF_GUI_Groupe guiGroupe;


    /**
     * Constructeur de l'interface
     *
     * @param code      Matricule du professeur ou code du groupe concerné
     * @param gui       Interface chercherPersonne
     * @param guiGroupe Interface d'un groupe
     */
    public BEGEOT_BUNOUF_GUI_addCours(int code, BEGEOT_BUNOUF_GUI_chercherPersonne gui, BEGEOT_BUNOUF_GUI_Groupe guiGroupe) {
        super("Ajouter un Cours", false, DIM_X, DIM_Y);
        this.gui = gui;
        this.guiGroupe = guiGroupe;

        if (gui == null) {
            WindowClosingVisible(this, guiGroupe);
        } else {
            WindowClosingVisible(this, gui);
        }


        buttonSave.addActionListener(e -> saveAddtoGroupe(code));
        putTheData(code);

        add(panel);
        pack();
        revalidate();
        setVisible(true);
    }

    /**
     * Fonction de remplissage de la drop-down box
     */
    private void putTheData(int code) {
        BEGEOT_BUNOUF_Database_Connection database = new BEGEOT_BUNOUF_Database_Connection();
        String sql = "SELECT * FROM cours";
        String query = "";
        if (gui == null) {
            query = "SELECT Code FROM suivre WHERE Groupe_ID = " + code;
        } else {
            query = "SELECT Code FROM enseigner WHERE Matricule_Prof = " + code;
        }

        ResultSet data = database.run_Statement_READ(sql);
        try {
            if (getRows(data) == 0) {
                ErrorDisplay(true);
            } else {
                while (data.next()) {
                    ResultSet alreadyAdded = database.run_Statement_READ(query);
                    boolean alreadyThere = false;

                    while (alreadyAdded.next()) {
                        //Condition pour eviter une ViolationPrimaryConstraint

                        if (alreadyAdded.getString("Code").equals(data.getString("Code"))) {
                            alreadyThere = true;
                        }
                    }

                    if (alreadyThere == false) {
                        //Met le code et le nom du Cours dans la drop-down Box
                        comboBoxItem.addItem(data.getString("Code") + ": " + data.getString("Nom"));
                    }
                }
                if (comboBoxItem.getItemCount() == 0) {
                    ErrorDisplay(true);
                } else {
                    ErrorDisplay(false);
                }
            }
        } catch (SQLException e) {
        }
    }

    private void ErrorDisplay(boolean mode) {
        if (mode) {
            labelError.setVisible(true);
            comboBoxItem.setVisible(false);
            buttonSave.setVisible(false);
            labelThingtoadd.setVisible(false);
        } else {
            comboBoxItem.setVisible(true);
            buttonSave.setVisible(true);
            labelError.setVisible(false);
            labelThingtoadd.setVisible(true);
        }
    }


    /**
     * Sauvegarde de l'ajout du cours au professeur ou au groupe dans la table enseigner ou suivre.
     *
     * @param code Matricule du professeur ou Code du groupe.
     */
    private void saveAddtoGroupe(int code) {
        String sql = "";
        String elementToAdd = comboBoxItem.getSelectedItem().toString();
        String[] result = elementToAdd.split(": ");


        if (gui != null) {
            sql = "INSERT INTO enseigner (Code, Matricule_Prof) VALUES (" +
                    Integer.parseInt(result[0]) + ", " + code + ")";
        } else {
            sql = "INSERT INTO suivre (Code, Groupe_ID) VALUES (" +
                    Integer.parseInt(result[0]) + ", " + code + ")";
        }
        BEGEOT_BUNOUF_Database_Connection database = new BEGEOT_BUNOUF_Database_Connection();

        database.run_Statement_WRITE(sql);
        database.Database_Deconnection();

        if (gui != null) {
            gui.displayCours();
            gui.setVisible(true);
        } else {
            guiGroupe.displayCours();
            guiGroupe.setVisible(true);
        }

        dispose();
    }
}
