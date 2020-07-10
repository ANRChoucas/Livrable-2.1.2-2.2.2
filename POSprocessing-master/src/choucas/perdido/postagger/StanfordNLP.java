/*
 * Copyright (C) 2016 Ludovic Moncla <ludovic.moncla@univ-pau.fr>
 * 
 * This file is part of POSprocessing - Perdido project <http://erig.univ-pau.fr/PERDIDO/>
 *
 * POSprocessing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * POSprocessing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with POSprocessing.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package choucas.perdido.postagger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;



import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import choucas.perdido.tools.FileTools;
import choucas.perdido.tools.StringTools;


/**
 * Treetagger class : provides methods to execute Treetagger POS taggers
 * @author Ludovic Moncla
 * @version 1.0
 */
public class StanfordNLP extends POStagger {
	
	
	/**
	 * 
	 * @param installDirectory
	 * @param lang
	 */
	public StanfordNLP(String installDirectory, String lang)
	{
		super(installDirectory, lang, "stanfordNLP");
		
		
		
	}
	
	
	
	/**
	 * launch the Treetagger POS analyser
	 * @param inputContent				file to tag
	 * @param outputFile				path of the output file
	 * @throws Exception
	 */
	@Override
	public void run(String inputFile, String outputFile) throws Exception {

		
		
		
		
		MaxentTagger tagger = new MaxentTagger(_installDirectory);
	   /*
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
										   "untokenizable=noneKeep");
	    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
	    PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));
	    DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
	    documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
	    */
	    //MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		FileReader fileToTag= new FileReader(inputFile);
		//Morphology(new Reader(fileToTag));
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(fileToTag));
		Morphology stemmer = new Morphology();
	    
	    for (List<HasWord> sentence : sentences) 
	    {
	    	//tagger.lemmatize(sentence, null);
			List<TaggedWord> tSentence = tagger.tagSentence(sentence);
			for(TaggedWord tw:tSentence)
		  	{
				
				FileTools.updateStat(outputFile,tw.word() + "\t" + tw.tag() + "\t" + stemmer.lemma(tw.word(), tw.tag()));
				/*
				System.out.print("Word: " + tw.word());
				System.out.print(", pos: " + tw.tag());
				System.out.println(", lemma: " +stemmer.lemma(tw.word(), tw.tag()));
				*/
			}
		}
	}
	
	
	/**
	 * 
	 * @param inputFile				path of the input file
	 * 
	 */
	@Override
	public String tagger2pivot(String inputFile) throws Exception {

		
		InputStream ips = new FileInputStream(inputFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);

		
		String outputPivot = "";
		String line = "";
		String token = "", pos = "", lemma = "";
		
		

		while ((line = br.readLine()) != null) 
		{
			if(!line.equals(""))
			{
				String str[] = line.split("\t");
	
				
				token = str[0];// .toLowerCase();
				pos = str[1];
				
				if(pos.equals("."))
					pos = "SENT";
				
				if(str.length>2) //if a lemma exists
				{
					if(str[2] != null  && !str[2].equals("<unknown>"))
					{
						String lem_tmp = str[2];
						//System.err.println("lem_tmp : " + lem_tmp);
						
						
						if(lem_tmp.contains("|")) //if several lemma, we choose the first one
						{
							
							//String c[] = lem_tmp.split("|"); // problème au niveau du split...
							//System.out.println("c[0] : " + c[0]);
							//System.out.println("c[1] : " + c[1]);
							//lemma = c[0];
							
							//System.out.println("lem "+lem_tmp.substring(0, lem_tmp.indexOf("|")));
							lemma = lem_tmp.substring(0, lem_tmp.indexOf("|"));
							
							
							//lemma = str[2];
						}
						else
						{
							//System.out.println("else");
							lemma = str[2];
						}
					}
					else
						lemma = "null";
				}
				else
					lemma = "null";
				
				
				//System.err.println("lemma : " + lemma);
				
				token = StringTools.filtreString(token);
				lemma = StringTools.filtreString(lemma);
				
			
				outputPivot += token + "\t" + pos + "\t" + lemma +"\n";
				
			}
		}
		
		br.close();
	
		return outputPivot;
	}

}


