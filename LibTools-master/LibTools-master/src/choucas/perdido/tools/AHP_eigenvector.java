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

package choucas.perdido.tools;

import java.util.Vector;



/**
 * AHP_eigenvector class
 * @author Ludovic Moncla
 * @version 1.0
 */
public class AHP_eigenvector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		double[][] matrix =
		    {
		        { 1, 3, 4, 4, 4, 2, 0.33, 0.33 } ,
		        { 0.33, 1, 2, 2, 2, 0.5, 0.2, 0.2 } ,
		        { 0.25, 0.5, 1, 1, 1, 0.33, 0.17, 0.17 } ,
		        { 0.25, 0.5, 1, 1, 1, 0.33, 0.17, 0.17 } ,
		        { 0.25, 0.5, 1, 1, 1, 0.33, 0.17, 0.17 } ,
		        { 0.5, 2, 3, 3, 3, 1, 0.25, 0.25 } ,
		        { 3, 5, 6, 6, 6, 4, 1, 1 } ,
		        { 3, 5, 6, 6, 6, 4, 1, 1 }
		    };
		
		double[] coef = {0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125};
		
		
		
		/*
		 //example of the wikipedia page (to compare the results)
		 
		 double[][] matrix =
		    {
		        { 1, 4, 3, 7  } ,
		        { 0.25, 1, 0.33, 3  } ,
		        { 0.33, 3, 1, 5  } ,
		        { 0.14, 0.33, 0.2, 1  } ,
		       
		    };
		    
		    double[] coef = {0.25, 0.25, 0.25, 0.25};
		*/
		
		
		
		
		for(int k=0;k<15;k++)
		{
		
			//calcul vecteur propre
			Vector<Double> coef_tmp = eigenvector(matrix,coef);
			
			//normalisation
			
			double sum = 0;
			
			for(int i=0; i<coef_tmp.size(); i++) {
				sum += coef_tmp.get(i);
				
			}
			
			for(int i=0; i<coef_tmp.size(); i++) {
				 coef[i] = coef_tmp.get(i) / sum;
				
			}
			
			
		}
		
		
		// computation of the consistency factor
		Vector<Double> coef_tmp = eigenvector(matrix,coef);
		
		
		
		Vector<Double> inc = new Vector<Double>();
		Vector<Double> ecart = new Vector<Double>();
		
		
		
		for(int i=0; i<coef_tmp.size(); i++) {
			inc.add(coef_tmp.get(i) / coef[i]);
			ecart.add((inc.get(i) - matrix[0].length) / matrix[0].length);
		}
		
		double inconsistency = 0;
		for(int i=0; i<ecart.size(); i++) {
			inconsistency += ecart.get(i);
		}
		
		inconsistency = inconsistency / ecart.size();
		
		// print results
		
		for(int i=1; i<coef.length+1; i++) {
			  
			 System.out.println("weight c"+i+" : "+coef[i-1]);
			 
		}
		 System.out.println("inconsistency : "+inconsistency);
		
		System.out.println("************************************");
			
	
		
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param coef
	 * @return
	 */
	protected static Vector<Double> eigenvector(double[][] matrix, double[] coef )
	{
		
	
		//double[] coef_tmp = {0, 0, 0, 0, 0, 0, 0, 0};
		
		Vector<Double> coef_tmp = new Vector<Double>();
		
		for(int i=0; i<matrix.length; i++) {
			
			double tmp = 0;
			for(int j=0; j<matrix[i].length; j++) {
				
				tmp += matrix[i][j] * coef[j];
			}
			
			coef_tmp.add(tmp);
			
		}
		
		
		return coef_tmp;
		
	}
	
	
}
