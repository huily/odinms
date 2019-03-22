  /*
* @丶小_路.
*/

package net.sf.odinms.scripting.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.odinms.client.MapleCharacter;

import net.sf.odinms.database.DatabaseConnection;

public class Marriage {
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Marriage.class);

        public static void createMarriage(MapleCharacter player, MapleCharacter partner) {
                try {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("INSERT INTO marriages (husbandid, wifeid) VALUES (?, ?)");
                        ps.setInt(1, player.getId());
                        ps.setInt(2, partner.getId());
                        ps.executeUpdate();
                        ps.close();
                } catch (SQLException ex) {
                        log.warn("结婚: " + player.getName() + " 和 " + partner.getName(), ex);
                }
        }

        public static void createEngagement(MapleCharacter player, MapleCharacter partner) {
                try {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("INSERT INTO engagements (husbandid, wifeid) VALUES (?, ?)");
                        ps.setInt(1, player.getId());
                        ps.setInt(2, partner.getId());
                        ps.executeUpdate();
                } catch (SQLException ex) {
                        log.warn("宣布与参与 " + player.getName() + " 和" + partner.getName(), ex);
                }
        }

        public static void divorceEngagement(MapleCharacter player, MapleCharacter partner) {
                try {
                        Connection con = DatabaseConnection.getConnection();
                        int pid = 0;
                        if (player.getGender() == 0) {
                                pid = player.getId();
                        } else {
                                pid = partner.getId();
                        }
                        PreparedStatement get = con.prepareStatement("SELECT FROM engagements WHERE husbandid = ?");
                        get.setInt(1, pid);
                        ResultSet rs = get.executeQuery();
                        PreparedStatement ps = con.prepareStatement("DELETE FROM engagements WHERE husbandid = ?");
                        if (rs.next()) {
                                ps.setInt(1, pid);
                        } else {
                                return;
                        }
                        ps.executeUpdate();
                        PreparedStatement ps1 = con.prepareStatement("UPDATE characters SET marriagequest = 0 WHERE id = ?, and WHERE id = ?");
                        ps1.setInt(1, player.getId());
                        ps1.setInt(2, partner.getId());
                        ps1.executeUpdate();
                } catch (SQLException ex) {
                        log.warn("宣布与参与 " + player.getName() + " 和 " + partner.getName(), ex);
                }
        }

        public static void divorceMarriage(MapleCharacter player, MapleCharacter partner) {
                try {
                        Connection con = DatabaseConnection.getConnection();
                        int pid = 0;
                        if (player.getGender() == 0) {
                                pid = player.getId();
                        } else {
                                pid = partner.getId();
                        }
                        PreparedStatement get = con.prepareStatement("SELECT partnerid FROM characters WHERE id = ?");
                        get.setInt(1, player.getId());
                        ResultSet rs = get.executeQuery();
                        if (rs.next()) {
                                PreparedStatement ps = con.prepareStatement("DELETE FROM marriages WHERE husbandid = ?");
                                ps.setInt(1, pid);
                                ps.executeUpdate();
                                PreparedStatement ps1 = con.prepareStatement("UPDATE characters SET married = 0 WHERE id = ?, and WHERE id = ?");
                                ps1.setInt(1, player.getId());
                                ps1.setInt(2, partner.getId());
                                ps1.executeUpdate();
                                ps.close();
                                ps1.close();
                        } else {
                                return;
                        }
                        get.close();
                } catch (SQLException ex) {
                        log.warn("离婚:" + player.getName() + " 和 " + partner.getName(), ex);
                }
        }

   public static void divorceMarriage(MapleCharacter player) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM marriages WHERE husbandid = ?");
            if (player.getGender() != 0) {
                ps = con.prepareStatement("DELETE FROM marriages WHERE wifeid = ?");
            }
            ps.setInt(1, player.getId());
            ps.executeUpdate();
            PreparedStatement ps1 = con.prepareStatement("UPDATE characters SET married = 0 WHERE id = ?");
            ps1.setInt(2, player.getPartnerId());
            ps1.executeUpdate();
            PreparedStatement ps2 = con.prepareStatement("UPDATE characters SET partnerid = 0 WHERE id = ?");
            ps2.setInt(2, player.getPartnerId());
            ps2.executeUpdate();
            ps.close();
            ps1.close();
            ps2.close();
        } catch (SQLException ex) {
            log.warn("Problem divorcing" + player.getName() + " and his or her partner", ex);
        }
         }
}