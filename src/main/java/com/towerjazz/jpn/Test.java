/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towerjazz.jpn;

import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;
import java.beans.Statement;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 *
 * @author sato
 */
public class Test {
    
    public static void main(String args[]) throws SQLException, MessagingException, UnsupportedEncodingException {
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());

        String db_url = "jdbc:oracle:thin:@db-server:1521:db";
        System.out.println("Connecting to: " + db_url);

        Connection conn =
            DriverManager.getConnection(db_url,"&&&","***");

        java.sql.Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                            ResultSet.CONCUR_READ_ONLY);
     
        ResultSet rs = stmt.executeQuery("select * from helpticket.ht_case where id=20");
        rs.next();
        String req_desc = rs.getString("req_desc");
        String detail_desc = rs.getString("detail_desc");
        rs.close();
        
        
        System.out.println("req_desc = " + req_desc);
        Properties props = System.getProperties();
        props.put("mail.smtp.host","localhost");
        Session session = Session.getInstance(props, null);
        
        
        SMTPMessage message = new SMTPMessage(session);
        message.setHeader("Content-Type", "text/plain; charset=UTF-8");
        message.setFrom(new InternetAddress("test@towerjazz.com"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("testjpn@gmail.com"));
        message.setSubject("TPSCO HT: " + cvt(req_desc));
        message.setText(detail_desc,"UTF-8","html");
        
        SMTPTransport trans = (SMTPTransport)session.getTransport("smtp");
        trans.connect();
        trans.sendMessage(message, message.getAllRecipients());
        trans.close();
        
        
    }
    
    private static String cvt(String s) {
        String result = "";
        String[] words = s.split(";");
        for (String word : words) {
            if (word.startsWith("&#")) {
                int hex = Integer.parseInt(word.substring(2));
                result += (char)hex;
            }
        }
        return result;
    }

}
