package SpringBoot.controller;

import com.google.gson.Gson;
import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.data.MfAlexMetricSpace;
import eu.similarity.msc.user_examples.GroundTruthTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QuickStartController {

    @Autowired
    SpringBoot.service.InsertFeedbackService insertFeedbackService;

    @RequestMapping("/getImages/{imageId}")
    @ResponseBody
    public String getGroundTruthByImage(@PathVariable("imageId") String imageId) throws IOException {
        GroundTruthTest gt = new GroundTruthTest();

        List<DataListView.IdDistancePair> idDistancePairs = gt.getGroundTruth(Integer.parseInt(imageId));

        List<DataListView.SimilarImage> similarImages = new ArrayList<>();
        for (DataListView.IdDistancePair idp : idDistancePairs) {
            similarImages.add(new DataListView.SimilarImage(idp));
        }
        return new Gson().toJson(similarImages.subList(0,20));
    }

    @RequestMapping("/getPoweredImages/{power}/{imageId}")
    @ResponseBody
    public String getPoweredGroundTruthByImage(@PathVariable("power") String power,
                                               @PathVariable("imageId") String imageId) throws IOException {
        GroundTruthTest gt = new GroundTruthTest();
        List<DataListView.IdDistancePair> idDistancePairs = new ArrayList<>();
        List<DataListView.SimilarImage> similarImages = new ArrayList<>();
        double pow = Double.valueOf(power);
        if (pow == 1.0) {
            idDistancePairs = gt.getGroundTruth(Integer.parseInt(imageId));
        } else {
            idDistancePairs = gt.getGroundTruthWithTransform(pow, Integer.parseInt(imageId));
        }

        for (DataListView.IdDistancePair idp : idDistancePairs) {
            similarImages.add(new DataListView.SimilarImage(idp));
        }
        if (similarImages.size() < 9) {
            return new Gson().toJson(similarImages);
        }
        return new Gson().toJson(similarImages.subList(0, 9));

    }

    @RequestMapping("/generateRandomImageIds")
    @ResponseBody
    public String generateRandomImagesInfo() {
        int max = 999999, min = 0;
        List<DataListView.IdImageURLPair> idImageURLPairs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int randomId = (int) (Math.random() * (max - min) + min);
            idImageURLPairs.add(new DataListView.IdImageURLPair(randomId));
        }

        return new Gson().toJson(idImageURLPairs);
    }

    @RequestMapping("/generateRandomFeedbackId")
    @ResponseBody
    public String generateRandomFeedbackId() {
        MfAlexMetricSpace mf = new MfAlexMetricSpace("Volumes/Data/mf_fc6_full_euc/", "Volumes/Data/mf_fc6_full_euc/mf_fc6_raw/");
        final List<DataListView.IdDatumPair> queries = DataListView.convert(mf.getQueries());
        int max = 999, min = 0;
        int randomIndex = (int) (Math.random() * (max - min) + min);
        int randomId = queries.get(randomIndex).id;
        return new Gson().toJson(randomId);
    }

    @PostMapping("/postFeedback")
    @ResponseBody
    public String postFeedback(@RequestParam("imageId") Integer imageId,
                               @RequestParam("result") String result) {

        System.out.println(imageId + " " + result);
        if (insertFeedbackService.insertFeedback(imageId, result)) {
            return "success";
        }
        return "error";
    }

    /*@PostMapping("/postFeedback/{imageId}/{result}")
    @ResponseBody
    public void postFeedback(@PathVariable("iamgeId") Integer imageId,
                              @PathVariable("result") String result) {
        System.out.println(imageId + " " + result);
    }*/
}