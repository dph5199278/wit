/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cs.wit.persistence.hibernate;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseService<T> {

    private final SessionFactory hibernateFactory;

    @Autowired
    public BaseService(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
    }


    /**
     * 批量更新
     *
     * @param ts 需更新的列表
     */
    public void saveOrUpdateAll(final List<Object> ts) {
        Session session = hibernateFactory.openSession();
        Transaction tx = session.beginTransaction();
        try (session) {
            for (final Object t : ts) {
                session.saveOrUpdate(t);
            }
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    public void saveOrUpdate(final Object t) {
        Session session = hibernateFactory.openSession();
        Transaction tx = session.beginTransaction();
        try (session) {
            session.saveOrUpdate(t);
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    public void save(final Object t) {
        Session session = hibernateFactory.openSession();
        Transaction tx = session.beginTransaction();
        try (session) {
            session.save(t);
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    /**
     * 批量删除
     *
     * @param objects 需删除的列表
     */
    public void deleteAll(final List<Object> objects) {
        Session session = hibernateFactory.openSession();
        Transaction tx = session.beginTransaction();
        try (session) {
            for (final Object t : objects) {
                session.delete(session.merge(t));
            }
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    public void delete(final Object object) {
        Session session = hibernateFactory.openSession();
        Transaction tx = session.beginTransaction();
        try (session) {
            session.delete(session.merge(object));
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> list(final String bean) {
        List<T> dataList = null;
        Session session = hibernateFactory.openSession();
        try (session) {
            CriteriaQuery<T> query = session.getCriteriaBuilder()
                .createQuery((Class<T>) Class.forName(bean));
            dataList = session.createQuery(query).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dataList;
    }
}