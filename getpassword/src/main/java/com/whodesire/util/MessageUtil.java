package com.whodesire.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MessageUtil extends JFrame {
	private static final long serialVersionUID = -5173182983556715476L;

	public enum LEVEL {
        INFO, WARNING, CRITICAL;
    }

    final private static List<MessageUtil> messageUtilList = new ArrayList<>();

    private JLabel closeLabel;
    private JLabel messageLabel;

    private String message;
    private int alive_for_seconds, count_seconds, currentTop, nextTop;
    private boolean inaugurate;
    private LEVEL level;

    private int width;

    public MessageUtil(String message){
        this(message, 15);
    }

    public MessageUtil(String message, LEVEL level){
        this(message, 15, level);
    }

    public MessageUtil(String message, int alive_for_seconds){
        this(message, alive_for_seconds, LEVEL.INFO);
    }

    public MessageUtil(String message, int alive_for_seconds, LEVEL level){

        this.message = message;
        this.alive_for_seconds = alive_for_seconds;
        this.level = level;

        init();
    }

    private boolean hasInaugurate(){
        return inaugurate;
    }

    private void init(){

        setUndecorated(true);
        setType(Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(null);

        OneMethod env = OneMethod.getOneMethod();
        width = (env.getScreenWidth()/2);
        setBounds(width - 10 + (300), 50, width, 32);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, width, 32);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setLayout(null);
        panel.setBackground(getLevelColor());
        getContentPane().add(panel);

        closeLabel = new JLabel();
        closeLabel.setIcon(new ImageIcon(MessageUtil.class.getResource("/icons/close_button_32.png")));
        closeLabel.setBounds(0, -1, 32, 32);
        panel.add(closeLabel);
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                closeMessage();
            }
        });

        messageLabel = new JLabel(message);
        messageLabel.setBounds(34, 0, width - 34, 32);
        panel.add(messageLabel);

        setVisible(true);

        messageUtilList.add(this);

        pushAll();

        inaugurate();
    }

    private void startClosingTimer(){

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(count_seconds++ > alive_for_seconds){
                    closeMessage();
                    cancel();
                }
            }
        };

        java.util.Timer timer = new Timer("timer");
        timer.schedule(task, 0, 1000);
    }

    private void inaugurate(){

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(getLocation().x > (width - 10))
                    setLocation(getLocation().x - 10, 50);
                else {
                    startClosingTimer();
                    currentTop = 50;
                    inaugurate = true;
                    cancel();
                }
            }
        };

        java.util.Timer timer = new Timer("timer");
        timer.schedule(task, 0, 5);
    }

    private static void pushAll(){

        for(MessageUtil util : messageUtilList){
            try{
                if(util.hasInaugurate())
                    util.pushMe();
            }catch(Exception exp){}
        }

    }

    private void pushMe(){

        nextTop = currentTop+(32+10);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (currentTop < nextTop){
                    currentTop += 2;
                    setLocation(width - 10, currentTop);
                }else {
                    currentTop = nextTop;
                    cancel();
                }
            }
        };

        java.util.Timer timer = new Timer("timer");
        timer.schedule(task, 0, 10);
    }

    private void closeMessage(){

        messageUtilList.remove(this);
        setVisible(false);
        System.gc();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

    }

    private Color getLevelColor(){

        Color color = null;

        if(level == LEVEL.INFO)
            color = Color.WHITE;
        else if(level == LEVEL.WARNING)
            color = Color.YELLOW;
        else if(level == LEVEL.CRITICAL)
            color = new Color(255, 0, 0);

        return color;
    }

//    public static void main(String[] args){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                int j  = 0;
//
//                for(int i = 0; i < 25; i++){
//                    try{
//                        Thread.sleep(500);
//                    }catch(InterruptedException iex){}
//
//                    if(++j > 2)
//                        j = 0;
//
//                    LEVEL level;
//
//                    if(j == 0){
//                        level = LEVEL.INFO;
//                    }else if(j == 1){
//                        level = LEVEL.WARNING;
//                    }else{
//                        level = LEVEL.CRITICAL;
//                    }
//
//                    MessageUtil util = new MessageUtil("Hello Message" + (i+1), 15, level);
//                }
//            }
//        }).start();
//    }

}
