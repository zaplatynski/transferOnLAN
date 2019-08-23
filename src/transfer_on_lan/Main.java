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
package transfer_on_lan;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import transfer_on_lan.gui.ModelSending;
import transfer_on_lan.gui.ModelUser;
import transfer_on_lan.gui.Window;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import transfer_on_lan.gui.ActionNotificationPresenceEmission;
import transfer_on_lan.gui.ActionQuit;
import transfer_on_lan.gui.ActionShow;
import transfer_on_lan.gui.ModelReception;
import static transfer_on_lan.Constants.*;
import transfer_on_lan.gui.ModelUserFiltered;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class Main {

    private static boolean modeDebug; // si vrai alors mode "debug" : les messages d'erreur sont affichés sur la sortie standard
    private String userId;
    private String userName;
    private ModelUser modelUser;
    private ModelUserFiltered modelUserFiltered;
    private ModelSending modelSending;
    private ModelReception modelReception;
    private String filter;
    private Window window;
    private String sourceDirectoryPath;
    private String destinationDirectoryPath;
    private File workingDirectory;
    private File tmpDirectory; // le répertoire temporaire
    //private File propertiesFile;
    private Properties properties; // les propriétés de l'application
    private Locale locale;
    private String appearence;
    private ResourceBundle strings; // pour l'internationalisation
    private IPAddress ipAddress;
    private InetAddress IPGroup;
    private MulticastSocket socketMulticast;
    private boolean presenceInUserList;
    private Handler fileHandler; // pour les log
    private int windowWidth;
    private int windowHeight;
    private String workingDirectoryPath;
    private int portTCP;
    private int serverPortUDP;
    private int serverPortTCP;
    private String machineName;
    private boolean avatarOn; // vrai si on veut utiliser icône pour représenter l'utilisateur
    private String avatarFilePath;
    private ImageIcon defaultIconAvatar;
    private boolean soundOn; // vrai => joue un son à la réception d'une demande de transfert
    private boolean acceptanceAuto; // vrai => les transferts sont acceptés automatiquement
    private String soundFilePath; // chemin du son qui sera joué à la réception d'une demande de transfert
    private boolean showHiddenFiles;
    private boolean showTabSendings;
    private boolean showTabReceptions;
    private boolean showTabHelp;
    private boolean showTabAbout;
    private boolean showFilter;
    private ExecutorService executor;
    private boolean invisible; // true => users don't see you
    private boolean useSystemTray; // true => use system tray
    private TrayIcon trayIcon;
    private Preferences prefs;
    private int x, y;

    public Main() {
    }

    /**
     * entrance of the application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        modeDebug = false;

        // management of command line arguments
        if (args.length > 0) {
            if (Tools.isContained("--info", args)) {
                System.out.println(NAME + " - " + VERSION);
                System.out.println(DESCRIPTION);
                System.out.println(AUTHOR);
                System.exit(0);
            } else if (Tools.isContained("--debug", args)) {
                modeDebug = true;
            } else if (Tools.isContained("--version", args) || Tools.isContained("-version", args)) {
                System.out.println(NAME + " - " + VERSION);
                System.out.println(DESCRIPTION);
                System.out.println("Copyright (C) 2011-2013 " + AUTHOR);
                System.out.println(SITE);
                System.out.println("License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>");
                System.out.println();
                System.out.println("This is free software; you are free to change and redistribute it.");
                System.out.println("There is NO WARRANTY, to the extent permitted by law.");
                System.exit(0);
            } else {
                System.out.println(args[0] + " : invalid option");
                System.out.println("Use :  java -jar transfer_on_lan-" + VERSION + ".jar [long option]");
                System.out.println("Long options : ");
                System.out.println("  --debug");
                System.out.println("  --info");
                System.out.println("  --version");
                System.exit(0);
            }
        }

        // creation of main object
        Main m = new Main();
        m.start();
    }

    /**
     *
     */
    private void start() {
        this.userId = Tools.generateId(20);

        // le répertoire de travail de Transfer On Lan est ".transferOnLan" dans le répertoire personnel de l'utilisteur
        this.workingDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "transferOnLan";
        this.workingDirectory = new File(this.workingDirectoryPath);

        // si le répertoire de travail de Transfer On Lan n'existe pas, on le créé
        if (!this.workingDirectory.exists()) {
            this.workingDirectory.mkdir();
        }

        this.prefs = Preferences.userRoot();

        // mise en place du système de log
        try {
            this.fileHandler = new FileHandler(this.workingDirectoryPath + File.separator + LOG_FILE_NAME, 10000, 1, true); // fichier de 10Ko, 1 seul fichier, ajout en fin de fichier
            this.fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            Logger.getLogger(NAME).setUseParentHandlers(modeDebug);  // empêche l'affichage des messages sur la sortie erreur standard
        } catch (IOException ex) {
            System.err.println("problème avec le fichier de log");
        }

        Logger.getLogger(NAME).log(Level.INFO, "Démarrage");

        // récupération du nom de la machine
        try {
            this.machineName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "problème lors de la récupération du nom de la machine", ex);
        }

        // mise en place du répertoire temporaire
        // (le nom du répertoire dépend du numéro de port TCP utilisé)
        this.tmpDirectory = new File(this.workingDirectory, this.portTCP + "-tmp"); // à effacer en quittant
        if (this.tmpDirectory.exists()) {
            Tools.deleteDirectory(this.tmpDirectory);
        }
        this.tmpDirectory.mkdir();

        // récupération des propriétés ////////////////////////////////////////
        loadPrefs();

        // récupération de l'icône représentant l'avatar par défaut ///////////
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/images/" + ICON_48_48));
            // enregistrement dans le répertoire temporaire : nécessaire pour le transfert de l'avatar
            File defaultIconAvatarFile = new File(tmpDirectory + File.separator + ICON_48_48);
            ImageIO.write(image, "png", defaultIconAvatarFile);
            this.defaultIconAvatar = new ImageIcon(image.getScaledInstance(AVATAR_WIDTH, AVATAR_HEIGHT, Image.SCALE_DEFAULT));
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "problème avec l'image de l'avatar par défaut", ex);
        }
        // fin récupération de l'icône représentant l'avatar par défaut //////

        // set the look & feel //////////////////////////////////////
        if (this.appearence.compareTo("nimbus") == 0) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "problème avec Nimbus", ex);
            }
        } else if (this.appearence.compareTo("metal") == 0) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "problème avec Metal", ex);
            }
        } else if (this.appearence.compareTo("system") == 0) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "problème avec le l&f système", ex);
            }
        }

        this.executor = Executors.newSingleThreadExecutor();

        this.modelUser = new ModelUser();
        this.modelUserFiltered = new ModelUserFiltered(this.filter, this.modelUser.getUsers());
        this.modelSending = new ModelSending();
        this.modelReception = new ModelReception();

        // run the graphical interface /////////////////////////////////
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // affichage de l'interface graphique
                window = new Window(Main.this, Main.this.windowWidth, Main.this.windowHeight);
                window.setTitle(NAME + " - " + VERSION);
                if (x == -1 || y == -1) {
                    window.setLocationRelativeTo(null);       // center the frame
                } else {
                    window.setLocation(x, y);
                }
                window.pack();
                window.setVisible(true);
            }
        });

        if (this.useSystemTray) {
            putInSystemTray();
        }

        try {
            Thread.sleep(700);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // mise en place d'un serveur de sockets
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.serverPortTCP, 0, this.ipAddress.getInet4Address()); // 0 => backlog par défaut
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "aucun port disponible");
            JOptionPane.showMessageDialog(null, this.strings.getString("Main.appIsRunning") + ".", this.strings.getString("Main.warning") + " !", JOptionPane.ERROR_MESSAGE);
            System.err.println("l'appli est déjà lancée");
            System.exit(1); // on arrête
        }

        this.portTCP = serverSocket.getLocalPort();
        // fin mise en place d'un serveur de sockets

        this.socketMulticast = null;
        this.IPGroup = null;
        try {
            this.socketMulticast = new MulticastSocket(this.serverPortUDP);
            this.socketMulticast.setInterface(this.ipAddress.getInet4Address());
            this.IPGroup = InetAddress.getByName(GROUP);
            this.socketMulticast.joinGroup(this.IPGroup);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "erreur au niveau du socket multicast", ex);
        }

        // avons-nous avons choisi d'apparaître dans la liste des utilisateurs ?
        if (isPresenceInUserList()) {
            User user = new User(this.userId, this.userName, this.ipAddress, this.portTCP, this.machineName);

            // affectation du fichier avatar à l'utilisateur
            File avatarFile = new File(AvatarFilePath());
            if (!isAvatarOn() || !avatarFile.canRead()) { // si l'avatar doit être utilisé ou si le fichier n'est pas lisible
                // on utilise l'avatar par défaut
                try {
                    BufferedImage image = ImageIO.read(getClass().getResource("/images/" + ICON_48_48));
                    avatarFile = new File(getTmpDirectory() + File.separator + ICON_48_48);
                    ImageIO.write(image, "png", avatarFile);
                } catch (IOException ex) {
                    Logger.getLogger(NAME).logp(Level.SEVERE, "NotificationReception", "gererPresenceUtilisateur", "erreur entrée/sortie", ex);
                }
            }

            user.setAvatarFile(avatarFile);
            getModelUser().add(user);
        }

        // mise en place d'un daemon pour la réception des notifications //////
        java.util.Timer timerServerNotification = new java.util.Timer(true); // true => crétion d'un daemon
        timerServerNotification.schedule(new NotificationReception(this, this.socketMulticast), 0);

        // émission régulière d'un message pour signaler sa présence
        ActionNotificationPresenceEmission ane = new ActionNotificationPresenceEmission(this, this.socketMulticast, this.IPGroup);

        Timer timerSwingNotificationClient = new Timer(WAITING * 1000, ane);
        timerSwingNotificationClient.setRepeats(true);
        timerSwingNotificationClient.start();
        // fin mise en place d'un daemon pour la réception des notifications //

        // émission d'un message pour signaler sa présence
        try {
            byte[] sentData = new byte[100];
            //  format message : encodage;identifiant;commande;nomUtilisateur;adresseIP;numeroPortTCP;nomMachine;
            String message = Charset.defaultCharset().name() + ";" + getUserId() + ";" + NOTIFICATION_PRESENCE + ";" + getUserName() + ";" + getIPAddress() + ";" + getPortTCP() + ";" + getMachineName() + ";";

            sentData = message.getBytes();
            DatagramPacket packet = new DatagramPacket(sentData, sentData.length, this.IPGroup, this.serverPortUDP);
            this.socketMulticast.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "ActionNotificationPresenceEmission", "actionPerformed", "erreur au niveau du socket", ex);
        }

        // mise en place du serveur de sockets TCP qui permet de gérer les transferts de fichiers
        try {
            while (true) { // boucle infinie
                Socket clientSocket = serverSocket.accept(); // attente d'un client
                //Service service = new Service(this, clientSocket);
                //SwingWorkerService service = new SwingWorkerService(this, clientSocket);
                ThreadService service = new ThreadService(this, clientSocket);
                try {
                    //service.doInBackground();
                    service.start();
                    //executeur.execute(transfertReception);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "erreur au niveau du socket", ex);
            System.exit(1);
        }
    }

    private void loadPrefs() {
        this.locale = new Locale(this.prefs.get("tol.language", System.getProperty("user.language", "en")));

        this.strings = ResourceBundle.getBundle("lang/strings", locale);
        this.userName = this.prefs.get("tol.userName", System.getProperty("user.name", DV_USER_NAME));
        this.appearence = this.prefs.get("tol.appearance", "nimbus");
        this.sourceDirectoryPath = this.prefs.get("tol.sourceDirectoryPath", System.getProperty("user.home"));
        this.destinationDirectoryPath = this.prefs.get("tol.destinationDirectoryPath", System.getProperty("user.home"));

        ArrayList<IPAddress> addresses = Tools.availableIPAddresses(); // récupération des adresses disponibles sur la machine
        if (addresses.isEmpty()) {
            // il n'y a pas d'adresse IP utilisable
            // le problème est signalé à l'utilisateur
            JOptionPane.showMessageDialog(null, this.strings.getString("Main.noIPAddress") + ".", this.strings.getString("Main.warning") + " !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(NAME).logp(Level.WARNING, "Main", "demarrer", "il n'y a pas d'adresse IP utilisable");
            System.exit(1); // on arrête le programme
        }

        if (this.prefs.get("tol.ipAddress", "").compareTo("") == 0) {
            // il n'y a pas d'adresse IP enregistrées
            if (addresses.size() <= 1) {
                // il n'y qu'une adresse IP disponible, on l'utilise
                this.ipAddress = addresses.get(0); // on prend la première adressse disponible
            } else {
                // il y a plusieurs adresses IP disponibles
                this.ipAddress = addresses.get(0); // on prend la première adressse disponible   
                // on prévient l'utilisateur de ce choix
                JOptionPane.showMessageDialog(null, this.strings.getString("Main.ipAddress") + " " + this.ipAddress + ".", this.strings.getString("Main.warning") + " !", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // il y a une adresse IP enregistrée
            this.ipAddress = new IPAddress(this.prefs.get("tol.ipAddress", "")); // récupération de l'adresse IP enregistrée
            // cette adresse est-elle présente dans la liste des adresses IP disponibles ?
            if (!addresses.contains(this.ipAddress)) {
                // non
                this.ipAddress = addresses.get(0); // on prend la première adressse disponible
                // et on prévient l'utilisateur
                JOptionPane.showMessageDialog(null, this.strings.getString("Main.ipAddressNotAvailable") + " (" + this.ipAddress + ").", this.strings.getString("Main.warning") + " !", JOptionPane.WARNING_MESSAGE);
            }
        }

        this.avatarOn = this.prefs.getBoolean("tol.avatarOn", DV_AVATAR_ON);
        this.avatarFilePath = this.prefs.get("tol.avatarFilePath", "");
        this.windowWidth = this.prefs.getInt("tol.width", WINDOW_WIDTH);
        this.windowHeight = this.prefs.getInt("tol.height", WINDOW_HEIGHT);
        this.x = this.prefs.getInt("tol.x", -1);
        this.y = this.prefs.getInt("tol.y", -1);
        this.presenceInUserList = this.prefs.getBoolean("tol.presenceInUserList", DV_PRESENCE_IN_USER_LIST);
        this.soundOn = this.prefs.getBoolean("tol.soundOn", DV_SOUND_ON);
        this.soundFilePath = this.prefs.get("tol.soundFilePath", "");
        this.acceptanceAuto = this.prefs.getBoolean("tol.acceptanceAuto", DV_ACCEPTANCE_AUTO_ON);
        this.showHiddenFiles = this.prefs.getBoolean("tol.showHiddenFiles", DV_SHOW_HIDDEN_FILES_ON);
        this.invisible = this.prefs.getBoolean("tol.invisible", DV_INVISIBLE_ON);
        this.useSystemTray = this.prefs.getBoolean("tol.systemTray", DV_SYSTEM_TRAY_ON);
        this.showTabSendings = this.prefs.getBoolean("tol.showTabSendings", DV_SHOW_TAB_ABOUT_ON);
        this.showTabReceptions = this.prefs.getBoolean("tol.showTabReceptions", DV_SHOW_TAB_ABOUT_ON);
        this.showTabHelp = this.prefs.getBoolean("tol.showTabHelp", DV_SHOW_TAB_ABOUT_ON);
        this.showTabAbout = this.prefs.getBoolean("tol.showTabAbout", DV_SHOW_TAB_ABOUT_ON);
        this.showFilter = this.prefs.getBoolean("tol.showFilter", DV_SHOW_FILTER_ON);
        this.filter = this.prefs.get("tol.filter", "");
        this.serverPortUDP = this.prefs.getInt("tol.serverPortUDP", DV_SERVER_PORT_UDP);
        this.serverPortTCP = this.prefs.getInt("tol.serverPortTCP", DV_SERVER_PORT_TCP);
    }

    public void savePrefs() {
        this.prefs.put("tol.language", this.locale.getLanguage());
        this.prefs.put("tol.appearance", this.appearence);
        this.prefs.put("tol.userName", this.userName);
        this.prefs.put("tol.destinationDirectoryPath", this.destinationDirectoryPath);
        this.prefs.put("tol.sourceDirectoryPath", this.sourceDirectoryPath);
        this.prefs.put("tol.ipAddress", this.ipAddress.toString());
        this.prefs.putBoolean("tol.presenceInUserList", this.presenceInUserList);
        this.prefs.putBoolean("tol.avatarOn", this.avatarOn);
        this.prefs.put("tol.avatarFilePath", this.avatarFilePath);
        this.prefs.putInt("tol.x", this.window.getX());
        this.prefs.putInt("tol.y", this.window.getY());
        this.prefs.putInt("tol.width", this.window.getWidth());
        this.prefs.putInt("tol.height", this.window.getHeight());
        this.prefs.putBoolean("tol.soundOn", this.soundOn);
        this.prefs.put("tol.soundFilePath", this.soundFilePath);
        this.prefs.putBoolean("tol.acceptanceAuto", this.acceptanceAuto);
        this.prefs.putBoolean("tol.showHiddenFiles", this.showHiddenFiles);
        this.prefs.putBoolean("tol.invisible", this.invisible);
        this.prefs.putBoolean("tol.systemTray", this.useSystemTray);
        this.prefs.putBoolean("tol.showTabSendings", this.showTabSendings);
        this.prefs.putBoolean("tol.showTabReceptions", this.showTabReceptions);
        this.prefs.putBoolean("tol.showTabHelp", this.showTabHelp);
        this.prefs.putBoolean("tol.showTabAbout", this.showTabAbout);
        this.prefs.putBoolean("tol.showFilter", this.showFilter);
        this.prefs.put("tol.filter", this.filter);
        this.prefs.putInt("tol.serverPortUDP", this.serverPortUDP);
        this.prefs.putInt("tol.serverPortTCP", this.serverPortTCP);
    }

    public void putInSystemTray() {
        if (SystemTray.isSupported()) {
            // On récupère l'instance du SystemTray
            SystemTray systemTray = SystemTray.getSystemTray();

            if (systemTray.getTrayIcons().length == 0) {
                // on est pas déjà présent dans le system tray

                // icon
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image image = tk.getImage(getClass().getResource("/images/" + ICON_48_48));

                // menu
                PopupMenu popup = new PopupMenu();

                MenuItem itemShow = new MenuItem(this.strings.getString("Main.show") + " " + NAME);
                itemShow.addActionListener(new ActionShow(this));
                popup.add(itemShow);

                MenuItem itemQuit = new MenuItem(this.strings.getString("quit"));
                itemQuit.addActionListener(new ActionQuit(this));
                popup.add(itemQuit);

                this.trayIcon = new TrayIcon(image, NAME, popup);
                this.trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Main.this.getWindow().setVisible(true);
                    }
                });
                // On active le redimensionnement automatique de
                // l'icône, afin qu'elle s'adapte au système
                // (sinon l'icône peut être tronqué ou disproportionné)

                this.trayIcon.setImageAutoSize(true);
                try {
                    // Et on ajoute notre TrayIcon dans le system tray
                    systemTray.add(this.trayIcon);
                } catch (AWTException ex) {
                    Logger.getLogger(NAME).logp(Level.WARNING, "Main", "putInSystemTray", "systemTray", ex);
                }
            }
        }
    }

    public void removeFromSystemTray() {
        if (SystemTray.isSupported()) {// On récupère l'instance du SystemTray
            SystemTray systemTray = SystemTray.getSystemTray();

            // Et on ajoute notre TrayIcon dans le system tray
            systemTray.remove(this.trayIcon);
        }
    }

    public void updateModelUserFiltered() {
        this.modelUserFiltered = new ModelUserFiltered(this.filter, this.modelUser.getUsers());
        if (this.isShowFilter()) {
            this.window.getLstUsers().setModel(this.modelUserFiltered);
        } else {
            this.window.getLstUsers().setModel(this.modelUser);
        }
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    // getters et setters /////////////////////////////////////////////////////
    public ModelUser getModelUser() {
        return this.modelUser;
    }

    public ModelUserFiltered getModelUserFiltered() {
        return this.modelUserFiltered;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public ModelSending getModelSending() {
        return this.modelSending;
    }

    public void setStrings(ResourceBundle strings) {
        this.strings = strings;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Window getWindow() {
        return this.window;
    }

    public String getDestinationDirectoryPath() {
        return this.destinationDirectoryPath;
    }

    public String getSourceDirectoryPath() {
        return this.sourceDirectoryPath;
    }

    public ResourceBundle getStrings() {
        return this.strings;
    }

    public String getAppearence() {
        return this.appearence;
    }

    public void setAppearence(String apparence) {
        this.appearence = apparence;
    }

    public void setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        String name = userName.replaceAll(";", ","); // characters ";" are replaced by ","        
        this.userName = name.substring(0, Math.min(30, name.length())); // the length is limited to 30 characters
    }

    public IPAddress getIPAddress() {
        return ipAddress;
    }

    public void setIPAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public MulticastSocket getSocket() {
        return this.socketMulticast;
    }

    public InetAddress getGroup() {
        return this.IPGroup;
    }

    public void setSourceDirectoryPath(String sourceDirectoryPath) {
        this.sourceDirectoryPath = sourceDirectoryPath;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public Handler getFileHandler() {
        return this.fileHandler;
    }

    public File getTmpDirectory() {
        return this.tmpDirectory;
    }

    public boolean isPresenceInUserList() {
        return this.presenceInUserList;
    }

    public void setPresenceInUserList(boolean value) {
        this.presenceInUserList = value;
    }

    public File getWorkingDirectory() {
        return this.workingDirectory;
    }

    public String getUserId() {
        return this.userId;
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public String AvatarFilePath() {
        return this.avatarFilePath;
    }

    public String getSoundFilePath() {
        return this.soundFilePath;
    }

    public void setSoundFilePath(String soundFilePath) {
        this.soundFilePath = soundFilePath;
    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public ImageIcon getDefaultIconAvatar() {
        return this.defaultIconAvatar;
    }

    public boolean isAvatarOn() {
        return this.avatarOn;
    }

    public void setAvatarOn(boolean value) {
        this.avatarOn = value;
    }

    public int getPortTCP() {
        return this.portTCP;
    }

    public String getMachineName() {
        return this.machineName;
    }

    public boolean isSoundOn() {
        return this.soundOn;
    }

    public void setSoundOn(boolean value) {
        this.soundOn = value;
    }

    public boolean isAcceptanceAutoOn() {
        return this.acceptanceAuto;
    }

    public void setAcceptationAuto(boolean acceptationAuto) {
        this.acceptanceAuto = acceptationAuto;
    }

    public ModelReception getModelReception() {
        return this.modelReception;
    }

    public boolean isShowHiddenFiles() {
        return this.showHiddenFiles;
    }

    public void setShowHiddenFiles(boolean value) {
        this.showHiddenFiles = value;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isSystemTray() {
        return this.useSystemTray;
    }

    public void setSystemTray(boolean systemTray) {
        this.useSystemTray = systemTray;
    }

    public boolean isShowTabAbout() {
        return showTabAbout;
    }

    public void setShowTabAbout(boolean value) {
        this.showTabAbout = value;
    }

    public boolean isShowTabSendings() {
        return showTabSendings;
    }

    public void setShowTabSendings(boolean value) {
        this.showTabSendings = value;
    }

    public boolean isShowTabReceptions() {
        return showTabReceptions;
    }

    public void setShowTabReceptions(boolean value) {
        this.showTabReceptions = value;
    }

    public boolean isShowTabHelp() {
        return showTabHelp;
    }

    public void setShowTabHelp(boolean value) {
        this.showTabHelp = value;
    }

    public boolean isShowFilter() {
        return showFilter;
    }

    public void setShowFilter(boolean value) {
        this.showFilter = value;
    }

    public int getServerPortUDP() {
        return serverPortUDP;
    }

    public void setServerPortUDP(int serverPortUDP) {
        this.serverPortUDP = serverPortUDP;
    }

    public int getServerPortTCP() {
        return serverPortTCP;
    }

    public void setServerPortTCP(int serverPortTCP) {
        this.serverPortTCP = serverPortTCP;
    }
}
