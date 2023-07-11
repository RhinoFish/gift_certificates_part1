package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class TagDAO{
    private static final String INSERT_SQL = "INSERT INTO tag (name) VALUES (?)";
    private static final String DELETE_SQL = "DELETE FROM tag WHERE tag_id = ?";

    private static final String DELETE_TAG_BY_NAME = "DELETE FROM tag WHERE tag.name = ? RETURNING *";
    private static final String SELECT_BY_ID = "SELECT * FROM tag WHERE tag_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM tag";
    private static final String SELECT_ID_BY_NAME = "SELECT tag_id FROM tag WHERE tag.name = ?";
    private static final String SELECT_TAG_BY_NAME = "SELECT * FROM tag WHERE tag.name = ?";

    private static final String SELECT_IDS = "SELECT tag_id FROM tag WHERE tag.name = ANY(?)";

    @Autowired
    private final DataSource dataSource;

    public TagDAO(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Tag get(int id) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)){
            ps.setInt(1,id);
            Tag tag = null;
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    tag = toTag(rs);
            }
            return tag;
        }catch (SQLException e){
            throw new RuntimeException(e);

        }

    }

    public Tag get(String name){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_TAG_BY_NAME)){
            ps.setString(1,name);
            Tag tag = null;
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                        tag = toTag(rs);
            }
            return tag;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int getId(String name){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ID_BY_NAME);
            ResultSet rs = ps.executeQuery())
        {
            return rs.getInt("tag_id");

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    public List<Integer> getIds(String[] tags){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_IDS)
            ){
            Array arrayTags = conn.createArrayOf("VARCHAR", tags);
            ps.setArray(1,arrayTags);
            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (rs.next()){
                ids.add(rs.getInt("tag_id"));
            }
            return ids;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public List<Tag> getAll() {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
            ResultSet rs = ps.executeQuery()){
            List<Tag> tags = new ArrayList<>();
            while(rs.next()){
                tags.add(toTag(rs));
            }
            return tags;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void save(Tag tag) throws SQLException{
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL)){
            preparedStatement(ps,tag);
            ps.executeUpdate();
        }
    }


    public void update(Tag tag) {

    }

    public void delete(Tag tag) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement((DELETE_SQL))){
            ps.setInt(1,tag.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Tag delete(String name){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_TAG_BY_NAME)){
            ps.setString(1,name);
            Tag tag = null;
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    tag = toTag(rs);
            }
            return tag;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    private void preparedStatement(PreparedStatement ps, Tag tag) throws  SQLException{
        ps.setString(1,tag.getName());
    }

    private Tag toTag(ResultSet rs) throws SQLException{
        Tag tag = new Tag();
        tag.setId(rs.getInt("tag_id"));
        tag.setName(rs.getString("name"));
        return tag;
    }


}
