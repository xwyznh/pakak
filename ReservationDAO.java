package com.mycompany.xy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ReservationDAO {

    public static void insert(Reservation r) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO reservation (guestID, roomID,  checkin, checkout, status) " + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, r.getGuestID());
            ps.setInt(2, r.getRoomID());

            ps.setDate(3, new java.sql.Date(r.getCheckIn().getTime()));
            ps.setDate(4, new java.sql.Date(r.getCheckOut().getTime()));
            ps.setString(5, "Reserved");

            ps.executeUpdate();

        } catch (SQLException e) {
        }
    }

    public static void deleteByGuestID(int guestID) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM reservation WHERE guestID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, guestID);
            ps.executeUpdate();

        } catch (Exception e) {
        }
    }

    public static List<Integer> getReservationIDsByGuest(int guestID) {
        List<Integer> ids = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT reservationID FROM reservation WHERE guestID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, guestID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("reservationID"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }
    

    public static int countByGuest(int guestID) {
        int count = 0;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM reservation WHERE guestid = ? AND status != 'COMPLETED'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, guestID);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
        }
        return count;
    }

    public static List<Reservation> getByGuestID(int guestID) {
        List<Reservation> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM reservation WHERE guestID = ? ORDER BY reservationID DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, guestID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();

                r.setReservationID(rs.getInt("reservationID"));
                r.setGuestID(rs.getInt("guestID"));
                r.setRoomID(rs.getInt("roomID"));

                r.setCheckIn(rs.getDate("checkin"));
                r.setCheckOut(rs.getDate("checkout"));
                r.setStatus(rs.getString("status"));

                list.add(r);
            }

        } catch (SQLException e) {
        }

        return list;
    }

    public static List<Reservation> getAll() {
        List<Reservation> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM reservation ORDER BY reservationid DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationID(rs.getInt("reservationid"));
                r.setGuestID(rs.getInt("guestid"));
                r.setRoomID(rs.getInt("roomid"));

                r.setCheckIn(rs.getDate("checkin"));
                r.setCheckOut(rs.getDate("checkout"));
                r.setStatus(rs.getString("status"));

                list.add(r);
            }

        } catch (SQLException e) {
        }

        return list;
    }
    

    public static void delete(int id) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM reservation WHERE reservationID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public static void updateStatus(int reservationID, String status) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE reservation SET status = ? WHERE reservationID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, status);
            stmt.setInt(2, reservationID);
            stmt.executeUpdate();
        } catch (SQLException e) {
        }
    }
    public static void cancel(int reservationID) {
    try (Connection conn = DBConnection.getConnection()) {

        // 1) Get roomID of reservation
        String getRoomSql = "SELECT roomid FROM reservation WHERE reservationid = ?";
        PreparedStatement stmt = conn.prepareStatement(getRoomSql);
        stmt.setInt(1, reservationID);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(null, "Reservation not found.");
            return; // STOP
        }

        int roomID = rs.getInt(1);

        // 2) Set reservation to Cancelled
        String cancelSql = "UPDATE reservation SET status = 'Cancelled' WHERE reservationid = ?";
        PreparedStatement cancelStmt = conn.prepareStatement(cancelSql);
        cancelStmt.setInt(1, reservationID);
        cancelStmt.executeUpdate();

        // 3) Free the room
        String freeSql = "UPDATE room SET guestid = NULL, status = 'Available' WHERE roomid = ?";
        PreparedStatement freeStmt = conn.prepareStatement(freeSql);
        freeStmt.setInt(1, roomID);
        freeStmt.executeUpdate();

        JOptionPane.showMessageDialog(null, "Reservation cancelled!");
        return;

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error cancelling reservation: " + ex.getMessage());
        return;
    }
}
    public static Reservation getByGuest(int guestID) {
    try (Connection conn = DBConnection.getConnection()) {

        String sql = "SELECT * FROM reservation WHERE guestid = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, guestID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Reservation r = new Reservation();
            r.setReservationID(rs.getInt("reservationid"));
            r.setGuestID(rs.getInt("guestid"));
            r.setRoomID(rs.getInt("roomid"));
            r.setCheckIn(rs.getDate("checkin"));
            r.setCheckOut(rs.getDate("checkout"));
            r.setStatus(rs.getString("status"));
            return r;
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    return null; // no reservation found
}


}
