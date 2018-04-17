package com.whodesire.manager;

import com.whodesire.config.ResourceSetting;
import com.whodesire.config.ResourceViewer;
import com.whodesire.config.SpringAppConfig;
import com.whodesire.data.Resource;
import com.whodesire.first.PasswordVerifier;
import com.whodesire.util.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PasswordManager extends JFrame {
    private static final long serialVersionUID = -674638076054759249L;

    private static ResourceViewer resourceViewer;

    private static PasswordManager passwordManager;
    private static boolean illustrating;
    private final Object[] tableColumns = new Object[]{"Mark", "Resource Name or URL", "Description",
            "Expire in Days"};
    private static JTable table;
    private static DefaultTableModel model;
    private static JScrollPane pane;
    private OneMethod env;
    private JButton searchBtn, addBtn, flashBtn, updateBtn, deleteBtn, settingBtn, aboutBtn;
    private JTextField searchField;

    final private static Color sapBlueLight = new Color(223, 235, 245);
    final private static Color sapBlueDeep = new Color(212, 223, 239);

    private PasswordManager() {
        init();
    }

    public synchronized static PasswordManager getPasswordManager() {
        if (passwordManager == null)
            passwordManager = new PasswordManager();
        return passwordManager;
    }

    public static boolean isIllustrating() {
        return illustrating;
    }

    public static void setIllustrating(final boolean illustrating) {

        if (passwordManager == null)
            passwordManager = getPasswordManager();

        PasswordManager.illustrating = illustrating;

        if (!illustrating){
            passwordManager.setVisible(false);

        }else {

            if (GetPassword.getInstance().isSessionActive()){

//                System.out.println("Password in PasswordManager is : " + new String(GetPassword.getInstance().getPassword()));
                validateTable(passwordManager.searchField.getText());
                passwordManager.setVisible(true);

            }else {

                if(PasswordManager.resourceViewer != null){
                    PasswordManager.resourceViewer.closeResourceViewer();
                }

                if(ResourceSetting.isIllustrating()){
                    ResourceSetting.closeResourceSettingStatically();
                }

                try{
                    Thread.sleep(100);
                }catch(InterruptedException iex){ }

                PasswordManager.illustrating = false;
                passwordManager.setVisible(false);

                if(!PasswordVerifier.isIllustrating()){
                    try (AbstractApplicationContext context =
                                 new AnnotationConfigApplicationContext(SpringAppConfig.class)) {
                        PasswordVerifier verifier = context.getBean(PasswordVerifier.class);
                        verifier.setAlwaysOnTop(true);
                    }
                }

            }

        }
    }

    private void init() {

        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle("Password Manager");
        setLayout(null);
        getContentPane().setBackground(sapBlueLight);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                OneMethod.hidePasswordManager();
                GetPassword.getInstance().setSessionActive(false);
            }
        });

        URL iconURL = getClass().getResource("/icons/add-user-32.png");
        setIconImage(new ImageIcon(iconURL).getImage());

        Font font = new Font("Dialog", Font.BOLD, 18);

        env = OneMethod.getOneMethod();

        // Simple Placed for listening shortcut and focusing search field
        searchBtn = new JButton("Search");
        searchBtn.setBounds(-150, 10, 100, 39);
        searchBtn.setFocusPainted(false);
        add(searchBtn);

        addBtn = new JButton("Add Resource");
        addBtn.setBounds(15, 10, 150, 39);
        addBtn.setFocusPainted(false);
        add(addBtn);

        searchField = new JTextField();
        searchField.setFont(font);
        searchField.setBounds(175, 10, env.getScreenWidth() - 190, 40);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
                    validateTable(searchField.getText());
                }else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
                    searchField.setText("");
                    validateTable("");
                }
            }
        });

        add(searchField);

        initTable();

        int width = (int) (env.getScreenWidth() - 67) / 5;

        flashBtn = new JButton("Flash Resource");
        flashBtn.setBounds(15, pane.getY() + pane.getHeight() + 10, width, 39);
        flashBtn.setFocusPainted(false);
        add(flashBtn);

        updateBtn = new JButton("Update Resource");
        updateBtn.setBounds(flashBtn.getX() + flashBtn.getWidth() + 10, flashBtn.getY(), width, 39);
        updateBtn.setFocusPainted(false);
        add(updateBtn);

        deleteBtn = new JButton("Delete Resource");
        deleteBtn.setBounds(updateBtn.getX() + updateBtn.getWidth() + 10, flashBtn.getY(), width, 39);
        deleteBtn.setFocusPainted(false);
        add(deleteBtn);

        settingBtn = new JButton("Setting");
        settingBtn.setBounds(deleteBtn.getX() + deleteBtn.getWidth() + 10, deleteBtn.getY(), width, 39);
        settingBtn.setFocusPainted(false);
        add(settingBtn);

        aboutBtn = new JButton("About");
        aboutBtn.setBounds(settingBtn.getX() + settingBtn.getWidth() + 10, settingBtn.getY(), width, 39);
        aboutBtn.setFocusPainted(false);
        add(aboutBtn);

        bindButtonsShortcutAction();

    }

    private void initTable() {

        Object[][] object = new Object[][]{};

        model = new DefaultTableModel(object, tableColumns) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (getColumnName(columnIndex)) {
                    case "Mark":
                        return Boolean.class;
                    case "Resource Name or URL":
                        return String.class;
                    case "Description":
                        return String.class;
                    case "Expire in Days":
                        return String.class;
                }
                return super.getColumnClass(columnIndex);
            };

            @Override
            public boolean isCellEditable(int row, int col) {
                // only allow to edit the checkbox value
                if (col == 0)
                    return true;
                else
                    return false;
            }
        };

        table = new JTable(model);

        TableCellRenderer rendererFromHeader = table.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setVerticalTextPosition(JLabel.CENTER);

        int[] width = new int[4];
        width[0] = 50;
        width[1] = ((env.getScreenWidth() - (33 + 50)) / 2) - 70;
        width[2] = width[1];
        width[3] = 120;

        Font font = new Font("Dialog", Font.PLAIN, 14);
        table.getTableHeader().setFont(font);

        table.setRowHeight(35);
        table.getColumnModel().getColumn(0).setPreferredWidth(width[0]);
        table.getColumnModel().getColumn(1).setPreferredWidth(width[1]);
        table.getColumnModel().getColumn(2).setPreferredWidth(width[2]);
        table.getColumnModel().getColumn(3).setPreferredWidth(width[3]);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setFont(font);

        ColorRenderer cr = new ColorRenderer();
        cr.setRenderingColor(sapBlueLight, sapBlueDeep);
        table.getColumn(table.getColumnName(1)).setCellRenderer(cr);
        table.getColumn(table.getColumnName(2)).setCellRenderer(cr);
        table.getColumn(table.getColumnName(3)).setCellRenderer(cr);

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                JTable source = (JTable) evt.getSource();
                final int tableRow = source.getSelectedRow();
                final int tableCol = source.getSelectedColumn();

//                System.out.println("listening tableRow and tableColumn is : " + tableRow + ", " + tableCol);
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (tableRow != -1 && tableCol != 0) {
                        if (source.isRowSelected(tableRow)) {
                            boolean flag = Boolean.valueOf(String.valueOf(source.getModel().getValueAt(tableRow, 0)));
                            table.setValueAt(!flag, tableRow, 0);
                        }
                    }
                }
            }
        });

        pane = new JScrollPane(table);
        pane.setBounds(15, 60, env.getScreenWidth() - 30, env.getScreenHeight() - 200);
        pane.setBorder(searchField.getBorder());
        pane.setComponentZOrder(pane.getVerticalScrollBar(), 0);
        pane.setComponentZOrder(pane.getViewport(), 1);
        pane.getVerticalScrollBar().setOpaque(false);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        add(pane);

        requestValidateTable();
    }

    private void requestValidateTable(){
        if(GetPassword.getInstance().isSessionActive())
            validateTable("");
    }

    private void bindButtonsShortcutAction() {

        Action searchAction = new AbstractAction("") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocus();
                searchField.setCaretPosition(searchField.getText().length());
            }
        };

        Action addAction = new AbstractAction("Add") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
        };

        Action flashAction = new AbstractAction("Flash") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                flashAction();
            }
        };

        Action updateAction = new AbstractAction("Update") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAction(ResourceViewer.Viewer.UPDATER);
            }
        };

        Action deleteAction = new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAction();
            }
        };

        Action settingAction = new AbstractAction("Setting") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                settingAction();
            }
        };

        Action aboutAction = new AbstractAction("About") {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutAction();
            }
        };

        searchAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        searchBtn.setAction(searchAction);

        addAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        addBtn.setAction(addAction);

        flashAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
        flashBtn.setAction(flashAction);

        updateAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
        updateBtn.setAction(updateAction);

        deleteAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
        deleteBtn.setAction(deleteAction);

        settingAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
        settingBtn.setAction(settingAction);

        aboutAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
        aboutBtn.setAction(aboutAction);

    }

    public void requestFocusOnTable() {
        try {
            searchField.requestFocus();
            searchField.setCaretPosition(0);

            Thread.sleep(20);

            if(model.getRowCount() > 0) {
                table.requestFocus();
                table.setRowSelectionInterval(0, 0);
            }

        } catch (InterruptedException evt) {
            evt.printStackTrace();
        }
    }

    private void addAction() {

        JsonParser parser = new JsonParser(GetPassword.getInstance().getPassword());
        if (parser.countResourceList() < 100){
            resourceViewer = new ResourceViewer();
            resourceViewer.prepare_init(ResourceViewer.Viewer.ADDER);
        } else{
            new MessageUtil("Can't Add more than 100 records, " +
                    "Either you delete some resources from list or" +
                    "\nor buy a commercial Password Manager to use, thanks.");
        }

    }

    private void settingAction() {

        ResourceSetting.getResourceSetting();

    }

    private void flashAction() {

        updateAction(ResourceViewer.Viewer.ILLUSTRATOR);

    }

    private void updateAction(ResourceViewer.Viewer viewer) {

        int count = 0, rowIndex = 0;
        looper: for (int i = 0; i < model.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, 0)) {
                rowIndex = i;
                count++;
                if (count > 1) {
                    new MessageUtil("Select Single Resource to Update");
                    break looper;
                }
            }
        }

        if (count == 0) {

            new MessageUtil("No Single Resource is Selected");

        }else if(count == 1){
            // Write your logic to update the selected JTable row...do something with the data...
            resourceViewer = new ResourceViewer();
            resourceViewer.setResourceName((String) table.getValueAt(rowIndex, 1));
            resourceViewer.prepare_init(viewer);
        }

    }

    private void deleteAction() {

        List<Resource> resourceList = new ArrayList<>();

        JsonParser parser = new JsonParser(GetPassword.getInstance().getPassword());

        for (int i = model.getRowCount()-1; i >= 0 ; i--) {

            if ((boolean) table.getValueAt(i, 0)) {
                Resource resource = parser.getResourceObjectByName((String)table.getValueAt(i, 1));
                if(resource != null){
                    resourceList.add(resource);
                    model.removeRow(i);
                }
            }
        }

        if(resourceList.size() == 1){
            parser.deleteObject(resourceList.get(0));
        }else if(resourceList.size() > 1){
            parser.deleteMultipleObject(resourceList);
        }

        table.revalidate();

    }

    private final void aboutAction(){
    	
        About about = new About();
        getLayeredPane().add(about);
        about.setEnabled(true);
        about.requestFocus();
        
    }

    public final static void validateTable(String searchValue) {

        JsonParser parser = new JsonParser(GetPassword.getInstance().getPassword());
        List<Resource> resourceList = parser.getResourceList();

        model.getDataVector().removeAllElements();

        Iterator<Resource> iterator = resourceList.iterator();
        while(iterator.hasNext()){

            Resource resource = iterator.next();

            if(searchValue.length() < 3){

                model.setRowCount(model.getRowCount() + 1);

                table.setValueAt(new Boolean(false), model.getRowCount() - 1, 0);
                table.setValueAt(resource.getResourceName(), model.getRowCount() - 1, 1);
                table.setValueAt(resource.getDescription(), model.getRowCount() - 1, 2);

                String expireInDays = calcRemainingDays(resource.getExpireInDays(), resource.getCreatedOn());
                table.setValueAt(expireInDays, model.getRowCount() - 1, 3);

            }else{

                if(resource.getResourceName().toUpperCase().contains(searchValue.toUpperCase())
                        || resource.getDescription().toUpperCase().contains(searchValue.toUpperCase())){
                    model.setRowCount(model.getRowCount() + 1);

                    table.setValueAt(new Boolean(false), model.getRowCount() - 1, 0);
                    table.setValueAt(resource.getResourceName(), model.getRowCount() - 1, 1);
                    table.setValueAt(resource.getDescription(), model.getRowCount() - 1, 2);

                    String expireInDays = calcRemainingDays(resource.getExpireInDays(), resource.getCreatedOn());
                    table.setValueAt(expireInDays, model.getRowCount() - 1, 3);
                }

            }

        }

        resourceList.clear();
    }

    private static String calcRemainingDays(int expireInDays, Date resourceCretedOnDate){

        if(expireInDays != 0){
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            Date currentDate = new Date();

            //Comparing dates
            long difference = Math.abs(currentDate.getTime() - resourceCretedOnDate.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            int dayDifference = Integer.parseInt(Long.toString(differenceDates));

            if((expireInDays - dayDifference) > 0)
                return String.valueOf((expireInDays - dayDifference));
            else
                return "Expired";
        }else{

            //send Infinity or Symbol as per support if expireInDays value is '0'
            String infinitySymbol = null;
            try {
                infinitySymbol =
                        new String(String.valueOf(Character.toString('\u221E')).getBytes("UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                infinitySymbol = "Infinity";
            }
            
            return " " + infinitySymbol;
        }
    }
    
    public static void main(String[] args){
    	try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
    	
    	PasswordManager.getPasswordManager().setVisible(true);
    }

}
