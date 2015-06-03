/*
 *  Copyright or © or Copr. Ecole des Mines d'Alès (2012-2014) 
 *  
 *  This software is a computer program whose purpose is to provide 
 *  several functionalities for the processing of semantic data 
 *  sources such as ontologies or text corpora.
 *  
 *  This software is governed by the CeCILL  license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL
 *  "http://www.cecill.info". 
 * 
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 

 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 * 
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms.
 */
package com.github.lgi2p.obirs_server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
@WebServlet(urlPatterns = {"/api/search", "/api/query", "/api/refine"})
public class Api extends HttpServlet {

    static Logger logger = LoggerFactory.getLogger(Api.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String extra) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ObirsController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ObirsController at " + request.getContextPath() + "</h1>");
            out.println("<p> " + extra + "</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * api/search?query={"concepts":[{"uri":"http://www.cea.fr/ontotoxnuc#AnalyseStatistique", "weight": 0.5},{"uri":"http://www.cea.fr/ontotoxnuc#Uranium", "weight":0.5}]}
     * /api/search?query=%7B"concepts"%3A%5B%7B"uri"%3A"http%3A%2F%2Fwww.cea.fr%2Fontotoxnuc%23AnalyseStatistique"%2C%20"weight"%3A%200.5%7D%2C%7B"uri"%3A"http%3A%2F%2Fwww.cea.fr%2Fontotoxnuc%23Uranium"%2C%20"weight"%3A%200.5%7D%5D%7D
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        try {
            PrintWriter out = response.getWriter();

//            out.println(request.getPathInfo());
//            out.println(request.getParameterMap());
//            out.println(request.getContextPath());
//            out.println(request.getServletPath());
            ObirsEngine engine = ObirsEngine.getInstance();

            String query = request.getParameter("query");
            String term = request.getParameter("term");

            switch (request.getServletPath()) {

                case "/api/search":
                    if (term != null) {
                        out.println(engine.getAutocomplete(term));
                    } else if (query != null) {
                        out.println(engine.queryJSON(query));
                    } else {
                        out.println("{error: \"invalid request, please precise a term or query value\"}");
                    }
                    break;

                case "/api/refine":
                    if (query != null) {
                        out.println(engine.refineQuery(query));
                    } else {
                        out.println("{error: \"invalid request, please precise a query value\"}");
                    }
                    break;
                default:
                    out.println("Error please refer to the documentation");
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String query = request.getParameter("query");
//        logger.info("Query: " + query);
//        String result = "Query:"+query;
//
//        if (query != null) {
//            try {
//                result += ObirsEngine.getInstance().queryJSON(query);
//            } catch (Exception ex) {
//                java.util.logging.Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        processRequest(request, response, result);
//    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
