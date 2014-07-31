package org.magnum.dataup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.magnum.dataup.model.Video;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * InMemoryVideoManager.java
 *
 * @author logogin
 * @date Jul 31, 2014
 *
 */
@Service
public class InMemoryVideoManager {

    private final AtomicLong currentId = new AtomicLong(1L);
    private final Map<Long,Video> videos = new HashMap<>();

    private final VideoFileManager dataManager;

    public InMemoryVideoManager() throws IOException {
        dataManager = VideoFileManager.get();
    }

    public Video get(Long id) {
        return videos.get(id);
    }

    public boolean hasData(Video entity) {
        return dataManager.hasVideoData(entity);
    }

    public void writeVideoData(Video entity, OutputStream out) throws IOException {
        dataManager.copyVideoData(entity, out);
    }

    public Video save(Video entity) {
        checkAndSetId(entity);
        checkAndSetDataUrl(entity);
        videos.put(entity.getId(), entity);
        return entity;
    }

    public void saveVideoData(Video entity, InputStream data) throws IOException {
        dataManager.saveVideoData(entity, data);
    }

    public Collection<Video> getAll() {
        return Collections.unmodifiableCollection(videos.values());
    }

    private void checkAndSetId(Video entity) {
        if(entity.getId() == 0){
            entity.setId(currentId.getAndIncrement());
        }
    }

    private void checkAndSetDataUrl(Video entity) {
        if(entity.getDataUrl() == null){
            entity.setDataUrl(getDataUrl(entity.getId()));
        }
    }

    private String getDataUrl(long videoId){
        return getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
    }

    private String getUrlBaseForLocalServer() {
       HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
       String base = "http://"+request.getServerName() + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
       return base;
    }
}
