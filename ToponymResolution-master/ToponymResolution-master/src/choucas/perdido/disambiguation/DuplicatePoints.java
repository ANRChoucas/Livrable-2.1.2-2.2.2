/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of ToponymResolution - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * ToponymResolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ToponymResolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ToponymResolution. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.disambiguation;

import java.util.Vector;

import choucas.perdido.tools.MapsFunctions;
import choucas.perdido.elements.Toponyme;




/**
 * DuplicatePoints class : provides some methods to remove duplicate points coming from different gazetteers
 * @author Ludovic Moncla

 */
public class DuplicatePoints {
	
	
	public DuplicatePoints() 
	{
		
	}
	
	
	/**
	 * Returns the list of toponyms without duplicate points coming from different gazetteers
	 * @param toponyms			Vector<Toponyme>
	 * @return Vector<Toponyme>
	 */
	public static Vector<Toponyme> removeDuplicatePoints(Vector<Toponyme> toponyms) 
	{
		
		boolean notAdded[] = new boolean[toponyms.size()+1];
		Vector<Toponyme> topoFilter = new Vector<Toponyme>();
		
		for(int i=0;i<toponyms.size();i++)
		{
			notAdded[i] = true;
		}
		
		for(int i=0;i<toponyms.size();i++)
		{
			if(notAdded[i])
			{
				notAdded[i] = false;
				topoFilter.add(toponyms.get(i));
				
				for(int j=0;j<toponyms.size();j++)
				{
					if(notAdded[j])
					{
						
						if(toponyms.get(i).getGid() != toponyms.get(j).getGid())
						{
							
							if(toponyms.get(i).getId() == toponyms.get(j).getId())
							{
								
								double distance = MapsFunctions.getDistance(toponyms.get(i).getLat(), toponyms.get(i).getLng(), toponyms.get(j).getLat(), toponyms.get(j).getLng());
								if(distance > 7000)
								{
									//System.err.println("distance > 7000 ("+distance+") : "+toponyms.get(i).getId()+" - "+toponyms.get(j).getId());
								//	notAdded[j] = false;
								//	topoFilter.add(toponyms.get(j));
								}
								else
								{
									notAdded[j] = false;
								}
							}
						}
					}
				}
			}
		}
		
		
		return topoFilter;
		

	}
}
