package com.company.activity.dao;

import com.company.activity.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {

    @Select("select * from activity_user where nickname = #{nickname}")
    User getByNickname(@Param("nickname") String nickname);

    @Select("select * from activity_user where id = #{id}")
    User getById(@Param("id") long id);


    @Update("update activity_user set password = #{password} where id = #{id}")
    void update(User user);


    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into activity_user (nickname ,password , salt ,head,register_date,last_login_date)value (#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate}) ")
    void insertUser(User user);


}
