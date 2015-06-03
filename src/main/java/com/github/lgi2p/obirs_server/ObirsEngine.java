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

import com.github.lgi2p.obirs.Conf;
import com.github.lgi2p.obirs.core.model.ObirsQuery;
import com.github.lgi2p.obirs.core.model.ObirsResult;
import com.github.lgi2p.obirs.core.model.RefinedObirsQuery;
import com.github.lgi2p.obirs.core.engine.ObirsGroupwise;
import com.github.lgi2p.obirs.core.engine.ObirsMohameth2010;
import com.github.lgi2p.obirs.core.index.ItemCollection;
import com.github.lgi2p.obirs.core.index.IndexationMemory;
import com.github.lgi2p.obirs.core.model.Item;
import com.github.lgi2p.obirs.utils.IndexerJSON;
import com.github.lgi2p.obirs.utils.JSONConverter;
import com.github.lgi2p.obirs.utils.Utils;
import com.github.lgi2p.obirs.utils.autocomplete.Autocompletion_Trie;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.utils.SMconf;

/**
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
public class ObirsEngine {

    static Logger logger = LoggerFactory.getLogger(ObirsEngine.class);
    private static ObirsEngine instance = null;

    private final ObirsGroupwise obirs;
    private final ObirsMohameth2010 obirsMohammet;
    SM_Engine engine;
    ItemCollection index;
    Map<URI, String> conceptLabels;
    IndexerJSON indexerJSON;
    Autocompletion_Trie autocomplete;
    static String ontoFile;
    static String annotFile;
    static String conceptIndex;

    private ObirsEngine() throws Exception {

        logger.info("Loading Engine");
        logger.info("Ontology   : " + ontoFile);
        logger.info("Concept label index: " + conceptIndex);
        logger.info("Annotations: " + annotFile);

        logger.info("Loading concept labels index");
        conceptLabels = Utils.loadConceptLabels(conceptIndex);
        logger.info("loading autocomplete...");
        autocomplete = new Autocompletion_Trie();
        for (Map.Entry<URI, String> e : conceptLabels.entrySet()) {
            autocomplete.load(e.getKey().stringValue(), e.getValue());
        }

        logger.info("loading ontology & building engine...");
        engine = Utils.buildSMEngine(ontoFile);

        logger.info("loading item index...");
        indexerJSON = new IndexerJSON();
        indexerJSON.index(annotFile);
        Iterable<Item> items = indexerJSON.getItems();
        index = new IndexationMemory(items);
        
        logger.info("Loading Obirs instances");
        
        SMconf similarityMeasureConf = Conf.getDefaultDirectGroupwiseSimilarityMeasure();
        obirs = new ObirsGroupwise(engine, index, similarityMeasureConf);
        obirsMohammet = new ObirsMohameth2010(engine, index);

    }

    public static ObirsEngine getInstance() throws Exception {
        if (instance == null) {
            if (ontoFile == null || annotFile == null || conceptIndex == null) {
                throw new Exception("Please set the configuration first");
            }
            instance = new ObirsEngine();
        }
        return instance;
    }

    public static void setConf(String ontoFile, String annotFile, String conceptIndex) {
        ObirsEngine.ontoFile = ontoFile;
        ObirsEngine.annotFile = annotFile;
        ObirsEngine.conceptIndex = conceptIndex;
    }

    public String queryJSON(String jsonQuery) throws Exception {

        ObirsQuery query = JSONConverter.parseObirsJSONQuery(engine, jsonQuery);
        List<ObirsResult> results = obirsMohammet.query(query);
        return JSONConverter.jsonifyObirsResults(results, indexerJSON, conceptLabels);
    }

    public String getAutocomplete(String query) throws Exception {

        Set<Autocompletion_Trie.AutocompleteElement> results = autocomplete.findCompletions(query);
        return JSONConverter.buildJSONString(results);
    }

    public String refineQuery(String query) throws Exception {

        RefinedObirsQuery refinedQuery = JSONConverter.parseRefinedObirsQuery(engine, query);
        ObirsQuery oQuery = obirsMohammet.refineQuery(refinedQuery);
        return JSONConverter.jsonifyObirsQuery(oQuery);

    }

}
