package de.jme.mpjoe.swing;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JToggleButton;

// Hmm, offenbar soll man sich nicht von AbstractButton ableiten sondern von JButton
//   http://stackoverflow.com/questions/5751311/creating-a-custom-button-in-java-with-jbutton
//
// JToggleButton
//   das selectedIcon zeigt an, wenn die Action selected ist
//
//
// JButton
//   das selectedIcon zeigt NICHT an, wenn die Action selected ist
//   aber mit JButton.seSelected(true) wird das selectedIcon angezeigt
//   ist also offenbar bzgl. selected nicht mit der Action gekoppelt
public class MpjButton extends JToggleButton {
    private static final long serialVersionUID = 1L;

    Icon thirdstateIcon;

    public MpjButton(AbstractAction action) {
        //JButton j;
        //super(action);
        setAction(action);
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusable(false);
        setHideActionText(true);
    }

    public MpjButton(MpjAction action) {
        //super(action);
        setAction(action);
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusable(false);
        setHideActionText(true);

        setSelectedIcon(action.getSelectedIcon());
        thirdstateIcon = action.getThirdstateIcon();
        setDisabledIcon(action.getDisabledIcon());
        setPressedIcon(action.getPressedIcon());
        setRolloverIcon(action.getRolloverIcon());
        //setSelected(action.isSelected());
    }
}
