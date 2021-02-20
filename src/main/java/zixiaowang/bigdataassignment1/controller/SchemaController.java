package zixiaowang.bigdataassignment1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SchemaController {

    @PostMapping("/plan/schema")
    public ResponseEntity<Map<String,String>> createSchema(@RequestBody String schema){
        Map<String,String> result = new HashMap<>();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("./planSchema.json"));
            out.write(schema);
            out.close();
        } catch (IOException e) {
            result.put("message","IO Exception!");
            e.printStackTrace();
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        result.put("message","Plan Schema Create!");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
