/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.ta;
import view.ta.featureFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MockFeatureFrame extends featureFrame {
    public MockFeatureFrame() {
        JTable table = new JTable(new DefaultTableModel(new Object[]{"roomNumber", "startTime", "endTime", "type"}, 0));
        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"roomNumber", "startTime", "endTime", "type"}));
        this.add(table);
        this.setVisible(false);
    }

    @Override
    public JTable getReservationTable() {
        return super.getReservationTable();
    }
}

