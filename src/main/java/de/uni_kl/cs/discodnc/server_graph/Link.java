/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc.server_graph;

public class Link {
    private int id;
    private String alias;
    private Server src;
    private Server dest;

    protected Link(int id, String alias, Server source, Server destination) {
        this.id = id;
        this.alias = alias;
        src = source;
        dest = destination;
    }

    public Server getSource() {
        return src;
    }

    public Server getDest() {
        return dest;
    }

    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Link)) {
            return false;
        }

        Link l = (Link) obj;
        return (this.src != null ? this.src.equals(l.src) : l.src == null)
                && (this.dest != null ? this.dest.equals(l.dest) : l.dest == null);
    }

    @Override
    public int hashCode() {
        return (int) src.hashCode() * dest.hashCode();
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // String Conversions
    // --------------------------------------------------------------------------------------------------------------

    private StringBuffer commonStringPrefix() {
    	StringBuffer link_str_prefix = new StringBuffer();

     	link_str_prefix.append("Link(");
     	link_str_prefix.append(alias);
     	link_str_prefix.append(", ");
     	link_str_prefix.append(Integer.toString(id));
     	
     	return link_str_prefix;
    }
    
    public String toShortString() {
    	StringBuffer link_str = commonStringPrefix();
    	
    	link_str.append(")");
    	
        return link_str.toString();
    }

    @Override
    public String toString() {
    	StringBuffer link_str = new StringBuffer();

     	link_str.append(",");
     	link_str.append(src.toShortString());
     	link_str.append(", ");
     	link_str.append(dest.toShortString());
     	link_str.append(")");
     	
     	return link_str.toString();
    }

    public String toExtendedString() {
    	StringBuffer link_str = new StringBuffer();

     	link_str.append(",");
     	link_str.append(src.toExtendedString());
     	link_str.append(", ");
     	link_str.append(dest.toExtendedString());
     	link_str.append(")");
     	
     	return link_str.toString();
    }
}
