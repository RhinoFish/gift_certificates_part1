package com.epam.esm.dao;

import com.epam.esm.dto.GiftDTO;
import com.epam.esm.model.Gift;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class GiftDAO{
    private static final String SELECT_GIFTS = "SELECT gift_certificate.*," +
                                                " array_agg(tag.name) AS tags_names" +
                                                " FROM gift_certificate" +
                                                " LEFT JOIN gift_tag  ON gift_certificate.gift_id = gift_tag.gift_id" +
                                                " LEFT JOIN tag  ON gift_tag.tag_id = tag.tag_id";
    private static final String SELECT_ALL_THE_TAGS = " WHERE gift_certificate.gift_id IN (" +
                                                    "  SELECT gc2.gift_id" +
                                                    "  FROM gift_certificate gc2" +
                                                    "  JOIN gift_tag gt2 ON gc2.gift_id = gt2.gift_id" +
                                                    "  JOIN tag t2 ON gt2.tag_id = t2.tag_id" +
                                                    "  WHERE t2.name = ";
    private static final String INSERT_GIFT = "INSERT INTO gift_certificate " +
                                "(name,description,price,duration,create_date,last_update_date)" +
            "VALUES(?, ?, ?, ?, ?, ?)";
    private static final String DELETE_GIFT_BY_ID = "DELETE FROM gift_certificate WHERE gift_id = ? RETURNING *";
    private static final String SELECT_ALL = "SELECT gift_certificate.*," +
                                             " array_agg(tag.name) AS tags_names" +
                                             " FROM gift_certificate" +
                                             " LEFT JOIN gift_tag  ON gift_certificate.gift_id = gift_tag.gift_id" +
                                             " LEFT JOIN tag  ON gift_tag.tag_id = tag.tag_id" +
                                             " GROUP BY gift_certificate.gift_id, gift_certificate.name" +
                                             " ORDER BY gift_certificate.gift_id";

    private static final String UPDATE_GIFT = "UPDATE gift_certificate SET"+
                                               " name = COALESCE(?,name),"+
                                               " description = COALESCE(?,description),"+
                                               " price = COALESCE(?,price),"+
                                               " duration = COALESCE(?,duration),"+
                                               " create_date = COALESCE(?,create_date),"+
                                               " last_update_date = COALESCE(?,last_update_date)"+
                                               " WHERE gift_id = ?";

    private static final String SELECT_ID_BY_NAME = "SELECT gift_id FROM gift_certificate WHERE gift_certificate.name = ?";

    private static final String GET_GIFT_BY_ID = "SELECT gift_certificate.*," +
                                                " array_agg(tag.name) AS tags_names" +
                                                " FROM gift_certificate" +
                                                " LEFT JOIN gift_tag  ON gift_certificate.gift_id = gift_tag.gift_id" +
                                                " LEFT JOIN tag  ON gift_tag.tag_id = tag.tag_id" +
                                                " WHERE gift_certificate.gift_id = ?"+
                                                " GROUP BY gift_certificate.gift_id, gift_certificate.name";

    private static final String INSERT_RELATIONSHIP = "INSERT INTO gift_tag VALUES(?,?)";
    private static final String DELETE_RELATIONSHIPS = "DELETE FROM gift_tag WHERE gift_id = ?";

    private static final String INSERT_TAGS = "INSERT INTO tag (name) VALUES (?)";
    @Autowired
    private final DataSource dataSource;
    @Autowired
    private final TagDAO tagDAO;

    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    public GiftDAO(DataSource dataSource, TagDAO tagDAO) {
        this.dataSource = dataSource;
        this.tagDAO = tagDAO;
    }



    public List<GiftDTO> getAllGifts(String sort,String tagName,String nameDesc,boolean ascending){
        String statement = getStatement(sort, tagName, nameDesc, ascending);
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);
            ResultSet rs =ps.executeQuery()){
            List<GiftDTO> giftsDTOs = new ArrayList<>();
            while(rs.next()){
                giftsDTOs.add(toGiftDTO(rs));
            }
            return giftsDTOs;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private String getStatement(String sort, String tagName, String nameDesc, boolean ascending) {
        StringBuilder statement = new StringBuilder(SELECT_GIFTS);
        StringBuilder orderBy = new StringBuilder(" ORDER BY");
        StringBuilder groupBy = new StringBuilder(" GROUP BY gift_certificate.gift_id");
        StringBuilder suffix = new StringBuilder();

        suffix.append(ascending?"ASC":"DESC");
        if(sort.equalsIgnoreCase("lastupdatedate")){
            orderBy.append(" last_update_date ");
            orderBy.append(suffix);
        } else if (sort.equalsIgnoreCase("createdate")) {
            orderBy.append(" create_date ");
            orderBy.append(suffix);
        }
        else{
            orderBy.append(" name ");
            orderBy.append(suffix);
        }
        if(tagName.isBlank() && nameDesc.isBlank()){
            statement.append(groupBy);
            statement.append(orderBy);
            return statement.toString();
        }else if (!(tagName.isBlank()) && nameDesc.isBlank()){
            statement.append(SELECT_ALL_THE_TAGS);
            statement.append("'" + tagName + "')");
            statement.append(groupBy);
            statement.append(orderBy);

        } else if (tagName.isBlank() && !(nameDesc.isBlank())) {
            statement.append("WHERE gift_certificate.name LIKE '%"+nameDesc+"%' OR gift_certificate.description LIKE '%"+nameDesc+"%'");
            statement.append(groupBy);
            statement.append(orderBy);
        }else {
            statement.append(SELECT_ALL_THE_TAGS);
            statement.append("'" + tagName + "')");
            statement.append("AND (gift_certificate.name LIKE '%"+nameDesc+"%' OR gift_certificate.description LIKE '%"+nameDesc+"%')");
            statement.append(groupBy);
            statement.append(orderBy);
        }
        return statement.toString();
    }

    public int getId(String name){

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ID_BY_NAME)
            ){
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt("gift_id");
            }else{
                throw new RuntimeException("Not Element with that name");
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Gift save(Gift gift) {

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_GIFT)){
            preparedStatement(ps,gift);
            ps.executeUpdate();
            return gift;

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

    }


    public void update(Gift gift) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE_GIFT)){
            preparedStatementForUpdate(ps,gift);
            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }



    private void preparedStatement(PreparedStatement ps, Gift gift) throws SQLException{
        ps.setString(1,gift.getName());
        ps.setString(2,gift.getDescription());
        ps.setBigDecimal(3,gift.getPrice());
        ps.setInt(4,gift.getDuration());
        ps.setTimestamp(5,gift.getCreateDate());
        ps.setTimestamp(6,gift.getLastUpdateDate());
    }
    private GiftDTO toGiftDTO(ResultSet rs) throws SQLException{
        GiftDTO giftDTO = new GiftDTO();
        giftDTO.setId(rs.getInt("gift_id"));
        giftDTO.setName(rs.getString("name"));
        giftDTO.setDescription(rs.getString("description"));
        giftDTO.setPrice(rs.getBigDecimal("price"));
        giftDTO.setDuration(rs.getInt("duration"));
        giftDTO.setCreateDate(rs.getTimestamp("create_date",tzUTC));
        giftDTO.setLastUpdateDate(rs.getTimestamp("last_update_date",tzUTC));
        giftDTO.setTags(Arrays.asList(getArray(rs)));
        return giftDTO;
    }

    private String[] getArray(ResultSet rs)throws SQLException{
        Array arr = rs.getArray("tags_names");
        if (arr != null) {
            Object[] arrayValues = (Object[]) arr.getArray();
            String[] stringValues = Arrays.copyOf(arrayValues, arrayValues.length, String[].class);
            return stringValues;
        }
       return null;
    }

    private void preparedStatementForUpdate(PreparedStatement ps, Gift gift) throws SQLException{
        ps.setString(1,gift.getName());
        ps.setString(2,gift.getDescription());
        ps.setBigDecimal(3,gift.getPrice());
        ps.setObject(4,gift.getDuration());
        ps.setTimestamp(5,gift.getCreateDate());
        ps.setTimestamp(6,gift.getLastUpdateDate());
        ps.setInt(7,gift.getId());
    }

    public Gift deleteGift(int giftId) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_GIFT_BY_ID)){
            ps.setInt(1,giftId);
            Gift gift = null;
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    gift = toGift(rs);
            }
            return gift;
        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    private Gift toGift(ResultSet rs) throws SQLException {
        Gift gift = new Gift();
        gift.setId(rs.getInt("gift_id"));
        gift.setName(rs.getString("name"));
        gift.setDescription(rs.getString("description"));
        gift.setPrice(rs.getBigDecimal("price"));
        gift.setDuration(rs.getInt("duration"));
        gift.setCreateDate(rs.getTimestamp("create_date",tzUTC));
        gift.setLastUpdateDate(rs.getTimestamp("last_update_date",tzUTC));
        return gift;
    }

    public GiftDTO get(int giftId) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_GIFT_BY_ID)){
            ps.setInt(1,giftId);
            GiftDTO giftDTO = null;
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    giftDTO = toGiftDTO(rs);
            }
            return giftDTO;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public Gift saveWithNoTags(Gift gift) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_GIFT)){
            preparedStatement(ps,gift);
            ps.executeUpdate();
            return gift;

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Gift saveWithNoNewTags(Gift gift, List<Integer> idTags) throws SQLException {
        Connection conn = dataSource. getConnection();
        try{
            conn.setAutoCommit(false);
            int giftId = 0;
            PreparedStatement insertGift = conn.prepareStatement(INSERT_GIFT,Statement.RETURN_GENERATED_KEYS);
            preparedStatement(insertGift,gift);
            insertGift.executeUpdate();
            ResultSet rs = insertGift.getGeneratedKeys();
            if(rs.next()){
                giftId = rs.getInt(1);
            }
            for(Integer id : idTags){
                PreparedStatement insertRelationship = conn.prepareStatement(INSERT_RELATIONSHIP);
                insertRelationship.setInt(1,giftId);
                insertRelationship.setInt(2,id);
                insertRelationship.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            return gift;
        }catch (Exception e){
            conn.rollback();
            conn.close();
            throw new RuntimeException(e);
        }
    }

    public Gift saveWithNewTags(Gift gift, List<String> newTags, List<String> tagsDTO) throws SQLException {
        Connection conn = dataSource.getConnection();
        try{
            conn.setAutoCommit(false);
            int giftId = 0;
            PreparedStatement insertGift = conn.prepareStatement(INSERT_GIFT,Statement.RETURN_GENERATED_KEYS);
            preparedStatement(insertGift,gift);
            insertGift.executeUpdate();
            ResultSet rs = insertGift.getGeneratedKeys();
            if(rs.next()){
                 giftId = rs.getInt(1);
            }
            List<Integer> tagsId =  tagDAO.getIds(tagsDTO.toArray(new String[0]));
            for(String tag:newTags){
                PreparedStatement insertNewTags = conn.prepareStatement(INSERT_TAGS,Statement.RETURN_GENERATED_KEYS);
                insertNewTags.setString(1,tag);
                insertNewTags.executeUpdate();
                ResultSet ResultTags = insertNewTags.getGeneratedKeys();
                if(ResultTags.next()){
                    tagsId.add(ResultTags.getInt(1));
                }
            }
            System.out.println(tagsId);
            System.out.println(tagsDTO);
            for(Integer id : tagsId){
                PreparedStatement insertRelationship = conn.prepareStatement(INSERT_RELATIONSHIP);
                insertRelationship.setInt(1,giftId);
                insertRelationship.setInt(2,id);
                insertRelationship.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            return gift;
        }catch (Exception e){
            conn.rollback();
            conn.close();
            throw new RuntimeException(e);
        }
    }

    public Gift updateWithoutTags(Gift gift) throws SQLException {
        Connection conn = dataSource.getConnection();
        try{
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE_GIFT);
            preparedStatementForUpdate(ps,gift);
            ps.executeUpdate();

            PreparedStatement deleteRelationships = conn.prepareStatement(DELETE_RELATIONSHIPS);
            deleteRelationships.setInt(1,gift.getId());
            deleteRelationships.executeUpdate();



            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            return gift;
        }catch (Exception e){
            conn.rollback();
            conn.close();
            throw new RuntimeException(e);
        }
    }

    public Gift updateWithNoNewTags(Gift gift, List<Integer> idTags) throws SQLException {
        Connection conn = dataSource.getConnection();
        try{
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE_GIFT);
            preparedStatementForUpdate(ps,gift);
            ps.executeUpdate();

            PreparedStatement deleteRelationships = conn.prepareStatement(DELETE_RELATIONSHIPS);
            deleteRelationships.setInt(1,gift.getId());
            deleteRelationships.executeUpdate();

            for(Integer id : idTags){
                PreparedStatement insertRelationship = conn.prepareStatement(INSERT_RELATIONSHIP);
                insertRelationship.setInt(1,gift.getId());
                insertRelationship.setInt(2,id);
                insertRelationship.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            return gift;
        }catch (Exception e){
            conn.rollback();
            conn.close();
            throw new RuntimeException(e);
        }
    }

    public Gift updateWithNewTags(Gift gift, List<String> newTags, List<String> tagsDTO) throws SQLException {
        Connection conn = dataSource.getConnection();
        try{
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE_GIFT);
            preparedStatementForUpdate(ps,gift);
            ps.executeUpdate();

            PreparedStatement deleteRelationships = conn.prepareStatement(DELETE_RELATIONSHIPS);
            deleteRelationships.setInt(1,gift.getId());
            deleteRelationships.executeUpdate();

            List<Integer> tagsId =  tagDAO.getIds(tagsDTO.toArray(new String[0]));
            for(String tag:newTags){
                PreparedStatement insertNewTags = conn.prepareStatement(INSERT_TAGS,Statement.RETURN_GENERATED_KEYS);
                insertNewTags.setString(1,tag);
                insertNewTags.executeUpdate();
                ResultSet ResultTags = insertNewTags.getGeneratedKeys();
                if(ResultTags.next()){
                    tagsId.add(ResultTags.getInt(1));
                }
            }

            for(Integer id : tagsId){
                PreparedStatement insertRelationship = conn.prepareStatement(INSERT_RELATIONSHIP);
                insertRelationship.setInt(1,gift.getId());
                insertRelationship.setInt(2,id);
                insertRelationship.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            return gift;
        }catch (Exception e){
            conn.rollback();
            conn.close();
            throw new RuntimeException(e);
        }
    }
}
