package com.mycompany.xy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RoomDAO {

    public static boolean save(Room r) {
        try (Connection conn = DBConnection.getConnection()) {
            String checksql = "SELECT COUNT(*) FROM room WHERE RoomNumber = ? AND status = 'Occupied'";
            try (PreparedStatement stmt = conn.prepareStatement(checksql)) {
                stmt.setString(1, r.getRoomnumber());
                ResultSet rs = stmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "This room is already occupied!\nChoose another room.");
                    return false;
                }
                String sql = "INSERT INTO room (RoomNumber, RoomType, Price, Status, GuestID) "
                        + "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement sstmt = conn.prepareStatement(sql);
                sstmt.setString(1, r.getRoomnumber());
                sstmt.setString(2, r.getRoomType());
                sstmt.setDouble(3, r.getPrice());
                sstmt.setString(4, "Occupied");   //
                sstmt.setInt(5, r.getGuestID());

                sstmt.executeUpdate();
                return true;
            }

            
        } catch (SQLException ex) {
            
            JOptionPane.showMessageDialog(null, "Error saving room: " + ex.getMessage());
            return false;

        }
    }

    public static List<Room> getAll() {
        List<Room> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM room ORDER BY RoomID DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomID(rs.getInt("RoomID"));
                    r.setRoomType(rs.getString("RoomType"));
                    r.setRoomnumber(rs.getString("RoomNumber"));
                    r.setPrice(rs.getDouble("Price"));
                    r.setStatus(rs.getString("Status"));
                    r.setGuestID(rs.getInt("GuestID"));
                    list.add(r);
                }

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching rooms: " + ex.getMessage());
        }

        return list;
    }

    public static Room getByID(int id) {
        Room room = null;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM room WHERE RoomID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        room = new Room();
                        room.setRoomID(rs.getInt("RoomID"));
                        room.setRoomType(rs.getString("RoomType"));
                        room.setRoomnumber(rs.getString("RoomNumber"));
                        room.setPrice(rs.getDouble("Price"));
                        room.setStatus(rs.getString("Status"));
                        room.setGuestID(rs.getInt("GuestID"));
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error finding room: " + ex.getMessage());
        }

        return room;
    }

    public static boolean roomOccupied(String roomnum) {
    try (Connection conn = DBConnection.getConnection()) {
        String checksql = "SELECT COUNT(*) FROM room WHERE RoomNumber = ? AND Status = 'Occupied'";
        PreparedStatement ps = conn.prepareStatement(checksql);
        ps.setString(1, roomnum);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;

    } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Cannot delete this room.\nThey still have active reservations.");
        JOptionPane.showMessageDialog(null, "Error deleting room: " + ex.getMessage());
        return false;
    }
}

    public static void freeRoomByGuest(Connection conn, int guestID) {
        try {

            String sql = "UPDATE room SET guestid = NULL, status = 'Available' WHERE guestid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, guestID);

            int updated = ps.executeUpdate();

            System.out.println("Rooms freed: " + updated);

        } catch (SQLException e) {
        }
    }

    public static Room getRoomByGuestID(int guestID) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM room WHERE guestID = ?");
            ps.setInt(1, guestID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room r = new Room();
                r.setRoomID(rs.getInt("roomid"));
                r.setRoomnumber(rs.getString("roomnumber"));
                r.setRoomType(rs.getString("roomtype"));
                r.setRoomnumber(rs.getString("RoomNumber"));
                r.setPrice(rs.getDouble("price"));
                r.setStatus(rs.getString("status"));
                r.setGuestID(rs.getInt("GuestID"));
                return r;
            }
        } catch (SQLException ex) {
        }
        return null;
    }
}
