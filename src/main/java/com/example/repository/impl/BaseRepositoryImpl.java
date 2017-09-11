package com.example.repository.impl;

import com.example.repository.BaseRepository;
import com.example.repository.transformer.UnderscoreToCamelAliasToBeanResultTransformer;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.*;

/**
 * 全局repository代理接口实现类，在此实现全局repository共享的方法
 * Created by zskx-dev on 2016/8/20.
 */
@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * 绑定参数
     *
     * @param query
     * @param params
     * @return
     */
    private <R extends Query> R bindParams(R query, Map<String, Object> params) {
        Set<Parameter<?>> parameters = query.getParameters();
        for (Parameter<?> parameter : parameters) {
            Optional.ofNullable(parameter.getName()).ifPresent(paramName_ -> query.setParameter(paramName_, params.get(paramName_)));
        }
        return query;
    }

    /**
     * 绑定分页参数
     *
     * @param query
     * @param pageable
     * @return
     */
    private <R extends Query> R bindPage(R query, Pageable pageable) {
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query;
    }

    @Override
    public Long count(String countJpql, Map<String, Object> params) {
        TypedQuery<Long> query = entityManager.createQuery(countJpql, Long.class);
        bindParams(query, params);
        return query.setMaxResults(1).getSingleResult();
    }

    @Override
    public Long countBySql(String countSql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(countSql);
        bindParams(query, params);
        return Long.valueOf(String.valueOf(query.setMaxResults(1).getSingleResult()));
    }

    @Override
    public <R> Page<R> queryPage(String jpql, String countJpql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        Objects.requireNonNull(jpql, "查询语句不能为空");
        Objects.requireNonNull(clazz, "查询类型不能为空");
        //Objects.requireNonNull(pageable, "分页参数不能为空");
        if (pageable == null) {
            return new PageImpl<>(query(jpql, null, params, clazz));
        } else {
            countJpql = Optional.ofNullable(countJpql).orElse(QueryUtils.createCountQueryFor(jpql));
            Long total = count(countJpql, params);
            if (total == 0 && total <= pageable.getOffset()) {
                return new PageImpl<>(Collections.emptyList());
            } else {
                return new PageImpl<>(query(jpql, pageable, params, clazz), pageable, total);
            }
        }
    }

    @Override
    public <R> Page<R> queryPage(String jpql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        return queryPage(jpql, null, pageable, params, clazz);
    }

    private <R> List<R> query(String jpql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        TypedQuery<R> query = entityManager.createQuery(jpql, clazz);
        bindParams(query, params);
        if (pageable != null) {
            bindPage(query, pageable);
        }
        return query.getResultList();
    }

    @Override
    public Page<T> queryPage(String jpql, String countJpql, Pageable pageable, Map<String, Object> params) {
        return queryPage(jpql, countJpql, pageable, params, getDomainClass());
    }

    @Override
    public <R> Page<R> queryPageBySql(String sql, String countSql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        Objects.requireNonNull(sql, "查询语句不能为空");
        Objects.requireNonNull(clazz, "查询类型不能为空");
        //Objects.requireNonNull(pageable, "分页参数不能为空");
        if (pageable == null) {
            return new PageImpl<>(queryBySql(sql, null, params, clazz));
        } else {
            countSql = Optional.ofNullable(countSql).orElse(QueryUtils.createCountQueryFor(sql, "1"));
            Long total = countBySql(countSql, params);
            if (total == 0 && total <= pageable.getOffset()) {
                return new PageImpl<>(Collections.emptyList());
            } else {
                return new PageImpl<>(queryBySql(sql, pageable, params, clazz), pageable, total);
            }
        }
    }

    @Override
    public <R> Page<R> queryPageBySql(String sql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        return queryPageBySql(sql, null, pageable, params, clazz);
    }

    @SuppressWarnings("unchecked")
    private <R> List<R> queryBySql(String sql, Pageable pageable, Map<String, Object> params, Class<R> clazz) {
        Query query = entityManager.createNativeQuery(sql);
        //query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(clazz));
        //query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        //query.unwrap(SQLQuery.class).setResultTransformer(UnderscoreToCamelTransformer.INSTANCE);
        query.unwrap(SQLQuery.class).setResultTransformer(UnderscoreToCamelAliasToBeanResultTransformer.aliasToBean(clazz));//这个里面兼容普通的bean和map
        bindParams(query, params);
        if (pageable != null) {
            bindPage(query, pageable);
        }
        List resultList = query.getResultList();
        //if (Map.class.isAssignableFrom(clazz)) {
        //    return resultList;
        //}
        //List<R> content = new ArrayList<>();
        //for (Object result : resultList) {
        //    //创建新对象
        //    R target = null;
        //    try {
        //        target = clazz.newInstance();
        //    } catch (Exception e) {
        //        log.error("以默认构造函数创建实例失败，请检查是否有公开的默认构造方法", e);
        //        throw e;
        //    }
        //    //注入属性值
        //    PropertyAccessorFactory.forBeanPropertyAccess(target).setPropertyValues(new MutablePropertyValues((Map) result), true, false);
        //    content.add(target);
        //}
        return resultList;
    }
}