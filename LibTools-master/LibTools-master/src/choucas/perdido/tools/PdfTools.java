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

import java.io.File;
import java.io.StringWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;



public class PdfTools  {

	public static void main(String[] args) {
	
		 System.out.println("Begin main");
		
		
		String txt = "";
		try {
			
			txt = parsePdfFile(new File("/Users/lmoncla/sample.pdf"));
		
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 System.out.println("Result :"+ txt);
		 System.out.println("End main");
	}
	
	
	private static String parsePdfFile(File stream) throws Exception {
        StringWriter output = new StringWriter(4096);
        PDDocument document = null;
        try {
            document = PDDocument.load(stream);
            if (document.isEncrypted()) {
                try {
                    document.decrypt("");
                } catch (Throwable e) {
                	System.err.println("Could not parse PDF File since the document is encrypted");
                    return "";
                }
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(Integer.MAX_VALUE);
            stripper.writeText(document, output);
            return output.toString();
        } catch (Exception e) {
            System.err.println("Exception parsing PDF document"+ e.toString());
            return "";
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    /* ignore */
                }
            }
        }
    }
	
	
}
