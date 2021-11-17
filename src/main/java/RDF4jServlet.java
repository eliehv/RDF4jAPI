
package rdf4jAPI;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
/**
 * Servlet implementation class RDF4jServlet
 */
@WebServlet("/RDF4jServlet")
public class RDF4jServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public RDF4jServlet() 
    {
        // TODO Auto-generated constructor stub
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String repositoyrURL=request.getParameter("RepositoryURL");
		String rdf4jServer = repositoyrURL;
		String repositoryID = request.getParameter("RepositoryID");
		//String repositoryID = repositoryID;
		
		PrintWriter writer = response.getWriter();
		
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		
		String sparqlEndpoint = "http://localhost:8080/sparql";
		Repository sparrepo = new SPARQLRepository(sparqlEndpoint);
		
		PrintWriter out = response.getWriter();
		//RepositoryConnection conn = repo.getConnection();
		try  (RepositoryConnection conn = repo.getConnection())
		{
			  String queryString = URLDecoder.decode(request.getParameter("Query"), "utf-8");
			  
			  //******************** CHECKING ALL POSSIBLE QUERIES ***************
			  ArrayList<String[]> results = new ArrayList<String[]>();
				queryString = queryString.trim();
				
				if (queryString == null || queryString.isEmpty()) 
				{
					System.out.println("Empty Query");
				}
						
				if (queryString.toLowerCase().indexOf("construct") >= 0
					|| queryString.toLowerCase().indexOf("describe") >= 0) 
				{
					GraphQuery tupleQuery = conn.prepareGraphQuery(queryString);
			    	   try (GraphQueryResult result = tupleQuery.evaluate()) 
			    	   {    		   
			    	      while (result.hasNext()) 
			    	      { 
			    	         Statement st = result.next();
			    	         String[] values = new String[3];
			    	         
			    	         values[0] = st.getSubject().stringValue();
			    	         values[1] = st.getPredicate().stringValue();
			    	         values[2] = st.getObject().stringValue();
			    	         
			    	         results.add(values);
			    	      }
			    	   String  graphResults= createQueryResult(new ArrayList<String>()
			    	   {{add("Subject"); add("Predicate"); add("Object");}},results);
						      
						        out.println(graphResults);
						        out.flush();
							    out.close();
			    	    
			    	   }
			    	}//GrapgQuery
					
					
				 
				else if (queryString.toLowerCase().indexOf("select") >= 0) 
				{
					System.out.println(queryString);
					   TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
					   TupleQueryResult result = tupleQuery.evaluate();
				        String resultTable;
					   try 
					   {
						   List<String> bindingNames = result.getBindingNames();
						   
					      while (result.hasNext()) 
					      {  // iterate over the result
					         BindingSet bindingSet = result.next();
					         String[] values = new String[bindingNames.size()];
			    	         
			    	         for (int i = 0; i < bindingNames.size(); i++) 
			    	         {
			    	        	 Value value = bindingSet.getValue(bindingNames.get(i));
			    	        	 values[i] = value.stringValue();
			    	         }			    	         
			    	         results.add(values);
					       
					      }//end of while
					      
					      resultTable = createQueryResult (bindingNames, results);		     
					        out.write(resultTable);
					        out.flush();
						    out.close();
					   }
					   finally {result.close();}
					} //SELECT Query
				
			else if (queryString.toLowerCase().indexOf("ask") >= 0) 
			{
						System.out.println(queryString);
						BooleanQuery query = conn.prepareBooleanQuery(queryString);
				    	   
				    	 if (query.evaluate()== true)
				    	 {
				    		 
					        out.write("Yes");
				    	 }
				    	 else
				    	 {
				    	
						        out.write("No");
				    	 }
				    		 
				    		
			}//ASK QUERY
		}
		catch (Exception e) 
		{
			
	        out.print(e);
		}	
		
	}//End of doGET Method
	
	
	//********************************** doPOST ***********************************	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
//*************************** Process Results to Print ************************************	
	
	public String createQueryResult(List<String >headers, ArrayList<String[]> content)
	{
		String queryResult;
		
		String head = "";
		for (int i = 0; i < headers.size(); i++) {
			String header = headers.get(i);
			head += "\t" + header + "\t";
		}
		head += "\n";
		
		String body = "";
		for (int m = 0; m < content.size(); m++) {
			
			String[] item = content.get(m);
			for (int i = 0; i < item.length; i++) {
		       	 body += "\t" +  item[i] + "\t";
	        }
	        body += "\n\n\n";
		}
		
		queryResult =  head + body ;
		return queryResult;
	}

}//End of class
