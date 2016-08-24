package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Map;

/**
 * 全局repository代理接口类，在此声明全局repository共享的方法
 * Created by zskx-dev on 2016/8/20.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /**
     * jpql的统计
     *
     * @param jpql
     * @param params
     * @return
     */
    public Long count(String jpql, Map<String, Object> params);

    /**
     * 原生sql的统计
     *
     * @param jpql
     * @param params
     * @return
     */
    public Long countBySql(String jpql, Map<String, Object> params);

    /**
     * 根据jpql进行分页查询，返回{@link R}类型page对象，countJpql为null时，将自动解析生成count语句（注意生成的count语句有可能报错，请注意测试）
     *
     * @param jpql
     * @param countJpql
     * @param pageable
     * @param params
     * @param clazz
     * @param <R>
     * @return
     */
    public <R> Page<R> queryPage(String jpql, String countJpql, Pageable pageable, Map<String, Object> params, Class<R> clazz);

    /**
     * 根据jpql进行分页查询，返回{@link T}类型page对象，countJpql为null时，将自动解析生成count语句（注意生成的count语句有可能报错，请注意测试）
     *
     * @param jpql
     * @param countJpql
     * @param pageable
     * @param params
     * @return
     */
    public Page<T> queryPage(String jpql, String countJpql, Pageable pageable, Map<String, Object> params);

    /**
     * 根据原生sql进行分页查询，返回{@link T}类型page对象，countSql为null时，将自动解析生成count语句（注意生成的count语句有可能报错，请注意测试）<br>
     * {@link R}必须提供公开的默认构造方法 <br>
     *
     * @param sql
     * @param countSql
     * @param pageable
     * @param params
     * @param clazz    如果是Map.class或者是HashMap.class，结果将返回{@code HashMap<String,Object>}类型
     * @param <R>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <R> Page<R> queryPageBySql(String sql, String countSql, Pageable pageable, Map<String, Object> params, Class<R> clazz) throws IllegalAccessException, InstantiationException;
}