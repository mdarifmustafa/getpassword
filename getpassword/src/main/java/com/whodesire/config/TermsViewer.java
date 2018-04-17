package com.whodesire.config;

import com.whodesire.util.OneMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class TermsViewer extends JDialog {
	private static final long serialVersionUID = -6145555331722318972L;

	private static TermsViewer termsViewer;

//    private JPanel parentPanel, childPanel;
//    private static boolean visible;
//    private JSlider slider;

//    private final static Font font = new Font("Dialog", Font.PLAIN, 15);

    private TermsViewer(){
        init();
    }

    public static TermsViewer getTermsViewer(){
        if(termsViewer == null)
            termsViewer = new TermsViewer();

        return termsViewer;
    }

    private void init(){

        OneMethod env = OneMethod.getOneMethod();

        setTitle("Terms and Conditions");
        setModal(true);
        setResizable(false);
        setType(Type.UTILITY);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds((env.getScreenWidth()/2) - 325,
                (env.getScreenHeight()/2) - 250, 650, 500);
        setLayout(new GridLayout(1,1));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    closeTermsViewer();
            }
        });

        add(getTermsPanel());

        setAlwaysOnTop(true);
        validate();
//        visible = true;
        setVisible(true);
    }

    private JScrollPane getTermsPanel(){

        List<String> terms = OneMethod.getFileLines("/mailx/terms.txt");
        StringBuffer buffer = new StringBuffer("");

        for(int i = 0; i < terms.size(); i++){
            buffer.append(terms.get(i) + "\n");
        }

        JTextArea area = new JTextArea(buffer.toString());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(new Font("Dialog", Font.BOLD, 16));
        area.setBackground(new Color(235, 235, 235));

        JScrollPane pane = new JScrollPane(area);
        return pane;
    }

    public void closeTermsViewer() {
        setModal(false);
        setVisible(false);
        dispose();
//        visible = false;
    }

}
