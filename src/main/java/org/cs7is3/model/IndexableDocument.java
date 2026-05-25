package org.cs7is3.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public interface IndexableDocument {

    String documentNumber();
    String header();
    String content();

    default Document toLuceneDocument(){
        Document document = new Document();
        document.add(new StringField("documentNumber", documentNumber(), Field.Store.YES));
        document.add(new TextField("header", header(), Field.Store.YES));
        document.add(new TextField("content", content(), Field.Store.YES));

        return document;
    }
}





