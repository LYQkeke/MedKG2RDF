
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by KEKE on 2018/11/19
 */
public class ModelSaver {

    // OWL 文件路径
    private String inputDir = "./src/main/resources/out.data";
    private String owlDir = "./src/main/resources/test.owl";
    // 模型构建信息
    private Model model = null;
    private String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    private String owl = "http://www.w3.org/2002/07/owl#";
    private String meta = "http://kse.seu.edu.cn/medkg/meta#";
    private String xsd = "http://www.w3.org/2001/XMLSchema#";
    private Resource article = null;
    private Resource p = null;
    private Resource i = null;
    private Resource c = null;
    private Resource o = null;
    private Resource s = null;
    private Property title = null;
    private Property has_p = null;
    private Property has_i = null;
    private Property has_c = null;
    private Property has_o = null;
    private Property has_s = null;

    /**
     * 默认构造函数，初始化Model
     */
    public ModelSaver(){

        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("rdf", rdf);
        model.setNsPrefix("rdfs", rdfs);
        model.setNsPrefix("owl", owl);
        model.setNsPrefix("meta", meta);
        model.setNsPrefix("xsd", xsd);

        // 文章
        article = model.createResource(meta + "Article");
        article.addProperty(RDF.type, OWL.Class);
        // 标题属性
        title = model.createProperty(meta + "title");
        title.addProperty(RDF.type, OWL.DatatypeProperty)
                .addProperty(RDFS.domain, article);
        // 研究对象 People
        p = model.createResource(meta + "P");
        p.addProperty(RDF.type, OWL.Class);
        // 干预措施 intervening measure
        i = model.createResource(meta + "I");
        i.addProperty(RDF.type, OWL.Class);
        // 比较措施 compare measure
        c = model.createResource(meta + "C");
        c.addProperty(RDF.type, OWL.Class);
        // 结局 outcome
        o = model.createResource(meta + "O");
        o.addProperty(RDF.type, OWL.Class);
        // 研究方法 study
        s = model.createResource(meta + "S");
        i.addProperty(RDF.type, OWL.Class);
        // has_p
        has_p = model.createProperty(meta + "has_p");
        has_p.addProperty(RDF.type, OWL.ObjectProperty)
                .addProperty(RDFS.domain, article)
                .addProperty(RDFS.range, p);
        // has_i
        has_i = model.createProperty(meta + "has_i");
        has_i.addProperty(RDF.type, OWL.ObjectProperty)
                .addProperty(RDFS.domain, article)
                .addProperty(RDFS.range, i);
        // has_c
        has_c = model.createProperty(meta + "has_c");
        has_c.addProperty(RDF.type, OWL.ObjectProperty)
                .addProperty(RDFS.domain, article)
                .addProperty(RDFS.range, c);
        // has_o
        has_o = model.createProperty(meta + "has_o");
        has_o.addProperty(RDF.type, OWL.ObjectProperty)
                .addProperty(RDFS.domain, article)
                .addProperty(RDFS.range, o);
        // has_s
        has_s = model.createProperty(meta + "has_s");
        has_s.addProperty(RDF.type, OWL.ObjectProperty)
                .addProperty(RDFS.domain, article)
                .addProperty(RDFS.range, s);
//        Resource article1 = model.createResource(meta+"article1");
//        article1.addProperty(RDF.type, article)
//                .addProperty(RDF.type, OWL2.NamedIndividual).addProperty(title, "this is a title");
    }

    /**
     * 保存owl文件
     */
    public void save2owl(){

        try {
            System.out.println(System.getProperty("user.dir"));
            File file = new File(owlDir);
            FileOutputStream outputStream = new FileOutputStream(file, false);
            outputStream.write("<?xml version=\"1.0\"?>\n".getBytes());
            RDFDataMgr.write(outputStream,model, Lang.RDFXML);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void build2Model(){
        try {
            File file = new File(inputDir);
            Scanner in = null;
            in = new Scanner(file);
//            System.out.println(in.nextLine());
            while (in.hasNextLine()){
                String[] extResults = in.nextLine().split("\\|");
                String curIdx = extResults[0].trim();
                String curTitle = extResults[1].trim();
                Resource curArticle = model.createResource(meta + curIdx);
                curArticle.addProperty(RDF.type, article)
                        .addProperty(RDF.type, OWL2.NamedIndividual)
                        .addProperty(title, curTitle);
                String[] extItems = extResults[2].trim().split(";");
                for (String extItem: extItems){
                    String[] idValue = extItem.split("@");
                    int id = Integer.parseInt(idValue[0].trim());
                    String value = idValue[1].replace(" ", "_");
                    Property curProperty = null;
                    Resource curClass = null;
                    switch (id){
                        case 402:
                            curClass = o;
                            curProperty = has_o;
                            break;
                        case 403:
                            curClass = s;
                            curProperty = has_s;
                            break;
                        case 404:
                            curClass = p;
                            curProperty = has_p;
                            break;
                        case 420:
                            curClass = i;
                            curProperty = has_i;
                            break;
                        case 421:
                            curClass = c;
                            curProperty = has_c;
                            break;
                        default:
                    }
                    Resource curInstance = model.createResource(meta + value);
                    curInstance.addProperty(RDF.type, curClass)
                        .addProperty(RDF.type, OWL2.NamedIndividual);
                    curArticle.addProperty(curProperty, curInstance);
//                    System.out.println();
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){

        ModelSaver saver = new ModelSaver();
        saver.build2Model();
        saver.save2owl();

    }
}
