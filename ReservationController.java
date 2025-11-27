package com.mycompany.xy;

import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;

public class ReservationController {

    ReservationView rv;
    int selectedGuestID = -1;

    public ReservationController(ReservationView view) {
        this.rv = view;
        rv.allListeners(new AllActions());
        loadGuestTable();
        addRowSelection();

    }

    private void loadGuestTable() {
        List<Guest> list = GuestDAO.getAll();
        DefaultTableModel model = (DefaultTableModel) rv.jTable1.getModel();
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

    private void addRowSelection() {
        rv.jTable1.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int row = rv.jTable1.getSelectedRow();
            if (row >= 0) {
                selectedGuestID = Integer.parseInt(rv.jTable1.getValueAt(row, 0).toString());
            }
        });
    }

    class AllActions implements ActionListener {

        @Override

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == rv.cancelBTN) {

                int row = rv.jTable1.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(null, "Select a reservation first!");
                    return;
                }

                int reservationID = (int) rv.jTable1.getValueAt(row, 0);

                ReservationDAO.cancel(reservationID);

                loadGuestTable();
                return;
            }

            if (e.getSource() == rv.nextBTN) {
                rv.setVisible(false);
                PaymentView pv = new PaymentView();
                new PaymentController(pv);
                pv.setVisible(true);
                return;
            }
            if (e.getSource() == rv.backBTN) {
                rv.setVisible(false);
                RoomView rview = new RoomView();
                new RoomController(rview);
                rview.setVisible(true);
                return;

            }

            try {
                if (selectedGuestID == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a guest first.");
                    return;
                }
                Room room = RoomDAO.getRoomByGuestID(selectedGuestID);

                if (room == null) {
                    JOptionPane.showMessageDialog(null,
                            "This guest does not have a room assigned.\n"
                            + "Assign a room first in Room Management.");
                    return;
                }

                int roomID = room.getRoomID();

                java.util.Date checkIn = rv.jDateChooser1.getDate();
                java.util.Date checkOut = rv.jDateChooser2.getDate();

                if (checkIn == null || checkOut == null) {
                    JOptionPane.showMessageDialog(null, "Please choose both dates.");
                    return;
                }

                Reservation r = new Reservation();
                r.setGuestID(selectedGuestID);
                r.setRoomID(roomID);
                r.setCheckIn(checkIn);
                r.setCheckOut(checkOut);
                System.out.println("Reservation Added");
                System.out.println("Room Number: " + rv.jDateChooser1.getDate());
                System.out.println("Room Type: " + rv.jDateChooser2.getDate());
                System.out.println("=====================");

                ReservationDAO.insert(r);

                JOptionPane.showMessageDialog(null, "Reservation Successful!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }

        }

    }
}
