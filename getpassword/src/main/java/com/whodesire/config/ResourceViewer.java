package com.whodesire.config;

import com.whodesire.data.Resource;
import com.whodesire.manager.GetPassword;
import com.whodesire.manager.PasswordManager;
import com.whodesire.util.JTextFieldLimit;
import com.whodesire.util.JsonParser;
import com.whodesire.util.OneMethod;
import com.whodesire.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

public class ResourceViewer extends JDialog {
	private static final long serialVersionUID = 431347165041469672L;

	public enum Viewer {
        ADDER, ILLUSTRATOR, UPDATER;
    }

    private enum ControlType {
        ADD, REMOVE, SAVE;
    }

    private String resourceName;
    private JPanel parentPanel, childPanel;
    private JTextField resrc_field, desc_field, expire_field, userid_field, userps_field;
    private JButton addMoreBtn, removeLastBtn;
    private int max_fields = 0;

    private Viewer viewer;

    private final static Font font = new Font("Dialog", Font.PLAIN, 15);

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    private Viewer getViewer(){
        return viewer;
    }

    public void prepare_init(Viewer viewer){

        OneMethod env = OneMethod.getOneMethod();

        this.viewer = viewer;

        setModal(true);
        setResizable(false);
        setType(Type.UTILITY);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds((env.getScreenWidth()/2) - 325,
                (env.getScreenHeight()/2) - 250, 650, 500);
        setLayout(new BorderLayout());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    closeResourceViewer();
            }
        });

        JPanel panel = null;

        switch (viewer){
            case ADDER: setTitle("Resource - Add");
                panel = adder_pane();    break;

            case UPDATER: setTitle("Resource - Update");
                panel = illustrator_pane(viewer);    break;

            case ILLUSTRATOR: setTitle("Resource - Display");
                panel = illustrator_pane(viewer);    break;

        }

        add(panel, BorderLayout.CENTER);
        panel.setLocation(0, 0);

        setAlwaysOnTop(true);
        validate();
        setVisible(true);
    }

    private JPanel getCommonPanel(boolean editable, Resource resource){

        String[] labels = new String[]{"Resource Name or URL",
                "Resource Description", "Expire In Days(0 - Ignore)",
                "Username or LoginId", "Login Password"};

        childPanel = new JPanel(new SpringLayout());
        childPanel.setBounds( 10, 10, 625, 200);

        KeyListener keyda = new KeyAdapter() {

            public void keyTyped(KeyEvent evt) {
                if (Character.isWhitespace(evt.getKeyChar()))
                    evt.consume();

                if(evt.getComponent() == expire_field){
                    if (!Character.isDigit(evt.getKeyChar()) && !Character.isISOControl(evt.getKeyChar()))
                        evt.consume();
                }
            }
        };

        JLabel lb;

        lb = new JLabel(labels[0], JLabel.TRAILING);
        resrc_field = new JTextField();
        resrc_field.setDocument(new JTextFieldLimit(75));
        resrc_field.setFont(font);
        resrc_field.setEditable(editable);
        lb.setFont(font);
        lb.setLabelFor(resrc_field);
        childPanel.add(lb);
        childPanel.add(resrc_field);

        lb = new JLabel(labels[1], JLabel.TRAILING);
        desc_field = new JTextField();
        desc_field.setDocument(new JTextFieldLimit(75));
        desc_field.setFont(font);
        desc_field.setEditable(editable);
        lb.setFont(font);
        lb.setLabelFor(desc_field);
        childPanel.add(lb);
        childPanel.add(desc_field);

        lb = new JLabel(labels[2], JLabel.TRAILING);
        expire_field = new JTextField();
        expire_field.setDocument(new JTextFieldLimit(3));
        expire_field.setFont(font);
        expire_field.setEditable(editable);
        lb.setFont(font);
        lb.setLabelFor(expire_field);
        childPanel.add(lb);
        childPanel.add(expire_field);
        expire_field.addKeyListener(keyda);

        lb = new JLabel(labels[3], JLabel.TRAILING);
        userid_field = new JTextField();
        userid_field.setDocument(new JTextFieldLimit(35));
        userid_field.setFont(font);
        userid_field.setEditable(editable);
        lb.setFont(font);
        lb.setLabelFor(userid_field);
        childPanel.add(lb);
        childPanel.add(userid_field);
        userid_field.addKeyListener(keyda);

        lb = new JLabel(labels[4], JLabel.TRAILING);
        userps_field = new JTextField();
        userps_field.setDocument(new JTextFieldLimit(35));
        userps_field.setFont(font);
        userps_field.setEditable(editable);
        lb.setFont(font);
        lb.setLabelFor(userps_field);
        childPanel.add(lb);
        childPanel.add(userps_field);
        userps_field.addKeyListener(keyda);

        if(resource != null){
            resrc_field.setText(resource.getResourceName());
            desc_field.setText(resource.getDescription());
            expire_field.setText(resource.getExpireInDays().toString());
            userid_field.setText(String.valueOf(resource.getSecret().get(0)));
            userps_field.setText(String.valueOf(resource.getSecret().get(1)));
        }

        // Layout the panel.
        SpringUtilities.makeCompactGrid(childPanel, labels.length, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad

        return childPanel;

    }

    private void bindControl(JButton button, ControlType controlType){

        switch(controlType){
            case ADD: {
                Action action = new AbstractAction("Add More") {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addMoreAction(null, null);
                    }
                };
                action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
                button.setAction(action);
            }   break;

            case REMOVE: {
                Action action = new AbstractAction("Remove Last") {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeLastAction();
                    }
                };
                action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
                button.setAction(action);
            }   break;

            case SAVE:  {
                Action action = new AbstractAction("Save Resource") {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveResourceAction();
                    }
                };
                action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
                button.setAction(action);
            }   break;

        }

    }

    private JPanel adder_pane(){

        parentPanel = new JPanel();
        parentPanel.setLayout(null);

        parentPanel.add(getCommonPanel(true, null));

        addMoreBtn = new JButton("Add More");
        addMoreBtn.setBounds(10, childPanel.getY()+childPanel.getHeight()+10,
                305, 30);
        addMoreBtn.setFocusPainted(false);
        addMoreBtn.setFont(font);
        parentPanel.add(addMoreBtn);

        removeLastBtn = new JButton("Remove Last");
        removeLastBtn.setBounds(addMoreBtn.getX() + addMoreBtn.getWidth() + 10,
                addMoreBtn.getY(),305, 30);
        removeLastBtn.setFocusPainted(false);
        removeLastBtn.setFont(font);
        parentPanel.add(removeLastBtn);

        JButton saveResourceBtn = new JButton("Save Resource");
        saveResourceBtn.setBounds( 10, 435, 620, 30);
        saveResourceBtn.setFocusPainted(false);
        saveResourceBtn.setFont(font);
        parentPanel.add(saveResourceBtn);

        bindControl(addMoreBtn, ControlType.ADD);
        bindControl(removeLastBtn, ControlType.REMOVE);
        bindControl(saveResourceBtn, ControlType.SAVE);

        return parentPanel;
    }

    //For flashing the data and updating the same.
    private JPanel illustrator_pane(Viewer viewer){

        parentPanel = new JPanel();
        parentPanel.setLayout(null);

        if(resourceName != null){

            JsonParser parser = new JsonParser(GetPassword.getInstance().getPassword());
            Resource resource = parser.getResourceObjectByName(resourceName);

            parentPanel.add(
                    getCommonPanel(viewer == Viewer.ILLUSTRATOR ? false: true, resource));

            addMoreBtn = new JButton("Add More");
            addMoreBtn.setBounds(10, childPanel.getY()+childPanel.getHeight()+10,
                    305, 30);
            addMoreBtn.setFocusPainted(false);
            addMoreBtn.setFont(font);
            parentPanel.add(addMoreBtn);

            removeLastBtn = new JButton("Remove Last");
            removeLastBtn.setBounds(addMoreBtn.getX() + addMoreBtn.getWidth() + 10,
                    addMoreBtn.getY(),305, 30);
            removeLastBtn.setFocusPainted(false);
            removeLastBtn.setFont(font);
            parentPanel.add(removeLastBtn);

            final JButton saveResourceBtn = new JButton("Save Resource");
            saveResourceBtn.setBounds( 10, 435, 620, 30);
            saveResourceBtn.setFocusPainted(false);
            saveResourceBtn.setFont(font);
            parentPanel.add(saveResourceBtn);

            bindControl(addMoreBtn, ControlType.ADD);
            bindControl(removeLastBtn, ControlType.REMOVE);
            bindControl(saveResourceBtn, ControlType.SAVE);

            Map<String, char[]> excessSpare = resource.getExcessSpare();
            if(excessSpare != null){
                if(excessSpare.size() > 0){
                    for ( Map.Entry<String, char[]> entry : excessSpare.entrySet() ) {
                        addMoreAction(entry.getKey(), entry.getValue());
                    }
                }
            }

            if (!(viewer == Viewer.ILLUSTRATOR ? false: true)){
                addMoreBtn.setEnabled(false);
                removeLastBtn.setEnabled(false);
                saveResourceBtn.setEnabled(false);
            }

        }

        return parentPanel;
    }

    private void addMoreAction(String resourceMapKey, char[] resourceMapValue){

        if(childPanel != null && max_fields < 4){

            JTextField keyField = new JTextField();
            JTextField valueField = new JTextField();

            keyField.setFont(font);
            valueField.setFont(font);

            keyField.setDocument(new JTextFieldLimit(75));
            valueField.setDocument(new JTextFieldLimit(75));

            keyField.setBounds(addMoreBtn.getX(),
                    childPanel.getY() + childPanel.getHeight() + 1+((max_fields*30)+(max_fields*10)),
                    addMoreBtn.getWidth(), 30);

            valueField.setBounds(removeLastBtn.getX(),
                    keyField.getY(),removeLastBtn.getWidth(), 30);

            parentPanel.add(keyField);
            parentPanel.add(valueField);

            ++max_fields;

            if(resourceMapKey != null && resourceMapValue.length > 0){
                keyField.setText(resourceMapKey);
                valueField.setText(String.valueOf(resourceMapValue));
                if(viewer == Viewer.ILLUSTRATOR){
                    keyField.setEditable(false);
                    valueField.setEditable(false);
                }
            }

            addMoreBtn.setLocation(10, keyField.getY()+keyField.getHeight()+10);
            removeLastBtn.setLocation(removeLastBtn.getX(), addMoreBtn.getY());

        }

    }

    private void removeLastAction(){

        if(childPanel != null && max_fields > 0){

            --max_fields;

            boolean flag = false;
            Point removedLocation = null;

            Component[] components = parentPanel.getComponents();
            for(Component comp : components){
                if(comp instanceof JTextField){
                    JTextField textField = (JTextField)comp;
                    if((textField.getY()+textField.getHeight()+10) == addMoreBtn.getY()){
                        flag = true;
                        removedLocation = textField.getLocation();
                        parentPanel.remove(textField);
                    }
                }
            }

            if(flag){
                addMoreBtn.setLocation(10, removedLocation.y);
                removeLastBtn.setLocation(removeLastBtn.getX(), addMoreBtn.getY());
                parentPanel.validate();
            }
        }

    }

    private void saveResourceAction(){

        if(childPanel != null){

            if (resrc_field.getText().length() > 3 &&
                    desc_field.getText().length() > 3 &&
                        expire_field.getText().length() > 0 &&
                            userid_field.getText().length() > 2 &&
                                userps_field.getText().length() >= 6){

                ArrayList<String> fieldList = new ArrayList<>();
                fieldList.add(resrc_field.getText());
                fieldList.add(desc_field.getText());
                fieldList.add(expire_field.getText());
                fieldList.add(userid_field.getText());
                fieldList.add(userps_field.getText());

                Map<Integer, String> keyList = new HashMap<>();
                Map<Integer, char[]> valueList = new HashMap<>();

                Map<String, char[]> mapFields = new HashMap<>();
                Component[] components = parentPanel.getComponents();
                for(Component comp : components){
                    if(comp instanceof JTextField){
                        JTextField textField = (JTextField)comp;
                        if(textField.getX() == addMoreBtn.getX()){
                            keyList.put(textField.getY(), textField.getText());
                        } else if(textField.getX() == removeLastBtn.getX()){
                            valueList.put(textField.getY(), textField.getText().toCharArray());
                        }
                    }
                }

                Set<Integer> keySet = keyList.keySet();
                Iterator<Integer> iterator = keySet.iterator();

                while(iterator.hasNext()){
                    int yIndex = iterator.next();
                    mapFields.put(keyList.get(yIndex), valueList.get(yIndex));
                }

                List<char[]> secret = new ArrayList<>();
                secret.add(fieldList.get(3).toCharArray());
                secret.add(fieldList.get(4).toCharArray());

                Resource resource = new Resource();
                resource.setResourceName(fieldList.get(0));
                resource.setDescription(fieldList.get(1));
                resource.setExpireInDays(Integer.parseInt(fieldList.get(2)));
                resource.setCreatedOn(new Date());
                resource.setSecret(secret);
                resource.setExcessSpare(mapFields);

                GetPassword.getInstance();

                JsonParser parser = new JsonParser(GetPassword.getInstance().getPassword());

                if (getViewer() == Viewer.ADDER) {

                    parser.addObject(resource);

                }else if (getViewer() == Viewer.UPDATER) {

                    int index = parser.getResourceObjectIndexByName(resourceName);
                    parser.updateObject(index, resource);

                }

                PasswordManager.validateTable("");
                closeResourceViewer();

            }
        }

    }

    public void closeResourceViewer() {
        setModal(false);
        setVisible(false);
        dispose();
    }

}
