package org.magnum.dataup;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * VideoController.java
 *
 * @author logogin
 * @date Jul 31, 2014
 *
 */
@Controller
public class VideoController {

    private @Autowired InMemoryVideoManager videoManager;

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> getVideoList() {
        return videoManager.getAll();
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
    public void getData(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Video video = videoManager.get(id);
        if ( null == video ) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        if ( !videoManager.hasData(video) ) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        videoManager.writeVideoData(video, response.getOutputStream());
    }

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    @ResponseBody
    public Video addVideo(@RequestBody Video entity) {
        return videoManager.save(entity);
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
    public ResponseEntity<VideoStatus> setVideoData(@PathVariable("id") Long id, @RequestParam("data") MultipartFile data) throws Exception {
        Video video = videoManager.get(id);
        if ( null == video ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        videoManager.saveVideoData(video, data.getInputStream());
        return new ResponseEntity<>(new VideoStatus(VideoState.READY), HttpStatus.OK);
    }
}
