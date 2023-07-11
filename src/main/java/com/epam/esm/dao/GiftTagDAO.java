package com.epam.esm.dao;

import com.epam.esm.model.GiftTag;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiftTagDAO {

    private final static String INSERT_GIFTTAGS = "INSERT INTO gift_tag VALUES(?,?)";
    private final static String DELETE_RELS_OF_A_GIFT = "DELETE FROM gift_tag WHERE gift_id = ?";

    private final static String GET_TAGS_GIFT = "SELECT * FROM gift_tag  WHERE gift_id = ?";
    @Autowired
    private final DataSource dataSource;

    public GiftTagDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(List<GiftTag> giftTag){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_GIFTTAGS)){
            preparedStatement(ps,giftTag);
            ps.executeBatch();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    private void preparedStatement(PreparedStatement ps,List<GiftTag> giftTags) throws SQLException{

        for(GiftTag giftTag: giftTags){
            ps.setInt(1,giftTag.getGift_id());
            ps.setInt(2,giftTag.getTag_id());
            ps.addBatch();
        }

    }

    public void delete(int giftId) {
        try(Connection conn =  dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_RELS_OF_A_GIFT)){
            ps.setInt(1,giftId);
            ps.executeUpdate();

    }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void delete(List<GiftTag> giftTags) {
        StringBuilder DELETE_RELS = new StringBuilder("DELETE FROM gift_tag WHERE gift_id = ? AND (");
        for(GiftTag giftTag : giftTags){
            DELETE_RELS.append("tag_id != ? AND ");
        }
        DELETE_RELS.delete(DELETE_RELS.length()-4,DELETE_RELS.length());
        DELETE_RELS.append(")");
        try(Connection conn =  dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_RELS.toString())){
            preparedStatementForDelete(ps,giftTags);
            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void preparedStatementForDelete(PreparedStatement ps, List<GiftTag> giftTags) throws SQLException{
        ps.setInt(1,giftTags.get(0).getGift_id());
        for(int i =0 ; i<giftTags.size(); i++){
            ps.setInt(i+2,giftTags.get(i).getTag_id());
        }
    }

    public List<Integer> getIdTagsGift(int giftId) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_TAGS_GIFT)){
            ps.setInt(1,giftId);
            ResultSet rs = ps.executeQuery();
            List<Integer> tagsIds = new ArrayList<>();
            while(rs.next()){
                tagsIds.add(rs.getInt("tag_id"));
            }
            return tagsIds;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
