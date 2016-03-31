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
package org.exoplatform.services.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by The eXo Platform SAS . Author : Tuan Nguyen
 * tuan08@users.sourceforge.net Date: Jun 14, 2003 Time: 1:12:22 PM
 */
public class Query implements Serializable
{

   private String userName_;

   private String fname_;

   private String lname_;
   
   private String displayName_;

   private String email_;

   private Date from_;

   private Date to_;
   
   private Set<MembershipQuery> memberhipsQuery_;

   public Query()
   {
   }

   public String getUserName()
   {
      return userName_;
   }

   public void setUserName(String s)
   {
      userName_ = s;
   }

   public String getFirstName()
   {
      return fname_;
   }

   public void setFirstName(String s)
   {
      fname_ = s;
   }

   public String getLastName()
   {
      return lname_;
   }

   public void setLastName(String s)
   {
      lname_ = s;
   }

   public String getEmail()
   {
      return email_;
   }

   public void setEmail(String s)
   {
      email_ = s;
   }

   public Date getFromLoginDate()
   {
      return from_;
   }

   public void setFromLoginDate(Date d)
   {
      from_ = d;
   }

   public Date getToLoginDate()
   {
      return to_;
   }

   public void setToLoginDate(Date d)
   {
      to_ = d;
   }

   public String getDisplayName() {
    return displayName_;
  }

  public void setDisplayName(String displayName) {
    this.displayName_ = displayName;
  }

  public Set<MembershipQuery> getMemberhipQuery() 
   {
      return memberhipsQuery_;
   }

  public void setMemberhipQuery(Set<MembershipQuery> memberhipQuery) 
  {
      this.memberhipsQuery_ = memberhipQuery;
  }

  public boolean isEmpty()
   {
      return email_ == null && fname_ == null && from_ == null && lname_ == null && to_ == null && userName_ == null && memberhipsQuery_ == null;
   }
  
  public static class MembershipQuery {
    private String groupId;
    
    private String membershipType;

    public MembershipQuery(String groupId, String membershipType) {
      this.groupId = groupId;
      this.membershipType = membershipType;
    }

    public String getGroupId() {
      return groupId;
    }

    public String getMembershipType() {
      return membershipType;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((membershipType == null) ? 0 : membershipType.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MembershipQuery other = (MembershipQuery) obj;
      if (groupId == null) {
        if (other.groupId != null)
          return false;
      } else if (!groupId.equals(other.groupId))
        return false;
      if (membershipType == null) {
        if (other.membershipType != null)
          return false;
      } else if (!membershipType.equals(other.membershipType))
        return false;
      return true;
    }    
  }
}
