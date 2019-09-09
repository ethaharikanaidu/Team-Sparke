import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class items_glossary {

	public static Map<String,String> glossary=new HashMap<>();
	//for collection of glossary terms and definitions

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.print("Input glossary terms:\n1-Manual Input\n2-Through File\nOption:");

		Scanner scn=new Scanner(System.in);

		char option=scn.next().charAt(0);

		if(option=='1') {
			glossaryThroughStandardInput();
		}
		else if(option=='2') {
			glossaryThroughInputFile();
		}
		//ending up with terms input
		
		//adding references in definitions also
		for(final String k:glossary.keySet()) {
			glossary.forEach((term,definition)->{
				glossary.put(term, definition.replaceAll("(?i)(?<![a-z])"+k+"(?![a-z])","<a href='#"+k+"'>"+k+"</a>"));
			});
		}
		
		//sorting the glossary items alphabetically on the basisi of terms
		Map<String,String> sortedGlossary=new LinkedHashMap<>();
		glossary.entrySet().stream().sorted(Map.Entry.comparingByKey()).
		forEachOrdered(x->sortedGlossary.put(x.getKey(), x.getValue()));
		/*
		 *writing HTML file
		 *with glossary terms and definitions 
		 */
		System.out.println("*********************************************************");
		System.out.println("Writing HTML file for glossary items......");
		try {
			outputHTMLFile(sortedGlossary);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("HTML file has written successfully in the root directory.");
		System.out.println("**********************************************************");
		System.exit(0);
	}

	private static void outputHTMLFile(Map<String,String> glossary) throws IOException {
		
		Set<String> terms=new HashSet<>();
		terms=glossary.keySet();
		String index = "glossay.html";
		File htmlTemplateFile = new File(index);
		Path path = Paths.get(index); 
		//Use try-with-resource to get auto-closeable writer instance
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
			writer.write("<html>\n");
			writer.write("<head>\n");
			writer.write("<title>" + "Glossary" + "</title>");
			writer.write("</head>\n");
			writer.write("<body>");
			writer.write("<h1 style='text-align:center'>Glossary</h1>");
			writer.write("\n");
			writer.write("<h3>Index</h3>");
			writer.write("<ul>");
			
			String termsAndDefinitionHTML="<ul>\n";
			glossary.forEach((term,definition)->{
				try {
					writer.write("<li><a href=#" + term + ">" + term
							+ "</a></li>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			writer.write("</ul>\n");
			writer.write("<h3>Terms and Definitions</h3>");
			
			glossary.forEach((term,definition)->{
				try {
					writer.write("<li id='"+term+"'>"
							+ "<span style='color:red'>"+term+": </span>"
									+ "<span>"+definition+"</span></li><br>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			writer.write("</ul>");
			writer.write("</body>");
			writer.write("</html>");

			writer.close();
		}
	}

	public static void glossaryThroughStandardInput() {
		char nextTerm;
		Scanner scn=new Scanner(System.in);
		do {
			System.out.print("Enter Term (consisting of a single word only):");
			String term=scn.nextLine();
			System.out.print("Enter Term definition (seperated with white spaces):");
			String definition=scn.nextLine();
			//adding term and its definition in glossary map
			glossary.put(term,definition);
			System.out.println("Your glossary item has been added in the glossary collections");
			System.out.print("Do you want to add more item in glossary (Y/N):");
			nextTerm=scn.nextLine().charAt(0);
		}while(nextTerm=='y'|| nextTerm=='Y');
		System.out.println("You have successfully added"+glossary.size()+" item(s) in the glossary");
	}

	private static void glossaryThroughInputFile()  {

		final JFileChooser fc = new JFileChooser();
		// Open the dialog using null as parent component if you are outside a
		// Java Swing application otherwise provide the parent comment instead
		JDialog wrapper = new JDialog((Window)null);
		wrapper.setVisible(true);
		int returnVal = fc.showOpenDialog(wrapper);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			wrapper.setVisible(false);
			// Retrieve the selected file
			File file = fc.getSelectedFile();
			try (FileInputStream fis = new FileInputStream(file)) {
				System.out.println("Loading Glossary Items from file........");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				try {
					String next = reader.readLine();
					while(next != null) {

						String definition = "";
						String term = "";
						boolean nextTerm = true;

						/*
						 * The term-ends will help to identify a new term and definition group in file
						 */
						if (next.equals("term-ends")) {
							nextTerm = false;
						} else {
							term = next;
						}
						while (nextTerm) {
							/*
							 * Until term-ends came or end of file, the words in lines will be
							 * concatenate in the term definition with an empty space
							 */
							next = reader.readLine();
							if (next == null)
								break;
							if (!next.equals("term-ends")) {
								definition = definition + " " + next;
							} else {
								nextTerm = false;
								next = reader.readLine();
							}
						}
						/*
						 * putting a term and its definition in map collection of glossary
						 */
						glossary.put(term, definition);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println(glossary.size()+" has been loaded successfully...");
	}
}
