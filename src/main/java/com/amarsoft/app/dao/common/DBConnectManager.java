package com.amarsoft.app.dao.common;

import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ryang on 2017/1/5.
 */
public class DBConnectManager {

    static Connection conn1 = null;
    static Connection conn2 = null;

    static{
            try {
                if(conn1==null) {
                    conn1 = ARE.getDBConnection("bdfin");
                }
                if(conn2==null){
                    conn2 = ARE.getDBConnection("dsfin");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }



}
