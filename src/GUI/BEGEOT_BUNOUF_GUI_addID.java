package GUI;


import static GUI.BEGEOT_BUNOUF_GUI_USER_Admin.WindowClosingVisible;

import GUI_Components.BEGEOT_BUNOUF_CustomJFrame;
import GUI_Components.BEGEOT_BUNOUF_CustomJTextField;
import GUI_Components.BEGEOT_BUNOUF_DateFunctions;
import UsefulFunctions.BEGEOT_BUNOUF_Database_Connection;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Month;

public class BEGEOT_BUNOUF_GUI_addID extends BEGEOT_BUNOUF_CustomJFrame {

    private static final int DIM_X = 400;
    private static final int DIM_Y = 300;

    private JPanel panel_ID;
    private JTextField textCity;
    private DatePicker textDate;
    private JTextField textSexe;
    private JButton buttonSave;
    private JTextField textCountry;

    private BEGEOT_BUNOUF_GUI_chercherPersonne gui;

    /**
     * Constructeur de l'interface
     *
     * @param ID_personne ID de la personne concernée
     * @param gui         Interface de la personne
     */
    public BEGEOT_BUNOUF_GUI_addID(int ID_personne, BEGEOT_BUNOUF_GUI_chercherPersonne gui) {
        super("Ajouter une identité", false, DIM_X, DIM_Y);
        this.gui = gui;
        WindowClosingVisible(this, gui);


        buttonSave.addActionListener(e -> saveID(ID_personne));

        textDate.setSettings(BEGEOT_BUNOUF_DateFunctions.customDates());
        LocalDate date = LocalDate.of(2000, Month.JANUARY, 1);
        textDate.setDate(date);

        add(panel_ID);
        pack();
        revalidate();
        setVisible(true);
    }

    /**
     * Création des contraintes du form
     */
    private void createUIComponents() {
        textDate = new DatePicker();
        textCity = new BEGEOT_BUNOUF_CustomJTextField("ALPHABET", false, 20);
        textSexe = new BEGEOT_BUNOUF_CustomJTextField("SEXE", false, 1);
    }

    /**
     * Sauvegarde de la nouvelle identité de la personne
     *
     * @param ID_personne ID de la personne concernée
     */
    private void saveID(int ID_personne) {
        BEGEOT_BUNOUF_Database_Connection database = new BEGEOT_BUNOUF_Database_Connection();

        if (textCity.getText().length() == 0 || textSexe.getText().length() == 0) {
            System.out.println("ERROR");
        } else {
            if (ID_personne != -1) {
                String sql = "INSERT INTO identite (date_naissance, ville_naissance, pays_naissance, sexe, ID_Personne) VALUES (" +
                        "'" + textDate.getDate().toString() + "','" +
                        textCity.getText() + "','" +
                        textCountry.getText() + "','" +
                        textSexe.getText() + "'," +
                        ID_personne + ")";

                database.run_Statement_WRITE(sql);
                database.Database_Deconnection();

                setVisible(false);
                gui.displayID();
                gui.setVisible(true);

                dispose(); //Destroy the Frame object
            }
        }

    }


}
