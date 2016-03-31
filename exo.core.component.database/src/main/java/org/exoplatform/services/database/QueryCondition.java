/*
 * Copyright (C) 2016 eXo Platform SAS.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryCondition
{
   protected String orderBy_;

   protected String groupBy_;

   protected List<Parameter> parameters_;

   protected List<Parameter> selectParameter_;
   
   protected List<QueryCondition> orClauses;
   
   protected Map<Class<?>, String> joins;

   public QueryCondition()
   {
      parameters_ = new ArrayList<Parameter>(3);
      selectParameter_ = new ArrayList<Parameter>(10);
      orClauses = new ArrayList<QueryCondition>();
      joins = new HashMap<Class<?>, String>();
   }

   public QueryCondition addEQ(String field, Object value)
   {
      if (value != null)
      {
         parameters_.add(new Parameter(" = ", field, value));
      }
      return this;
   }

   public QueryCondition addGT(String field, Object value)
   {
      if (value != null)
      {
         parameters_.add(new Parameter(" > ", field, value));
      }
      return this;
   }

   public QueryCondition addLT(String field, Object value)
   {
      if (value != null)
      {
         parameters_.add(new Parameter(" < ", field, value));
      }
      return this;
   }

   public QueryCondition addLIKE(String field, String value)
   {
      if (value != null && value.length() > 0)
      {
         parameters_.add(new Parameter(" LIKE ", field, optimizeInputString(value)));
      }
      return this;
   }

   public String optimizeInputString(String value)
   {
      value = value.replace('*', '%');
      return value;
   }

   public QueryCondition addSUM(String field)
   {
      selectParameter_.add(new Parameter("SUM", field));
      return this;
   }

   public QueryCondition addSelect(String field)
   {
      selectParameter_.add(new Parameter("FIELDSELECT", field));
      return this;
   }

   public QueryCondition addSelectCount(String type)
   {
      selectParameter_.add(new Parameter("COUNTSELECT", type));
      return this;
   }

   public QueryCondition addSelectMaxMin(String op, String field)
   {
      selectParameter_.add(new Parameter(op, field));
      return this;
   }

   public QueryCondition setGroupBy(String field)
   {
      groupBy_ = " GROUP BY o." + field;
      return this;
   }

   public QueryCondition setAscOrderBy(String field)
   {
      orderBy_ = " ORDER BY o." + field + " asc";
      return this;
   }

   public QueryCondition setDescOrderBy(String field)
   {
      orderBy_ = " ORDER BY o." + field + " desc";
      return this;
   }
   
   public QueryCondition or(QueryCondition query) {
     this.orClauses.add(query);
     return this;
   }
   
   public QueryCondition join(Class<?> clazz, String alias) {
     joins.put(clazz, alias);
     return this;
   }

   /**
    * 
    * @return
    */
   public Map<String, Object> getBindingFields()
   {
      Map<String, Object> binding = new HashMap<String, Object>();

      if (parameters_.size() > 0)
      {
         for (int i = 0; i < parameters_.size(); i++)
         {
            Parameter p = parameters_.get(i);
            if (p.value_ instanceof String)
            {
               if (p.field_.startsWith("UPPER") || p.field_.startsWith("LOWER"))
               {
                  binding.put(p.field_.substring(6, p.field_.length() - 1) + i, p.value_);
               }
               else
               {
                  binding.put(p.field_ + i, p.value_);
               }
            }
            else if (p.value_ instanceof Date)
            {
               binding.put(p.field_ + i, p.value_);
            }
         }
      }

      return binding;
   }

   protected static class Parameter
   {
      String op_;

      String field_;

      String label_;

      Object value_;

      Parameter(String op, String field, Object value)
      {
         op_ = op;
         field_ = field;
         value_ = value;
      }

      Parameter(String op, String field)
      {
         op_ = op;
         field_ = field;
      }
   }
}
