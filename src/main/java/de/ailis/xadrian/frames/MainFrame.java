/*
 * $Id$
 * Copyright (C) 2010 Klaus Reimer <k@ailis.de>
 * See LICENSE.TXT for licensing information
 */

package de.ailis.xadrian.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import de.ailis.xadrian.actions.AboutAction;
import de.ailis.xadrian.actions.AddFactoryAction;
import de.ailis.xadrian.actions.ChangePricesAction;
import de.ailis.xadrian.actions.ChangeSectorAction;
import de.ailis.xadrian.actions.ChangeSunsAction;
import de.ailis.xadrian.actions.CloseAction;
import de.ailis.xadrian.actions.CloseAllAction;
import de.ailis.xadrian.actions.CopyAction;
import de.ailis.xadrian.actions.ExitAction;
import de.ailis.xadrian.actions.NewAction;
import de.ailis.xadrian.actions.OpenAction;
import de.ailis.xadrian.actions.PreferencesAction;
import de.ailis.xadrian.actions.PrintAction;
import de.ailis.xadrian.actions.SaveAction;
import de.ailis.xadrian.actions.SaveAllAction;
import de.ailis.xadrian.actions.SaveAsAction;
import de.ailis.xadrian.actions.SelectAllAction;
import de.ailis.xadrian.actions.ToggleBaseComplexAction;
import de.ailis.xadrian.components.ComplexEditor;
import de.ailis.xadrian.data.Complex;
import de.ailis.xadrian.data.Sector;
import de.ailis.xadrian.dialogs.AboutDialog;
import de.ailis.xadrian.dialogs.PreferencesDialog;
import de.ailis.xadrian.interfaces.ClipboardProvider;
import de.ailis.xadrian.interfaces.ComplexProvider;
import de.ailis.xadrian.interfaces.SectorProvider;
import de.ailis.xadrian.listeners.ClipboardStateListener;
import de.ailis.xadrian.listeners.EditorStateListener;
import de.ailis.xadrian.listeners.MainStateListener;
import de.ailis.xadrian.listeners.StateListener;
import de.ailis.xadrian.resources.Images;
import de.ailis.xadrian.support.Config;
import de.ailis.xadrian.support.I18N;
import de.ailis.xadrian.support.ModalDialog.Result;


/**
 * The main frame.
 * 
 * @author Klaus Reimer (k@ailis.de)
 * @version $Revision$
 */

public class MainFrame extends JFrame implements EditorStateListener,
    ChangeListener, ClipboardProvider, ClipboardStateListener,
    ComplexProvider, SectorProvider, StateListener
{
    /** Serial version UID */
    private static final long serialVersionUID = 7989554637240491666L;

    /** The event listener list */
    private final EventListenerList listenerList = new EventListenerList();

    /** The "exit" action */
    private final Action exitAction = new ExitAction(this);

    /** The "new" action */
    private final Action newAction = new NewAction(this);

    /** The "open" action */
    private final Action openAction = new OpenAction(this);

    /** The "close" action */
    private final Action closeAction = new CloseAction(this);

    /** The "close all" action */
    private final Action closeAllAction = new CloseAllAction(this);

    /** The "save" action */
    private final Action saveAction = new SaveAction(this);

    /** The "save as" action */
    private final Action saveAsAction = new SaveAsAction(this);

    /** The "save all" action */
    private final Action saveAllAction = new SaveAllAction(this);

    /** The "print" action */
    private final Action printAction = new PrintAction(this);

    /** The "about" action */
    private final Action aboutAction = new AboutAction(this);

    /** The "preferences" action */
    private final Action preferencesAction = new PreferencesAction(this);

    /** The "addFactory" action */
    private final Action addFactoryAction = new AddFactoryAction(this);

    /** The "changeSuns" action */
    private final Action changeSunsAction = new ChangeSunsAction(this);

    /** The "changeSector" action */
    private final Action changeSectorAction = new ChangeSectorAction(this, "complex");

    /** The "changePrices" action */
    private final Action changePricesAction = new ChangePricesAction(this);

    /** The "select all" action */
    private final Action selectAllAction = new SelectAllAction(this);

    /** The "toggleBaseComplex" action */
    private final Action toggleBaseComplexAction =
        new ToggleBaseComplexAction(this);

    /** The about dialog */
    private final AboutDialog aboutDialog = new AboutDialog();

    /** The tabbed pane */
    private JTabbedPane tabs;


    /**
     * Constructor
     */

    public MainFrame()
    {
        setTitle(I18N.getString("title"));
        setName("mainFrame");
        setIconImages(Images.LOGOS);

        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                exit();
            }
        });
        setLocationRelativeTo(null);

        createMenuBar();
        createToolBar();
        createContent();
        createStatusBar();

        createComplexTab();

        pack();

        Config.restoreWindowState(this);

        this.tabs.requestFocus();
    }


    /**
     * Creates the menu.
     */

    private void createMenuBar()
    {
        // Create the menu bar
        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Create the 'File' menu
        final JMenu fileMenu = I18N.createMenu(menuBar, "file");
        fileMenu.add(this.newAction);
        fileMenu.add(this.openAction);
        fileMenu.addSeparator();
        fileMenu.add(this.closeAction);
        fileMenu.add(this.closeAllAction);
        fileMenu.addSeparator();
        fileMenu.add(this.saveAction);
        fileMenu.add(this.saveAsAction);
        fileMenu.add(this.saveAllAction);
        fileMenu.addSeparator();
        fileMenu.add(this.printAction);
        fileMenu.addSeparator();
        fileMenu.add(this.exitAction);

        // Create the 'Edit' menu
        final JMenu editMenu = I18N.createMenu(menuBar, "edit");
        editMenu.add(new CopyAction(this));
        editMenu.add(this.selectAllAction);
        editMenu.addSeparator();
        editMenu.add(this.preferencesAction);

        // Create the 'Complex' menu
        final JMenu complexMenu = I18N.createMenu(menuBar, "complex");
        complexMenu.add(this.addFactoryAction);
        complexMenu.add(this.changeSectorAction);
        complexMenu.add(this.changeSunsAction);
        complexMenu.add(this.changePricesAction);
        complexMenu.add(new JCheckBoxMenuItem(this.toggleBaseComplexAction));

        // Create the 'Help' menu
        final JMenu helpMenu = I18N.createMenu(menuBar, "help");
        helpMenu.add(this.aboutAction);
    }


    /**
     * Creates the tool bar.
     */

    private void createToolBar()
    {
        final JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(this.exitAction);
        toolBar.addSeparator();
        toolBar.add(this.newAction);
        toolBar.add(this.openAction);
        toolBar.add(this.closeAction);
        toolBar.add(this.saveAction);
        toolBar.add(this.printAction);
        toolBar.addSeparator();
        toolBar.add(this.addFactoryAction);
        toolBar.add(this.changeSectorAction);
        toolBar.add(this.changeSunsAction);
        toolBar.add(this.changePricesAction);
        final JToggleButton btn =
            new JToggleButton(this.toggleBaseComplexAction);
        btn.setHideActionText(true);
        toolBar.add(btn);
        add(toolBar, BorderLayout.NORTH);
    }


    /**
     * Creates the content.
     */

    private void createContent()
    {
        this.tabs = new JTabbedPane();
        add(this.tabs, BorderLayout.CENTER);
        this.tabs.setPreferredSize(new Dimension(640, 480));
        this.tabs.addChangeListener(this);
    }


    /**
     * Creates the status bar.
     */

    private void createStatusBar()
    {
        final JLabel statusBar = new JLabel(" ");
        statusBar.setBorder(new EmptyBorder(2, 5, 2, 5));
        add(statusBar, BorderLayout.SOUTH);
    }


    /**
     * Adds a state listener.
     * 
     * @param listener
     *            The state listener to add
     */

    public void addStateListener(final MainStateListener listener)
    {
        this.listenerList.add(MainStateListener.class, listener);
    }


    /**
     * Removes a state listener.
     * 
     * @param listener
     *            The state listener to remove
     */

    public void removeStateListener(final MainStateListener listener)
    {
        this.listenerList.remove(MainStateListener.class, listener);
    }


    /**
     * Fire the state change event.
     */

    private void fireChange()
    {
        final Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == MainStateListener.class)
                ((MainStateListener) listeners[i + 1]).mainStateChanged(this);
        changeState();
        fireClipboardState();
    }


    /**
     * Creates a new factory complex tab
     */

    public void createComplexTab()
    {
        createComplexTab(new ComplexEditor(new Complex()));
    }


    /**
     * Creates a new factory complex tab with the specified complex editor in
     * it.
     * 
     * @param editor
     *            The complex editor
     */

    private void createComplexTab(final ComplexEditor editor)
    {
        this.tabs.addTab(editor.getComplex().getName(), editor);
        this.tabs.setSelectedComponent(editor);
        editor.addStateListener((EditorStateListener) this);
        editor.addStateListener((StateListener) this);
        editor.addClipboardStateListener(this);
        this.fireChange();
    }


    /**
     * Creates a complex editor tab with a loaded complex.
     * 
     * @param editor
     *            The complex editor
     */

    public void createLoadedComplexTab(final ComplexEditor editor)
    {
        // If current tab is new then close this one in favor of the new one
        final Component current = getCurrentTab();
        if (current != null && current instanceof ComplexEditor
            && ((ComplexEditor) current).isNew()) closeCurrentTab();

        createComplexTab(editor);
    }


    /**
     * Returns the current tab component or null if no tab is currently present.
     * 
     * @return The current tab component
     */

    public Component getCurrentTab()
    {
        return this.tabs == null ? null : this.tabs.getSelectedComponent();
    }


    /**
     * Closes the current tab. Prompts for saving unsaved changes before
     * closing. Returns true if the tab was closed or false if it was not
     * closed.
     * 
     * @return True if tab was closed, false if not
     */

    public boolean closeCurrentTab()
    {
        final Component current = getCurrentTab();
        if (current != null)
        {
            final ComplexEditor editor = (ComplexEditor) current;
            if (editor.isChanged())
            {
                final int answer =
                    JOptionPane.showConfirmDialog(null, String.format(I18N
                        .getString("confirm.saveChanges"), editor.getComplex()
                        .getName()), I18N.getTitle("confirm.saveChanges"),
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (answer == JOptionPane.CLOSED_OPTION) return false;
                if (answer == JOptionPane.CANCEL_OPTION) return false;
                if (answer == JOptionPane.YES_OPTION)
                {
                    editor.save();
                    if (editor.isChanged()) return false;
                }
            }

            this.tabs.remove(current);
            this.fireChange();
            return true;
        }
        return false;
    }


    /**
     * Closes all open tabs. Prompts for unsaved changes. Returns true if all
     * tabs have been closed or false if at least one tab was not closed.
     * 
     * @return True if all tabs were closed, false if not.
     */

    public boolean closeAllTabs()
    {
        while (this.tabs.getComponentCount() > 0)
            if (!closeCurrentTab()) return false;
        this.fireChange();
        return true;
    }


    /**
     * Shows the about dialog.
     */

    public void about()
    {
        this.aboutDialog.open();
    }


    /**
     * Shows the preferences dialog.
     */

    public void preferences()
    {
        if (PreferencesDialog.getInstance().open() == Result.OK)
        {
            for (final Component component: getTabs().getComponents())
            {
                if (component instanceof ComplexEditor)
                {
                    final ComplexEditor editor = (ComplexEditor) component;
                    editor.updateBaseComplex();
                }
            }
        }
    }


    /**
     * Returns the tabs.
     * 
     * @return The tabs
     */

    public JTabbedPane getTabs()
    {
        return this.tabs;
    }


    /**
     * @see de.ailis.xadrian.listeners.EditorStateListener#editorStateChanged(de.ailis.xadrian.components.ComplexEditor)
     */

    @Override
    public void editorStateChanged(final ComplexEditor sender)
    {
        final int index = this.tabs.indexOfComponent(sender);
        this.tabs.setTitleAt(index, sender.getComplex().getName()
            + (sender.isChanged() ? "*" : ""));
        fireChange();
    }


    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */

    @Override
    public void stateChanged(final ChangeEvent e)
    {
        fireChange();
    }


    /**
     * Exits the application. Prompts for saving unsaved changes before that.
     */

    public void exit()
    {
        if (closeAllTabs())
        {
            Config.saveWindowState(this);
            Config.getInstance().save();
            System.exit(0);
        }
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#canCopy()
     */

    @Override
    public boolean canCopy()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ClipboardProvider)) return false;
        return ((ClipboardProvider) tab).canCopy();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#canCut()
     */

    @Override
    public boolean canCut()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ClipboardProvider)) return false;
        return ((ClipboardProvider) tab).canCut();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#canPaste()
     */

    @Override
    public boolean canPaste()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ClipboardProvider)) return false;
        return ((ClipboardProvider) tab).canPaste();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#copy()
     */

    @Override
    public void copy()
    {
        ((ClipboardProvider) getCurrentTab()).copy();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#cut()
     */

    @Override
    public void cut()
    {
        ((ClipboardProvider) getCurrentTab()).cut();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#paste()
     */

    @Override
    public void paste()
    {
        ((ClipboardProvider) getCurrentTab()).paste();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#addClipboardStateListener(de.ailis.xadrian.listeners.ClipboardStateListener)
     */

    @Override
    public void addClipboardStateListener(final ClipboardStateListener listener)
    {
        this.listenerList.add(ClipboardStateListener.class, listener);
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#removeClipboardStateListener(de.ailis.xadrian.listeners.ClipboardStateListener)
     */

    @Override
    public void removeClipboardStateListener(
        final ClipboardStateListener listener)
    {
        this.listenerList.remove(ClipboardStateListener.class, listener);
    }


    /**
     * Fire the clipboard state event.
     */

    private void fireClipboardState()
    {
        final Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ClipboardStateListener.class)
                ((ClipboardStateListener) listeners[i + 1])
                    .clipboardStateChanged(this);
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#selectAll()
     */

    @Override
    public void selectAll()
    {
        ((ClipboardProvider) getCurrentTab()).selectAll();
    }


    /**
     * @see de.ailis.xadrian.listeners.ClipboardStateListener#clipboardStateChanged(de.ailis.xadrian.interfaces.ClipboardProvider)
     */

    @Override
    public void clipboardStateChanged(final ClipboardProvider provider)
    {
        fireClipboardState();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ClipboardProvider#canSelectAll()
     */

    @Override
    public boolean canSelectAll()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ClipboardProvider)) return false;
        return ((ClipboardProvider) tab).canSelectAll();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#addFactory()
     */

    @Override
    public void addFactory()
    {
        ((ComplexProvider) getCurrentTab()).addFactory();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#canAddFactory()
     */

    @Override
    public boolean canAddFactory()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).canAddFactory();
    }

    
    /**
     * @see de.ailis.xadrian.listeners.StateListener#stateChanged()
     */
    
    @Override
    public void stateChanged()
    {
        changeState();
    }

    
    /**
     * Fire the state change event.
     */

    private void changeState()
    {
        final Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == StateListener.class)
                ((StateListener) listeners[i + 1])
                    .stateChanged();
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#canChangeSuns()
     */
    
    @Override
    public boolean canChangeSuns()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).canChangeSuns();
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#canToggleBaseComplex()
     */
    
    @Override
    public boolean canToggleBaseComplex()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).canToggleBaseComplex();
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#changeSuns()
     */
    
    @Override
    public void changeSuns()
    {
        ((ComplexProvider) getCurrentTab()).changeSuns();
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#toggleBaseComplex()
     */
    
    @Override
    public void toggleBaseComplex()
    {
        ((ComplexProvider) getCurrentTab()).toggleBaseComplex();
    }


    
    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#isAddBaseComplex()
     */
    
    @Override
    public boolean isAddBaseComplex()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).isAddBaseComplex();
    }


    /**
     * @see ComplexProvider#canChangeSector()
     */
    
    @Override
    public boolean canChangeSector()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).canChangeSector();
    }


    /**
     * @see ComplexProvider#changeSector()
     */
    
    @Override
    public void changeSector()
    {
        ((ComplexProvider) getCurrentTab()).changeSector();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#canChangePrices()
     */
    
    @Override
    public boolean canChangePrices()
    {
        final Component tab = getCurrentTab();
        if (tab == null) return false;
        if (!(tab instanceof ComplexProvider)) return false;
        return ((ComplexProvider) tab).canChangePrices();
    }


    /**
     * @see de.ailis.xadrian.interfaces.ComplexProvider#changePrices()
     */
    
    @Override
    public void changePrices()
    {
        ((ComplexProvider) getCurrentTab()).changePrices();
    }


    
    /**
     * @see de.ailis.xadrian.interfaces.StateProvider#addStateListener(de.ailis.xadrian.listeners.StateListener)
     */
    
    @Override
    public void addStateListener(final StateListener listener)
    {
        this.listenerList.add(StateListener.class, listener);
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.StateProvider#removeStateListener(de.ailis.xadrian.listeners.StateListener)
     */
    
    @Override
    public void removeStateListener(final StateListener listener)
    {
        this.listenerList.remove(StateListener.class, listener);
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.SectorProvider#getSector()
     */
    
    @Override
    public Sector getSector()
    {
        return ((SectorProvider) getCurrentTab()).getSector();
    }

    
    /**
     * @see de.ailis.xadrian.interfaces.SectorProvider#setSector(de.ailis.xadrian.data.Sector)
     */
    
    @Override
    public void setSector(final Sector sector)
    {
        ((SectorProvider) getCurrentTab()).setSector(sector);
    }
}
