package com.mycompany.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PaymentController {

    PaymentView pv;
    Reservation selectedReservation = null;
    Guest selectedGuest = null;

    public PaymentController(PaymentView pv) {
        this.pv = pv;
        pv.allListeners(new AllActions());
        loadReservationTable();
        loadRoomTable();
        pv.jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!pv.jTable1.getSelectionModel().isSelectionEmpty()) {
                int row = pv.jTable1.getSelectedRow();

                int guestID = Integer.parseInt(pv.jTable1.getValueAt(row, 1).toString());
                selectedGuest = GuestDAO.getById(guestID);

                // Load this guest’s reservation into jTable2
                loadReservationTableByGuest(guestID);
            }
        });
        pv.jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!pv.jTable2.getSelectionModel().isSelectionEmpty()) {

                int row = pv.jTable2.getSelectedRow();
                selectedReservation = new Reservation();
                selectedReservation.setReservationID(
                        Integer.parseInt(pv.jTable2.getValueAt(row, 0).toString())
                );

                String outStr = pv.jTable2.getValueAt(row, 1).toString();
                String inStr = pv.jTable2.getValueAt(row, 2).toString();

                LocalDate out = LocalDate.parse(outStr);
                LocalDate in = LocalDate.parse(inStr);

                long days = ChronoUnit.DAYS.between(in, out);

                // get room type from room table (top table)
                String roomType = pv.jTable1.getValueAt(0, 3).toString();

                double price = 0;

                if (roomType.equalsIgnoreCase("Single")) {
                    price = 500;
                } else if (roomType.equalsIgnoreCase("Suite")) {
                    price = 300;
                } else if (roomType.equalsIgnoreCase("Double")) {
                    price = 900;
                }

                double total = days * price;

                pv.jTextField2.setText(String.valueOf(total));
            }
        });
    }

    private void loadRoomTable() {
        List<Room> list = RoomDAO.getAll();
        DefaultTableModel model = (DefaultTableModel) pv.jTable1.getModel();
        model.setRowCount(0);

        for (Room r : list) {
            model.addRow(new Object[]{
                r.getRoomID(),
                r.getGuestID(),
                r.getRoomnumber(),
                r.getRoomType(),
                r.getPrice(),
                r.getStatus()
            });
        }
    }

    private void loadReservationTable() {
        List<Reservation> list = ReservationDAO.getAll();
        DefaultTableModel model = (DefaultTableModel) pv.jTable2.getModel();
        model.setRowCount(0);

        for (Reservation r : list) {
            model.addRow(new Object[]{
                r.getReservationID(),
                r.getCheckIn(),
                r.getCheckOut()
            });
        }
    }

    private void loadReservationTableByGuest(int guestID) {
        List<Reservation> list = ReservationDAO.getByGuestID(guestID);
        DefaultTableModel model = (DefaultTableModel) pv.jTable2.getModel();
        model.setRowCount(0);

        for (Reservation r : list) {
            model.addRow(new Object[]{
                r.getReservationID(),
                r.getCheckIn(),
                r.getCheckOut()
            });
        }
    }

    class AllActions implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == pv.backBTN) {
                ReservationView rv = new ReservationView();
                new ReservationController(rv);
                rv.setVisible(true);
                pv.dispose();
                return;
            }

            if (e.getSource() == pv.payBTN) {

            }

            if (selectedGuest == null) {
                JOptionPane.showMessageDialog(null, "Please select a guest.");
                return;
            }

            if (pv.jTable2.getSelectionModel().isSelectionEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a reservation.");
                return;
            }
            if (pv.jTextField2.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter amount.");
                return;
            }
            if (pv.jDateChooser1.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Please Select Payment Date.");
                return;
            }

            double amount = Double.parseDouble(pv.jTextField2.getText());

            Payment payment = new Payment();
            payment.setReservationID(selectedReservation.getReservationID());
            payment.setAmount(amount);
            payment.setDate(pv.jDateChooser1.getDate());
            payment.setStatus("PAID");
            System.out.println("Payment Added:");
            System.out.println("Payment Amount: " + pv.jTextField2.getText());
            System.out.println("Room Type: " + pv.jDateChooser1.getDate());
            System.out.println("=====================");

            PaymentDAO.addPayment(payment);
            ReservationDAO.updateStatus(selectedReservation.getReservationID(), "COMPLETED");
            JOptionPane.showMessageDialog(null, "Payment Successful");
            pv.dispose();

        }
    }
}
