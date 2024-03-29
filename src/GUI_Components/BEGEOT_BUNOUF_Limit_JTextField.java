package GUI_Components;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * Code that limits the size of a {@link BEGEOT_BUNOUF_CustomJTextField}
 *
 * @author Hugues
 */
class BEGEOT_BUNOUF_Limit_JTextField extends PlainDocument
{
    private final int limit;

    BEGEOT_BUNOUF_Limit_JTextField(int limit)
    {
        super();
        this.limit = limit;
    }


    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
    {
        if (str == null)
            return;

        if ((getLength() + str.length()) <= limit)
            super.insertString(offset, str, attr);
    }
}