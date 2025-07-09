package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.service.LevelService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LevelServiceImpl implements LevelService {
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
}
