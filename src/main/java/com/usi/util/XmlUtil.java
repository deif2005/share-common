package com.usi.util;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * XmlUtil
 * xml操作工具类
 * @date 2016/7/12
 */
public class XmlUtil {

    private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    //用于将bean转成xml时list<Bean>节点的bean的统一节点名称
    private static String defaultListPointName = "data";

    /**
     * 把document对象写入新的文件
     *
     * @param document
     * @throws Exception
     */
    public void writer(Document document) throws Exception {
        // 紧凑的格式
        // OutputFormat format = OutputFormat.createCompactFormat();
        // 排版缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置编码
        format.setEncoding("UTF-8");
        // 创建XMLWriter对象,指定了写出文件及编码格式
        // XMLWriter writer = new XMLWriter(new FileWriter(new
        // File("src//a.xml")),format);
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(
                new FileOutputStream(new File("src//a.xml")), "UTF-8"), format);
        // 写入
        writer.write(document);
        // 立即写入
        writer.flush();
        // 关闭操作
        writer.close();
    }

    /**
     * xml文件转为javaBean
     * @param root 要转为bean的节点
     * @param clazz
     * @return
     */
    public static Object xmlToBean(Element root,Class clazz) throws Exception{
        Object obj;
        try {
            if (root == null)
                return null;
            // 遍历根节点下所有子节点
            Iterator<?> iter = root.elementIterator();
            obj = clazz.newInstance();
            while(iter.hasNext()){
                Element ele = (Element)iter.next();
                //获取set方法中的参数字段（实体类的属性）
                Field field = clazz.getDeclaredField(ele.getName());
                //获取set方法，field.getType())获取它的参数数据类型
                Method method = clazz.getDeclaredMethod("set"+StringUtil.toUpperCaseFirstOne(ele.getName()), field.getType());
                //调用set方法
                method.invoke(obj, ele.getText());
            }
        } catch (Exception e) {
            logger.error("xmlToBean error：", e);
            throw e;
        }
        return obj;
    }

    /**
     * xml文件转bean
     * @param xmlPath xml文件路径
     * @param clazz xml根节点bean
     * @param classMap 可选，对于复杂嵌套list节点，需要指定list节点名称及class
     * @return
     */
    public static Object xmlToBean(String xmlPath,Class clazz,Map<String,Class> classMap) throws Exception{
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSONObject jo = (JSONObject)xmlSerializer.read(FileUtil.readerFileContent(xmlPath,"UTF-8"));
        return JSONObject.toBean(jo,clazz,classMap);
    }

    /**
     * xml文件转List
     * @param xmlPath xml文件路径
     * @param clazz
     * @return
     */
    public static Collection xmlToList(String xmlPath,Class clazz){
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSONArray jo = (JSONArray)xmlSerializer.read(FileUtil.readerFileContent(xmlPath, "UTF-8"));
        return JSONArray.toCollection(jo,clazz);
    }

    /**
     * bean转xml并输出文件
     * @param bean 需要输出xml文件的bean
     * @param rootName 根节点名称
     * @param savePath 保存路径
     */
    public static void beanToXmlFile(Object bean,String rootName,String savePath){
        XMLWriter writer = null;
        try {
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setTypeHintsEnabled(false);
            xmlSerializer.setRootName(rootName);
            //list<bean>节点的统一名称
            xmlSerializer.setElementName(defaultListPointName);
            //将bean转为json对象(支持JSONObject及JSONArray)
            JSON data = JSONSerializer.toJSON(bean);
            //将json改为xml格式
            String xml = xmlSerializer.write(data, "UTF-8");
            //创建输出格式化
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            //设置编码为UTF-8
            outputFormat.setEncoding("UTF-8");
            //生成xml文件
            FileUtil.createDirOrFile(savePath,true);
            //将xml字符串转为xml操作document对象(用于格式化输出)
            Document document = DocumentHelper.parseText(xml);
            //输出xml文件
            writer = new XMLWriter(new FileOutputStream(new File(savePath)), outputFormat);
            writer.write(document);
        } catch (Exception e) {
            logger.error("生成XML文件异常：",e);
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        try {
            /*String xmlPath = "D:/upload/test/123.xml";
            Map<String, Class> classMap = new HashMap<>();
            classMap.put("sectionList",SectionXml.class);
            classMap.put("questionList",QuestionXml.class);
            classMap.put("resourceList",ResourceXml.class);
            classMap.put("topicList",TopicXml.class);
            PaperXml xml = (PaperXml)xmlToBean(xmlPath,PaperXml.class,classMap);
            System.out.println(xml.getName());*/

            /*String xmlPath = "D:/project/ICES/ICES/doc/ICES系统设计/kwb/subjects.xml";
            List<Subject> list = (List<Subject>)xmlToList(xmlPath,Subject.class);
            System.out.println(list.get(0).toString());*/

//            String json = "{\"id\":\"444\",\"name\":\"测试勿删003\",\"remark\":\"123123\",\"length\":\"120\",\"score\":\"100.00\",\"passScore\":\"60.00\",\"author\":\"11111\",\"createTime\":\"2016-07-16 16:09:30.0\",\"topicList\":[{\"id\":\"1\",\"name\":\"第一章节\",\"score\":\"100.00\",\"remark\":\"第一章节\",\"sectionList\":[{\"id\":\"444\",\"name\":\"选择题\",\"score\":\"3.00\",\"remark\":\"不知道说什么\",\"questionList\":[{\"index\":\"1\",\"id\":\"444\",\"questionType\":{\"code\":101,\"name\":\"单选题\"},\"content\":\"<p>1+1?<br/><\\/p>\",\"analysis\":\"<p>解析...<\\/p>\",\"answer\":[\"10102\"],\"score\":\"3.00\",\"difficulty\":\"2\",\"resourceList\":[{\"id\":\"963a1c95-4a6a-11e6-96a4-2047478070ea\",\"name\":\"test.jpg\",\"fileDir\":\"/resource/upload/question/2016-07-16/jpg/test.jpg\"},{\"id\":\"92a4a4a2-c30c-4a0f-a510-c9d7e7bc6e56\",\"name\":\"407aadffe88c4786b90592acdd3a72bd-1\",\"fileDir\":\"/resource/upload/question/2016-07-16/jpg/407aadffe88c4786b90592acdd3a72bd-1.jpg\"}]},{\"index\":\"1\",\"id\":\"555\",\"questionType\":{\"code\":101,\"name\":\"单选题\"},\"content\":\"<p>1+1?<br/><\\/p>\",\"analysis\":\"<p>解析...<\\/p>\",\"answer\":[\"10102\"],\"score\":\"3.00\",\"difficulty\":\"2\",\"resourceList\":[{\"id\":\"0488f883-4d7e-11e6-96a4-2047478070ea\",\"name\":\"407aadffe88c4786b90592acdd3a72bd-1\",\"fileDir\":\"/resource/upload/question/2016-07-16/jpg/407aadffe88c4786b90592acdd3a72bd-1.jpg\"},{\"id\":\"048f6c80-4d7e-11e6-96a4-2047478070ea\",\"name\":\"test\",\"fileDir\":\"/resource/upload/question/2016-07-16/jpg/test.jpg\"}]}]}]}]}";

            /*Document document = DocumentHelper.createDocument();
            //写入文件试卷节点内容
            Element paperPoint = document.addElement("paper");
            Element paperId = paperPoint.addElement("paper_id");
            paperId.addText("测试......");*/

            /*//增加根节点
            Element books = doc.addElement("books");
            //增加子元素
            Element book1 = books.addElement("book");
            Element title1 = book1.addElement("title");
            Element author1 = book1.addElement("author");

            Element book2 = books.addElement("book");
            Element title2 = book2.addElement("title");
            Element author2 = book2.addElement("author");

            //为子节点添加属性
            book1.addAttribute("id", "001");
            //为元素添加内容
            title1.setText("Harry Potter");
            author1.setText("J K. Rowling");

            book2.addAttribute("id", "002");
            title2.setText("Learning XML");
            author2.setText("Erik T. Ray");*/

           /* //实例化输出格式对象
            OutputFormat format = OutputFormat.createPrettyPrint();
            //设置输出编码
            format.setEncoding("UTF-8");
            //创建需要写入的File对象
            File file = new File("D:" + File.separator + "books.xml");
            //生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            //开始写入，write方法中包含上面创建的Document对象
            writer.write(document);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
