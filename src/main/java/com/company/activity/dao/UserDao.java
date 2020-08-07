package com.company.activity.dao;

import com.company.activity.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    @Select("select * from miaosha_user where nickname = #{nickname}")
    public User getByNickname(@Param("nickname") String nickname);

    @Select("select * from miaosha_user where id = #{id}")
    public User getById(@Param("id") long id);


    @Update("update miaosha_user set password = #{password} where id = #{id}")
    public void update(User toBeUpdate);


    @Insert("insert into miaosha_user (id , nickname ,password , salt ,head,register_date,last_login_date)value (#{id},#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertMiaoShaUser(User miaoshaUser);
}
