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

import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
public class ObirsServerServletContextListener implements ServletContextListener {

    static Logger logger = LoggerFactory.getLogger(ObirsServerServletContextListener.class);

    static String annotationFile = "/data/toxnuc/toxnuc_annots_5_11_14.json";
    static String ontologyFile = "/data/toxnuc/ontologie_toxnuc_intd_aclp.owl";
    static String conceptIndex = "/data/toxnuc/ontologie_toxnuc_intd_aclp.owl.labels.index.tsv";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logger.info("************************************************");
            logger.info("*** Initializing Obirs Server " + new Date());
            logger.info("************************************************");

            logger.info("Annotation file: "+annotationFile);
            logger.info("Ontology file: "+ontologyFile);
            logger.info("Concept index: "+conceptIndex);
            
            ObirsEngine.setConf(ontologyFile, annotationFile,conceptIndex);
            ObirsEngine.getInstance();

        } catch (Exception ex) {
            logger.error("Error " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
        logger.info("************************************************");
        logger.info("*** Obirs Server now initialized  " + new Date());
        logger.info("************************************************");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            logger.info("************************************************");
            logger.info("*** Shuthdown Obirs Server " + new Date());
            logger.info("************************************************");
        } catch (Exception ex) {
            logger.error("Error " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
        logger.info("************************************************");
        logger.info("*** Obirs Server now shutdown  " + new Date());
        logger.info("************************************************");
    }
}
