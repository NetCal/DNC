/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.network.server_graph;

import java.util.Objects;

public class Turn {
    private int id;
    private String alias;
    private Server src;
    private Server dest;

    protected Turn(int id, String alias, Server source, Server destination) {
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
        if (obj == null || !(obj instanceof Turn)) {
            return false;
        }

        Turn l = (Turn) obj;
		return Objects.equals(this.src, l.src) && 
				Objects.equals(this.dest, l.dest);
    }

    @Override
    public int hashCode() {
		return Objects.hash(src, dest);
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // String Conversions
    // --------------------------------------------------------------------------------------------------------------

    private StringBuffer commonStringPrefix() {
    	StringBuffer turn_str_prefix = new StringBuffer();

     	turn_str_prefix.append("Turn(");
     	turn_str_prefix.append(alias);
     	turn_str_prefix.append(", ");
     	turn_str_prefix.append(Integer.toString(id));
     	
     	return turn_str_prefix;
    }
    
    public String toShortString() {
    	StringBuffer turn_str = commonStringPrefix();
    	
    	turn_str.append(")");
    	
        return turn_str.toString();
    }

    @Override
    public String toString() {
    	StringBuffer turn_str = commonStringPrefix();

    	turn_str.append(", ");
     	turn_str.append(src.toShortString());
     	turn_str.append(", ");
     	turn_str.append(dest.toShortString());
     	turn_str.append(")");
     	
     	return turn_str.toString();
    }

    public String toExtendedString() {
    	StringBuffer turn_str = commonStringPrefix();

    	turn_str.append(", ");
     	turn_str.append(src.toExtendedString());
     	turn_str.append(", ");
     	turn_str.append(dest.toExtendedString());
     	turn_str.append(")");
     	
     	return turn_str.toString();
    }
}
