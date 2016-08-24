package com.example.repository.factory;

import com.example.repository.impl.BaseRepositoryImpl;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 覆盖基础仓库代理工厂，实现自己的全局BaseRepository方法
 * Created by ZoeMak on 2016/8/23.
 */
public class BaseRepositoryFactoryBean extends JpaRepositoryFactoryBean {

    //覆盖父类方法，使用自定义的BaseRepositoryFactory作为repository工厂类
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new BaseRepositoryFactory(entityManager);
    }

    //使用自定义的BaseRepositoryFactory作为repository工厂类
    private class BaseRepositoryFactory extends JpaRepositoryFactory {

        /**
         * Creates a new {@link JpaRepositoryFactory}.
         *
         * @param entityManager must not be {@literal null}
         */
        public BaseRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        //覆盖父类此方法，在创建repository类的时候使用BaseRepositoryImpl作为基础实现类，共享BaseRepositoryImpl全局方法，返回的一定是下面getRepositoryBaseClass的类型
        @Override
        protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
            return new BaseRepositoryImpl<>(getEntityInformation(metadata.getDomainType()), entityManager);
        }

        //覆盖父类此方法，在创建repository类的时候使用BaseRepositoryImpl作为基础实现类，共享BaseRepositoryImpl全局方法
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseRepositoryImpl.class;
        }
    }
}
