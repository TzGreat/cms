package com.example.demo.mapper;

import com.example.demo.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {
    //新增
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            "values (#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})")
    void add(Article article);

    //分页查询(动态查询使用xml)
    List<Article> list(Integer userId, Integer categoryId, String state);

    //id查询文章
    @Select("select * from article where id=#{id} and create_user=#{createUser}")
    Article selectById(Integer id, Integer createUser);

    //更新
    @Update("update article set title=#{title},content=#{content},cover_img=#{coverImg},state=#{state},category_id=#{categoryId}," +
            "update_time=#{updateTime} where id=#{id} and create_user=#{createUser}")
    void update( Article article);

    //删除
    @Delete("delete from article where id=#{id} and create_user=#{createUser}")
    void delete(@Param("id") Integer id,@Param("createUser") Integer createUser);

    //更新摘要
    @Update("update article set summary=#{summary} where id=#{id} and create_user=#{createUser}")
    void updateSummary(@Param("id") Integer id, @Param("summary") String summary, @Param("createUser") Integer createUser);

    //根据ID查询文章（不带createUser限制，用于AI服务）
    @Select("select * from article where id=#{id}")
    Article selectByIdForAI(Integer id);

}