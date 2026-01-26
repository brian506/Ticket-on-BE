package com.ticketon.ticketon.utils;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
public abstract class QuerydslRepositorySupport {

    private final Class<?> entityClass;
    private Querydsl querydsl;
    private EntityManager entityManager;
    private JPAQueryFactory jpaQueryFactory;

    protected QuerydslRepositorySupport(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;

        EntityPath<?> path = SimpleEntityPathResolver.INSTANCE.createPath(
                JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager).getJavaType());

        this.querydsl = new Querydsl(entityManager, new PathBuilder<>(path.getType(), path.getMetadata()));
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    protected <T> JPAQuery<T> select(EntityPath<T> select) {
        return this.jpaQueryFactory.select(select);
    }

    protected <T> JPAQuery<T> select(Expression<T> select) {
        return this.jpaQueryFactory.select(select);
    }

    protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
        return this.jpaQueryFactory.selectFrom(from);
    }

    protected JPAQuery<Integer> selectOne() {
        return this.jpaQueryFactory.selectOne();
    }

    protected <T> JPAUpdateClause update(EntityPath<T> from) {
        return this.jpaQueryFactory.update(from);
    }


}
