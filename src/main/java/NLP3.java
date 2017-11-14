import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ie.util.RelationTriple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NLP3 {
    public static void main (String[] args) throws IOException{

        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,mention,coref,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        File foo = new File("foo.txt");
//        ArrayList<File> files = new ArrayList<>();
//        files.add(foo);

//        String text = new String(Files.readAllBytes(Paths.get("lincoln.txt")));

        File folder = new File("./lincoln/");
        File[]  list = folder.listFiles();

        File out = new File("triples.csv");
        out.createNewFile();

        FileWriter outWriter = new FileWriter(out);

        outWriter.write("confidence,subject,relation,object\n");


//        Annotation document = new Annotation(text);
//        pipeline.annotate(document);
//        System.out.println(text.toString());
//        List<CoreMap> sentences = pipeline.get(CoreAnnotations.SentencesAnnotation.class);

        for (File f: list) {
            System.out.println(f.getPath());
            String text = new String(Files.readAllBytes(Paths.get(f.getPath())));
            // create an empty Annotation just with the given text
            Annotation document = new Annotation(text);

            // run all Annotators on this text
            pipeline.annotate(document);

            // these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

            Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);

//            for(Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
//                System.out.println("ClusterId: " + entry.getKey());
//                System.out.println("CHAIN : " + entry.getValue());
//                CorefChain c = entry.getValue();
//                CorefChain.CorefMention representativeMention = c.getRepresentativeMention();
//                System.out.println(representativeMention);
//            }

            for (CoreMap sentence : sentences) {
                Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
                for (RelationTriple triple : triples) {
//                    for(CoreLabel l: triple.object){
//                        String pos = l.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                        if (pos.equals("PRP") || pos.equals("PRP$")){
//                            for(Mention m: sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)){
//                                System.out.println(m);
//                            }
//                        }
//                    }
                    String obj = "";
                    for (CoreLabel l : triple.object){
                        Integer clusterID = l.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
                        if (clusterID == null){
                            obj = triple.objectLemmaGloss();
                        } else {
                            CorefChain.CorefMention m = graph.get(clusterID).getRepresentativeMention();
                            obj = m.mentionSpan;
                        }
                    }

                    String subj = "";
                    for (CoreLabel l : triple.subject){
                        Integer clusterID = l.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
                        if (clusterID == null){
                            subj = triple.subjectLemmaGloss();
                        } else {
                            CorefChain.CorefMention m = graph.get(clusterID).getRepresentativeMention();
                            subj = m.mentionSpan;
                        }
                    }

                    String rel = "";
                    for (CoreLabel l : triple.relation){
                        Integer clusterID = l.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
                        if (clusterID == null){
                            rel = triple.relationLemmaGloss();
                        } else {
                            CorefChain.CorefMention m = graph.get(clusterID).getRepresentativeMention();
                            rel = m.mentionSpan;
                        }
                    }
                    outWriter.write(triple.confidence + "," +
                            subj  + "," +
                            rel  + "," +
                            obj+"\n");
                }
            }
        }

        outWriter.close();

    }
}