package zixiaowang.bigdataassignment1.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.JsonNodeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;

public class JsonUtil {

    Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * string to JsonNode
     * @param jsonStr
     * @return jsonNode from input jsonStr
     */
    public static JsonNode strToJsonNode(String jsonStr){
        JsonNode jsonNode = null;
        try{
            jsonNode = JsonLoader.fromString(jsonStr);
        }catch (IOException e){
            e.printStackTrace();
        }
        return jsonNode;
    }

    /**
     * get pre store json schema
     * @param jsonFilePath
     * @return jsonNode from json schema file
     */
    public static JsonNode getSchemaJsonNodeFromFilePath(String jsonFilePath){
        JsonNode jsonSchemaNode = null;
        try{
            jsonSchemaNode= new JsonNodeReader().fromReader(new FileReader(ResourceUtils.getFile(jsonFilePath)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return jsonSchemaNode;
    }
}
