package de.jme.mpjoe.swing;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

// Eigene Action Klasse, weil:
// - ich möchte setSelected() / isSelected() statt put/getValue(SELECTED_KEY, ...));
// - ich möchte an der Action auch das selectedIcon sowie disabledIcon verankern
// - für Mpjoe brauche ich z.B. bei der kombinierten Play/Pause Action einen zusätzlichen Zustand

// TODO:
// - Bzgl. der Accelerator Keys schreibt Apple, man solle "java.awt.Tookit.getMenuShortcutKeyMask()" verwenden
//   siehe https://developer.apple.com/library/mac/documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html
public abstract class MpjAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final String THIRDSTATE_KEY  = "MpjThirdstateKey";
    public static final String BLINKING_KEY    = "MpjBlinkingKey";

    private Icon selectedIcon;
    private Icon thirdstateIcon;
    private Icon disabledIcon;
    private Icon pressedIcon;
    private Icon rolloverIcon;


    public MpjAction() {
    }

    public MpjAction(String name) {
        super(name);
    }

    public MpjAction(String name, Icon icon) {
        super(name, icon);
    }

    /**
     * Set the Action Name
     */
    public void setName(String name) {
        putValue(NAME, name);
    }

    /**
     * Get the Action Name
     */
    public String getName() {
        return (String)getValue(NAME);
    }

    /**
     * Set the Action Icon
     */
    public void setIcon(Icon icon) {
        putValue(SMALL_ICON, icon);
    }

    /**
     * Set the Action Icon from resource
     */
    public void setIconFromResource(String resourcePath) {
        setIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action Icon
     */
    public Icon getIcon() {
        return (Icon)getValue(SMALL_ICON);
    }

    /**
     * Set the Action largeIcon
     */
    public void setLargeIcon(Icon icon) {
        putValue(LARGE_ICON_KEY, icon);
    }

    /**
     * Set the Action largeIcon from resource
     */
    public void setLargeIconFromResource(String resourcePath) {
        setLargeIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action largeIcon
     */
    public Icon getLargeIcon() {
        return (Icon)getValue(LARGE_ICON_KEY);
    }

    /**
     * Set the Action selectedIcon
     */
    public void setSelectedIcon(Icon icon) {
        selectedIcon = icon;
        setSelectable(true);
    }

    /**
     * Set the Action selectedIcon from resource
     */
    public void setSelectedIconFromResource(String resourcePath) {
        setSelectedIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action selectedIcon
     */
    public Icon getSelectedIcon() {
        return selectedIcon;
    }

    /**
     * Set the Action thirdstateIcon
     */
    public void setThirdstateIcon(Icon icon) {
        thirdstateIcon = icon;
    }

    /**
     * Set the Action thirdstateIcon from resource
     */
    public void setThirdstateIconFromResource(String resourcePath) {
        setThirdstateIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action thirdstateIcon
     */
    public Icon getThirdstateIcon() {
        return thirdstateIcon;
    }

    /**
     * Set the Action disabledIcon
     */
    public void setDisabledIcon(Icon icon) {
        disabledIcon = icon;
    }

    /**
     * Set the Action disabledIcon from resource
     */
    public void setDisabledIconFromResource(String resourcePath) {
        setDisabledIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action disabledIcon
     */
    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    /**
     * Set the Action pressedIcon
     */
    public void setPressedIcon(Icon icon) {
        pressedIcon = icon;
    }

    /**
     * Set the Action pressedIcon from resource
     */
    public void setPressedIconFromResource(String resourcePath) {
        setPressedIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action pressedIcon
     */
    public Icon getPressedIcon() {
        return pressedIcon;
    }

    /**
     * Set the Action rolloverIcon
     */
    public void setRolloverIcon(Icon icon) {
        rolloverIcon = icon;
    }

    /**
     * Set the Action rolloverIcon from resource
     */
    public void setRolloverIconFromResource(String resourcePath) {
        setRolloverIcon(new ImageIcon(System.class.getResource(resourcePath)));
    }

    /**
     * Get the Action rolloverIcon
     */
    public Icon getRolloverIcon() {
        return rolloverIcon;
    }

    /**
     * Set the Action short description, used for Tooltim
     */
    public void setShortDescription(String description) {
        putValue(SHORT_DESCRIPTION, description);
    }

    /**
     * Get the Action short description
     */
    public String getShortDescription() {
        return (String)getValue(SHORT_DESCRIPTION);
    }

    /**
     * Set the Action accelerator key
     * e.g. setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
     */
    public void setAccelerator(KeyStroke accelerator) {
        putValue(ACCELERATOR_KEY, accelerator);
    }

    /**
     * Get the Action accelerator key
     */
    public KeyStroke getAccelerator() {
        return (KeyStroke)getValue(ACCELERATOR_KEY);
    }

    /**
     * Set the Action mnemonic
     * This is the e.g. underlined letter of a menue entry
     * e.g. setMnemonic(KeyEvent.VK_A);
     */
    public void setMnemonic(int mnemonic) {
        putValue(MNEMONIC_KEY, mnemonic);
    }

    /**
     * Get the Action mnemonic
     */
    public int getMnemonic() {
        return (int)getValue(MNEMONIC_KEY);
    }

    /**
     * Make the action selectable
     */
    public void setSelectable(boolean b) {
        if (b == isSelectable()) return;
        if (!b) {
            putValue(SELECTED_KEY, null);
        } else {
            putValue(SELECTED_KEY, false);
        }
    }

    /**
     * Determine if the action is selectable
     */
    public boolean isSelectable() {
        return getValue(SELECTED_KEY) != null;
    }

    /**
     *  Change the selected state of the action
     *  This will implicity make the action selectable
     */
    public void setSelected(boolean b) {
        Object value = getValue(SELECTED_KEY);
        if (value == null || (boolean)value != b)
            putValue(SELECTED_KEY, b);
    }

    /**
     * Determine if the action is selected or not
     */
    public boolean isSelected() {
        Object value = getValue(SELECTED_KEY);
        return (value != null) && (boolean)value;
    }

    /**
     * The "thirdstate" is a special state for e.g. the combined Play/Pause Button.
     * It's something like "selected in a different way"
     */
    public void setThirdstate(boolean b) {
        Object value = getValue(THIRDSTATE_KEY);
        if (value == null || (boolean)value != b)
            putValue(THIRDSTATE_KEY, b);
    }

    /**
     * Determine if the action is set to thirdstate or not
     */
    public boolean isThirdstate() {
        Object value = getValue(THIRDSTATE_KEY);
        return (value != null) && (boolean)value;
    }

    /**
     * Blinking lets the appearence of the button toggeling between it's normal state
     * and its selected or thirdstate icon - whether is currently set
     * If the associated action isn't either selected or thirdstate, then the button won't be displayed blinking
     */
    public void setBlinking(boolean b) {
        Object value = getValue(BLINKING_KEY);
        if (value == null || (boolean)value != b)
            putValue(BLINKING_KEY, b);
    }

    /**
     * Determine if the action is set to blinking appearence or not
     */
    public boolean isBlinking() {
        Object value = getValue(BLINKING_KEY);
        return (value != null) && (boolean)value;
    }

}
