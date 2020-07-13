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

package choucas.perdido.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.google.gson.stream.JsonReader;

import choucas.perdido.elements.Toponyme;

import choucas.perdido.elements.Verb;
import choucas.perdido.postgresql.Postgis;
import choucas.perdido.tools.GazetteerException;
import choucas.perdido.tools.XmlTools;
import choucas.perdido.disambiguation.Subtyping;
import choucas.perdido.resolution.ToponymResolution;



/**
 * ParsingPerdidoTEI class : provide some methods for 
 * @author Ludovic Moncla
 * @version 1.0
 */
public class ParsingPerdidoTEI {
	
	
	private String _lang = null;
	private String _inputFile = null;
	
	private Vector<Toponyme> _toponyms = null;
	private Vector<Toponyme> _toponymsTmp = null; //est ecrasé à chaque nouveau Name trouvé.
	
	private int _cptES = 0;
	
	ToponymResolution _tr = null;
	
	
			
	
	public ParsingPerdidoTEI(String inputFile, String lang,boolean doStrictQuery, int maxResults, boolean geonames, boolean ign, boolean osm,String[] ign_bdd_tables, String geom, Postgis objPostgis, String geonamesAPIkey) 
	{
		_inputFile = inputFile;
		_lang = lang;
		
		_toponyms = new Vector<Toponyme>();
		_toponymsTmp = new Vector<Toponyme>();
		
		_tr = new ToponymResolution(doStrictQuery, maxResults, lang, geonames, ign, osm, ign_bdd_tables, geom, objPostgis, geonamesAPIkey);
	}
	
	
	
	

	/**
	 * parseOutputUnitex : parse the output of Unitex (XML)
	 * @param input 		path of the XML file to parse	
	 */
	public Vector<Toponyme> execute()  throws Exception{
	
		System.err.println("-- execute ParsinPerdidoTEI");
		//System.err.println(" LIBRARY OK");
		
		
		SAXBuilder sxb = new SAXBuilder();
		Document document = sxb.build(new File(_inputFile));
		
		//Element racine = document.getRootElement();
		
		ElementFilter eFilter = new ElementFilter("name", null ); 
		Iterator<Element> iName = document.getRootElement().getDescendants(eFilter); //Gets the requested elements. 
		
		
		while (iName.hasNext())
		{
			
			double prec = 0.0;
			
			Element eltName = (Element) iName.next();
			
			//_vecTopoTmp.clear();
			
		
		
			boolean roadName = false;
			boolean person = false;
			
			if(eltName.hasAttributes())
			{
				if(eltName.getAttributeValue("type").equals("place"))
				{
					if(eltName.getAttributeValue("subtype").equals("roadName"))
					{
						roadName = true;
						System.out.println("  > RoadName = true;");
					}
				}
				else
				{
					if(eltName.getAttributeValue("type").equals("person"))
					{
						System.out.println("  > Person = true;");
						person = true;
					}
				}	
			}
			
			
			
			if(!roadName && !person)
			{	
					
				Verb verb = null;
				
				System.out.println(" ++ Récupération du phr ");
				//si on est dans un VT on récupère le verb de récit de voyage associé
				Element eltPhr = XmlTools.getParentElt(eltName,"phr");   //VT remplacer par phr de type V
				
				//si on est dans un phr on récupère le verbe
				if(eltPhr != null)
				{
					System.out.println("  -- phr type : "+eltPhr.getAttribute("type"));
					verb = XmlTools.getVerbTei(eltPhr);
					
					System.out.println("verb (lemma) : "+verb.getLemma());		
					prec += 0.1;
				}
				
				//si on est dans une balise origine
			//	Element eltOrig =  XmlTools.getParentElt(eltES,"origin");
				
				//si on est dans une balise destination
			//	Element eltDest =  XmlTools.getParentElt(eltES,"destination");
			
				
				String typeRs = "";
		
				Element eltRs = eltName;
				Element eltRsTmp = null;
				boolean rsExN = true;
				boolean rsRel = false;
				
				int rsLevel = 0; //niveau d'imbrication des rs (expandedName)
				
				try{
					
					while(rsExN)
					{
						System.out.println(" ++ Récupération du rs parent, level : "+rsLevel);
						eltRsTmp = XmlTools.getParentElt(eltRs,"rs");
						
						if(eltRsTmp != null)
						{
							if(eltRsTmp.hasAttributes())
							{
								typeRs = eltRsTmp.getAttributeValue("type");
								if(typeRs.equals("rel"))
								{
									//System.out.println("  -- rs de type rel");
									rsExN = false;
									rsRel = true;
									eltRs = eltRsTmp;
									rsLevel++;
								}
								else //type = expandedName
								{
									//System.out.println("  -- rs de type : "+typeRs);
									eltRs = eltRsTmp;
									rsLevel++;
								}
							}
							else
							{
								//System.out.println("  -- rs sans attribut!");
								//dans le cas d'un rs sans attribut on ne l'analyse pas on reste au niveau des expandedName ou du name
								//eltRs = eltRsTmp;
								rsExN = false;
							}
						}
						else
							rsExN = false;
					}
				}catch(Exception e){System.err.println("err : "+e.toString());}
				
				
				
				
				//dans le cas d'un rs de type relatif
				//on va analyser le rs fils de type "expandedName" si il y en a sinon on analyse le "name"
				int cptESTmp = _cptES;
				if(rsRel)
				{
					System.out.println(" ++ rs type=rel ");
					
					
					List<?> l = eltRs.getChildren();
					Iterator<?> it = l.iterator();
					while (it.hasNext()) 
					{	
						//System.out.println("debug 3");
						Element rsFils = (Element) it.next();
						
						if(rsFils.getName().equals("rs"))
						{
							if(rsFils.getAttributeValue("type").equals("expandedName"))
							{
								
								System.out.println(" ++ rsFils type=expandedName ");
								rsFils = checkExpandedName("rs",rsFils,0,verb);
							}
						}
						else
						{
							if(rsFils.getName().equals("name"))
							{
								System.out.println(" ++ rsFils : name ");
								rsFils = checkExpandedName("name",rsFils,0,verb);
							}
						}
					}
					
					
					//checkExpandedName incrémente le variable _cptES lorsqu'un ES est trouvé dans les ressources
					
					//si le eltRs à changé (on analyse les term et offset du rs rel)
					if(_cptES > cptESTmp)
					{
						System.out.println(" ++ _cptES > cptESTmp ");
					
						//on récupère la certainty du fils
						prec = getChildCertainty(eltRs);
						

						
						List<?> listTerm = eltRs.getChildren(); 
						if(listTerm != null)
						{
							Iterator<?> i = listTerm.iterator();
							while (i.hasNext()) 
							{	
								//System.out.println("debug 3");
								Element term = (Element) i.next();
								
								if(term.getName().equals("term"))
								{
									
									//String valTerm = XmlTools.getValueEltTei(term);
									String valTerm = XmlTools.getValueTermTei(term);
									System.out.println(" ++ term : "+valTerm);
									String type = term.getAttribute("type").getValue();
									
									//cas d'un type N
									if(type.equals("N"))
									{
										System.out.println("  -- test feature : "+valTerm);
										boolean isFeature = Subtyping.isFeatureGeo(XmlTools.getValueTermTei(term),_lang);
			
										if(isFeature)
										{
											prec += 0.4;
										
											//on modifie la balise en geogFeat
											term = setEltGeo(term);
										}
										else
											prec -= 0.6;
	
									}
									//cas d'un type offset
									if(type.equals("offset"))
									{
										System.out.println("  -- offset");
										term = setEltGeo(term);
										//term.setAttribute("type", term.getAttribute("subtype").getValue());
										//term.removeAttribute("subtype");
										//term.setName("offset");
										
										prec += 0.1;
									}
									//cas d'un type distance
									if(type.equals("measure"))
									{
										System.out.println("  -- measure");
										if(term.getAttributeValue("subtype").equals("distance"))
										{
											term = setEltGeo(term);
											//term.removeAttribute("type");
											//term.removeAttribute("subtype");
											//term.setName("measure");
											
											prec += 0.1;
										}
									}
								}
							}
						}
						
						if(prec >= 0.5)
							eltRs = setEltGeo(eltRs);
						//eltRs = setChildGeo(eltRs);
						eltRs = addAttCertainty(eltRs,prec);
						
					}
				}
				else //si ce n'est pas un rs @type=rel
				{
					
					if(rsLevel == 0) //dans le cas ou il n'y a pas d'rs mais directement un name
					{
						System.out.println(" ++ directement Name");
						eltRs = checkExpandedName("name",eltRs,0,verb); //met à jour le vecTopoTmp et _cptES
						
						if(_cptES > cptESTmp)
						{
							Element eltRsParent = XmlTools.getParentElt(eltRs, "rs");
							

							if(eltRsParent != null)
							{
								if(!eltRsParent.hasAttributes())
								{
									double certTmp = getChildCertainty(eltRsParent);
									
									eltRs.removeAttribute("certainty");
									
									//ajouter la precision récupérée du name
									if(certTmp >= 0.5)
										eltRsParent = setEltGeo(eltRsParent);
									
									eltRsParent = addAttCertainty(eltRsParent,certTmp);
								}
							}
						}
					}
					else //cas d'un rs expandedName
					{
						System.out.println(" ++ rs expandedName");
						eltRs = checkExpandedName("rs",eltRs,0,verb);
						if(_cptES > cptESTmp){
							
							//on récupère l'incertitude du rs expandedName
							double certTmp = Double.parseDouble(eltRs.getAttributeValue("certainty")); 
							
							
							//remonter au rs parent et si sans attribut alors le renommer
							Element eltRsParent = XmlTools.getParentElt(eltRs, "rs");
							if(eltRsParent != null){
								if(!eltRsParent.hasAttributes()){
									System.out.println(" :: le rs parent n'a pas d'attribut ");
									
									//si l'incertitude est superieur au seuil on change le nom du rs parent
									if(certTmp >= 0.5)
										eltRsParent = setEltGeo(eltRsParent);
								}
							}
						}
					}
				}
			}
		}
		
		//System.out.println("Fin boucle");
		

		/**
		 * Ajout pour l'annotation semi-automatique
		 */
		
		/*
		 * 
		 Pour l'annotation semi-automatique on ne met pas la balise certainty
		*/
		/*
		document = addCertainty(document, "geogName");
		document = addCertainty(document, "placeName");
		document = addCertainty(document, "rs");
		document = addCertainty(document, "name");
		document = addCertainty(document, "place");
		*/
		System.err.println("removeCertainty");
		
		document = removeCertainty(document, "geogName");
		document = removeCertainty(document, "placeName");
		document = removeCertainty(document, "rs");
		document = removeCertainty(document, "name");
		document = removeCertainty(document, "place");
		
		
		System.err.println("adaptName");
		
		
		document = adaptName(document);
		
		System.err.println("placeName");
		
		document = adaptPlaceName(document);
		
		
		System.err.println("geogName");
		
		document = adaptGeogName(document);
		
		System.err.println("offset");
		document = adaptOffset(document);
		
		
		
		System.err.println("end");
		
		/*while (iplaceName.hasNext()) 
		{
			
			Element placeName = (Element) iplaceName.next();
			
			
			Element eltP = new Element("precision");
			
			Attribute at = new Attribute("degree", ""+placeName.getAttribute("precision").getValue());
			eltP.setAttribute(at);
			
			placeName = addElt(placeName,eltP);
			
			
		}*/
		

		XMLOutputter output = new XMLOutputter();
		output.output(document, new FileOutputStream(_inputFile));

		
		
		/*
		
		String resultsStatChain = _documentName+";";
		for(int i=0;i<_tabStatsToponyms.length;i++)
		{
			resultsStatChain += _tabStatsToponyms[i]+";";
		}
		
		FileTools.updateStat(statsFileToponyms,resultsStatChain);
	
		
		for(int i=0;i<_listTopo.size();i++)
		{
			FileTools.updateFile(statsFileToponyms2, _listTopo.get(i));
		}
		
		*/
		
	
		System.out.println("End parseOutputUnitexTei");		
		
		
		return _toponyms;
	}
	
	
	
	public Document adaptOffset(Document document)
	{
	
		ElementFilter filter2 = new ElementFilter("term", null ); 
		Iterator<Element> iElt = document.getRootElement().getDescendants(filter2); //Gets the requested elements. 
		
		List<Element> result = new ArrayList<Element>();
		while (iElt.hasNext()){
		    result.add(iElt.next()); 
		}
		
				
		int cpt = 0;
		for (Element elt : result) 
		{
			elt = setEltGeo(elt);
		}
		
		return document;
	}
	
	
	
	public Document adaptPlaceName(Document document)
	{
	
		ElementFilter filter2 = new ElementFilter("placeName", null ); 
		Iterator<Element> iplaceName = document.getRootElement().getDescendants(filter2); //Gets the requested elements. 
		
		List<Element> result = new ArrayList<Element>();
		while (iplaceName.hasNext()){
		    result.add(iplaceName.next());
		}
		
		int cpt = 0;
		for (Element eltPlaceName : result) 
		{
			
			//Element eltPlaceName = (Element) iplaceName.next();
			
			
			eltPlaceName.setAttribute(new Attribute("id", "ene."+cpt,Namespace.XML_NAMESPACE));
			cpt++;
			
			
			
			
			int nbElt = countElt(eltPlaceName, "placeName");
			
			eltPlaceName.setAttribute(new Attribute("n", ""+nbElt));
			//faire une fonction récursive qui compte les placeName imbriqués
			
			eltPlaceName.setAttribute(new Attribute("ref", ""));
			
			
		}
		
		
		return document;
	}
	
	
	
	public Document adaptGeogName(Document document)
	{
		Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
		ElementFilter filter2 = new ElementFilter("geogName", null ); 
		Iterator<Element> it = document.getRootElement().getDescendants(filter2); //Gets the requested elements. 
		
		List<Element> result = new ArrayList<Element>();
		while (it.hasNext()){
		    result.add(it.next());
		}
		
		//int cpt = 0;
		for (Element elt : result) 
		{
			
			String type = "";
			String subtype = "";
			//System.out.println("elt : "+elt.getValue());
			
			if(elt.getChild("geogFeat",namespace) != null)
			{
				String term = XmlTools.getValueTermTei(elt.getChild("geogFeat",namespace));
				System.out.println("geogFeat : "+term);
				
				
				
				if(term.contains("hôtel"))
				{
					type = "S";
					subtype = "HTL";
				}
				if(term.contains("refuge"))
				{
					type = "S";
					subtype = "RHSE";
				}
				if(term.contains("col"))
				{
					type = "T";
					subtype = "PASS";
				}
				if(term.contains("pont"))
				{
					type = "S";
					subtype = "BDG";
				}
				if(term.contains("lac"))
				{
					type = "H";
					subtype = "LK";
				}
				if(term.contains("hameau"))
				{
					type = "P";
					subtype = "PPL";
				}
				if(term.contains("torrent"))
				{
					type = "H";
					subtype = "STM";
				}
				if(term.contains("pic"))
				{
					type = "T";
					subtype = "PK";
				}
				if(term.contains("chalet"))
				{
					type = "S";
					subtype = "HSE";
				}
				if(term.contains("église"))
				{
					type = "S";
					subtype = "CH";
				}
				if(term.contains("glacier"))
				{
					type = "H";
					subtype = "GLCR";
				}
				if(term.contains("montagne"))
				{
					type = "T";
					subtype = "MT";
				}
				if(term.contains("vallée"))
				{
					type = "T";
					subtype = "VAL";
				}
				if(term.contains("gare"))
				{
					type = "S";
					subtype = "RSTN";
				}
				if(term.contains("parking"))
				{
					type = "S";
					subtype = "PKLT";
				}
				if(term.contains("forêt"))
				{
					type = "V";
					subtype = "FRST";
				}
				if(term.contains("route"))
				{
					type = "R";
					subtype = "RD";
				}
				if(term.contains("rue") || term.contains("avenue"))
				{
					type = "R";
					subtype = "ST";
				}
			}
			
			elt.setAttribute(new Attribute("type", ""+type));
			elt.setAttribute(new Attribute("subtype", ""+subtype));
			
			
		}
		
		
		return document;
	}
	
	public Document adaptName(Document document)
	{
		
		//on ajoute les balises placeName de n="0" au dessus des name 
		Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
		
		ElementFilter filter = new ElementFilter("name", null ); 
		Iterator<Element> iName2 = document.getRootElement().getDescendants(filter); //Gets the requested elements. 
		
		
		List<Element> result = new ArrayList<Element>();
		while (iName2.hasNext()){
		    result.add(iName2.next());
		}
		//System.out.println("debug 9");
		
		for (Element eltName : result) 
		{	
		
			
			//_vecTopoTmp.clear();
			Element parent = eltName.getParentElement(); //tester si egale placeName
		
			//si c'est un roadname on ajoute les balises geogName et placeName
			
			if(eltName.getAttribute("subtype") != null)
			{
				if(eltName.getAttribute("subtype").getValue().equals("roadName"))
				{
					//ajouter une balise placeName
					Element eltPlaceName = new Element("placeName",namespace);
					
					//ajouter une balise placeName
					Element eltGeogName = new Element("geogName",namespace);
					eltGeogName.setAttribute(new Attribute("type", "R"));
					eltGeogName.setAttribute(new Attribute("subtype", "RD"));
					
					
					Element elemCopy = (Element)eltName.clone();
					elemCopy.detach();
					//destination.addContent(elemCopy);
					
					
					eltGeogName.addContent(elemCopy);
					
					//System.out.println("Suppression de la balise name");
					parent.removeChild("name",namespace);
					//parent.removeChildren("name");
					
					eltPlaceName.addContent(eltGeogName);
					
					//System.out.println("Ajout de la nouvelle balise placeName");
					parent.addContent(eltPlaceName);
				}
			}
			else
			{
				if(!parent.getName().equals("placeName"))
				{
					//ajouter une balise placeName
					Element eltNew = new Element("placeName",namespace);
					
					
					Element elemCopy = (Element)eltName.clone();
					elemCopy.detach();
					//destination.addContent(elemCopy);
					
					eltNew.addContent(elemCopy);
					
					
					//System.out.println("Suppression de la balise name");
					parent.removeChild("name",namespace);
					//parent.removeChildren("name");
					
					
					//System.out.println("Ajout de la nouvelle balise placeName");
					parent.addContent(eltNew);
					
					
				}
			}
		}
		
		return document;
	}
	
	public int countElt(Element elt, String name)
	{
		//on ajoute les balises placeName de n="0" au dessus des name 
		//Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
		
		//Element eltChild = elt.getChildren();
		int cpt = 0;
		List<?> listLss = elt.getChildren();
		Iterator<?> i = listLss.iterator();
		while (i.hasNext()) {
			Element courant = (Element) i.next();
			if(courant.getName().equals(name))
			{
				return 1 + countElt(courant, name);
			}
			else
			{
				cpt += countElt(courant, name);
			}		
		}
		return cpt;
		
		
	}
	
	
	/**
	 * Fonction récursive qui vérifie si les RS ou les NAME existent dans les gazetteers (uniquement pour les Rs de type expandedName
	 * @param typeRs : rs ou name
	 * @param elt
	 * @param level : niveau d'imbrication des Rs (0 = plus grand RS)
	 * @param verb
	 * @return
	 * @throws GazetteerException
	 * @throws Exception
	 */
	public Element checkExpandedName(String typeRs, Element elt, int level, Verb verb) throws Exception
	{
		System.out.println("-- checkExpandedName");
		
		double prec = 0.0;
		//double precTmp = 0;
		//vérifier que le nom existe dans les gazetteers
		//récupérer un liste de Toponyme
		
		String value = XmlTools.getValueEltTei(elt).trim();
		
		System.out.println("type balise : "+typeRs+"; value : "+value);
		System.out.println("level : "+level);

		
		
		//Vector<Toponyme> res = isTopo(value, verb, featureText, typeRs, level);
		Vector<Toponyme> resTopo = _tr.searchToponym(value);
		
		//searchToponym
		
		if(resTopo.isEmpty())
		{
		
			System.out.println("pas de résultats pour : "+value);
			//si il n'y a pas de résultats avec le RS complet on regarde si il y a un sous-RS et des term (@type=N)
			
			if(typeRs.equals("rs"))
			{
				//le rs n'existe pas dans la ressource
				
				//on récupère le feature "term" (si il y en a un)
				/*String featureTextTmp = "";
				List lTerm = elt.getChildren(); 
				if(lTerm != null)
				{
					Iterator i = lTerm.iterator();
					while (i.hasNext()) 
					{	
						Element term = (Element) i.next();
						
						if(term.getName().equals("term"))
						{
							String type = term.getAttribute("type").getValue();				
							//cas d'un type N
							if(type.equals("N"))
							{
								featureTextTmp = XmlTools.getValueTermTei(term);
							}
						}
					}
				}
				*/
				
				
				
				ElementFilter eFilter = new ElementFilter( "rs", null ); 
				Iterator<Element> children = elt.getDescendants(eFilter);
				if(children.hasNext())
				{
					while (children.hasNext())
					{
						prec = 0;
						int cptESTemp = _cptES;
						Element eltRs = (Element) children.next();

						
						//on recherche avec le sous-rs
						eltRs = checkExpandedName("rs",eltRs,++level,verb);
						
						//récupérer la valeur du certainty si le _cptES à changé
						if(_cptES > cptESTemp)
						{
							if(eltRs.hasAttributes())
							{
								String c = eltRs.getAttributeValue("certainty");
								
								if(c != null)
								{
									prec = Double.parseDouble(c);
								}
							}
						}
						
						boolean isG = false;
						//on regarde si ya un term pour ajouter à la certainty
						List<Element> listTerm = elt.getChildren(); 
						if(listTerm != null)
						{
							Iterator<Element> i = listTerm.iterator();
							while (i.hasNext()) 
							{	
								//System.out.println("debug 3");
								Element term = (Element) i.next();
								
								if(term.getName().equals("term"))
								{
									//cas d'un type N
									if(term.getAttribute("type").getValue().equals("N"))
									{
										
										_toponymsTmp = Subtyping.checkFeature(_toponymsTmp,XmlTools.getValueTermTei(term));
										
										boolean isFeatureMetadata = Subtyping.isSameFeature(_toponymsTmp);
									
										if(isFeatureMetadata)
										{
											System.out.println("isFeatureMetadata : true");

											prec += 0.6;

										}
										else
										{
											isG = Subtyping.isFeatureGeo(XmlTools.getValueTermTei(term),_lang);
											//isG = isFeatureGeo(term);
											if(isG)
												prec += 0.5;
											else
												prec -= 0.6;
										}
									}
								}
							}
						}
						if(prec >= 0.5)
						{
							elt = setEltGeo(elt);
							if(isG)
								elt = setTermChildGeo(elt);
						}
						elt = addAttCertainty(elt,prec);
					}
				}
				else
				{
					//si il n'y a pas d'autre RS fils on relance avec le Name
					prec = 0;
					ElementFilter filter = new ElementFilter( "name", null ); 
					Iterator<Element> names = elt.getDescendants(filter); 
					//en théorie il ne peut y avoir qu'un seul name dans un RS
					while (names.hasNext())
					{
						
						int cptESTemp = _cptES;
						Element eltName = (Element) names.next();
						
						eltName = checkExpandedName("name",eltName,++level,verb);
						
						//on récupère la valeur de la certainty du name (qui doit etre 0.3)
						if(_cptES > cptESTemp)
						{
							if(eltName.hasAttributes())
							{
								String c = eltName.getAttributeValue("certainty");
								
								if(c != null)
								{
									prec = Double.parseDouble(c);
									//elt = addAttCertainty(elt,prec);
								}
							}
						}
						boolean isG = false;
						//on regarde si ya un term pour ajouter à la certainty
						List<?> listTerm = elt.getChildren(); 
						if(listTerm != null)
						{
							Iterator<?> i = listTerm.iterator();
							while (i.hasNext()) 
							{	
								//System.out.println("debug 3");
								Element term = (Element) i.next();
								
								if(term.getName().equals("term"))
								{
									//cas d'un type N
									if(term.getAttribute("type").getValue().equals("N"))
									{
										_toponymsTmp = Subtyping.checkFeature(_toponymsTmp,XmlTools.getValueTermTei(term));
										
										boolean isFeatureMetadata = Subtyping.isSameFeature(_toponymsTmp);
										//boolean isFeatureMetadata = checkFeature(_toponymsTmp,XmlTools.getValueTermTei(term));
										if(isFeatureMetadata)
										{
											System.out.println("isFeatureMetadata : true");
											prec += 0.6;
											isG = true;
										}
										else
										{	
											isG = Subtyping.isFeatureGeo(XmlTools.getValueTermTei(term),_lang);
											//isG = isFeatureGeo(term);
												
											if(isG)
												prec += 0.5;
											else
												prec -= 0.6;
										}
									}
								}
							}
						}
						
						System.out.println("Test prec : "+prec);
						if(prec >= 0.5)
						{
							elt = setEltGeo(elt);
							if(isG)
								elt = setTermChildGeo(elt);
						}
						elt = addAttCertainty(elt,prec);
					}
				}	
			}
			else
			{
				//le name n'existe pas
				elt = addAttCertainty(elt,0);
			}
		}
		else //res is not empty
		{
			
			//mettre à jour les toponyms (verb, features, etc)
			
			System.out.println("il y a "+resTopo.size()+" résultats pour : "+value);
			
			
			
			
			_toponymsTmp.clear();
			_toponymsTmp.addAll(resTopo);
			
			System.out.println("- update toponyms");
			
			for(int i=0;i<_toponymsTmp.size();i++)
			{
				_toponymsTmp.get(i).setId(_cptES);
				_toponymsTmp.get(i).setGid(_toponyms.size()+i);
				_toponymsTmp.get(i).setVerb(verb);
			}
			
			
			_toponyms.addAll(_toponymsTmp); //on ajoute tous les toponymes
			
			//Attribute at1 = new Attribute("id", ""+_cptES,Namespace.XML_NAMESPACE);
			//elt.setAttribute(at1);
			
			//System.out.println("- set xml:id");
			
			_cptES++;	
			
			if(typeRs.equals("name"))
				prec = 0.5;
			else
				prec = 1.0; //la partie qui est trouvé est composer d'un feature + du nom

			
			// si il y a des resultats mais qu'on est pas en niveau 0 on regarde dans le Rs parent si il y a un term(@type=N) feature
			if(level>0)
			{
				elt = setEltGeo(elt);
				elt = setDescendantsGeo(elt);
				elt = addAttCertainty(elt,prec);
			}
			else // si level == 0
			{
				elt = addAttCertainty(elt,prec);
				
				System.out.println("setChildGeo");
				elt = setEltGeo(elt);   // on transforme la balise rs en geogName ou placeName
				elt = setDescendantsGeo(elt);
			}
		}
		
		System.out.println("-- Fin checkExpandedName");
		return elt;
	}
	
	
	/**
	 * 
	 * @param e
	 * @param prec
	 * @return
	 */
	public Element addAttCertainty(Element e, double prec)
	{
		
		if(prec > 1)
			prec = 1;
		if(prec < 0)
			prec = 0;
		
		String p = String.valueOf(prec);
		if(p.length() > 3)
			p = p.substring(0,3);
		Attribute at = new Attribute("certainty", ""+p);
		e.setAttribute(at);
		
		return e;
	}
	
	public Element setTermChildGeo(Element elt)
	{
	
		List<?> l = elt.getChildren();
		//Iterator j = elt.getDescendants();
		Iterator<?> i = l.iterator();
		while (i.hasNext()) 
		{
			Element e = (Element) i.next();
			String name = e.getName();
			
			if(name.equals("term"))
				e = setEltGeo(e);
			
			
		}
		
		return elt;
	}
	
	public Element setDescendantsGeo(Element elt)
	{
	
		List<?> l = elt.getChildren();
		//Iterator j = elt.getDescendants();
		Iterator<?> i = l.iterator();
		while (i.hasNext()) 
		{
			Element e = (Element) i.next();
			String name = e.getName();
			
			if(name.equals("name") || name.equals("term") || name.equals("rs"))
				e = setEltGeo(e);
			
			if(name.equals("rs"))
				e = setDescendantsGeo(e);
		}
		
		return elt;
	}
	
	public Element setEltGeo(Element elt)
	{
		//System.out.println("Debut setEltGeo");
		
		String name = elt.getName();
		//System.out.println("Balise : "+name);
		
		
		//si c'est une balise name ->
		if(name.equals("name"))
		{

		}
		
		//si c'est une balise term -> geogFeat
		if(name.equals("term"))
		{
			String type = elt.getAttributeValue("type");
			if(type.equals("N"))
			{
				elt.removeAttribute("type");
				elt.setName("geogFeat");
			}
			
			if(type.equals("offset"))
			{
				
				if(elt.getAttribute("subtype").getValue().equals("directionFinal"))
				{
					elt.setAttribute("type", "direction");
					elt.setAttribute("subtype", "final");
				}
				else if(elt.getAttribute("subtype").getValue().equals("directionInitial"))
				{
					elt.setAttribute("type", "direction");
					elt.setAttribute("subtype", "initial");
				}
				else
				{
					elt.setAttribute("type", elt.getAttribute("subtype").getValue());
					elt.removeAttribute("subtype");
				}
				elt.setName("offset");
				
			}
			//cas d'un type distance
			if(type.equals("measure"))
			{
				if(elt.getAttributeValue("subtype").equals("distance"))
				{
					elt.removeAttribute("type");
					elt.removeAttribute("subtype");
					elt.setName("measure");
				}
			}
		}
		
		//si c'est une balise rs -> placeName
		if(name.equals("rs"))
		{
			if(elt.hasAttributes())
			{
				if(elt.getAttributeValue("type").equals("expandedName"))
				{
					elt.removeAttribute("type");
					elt.setName("geogName");
				}
				else
				{
					if(elt.getAttributeValue("type").equals("rel"))
					{
						elt.removeAttribute("type");
						elt.removeAttribute("subtype");
						elt.setName("placeName");
					}
					else
					{
						if(elt.getAttributeValue("type").equals("sequence"))
						{
							//elt.removeAttribute("type");
							//elt.removeAttribute("subtype");
							elt.setName("place");
						}
					}
				}
			}
			else
			{
				elt.setName("placeName");
			}
			
			
				
		}
		
		//System.out.println("Fin setEltGeo");
		return elt;
		
	}
	
	
	

	public double getChildCertainty(Element elt)
	{
		//System.out.println("Debut getChildCertainty");
		
		String certainty = "";
		
		List<Element> l = elt.getChildren();
		//Iterator j = elt.getDescendants();
		Iterator<Element> i = l.iterator();
		while (i.hasNext()) 
		{
			Element e = (Element) i.next();
			
			if(e.hasAttributes())
			{
				certainty = e.getAttributeValue("certainty");
				//System.out.println("Name : "+e.getName()+" certainty : "+certainty);
			}
		}
		
		//System.out.println("Fin getChildCertainty");
		if(certainty == null)
			return -1;
		return Double.parseDouble(certainty);
	}
	
	public Document removeCertainty(Document document, String elt)
	{
		ElementFilter eFilter2 = new ElementFilter(elt, null); 
		Iterator<Element> iElt = document.getRootElement().getDescendants(eFilter2); //Gets the requested elements. 
		
		while (iElt.hasNext()){
			Element e = (Element) iElt.next();
			
			if(e.getAttribute("certainty") != null)
			{
				e.removeAttribute("certainty");
			}
			
		}
		

		return document;
		
	}
	
	/**
	 * Récupére le certainty dans l'attribut et créer la balise
	 * @param document
	 * @param elt
	 * @return
	 */
	public Document addCertainty(Document document, String elt)
	{
		//System.out.println("Debut addCertainty : "+elt);
		
		Namespace namespace = Namespace.getNamespace("http://www.tei-c.org/ns/1.0");
		
		ElementFilter eFilter2 = new ElementFilter(elt, null); 
		Iterator<Element> iplaceName = document.getRootElement().getDescendants(eFilter2); //Gets the requested elements. 
		
		//System.out.println("debug 8");
					
		List<Element> result = new ArrayList<Element>();
		while (iplaceName.hasNext()){
		    result.add(iplaceName.next());
		}
		//System.out.println("debug 9");
		
		for (Element s : result) 
		{	 
			if(s.getName().equals("name"))
			{
				//s.removeAttribute("certainty");
				//System.out.println(" -- s.getName().equals(name)");
				
				if(s.hasAttributes())
				{
					
					//System.out.println(" -- s.hasAttributes()");
					String degree = s.getAttributeValue("certainty");
					//si il y a un attribut certainty dans une balise name alors on ajoute une balise placeName au dessus
					if(degree != null)
					{
						//System.out.println(" -- degree != null");
						Element eltCertainty = new Element("certainty",namespace);
						Element eltNew = null;//new Element("placeName",namespace);
						
						//on met l'element name dans le nouvel élément placeName
						
						eltCertainty.setAttribute("locus", "name");
						
						if(Double.parseDouble(degree) < 0.5)
						{
							eltCertainty.setAttribute("assertedValue", "placeName");
							eltNew = new Element("rs",namespace);
						}
						else
						{
							eltCertainty.setAttribute("assertedValue", "rs");
							eltNew = new Element("placeName",namespace);
						}
						
						eltCertainty.setAttribute("degree", degree);
						//on rajoute l'élément certainty
						
						System.out.println(" -- ");
						
						Element parent = s.getParentElement();
						
						eltNew.addContent(eltCertainty);
						//System.out.println(" -- eltPlaceName.addContent(eltCertainty)");
						
						s.removeAttribute("certainty");
						
						Element elemCopy = (Element)s.clone();
						elemCopy.detach();
						//destination.addContent(elemCopy);
						
						eltNew.addContent(elemCopy);
						System.out.println(" -- eltPlaceName.addContent(s)");
						
						//System.out.println("Suppression de la balise name");
						parent.removeChild("name",namespace);
						//parent.removeChildren("name");
						
						
						//System.out.println("Ajout de la nouvelle balise placeName");
						parent.addContent(eltNew);
						
					}
				}
				
			}
			else
			{
			
				Element eltP = new Element("certainty",namespace);
				String degree = "";
				
				try{
					eltP.setAttribute("target", "#"+s.getAttribute("id",Namespace.XML_NAMESPACE).getValue());
				}catch (Exception e){
					//System.err.println("target id :"+e.toString());
				}
				
				try{
	
					degree = s.getAttribute("certainty").getValue();
					
					eltP.setAttribute("locus", "name");
					
					
					
					if(Double.parseDouble(degree) < 0.5)
						eltP.setAttribute("assertedValue", "placeName");
					else
						eltP.setAttribute("assertedValue", "rs");
					
					
					eltP.setAttribute("degree", degree);
					
					s.removeAttribute("certainty");
					s.addContent(0,eltP);
					//eltP.removeAttribute("xmlns");
					
				}catch (Exception e){
					//System.err.println("certainty does not exist :"+e.toString());
				}
			}
		}
		
		//System.out.println("Fin addCertainty");
		
		return document;
	}
	
	/**
	 * loadMarkers loads toponyms from a JSON file
	 * @param toponyms			Json file uri
	 */
	public static Vector<Toponyme> loadToponymsFromJson(String jsonFile) throws Exception
	{
		//System.out.println(" Begin loadTopo");
		
		Vector<Toponyme> toponyms = new Vector<Toponyme>(); 
		
		
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
		
		reader.beginArray();
	    while (reader.hasNext()) {
	    	String title = "" ,description = "",id="",idPath="", src="", polarite ="", verbe="", vlemma ="", findName= "",country="",continent="",feature="", featureText="", clcCode="";
	    	double lat=0,lng = 0,elevation = 0;
	    	int cluster = 0, nb=1,localise = -1, perception = 0, negation = 0;
	    	int relTemporal = -1;
	    	String relSpatial = "";
	    	reader.beginObject();
    	   
	    	while (reader.hasNext()) {
	    	 
	    		String name = reader.nextName();
	    		if (name.equals("title")) {
				   title = reader.nextString();
				 } else if (name.equals("id")) {
					    id = reader.nextString();
				 } else if (name.equals("idPath")) {
					    idPath = reader.nextString();
				 } else if (name.equals("lat")) {
				        lat = reader.nextDouble();
				 } else if (name.equals("lng")) {
				    lng = reader.nextDouble();
				 } else if (name.equals("description")) {
				       description = reader.nextString();
				 } else if (name.equals("polarite")) {
				       polarite = reader.nextString(); 
				 } else if (name.equals("verbe")) {
					 	verbe = reader.nextString();
				 } else if (name.equals("vlemma")) {
					 	vlemma = reader.nextString();
				 } else if (name.equals("src")) {
				       src = reader.nextString();
				 } else if (name.equals("nb")) {
				       nb = reader.nextInt();
				 } else if (name.equals("perception")) {
				       perception = reader.nextInt();
				 } else if (name.equals("negation")) {
				       negation = reader.nextInt();
				 } else if (name.equals("findName")) {
				       findName = reader.nextString();
				 } else if (name.equals("country")) {
				       country = reader.nextString();
				 } else if (name.equals("continent")) {
				       continent = reader.nextString();
				 } else if (name.equals("feature")) {
				       feature = reader.nextString();
				 } else if (name.equals("featureText")) {
				       featureText = reader.nextString();
				 } else if (name.equals("localise")) {
				       localise = reader.nextInt();
				 } else if (name.equals("cluster")) {
				       cluster = reader.nextInt();
				 } else if (name.equals("elevation")) {
				       elevation = reader.nextDouble();
				 } else if (name.equals("clc")) {
					 clcCode = reader.nextString();
				 } else if (name.equals("relSpatial")) {
					 relSpatial = reader.nextString();
				 } else if (name.equals("relTemporal")) {
					 relTemporal = reader.nextInt();
				 }else{
				     reader.skipValue();
				 }
		         
	    	   }
	    	   reader.endObject();

    	   
	    	   if(!id.isEmpty())
	    	   {
	    		   
	    		   //System.out.println("id : "+id+" Name : "+title);
	    		   Verb verb = new Verb(verbe,"",polarite,vlemma);
		    	 //cptMarker++;
			     //System.out.println("id : "+id+" point : "+title+" lat : "+lat+" lng : "+lng+" src : "+src);
	    		   Toponyme t = new Toponyme(Integer.parseInt(id),Integer.parseInt(idPath),findName,title,verb,lat,lng,src,nb,country,continent,feature,featureText,localise,cluster,false);
	    		   t.setPerception(perception);
	    		   t.setNegation(negation);
		    	 
	    		   t.setElevation(elevation);
	    		   t.setClcCode(clcCode);
		    	 
	    		   t.setRelSpatial(relSpatial);
	    		   t.setRelTemporal(relTemporal);
			    
	    		   toponyms.add(t);
	    	   }
		    }
		    reader.endArray();
		    
		    reader.close();
	 
	    return toponyms;
	    //System.out.println(" End loadTopo");
	}
	
	
	/**
	 * vector2Bdd : add the vector of toponym in postgis database
	 * @param tableName 		name of the table of the postgis database
	 * @param toponyms			vector of toponyms
	 */
	protected void vector2Bdd(String tableName, Vector<Toponyme> toponyms, Postgis objPostgis){
	
		System.out.println("### CREATE POSTGIS DATABASE ###");
	
		try {
			//_objPostgis.connect("outputDemo");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
	}
	
	

}
