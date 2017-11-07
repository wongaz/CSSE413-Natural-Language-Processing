import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

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
        String text = "Pick up that block";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        System.out.println(text.toString());

    }
}
