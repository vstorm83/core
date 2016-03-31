/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.services.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Nov 25, 2004
 * @version $Id: ObjectQuery.java 6006 2006-06-06 10:01:27Z thangvn $
 */
public class ObjectQuery extends QueryCondition
{

   private Class<?> type_;

   public ObjectQuery(Class<?> type)
   {
      type_ = type;
   }

   public String optimizeInputString(String value)
   {
      value = value.replace('*', '%');
      return value;
   }

   public String getHibernateQuery()
   {
      StringBuffer b = new StringBuffer();
      b.append("from o in class ").append(type_.getName());
      buildJoin(b);
      if (parameters_.size() > 0 || orClauses.size() > 0) {
        b.append(" WHERE ");
        buildCondition(b, this);        
      }
      if (orderBy_ != null)
         b.append(orderBy_);
      return b.toString();
   }

  /**
    * 
    * @return
    */
   public String getHibernateQueryWithBinding()
   {
      StringBuffer b = new StringBuffer();
      b.append("from o in class ").append(type_.getName()).append(" as ").append(type_.getSimpleName());
      buildJoin(b);
      if (parameters_.size() > 0 || orClauses.size() > 0) 
      {
        b.append(" WHERE ");
        buildConditionWithBinding(b, this);
      }
      if (orderBy_ != null)
      {
         b.append(orderBy_);
      }

      return b.toString();
   }
   
   private void buildCondition(StringBuffer b, QueryCondition query) 
   {
     if (query.parameters_.size() > 0)
     {
        for (int i = 0; i < query.parameters_.size(); i++)
        {
           if (i > 0)
              b.append(" AND ");
           Parameter p = query.parameters_.get(i);
           if (p.value_ instanceof String)
           {
              if (p.field_.startsWith("UPPER") || p.field_.startsWith("LOWER"))
              {
                 b.append(p.field_).append(p.op_).append("'").append(p.value_).append("'");
              }
              else
              {
                 b.append(" o.").append(p.field_).append(p.op_).append("'").append(p.value_).append("'");
              }
           }
           else if (p.value_ instanceof Date)
           {
              SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
              String value = ft.format((Date)p.value_);
              b.append(" o.").append(p.field_).append(p.op_).append("'").append(value).append("'");
           }
           else
           {
              b.append(" o.").append(p.field_).append(p.op_).append(p.value_);
           }
        }
        if (query.orClauses != null && !query.orClauses.isEmpty()) 
        {
          b.append(" AND (");
          buildOrClause(b, query.orClauses, false);
          b.append(") ");
        }
     }
     else 
     {
        buildOrClause(b, query.orClauses, false);
     }
  }

   private void buildConditionWithBinding(StringBuffer b, QueryCondition query) 
   {
     if (query.parameters_.size() > 0)
     {        
        for (int i = 0; i < query.parameters_.size(); i++)
        {
           if (i > 0)
              b.append(" AND ");
           Parameter p = query.parameters_.get(i);
           if (p.value_ instanceof String)
           {
              if (p.field_.startsWith("UPPER") || p.field_.startsWith("LOWER"))
              {
                 b.append(p.field_).append(p.op_).append(":").append(p.field_.substring(6, p.field_.length() - 1))
                    .append(i);
              }
              else
              {
                 b.append(" o.").append(p.field_).append(p.op_).append(":").append(p.field_).append(i);
              }
           }
           else if (p.value_ instanceof Date)
           {
              b.append(" o.").append(p.field_).append(p.op_).append(":").append(p.field_).append(i);
           }
           else
           {
              b.append(" o.").append(p.field_).append(p.op_).append(p.value_);
           }
        }
        if (query.orClauses != null && !query.orClauses.isEmpty()) 
        {
          b.append(" AND (");
          buildOrClause(b, query.orClauses, true);
          b.append(") ");
        }
     }
     else 
     {
        buildOrClause(b, query.orClauses, true);
     }
   }

   private void buildOrClause(StringBuffer b, List<QueryCondition> orClauses, boolean withBinding) 
   {
     if (orClauses != null) 
     {
       for (int i = 0; i < orClauses.size(); i++) {
         if (i > 0) 
         {
           b.append(" OR ");              
         }
         b.append("(");
         if (withBinding) {
           buildConditionWithBinding(b, orClauses.get(i));           
         } else {
           buildCondition(b, orClauses.get(i));
         }
         b.append(") ");
       }       
     }
  }

  private void buildJoin(StringBuffer b) 
  {
     for (Class<?> clazz : joins.keySet()) {
       b.append(" INNER JOIN ").append(type_.getSimpleName()).append(".memberships");
//       b.append(" as ").append(joins.get(clazz));
     }
  }

  public String getHibernateGroupByQuery()
   {
      StringBuffer b = new StringBuffer();
      b.append("select ");
      if (selectParameter_.size() > 0)
      {
         for (int i = 0; i < selectParameter_.size(); i++)
         {
            Parameter p = selectParameter_.get(i);
            if (p.op_.equals("fieldselect"))
            {
               b.append("o.").append(p.field_);
            }
            else if (p.op_.equals("countselect"))
            {
               b.append("COUNT");
               if (!(p.field_.equals("")) || p.field_.length() > 0)
               {
                  b.append("(").append(p.field_).append(" o)");
               }
               else
               {
                  b.append("(o)");
               }
            }
            else
            {
               b.append(p.op_).append("(").append("o.").append(p.field_).append(") ");
            }
            if (i < selectParameter_.size() - 1)
               b.append(" , ");
         }
      }
      b.append(" from o in class ").append(type_.getName());
      if (parameters_.size() > 0)
      {
         b.append(" where ");
         for (int i = 0; i < parameters_.size(); i++)
         {
            if (i > 0)
               b.append(" and ");
            Parameter p = parameters_.get(i);
            if (p.value_ instanceof String)
            {
               b.append(" o.").append(p.field_).append(p.op_).append("'").append(p.value_).append("'");
            }
            else if (p.value_ instanceof Date)
            {
               SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
               String value = ft.format((Date)p.value_);
               b.append(" o.").append(p.field_).append(p.op_).append("'").append(value).append("'");
            }
            else if (p.op_.equals("MAX") || p.op_.equals("MIN"))
            {
               b.append(p.op_).append("(").append("o.").append(p.field_).append(") ");
            }
            else
            {
               b.append(" o.").append(p.field_).append(p.op_).append(p.value_);
            }
         }
      }
      if (groupBy_ != null)
         b.append(groupBy_);
      if (orderBy_ != null)
         b.append(orderBy_);
      return b.toString();
   }

   public String getHibernateCountQuery()
   {
      StringBuffer b = new StringBuffer();
      b.append("SELECT COUNT(o) FROM o IN CLASS ").append(type_.getName());
      buildJoin(b);
      if (parameters_.size() > 0 || orClauses.size() > 0) 
      {
        b.append(" WHERE ");
        buildCondition(b, this);
      }
      return b.toString();
   }

   /**
    * 
    * @return
    */
   public String getHibernateCountQueryWithBinding()
   {
      StringBuffer b = new StringBuffer();
      b.append("SELECT COUNT(o) FROM o IN CLASS ").append(type_.getName());      
      buildJoin(b);
      if (parameters_.size() > 0 || orClauses.size() > 0) 
      {
        b.append(" WHERE ");
        buildConditionWithBinding(b, this);        
      }
      return b.toString();
   }
}
