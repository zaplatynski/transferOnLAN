/*
 Copyright (C) 2011 Arnaud FRANÇOIS
  
 This file is part of Transfer on LAN (http://code.google.com/p/transfer-on-lan).
 
 Transfer on LAN is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Transfer on LAN is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Transfer on LAN.  If not, see <http://www.gnu.org/licenses/>. 
 */
package transfer_on_lan.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import transfer_on_lan.User;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class CellRendererUser extends JPanel implements ListCellRenderer {

    private JLabel labImage;
    private JLabel labLine1;
    private JLabel labLine2;

    public CellRendererUser() {
        super();
        setOpaque(true);
        setLayout(new GridBagLayout());

        labImage = new JLabel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        add(labImage, constraints);

        labLine1 = new JLabel();
        Font font = new Font("Arial", Font.PLAIN, 18);
        labLine1.setFont(font);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1.0;
        labLine1.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        add(labLine1, constraints);

        labLine2 = new JLabel();
        font = new Font("Helvetica", Font.ITALIC, 10);
        labLine2.setFont(font);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
        labLine2.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        add(labLine2, constraints);
        
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User user = (User) value;
 
        labImage.setIcon(user.getIcon());
        labLine1.setText(user.getName());
        labLine2.setText(user.getIpAddress().toString());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
