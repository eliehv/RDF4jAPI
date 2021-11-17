# RDF4jAPI

Create a SPARQL endpoint service as a query endpoint to RDF4J(Sesame)  repository using RDF4J(Sesame) API:
    Create a servlet to be run in Apache Tomcat server:
        ◦ Create a simple client html page with:
            ▪ text fields for the URL of remote repository and repository ID
            ▪ a text field for SPARQL query
            ▪ a button to run a query
        ◦ Simple functionality of the servlet backend includes:
            ▪ receiving SPARQL query from the client  
            ▪ connection to the remote repository
            ▪ performing SPARQL query.
            ▪ sending query result back to the client     
