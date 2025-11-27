package com.mycompany.xy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class RoomController {
    RoomView rv;
    int selectedGuestID = -1;
    public RoomController(RoomView view) {
        this.rv = view;
        loadGuestTable();
        addGuestSelection();
        rv.comboBox.addActionListener(e -> {

    String type = rv.comboBox.getSelectedItem().toString();

    if (type.equals("Suite")) {
        rv.jTextField2.setText("300");
    } else if (type.equals("Single")) {
        rv.jTextField2.setText("500");
    } else if (type.equals("Double")) {
        rv.jTextField2.setText("900");
    } else {
        rv.jTextField2.setText("");
    }
});

        rv.allListeners(new AllActions());
       
    }
    
    private void loadGuestTable() {
        List<Guest> list = GuestDAO.getAll();
        DefaultTableModel model = (DefaultTableModel) rv.jTable2.getModel();
        model.setRowCount(0);
        for (Guest g : list) {
            model.addRow(new Object[]{
                g.getGuestID(),
                g.getFullname(),
                g.getAddress(),
                g.getPhoneNumber()
            });
        }
    }
    
    private void addGuestSelection() {
        rv.jTable2.getSelectionModel().addListSelectionListener(e -> {
            int row = rv.jTable2.getSelectedRow();
            if (row >= 0) {
                selectedGuestID = (int) rv.jTable2.getValueAt(row, 0);
            }
        });
    }
    class AllActions implements ActionListener {
     @Override
     public void actionPerformed(ActionEvent e) {
         
         if (e.getSource() == rv.roomBTN) {
             if (selectedGuestID == -1) {
                JOptionPane.showMessageDialog(null, "Please select a guest first!");
                return;
         }
              String roomNum = rv.jTextField3.getText();
             if(RoomDAO.roomOccupied(roomNum)) {
                 JOptionPane.showMessageDialog(null,
                        "This room is already occupied!\nChoose another room.");
                return;
            }   
            
             Room r = new Room();
            r.setRoomnumber(rv.jTextField3.getText());                      
            r.setRoomType(rv.comboBox.getSelectedItem().toString());
            r.setPrice(Double.parseDouble(rv.jTextField2.getText()));     
            r.setStatus("Occupied");
            r.setGuestID(selectedGuestID);
            RoomDAO.save(r);
            
            
            System.out.println("Room Added:");
            System.out.println("Room Number: " + rv.jTextField3.getText());
            System.out.println("Room Type: " + rv.comboBox.getSelectedItem());
            System.out.println("Room Price: " + rv.jTextField2.getText());
            System.out.println("Room Type: " + rv.comboBox1.getSelectedItem());
            System.out.println("=====================");
            JOptionPane.showMessageDialog(null, "Room assigned to guest!");
            loadGuestTable();
            return;
     }
     
        if (e.getSource() == rv.backBTN) {
                rv.setVisible(false);
            GuestView gv = new GuestView();
            new GuestController(gv);
            gv.setVisible(true);
     return;
        }
        if (e.getSource() == rv.nextBTN) {
        rv.setVisible(false);
        ReservationView rview = new ReservationView();
        new ReservationController(rview);
        rview.setVisible(true);
    }
    }
    
    }
}
