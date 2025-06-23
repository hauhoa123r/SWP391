package org.project.geolocation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.project.entity.HospitalEntity;
import org.project.entity.PatientEntity;
import org.project.enums.FamilyRelationship;
import org.project.enums.UserRole;
import org.project.model.dai.GeolocationDAI;
import org.project.model.response.DistanceShortestResponse;
import org.project.repository.HospitalRepository;
import org.project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeocodingService {

    private final DistanceCalculator distanceCalculator;
    private final HospitalRepository hospitalRepository;
    private final PatientRepository patientRepository;
    private final double distanceShortest = Double.MAX_VALUE;
    @Value("${opencage.api.key}")
    private String apiKey;

    public GeocodingService(DistanceCalculator distanceCalculator,
                            HospitalRepository hospitalRepository,
                            PatientRepository patientRepository) {
        this.distanceCalculator = distanceCalculator;
        this.hospitalRepository = hospitalRepository;
        this.patientRepository = patientRepository;
    }

    private GeolocationDAI getGeoLocation(String address) {
        GeolocationDAI geo = new GeolocationDAI();
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String urlString = "https://api.opencagedata.com/geocode/v1/json?q=" + encodedAddress
                    + "&key=" + apiKey
                    + "&language=vi&limit=1";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            if (!results.isEmpty()) {
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                geo.setLat(geometry.getDouble("lat"));
                geo.setLng(geometry.getDouble("lng"));
                geo.setAddress(results.getJSONObject(0).getString("formatted"));
            } else {
                geo.setAddress("Không tìm thấy địa điểm: " + address);
            }
        } catch (Exception e) {
            geo.setAddress("Lỗi xử lý địa điểm: " + address);
        }
        return geo;
    }

    public String toGetAddressShortest(Long userId) {
        List<HospitalEntity> hospitalEntities = hospitalRepository.findAll();
        ArrayList<DistanceShortestResponse> distanceShortestResponses = new ArrayList<>();
        PatientEntity patientEntity = patientRepository.findByUserEntity_IdAndUserEntity_UserRoleAndFamilyRelationship(userId, UserRole.PATIENT, FamilyRelationship.SELF);
        for (HospitalEntity hospitalEntity : hospitalEntities) {
            DistanceShortestResponse distanceShortestResponse = new DistanceShortestResponse();
            distanceShortestResponse.setDistance(toGetDistanceAddressUserToHospital(patientEntity.getAddress(), hospitalEntity.getAddress()));
            distanceShortestResponse.setAddress(hospitalEntity.getAddress());
            distanceShortestResponses.add(distanceShortestResponse);
        }
        distanceShortestResponses.sort((o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
        return toGetPromptDistanceShortest(distanceShortestResponses);
    }

    private double toGetDistanceAddressUserToHospital(String userAdress, String hospitalAddress) {
        GeolocationDAI userGeo = getGeoLocation(userAdress);
        GeolocationDAI hospitalGeo = getGeoLocation(hospitalAddress);
        return distanceCalculator.calculateDistance(userGeo.getLat(), userGeo.getLng(), hospitalGeo.getLat(), hospitalGeo.getLng());
    }

    private String toGetPromptDistanceShortest(List<DistanceShortestResponse> distanceShortestResponses) {
        StringBuilder result = new StringBuilder();
        for (DistanceShortestResponse distanceShortestResponse : distanceShortestResponses) {
            result.append(distanceShortestResponse.getAddress()).append(" (").append(distanceShortestResponse.getDistance()).append(" km)\n");
        }
        return result.toString();
    }
}
