/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

package de.uni_kl.cs.disco.network;

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

    public String toShortString() {
        return alias;
    }

    @Override
    public String toString() {
        return "Link(" + src.toShortString() + ", " + dest.toShortString() + ")";
    }

    public String toExtendedString() {
        return "Link(" + src.toExtendedString() + ", " + dest.toExtendedString() + ")";
    }
}
