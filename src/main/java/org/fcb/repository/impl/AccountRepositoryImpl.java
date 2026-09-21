package org.fcb.repository.impl;

import org.fcb.domain.Account;
import org.fcb.repository.filter.AccountFilter;
import org.fcb.repository.query.AccountRepositoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountRepositoryImpl implements AccountRepositoryQuery {

    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    @PersistenceContext
    private EntityManager manager;


    @Override
    public Page<Account> filter(AccountFilter accountFilter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> root = criteria.from(Account.class);

        getSortOrder(accountFilter, builder, criteria, root);

        Predicate[] predicates = createRestrictions(accountFilter, builder, root);
        criteria.where(predicates);

        TypedQuery<Account> query = manager.createQuery(criteria);
        addRestrictionsPagination(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(accountFilter));
    }

    private void addRestrictionsPagination(TypedQuery<?> query, Pageable pageable) {
        int currentPage = pageable.getPageNumber();
        int totalRecordsByPage = pageable.getPageSize();
        int firstPageRecord = currentPage * totalRecordsByPage;

        query.setFirstResult(firstPageRecord);
        query.setMaxResults(totalRecordsByPage);

        logger.info("Current page: " + currentPage + " Total records by page: " + totalRecordsByPage + " First record page " + firstPageRecord);
    }

    private Long total(AccountFilter accountFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Account> root = criteria.from(Account.class);

        Predicate[] predicates = createRestrictions(accountFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }

    private Predicate[] createRestrictions(AccountFilter accountFilter, CriteriaBuilder builder,Root<Account> root) {
        List<Predicate> predicates = new ArrayList<>();

        restrictions(accountFilter, predicates, builder, root);

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    public void restrictions(AccountFilter accountFilter, List<Predicate> predicates, CriteriaBuilder builder, Root<Account> root){

        if (accountFilter.getNumber() != null) {
            predicates.add(builder.equal(
                    builder.lower(root.get("number")), accountFilter.getNumber()));
        }

        //if (accountFilter.getCustomer() != null) {
        //    predicates.add(builder.equal(
        //            builder.lower(root.get("customer").get("name")), accountFilter.getCustomer().getName()));
        //}
    }

    public void getSortOrder(AccountFilter accountFilter, CriteriaBuilder builder, CriteriaQuery<Account> criteria, Root<Account> root){
        if(Objects.equals(accountFilter.getSortBy(), "id,asc")){
            criteria.orderBy(builder.asc(root.get("id")));
        }
        if(Objects.equals(accountFilter.getSortBy(), "id,desc")){
            criteria.orderBy(builder.desc(root.get("id")));
        }
    }

}
