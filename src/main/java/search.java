import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.util.CoreMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class search {

    private static boolean compareStrings(String a, String b){
        String[] aList = a.split(" ");
        String[] bList = b.split(" ");

        int matchCount = 0;

        for(String word: aList){
            for (String word2: bList){
                if(word.equals(word2)){
                    matchCount++;
                }
            }
        }

        double pctMatch = (double)matchCount/((double) Integer.max(aList.length, bList.length));
        if (pctMatch > 0.7) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws FileNotFoundException{

        Scanner trips = new Scanner(new File("triples.csv"));
        ArrayList<String[]> facts = new ArrayList<String[]>();
        while(trips.hasNextLine()){
            String[] tmp = trips.nextLine().split(",");
            facts.add(tmp);
        }


        System.out.print("Type your question below, we will do our best to respond in 1-3 business days\n>");
        Scanner stdin = new Scanner(System.in);
        String q = stdin.nextLine();

        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,mention,coref,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation doc = new Annotation(q);

        pipeline.annotate(doc);

//        for (Sentence s: doc.sentences()) {
//            for (RelationTriple t : s.){

        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            for (RelationTriple t : triples) {
                System.out.println(t.subjectLemmaGloss() + "\t" + t.relationLemmaGloss() + "\t" + t.objectLemmaGloss());

                for (String[] f : facts) {
                    if (compareStrings(f[1], t.relationLemmaGloss())) {
                        if (compareStrings(f[0], t.subjectLemmaGloss()) && compareStrings(f[2], t.objectLemmaGloss())){
                            System.out.println("Yes");
                            System.exit(0);
                        } else if (compareStrings(f[2], t.subjectLemmaGloss()) && compareStrings(f[0], t.objectLemmaGloss())) {
                            System.out.println("Yes");
                            System.exit(0);
                        }
                    }
                }
            }
        }

        System.out.println("No.");
    }
}
