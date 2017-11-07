import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;

import java.util.List;
import java.util.Properties;

public class NLP2 {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP coreNLP = new StanfordCoreNLP(props);
//        File foo = new File("foo.txt");
//        Collection<File> files = new ArrayList<File>();
//        files.add(foo);
//        try {
//			coreNLP.processFiles(files);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // read some text in the text variable
        //String text = "Pick up that block";
        //String text = "";
        String text = "Finally, we can afford to buy a new house";
        //String text = "In 1921, Einstein received the Nobel Prize for his original work on the photoelectric effect.";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        System.out.println(text.toString());
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println("word " + word + " ,pos: " + pos + " ,ne: " + ne);
            }
            //this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println();
            System.out.println(tree);



            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            System.out.println();
            System.out.println(dependencies);

            IndexedWord root = dependencies.getFirstRoot();
            // type of root
            String type = root.tag();
            switch (type) {
                case "VB": processVerbPhrase(dependencies, root); break;
                case "NN": processNounPhrase(dependencies, root); break;
                case "DT": processDeterminer(dependencies, root); break;
                default: System.out.println("Cannot identify sentence structure.");
            }
            IndexedWord subject = null;
            String quantifier = null;
            Boolean goodToGo = false;
            Boolean TopLevelNegation = false;
            Boolean PredicateLevelNegation = false;

            //List<Pair<GrammaticalRelation,IndexedWord>> s = dependencies.childPairs(predicate);
//            for (Pair<GrammaticalRelation,IndexedWord> item : s) {
//        	    if (item.first.toString().equals("nsubj")) {
//        		  subject = item.second;
//                }
//        	    if (item.first.toString().equals("cop") && (item.second.originalText().toLowerCase().equals("are") || item.second.originalText().toLowerCase().equals("is"))) {
//        		    goodToGo = true;
//                }
//        	    if (item.first.toString().equals("neg")) {
//        	        PredicateLevelNegation = true;
//        	    }
//            }

        }


    }
    // Processes: {This, that} one?
    static public void processDeterminer(SemanticGraph dependencies, IndexedWord root){
        List<Pair<GrammaticalRelation,IndexedWord>> s = dependencies.childPairs(root);

        System.out.println("Identity of object: " + root.originalText().toLowerCase());
    }

    //Processes: {That, this, the} {block, sphere}
    static public void processNounPhrase(SemanticGraph dependencies, IndexedWord root){
        List<Pair<GrammaticalRelation,IndexedWord>> s = dependencies.childPairs(root);

        System.out.println("Identity of object: " + root.originalText().toLowerCase());
        System.out.println("Type of object: " + s.get(0).second.originalText().toLowerCase());
    }

    // Processes: {Pick up, put down} {that, this} {block, sphere}
    static public void processVerbPhrase(SemanticGraph dependencies, IndexedWord root){
        List<Pair<GrammaticalRelation,IndexedWord>> s = dependencies.childPairs(root);
        Pair<GrammaticalRelation,IndexedWord> prt = s.get(0);
        Pair<GrammaticalRelation,IndexedWord> dobj = s.get(1);

        List<Pair<GrammaticalRelation,IndexedWord>> newS = dependencies.childPairs(dobj.second);

        System.out.println("Action: " + root.originalText().toLowerCase() + prt.second.originalText().toLowerCase());
        System.out.println("Type of object: " + dobj.second.originalText().toLowerCase());
        System.out.println("Identity of object: " + newS.get(0).second.originalText().toLowerCase());
    }
}
