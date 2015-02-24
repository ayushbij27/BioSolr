/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.flax.biosolr.ontology.search.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flax.biosolr.ontology.api.EFOAnnotation;
import uk.co.flax.biosolr.ontology.config.SolrConfiguration;
import uk.co.flax.biosolr.ontology.search.OntologySearch;
import uk.co.flax.biosolr.ontology.search.ResultsList;
import uk.co.flax.biosolr.ontology.search.SearchEngineException;

/**
 * Solr implementation of the Ontology search engine.
 * 
 * @author Matt Pearce
 */
public class SolrOntologySearch extends SolrSearchEngine implements OntologySearch {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolrOntologySearch.class);

	private final SolrConfiguration config;
	private final SolrServer server;
	
	public SolrOntologySearch(SolrConfiguration config) {
		this.config = config;
		this.server = new HttpSolrServer(config.getOntologyUrl());
	}
	
	protected SolrServer getServer() {
		return server;
	}
	
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	public ResultsList<EFOAnnotation> searchOntology(String term, int start, int rows) throws SearchEngineException {
		ResultsList<EFOAnnotation> results = null;
		
		try {
			SolrQuery query = new SolrQuery(term);
			query.setStart(start);
			query.setRows(rows);
			query.setRequestHandler(config.getOntologyRequestHandler());
			
			QueryResponse response = server.query(query);
			List<EFOAnnotation> annotations = response.getBeans(EFOAnnotation.class);
			results = new ResultsList<>(annotations, rows, (start / rows), response.getResults().getNumFound());
		} catch (SolrServerException e) {
			throw new SearchEngineException(e);
		}
		
		return results;
	}
	
	@Override
	public EFOAnnotation findOntologyEntryByUri(String uri) throws SearchEngineException {
		EFOAnnotation retVal = null;
		
		try {
			SolrQuery query = new SolrQuery(uri);
			query.setRequestHandler(config.getOntologyNodeRequestHandler());
			
			QueryResponse response = server.query(query);
			List<EFOAnnotation> annotations = response.getBeans(EFOAnnotation.class);
			if (annotations.size() > 0) {
				retVal = annotations.get(0);
			}
		} catch (SolrServerException e) {
			throw new SearchEngineException(e);
		}
		
		return retVal;
	}

}