package zixiaowang.bigdataassignment1.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import zixiaowang.bigdataassignment1.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlanService {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * insert new plan and return the objectid
     * @param newPlanJsonStr
     * @return
     * @throws ProcessingException
     */
    public ResponseEntity<Map<Object,Object>> insert(String newPlanJsonStr) throws ProcessingException {
        Map<String,String> result = new HashMap();

        // if the input is valid
        if(newPlanJsonStr == null){
            result.put("message","request body error");
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        }

        // fge validate the json body
        JsonNode newPlanJsonNode = JsonUtil.strToJsonNode(newPlanJsonStr);
        JsonNode schemaJsonNode = JsonUtil.getSchemaJsonNodeFromFilePath("./planSchema.json");
        ProcessingReport report = JsonSchemaFactory
                .byDefault()
                .getJsonSchema(schemaJsonNode)
                .validate(newPlanJsonNode);

        if(report.isSuccess()){
            // validate success!

            JSONObject jsonObject = JSONObject.parseObject(newPlanJsonStr);

            Document doc = Document.parse(newPlanJsonStr);

            // check if the objectId is exist
            Query query = new Query(Criteria.where("objectId").is(jsonObject.getString("objectId")));
            JSONObject jsonFindObject = mongoTemplate.findOne(query,JSONObject.class,"Plan");

            if(jsonFindObject==null){
                Document data = mongoTemplate.insert(doc,"Plan");
                result.put("message","insert success! object id: "+jsonObject.getString("objectId"));


                // set etag for conditional read
                int hashCode = jsonObject.toString().hashCode();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("If-None-Match",
                        String.valueOf(hashCode));

                return new ResponseEntity(result, responseHeaders,HttpStatus.OK);
            }else{
                result.put("message","repeat object! object id: "+jsonObject.getString("objectId"));
                return new ResponseEntity(result,HttpStatus.BAD_REQUEST);
            }
        }
        result.put("message","validation failed!");
        return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<JSONObject> find(String planObjectId){
        // check if the objectId is exist
        Query query = new Query(Criteria.where("objectId").is(planObjectId));
        JSONObject jsonFindObject = mongoTemplate.findOne(query,JSONObject.class,"Plan");

        if(jsonFindObject!=null){
            // set etag for conditional read
            int hashCode = jsonFindObject.toString().hashCode();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("If-None-Match",
                    String.valueOf(hashCode));

            return new ResponseEntity(jsonFindObject,responseHeaders,HttpStatus.OK);
        }
        jsonFindObject = new JSONObject();
        jsonFindObject.put("message","no such medical plan! object id: "+planObjectId);
        return new ResponseEntity(jsonFindObject,HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Map<Object,Object>> delete(String planObjectId){
        Map<String,String> result = new HashMap<>();

        // check if the objectId is exist
        Query query = new Query(Criteria.where("objectId").is(planObjectId));
        JSONObject jsonFindObject = mongoTemplate.findAndRemove(query,JSONObject.class,"Plan");

        if(jsonFindObject!=null){
            result.put("message","medical plan delete success! object id: "+planObjectId);
            return new ResponseEntity(result,HttpStatus.OK);
        }
        result.put("message","no such medical plan! object id: "+planObjectId);
        return new ResponseEntity(result,HttpStatus.BAD_REQUEST);
    }

//    public ResponseEntity<JSONObject> find(String planJsonStr){
//        // check if the objectId is exist
//        Query query = new Query(Criteria.where("objectId").is(planJsonStr));
//        JSONObject jsonFindObject = mongoTemplate.findOne(query,JSONObject.class,"Plan");
//
//        if(jsonFindObject!=null){
//            return new ResponseEntity(jsonFindObject,HttpStatus.OK);
//        }
//        jsonFindObject = new JSONObject();
//        jsonFindObject.put("message","no such medical plan! object id: "+planJsonStr);
//        return new ResponseEntity(jsonFindObject,HttpStatus.NO_CONTENT);
//
//    }
}
