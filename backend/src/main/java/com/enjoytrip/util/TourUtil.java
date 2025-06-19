package com.enjoytrip.util;

import com.enjoytrip.model.dto.AttractionDataDTO;
import com.enjoytrip.model.dto.GugunsDataDTO;
import com.enjoytrip.model.dto.SidosDataDTO;
import com.google.gson.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TourUtil {
    @Value("${tour.api.url}")
    private String tourUrl;
    @Value("${tour.api.key}")
    private String eKey;

    public List<SidosDataDTO> getSidosInfo(int no) {
        try {
            ArrayList<SidosDataDTO> dtoList = new ArrayList<>();

            HttpURLConnection conn = null;
            JsonParser jsonParser = new JsonParser();
            ObjectMapper objectMapper = new ObjectMapper();

            StringBuilder urlBuilder = new StringBuilder(tourUrl + "/areaCode1");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + eKey);
            urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=" + URLEncoder.encode("EnjoyTrip", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(String.valueOf(no), "UTF-8"));

            conn = getConnection(urlBuilder.toString());
            if (conn == null) return null;

            String line;
            BufferedReader br = null;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                while((line = br.readLine())!=null) {
                    sb.append(line);
                }

                XmlMapper xmlMapper = new XmlMapper();
                JsonNode jsonNode = xmlMapper.readTree(sb.toString());

                String jsonString = objectMapper.writeValueAsString(jsonNode);

                JsonObject json = (JsonObject) jsonParser.parse(jsonString);

                JsonObject cmmMsgHeader = json.getAsJsonObject("cmmMsgHeader");
                if (cmmMsgHeader != null) {
                    String returnReasonCode = cmmMsgHeader.get("returnReasonCode").getAsString();
                    if ("22".equals(returnReasonCode)) {
                        System.out.println("[DEBUG] 요청 초과 오류 발생. return null.");
                        return null;
                    }
                }

                int numOfRows = json.getAsJsonObject("body").get("numOfRows").getAsInt();
                if (numOfRows == 0) return null;

                JsonElement itemsElement = ((JsonObject) json.get("body")).getAsJsonObject("items").get("item");

                JsonArray list;
                if (itemsElement.isJsonArray()) {
                    list = itemsElement.getAsJsonArray(); // 이미 배열이면 그대로 사용
                } else {
                    list = new JsonArray();
                    list.add(itemsElement); // 단일 객체라면 배열로 변환
                }

                dtoList.addAll(Arrays.asList(new ObjectMapper().readValue(list.toString(), SidosDataDTO[].class)));
            }

            return dtoList;
        } catch(Exception e) {
            System.out.println("Failed to fetch data from external API");
            return null;
        }
    }

    public List<GugunsDataDTO> getGugunsInfo(int sido, int no) {
        try {
            ArrayList<GugunsDataDTO> dtoList = new ArrayList<>();

            HttpURLConnection conn = null;
            JsonParser jsonParser = new JsonParser();
            ObjectMapper objectMapper = new ObjectMapper();

            StringBuilder urlBuilder = new StringBuilder(tourUrl + "/areaCode1");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + eKey);
            urlBuilder.append("&" + URLEncoder.encode("areaCode","UTF-8") + "=" + URLEncoder.encode(String.valueOf(sido), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=" + URLEncoder.encode("EnjoyTrip", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(String.valueOf(no), "UTF-8"));

            conn = getConnection(urlBuilder.toString());
            if (conn == null) return null;

            String line;
            BufferedReader br = null;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                while((line = br.readLine())!=null) {
                    sb.append(line);
                }

                XmlMapper xmlMapper = new XmlMapper();
                JsonNode jsonNode = xmlMapper.readTree(sb.toString());

                String jsonString = objectMapper.writeValueAsString(jsonNode);

                JsonObject json = (JsonObject) jsonParser.parse(jsonString);

                JsonObject cmmMsgHeader = json.getAsJsonObject("cmmMsgHeader");
                if (cmmMsgHeader != null) {
                    String returnReasonCode = cmmMsgHeader.get("returnReasonCode").getAsString();
                    if ("22".equals(returnReasonCode)) {
                        System.out.println("[DEBUG] 요청 초과 오류 발생. return null.");
                        return null;
                    }
                }

                int numOfRows = json.getAsJsonObject("body").get("numOfRows").getAsInt();
                if (numOfRows == 0) return null;

                JsonElement itemsElement = ((JsonObject) json.get("body")).getAsJsonObject("items").get("item");

                JsonArray list;
                if (itemsElement.isJsonArray()) {
                    list = itemsElement.getAsJsonArray(); // 이미 배열이면 그대로 사용
                } else {
                    list = new JsonArray();
                    list.add(itemsElement); // 단일 객체라면 배열로 변환
                }

                dtoList.addAll(Arrays.asList(new ObjectMapper().readValue(list.toString(), GugunsDataDTO[].class)));
            }
            return dtoList;
        } catch(Exception e) {
            System.out.println("Failed to fetch data from external API");
            return null;
        }
    }

    public List<AttractionDataDTO> searchAttractionsByAttractionDTO(AttractionDataDTO dto) {
        try {
            ArrayList<AttractionDataDTO> dtoList = new ArrayList<>();

            int index = 1;
            while (true) {
                HttpURLConnection conn = null;
                JsonParser jsonParser = new JsonParser();
                ObjectMapper objectMapper = new ObjectMapper();

                StringBuilder urlBuilder = new StringBuilder(tourUrl + "/areaBasedList1");
                urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + eKey);
                urlBuilder.append("&" + URLEncoder.encode("listYN","UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("areaCode","UTF-8") + "=" + URLEncoder.encode(String.valueOf(dto.getAreacode()), "UTF-8"));
                if (dto.getSigungucode() != null) {
                    urlBuilder.append("&" + URLEncoder.encode("sigunguCode","UTF-8") + "=" + URLEncoder.encode(String.valueOf(dto.getSigungucode()), "UTF-8"));
                }
                if (dto.getContenttypeid() != null) {
                    urlBuilder.append("&" + URLEncoder.encode("contentTypeId","UTF-8") + "=" + URLEncoder.encode(String.valueOf(dto.getContenttypeid()), "UTF-8"));
                }
                urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=" + URLEncoder.encode("EnjoyTrip", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(String.valueOf(index++), "UTF-8"));

                conn = getConnection(urlBuilder.toString());
                if (conn == null) return null;

                String line;
                BufferedReader br = null;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    while((line = br.readLine())!=null) {
                        sb.append(line);
                    }

                    XmlMapper xmlMapper = new XmlMapper();
                    JsonNode jsonNode = xmlMapper.readTree(sb.toString());

                    String jsonString = objectMapper.writeValueAsString(jsonNode);

                    JsonObject json = (JsonObject) jsonParser.parse(jsonString);

                    JsonObject cmmMsgHeader = json.getAsJsonObject("cmmMsgHeader");
                    if (cmmMsgHeader != null) {
                        String returnReasonCode = cmmMsgHeader.get("returnReasonCode").getAsString();
                        if ("22".equals(returnReasonCode)) {
                            System.out.println("[DEBUG] 요청 초과 오류 발생. return null.");
                            return null;
                        }
                    }

                    int numOfRows = json.getAsJsonObject("body").get("numOfRows").getAsInt();
                    if (numOfRows == 0) break;

                    JsonElement itemsElement = ((JsonObject) json.get("body")).getAsJsonObject("items").get("item");

                    JsonArray list;
                    if (itemsElement.isJsonArray()) {
                        list = itemsElement.getAsJsonArray(); // 이미 배열이면 그대로 사용
                    } else {
                        list = new JsonArray();
                        list.add(itemsElement); // 단일 객체라면 배열로 변환
                    }

                    dtoList.addAll(Arrays.asList(new ObjectMapper().readValue(list.toString(), AttractionDataDTO[].class)));
                }
            }

            List<AttractionDataDTO> list = dtoList.stream()
                    .filter(element -> isInKoreanRange(element.getMapx(), element.getMapy()))
                    .collect(Collectors.toList());

            return list;
        } catch(Exception e) {
            System.out.println("Failed to fetch data from external API");
            return null;
        }
    }

    public AttractionDataDTO searchAttractionsByTitle(String title) {
        try {
            HttpURLConnection conn = null;
            JsonParser jsonParser = new JsonParser();
            ObjectMapper objectMapper = new ObjectMapper();

            StringBuilder urlBuilder = new StringBuilder(tourUrl + "/searchKeyword1");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + eKey);
            urlBuilder.append("&" + URLEncoder.encode("keyword","UTF-8") + "=" + URLEncoder.encode(title, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=" + URLEncoder.encode("EnjoyTrip", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

            conn = getConnection(urlBuilder.toString());
            if (conn == null) return null;

            String line;
            BufferedReader br = null;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                while((line = br.readLine())!=null) {
                    sb.append(line);
                }

                XmlMapper xmlMapper = new XmlMapper();
                JsonNode jsonNode = xmlMapper.readTree(sb.toString());

                String jsonString = objectMapper.writeValueAsString(jsonNode);

                JsonObject json = (JsonObject) jsonParser.parse(jsonString);

                JsonObject cmmMsgHeader = json.getAsJsonObject("cmmMsgHeader");
                if (cmmMsgHeader != null) {
                    String returnReasonCode = cmmMsgHeader.get("returnReasonCode").getAsString();
                    if ("22".equals(returnReasonCode)) {
                        System.out.println("[DEBUG] 요청 초과 오류 발생. return null.");
                        return null;
                    }
                }

                int numOfRows = json.getAsJsonObject("body").get("numOfRows").getAsInt();
                if (numOfRows == 0) return null;

                JsonElement itemsElement = ((JsonObject) json.get("body")).getAsJsonObject("items").get("item");

                JsonArray list;
                if (itemsElement.isJsonArray()) {
                    list = itemsElement.getAsJsonArray(); // 이미 배열이면 그대로 사용
                } else {
                    list = new JsonArray();
                    list.add(itemsElement); // 단일 객체라면 배열로 변환
                }

                if (list != null && list.size() > 0) {
                    List<AttractionDataDTO> dtoList = Arrays.asList(
                            objectMapper.readValue(list.toString(), AttractionDataDTO[].class)
                    );

                    AttractionDataDTO attractionDataDTO = dtoList.get(0);

                    if (isInKoreanRange(attractionDataDTO.getMapx(), attractionDataDTO.getMapy())) {
                        return attractionDataDTO;
                    }

                }
            }

            return null;
        } catch(Exception e) {
            System.out.println("Failed to fetch data from external API");
            return null;
        }
    }

    private boolean isInKoreanRange(double x, double y) {
        if (x >= 124.6 && x <= 131.9 && y >= 33 && y <= 38.6) {
            return true;
        }

        return false;
    }

    private HttpURLConnection getConnection(String link) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();
            return conn;
        } catch (Exception e) {
            return null;
        }
    }

}
