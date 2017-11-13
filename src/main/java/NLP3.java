import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ie.util.RelationTriple;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class NLP3 {
    public static void main (String[] args) throws IOException{

        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        File foo = new File("foo.txt");
//        ArrayList<File> files = new ArrayList<>();
//        files.add(foo);

        String text = new String(Files.readAllBytes(Paths.get("foo.txt")));


//        Annotation document = new Annotation(text);
//        pipeline.annotate(document);
//        System.out.println(text.toString());
//        List<CoreMap> sentences = pipeline.get(CoreAnnotations.SentencesAnnotation.class);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);


        for(CoreMap sentence: sentences){
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            for (RelationTriple triple : triples) {
                System.out.println(triple.confidence + "\t" + ","+
                        triple.subjectLemmaGloss() + "\t" + ","+
                        triple.relationLemmaGloss() + "\t" + ","+
                        triple.objectLemmaGloss());
            }
        }

    }
}