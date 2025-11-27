package com.mycompany.xy;

import java.util.Date;

public class Payment {
    private int reservationID;
    private int paymentID;
    private double amount;
    private  Date paymentdate;
    private String Status;

    public int getReservationID() {
        return reservationID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

   
    

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

   

    

   

    public Date getDate() {
        return paymentdate;
    }

    public void setDate(Date paymentdate  ) {
        this.paymentdate = paymentdate;
    }
    
}