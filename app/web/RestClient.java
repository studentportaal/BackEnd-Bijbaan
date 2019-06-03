package web;

import com.fasterxml.jackson.databind.JsonNode;
import models.dto.ReviewDto;
import play.libs.Json;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.ahc.AhcCurlRequestLogger;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestClient implements WSBodyReadables, WSBodyWritables {
    private final Logger LOGGER = Logger.getLogger(RestClient.class.getName());
    private final WSClient ws;
    private final String reviewUrl = "https://europe-west1-pts6-bijbaan.cloudfunctions.net/addReview";

    @Inject
    public RestClient(WSClient ws) {
        this.ws = ws;
    }

    public void createReviews(String company, String student) {
        LOGGER.info("Creating reviews");

        // Review to be written by the student
        ReviewDto studentReview = new ReviewDto(student, company);

        // Review to be written by the company
        ReviewDto companyReview = new ReviewDto(company, student);

        try {
            ws.url(reviewUrl)
                    .setRequestFilter(new AhcCurlRequestLogger())
                    .post(Json.toJson(studentReview))
                    .toCompletableFuture().get();
            ws.url(reviewUrl)
                    .setRequestFilter(new AhcCurlRequestLogger())
                    .post(Json.toJson(companyReview))
                    .toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

    }
}
