/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.1 "Centaur".
 *
 * Copyright (C) 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.minplus;

import java.util.Set;

/**
 * 
 * @author Steffen Bondorf
 *
 */
public class OperatorInputChecks {
	/**
	 * @param obj1
	 * @param obj2
	 * @return 0 == none of the objects is null, <br/>
	 * 		   1 == the first object is null, <br/>
	 * 		   2 == the second object is null, <br/>
	 * 		   3 == both objects is null.
	 */
	public static int inputNullCheck( Object obj1, Object obj2 ) {
		// Usually neither is null so this initial check promises best overall performance.
		if ( obj1 != null && obj2 != null ) {
			return 0;
		}
		
		int return_value = 0;
		
		if ( obj1 == null ) {
			return_value += 1;
        }
    	if ( obj2 == null ) {
    		return_value += 2;
        }
    	
    	return return_value;
	}
	
	/**
	 * @param set1
	 * @param set
	 * @return 0 == none of the sets is empty, <br/>
	 * 		   1 == the first sets is empty, <br/>
	 * 		   2 == the second sets is empty, <br/>
	 * 		   3 == both sets is empty.
	 */
	@SuppressWarnings("rawtypes")
	public static int inputEmptySetCheck( Set set1, Set set2 ) {
		// Usually neither is empty so this initial check promises best overall performance.
		if ( !set1.isEmpty() && !set2.isEmpty() ) {
			return 0;
		}
		
		int return_value = 0;
		
		if ( set1.isEmpty() ) {
			return_value += 1;
        }
    	if ( set2.isEmpty() ) {
    		return_value += 2;
        }
    	
    	return return_value;
	}
}
