/*This code is originally from https://stackoverflow.com/users/928711/guillaume-polet
 * It has been slightly modified
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GhostText implements FocusListener, DocumentListener, PropertyChangeListener {
    private final JTextField textfield;
    private boolean isEmpty;
    private Color ghostColor;
    private Color foregroundColor;
    private final String ghostText;

    protected GhostText(final JTextField textfield, String ghostText) {
        super();
        this.textfield = textfield;
        this.ghostText = ghostText;
        this.ghostColor = Color.LIGHT_GRAY;
        textfield.addFocusListener(this);
        registerListeners();
        updateState();
        if (!this.textfield.hasFocus()) {
            focusLost(null);
        }
    }

    protected GhostText(final JTextField textfield, String ghostText, Color color) {
        super();
        this.textfield = textfield;
        this.ghostText = ghostText;
        this.ghostColor = color;
        textfield.addFocusListener(this);
        registerListeners();
        updateState();
        if (!this.textfield.hasFocus()) {
            focusLost(null);
        }
    }

    private void registerListeners() {
        textfield.getDocument().addDocumentListener(this);
        textfield.addPropertyChangeListener("foreground", this);
    }

    private void unregisterListeners() {
        textfield.getDocument().removeDocumentListener(this);
        textfield.removePropertyChangeListener("foreground", this);
    }

    private void updateState() {
        isEmpty = textfield.getText().length() == 0;
        foregroundColor = textfield.getForeground();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                textfield.setText("");
                textfield.setForeground(foregroundColor);
            } finally {
                registerListeners();
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                textfield.setText(ghostText);
                textfield.setForeground(ghostColor);
            } finally {
                registerListeners();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateState();
    }

}