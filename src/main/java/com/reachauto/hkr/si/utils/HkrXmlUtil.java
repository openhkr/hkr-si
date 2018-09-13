package com.reachauto.hkr.si.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HkrXmlUtil {

    private static XStream getXStream(Class cl){

        Class[] clazzList = new Class[]{cl};

        XStream xStream = new XStream(new Xpp3DomDriver(nameCoder)) {

            /**
             * @param next
             * @return
             */
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return createSkipOverElementMapperWrapper(next);
            }

        };

        XStream.setupDefaultSecurity(xStream);
        // 设置允许使用的类
        xStream.allowTypes(clazzList);
        /**
         * 这种禁用对象图支持和治疗对象结构就像一个树。复制被当作两个独立的对象的引用,循环引用导致异常。这是稍快,使用更少的内存比其他两种模式。
         */
        xStream.setMode(XStream.NO_REFERENCES);

        xStream.processAnnotations(clazzList);

        return xStream;
    }

    /**
     * xml 转Bean
     * @param xml
     * @param cl
     * @param <T>
     * @return
     */
    public static <T> T readBeanFromXML(String xml, Class<T> cl) {
        if(StringUtils.isBlank(xml)){
            return null;
        }
        XStream xStream = getXStream(cl);
        try {
            return (T) xStream.fromXML(xml);
        } catch (Exception e) {
            log.error("解析xml失败".concat(xml), e);
        }
        return null;
    }

    /**
     * Bean 转xml
     * @param object
     * @return
     */
    public static String toXml(Object object, Class cl){
        XStream xStream = getXStream(cl);
        return xStream.toXML(object);
    }

    /**
     * 转换过程中特殊字符转码用以解决  _ 被解析成 __ 的问题
     */
    private static NameCoder nameCoder = new NameCoder() {
        @Override
        public String encodeNode(String arg0) {
            return arg0;
        }
        @Override
        public String encodeAttribute(String arg0) {
            return arg0;
        }
        @Override
        public String decodeNode(String arg0) {
            return arg0;
        }
        @Override
        public String decodeAttribute(String arg0) {
            return arg0;
        }
    };

    /**
     * 用以解决xml中有的元素，bean中没有时抛异常的问题
     * @param mapper
     * @return
     */
    private static MapperWrapper createSkipOverElementMapperWrapper(Mapper mapper) {
        MapperWrapper resMapper = new MapperWrapper(mapper) {
            @Override
            public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
                // 不能识别的节点，掠过。
                if (definedIn == Object.class) {
                    return false;
                }
                return super.shouldSerializeMember(definedIn, fieldName);
            }
        };
        return resMapper;
    }
}
