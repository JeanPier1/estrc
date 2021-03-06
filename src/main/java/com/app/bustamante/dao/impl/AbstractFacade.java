package com.app.bustamante.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author jhoffma5
 */
public abstract class AbstractFacade<T> {

	private Class<T> entityClass;

	public AbstractFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	public T create(T entity) {
		getEntityManager().persist(entity);
		getEntityManager().flush();
		return entity;
	}

	public T edit(T entity) {
		getEntityManager().merge(entity);
		getEntityManager().flush();
		return entity;
	}

	public void delete(Object id) {
		Object ref = getEntityManager().getReference(entityClass, id);
		getEntityManager().remove(ref);
	}

	public T remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
		getEntityManager().flush();
		return entity;
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> findAll() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> findRange(int[] range) {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0] + 1);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int count() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> findWithNamedQuery(String namedQueryName) {
		return getEntityManager().createNamedQuery(namedQueryName).getResultList();
	}

	@SuppressWarnings("rawtypes")
	public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = getEntityManager().createNamedQuery(namedQueryName);
		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findWithQuery(String queryName) {
		return getEntityManager().createQuery(queryName).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNativeQuery(String sql) {
		return getEntityManager().createNativeQuery(sql, entityClass).getResultList();
	}

	@SuppressWarnings("unchecked")
	public T findSingleWithNamedQuery(String namedQueryName) {
		T result = null;
		try {
			result = (T) getEntityManager().createNamedQuery(namedQueryName).getSingleResult();
		} catch (NoResultException e) {
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public T findSingleWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = getEntityManager().createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		T result = null;
		try {
			result = (T) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return result;
	}
}