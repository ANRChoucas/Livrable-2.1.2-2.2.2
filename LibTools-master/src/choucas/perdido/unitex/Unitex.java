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

package choucas.perdido.unitex;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import choucas.perdido.tools.CallableProcess;


/**
 * Unitex class : provides methods to execute Unitex programs such as cascade of transducers (Cassys)
 * @author Ludovic Moncla
 * @version 1.0
 */
public class Unitex {
	
	protected String _lang = null;
	protected String _installDirectory = null;
	protected String _installDirectoryApp = null;
	protected String _synthesisCascade = null;
	protected String _analysisCascade = null;
	
	protected String _unitexVersion = null;
	
	
	protected boolean _useUnitexDictionary = false;
	
	/**
	 * 
	 * @param installDirectory
	 * @param lang
	 */
	public Unitex(String installDirectory, String installDirectoryApp, String lang, String unitexVersion)
	{
		_unitexVersion = unitexVersion;
		_lang = lang;
		_installDirectory = installDirectory;
		_installDirectoryApp = installDirectoryApp;
		
	}
	
	
	public Unitex(String installDirectory, String installDirectoryApp, String lang, String unitexVersion, String analysisCascade, String synthesisCascade)
	{
		_unitexVersion = unitexVersion;
		_lang = lang;
		_installDirectory = installDirectory;
		_installDirectoryApp = installDirectoryApp;
		
		_analysisCascade = analysisCascade;
		_synthesisCascade = synthesisCascade;
		
	}
	
	public void setUnitexVersion(String unitexVersion)
	{
		_unitexVersion = unitexVersion;
	}
	
	public void setAnalysisCascade(String analysisCascade)
	{
		_analysisCascade = analysisCascade;
	}
	
	public void setSynthesisCascade(String synthesisCascade)
	{
		_synthesisCascade = synthesisCascade;
	}
	
	
	public void setUseUnitexDictionary(boolean useUnitexDictionary)
	{
		_useUnitexDictionary = useUnitexDictionary;
	}
	
	public void unitexPreprocessing(String filePath) throws IOException, InterruptedException
	{
		Runtime runtime = Runtime.getRuntime();
		Process proc;
		String cmd = "";
		
		
		cmd = _installDirectoryApp
				+ "/UnitexToolLogger Grf2Fst2 "
				+ _installDirectory
				+ "/"+_lang+"/Graphs/Preprocessing/Sentence/Sentence.grf -y --alphabet="
				+ _installDirectory + "/"+_lang+"/Alphabet.txt -d " + _installDirectory
				+ "/"+_lang+"/Graphs";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp
				+ "/Flatten "
				+ _installDirectory
				+ "/"+_lang+"/Graphs/Preprocessing/Sentence/Sentence.fst2 --rtn -d5";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp + "/Fst2Txt -t" + filePath + ".snt " + _installDirectory
				+ "/"+_lang+"/Graphs/Preprocessing/Sentence/Sentence.fst2 -a"
				+ _installDirectory + "/"+_lang+"/Alphabet.txt -M";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp
				+ "/Grf2Fst2 "
				+ _installDirectory
				+ "/"+_lang+"/Graphs/Preprocessing/Replace/Replace.grf -y --alphabet="
				+ _installDirectory + "/"+_lang+"/Alphabet.txt -d " + _installDirectory
				+ "/"+_lang+"/Graphs";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp + "/Fst2Txt -t" + filePath + ".snt " + _installDirectory
				+ "/"+_lang+"/Graphs/Preprocessing/Replace/Replace.fst2 -a"
				+ _installDirectory + "/"+_lang+"/Alphabet.txt -R";
		proc = runtime.exec(cmd);
		proc.waitFor();
		
		 
		
	}
	
	
	
	
	public void loadDictionaries(String filePath) throws IOException, InterruptedException
	{
		Runtime runtime = Runtime.getRuntime();
		Process proc;
		String cmd = "";
		
		
		if(_lang.equals("French"))
		{
			cmd = _installDirectoryApp + "/Dico -t" + filePath + ".snt -a" + _installDirectory + "/French/Alphabet.txt ";
	
			cmd +=   _installDirectory + "/French/Dela/dela-fr-public.bin " + _installDirectory + "/French/Dela/NPr+.fst2 ";
		}
		if(_lang.equals("Spanish"))
		{
			cmd = _installDirectoryApp + "/Dico -t" + filePath + ".snt -a" + _installDirectory + "/Spanish/Alphabet.txt ";
			
			cmd += _installDirectory+ "/Spanish/Dela/delaf.bin ";
					
		}
		if(_lang.equals("Italian"))
		{
			cmd = _installDirectoryApp + "/Dico -t" + filePath + ".snt -a" + _installDirectory + "/Italian/Alphabet.txt ";
			
			cmd += _installDirectory+ "/Italian/Dela/mini-delaf.bin ";
					
		}
		proc = runtime.exec(cmd);
		proc.waitFor();
		
		cmd = _installDirectoryApp + "/SortTxt " + filePath + "_snt/dlf -l" + filePath
				+ "_snt/dlf.n -o" + _installDirectory + "/"+_lang+"/Alphabet_sort.txt";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp + "/SortTxt " + filePath + "_snt/dlc -l" + filePath
				+ "_snt/dlc.n -o" + _installDirectory + "/"+_lang+"/Alphabet_sort.txt";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp + "/SortTxt " + filePath + "_snt/err -l" + filePath
				+ "_snt/err.n -o" + _installDirectory + "/"+_lang+"/Alphabet_sort.txt";
		proc = runtime.exec(cmd);
		proc.waitFor();
		cmd = _installDirectoryApp + "/SortTxt " + filePath + "_snt/tags_err -l" + filePath
				+ "_snt/tags_err.n -o" + _installDirectory
				+ "/"+_lang+"/Alphabet_sort.txt";
		proc = runtime.exec(cmd);
		proc.waitFor();
		
	}
	
	
	
	public String launchCascade(String filePath) throws Exception {
		
		//System.out.println(" ** BEGIN LAUNCHCASCADE :" + filePath);
	
		//boolean useDico = false;
		
		Runtime runtime = Runtime.getRuntime();
		Process proc;
	
		String cmd = "";
		try{
	
			System.out.println("  - Unitex preprocessing");
			
			if(_unitexVersion.equals("3.1beta"))
			{
			//Unitex 3.1beta 
			cmd = _installDirectoryApp + "/Normalize " + filePath + ".txt -r" + _installDirectory + "/"+_lang+"/Norm.txt";
			}
			else
			{
			//Unitex-Gramlab 3.1
			cmd = _installDirectoryApp + "/UnitexToolLogger Normalize " + filePath + ".txt -r" + _installDirectory + "/"+_lang+"/Norm.txt";
			}
			
			
			
			proc = runtime.exec(cmd);
			proc.waitFor();
			
			if(_useUnitexDictionary)
			{
				unitexPreprocessing(filePath);
			}
			
			new File(filePath + "_snt").mkdir();
	
			
			if(_unitexVersion.equals("3.1beta"))
			{
				//Unitex 3.1beta 
				cmd = _installDirectoryApp + "/Tokenize " + filePath + ".snt -a" + _installDirectory + "/"+_lang+"/Alphabet.txt";
			}
			else
			{
				//Unitex-Gramlab 3.1
				cmd = _installDirectoryApp + "/UnitexToolLogger Tokenize " + filePath + ".snt -a" + _installDirectory + "/"+_lang+"/Alphabet.txt";
			}
			proc = runtime.exec(cmd);
			proc.waitFor();
	
			
			
			if(_useUnitexDictionary)
			{
				
				System.out.println("  - Loading dictionary");
				
				loadDictionaries(filePath);			
			}
			
			//tester la présence des fichiers
			
			System.out.println("  - Analysis Cascade");
			
			//Appel de la premiere cascade
			
			
			//Unitex 3.1beta 
			if(_unitexVersion.equals("3.1beta"))
			{
			cmd = _installDirectoryApp + "/Cassys -a" + _installDirectory + "/"+_lang+"/Alphabet.txt -t"
					+ filePath + ".snt -l" + _installDirectory
					+ "/"+_lang+_analysisCascade;//"/CasSys/Perdido/Perdido_analysis.csc";// -w"+rootName+"/French/Dela/dela-fr-public.bin";
			}
			else
			{
			//Unitex-Gramlab 3.1
			cmd = _installDirectoryApp + "/UnitexToolLogger Cassys -a" + _installDirectory + "/"+_lang+"/Alphabet.txt -t"
					+ filePath + ".snt -l" + _installDirectory
					+ "/"+_lang+_analysisCascade +" -v -r" + _installDirectory + "/" +_lang + "/Graphs/";//"/CasSys/Perdido/Perdido_analysis.csc";// -w"+rootName+"/French/Dela/dela-fr-public.bin";
			}
			//System.out.println("cmd : " + cmd);
			
			proc = runtime.exec(cmd);
			proc.waitFor();
			//CallableProcess.exec(cmd,10); //permet de mettre un timeout de 10 secondes
			
			
			if(_unitexVersion.equals("3.1beta"))
			{
				//Unitex 3.1beta 
				cmd = _installDirectoryApp + "/Normalize " + filePath + "_csc.txt -r" + _installDirectory
						+ "/"+_lang+"/Norm.txt";
			}
			else
			{
				//Unitex-Gramlab 3.1
				cmd = _installDirectoryApp + "/UnitexToolLogger Normalize " + filePath + "_csc.txt -r" + _installDirectory
						+ "/"+_lang+"/Norm.txt";
			}
			proc = runtime.exec(cmd);
			proc.waitFor();
			
			
			new File(filePath + "_csc_snt").mkdir();
			
			if(_unitexVersion.equals("3.1beta"))
			{
				//Unitex 3.1beta 
				cmd = _installDirectoryApp + "/Tokenize " + filePath + "_csc.snt -a" + _installDirectory
						+ "/"+_lang+"/Alphabet.txt";
			}
			else
			{
				//Unitex-Gramlab 3.1
				cmd = _installDirectoryApp + "/UnitexToolLogger Tokenize " + filePath + "_csc.snt -a" + _installDirectory
						+ "/"+_lang+"/Alphabet.txt";
			}
			proc = runtime.exec(cmd);
			proc.waitFor();
			
			
			
		
			//Call of the second cascade
			System.out.println("  - Synthesis cascade");
			
			if(_unitexVersion.equals("3.1beta"))
			{
				//Unitex 3.1beta 
				cmd = _installDirectoryApp + "/Cassys -a" + _installDirectory + "/"+_lang+"/Alphabet.txt -t"
						+ filePath + "_csc.snt -l" + _installDirectory
						+ "/"+_lang+_synthesisCascade;//"/CasSys/Perdido/Perdido_synthesis.csc";
			}
			else
			{
				//Unitex-Gramlab 3.1
				cmd = _installDirectoryApp + "/UnitexToolLogger Cassys -a" + _installDirectory + "/"+_lang+"/Alphabet.txt -t"
						+ filePath + "_csc.snt -l" + _installDirectory
						+ "/"+_lang+_synthesisCascade +" -v -r" + _installDirectory + "/" +_lang + "/Graphs/";//"/CasSys/Perdido/Perdido_synthesis.csc";
			}
			CallableProcess.exec(cmd,10); //add a timeout of 10 seconds
		
			if(_unitexVersion.equals("3.1beta"))
			{
				//Unitex 3.1beta 
				cmd = _installDirectoryApp + "/Convert -s"+_lang.toUpperCase()+" -dUTF-8 --ss=-old " + filePath + "_csc_csc.txt";
			}
			else
			{
				//Unitex-Gramlab 3.1
				cmd = _installDirectoryApp + "/UnitexToolLogger Convert -s"+_lang.toUpperCase()+" -dUTF-8 --ss=-old " + filePath + "_csc_csc.txt";
			}
			proc = runtime.exec(cmd);
			proc.waitFor();
			
	
		} catch (TimeoutException e) {
			return "TIMEOUT : unitex cascade";
	        
	    }
		catch (Exception e) {
			
			return "Execution cascade error";
		}
		
		//System.out.println("-- END LAUNCHCASCADE");
		
		return "";
	}
	
	
	
	
	
	
	

}
