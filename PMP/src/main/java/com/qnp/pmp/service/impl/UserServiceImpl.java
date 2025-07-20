package com.qnp.pmp.service.impl;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.qnp.pmp.Enum.RoleUser;
import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.UserViewDTO;
import com.qnp.pmp.dto.UserViewModel;
import com.qnp.pmp.entity.User;
import com.qnp.pmp.service.UserService;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public void saveUser(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();
            String sql = "INSERT INTO user(username,password,full_name,created_at,role) VALUES(?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getFullName());
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, RoleUser.USER.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
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

    @Override
    public void createAdmin(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();
            String sql = "INSERT INTO user(username,password,full_name,created_at,role) VALUES(?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getFullName());
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, RoleUser.ADMIN.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
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

    @Override
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM user WHERE username= ?";
        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = new User();
                    user.setId(resultSet.getString("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(hashedPassword); // or null for security
                    user.setFullName(resultSet.getString("full_name"));
                    user.setRole(RoleUser.valueOf(resultSet.getString("role")));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser() {

        return null;
    }

    @Override
    public List<UserViewDTO> getUserByRoleAdmin() {
            String sql="SELECT * FROM user WHERE role='ADMIN'";
            List<UserViewDTO> userViewModelList=new ArrayList<>();
            try (Connection conn=MySQLConnection.getConnection()){
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs=stmt.executeQuery();
                int index=1;
                while (rs.next()){
                    int id = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    String username = rs.getString("username");

                    // Tạo UserViewModel (hoặc UserViewDTO) với chỉ số tăng dần
                    UserViewDTO user = new UserViewDTO(index++, fullName, username);
                    userViewModelList.add(user);

                }
            } catch (Exception e) {
                e.printStackTrace(); // hoặc log lỗi nếu có logger
            }
        return userViewModelList;
    }

    @Override
    public List<UserViewDTO> getUserByRoleUser() {
        String sql="SELECT * FROM user WHERE role='USER'";
        List<UserViewDTO> userViewModelList=new ArrayList<>();
        try (Connection conn=MySQLConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            int index=1;
            while (rs.next()){
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String username = rs.getString("username");
                UserViewDTO user = new UserViewDTO(index++, fullName, username);
                userViewModelList.add(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userViewModelList;
    }
}
