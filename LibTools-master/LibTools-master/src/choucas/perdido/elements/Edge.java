/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of LibTools - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * LibTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LibTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LibTools. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.elements;

import java.io.Serializable;


/**
 * Edge class
 * @author Ludovic Moncla
 * @version 1.0
 */
public class Edge implements Serializable {
	 
	private Node _nodeStart = null;
    private Node _nodeEnd = null;
    private int _direction = 0; //0 start to end, 1 end to start
    
    private double _weight = 0;
    
    /* Déclaration des critères qui servent à pondérer le */
    private double _distanceHaversine = 0; //valeur du critère distance
    private double _distanceEuclidian = 0;
    private double _distanceElevation = 0;
    private double _distanceEffort = 0;
    private double _effort = 0;
    private int _textOrder = 0;
    private double _cumulativeElevation = 0;
    private double _cumulativePositiveElevation = 0;
    private double _cumulativeNegativeElevation = 0;
    private double _changeCover = 0;
    private int _temporalOrder = 0;
    private int _barrier = 0;
    private int _perception = 0;
    private int _negation = 0;
    private int _visibility = 0;
   
    private double _relElevation = 0;
    private double _relSpatial = 0;
    private int _relTemporal = 1;
    
	/**
	 * Constructor class
	 * @param nodeStart
	 * @param nodeEnd
	 */
    public Edge(Node nodeStart, Node nodeEnd)
	{
    	_nodeStart = nodeStart;
    	_nodeEnd = nodeEnd;
   
	}
    
    public boolean contains(Node node)
    {
    	if(node.getId() == _nodeStart.getId() || node.getId() == _nodeEnd.getId())
    	{
    		return true;
    	}
 
    	
    	return false;
    }
    
    public boolean contains(Node node1, Node node2)
    {
    	if(node1.getId() == _nodeStart.getId() && node2.getId() == _nodeEnd.getId())
    	{
    		return true;
    	}
 
    	if(node1.getId() == _nodeEnd.getId() && node2.getId() == _nodeStart.getId() )
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    
    public boolean firstNodeEqual(Node node)
    {
    	if(node.getId() == _nodeStart.getId())
    	{
    		return true;
    	}
 
    	return false;
    }
    
    public Node getOtherNode(Node node)
    {
    	if(node.getId() == _nodeStart.getId())
    	{
    		return _nodeEnd;
    	}
    	
    	if(node.getId() == _nodeEnd.getId())
    	{
    		return _nodeStart;
    	}
 
    	
    	return null;
    }
    
    public boolean equals(Edge edge)
    {

    	if(edge.getNodeStart().getId() == _nodeStart.getId() && edge.getNodeEnd().getId() == _nodeEnd.getId())
    	{
    		return true;
    	}
    	
    	if(edge.getNodeStart().getId() == _nodeEnd.getId() && edge.getNodeEnd().getId() == _nodeStart.getId())
    	{
    		return true;
    	}
    	
    	return false;
    }
   
    /**
     * 
     * @return
     */
    public Node getNodeStart()
    {
    	return _nodeStart;
    }

    
    public Node getNodeEnd()
    {
    	return _nodeEnd;
    }
    
    public void setDirection(int direction)
    {
    	_direction = direction;
    }
    
    public int getDirection()
    {
    	return _direction;
    }
    
    
    public void setRelTemporal(int relTemporal)
    {
    	_relTemporal = relTemporal;
    }
    
    public int getRelTemporal()
    {
    	return _relTemporal;
    }
    
    public void setRelElevation(double relElevation)
    {
    	_relElevation = relElevation;
    }
    
    public double getRelElevation()
    {
    	return _relElevation;
    }
    
    public void setRelSpatial(double relSpatial)
    {
    	_relSpatial = relSpatial;
    }
    
    public double getRelSpatial()
    {
    	return _relSpatial;
    }
    
    
    public void setVisibility(int visibility)
    {
    	_visibility = visibility;
    }
    
    public int getVisibility()
    {
    	return _visibility;
    }
    
    public void setPerception(int perception)
    {
    	_perception = perception;
    }
    
    public int getPerception()
    {
    	return _perception;
    }
    
    
    public void setNegation(int negation)
    {
    	_negation = negation;
    }
    
    public int getNegation()
    {
    	return _negation;
    }
    
    
    public void setTextOrder(int textorder)
    {
    	_textOrder = textorder;
    }
    
    public int getTextOrder()
    {
    	return _textOrder;
    }
    
    
    public void setChangeCover(double changeCover)
    {
    	_changeCover = changeCover;
    }
    
    public double getChangeCover()
    {
    	return _changeCover;
    }
    
    public void setCumulativeElevation(double CumulativeElevation)
    {
    	_cumulativeElevation = CumulativeElevation;
    }
    
    public double getCumulativeElevation()
    {
    	return _cumulativeElevation;
    }
    
    public void setCumulativeNegativeElevation(double CumulativeNegativeElevation)
    {
    	_cumulativeNegativeElevation = CumulativeNegativeElevation;
    }
    
    public double getCumulativeNegativeElevation()
    {
    	return _cumulativeNegativeElevation;
    }
    
    public void setCumulativePositiveElevation(double CumulativePositiveElevation)
    {
    	_cumulativePositiveElevation = CumulativePositiveElevation;
    }
    
    public double getCumulativePositiveElevation()
    {
    	return _cumulativePositiveElevation;
    }
    
    
    
    public void setWeight(double weight)
    {
    	_weight = weight;
    }
    
    public double getWeight()
    {
    	return _weight;
    }
    
    
    public void setDistanceHaversine(double distanceHaversine)
    {
    	_distanceHaversine = distanceHaversine;
    }
    
    public double getDistanceHaversine()
    {
    	return _distanceHaversine;
    }
    
    public void setDistanceEffort(double distanceEffort)
    {
    	_distanceEffort = distanceEffort;
    }
    
    public double getDistanceEffort()
    {
    	return _distanceEffort;
    }
    
    public void setEffort(double effort)
    {
    	_effort = effort;
    }
    
    public double getEffort()
    {
    	return _effort;
    }
    
    
    public void setDistanceEuclidian(double distanceEuclidian)
    {
    	_distanceEuclidian = distanceEuclidian;
    }
    
    public double getDistanceEuclidian()
    {
    	return _distanceEuclidian;
    }
    
    
    public void setDistanceElevation(double distanceElevation)
    {
    	_distanceElevation = distanceElevation;
    }
    
    public double getDistanceElevation()
    {
    	return _distanceElevation;
    }
    
    
}
