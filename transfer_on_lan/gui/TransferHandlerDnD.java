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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.TransferHandler;
import transfer_on_lan.Main;
import transfer_on_lan.User;
import static transfer_on_lan.Constants.NAME;
import transfer_on_lan.ThreadTransferSending;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class TransferHandlerDnD extends TransferHandler {

    private Main main;
    private Window window;
    private DataFlavor uriDataFlavor;

    public TransferHandlerDnD(Main main, Window window) {
        super();
        this.main = main;
        this.window = window;
        try {
            this.uriDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NAME).logp(Level.SEVERE, "TransferHandlerDnD", "TransferHandlerDnD", "erreur", ex);
        }
    }

    @Override
    public boolean canImport(TransferSupport supp) {
        boolean result = false;

        // on accepte seulement une chaîne de caractères
        if (supp.isDataFlavorSupported(this.uriDataFlavor) || supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // Linux : uriDataFlavor
            // Windows : DataFlavor.javaFileListFlavor
            result = true;
        }

        return result;
    }

    @Override
    public boolean importData(TransferSupport supp) {
        boolean result = true;
        File[] files = null;

        // récupération de la chaîne transférée
        Transferable t = supp.getTransferable();
        if (supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // cas Windows
            try {
                List listOfFiles = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
                files = (File[]) listOfFiles.toArray();
            } catch (Exception ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "TransferHandlerDnD", "importData", "erreur", ex);
                result = false;
                return result;
            }
        } else if (supp.isDataFlavorSupported(uriDataFlavor)) {
            // cas Linux
            String data = null;
            try {
                data = (String) t.getTransferData(this.uriDataFlavor);
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(NAME).logp(Level.SEVERE, "TransferHandlerDnD", "importData", "problème flavor", ex);
                result = false;
                return result;
            } catch (IOException ex) {
                Logger.getLogger(NAME).logp(Level.SEVERE, "TransferHandlerDnD", "importData", "erreur entrée/sortie", ex);
                result = false;
                return result;
            }

            StringTokenizer st = new java.util.StringTokenizer(data, "\r\n");
            int nbFiles = 0;
            files = new File[st.countTokens()];
            while (st.hasMoreTokens()) {
                try {
                    files[nbFiles++] = new File(new URI(st.nextToken()));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "TransferHandlerDnD", "importData", "erreur de syntaxe URI", ex);
                }
            }
        }
        // pour tous les utilisateurs sélectionnés, un transfert doit être effectué
        for (int i = 0; i < this.window.getLstUsers().getSelectedValues().length; i++) {
            User user = (User) this.window.getLstUsers().getSelectedValues()[i];
            //SwingWorkerTransferSending swts = new SwingWorkerTransferSending(this.main, user, files);
            ThreadTransferSending tts = new ThreadTransferSending(this.main, user, files);
            this.main.getExecutor().execute(tts);
        }

        return result;
    }
}
