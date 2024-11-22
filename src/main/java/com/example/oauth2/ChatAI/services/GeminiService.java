package com.example.oauth2.ChatAI.services;


import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;
import com.example.oauth2.SapoStore.payload.reponse.ProductResponse;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import com.example.oauth2.SapoStore.repository.ProductRepository;
import com.example.oauth2.SapoStore.repository.StoreRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import com.example.oauth2.SapoStore.service.iservice.IProductService;
import com.example.oauth2.SapoStore.service.iservice.IStoreService;
import com.google.gson.Gson;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    private static final String GEMINI_MODEL = "gemini-1.5-flash";
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IProductOfStoreService iProductOfStoreService;


    String dataSystem="";
    String conversationHistory="";
    String modelProduct = "Bảng Product có cấu trúc :" +
            "proId: int (Primary Key, mã định danh sản phẩm)  \n" +
            "proName: String (Tên sản phẩm)  \n" +
            "slug: String (Đường dẫn URL ngắn)  \n" +
            "thumbnail: String (Đường dẫn hình ảnh thu nhỏ)  \n" +
            "description: String (Mô tả chi tiết sản phẩm)  \n" +
            "category: String (Danh mục sản phẩm) ";
    String modelProductOS ="Bảng sản phẩm chi tiết có cấu trúc :" +
            "id: Long (Primary Key, mã định danh sản phẩm)  \n" +
            "priceO: Long (Giá gốc của sản phẩm)  \n" +
            "priceI: Long (Giá nhập vào của sản phẩm)  \n" +
            "discount: double (Tỷ lệ giảm giá, theo phần trăm)  \n" +
            "CU: String (Đơn vị tiền tệ, mặc định là \"VND\")  \n" +
            "view: long (Số lượt xem sản phẩm)  \n" +
            "status: boolean (Trạng thái sản phẩm: true = còn hàng, false = hết hàng)  \n" +
            "proName: String (Tên sản phẩm)  \n" +
            "description: String (Mô tả chi tiết sản phẩm)  \n" +
            "quantity: int (Số lượng sản phẩm còn trong kho)  \n" +
            "evaluate: double (Đánh giá trung bình của sản phẩm từ người dùng)  \n" +
            "slug: String (Đường dẫn URL ngắn cho sản phẩm)  \n" +
            "category: String (Danh mục sản phẩm)  \n" +
            "storeName: String (Tên cửa hàng bán sản phẩm)  \n" +
            "storeCode: String (Mã cửa hàng)  \n" +
            "thumbnail: String (Đường dẫn hình ảnh thu nhỏ, mặc định là URL ảnh từ Cloudinary)\n";
    private static final String API_KEY ="AIzaSyDoQ2Te_XGgB12biKBDd9Js3jXcKqOXMTU";
//    @Scheduled(cron = "0 0 0 * * ?")
    public  void loadData() {
        StringBuilder dataProduct = new StringBuilder();
        StringBuilder dataProductOS= new StringBuilder();
        List<ProductResponse> productResponses = iProductService.getAllData();
        for (ProductResponse productResponse : productResponses) {
            if (productResponse != null) {
                dataProduct.append(new Gson().toJson(productResponse)); // Chuyển từng đối tượng sang JSON
            }
        }
        List<ProductOfStoreResponse> productOfStoreResponses = iProductOfStoreService.getAllData();
        for (ProductOfStoreResponse productOfStoreResponse : productOfStoreResponses){
            if (productOfStoreResponse != null) {
                dataProductOS.append(new Gson().toJson(productOfStoreResponse)); // Chuyển từng đối tượng sang JSON
            }
        }
        dataSystem = "Product Data: " + dataProduct.toString() + "\n" +
                "Product of Store Data: " + dataProductOS.toString() +
                "| Biết các dữ liệu ở dạng Json. 2 bảng có các trường dữ liệu tách biệt. Bạn hãy hiểu cấu trúc bảng dựa theo các thông tin sau :  " + modelProduct + "và " + modelProductOS;

        System.out.println(dataSystem);
        conversationHistory = "Bạn hãy trả lời dựa trên những thông tin sau : "+
                " dataSystem : "+ dataSystem +
                "conversationHistory = \"\"\"\n" +
                "    Bạn hãy trả lời dựa trên những thông tin sau:\n" +
                "    dataSystem: %s\n" +
                "    Bạn hãy hiểu cấu trúc thông tin như sau:\n" +
                "    Ví dụ 1:\n" +
                "    proId: 1\n" +
                "    proName: Máy giặt LG\n" +
                "    slug: máy-giặt-lg\n" +
                "    thumbnail: https://res.cloudinary.com/dqvr7kat6/image/upload/v1730533798/vzad1wsbaw3ecwbktpg9.jpg\n" +
                "    description: Máy giặt LG siêu nhanh\n" +
                "    category: Máy giặt\n" +
                "    -> Sản phẩm có tên là *Máy giặt LG*, thuộc danh mục *Máy giặt* và có mô tả sản phẩm là *Máy giặt LG siêu nhanh*.\n" +
                "    \n" +
                "    Ví dụ 2:\n" +
                "    id: 1\n" +
                "    priceO: 11000\n" +
                "    priceI: 10000\n" +
                "    discount: 10.0\n" +
                "    CU: VND\n" +
                "    view: 13\n" +
                "    status: true\n" +
                "    proName: Quạt điện to\n" +
                "    description: Quá nà đẹp với công suất lớn\n" +
                "    quantity: -11\n" +
                "    evaluate: 4.0\n" +
                "    slug: quạt-điện-to\n" +
                "    category: Quạt\n" +
                "    storeName: Duy Thuong\n" +
                "    storeCode: SAPO235072\n" +
                "    thumbnail: https://res.cloudinary.com/dqvr7kat6/image/upload/v1721289530/agbhiqut7wyrgpjcgxm9.jpg\n" +
                "    -> Sản phẩm có tên là *Quạt điện to*, mô tả là *Quá nà đẹp với công suất lớn*, thuộc danh mục *Quạt*, thuộc cửa hàng *Duy Thuong*, có giá bán *11000 VND*, có lượt xem *13*.\n" +
                "    \n" +
                "    Từ những ví dụ này, bạn sẽ tự động phân tích, đánh giá sản phẩm, và đưa ra gợi ý phù hợp cho khách hàng dựa trên dữ liệu có trong `dataSystem`. \n" +
                "    Bạn không trả lời những thông tin không liên quan hoặc không cần thiết.\n" +
                "    \n" +
                "    Khi khách chào mình, bạn hãy chào lại bằng đúng ngôn ngữ mà khách sử dụng.\n" +
                "\"\"\".formatted(dataSystem);\n"+
                "Khi khách chào mình thì mình chào lại bằng đúng ngôn ngữ đó ";
    }

    public String chat(String prompt) {
        if (conversationHistory.isBlank()){
            loadData();
        }
        //Adding previous conversation history
        String fullPrompt = prompt;

        //Add the old history for adding context
        if (!Strings.isBlank(conversationHistory)) {
            fullPrompt = "[Context]" + conversationHistory + " [Content] " + prompt;
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare request entity
        fullPrompt = getPromptBody(fullPrompt);
        HttpEntity<String> requestEntity = new HttpEntity<>(fullPrompt, headers);


        // Perform HTTP POST request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key="+ API_KEY,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

        // Handle the response based on the status code
        if (statusCode == HttpStatus.OK) {
            String responseText = responseEntity.getBody();
            try {
                responseText = parseGeminiResponse(responseText);
                conversationHistory += prompt + "\n" + responseText + "\n"; // Update conversation history
            } catch (Exception e) {
                logger.error("Error in Parding");
            }
            return responseText; // Return the fetched summary response
        } else {
            throw new RuntimeException("API request failed with status code: " + statusCode + " and response: " + responseEntity.getBody());
        }
    }

    public String getPromptBody(String prompt) {
        // Create prompt for generating summary in document language
        JSONObject promptJson = new JSONObject();

        // Array to contain all the content-related data, including the text and role
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        contentsObject.put("role", "user");

        // Array to hold the specific parts (or sections) of the user's input text
        JSONArray partsArray = new JSONArray();
        JSONObject partsObject = new JSONObject();
        partsObject.put("text", prompt);
        partsArray.add(partsObject);
        contentsObject.put("parts", partsArray);

        contentsArray.add(contentsObject);
        promptJson.put("contents", contentsArray);

        // Array to hold various safety setting objects to ensure the content is safe and appropriate
        JSONArray safetySettingsArray = new JSONArray();

        // Adding safety settings for hate speech
        JSONObject hateSpeechSetting = new JSONObject();
        hateSpeechSetting.put("category", "HARM_CATEGORY_HATE_SPEECH");
        hateSpeechSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(hateSpeechSetting);

        // Adding safety settings for dangerous content
        JSONObject dangerousContentSetting = new JSONObject();
        dangerousContentSetting.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        dangerousContentSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(dangerousContentSetting);

        // Adding safety settings for sexually explicit content
        JSONObject sexuallyExplicitSetting = new JSONObject();
        sexuallyExplicitSetting.put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT");
        sexuallyExplicitSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(sexuallyExplicitSetting);

        // Adding safety settings for harassment content
        JSONObject harassmentSetting = new JSONObject();
        harassmentSetting.put("category", "HARM_CATEGORY_HARASSMENT");
        harassmentSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(harassmentSetting);

        promptJson.put("safetySettings", safetySettingsArray);

        // Creating and setting generation configuration parameters such as temperature and topP
        JSONObject parametersJson = new JSONObject();
        parametersJson.put("temperature", 0.5);
        parametersJson.put("topP", 0.99);
        promptJson.put("generationConfig", parametersJson);

        // Convert the JSON object to a JSON string
        return promptJson.toJSONString();
    }

    public String parseGeminiResponse(String jsonResponse) throws IOException, ParseException {
        // Parse the JSON string
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonResponse);

        // Get the "candidates" array
        JSONArray candidatesArray = (JSONArray) jsonObject.get("candidates");

        // Assuming there's only one candidate (index 0), extract its content
        JSONObject candidateObject = (JSONObject) candidatesArray.get(0);
        JSONObject contentObject = (JSONObject) candidateObject.get("content");

        // Get the "parts" array within the content
        JSONArray partsArray = (JSONArray) contentObject.get("parts");

        // Assuming there's only one part (index 0), extract its text
        JSONObject partObject = (JSONObject) partsArray.get(0);
        String responseText = (String) partObject.get("text");

        return responseText;
    }
}
