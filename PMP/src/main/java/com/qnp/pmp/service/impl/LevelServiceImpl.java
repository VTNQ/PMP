package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.LevelDTO;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.service.LevelService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LevelServiceImpl implements LevelService
{
    @Override
    public List<Level> getAll() {
        List<Level> levelList = new ArrayList<Level>();
        String sql = "select * from level";
        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet re = stmt.executeQuery();
            while (re.next()) {
                Level level = new Level();
                level.setId(re.getInt("id"));
                level.setName(re.getString("name"));
                levelList.add(level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return levelList;
    }

    @Override
    public Level getByName(String levelName) {
        String sql = "select * from level where name = ?";
        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, levelName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Level level = new Level();
                level.setId(rs.getInt("id"));
                level.setName(rs.getString("name"));
                return level;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(LevelDTO level) {
        Connection conn=null;
        PreparedStatement stmt=null;
        try {
            conn=MySQLConnection.getConnection();
            String sql="insert into level(name,salary) values(?,?)";
            stmt=conn.prepareStatement(sql);
            stmt.setString(1,level.getName());
            stmt.setDouble(2,level.getSalary());
            stmt.executeUpdate();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
            }
        }
    }
}
