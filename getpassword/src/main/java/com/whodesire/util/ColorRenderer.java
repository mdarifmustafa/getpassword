package com.whodesire.util;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ColorRenderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    private Color col1 = Color.BLACK, col2 = Color.WHITE;
    private static Font font = new Font("Dialog", Font.PLAIN, 14);

    public ColorRenderer(){
        setOpaque(true);
    }

    public void setRenderingColor(Color col1, Color col2){
        this.col1 = col1;
        this.col2 = col2;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){

        setBorder(BorderFactory.createEmptyBorder());
        setFont(font);

        if (value != null){
            setText(" " + value.toString());
        }

        if(isSelected){

            setBackground(table.getSelectionBackground());
            table.setRowHeight(row, 50);

        }else{

            if(table.getSelectedRow() != row) {

                if(row%2==0){
                    setBackground(getRowColor(table, value, row, column, col1));
                }else{
                    setBackground(getRowColor(table, value, row, column, col2));
                }

            }
            table.setRowHeight(row, 35);

        }
        return this;
    }

    private Color getRowColor(JTable table, Object value, int row, int column, Color defCol){

        if(column == 3 && isNumber(value)){
            if(getNumber(value) == 0)
                return Color.YELLOW;
            else if(getNumber(value) < 0)
                return Color.RED;
        }
        return defCol;

    }

    private static boolean isNumber(Object object) {
        try {
            Integer.parseInt(String.valueOf(object));
        } catch(NumberFormatException nfe) {
            return false;
        } catch(Exception ex){
            return false;
        }
        return true;
    }

    private static int getNumber(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

}
