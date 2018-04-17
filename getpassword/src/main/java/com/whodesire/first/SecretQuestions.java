package com.whodesire.first;

import com.whodesire.util.JTextFieldLimit;
import com.whodesire.util.OneMethod;
import com.whodesire.util.SpringUtilities;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecretQuestions extends JDialog {
	private static final long serialVersionUID = -4724941193666283603L;
	
	private static SecretQuestions ourInstance = new SecretQuestions();
    public static SecretQuestions getInstance() {
        return ourInstance;
    }

    private Map<String, String> secretMap;
    private List<String> questionList;
    private List<JTextField> textFieldList;
    private char[] password;

    public final char[] getPassword(){
        return password;
    }

    public final void setPassword(char[] password){
        this.password = password;
    }

    private SecretQuestions() { init(); }

    private void init(){

        OneMethod env = OneMethod.getOneMethod();

        setModal(true);
        setResizable(false);
        setTitle("#Give answers at least for five or more questions");
        setType(Type.UTILITY);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setBounds(0, 0, env.getScreenWidth(), env.getScreenHeight());
        setLayout(new BorderLayout());

        JScrollPane pane = init_pane();
        add(pane, BorderLayout.CENTER);
        pane.setLocation(0, 0);

        setAlwaysOnTop(true);
        validate();

    }

    private JScrollPane init_pane(){

        OneMethod env = OneMethod.getOneMethod();

        JPanel panel = new JPanel(new SpringLayout());

        questionList = OneMethod.getFileLines("/security/questions.list");
        questionList.add("Submit Details");
        Font font = new Font("Dialog", Font.PLAIN, 15);

        JLabel lb;
        JTextField textField;
        JButton button = null;
        textFieldList = new ArrayList<>();

        for(int i = 0; i < questionList.size(); i++){

            lb = new JLabel(questionList.get(i), JLabel.TRAILING);
            lb.setFont(font);

            if(i != (questionList.size()-1)) {

                textField = new JTextField();
                textField.setDocument(new JTextFieldLimit(25));
                textField.setFont(font);
                lb.setLabelFor(textField);
                panel.add(lb);
                panel.add(textField);

                textFieldList.add(textField);

            }else{

                button = new JButton("Save Answers");
                button.setFont(font);
                button.setFocusPainted(false);
                lb.setLabelFor(button);
                panel.add(lb);
                panel.add(button);
            }
        }

        // Layout the panel.
        SpringUtilities.makeCompactGrid(panel, questionList.size(), 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad

        JScrollPane pane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setPreferredSize(new Dimension(env.getScreenWidth() - 10,
                env.getScreenHeight() - 80));

        if(button != null){
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
//                    List<String> valueList = new ArrayList<>();
                    secretMap = new HashMap<>();

                    for(int i = 0; i < textFieldList.size(); i++){
                        String value = textFieldList.get(i).getText();
                        if(value != null && value != "" && value.length() >= 4){
                            if(value.replaceAll(" ", "").length() >= 4)
                                secretMap.put(questionList.get(i), value);
                        }
                    }

                    if(secretMap.size() >= 5){

                        StringBuffer buffer = new StringBuffer();
                        Set<String> keySet = secretMap.keySet();
                        Iterator<String> itr = keySet.iterator();
                        while(itr.hasNext()){
                            String key = itr.next();
                            buffer.append("\n" + key + "=" + secretMap.get(key));
                        }

                        int reply = JOptionPane.showConfirmDialog(getInstance(),
                                "Are you satisfied with your below given " + secretMap.size() + " answers :\n"+ buffer.toString(),
                                "Confirm Dialog", JOptionPane.YES_NO_OPTION);

                        if (reply == JOptionPane.YES_OPTION) {
                            PasswordVerifier.setSecurityQuestions(secretMap);
                            closeSecretQuestions();
                        }

                    }else{
                        JOptionPane.showMessageDialog(getInstance(),
                              "You must full fill below said to accept your answers:\n\n" +
                                "- You must give answers for at least 5 questions for better security\n" +
                                 "- Answer must be 4 digits or letters long (if 4 then without spaces)");
                    }
                }
            });
        }

        return pane;
    }

    private void closeSecretQuestions() {
        setModal(false);
        setVisible(false);
        dispose();
    }

}
