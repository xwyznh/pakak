package com.mycompany.xy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class GuestDAO {

    public static void save(Guest g) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO guest (fullname, Address, PhoneNumber) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, g.getFullname());
                stmt.setString(2, g.getAddress());
                stmt.setString(3, g.getPhoneNumber());

                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Guest saved!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error saving guest: " + ex.getMessage());
        }
    }

    public static int saveAndReturnId(Guest g) {
        int id = -1;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO guest (fullname, Address, PhoneNumber) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, g.getFullname());
            stmt.setString(2, g.getAddress());
            stmt.setString(3, g.getPhoneNumber());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error saving guest: " + ex.getMessage());
        }
        return id;
    }

    public static void update(Guest g) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE guest SET fullname=?, Address=?, PhoneNumber=? WHERE GuestID=?";
            try (PreparedStatement ustmt = conn.prepareStatement(sql)) {
                ustmt.setString(1, g.getFullname());
                ustmt.setString(2, g.getAddress());
                ustmt.setString(3, g.getPhoneNumber());
                ustmt.setInt(4, g.getGuestID());

                ustmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Guest updated!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating guest: " + ex.getMessage());
        }
    }
    public static void deleteGuestCascade(int guestID) {
    try (Connection conn = DBConnection.getConnection()) {

        // Delete payments
        PreparedStatement ps1 = conn.prepareStatement(
            "DELETE FROM payment WHERE ReservationID IN (SELECT ReservationID FROM reservation WHERE GuestID = ?)"
        );
        ps1.setInt(1, guestID);
        ps1.executeUpdate();

        // Delete reservations
        PreparedStatement ps2 = conn.prepareStatement(
            "DELETE FROM reservation WHERE GuestID = ?"
        );
        ps2.setInt(1, guestID);
        ps2.executeUpdate();

        // Delete rooms
        PreparedStatement ps3 = conn.prepareStatement(
            "DELETE FROM room WHERE GuestID = ?"
        );
        ps3.setInt(1, guestID);
        ps3.executeUpdate();

        // Delete guest
        PreparedStatement ps4 = conn.prepareStatement(
            "DELETE FROM guest WHERE GuestID = ?"
        );
        ps4.setInt(1, guestID);
        ps4.executeUpdate();

    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}


    public static Guest getById(int guestID) {
        Guest g = null;
        try (Connection conn = DBConnection.getConnection()) {
            String gsql = "SELECT * FROM guest WHERE guestid=?";
            try (PreparedStatement ps = conn.prepareStatement(gsql)) {
                ps.setInt(1, guestID);
                try (ResultSet sr = ps.executeQuery()) {
                    if (sr.next()) {
                        g = new Guest();
                        g.setGuestID(sr.getInt("GuestID"));
                        g.setFullname(sr.getString("FullName"));
                        g.setAddress(sr.getString("Address"));
                        g.setPhoneNumber(sr.getString("PhoneNumber"));
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching guest: " + ex.getMessage());
        }
        return g;
    }

    public static List<Guest> getAll() {
        List<Guest> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM guest ORDER BY GuestID DESC";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rst = pst.executeQuery()) {
                while (rst.next()) {
                    Guest g = new Guest();
                    g.setGuestID(rst.getInt("GuestID"));
                    g.setFullname(rst.getString("FullName"));

                    g.setAddress(rst.getString("Address"));
                    g.setPhoneNumber(rst.getString("PhoneNumber"));
                    list.add(g);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching guests: " + ex.getMessage());
        }
        return list;
    }

    public static int countAll() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM guest";
            try (PreparedStatement psest = conn.prepareStatement(sql); ResultSet rest = psest.executeQuery()) {
                if (rest.next()) {
                    int c = rest.getInt(1);
                    rest.close();
                    psest.close();
                    return c;
                }
            }
        } catch (SQLException ex) {
        }
        return 0;
    }
}
