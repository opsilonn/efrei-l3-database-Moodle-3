package GUI;


import GUI_Components.CustomJFrame;
import Gestion_admin.Database_Connection;
import recherche.rechercheEtudiant;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Fenêtre dédiée à l'utilisation du logiciel par un Eleve
 *
 * Cette classe hérite de {@link CustomJFrame}
 *
 * @author Hugues
 */
class GUI_Etudiant extends CustomJFrame
{
    private static final int DIM_X = 800;
    private static final int DIM_Y = 500;

    private rechercheEtudiant ETUDIANT;

    private String matricule;

    private JPanel panel;

    private JLabel labelNom;
    private JLabel labelMatricule;
    private JLabel labelGroupe;
    private JLabel labelMoyenne;

    private JButton buttonBulletin;

    private JLabel labelErreur;

    private JScrollPane bulletin;
    private JTable bulletinValeurs;

    /**
     * Création de l'interface pour un Eleve
     *
     * @param  database liaison à la base de données SQL
     * @param matricule - Matricule de l'élève connecté
     */
    public GUI_Etudiant(Database_Connection database, String matricule)
    {
        super("Etudiant", true, database, DIM_X, DIM_Y);
        this.matricule = matricule;
        ETUDIANT = new rechercheEtudiant(database);


        remplirInformations();
        remplirNotes();


        add(panel);
        pack();
        revalidate();
        setVisible(true);
    }


    /**
     * Remplissage des champs sur l'information de l'étudiant connecté
     */
    private void remplirInformations()
    {
        // ON AFFICHE LE PRENOM + NOM
        labelNom.setText(
                ETUDIANT.getPersonne(matricule, "Prenom") + " " +
                ETUDIANT.getPersonne(matricule, "Nom").toUpperCase());


        // ON AFFICHE LE MATRICULE
        labelMatricule.setText( matricule );


        // ON AFFICHE LE GROUPE (si aucun trouvé, on affiche "aucun groupe")
        if( ETUDIANT.possedeGroupe(matricule) )
        {
            labelGroupe.setText(
                    ETUDIANT.getGroupe(matricule, "Groupe_ID")+ " - " +
                    ETUDIANT.getGroupe(matricule, "Nom"));
        }
        else
        {
            labelGroupe.setText("N'appartient à aucun Groupe");
        }


        // ON AFFICHE LA MOYENNE (si aucun trouvé, on affiche "indéfinie")
        float moyenneGenerale = ETUDIANT.moyenneGenerale(matricule);
        if( moyenneGenerale == -1 )
            labelMoyenne.setText("indéfinie");
        else
            labelMoyenne.setText(String.valueOf(moyenneGenerale));
    }




    private String[] columns = new String[]{"Matière", "Code - Matière","Moyenne", "Type", "Note", "Coefficient", "ID-Note"};
    private Object[][] DATA;
    private int CURSOR;
    private final static int SIZE_OCCUPIED_BY_COURSE = 4;


    /**
     * Remplissage des champs sur les notes de l'étudiant connecté
     */
    private void remplirNotes()
    {
        String query;
        ResultSet resultat;
        CURSOR = 0;

        int nombreCours = ETUDIANT.nombreCours(matricule);

        if(nombreCours == 0)
        {
            bulletin.setVisible(false);
            bulletinValeurs.setVisible(false);
        }
        else
        {
            labelErreur.setVisible(false);
            DATA = new Object[nombreCours * SIZE_OCCUPIED_BY_COURSE][columns.length];

            query =
                "SELECT * " +
                "FROM etudiant, groupe, cours, suivre " +
                "WHERE etudiant.Matricule = " + matricule + " " +
                "AND etudiant.Groupe_ID = groupe.Groupe_ID " +
                "AND groupe.Groupe_ID = suivre.Groupe_ID " +
                "AND suivre.Code = cours.Code;";

            resultat = database.run_Statement_READ(query);


            try
            {
                while ( resultat.next() )
                {
                    String coursCode = resultat.getString("cours.Code");
                    String coursNom = resultat.getString("cours.Nom");
                    String coursCoef = resultat.getString("cours.Coefficient");

                    Map<String, String> coefficients = new HashMap<>();
                    coefficients.put("TP", resultat.getString("cours.TP_Pourcentage"));
                    coefficients.put("DE", resultat.getString("cours.DE_Pourcentage"));
                    coefficients.put("PROJET", resultat.getString("cours.Projet_Pourcentage"));

                    remplirNotesCours(coursCode, coursNom, coursCoef, coefficients);
                }

                DefaultTableModel model = new DefaultTableModel(DATA, columns);
                bulletinValeurs.setModel(model);
                centrerJTable(bulletinValeurs);
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }

        }
    }


    /**
     * Remplissage des champs sur les notes de l'étudiant connecté sur une matière spécifique
     * @param coursCode Code du cours en question
     * @param coursNom Code du cours en question
     * @param coefficients Tableau des coefficients des notes du cours en question
     */
    private void remplirNotesCours(String coursCode, String coursNom, String coursCoef, Map<String, String> coefficients)
    {
        String TP_note = "Pas de note";
        String DE_note = "Pas de note";
        String PROJET_note = "Pas de note";

        String TP_ID = "";
        String DE_ID = "";
        String PROJET_ID = "";

        String query =
                "SELECT * " +
                "FROM cours, note " +
                "WHERE cours.Code = " + coursCode + " " +
                "AND cours.Code = note.Code " +
                "AND note.Matricule_Etudiant = " + matricule + " ;";

        ResultSet resultat = database.run_Statement_READ(query);

        try
        {
            while( resultat.next() )
            {
                String note = resultat.getString("note.Valeur");
                String noteID = resultat.getString("note.ID");

                switch ( resultat.getString("note.Type") )
                {
                    case "TP":
                        TP_note = note;
                        TP_ID = noteID;
                        break;

                    case "DE":
                        DE_note = note;
                        DE_ID = noteID;
                        break;

                    case "Projet":
                        PROJET_note = note;
                        PROJET_ID = noteID;
                        break;

                    default:
                        System.out.println("That's weird");
                        break;
                }
            }



            DATA[CURSOR + 1][0] = coursNom;
            DATA[CURSOR + 1][1] = coursCode;
            float moyenne = ETUDIANT.moyenne(matricule, coursCode);
            if( moyenne == -1 )
                DATA[CURSOR + 1][2] = "indéfinie";
            else
                DATA[CURSOR + 1][2] = moyenne;

            DATA[CURSOR + 2][0] = "coef : " + coursCoef;


            DATA[CURSOR + 0][3] = "TP";
            DATA[CURSOR + 1][3] = "DE";
            DATA[CURSOR + 2][3] = "Projet";

            DATA[CURSOR + 0][4] = TP_note;
            DATA[CURSOR + 1][4] = DE_note;
            DATA[CURSOR + 2][4] = PROJET_note;

            DATA[CURSOR + 0][5] = coefficients.get("TP");
            DATA[CURSOR + 1][5] = coefficients.get("DE");
            DATA[CURSOR + 2][5] = coefficients.get("PROJET");

            DATA[CURSOR + 0][6] = TP_ID;
            DATA[CURSOR + 1][6] = DE_ID;
            DATA[CURSOR + 2][6] = PROJET_ID;

            CURSOR += SIZE_OCCUPIED_BY_COURSE;
        }
        catch (SQLException e1)
        {
            e1.printStackTrace();
        }
    }
}