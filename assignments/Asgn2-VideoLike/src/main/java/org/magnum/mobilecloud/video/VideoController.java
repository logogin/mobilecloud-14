package org.magnum.mobilecloud.video;

import static org.magnum.mobilecloud.video.client.VideoSvcApi.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
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

import com.google.common.collect.Lists;

/**
 * VideoController.java
 *
 * @author Pavel Danchenko
 * @date Aug 22, 2014
 *
 */
@Controller
public class VideoController {

    private @Autowired VideoRepository repository;

    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> getVideoList() {
        return Lists.newArrayList(repository.findAll());
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Video> getVideoById(@PathVariable("id") long id) {
        Video entity = repository.findOne(id);
        if ( null == entity ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
    @ResponseBody
    public Video addVideo(@RequestBody Video v) {
        v.setLikes(0);
        Video entity = repository.save(v);
        return entity;
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
    public void likeVideo(@PathVariable("id") long id, Principal user, HttpServletResponse response) {
        Video entity = repository.findOne(id);
        if ( null == entity ) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        if ( entity.getUsers().contains(user.getName()) ) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        entity.setLikes(entity.getLikes() + 1);
        entity.getUsers().add(user.getName());
        entity = repository.save(entity);
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
    public void unlikeVideo(@PathVariable("id") long id, Principal user, HttpServletResponse response) {
        Video entity = repository.findOne(id);
        if ( null == entity ) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        if ( !entity.getUsers().contains(user.getName()) ) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        entity.setLikes(entity.getLikes() - 1);
        entity.getUsers().remove(user.getName());
        entity = repository.save(entity);
    }

    @RequestMapping(value = VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> findByTitle(@RequestParam(TITLE_PARAMETER) String title) {
        return Lists.newArrayList(repository.findByName(title));
    }

    @RequestMapping(value = VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> findByDurationLessThan(@RequestParam(DURATION_PARAMETER) long duration) {
        return Lists.newArrayList(repository.findByDurationLessThan(duration));
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Set<String>> getUsersWhoLikedVideo(@PathVariable("id") long id) {
        Video entity = repository.findOne(id);
        if ( null == entity ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entity.getUsers(), HttpStatus.OK);
    }
}
