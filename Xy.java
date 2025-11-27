/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.xy;

/**
 *
 * @author gamin
 */
public class Xy {

    
    public static void main(String[] args) {

        Dashboard dash = new Dashboard();
        new DashboardController(dash);   // attach controller
        dash.setVisible(true);          // show dashboard
    }
}

