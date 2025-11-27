package com.mycompany.xy;

import java.awt.event.*;

public class DashboardController {

    Dashboard view;

    public DashboardController(Dashboard v) {
        view = v;
        view.allListeners(new AllActions());
    }

    class AllActions implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (e.getSource() == view.guestBTN) {
                GuestView gv = new GuestView();
                new GuestController(gv);
                gv.setVisible(true);
                view.dispose();
            }
            if (e.getSource() == view.exitBTN) {
                System.exit(0);
            }
        }
    }
}
