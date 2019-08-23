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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import transfer_on_lan.Format;
import transfer_on_lan.Reception;
import static transfer_on_lan.Constants.PROGRESS_BAR_LENGTH;

/**
 * Cette classe permet d'afficher les informations sur les expéditions dans une
 * liste.
 *
 * @author Arnaud FRANCOIS
 */
public class CellRendererReception extends JPanel implements ListCellRenderer {

    private JLabel labMessage;
    private JProgressBar prbBar;
    private JLabel labSender;
    private JLabel labVolume;
    private Locale locale;
    private boolean selection;
    private ResourceBundle strings;
    private Format format;
    private DateFormat shortDateFormat;
    private JLabel labDate;

    /**
     *
     * @param locale
     */
    public CellRendererReception(Locale locale) {
        super();
        setOpaque(true);

        this.locale = locale;
        this.strings = ResourceBundle.getBundle("lang/strings", locale);
        this.format = new Format(locale, this.strings);
        this.shortDateFormat = new SimpleDateFormat("H:mm", locale);

        setLayout(new GridBagLayout());

        /* ********** line 1 ********** */
        this.labMessage = new JLabel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labMessage, constraints);

        this.labDate = new JLabel();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(this.labDate, constraints);

        /* ********** line 2 ********** */
        this.prbBar = new JProgressBar(0, PROGRESS_BAR_LENGTH);
        this.prbBar.setStringPainted(true);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1.0;
        add(this.prbBar, constraints);

        /* ********** line 3 ********** */
        this.labSender = new JLabel();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(this.labSender, constraints);

        this.labVolume = new JLabel();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(this.labVolume, constraints);

        this.selection = false;

        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        this.setDoubleBuffered(true);
    }

    
    
    
    /**
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Reception reception = (Reception) value;

        //this.labMessage.setText(reception.getMessage());
        switch (reception.getState()) {
            case Reception.IN_PROGRESS:
                this.labMessage.setText(reception.getNumber() + " - " + strings.getString("transfer") + " : " + strings.getString("inProgress"));
                break;
            case Reception.DONE:
                this.labMessage.setText(reception.getNumber() + " - " + strings.getString("transfer") + " : " + strings.getString("completed"));
                break;
            case Reception.REFUSED:
                this.labMessage.setText(reception.getNumber() + " - " + strings.getString("transfer") + " : " + strings.getString("refused"));
                break;
            case Reception.CANCELED:
                this.labMessage.setText(reception.getNumber() + " - " + strings.getString("transfer") + " : " + strings.getString("canceled"));
                break;
            case Reception.ERROR:
                // not supported for now
                break;
        }

        this.labMessage.setBackground(list.getBackground());
        this.prbBar.setValue(reception.getProgression());
        this.labSender.setText(reception.getSenderName());

        String elapsedTimeString = this.format.formatDurationHMS(reception.getElapsedTime());
        String totalDataVolumeString = this.format.formatVolume(reception.getTotalDataVolume());
        String dataVolumeString = this.format.formatVolume(reception.getDataVolume());
        String flowRateString = this.format.formatFlowRate(reception.getDataVolume(), reception.getElapsedTime());

        long remainingDataVolume = reception.getTotalDataVolume() - reception.getDataVolume();
        double flowRate = reception.getDataVolume() / (reception.getElapsedTime() + 1);
        double remainingTime = remainingDataVolume / flowRate;
        String remainingTimeString = this.format.formatDurationHMS((int) remainingTime);

        if (reception.getState() == Reception.DONE) {
            this.labVolume.setText(dataVolumeString + " - " + elapsedTimeString + " (" + flowRateString + ")");
            //this.labDate.setText(shortDateFormat.format(reception.getDate()));
        } else {
            this.labVolume.setText(dataVolumeString + " " + this.strings.getString("of") + " " + totalDataVolumeString + " - " + remainingTimeString + " (" + flowRateString + ")");
        }

        labDate.setText(reception.getFormattedDate());

        // set foreground color and background color
        if (isSelected && !this.selection) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            this.selection = true;
        } else if (!isSelected && this.selection) {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            this.selection = false;
        }

        setToolTipText(reception.getFileNames().toString());

        return this;
    }
}
