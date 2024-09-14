package com.ufc.molic.dao;

import com.ufc.molic.entity.Anotacao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;

public class AnotacaoDAOImpl implements AnotacaoDAO {

    @Override
    public void save(Anotacao anotacao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            JPAUtil.beginTransaction();
            em.merge(anotacao);
            JPAUtil.commit();
        } catch (Exception e) {
            JPAUtil.rollback();
            throw new DAOException("Erro ao salvar anotacao", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    @Override
    public void update(Anotacao anotacao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Anotacao anotacao1 = em.find(Anotacao.class, anotacao.getId());
            JPAUtil.beginTransaction();
            anotacao1.setNote(anotacao.getNote());
            em.merge(anotacao1);
            JPAUtil.commit();
        } catch (Exception e) {
            JPAUtil.rollback();
            throw new DAOException("Erro ao atualizar anotacao", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            JPAUtil.beginTransaction();
            Anotacao anotacao = em.find(Anotacao.class, id);
            if (anotacao != null) {
                em.remove(anotacao);
            }
            JPAUtil.commit();
        } catch (Exception e) {
            JPAUtil.rollback();
            throw new DAOException("Erro ao excluir anotacao", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    @Override
    public Anotacao find(String note) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select c from Anotacao c where c.note = :note", Anotacao.class)
                    .setParameter("note", note)
                    .getSingleResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar anotacao", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    @Override
    public List<Anotacao> find() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Anotacao> criteriaQuery = em.getCriteriaBuilder().createQuery(Anotacao.class);
            criteriaQuery.from(Anotacao.class);
            return em.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar anotações", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }
}
