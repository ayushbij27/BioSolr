package org.apache.solr.search.federated;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.schema.FieldType;

public abstract class MergeAbstractFieldType extends FieldType {

  public static final Object DEFAULT_MERGE_BEHAVIOUR = new Object();
  
  @Override
  public void write(TextResponseWriter writer, String name, IndexableField f) throws IOException {
    // do nothing
  }
  
  /**
   * Convert from a shard value.
   */
  @Override
  protected IndexableField createField(String name, String val, org.apache.lucene.document.FieldType type, float boost) {
    return new ObjectField(name, convert(val), type, boost);
  }
  
  @Override
  public Object toObject(IndexableField f) {
    return ((ObjectField)f).object;
  }
  
  public abstract Object convert(String val);

  /**
   * Override with your own merge behaviour if desired. It is called once for each
   * shard that returns the document, and shardValue may be a List of values.
   */
  public Object merge(String shardAddress, Object mergeValue, Object shardValue) {
    return DEFAULT_MERGE_BEHAVIOUR;
  }
  
  private class ObjectField implements IndexableField {

    private String name;
    
    private Object object;
    
    private IndexableFieldType type;
    
    private float boost;

    private ObjectField(String name, Object object, IndexableFieldType type, float boost) {
      this.name = name;
      this.object = object;
      this.type = type;
      this.boost = boost;
    }
    
    @Override
    public String name() {
      return name;
    }

    @Override
    public IndexableFieldType fieldType() {
      return type;
    }

    @Override
    public float boost() {
      return boost;
    }

    @Override
    public BytesRef binaryValue() {
      return null;
    }

    @Override
    public String stringValue() {
      return null;
    }

    @Override
    public Reader readerValue() {
      return null;
    }

    @Override
    public Number numericValue() {
      return null;
    }

    @Override
    public TokenStream tokenStream(Analyzer analyzer, TokenStream reuse) throws IOException {
      return null;
    }
    
  }
  
}
